package de.pantle.qwixx.utils;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;

import de.pantle.qwixx.networking.ConnectionHelper;
import de.pantle.qwixx.networking.GameServer;
import de.pantle.qwixx.networking.Message;

public class ServerGameController {
	private static ServerListener serverListener;
	
	// Spieler, der dran ist
	private static int currentPlayer;
	// Liste aller Spieler (ids)
	private static ArrayList<Integer> players;
	
	public static void init() {
		// wird beim Starten des Multiplayer-Spiels aufgerufen
		players = ConnectionHelper.getPlayers();
		currentPlayer = players.get(0);
		GameServer.getServer().sendToAllTCP(new Message(Message.MessageType.NEXT_PLAYER, currentPlayer));
		
		// Nachricht an Server, wer dran ist
		serverListener.onMoveFinished(new Message(Message.MessageType.NEXT_PLAYER, currentPlayer));
	}
	
	public static void finishMove() {
		// Spieler beendet seinen Zug - nächsten Spieler auswählen
		if (players.indexOf(currentPlayer) >= players.size() - 1) {
			currentPlayer = players.get(0);
		}
		else {
			currentPlayer = players.get(players.indexOf(currentPlayer) + 1);
		}
		
		// Nachricht senden, wer dran ist
		Gdx.app.log("new current player", "" + currentPlayer);
		GameServer.getServer().sendToAllTCP(new Message(Message.MessageType.NEXT_PLAYER, currentPlayer));
		serverListener.onMoveFinished(new Message(Message.MessageType.NEXT_PLAYER, currentPlayer));
	}
	
	public static void setServerListener(ServerListener serverListener) {
		ServerGameController.serverListener = serverListener;
	}
}
