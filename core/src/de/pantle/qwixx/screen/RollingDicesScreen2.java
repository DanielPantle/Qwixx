package de.pantle.qwixx.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.pantle.qwixx.utils.Constants;
import de.pantle.qwixx.utils.Helper;

/**
 * Created by Daniel on 05.04.2018.
 */

public class RollingDicesScreen2 extends AbstractScreen implements InputProcessor {
	
	private final static short GROUND_FLAG = 1 << 8;
	private final static short OBJECT_FLAG = 1 << 9;
	
	private final static int GROUND_WIDTH = 15;
	private final static int WALL_HEIGHT = 10;
	
	private ModelBatch modelBatch;
	private Environment environment;
	private PerspectiveCamera camera;
	private CameraInputController cameraInputController;
	private AssetManager assetManager;
	private Array<Helper.GameObject> instances;
	private boolean loading;
	
	private btCollisionConfiguration collisionConfig;
	private btDispatcher dispatcher;
	private Helper.MyContactListener contactListener;
	private btBroadphaseInterface broadphase;
	private btDynamicsWorld dynamicsWorld;
	private btConstraintSolver constraintSolver;
	
	private Array<Helper.GameObject> dices;
	private ArrayMap<String, Helper.GameObject.Constructor> constructors;
	
	// Drag and drop
	private int selected = -1;
	private Material selectionMaterial;
	private Material originalMaterial;
	private Vector3 position = new Vector3();
	
	// Start-Positionen von 1 - 6, werden zufällig ausgewählt
	private static final int[][] STARTING_POSITIONS = {
			{1, 0, 0, 180},
			{1, 0, 0, 90},
			{0, 0, 1, 270},
			{0, 0, 1, 90},
			{1, 0, 0, 270},
			{1, 0, 0, 0},
	};
	
	// Status der Würfel - ob sie noch fallen oder schon liegen
	private enum DiceState {
		ROLLING,
		WAITING,
		LYING
	}
	
	private DiceState diceState;
	
	public RollingDicesScreen2() {
		super();
		
		Bullet.init();
		
		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(GROUND_WIDTH * 2 / 3, GROUND_WIDTH * 2 / 3, GROUND_WIDTH * 2 / 3, -1f, -0.8f, -0.2f));
		
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(3, 10, 4);
		camera.lookAt(0, 3, 0);
		camera.near = -1f;
		camera.far = 300f;
		camera.update();
		
		cameraInputController = new CameraInputController(camera);
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(this);
		inputMultiplexer.addProcessor(cameraInputController);
		
		instances = new Array<Helper.GameObject>();
		
		assetManager = new AssetManager();
		for (String path : Constants.DICE_FILE_PATHS) {
			assetManager.load(Constants.DICE_PATH + path, Model.class);
		}
		loading = true;
		
		
		// Collision detection
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		constraintSolver = new btSequentialImpulseConstraintSolver();
		dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
		dynamicsWorld.setGravity(new Vector3(0, -10f, 0));
		contactListener = new Helper.MyContactListener();
		
		// Drag and drop
		selectionMaterial = new Material();
		selectionMaterial.set(ColorAttribute.createDiffuse(Color.ORANGE));
		originalMaterial = new Material();
	}
	
	@Override
	public void show() {
		super.show();
		Overlay.getInstance().show(stage);
	}
	
	private void doneLoading() {
		ModelBuilder mb = new ModelBuilder();
		
		Material material = new Material(ColorAttribute.createDiffuse(0.6f, 0.6f, 0.6f, 1));
		
		Model groundModel = mb.createBox(GROUND_WIDTH, 0.1f, GROUND_WIDTH, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		Model wallModel = mb.createBox(0.1f, WALL_HEIGHT, GROUND_WIDTH, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		
		constructors = new ArrayMap<String, Helper.GameObject.Constructor>(String.class, Helper.GameObject.Constructor.class);
		constructors.put("ground", new Helper.GameObject.Constructor(groundModel, "node1", new btBoxShape(new Vector3(GROUND_WIDTH / 2, 0.05f, GROUND_WIDTH / 2)), 0f));
		
		constructors.put("wall", new Helper.GameObject.Constructor(wallModel, "node1", new btBoxShape(new Vector3(0.05f, WALL_HEIGHT / 2, GROUND_WIDTH / 2)), 0f));
		
		dices = new Array<Helper.GameObject>();
		
		for (int i = 0; i < Constants.DICE_FILE_PATHS.length; i++) {
			Model diceModel = assetManager.get(Constants.DICE_PATH + Constants.DICE_FILE_PATHS[i], Model.class);
			constructors.put("dice", new Helper.GameObject.Constructor(diceModel, diceModel.nodes.get(0).id, new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)), 1.5f));
			
			dices.add(constructors.get("dice").construct());
			for (Material m : dices.get(i).materials) {
				m.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
			}
			randomizeDicePosition(i);
			dices.get(i).body.setUserValue(instances.size);
			dices.get(i).body.setCollisionFlags(dices.get(i).body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			dynamicsWorld.addRigidBody(dices.get(i).body);
			dices.get(i).body.setContactCallbackFlag(OBJECT_FLAG);
			dices.get(i).body.setContactCallbackFilter(GROUND_FLAG);
		}
		
		instances.addAll(dices);
		
		
		Helper.GameObject ground = constructors.get("ground").construct();
		ground.body.setCollisionFlags(ground.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
		instances.add(ground);
		dynamicsWorld.addRigidBody(ground.body);
		ground.body.setContactCallbackFlag(GROUND_FLAG);
		ground.body.setContactCallbackFilter(0);
		ground.body.setActivationState(Collision.DISABLE_DEACTIVATION);
		
		Array<Helper.GameObject> walls = new Array<Helper.GameObject>();
		
		Helper.GameObject wall = constructors.get("wall").construct();
		wall.transform.translate(GROUND_WIDTH / 2, WALL_HEIGHT / 2, 0);
		walls.add(wall);
		
		wall = constructors.get("wall").construct();
		wall.transform.translate(-GROUND_WIDTH / 2, WALL_HEIGHT / 2, 0);
		walls.add(wall);
		
		wall = constructors.get("wall").construct();
		wall.transform.translate(0, WALL_HEIGHT / 2, GROUND_WIDTH / 2);
		wall.transform.rotate(new Vector3(0, 1, 0), 90);
		walls.add(wall);
		
		wall = constructors.get("wall").construct();
		wall.transform.translate(0, WALL_HEIGHT / 2, -GROUND_WIDTH / 2);
		wall.transform.rotate(new Vector3(0, 1, 0), 90);
		walls.add(wall);
		
		
		for (int i = 0; i < walls.size; i++) {
			walls.get(i).body.setCollisionFlags(walls.get(i).body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
			dynamicsWorld.addRigidBody(walls.get(i).body);
			walls.get(i).body.setContactCallbackFlag(GROUND_FLAG);
			walls.get(i).body.setContactCallbackFilter(0);
			walls.get(i).body.setActivationState(Collision.DISABLE_DEACTIVATION);
		}
		
		instances.addAll(walls);
		
		loading = false;
	}
	
	// Setzt den Würfel an eine zufällige Position
	private void randomizeDicePosition(int i) {
		dices.get(i).transform.set(new Vector3(MathUtils.random(4), 5f, MathUtils.random(4)), new Quaternion());
		int x = MathUtils.random(5);
		dices.get(i).transform.rotate(STARTING_POSITIONS[x][0], STARTING_POSITIONS[x][1], STARTING_POSITIONS[x][2], STARTING_POSITIONS[x][3]);
		dices.get(i).body.applyImpulse(new Vector3(-MathUtils.random(6), -MathUtils.random(5), -MathUtils.random(6)), new Vector3(MathUtils.random(), MathUtils.random(), MathUtils.random()));
		//ohne z: dices.get(i).body.applyImpulse(new Vector3(-MathUtils.random(6), -MathUtils.random(5), 0), new Vector3(MathUtils.random(), MathUtils.random(), 0));
		
		dices.get(i).body.proceedToTransform(dices.get(i).transform);
	}
	
	public void rollDices() {
		if (!loading) {
			for (int i = 0; i < dices.size; i++) {
				randomizeDicePosition(i);
			}
		}
		
		Overlay.disableButtons();
		diceState = DiceState.ROLLING;
	}
	
	@Override
	public void render(float delta) {
		if (loading && assetManager.update()) {
			doneLoading();
		}
		
		//dynamicsWorld.stepSimulation(delta, 5, 1f / 60f);
		dynamicsWorld.stepSimulation(delta * 3);
		
		cameraInputController.update();
		
		if (!loading) {
			DiceState currentState = DiceState.LYING;
			
			for (int i = 0; i < dices.size; i++) {
				Quaternion quaternion = dices.get(i).transform.getRotation(new Quaternion());
				
				int x = Math.round(quaternion.getRoll() / 10) * 10;
				int y = Math.round(quaternion.getPitch() / 10) * 10;
				int z = Math.round(quaternion.getYaw() / 10) * 10;
				
				if(x == -180) x = 180;
				if(y == -180) y = 180;
				if(z == -180) z = 180;
				if(x == -90) x = 270;
				if(y == -90) y = 270;
				if(z == -90) z = 270;
				
				int number = 0;
				
				if((x == 180 && y == 0)) {
					number = 1;
				}
				else if((x == 0 && y == 90) || (y == 90 && z == 90)) {
					number = 2;
				}
				else if((y == 270 && z == 0)) {
					number = 3;
				}
				else if((x == 270 && y == 0) || (y == 90 && z == 0)) {
					number = 4;
				}
				else if((x == 0 && y == 0) || (y == 270 && z == 90)) {
					number = 6;
				}
				
				else if(y == 270) {
					number = 5;
				}
				else if(y == 90) {
					number = 2;
				}
				else {
					currentState = DiceState.ROLLING;
				}
				
				
				/*
				if (x == 180 && y == 0) {
					number = 1;
				}
				else if(x == 90 && y == 0) {
					number = 2;
				}
				else if(x == 270 && y == 0) {
					number = 4;
				}
				else if (y == -270 && z == 0) {
					number = 3;
				}
				else if (y == 90 && z == 0) {
					number = 4;
					if(i==5)
					Gdx.app.log("4", x + ", " + y + ", " + z);
				}
				else if (y == -270) {
					number = 5;
					if(i==5)
					Gdx.app.log("5", x + ", " + y + ", " + z);
				}
				else if (x == 0 && y == 0) {
					number = 6;
					if(i==5)
					Gdx.app.log("6", x + ", " + y + ", " + z);
				}
				else if (y == 90) {
					number = 2;
				}
				else if(x == 0 && y == 0) {
					number = 6;
				}*/
				
				Gdx.app.log(number + " " + Constants.DICE_ATLAS_NAMES[i + 1], "x: " + x + ", y: " + y + ", z: " + z);
				
				Overlay.setValue(i, number);
			}
			
			// Wenn alle Würfel liegen
			if (currentState == DiceState.ROLLING) {
				diceState = DiceState.ROLLING;
			}
			else if (diceState == DiceState.ROLLING) {
				diceState = DiceState.WAITING;
			}
			else if (diceState == DiceState.WAITING) {
				diceState = DiceState.LYING;
				Gdx.app.log("TEST", "ALLE WÜRFEL LIEGEN!");
				
				Overlay.enableButtons();
			}
		}
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		modelBatch.begin(camera);
		modelBatch.render(instances, environment);
		modelBatch.end();
		
		super.render(delta);
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Gdx.app.log("TEST", "touchDown");
		selected = getObject(screenX, screenY);
		return selected >= 0;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (selected < 0) {
			return false;
		}
//		else if(selected == selecting) {
		Ray ray = camera.getPickRay(screenX, screenY);
		final float distance = -ray.origin.y / ray.direction.y;
		position.set(ray.direction).scl(distance).add(ray.origin);
		
		position.y = dices.get(selected).transform.getTranslation(new Vector3()).y;
		
		dices.get(selected).transform.setTranslation(position);
		dices.get(selected).body.proceedToTransform(dices.get(selected).transform);
//		}
		return true;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Gdx.app.log("TEST", "touchUp");
		if (selected >= 0) {
			if (selected == getObject(screenX, screenY)) {
				setSelected(selected);
			}
			selected = -1;
			return true;
		}
		return false;
	}
	
	private int getObject(int screenX, int screenY) {
		Gdx.app.log("TEST", "getObject");
		Ray ray = camera.getPickRay(screenX, screenY);
		int result = -1;
		float distance = -1;
		
		for (int i = 0; i < dices.size; ++i) {
			final Helper.GameObject dice = dices.get(i);
			dice.transform.getTranslation(position);
			position.add(dice.center);
			final float len = ray.direction.dot(position.x - ray.origin.x, position.y - ray.origin.y, position.z - ray.origin.z);
			if (len < 0f)
				continue;
			float dist2 = position.dst2(ray.origin.x + ray.direction.x * len, ray.origin.y + ray.direction.y * len, ray.origin.z + ray.direction.z * len);
			if (distance >= 0f && dist2 > distance)
				continue;
			if (dist2 <= dice.radius * dice.radius) {
				result = i;
				distance = dist2;
			}
		}
		
		return result;
	}
	
	private void setSelected(int value) {
		Gdx.app.log("TEST", "setSelected");
		if (selected == value) {
			return;
		}
		if (selected >= 0) {
			Material material = dices.get(selected).materials.get(0);
			material.clear();
			material.set(originalMaterial);
		}
		
		selected = value;
		if (selected >= 0) {
			Material material = dices.get(selected).materials.get(0);
			originalMaterial.clear();
			originalMaterial.set(material);
			material.clear();
			material.set(selectionMaterial);
		}
	}
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		return false;
	}
	
	@Override
	public boolean keyTyped(char character) {
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}
	
	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	@Override
	public void pause() {
	
	}
	
	@Override
	public void resume() {
	
	}
	
	@Override
	public void hide() {
	
	}
	
	@Override
	public void dispose() {
		modelBatch.dispose();
		instances.clear();
		assetManager.clear();
		
		for (Helper.GameObject.Constructor constructor : constructors.values()) {
			constructor.dispose();
		}
		
		for (Helper.GameObject dice : dices) {
			dice.dispose();
		}
		
		dynamicsWorld.dispose();
		constraintSolver.dispose();
		broadphase.dispose();
		dispatcher.dispose();
		collisionConfig.dispose();
		contactListener.dispose();
	}
}
