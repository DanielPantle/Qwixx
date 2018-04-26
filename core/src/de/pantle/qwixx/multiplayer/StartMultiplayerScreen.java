package de.pantle.qwixx.multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.kryonet.Connection;

import de.pantle.qwixx.networking.ConnectionHelper;
import de.pantle.qwixx.networking.GameClient;
import de.pantle.qwixx.networking.GameServer;
import de.pantle.qwixx.networking.Message;
import de.pantle.qwixx.networking.NetworkListener;
import de.pantle.qwixx.utils.AbstractScreen;
import de.pantle.qwixx.utils.Button;
import de.pantle.qwixx.utils.ScreenManager;

public class StartMultiplayerScreen extends AbstractScreen implements NetworkListener {
	private Table table;
	private Label output;
	
	public StartMultiplayerScreen() {
		super();
		
		table = new Table();
		table.setFillParent(true);
		
		output = new Label("", new Label.LabelStyle(new BitmapFont(), Color.BLACK));
		table.add(output).center();
		
		stage.addActor(table);
		
		// Server und Client initialisieren
		ConnectionHelper.init(this);
	}
	
	private void showStartGameButton() {
		Button showStartGameButton = new Button("Spiel starten", Button.ButtonType.STANDARD);
		showStartGameButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				GameServer.sendMessage(new Message(Message.MessageType.START_GAME));
				ScreenManager.showScorecardMultiplayerScreen();
			}
		});
		table.row().pad(20);
		table.add(showStartGameButton);
	}
	
	@Override
	public void show() {
		super.show();
		
		// Versuch, mit Server zu verbinden
		setOutput("Verbindung wird hergestellt...");
		GameClient.connect();
	}
	
	private void setOutput(String message) {
		output.setText(output.getText() + "\n" + message);
	}
	
	
	@Override
	public void onClientConnected(Connection connection) {
		Gdx.app.log(getClass().getSimpleName(), "onConnected");
		setOutput(GameServer.getConnectedClientsCount() + " Client(s) verbunden");
	}
	
	@Override
	public void onConnectedToServer(Connection connection) {
		Gdx.app.log(getClass().getSimpleName(), "onConnectedToServer");
		setOutput("Verbindung mit Server hergestellt");
	}
	
	@Override
	public void onClientDisconnected(Connection connection) {
		Gdx.app.log(getClass().getSimpleName(), "onClientDisconnected");
		setOutput(GameServer.getConnectedClientsCount() + " Client(s) verbunden");
	}
	
	@Override
	public void onReceived(Connection connection, Message message) {
		Gdx.app.log(getClass().getSimpleName(), "onReceived");
		Gdx.app.log(getClass().getSimpleName(), "Message type: " + message.getType());
		if (message.getType() == Message.MessageType.START_GAME) {
			ScreenManager.showScorecardMultiplayerScreen();
		}
	}
	
	@Override
	public void onStartingServerFailed() {
		Gdx.app.log(getClass().getSimpleName(), "onStartingServerFailed");
		setOutput("Starten des Servers fehlgeschlagen");
		GameClient.connect();
	}
	
	@Override
	public void onServerDisconnected(Connection connection) {
		Gdx.app.log(getClass().getSimpleName(), "onStartingServerFailed");
		setOutput("Serververbindung abgebrochen");
		GameClient.connect();
	}
	
	@Override
	public void onServerStarted() {
		Gdx.app.log(getClass().getSimpleName(), "onServerStarted");
		setOutput("Server erfolgreich gestartet. Warte auf Clients...");
		showStartGameButton();
	}
	
	@Override
	public void onConnectingFailed() {
		Gdx.app.log(getClass().getSimpleName(), "onConnectingFailed");
		setOutput("Verbinden mit Server fehlgeschlagen");
		GameServer.start();
	}
}
