package de.pantle.qwixx.multiplayer;

import com.badlogic.gdx.scenes.scene2d.Stage;

import de.pantle.qwixx.utils.AbstractOverlay;
import de.pantle.qwixx.utils.ScreenManager;

public class MultiplayerOverlay extends AbstractOverlay {
	public MultiplayerOverlay(Stage stage) {
		super(stage);
	}
	
	public void rollDicesButtonCalled() {
		if (ScreenManager.getScreen().getClass() == RollingDicesMultiplayerScreen.class) {
			ScreenManager.getRollingDicesMultiplayerScreen().rollDices();
		}
		else {
			ScreenManager.changeScreen();
			ScreenManager.getRollingDicesMultiplayerScreen().rollDices();
		}
	}
	
}
