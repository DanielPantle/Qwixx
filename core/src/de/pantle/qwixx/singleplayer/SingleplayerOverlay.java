package de.pantle.qwixx.singleplayer;

import com.badlogic.gdx.scenes.scene2d.Stage;

import de.pantle.qwixx.utils.AbstractOverlay;
import de.pantle.qwixx.utils.ScreenManager;

public class SingleplayerOverlay extends AbstractOverlay {
	public SingleplayerOverlay(Stage stage) {
		super(stage);
	}
	
	@Override
	public void rollDicesButtonCalled() {
		if (ScreenManager.getScreen().getClass() == RollingDicesSingleplayerScreen.class) {
			ScreenManager.getRollingDicesSingleplayerScreen().rollDices();
		}
		else {
			ScreenManager.changeScreen();
			ScreenManager.getRollingDicesSingleplayerScreen().rollDices();
		}
	}
}
