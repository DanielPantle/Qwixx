package de.pantle.qwixx.multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.pantle.qwixx.utils.AbstractOverlay;
import de.pantle.qwixx.utils.Button;
import de.pantle.qwixx.utils.GameListener;
import de.pantle.qwixx.utils.ScreenManager;

public class MultiplayerOverlay extends AbstractOverlay {
	private static GameListener gameListener;
	
	private static Button finishMoveButton;
	private static Label currentPlayerLabel;
	
	public MultiplayerOverlay() {
		super();
		
		currentPlayerLabel = new Label("", new Label.LabelStyle(new BitmapFont(), Color.BLACK));
		currentPlayerLabel.setHeight(50);
		currentPlayerLabel.setPosition(0, Gdx.graphics.getHeight() - currentPlayerLabel.getHeight());
		
		finishMoveButton = new Button("Fertig", Button.ButtonType.STANDARD);
		finishMoveButton.setPosition(Gdx.graphics.getWidth() - finishMoveButton.getWidth(), Gdx.graphics.getHeight() - finishMoveButton.getHeight());
		disableButtons();
		finishMoveButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameListener.onButtonFinishClicked();
			}
		});
		
		instance = this;
	}
	
	public static MultiplayerOverlay getInstance() {
		return (MultiplayerOverlay) instance;
	}
	
	@Override
	protected void show(Stage stage) {
		super.show(stage);
		
		// current-player-Label und finish-move-Button anzeigen
		stage.addActor(currentPlayerLabel);
		stage.addActor(finishMoveButton);
	}
	
	@Override
	public void rollDicesButtonClicked() {
		if (ScreenManager.getScreen().getClass() == ScorecardMultiplayerServerScreen.class
				|| ScreenManager.getScreen().getClass() == ScorecardMultiplayerClientScreen.class) {
			ScreenManager.changeScreenMultiplayer();
		}
		ScreenManager.getRollingDicesMultiplayerScreen().rollDices();
	}
	
	@Override
	public void changeScreenButtonClicked() {
		ScreenManager.changeScreenMultiplayer();
	}
	
	public static void setGameListener(GameListener gameListener) {
		MultiplayerOverlay.gameListener = gameListener;
	}
	
	public static void enableButtons() {
		AbstractOverlay.enableButtons();
		finishMoveButton.setTouchable(Touchable.enabled);
	}
	
	public static void disableButtons() {
		AbstractOverlay.disableButtons();
		finishMoveButton.setTouchable(Touchable.disabled);
	}
	
	public static void setCurrentPlayerText() {
		currentPlayerLabel.setText("Du bist dran!");
	}
	
	public static void setCurrentPlayerText(String currentPlayer) {
		currentPlayerLabel.setText("Spieler " + currentPlayer + " ist dran.");
	}
}
