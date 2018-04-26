package de.pantle.qwixx.networking;

import de.pantle.qwixx.multiplayer.StartMultiplayerScreen;

public class ConnectionHelper {
	public static void init(StartMultiplayerScreen screen) {
		GameServer.setNetworkListener(screen);
		GameClient.setNetworkListener(screen);
	}
}
