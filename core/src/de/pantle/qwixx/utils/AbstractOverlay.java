package de.pantle.qwixx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;

import static de.pantle.qwixx.utils.Constants.DICE_COLORS;

public abstract class AbstractOverlay extends Actor {
	protected static AbstractOverlay instance;
	
	private static Table table;
	private static Button changeScreenButton;
	private static Button rollDicesButton;
	
	private static Array<Label> outputDiceValues;
	
	public AbstractOverlay() {
		table = new Table();
		table.background(new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal(Button.BUTTONS_PATH + Button.ButtonType.STANDARD + Button.FILE_EXTENSION)))));
		setTableSize();
		
		if (outputDiceValues == null) {
			outputDiceValues = new Array<Label>();
			
			FileHandle fontFile = Gdx.files.internal("Comfortaa.ttf");
			FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(fontFile);
			FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
			parameter.size = 50;
			
			for (int i = 0; i < Constants.DICE_COLORS.size; i++) {
				parameter.color = DICE_COLORS.get(i);
				outputDiceValues.add(new Label("?", new Label.LabelStyle(fontGenerator.generateFont(parameter), DICE_COLORS.get(i))));
				table.add(outputDiceValues.get(i)).width(table.getWidth() / 3 * 2 / (DICE_COLORS.size + 2));
			}
			
			fontGenerator.dispose();
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
	}
	
	protected abstract void changeScreenButtonClicked();
	
	protected abstract void rollDicesButtonClicked();
	
	protected void show(Stage stage) {
		table.clear();
		setTableSize();
		
		// Würfel-Werte laden und ausgeben
		for (int i = 0; i < Constants.DICE_COLORS.size; i++) {
			table.add(outputDiceValues.get(i)).width(table.getWidth() / 3 * 2 / (DICE_COLORS.size + 2));
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
	
	
	public static void setButtonText(String text) {
		changeScreenButton.setText(text);
	}
	
	private static void setTableSize() {
		table.setSize(Gdx.graphics.getWidth(), (Gdx.graphics.getHeight() * Constants.EDGE_HEIGHT_PERCENT) - Constants.BUTTONS_PADDING);
		table.setPosition((Gdx.graphics.getWidth() - table.getWidth()) / 2, Gdx.graphics.getHeight() - table.getHeight());
	}
	
	public static void setValue(int i, int number) {
		if (number == 0) {
			outputDiceValues.get(i).setText("?");
		}
		else {
			outputDiceValues.get(i).setText(String.valueOf(number));
		}
	}
	
	public static void resize() {
		setTableSize();
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
