package de.pantle.qwixx.networking;

import com.esotericsoftware.kryonet.Connection;

import java.util.ArrayList;

public class ConnectionHelper {
	public static final int SERVER_ID = -1;
	
	public static void connectClient() {
		GameClient.connect();
	}
	
	public static void startServer() {
		GameServer.start();
	}
	
	public static int getConnectedClientsCount() {
		return GameServer.getServer().getConnections().length;
	}
	
	public static void sendMessageFromServer(Message message) {
		GameServer.getServer().sendToAllTCP(message);
	}
	
	private static void sendMessageFromClient(Message message) {
		GameClient.getClient().sendTCP(message);
	}
	
	public static ArrayList<Integer> getPlayers() {
		ArrayList<Integer> players = new ArrayList<Integer>();
		// Server hinzufügen
		players.add(SERVER_ID);
		
		for (Connection player : GameServer.getServer().getConnections()) {
			players.add(player.getID());
		}
		return players;
	}
	
	public static int getId() {
		if (GameClient.getClient().isConnected()) {
			return GameClient.getClient().getID();
		}
		else {
			return SERVER_ID;
		}
	}
}
