package de.pantle.qwixx.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import de.pantle.qwixx.utils.Button;
import de.pantle.qwixx.utils.Constants;
import de.pantle.qwixx.utils.ScreenManager;

import static de.pantle.qwixx.utils.Constants.DICE_COLORS;

public class Overlay extends Actor {
	private static Overlay instance;
	
	private static Table table;
	private static Button changeScreenButton;
	private static Button rollDicesButton;
	
	private static Array<Image> outputDiceImages;
	private static Array<Integer> outputDiceValues;
	private static TextureAtlas diceAtlas;
	
	public Overlay() {
		table = new Table();
		table.background(new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal(Button.BUTTONS_PATH + Button.ButtonType.STANDARD.getUp() + Button.FILE_EXTENSION)))));
		setTableSize();
		
		// obere Anzeige der Würfel-Ergebnisse
		if(outputDiceImages == null) {
			outputDiceImages = new Array<Image>();
			outputDiceValues = new Array<Integer>();
			
			AssetManager assetManager = new AssetManager();
			assetManager.load(Constants.DICE_ATLAS_PATH, TextureAtlas.class);
			assetManager.finishLoading();
			
			diceAtlas = assetManager.get(Constants.DICE_ATLAS_PATH);
			
			for(int i = 0; i < Constants.DICE_COLORS.size; i++) {
				outputDiceValues.insert(i, 0);
				outputDiceImages.add(new Image(diceAtlas.findRegion(Constants.DICE_ATLAS_NAMES[i + 1] + "Questionmark")));
			}
		}
		
		// Button: neu würfeln
		rollDicesButton = new Button("neu würfeln", Button.ButtonType.STANDARD);
		rollDicesButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				instance.rollDicesButtonClicked();
			}
		});
		rollDicesButton.setSize((Gdx.graphics.getWidth() / 2) - Constants.BUTTONS_PADDING, (Gdx.graphics.getHeight() * Constants.EDGE_HEIGHT_PERCENT) - Constants.BUTTONS_PADDING);
		rollDicesButton.setPosition(Gdx.graphics.getWidth() - rollDicesButton.getWidth(), 0);
		
		// Button: Screen wechseln
		changeScreenButton = new Button("Würfel anzeigen", Button.ButtonType.STANDARD);
		changeScreenButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				instance.changeScreenButtonClicked();
			}
		});
		changeScreenButton.setSize((Gdx.graphics.getWidth() / 2) - Constants.BUTTONS_PADDING, (Gdx.graphics.getHeight() * Constants.EDGE_HEIGHT_PERCENT) - Constants.BUTTONS_PADDING);
		changeScreenButton.setPosition(0, 0);
		instance = this;
	}
	
	public static Overlay getInstance() {
		return instance;
	}
	
	public void show(Stage stage) {
		table.clear();
		setTableSize();
		
		// Würfel-Werte laden und ausgeben
		for (int i = 0; i < Constants.DICE_COLORS.size; i++) {
			//table.add(outputDiceImages.get(i)).size(table.getWidth() / 3 * 2 / (DICE_COLORS.size + 2));
			table.add(outputDiceImages.get(i)).size(table.getHeight());
		}
		
		// Tabelle und Buttons anzeigen
		stage.addActor(table);
		stage.addActor(rollDicesButton);
		stage.addActor(changeScreenButton);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}
	
	private void rollDicesButtonClicked() {
		if (ScreenManager.getScreen().getClass() == ScorecardScreen.class) {
			ScreenManager.changeScreen();
		}
		ScreenManager.getRollingDicesScreen().rollDices();
	}
	
	private void changeScreenButtonClicked() {
		ScreenManager.changeScreen();
	}
	
	
	public static void setButtonText(String text) {
		changeScreenButton.setText(text);
	}
	
	private static void setTableSize() {
		table.setSize(Gdx.graphics.getWidth(), (Gdx.graphics.getHeight() * Constants.EDGE_HEIGHT_PERCENT) - Constants.BUTTONS_PADDING);
		table.setPosition((Gdx.graphics.getWidth() - table.getWidth()) / 2, Gdx.graphics.getHeight() - table.getHeight());
	}
	
	public static void setValue(int i, int number) {
		outputDiceValues.set(i, number);
		//Gdx.app.log("test", "setValue");
		
		if (number == 0) {
			outputDiceImages.get(i).setDrawable(new TextureRegionDrawable(diceAtlas.findRegion(Constants.DICE_ATLAS_NAMES[i + 1] + "Questionmark")));
		}
		else {
			outputDiceImages.get(i).setDrawable(new TextureRegionDrawable(diceAtlas.findRegion(Constants.DICE_ATLAS_NAMES[i + 1] + String.valueOf(number))));
		}
	}
	
	public static int getValue(int i) {
		return outputDiceValues.get(i);
	}
	
	public static void resize() {
		//setTableSize();
	}
	
	public static void enableButtons() {
		rollDicesButton.setTouchable(Touchable.enabled);
		changeScreenButton.setTouchable(Touchable.enabled);
	}
	
	public static void disableButtons() {
		rollDicesButton.setTouchable(Touchable.disabled);
		changeScreenButton.setTouchable(Touchable.disabled);
	}
}
