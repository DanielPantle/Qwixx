package de.pantle.qwixx.networking;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

public class GameServer {
	private static Server server;
	private static de.pantle.qwixx.networking.NetworkListener networkListener;
	
	private static boolean serverRunning = false;
	
	private static void init() {
		// init and start server
		server = new Server();
		
		// add listeners to server
		server.addListener(new Listener() {
			@Override
			public void connected(Connection connection) {
				// client connected to server
				networkListener.onClientConnected(connection);
			}
			
			@Override
			public void disconnected(final Connection connection) {
				// client disconnected from server
				networkListener.onClientDisconnected(connection);
			}
			
			@Override
			public void received(Connection connection, Object object) {
				// message from client received
				if (object instanceof Message) {
					networkListener.onReceived(connection, (Message) object);
				}
			}
		});
		
		// register classes
		Message.registerClasses(server);
	}
	
	public static void setNetworkListener(de.pantle.qwixx.networking.NetworkListener networkListener) {
		GameServer.networkListener = networkListener;
	}
	
	/*
	public static void sendToClient(int connectionId, Message.MessageType messageType) {
		server.sendToTCP(connectionId, new Message(messageType));
	}
	public static void sendToClient(int connectionId, Message.MessageType messageType, Object object) {
		server.sendToTCP(connectionId, new Message(messageType, object));
	}
	*/
	
	public static void sendMessage(Message message) {
		server.sendToAllTCP(message);
	}
	
	public static void start() {
		if (server == null) {
			init();
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				try {
					// starts and opens the server
					server.start();
					server.bind(de.pantle.qwixx.networking.NetworkConstants.TCP_PORT, de.pantle.qwixx.networking.NetworkConstants.UDP_PORT);
					
					Gdx.app.log(getClass().getSimpleName(), "server started successfully");
					
					// start game screen
					networkListener.onServerStarted();
				}
				catch (IOException e) {
					Gdx.app.log(getClass().getSimpleName(), "error starting server: " + e.getMessage());
					networkListener.onStartingServerFailed();
				}
			}
		}).start();
	}
	
	public static int getConnectedClientsCount() {
		return server.getConnections().length;
	}
}
