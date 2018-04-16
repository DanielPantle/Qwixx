package de.pantle.qwixx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;

import static de.pantle.qwixx.utils.Constants.DICE_COLORS;

public class DiceValues extends Actor {
	private Table table;
	private static Array<Integer> values;
	private static Array<Label> outputDiceValues;
	
	private float label_width;
	
	public DiceValues() {
		table = new Table();
		table.background(new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("buttons/normal_down.png")))));
		setTableSize();
		
		label_width = table.getWidth() / 3 * 2 / (DICE_COLORS.size + 2);
		
		FileHandle fontFile = Gdx.files.internal("Comfortaa.ttf");
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(fontFile);
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 50;
		
		values = new Array<Integer>();
		
		outputDiceValues = new Array<Label>();
		for (int i = 0; i < Constants.DICE_COLORS.size; i++) {
			values.add(0);
			
			parameter.color = DICE_COLORS.get(i);
			outputDiceValues.add(new Label("?", new Label.LabelStyle(fontGenerator.generateFont(parameter), DICE_COLORS.get(i))));
			table.add(outputDiceValues.get(i)).width(label_width);
		}
		
		fontGenerator.dispose();
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		table.draw(batch, parentAlpha);
	}
	
	// speichert die Werte, um sie auf dem anderen Screen wieder anzuzeigen
	public static void saveValues() {
		for (int i = 0; i < outputDiceValues.size; i++) {
			String text = outputDiceValues.get(i).getText().toString();
			if(text.contains("?")) {
				values.set(i, 0);
			}
			else {
				values.set(i, Integer.valueOf(text));
			}
		}
	}
	
	// lädt die Werte aus und setzt die Labels
	public void loadValues() {
		table.clearChildren();
		for (int i = 0; i < outputDiceValues.size; i++) {
			if(values.get(i) == 0) {
				outputDiceValues.get(i).setText("?");
			}
			else {
				outputDiceValues.get(i).setText(String.valueOf(values.get(i)));
			}
			table.add(outputDiceValues.get(i)).width(label_width);
		}
	}
	
	private void setTableSize() {
		table.setSize(Gdx.graphics.getWidth(), (Gdx.graphics.getHeight() * Constants.EDGE_HEIGHT_PERCENT) - Constants.BUTTONS_PADDING);
		table.setPosition((Gdx.graphics.getWidth() - table.getWidth()) / 2, Gdx.graphics.getHeight() - table.getHeight());
	}
	
	public void setValue(int i, int number) {
		if(number == 0) {
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
