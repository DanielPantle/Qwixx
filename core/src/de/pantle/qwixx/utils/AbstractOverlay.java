package de.pantle.qwixx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;

import de.pantle.qwixx.singleplayer.RollingDicesSingleplayerScreen;

import static de.pantle.qwixx.utils.Constants.DICE_COLORS;

public abstract class AbstractOverlay extends Actor {
	private Table table;
	private static Button changeScreenButton;
	
	private static Array<Label> outputDiceValues;
	
	
	public AbstractOverlay(Stage stage) {
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
		Button rollDicesButton = new Button("neu würfeln", Button.ButtonType.STANDARD);
		rollDicesButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				rollDicesButtonCalled();
			}
		});
		rollDicesButton.setSize((Gdx.graphics.getWidth() / 2) - Constants.BUTTONS_PADDING, (Gdx.graphics.getHeight() * Constants.EDGE_HEIGHT_PERCENT) - Constants.BUTTONS_PADDING);
		rollDicesButton.setPosition(Gdx.graphics.getWidth() - rollDicesButton.getWidth(), 0);
		
		// Button: zum Spielplan
		changeScreenButton = new Button("Würfel anzeigen", Button.ButtonType.STANDARD);
		changeScreenButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ScreenManager.changeScreen();
			}
		});
		changeScreenButton.setSize((Gdx.graphics.getWidth() / 2) - Constants.BUTTONS_PADDING, (Gdx.graphics.getHeight() * Constants.EDGE_HEIGHT_PERCENT) - Constants.BUTTONS_PADDING);
		changeScreenButton.setPosition(0, 0);
		
		stage.addActor(rollDicesButton);
		stage.addActor(changeScreenButton);
	}
	
	public abstract void rollDicesButtonCalled();
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}
	
	
	// lädt die Werte aus und setzt die Labels
	public void show(Stage stage) {
		setTableSize();
		table.clear();
		for (int i = 0; i < Constants.DICE_COLORS.size; i++) {
			table.add(outputDiceValues.get(i)).width(table.getWidth() / 3 * 2 / (DICE_COLORS.size + 2));
		}
		stage.addActor(table);
	}
	
	public static void setButtonText(String text) {
		changeScreenButton.setText(text);
	}
	
	private void setTableSize() {
		table.setSize(Gdx.graphics.getWidth(), (Gdx.graphics.getHeight() * Constants.EDGE_HEIGHT_PERCENT) - Constants.BUTTONS_PADDING);
		table.setPosition((Gdx.graphics.getWidth() - table.getWidth()) / 2, Gdx.graphics.getHeight() - table.getHeight());
	}
	
	public void setValue(int i, int number) {
		if (number == 0) {
			outputDiceValues.get(i).setText("?");
		}
		else {
			outputDiceValues.get(i).setText(String.valueOf(number));
		}
	}
	
	public void resize() {
		setTableSize();
	}
}
