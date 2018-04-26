package de.pantle.qwixx.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import de.pantle.qwixx.StartScreen;
import de.pantle.qwixx.multiplayer.MultiplayerOverlay;
import de.pantle.qwixx.multiplayer.ScorecardMultiplayerScreen;
import de.pantle.qwixx.multiplayer.RollingDicesMultiplayerScreen;
import de.pantle.qwixx.multiplayer.StartMultiplayerScreen;
import de.pantle.qwixx.singleplayer.RollingDicesSingleplayerScreen;
import de.pantle.qwixx.singleplayer.ScorecardSingleplayerScreen;
import de.pantle.qwixx.singleplayer.SingleplayerOverlay;

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
	private static ScorecardMultiplayerScreen scorecardMultiplayerScreen;
	private static RollingDicesMultiplayerScreen rollingDicesMultiplayerScreen;
	
	
	public static void start(Game g) {
		game = g;
		
		// init screens
		StartScreen startScreen = new StartScreen();
		
		
		
		game.setScreen(startScreen);
	}
	
	public static void changeScreen() {
		if(scorecardSingleplayerScreen == null) {
			// Multiplayer-Spiel
			if (game.getScreen().getClass() == ScorecardMultiplayerScreen.class) {
				game.setScreen(rollingDicesMultiplayerScreen);
				MultiplayerOverlay.setButtonText("Spielplan anzeigen");
			}
			else {
				game.setScreen(scorecardMultiplayerScreen);
				MultiplayerOverlay.setButtonText("Würfel anzeigen");
			}
		}
		else {
			// Singleplayer-Spiel
			if (game.getScreen().getClass() == ScorecardSingleplayerScreen.class) {
				game.setScreen(rollingDicesSingleplayerScreen);
				SingleplayerOverlay.setButtonText("Spielplan anzeigen");
			}
			else {
				game.setScreen(scorecardSingleplayerScreen);
				SingleplayerOverlay.setButtonText("Würfel anzeigen");
			}
		}
	}
	
	public static Screen getScreen() {
		return game.getScreen();
	}
	
	public static void showScorecardMultiplayerScreen() {
		game.setScreen(scorecardMultiplayerScreen);
	}
	
	public static RollingDicesSingleplayerScreen getRollingDicesSingleplayerScreen() {
		return rollingDicesSingleplayerScreen;
	}
	public static RollingDicesMultiplayerScreen getRollingDicesMultiplayerScreen() {
		return rollingDicesMultiplayerScreen;
	}
	
	
	public static void startSingleplayer() {
		scorecardSingleplayerScreen = new ScorecardSingleplayerScreen();
		rollingDicesSingleplayerScreen = new RollingDicesSingleplayerScreen();
		game.setScreen(scorecardSingleplayerScreen);
	}
	
	public static void startMultiplayer() {
		startMultiplayerScreen = new StartMultiplayerScreen();
		scorecardMultiplayerScreen = new ScorecardMultiplayerScreen();
		rollingDicesMultiplayerScreen = new RollingDicesMultiplayerScreen();
		game.setScreen(startMultiplayerScreen);
	}
}
