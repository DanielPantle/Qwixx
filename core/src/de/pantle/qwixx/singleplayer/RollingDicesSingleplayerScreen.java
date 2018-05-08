package de.pantle.qwixx.singleplayer;

import de.pantle.qwixx.utils.AbstractRollingDicesScreen;

/**
 * Created by Daniel on 05.04.2018.
 */

public class RollingDicesSingleplayerScreen extends AbstractRollingDicesScreen {
	public RollingDicesSingleplayerScreen() {
		super();
	}
	
	@Override
	public void show() {
		super.show();
		SingleplayerOverlay.getInstance().show(stage);
	}
}
