package de.pantle.qwixx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import de.pantle.qwixx.utils.Button;
import de.pantle.qwixx.utils.DiceValues;
import de.pantle.qwixx.utils.ScreenManager;

public class Qwixx extends Game {
	@Override
	public void create() {
		// initialisations
		Button.init();
		
		ScreenManager.init(this);
		ScreenManager.showScorecard();
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		super.render();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		Button.dispose();
	}
}
