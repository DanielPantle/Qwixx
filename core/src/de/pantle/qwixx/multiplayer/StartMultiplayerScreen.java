package de.pantle.qwixx.multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.Random;

import de.pantle.qwixx.appwarp.WarpController;
import de.pantle.qwixx.utils.AbstractScreen;

public class StartMultiplayerScreen extends AbstractScreen {
	
	private Label output;
	
	public StartMultiplayerScreen() {
		super();
		
		Table table = new Table();
		table.setFillParent(true);
		
		output = new Label("", new Label.LabelStyle(new BitmapFont(), Color.BLACK));
		table.add(output).center();
		
		stage.addActor(table);
	}
	
	@Override
	public void show() {
		super.show();
		
		// init Warp
		WarpController.getInstance().setListener(this);
		WarpController.getInstance().startApp(getRandomUsername());
		setOutput("Verbindung wird hergestellt...");
	}
	
	public void setOutput(String message) {
		output.setText(message);
	}
	
	private String getRandomUsername() {
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		while (sb.length() < 10) {
			sb.append(Integer.toHexString(random.nextInt()));
		}
		return sb.toString().substring(0, 10);
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
