package de.pantle.qwixx.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class Constants {
	public final static String DICE_ATLAS_PATH = "dice_images_s.atlas";
	public final static String[] DICE_ATLAS_NAMES = {"questionmark", "white", "white", "red", "yellow", "green", "blue"};
	
	// Würfel
	public final static String DICE_PATH = "dice/";
	public final static String[] DICE_FILE_PATHS = {"dice_white.g3db", "dice_white.g3db", "dice_red.g3db", "dice_yellow.g3db", "dice_green.g3db", "dice_blue.g3db"};
	public final static Array<Color> DICE_COLORS = new Array<Color>(new Color[]{Color.WHITE, Color.WHITE, Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE});
	
	// Anzeige der Buttons
	public static final int ROW_COUNT_TOTAL = 6;
	public static final int COLORS_COUNT = 4;
	public static final int BUTTONS_PER_COLOR_COUNT = 12;
	
	// Design
	public static final float EDGE_HEIGHT_PERCENT = 0.15f;
	public static final int BUTTONS_PADDING = 5;
}
