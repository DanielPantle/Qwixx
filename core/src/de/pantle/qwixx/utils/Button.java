package de.pantle.qwixx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.HashMap;

/**
 * Created by Daniel on 07.04.2018.
 */

public class Button extends TextButton {
	
	public enum ButtonType {
		ROT("rot", "grau"),
		GELB("gelb", "grau"),
		GRUEN("gruen", "grau"),
		BLAU("blau", "grau"),
		MISS("miss", "grau"),
		RES("res", "grau"),
		CHECKED("grau"),
		STANDARD("standard", "standard_down", "standard"),
		OPERATOR("none");
		
		ButtonType(String up) {
			this.up = up;
			this.down = up;
			this.checked = up;
		}
		
		ButtonType(String up, String checked) {
			this.up = up;
			this.down = up;
			this.checked = checked;
		}
		
		ButtonType(String up, String down, String checked) {
			this.up = up;
			this.down = down;
			this.checked = checked;
		}
		
		private String up;
		private String down;
		private String checked;
		
		public String getUp() {
			return up;
		}
		
		public String getChecked() {
			return checked;
		}
		
		public String getDown() {
			return down;
		}
	}
	
	public static final String BUTTONS_PATH = "buttons/";
	public static final String FILE_EXTENSION = ".png";
	private static final String BUTTONS_ATLAS = "buttons.atlas";
	
	private static HashMap<ButtonType, TextButtonStyle> textButtonStyles;
	private static TextureAtlas textureAtlas;
	private static BitmapFont font;
	
	private Button(String text, TextButtonStyle style) {
		super(text, style);
	}
	
	public Button(String text, ButtonType buttonType) {
		this(text, textButtonStyles.get(buttonType));
	}
	
	public static void init() {
		init(Color.BLACK);
	}
	
	private static void init(Color color) {
		FileHandle fontFile = Gdx.files.internal("Comfortaa.ttf");
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 30;
		parameter.color = color;
		font = generator.generateFont(parameter);
		generator.dispose();
		
		textureAtlas = new TextureAtlas(Gdx.files.internal(BUTTONS_PATH + BUTTONS_ATLAS));
		
		textButtonStyles = new HashMap<ButtonType, TextButtonStyle>();
		for (ButtonType buttonType : ButtonType.values()) {
			textButtonStyles.put(buttonType, new TextButtonStyle(new TextureRegionDrawable(new TextureRegion(textureAtlas.findRegion(buttonType.getUp()))), new TextureRegionDrawable(new TextureRegion(textureAtlas.findRegion(buttonType.getDown()))), new TextureRegionDrawable(new TextureRegion(textureAtlas.findRegion(buttonType.getChecked()))), font));
		}
	}
	
	public static void dispose() {
		textureAtlas.dispose();
		font.dispose();
	}
}
