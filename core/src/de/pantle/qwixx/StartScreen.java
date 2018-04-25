package de.pantle.qwixx;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.pantle.qwixx.utils.AbstractScreen;
import de.pantle.qwixx.utils.Button;
import de.pantle.qwixx.utils.ScreenManager;

/**
 * Created by Daniel on 03.02.2018.
 */

public class StartScreen extends AbstractScreen {
	
	public StartScreen() {
		super();
		
		Table table = new Table();
		table.setFillParent(true);
		
		// Button fürs Mehrspieler-Modus
		Button multiplayerButton = new Button("Multiplayer", Button.ButtonType.STANDARD);
		multiplayerButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ScreenManager.startMultiplayer();
			}
		});
		table.add(multiplayerButton).center().padBottom(20).row();
		
		// Button für Einzelspieler-Modus
		Button singleplayerButton = new Button("Einzelspieler", Button.ButtonType.STANDARD);
		singleplayerButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ScreenManager.startSingleplayer();
			}
		});
		table.add(singleplayerButton).center();
		
		
		stage.addActor(table);
	}
}
