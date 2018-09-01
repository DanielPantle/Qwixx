package de.pantle.qwixx.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import de.pantle.qwixx.screen.Overlay;
import de.pantle.qwixx.screen.RollingDicesScreen;
import de.pantle.qwixx.screen.ScorecardScreen;

/**
 * Created by Daniel on 09.04.2018.
 */

public class ScreenManager {
	private static Game game;
	
	private static ScorecardScreen scorecardScreen;
	private static RollingDicesScreen rollingDicesScreen;
	
	public static void start(Game g) {
		game = g;
		
		scorecardScreen = new ScorecardScreen();
		rollingDicesScreen = new RollingDicesScreen();
		game.setScreen(scorecardScreen);
	}
	
	public static void changeScreen() {
		if (game.getScreen().getClass() == ScorecardScreen.class) {
			game.setScreen(rollingDicesScreen);
			Overlay.setButtonText("Spielplan anzeigen");
		}
		else {
			game.setScreen(scorecardScreen);
			Overlay.setButtonText("Würfel anzeigen");
		}
	}
	
	public static Screen getScreen() {
		return game.getScreen();
	}
	
	public static RollingDicesScreen getRollingDicesScreen() {
		return rollingDicesScreen;
	}
}
