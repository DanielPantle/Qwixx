package de.pantle.qwixx.screen;

import de.pantle.qwixx.utils.Scorecard;

/**
 * Created by Daniel on 03.02.2018.
 */

public class ScorecardScreen extends AbstractScreen {
	private Scorecard scorecard;
	
	public ScorecardScreen() {
		super();
		
		// Overlay initialisieren
		new Overlay();
		
		// Scorecard anzeigen
		scorecard = new Scorecard(stage);
	}
	
	
	@Override
	public void show() {
		super.show();
		Overlay.getInstance().show(stage);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		
		Overlay.resize();
	}
}
