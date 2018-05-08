package de.pantle.qwixx.singleplayer;

import com.badlogic.gdx.scenes.scene2d.Stage;

import de.pantle.qwixx.multiplayer.MultiplayerOverlay;
import de.pantle.qwixx.utils.AbstractOverlay;
import de.pantle.qwixx.utils.ScreenManager;

public class SingleplayerOverlay extends AbstractOverlay {
	public SingleplayerOverlay() {
		super();
		instance = this;
	}
	
	public static SingleplayerOverlay getInstance() {
		return (SingleplayerOverlay) instance;
	}
	
	@Override
	protected void show(Stage stage) {
		super.show(stage);
	}
	
	@Override
	protected void rollDicesButtonClicked() {
		if (ScreenManager.getScreen().getClass() == ScorecardSingleplayerScreen.class) {
			ScreenManager.changeScreenSingleplayer();
		}
		ScreenManager.getRollingDicesSingleplayerScreen().rollDices();
	}
	
	@Override
	protected void changeScreenButtonClicked() {
		ScreenManager.changeScreenSingleplayer();
	}
}
