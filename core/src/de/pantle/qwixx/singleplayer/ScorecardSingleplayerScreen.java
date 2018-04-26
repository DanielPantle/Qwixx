package de.pantle.qwixx.singleplayer;

import de.pantle.qwixx.utils.AbstractScreen;
import de.pantle.qwixx.utils.Scorecard;

/**
 * Created by Daniel on 03.02.2018.
 */

public class ScorecardSingleplayerScreen extends AbstractScreen {
	private SingleplayerOverlay singleplayerOverlay;
	
	public ScorecardSingleplayerScreen() {
		super();
		
		// Scorecard anzeigen
		new Scorecard(stage);
		
		// SingleplayerOverlay: Ausgabe der Zahlenwerte
		singleplayerOverlay = new SingleplayerOverlay(stage);
	}
	
	
	@Override
	public void show() {
		super.show();
		singleplayerOverlay.show(stage);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		
		singleplayerOverlay.resize();
	}
}
