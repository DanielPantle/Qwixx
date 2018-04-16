package de.pantle.qwixx.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import de.pantle.qwixx.screens.RollingDices;
import de.pantle.qwixx.screens.Scorecard;

/**
 * Created by Daniel on 09.04.2018.
 */

public class ScreenManager {
	private static Game game;
	
	private static AbstractScreen scorecard;
	private static AbstractScreen rollingDices;
	
	public static void init(Game g) {
		game = g;
		scorecard = new Scorecard();
		rollingDices = new RollingDices();
	}
	
	public static void showScorecard() {
		game.setScreen(scorecard);
	}
	public static void showRollingDices() {
		game.setScreen(rollingDices);
	}
}
