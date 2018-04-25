package de.pantle.qwixx.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import de.pantle.qwixx.StartScreen;
import de.pantle.qwixx.multiplayer.StartMultiplayerScreen;
import de.pantle.qwixx.singleplayer.RollingDicesSingleplayerScreen;
import de.pantle.qwixx.singleplayer.ScorecardSingleplayerScreen;

/**
 * Created by Daniel on 09.04.2018.
 */

public class ScreenManager {
	private static Game game;
	
	// Singleplayer-Screens
	private static ScorecardSingleplayerScreen scorecardSingleplayerScreen;
	private static RollingDicesSingleplayerScreen rollingDicesSingleplayerScreen;
	
	// Multiplayer-Screens
	private static StartMultiplayerScreen startMultiplayerScreen;
	
	
	public static void start(Game g) {
		game = g;
		
		// init screens
		StartScreen startScreen = new StartScreen();
		
		scorecardSingleplayerScreen = new ScorecardSingleplayerScreen();
		rollingDicesSingleplayerScreen = new RollingDicesSingleplayerScreen();
		
		startMultiplayerScreen = new StartMultiplayerScreen();
		
		game.setScreen(startScreen);
	}
	
	public static void changeScreen() {
		if (game.getScreen().getClass() == ScorecardSingleplayerScreen.class) {
			game.setScreen(rollingDicesSingleplayerScreen);
			Overlay.setButtonText("Spielplan anzeigen");
		}
		else {
			game.setScreen(scorecardSingleplayerScreen);
			Overlay.setButtonText("Würfel anzeigen");
		}
	}
	
	public static Screen getScreen() {
		return game.getScreen();
	}
	
	public static RollingDicesSingleplayerScreen getRollingDicesSingleplayerScreen() {
		return rollingDicesSingleplayerScreen;
	}
	
	public static void startSingleplayer() {
		game.setScreen(scorecardSingleplayerScreen);
	}
	
	public static void startMultiplayer() {
		game.setScreen(startMultiplayerScreen);
	}
}
