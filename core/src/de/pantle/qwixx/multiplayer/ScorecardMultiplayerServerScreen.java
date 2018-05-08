package de.pantle.qwixx.multiplayer;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;

import de.pantle.qwixx.networking.ConnectionHelper;
import de.pantle.qwixx.networking.GameServer;
import de.pantle.qwixx.networking.Message;
import de.pantle.qwixx.utils.ServerGameController;

public class ScorecardMultiplayerServerScreen extends AbstractScorecardMultiplayerScreen {
	@Override
	public void show() {
		super.show();
		
		ServerGameController.setServerListener(this);
		GameServer.setNetworkListener(this);
		
		ServerGameController.init();
	}
	
	@Override
	public void onButtonFinishClicked() {
		ServerGameController.finishMove();
	}
	
	@Override
	public void onMoveFinished(Message message) {
		// Spieler hat Zug beendet
		int currentPlayer = ((Integer) message.getObject());
		
		if (currentPlayer == ConnectionHelper.SERVER_ID) {
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
	
	@Override
	public void onReceived(Connection connection, Message message) {
		Gdx.app.log(getClass().getSimpleName(), "onReceived: " + message.getType());
		
		if (message.getType() == Message.MessageType.MOVE_FINISHED) {
			ServerGameController.finishMove();
		}
		else if (message.getType() == Message.MessageType.NEXT_PLAYER) {
			onMoveFinished(message);
		}
	}
}
