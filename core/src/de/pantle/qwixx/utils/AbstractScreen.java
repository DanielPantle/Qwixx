package de.pantle.qwixx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.pantle.qwixx.appwarp.WarpListener;

/**
 * Created by Daniel on 09.04.2018.
 */

public class AbstractScreen implements Screen, WarpListener {
	protected Stage stage;
	
	public AbstractScreen() {
		stage = new Stage(new ScreenViewport());
	}
	
	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
	public void render(float delta) {
		stage.draw();
	}
	
	
	@Override
	public void resize(int width, int height) {
	
	}
	
	@Override
	public void pause() {
	
	}
	
	@Override
	public void resume() {
	
	}
	
	@Override
	public void hide() {
	
	}
	
	@Override
	public void dispose() {
		stage.dispose();
	}
	
	@Override
	public void onWaitingStarted(String message) {
		Gdx.app.log("onWaitingStarted", message);
	}
	
	@Override
	public void onGameStarted(String message) {
		Gdx.app.log("onGameStarted", message);
	}
	
	@Override
	public void onGameFinished(int code, boolean isRemote) {
		Gdx.app.log("onGameFinished", code + " ; " + isRemote);
	}
	
	@Override
	public void onGameUpdateReceived(String message) {
		Gdx.app.log("onGameUpdateReceived", message);
	}
}
