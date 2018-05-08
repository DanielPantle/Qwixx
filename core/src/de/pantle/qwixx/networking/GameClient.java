package de.pantle.qwixx.networking;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

public class GameClient {
	private static Client client;
	private static de.pantle.qwixx.networking.NetworkListener networkListener;
	
	private static void init() {
		// init and start client
		client = new Client();
		
		// register classes
		Message.registerClasses(client);
		
		client.addListener(new Listener() {
			@Override
			public void connected(Connection connection) {
				// client connected to server
				networkListener.onConnectedToServer(connection);
			}
			
			@Override
			public void disconnected(Connection connection) {
				// client disconnected from server
				networkListener.onServerDisconnected(connection);
			}
			
			@Override
			public void received(final Connection connection, final Object object) {
				// received message from server
				if (object instanceof Message) {
					Gdx.app.log("GameClient", "received: " + ((Message) object).getType());
					networkListener.onReceived(connection, (Message) object);
				}
			}
		});
	}
	
	public static void setNetworkListener(NetworkListener networkListener) {
		GameClient.networkListener = networkListener;
	}
	
	public static void connect() {
		if (client == null) {
			init();
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String serverAddress = client.discoverHost(NetworkConstants.UDP_PORT, NetworkConstants.TIMEOUT_GET_SERVER_ADDRESS).getHostAddress();
					client.start();
					
					client.connect(NetworkConstants.TIMOUT_CONNECT_TO_SERVER, serverAddress, NetworkConstants.TCP_PORT, NetworkConstants.UDP_PORT);
				}
				catch (NullPointerException e) {
					networkListener.onConnectingFailed();
				}
				catch (IOException e) {
					networkListener.onConnectingFailed();
				}
			}
		}).start();
	}
	
	public static Client getClient() {
		return client;
	}
}
