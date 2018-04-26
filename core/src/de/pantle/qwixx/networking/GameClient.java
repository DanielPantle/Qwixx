package de.pantle.qwixx.networking;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

public class GameClient {
	private static Client client;
	private static de.pantle.qwixx.networking.NetworkListener networkListener;
	//private static String serverAddress;
	
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
					networkListener.onReceived(connection, (Message) object);
				}
			}
		});
	}
	
	public static void setNetworkListener(de.pantle.qwixx.networking.NetworkListener networkListener) {
		GameClient.networkListener = networkListener;
	}
	
	/*
	private static void setServerAddress(String serverAddress) {
		//GameClient2.serverAddress = serverAddress;
	}
	*/
	
	public static void connect() {
		if (client == null) {
			init();
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String serverAddress = client.discoverHost(de.pantle.qwixx.networking.NetworkConstants.UDP_PORT, de.pantle.qwixx.networking.NetworkConstants.TIMEOUT_GET_SERVER_ADDRESS).getHostAddress();
					//setServerAddress(serverAddress);
					client.start();
					
					client.connect(de.pantle.qwixx.networking.NetworkConstants.TIMOUT_CONNECT_TO_SERVER, serverAddress, de.pantle.qwixx.networking.NetworkConstants.TCP_PORT, de.pantle.qwixx.networking.NetworkConstants.UDP_PORT);
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
	
	/*
	public static void sendToServer(Message.MessageType type, Object object) {
		client.sendTCP(new Message(type, object));
	}
	*/
}
