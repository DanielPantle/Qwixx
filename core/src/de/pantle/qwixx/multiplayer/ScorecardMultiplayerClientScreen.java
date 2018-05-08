package de.pantle.qwixx.multiplayer;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;

import de.pantle.qwixx.networking.ConnectionHelper;
import de.pantle.qwixx.networking.GameClient;
import de.pantle.qwixx.networking.Message;

public class ScorecardMultiplayerClientScreen extends AbstractScorecardMultiplayerScreen {
	private int id;
	
	@Override
	public void show() {
		super.show();
		
		// ClientListener setzen
		GameClient.setNetworkListener(this);
		
		id = ConnectionHelper.getId();
	}
	
	@Override
	public void onReceived(Connection connection, Message message) {
		Gdx.app.log(getClass().getSimpleName(), "onReceived: " + message.getType());
		
		if (message.getType() == Message.MessageType.NEXT_PLAYER) {
			// Spieler hat seinen Zug beendet
			int currentPlayer = ((Integer) message.getObject());
			if (currentPlayer == id) {
				// Spieler ist dran
				MultiplayerOverlay.enableButtons();
				MultiplayerOverlay.setCurrentPlayerText();
			}
			else {
				// Spieler ist nicht dran
				MultiplayerOverlay.disableButtons();
				MultiplayerOverlay.setCurrentPlayerText(String.valueOf(currentPlayer));
			}
		}
	}
	
	@Override
	public void onButtonFinishClicked() {
		GameClient.getClient().sendTCP(new Message(Message.MessageType.MOVE_FINISHED));
	}
}
