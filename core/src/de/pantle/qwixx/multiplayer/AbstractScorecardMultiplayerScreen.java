package de.pantle.qwixx.multiplayer;

import com.esotericsoftware.kryonet.Connection;

import de.pantle.qwixx.networking.Message;
import de.pantle.qwixx.networking.NetworkListener;
import de.pantle.qwixx.utils.AbstractScreen;
import de.pantle.qwixx.utils.Scorecard;
import de.pantle.qwixx.utils.ServerListener;

public abstract class AbstractScorecardMultiplayerScreen extends AbstractScreen implements ServerListener, NetworkListener {
	public AbstractScorecardMultiplayerScreen() {
		new Scorecard(stage);
		
		// MultiplayerOverlay initialisieren
		new MultiplayerOverlay();
	}
	
	
	@Override
	public void show() {
		super.show();
		
		MultiplayerOverlay.setGameListener(this);
		MultiplayerOverlay.getInstance().show(stage);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
	}
	
	@Override
	public void resize(int width, int height) {
		//stage.getViewport().update(width, height, true);
		
		MultiplayerOverlay.resize();
	}
	
	
	@Override
	public abstract void onButtonFinishClicked();
	
	@Override
	public abstract void onReceived(Connection connection, Message message);
	
	@Override
	public void onClientConnected(Connection connection) {
	}
	
	@Override
	public void onMoveFinished(Message message) {
	}
	
	@Override
	public void onConnectedToServer(Connection connection) {
	}
	
	@Override
	public void onClientDisconnected(Connection connection) {
	}
	
	@Override
	public void onServerDisconnected(Connection connection) {
	}
	
	@Override
	public void onStartingServerFailed() {
	}
	
	@Override
	public void onServerStarted() {
	}
	
	@Override
	public void onConnectingFailed() {
	}
}
