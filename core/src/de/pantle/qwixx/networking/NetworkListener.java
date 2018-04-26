package de.pantle.qwixx.networking;

import com.esotericsoftware.kryonet.Connection;

public interface NetworkListener {
	void onClientConnected(Connection connection);
	
	void onConnectedToServer(Connection connection);
	
	void onClientDisconnected(Connection connection);
	
	void onServerDisconnected(Connection connection);
	
	void onReceived(Connection connection, Message message);
	
	void onStartingServerFailed();
	
	void onServerStarted();
	
	void onConnectingFailed();
}
