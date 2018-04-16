package de.pantle.qwixx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.pantle.qwixx.utils.AbstractScreen;
import de.pantle.qwixx.utils.Button;
import de.pantle.qwixx.utils.Constants;
import de.pantle.qwixx.utils.DiceValues;
import de.pantle.qwixx.utils.Helper;
import de.pantle.qwixx.utils.ScreenManager;

/**
 * Created by Daniel on 05.04.2018.
 */

public class RollingDices extends AbstractScreen {
	
	private final static short GROUND_FLAG = 1 << 8;
	private final static short OBJECT_FLAG = 1 << 9;
	
	
	private final static int GROUND_WIDTH = 15;
	private final static int WALL_HEIGHT = 10;
	
	
	private Stage stage;
	private ModelBatch modelBatch;
	private Environment environment;
	private PerspectiveCamera camera;
	private CameraInputController cameraInputController;
	private AssetManager assetManager;
	private Array<Helper.GameObject> instances;
	private boolean loading;
	
	private InputMultiplexer inputMultiplexer;
	
	private btCollisionConfiguration collisionConfig;
	private btDispatcher dispatcher;
	private Helper.MyContactListener contactListener;
	private btBroadphaseInterface broadphase;
	private btDynamicsWorld dynamicsWorld;
	private btConstraintSolver constraintSolver;
	
	private Array<Helper.GameObject> dices;
	private DiceValues diceValues;
	private ArrayMap<String, Helper.GameObject.Constructor> constructors;
	
	
	public RollingDices() {
		Bullet.init();
		
		stage = new Stage();
		
		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(GROUND_WIDTH * 2 / 3, GROUND_WIDTH * 2 / 3, GROUND_WIDTH * 2 / 3, -1f, -0.8f, -0.2f));
		
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(3, 15, 4);
		camera.lookAt(0, 0, 0);
		camera.near = -1f;
		camera.far = 300f;
		camera.update();
		
		cameraInputController = new CameraInputController(camera);
		
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(stage);
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
		
		
		// Labels: Ausgabe der Zahlenwerte
		diceValues = new DiceValues();
		stage.addActor(diceValues);
		
		// Button: neu würfeln
		Button rollDicesButton = new Button("neu würfeln", Button.ButtonType.NORMAL);
		rollDicesButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(!loading) {
					for (int i = 0; i < dices.size; i++) {
						randomizeDicePosition(i);
					}
				}
			}
		});
		rollDicesButton.setSize((Gdx.graphics.getWidth() / 2) - Constants.BUTTONS_PADDING,(Gdx.graphics.getHeight() * Constants.EDGE_HEIGHT_PERCENT) - Constants.BUTTONS_PADDING);
		rollDicesButton.setPosition(Gdx.graphics.getWidth() - rollDicesButton.getWidth(), 0);
		stage.addActor(rollDicesButton);
		
		// Button: zum Spielplan
		Button showScorecardButton = new Button("Spielplan anzeigen", Button.ButtonType.NORMAL);
		showScorecardButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				DiceValues.saveValues();
				ScreenManager.showScorecard();
			}
		});
		showScorecardButton.setSize((Gdx.graphics.getWidth() / 2) - Constants.BUTTONS_PADDING,(Gdx.graphics.getHeight() * Constants.EDGE_HEIGHT_PERCENT) - Constants.BUTTONS_PADDING);
		showScorecardButton.setPosition(0, 0);
		stage.addActor(showScorecardButton);
	}
	
	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		diceValues.loadValues();
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
			constructors.put("dice", new Helper.GameObject.Constructor(diceModel, diceModel.nodes.get(0).id, new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)), 1));
			
			dices.add(constructors.get("dice").construct());
			for (Material m : dices.get(i).materials) {
				m.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
			}
			randomizeDicePosition(i);
			dices.get(i).body.setUserValue(instances.size);
			dices.get(i).body.setCollisionFlags(dices.get(i).body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			instances.add(dices.get(i));
			dynamicsWorld.addRigidBody(dices.get(i).body);
			dices.get(i).body.setContactCallbackFlag(OBJECT_FLAG);
			dices.get(i).body.setContactCallbackFilter(GROUND_FLAG);
		}
		
		
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
		//dices.get(i).transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
		dices.get(i).transform.set(new Vector3(MathUtils.random(-2.5f, 2.5f), 9f, MathUtils.random(-2.5f, 2.5f)), new Quaternion());
		dices.get(i).body.applyImpulse(new Vector3(MathUtils.random(-5f, 5f), MathUtils.random(-5f, 5f), MathUtils.random(-5f, 5f)), new Vector3(MathUtils.random(-0.5f, 0.5f), MathUtils.random(-1f, 1f), MathUtils.random(-0.5f, 0.5f)));
		
		dices.get(i).body.proceedToTransform(dices.get(i).transform);
	}
	
	@Override
	public void render(float delta) {
		if (loading && assetManager.update()) {
			doneLoading();
		}
		
		dynamicsWorld.stepSimulation(delta, 5, 1f / 60f);
		
		cameraInputController.update();
		
		if (!loading) {
			for (int i = 0; i < dices.size; i++) {
				Quaternion quaternion = dices.get(i).transform.getRotation(new Quaternion());
				
				int x = Math.round(quaternion.getRoll() / 10) * 10;
				int y = Math.round(quaternion.getPitch() / 10) * 10;
				int z = Math.round(quaternion.getYaw() / 10) * 10;
				
				int number = 0;
				
				if(x == -180 && y == 0) {
					number = 1;
				}
				else if(x == 180 && y == 0) {
					number = 1;
				}
				else if(y == -90 && z == 0) {
					number = 3;
				}
				else if(y == 90 && z == 0) {
					number = 4;
				}
				else if(y == -90) {
					number = 5;
				}
				else if (x == 0 && y == 0) {
					number = 6;
				}
				else if(y == 90) {
					number = 2;
				}
				
				diceValues.setValue(i, number);
			}
		}
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		modelBatch.begin(camera);
		modelBatch.render(instances, environment);
		modelBatch.end();
		
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
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
		stage.dispose();
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
