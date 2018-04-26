package de.pantle.qwixx.multiplayer;

import java.util.ArrayList;

import de.pantle.qwixx.utils.AbstractScreen;
import de.pantle.qwixx.utils.Scorecard;

public class ScorecardMultiplayerScreen extends AbstractScreen {
	private MultiplayerOverlay multiplayerOverlay;
	private ArrayList<Integer> players;
	
	public ScorecardMultiplayerScreen() {
		new Scorecard(stage);
		
		players = new ArrayList<Integer>();
		
		// SingleplayerOverlay: Ausgabe der Zahlenwerte
		multiplayerOverlay = new MultiplayerOverlay(stage);
	}
	
	
	@Override
	public void show() {
		super.show();
		multiplayerOverlay.show(stage);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		
		multiplayerOverlay.resize();
	}
}
