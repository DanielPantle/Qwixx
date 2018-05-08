package de.pantle.qwixx.multiplayer;

import de.pantle.qwixx.utils.AbstractRollingDicesScreen;

/**
 * Created by Daniel on 05.04.2018.
 */

public class RollingDicesMultiplayerScreen extends AbstractRollingDicesScreen {
	public RollingDicesMultiplayerScreen() {
		super();
	}
	
	@Override
	public void show() {
		super.show();
		MultiplayerOverlay.getInstance().show(stage);
	}
}
