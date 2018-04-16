package de.pantle.qwixx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;

import de.pantle.qwixx.utils.AbstractScreen;
import de.pantle.qwixx.utils.Button;
import de.pantle.qwixx.utils.Constants;
import de.pantle.qwixx.utils.DiceValues;
import de.pantle.qwixx.utils.ScreenManager;

/**
 * Created by Daniel on 03.02.2018.
 */

public class Scorecard extends AbstractScreen {
	private Stage stage;
	
	private ArrayList<ArrayList<Button>> buttons;
	private Table table;
	private DiceValues diceValues;
	
	public Scorecard() {
		stage = new Stage(new ScreenViewport());
		
		// Tabelle initialisieren
		table = new Table();
		table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() * Constants.EDGE_HEIGHT_PERCENT * 2));
		table.setPosition(0, Gdx.graphics.getHeight() * Constants.EDGE_HEIGHT_PERCENT);
		
		float buttonSize = (table.getHeight() / Constants.ROW_COUNT_TOTAL) - (Constants.BUTTONS_PADDING * 2);
		
		// enthält alle Buttons
		buttons = new ArrayList<ArrayList<Button>>();
		
		// Buttons für die 4 Farben hinzufügen
		for (int row = 0; row < Constants.COLORS_COUNT; row++) {
			ArrayList<Button> colorRowButtons = new ArrayList<Button>();
			
			// Buttons hinzufügen
			for (int i = 2; i <= Constants.BUTTONS_PER_COLOR_COUNT + 1; i++) {
				Button colorButton = new Button(String.valueOf(i), Button.ButtonType.values()[row]);
				
				if (i == Constants.BUTTONS_PER_COLOR_COUNT + 1) {
					colorButton.setText("+");
					colorButton.setDisabled(true);
				}
				
				colorButton.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						buttonClicked(actor);
					}
				});
				
				table.add(colorButton).size(buttonSize).pad(5);
				
				colorRowButtons.add(colorButton);
			}
			
			buttons.add(colorRowButtons);
			table.row();
		}
		
		
		// Buttons für die Fehlwürfe hinzufügen
		ArrayList<Button> missButtons = new ArrayList<Button>();
		table.add(new Image()).colspan(8);
		for (int i = 1; i < 5; i++) {
			Button missButton = new Button("", Button.ButtonType.MISS);
			
			missButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					missButtonClicked(actor);
				}
			});
			
			table.add(missButton).size(buttonSize).pad(5).right();
			
			missButtons.add(missButton);
		}
		buttons.add(missButtons);
		table.row();
		table.row();
		
		
		// Buttons für die Auswertung hinzufügen
		ArrayList<Button> resultButtons = new ArrayList<Button>();
		for (int i = 0; i < 6; i++) {
			Button resultButton = new Button("0", Button.ButtonType.values()[i]);
			resultButton.setDisabled(true);
			resultButtons.add(resultButton);
			
			if (i < 5) {
				table.add(resultButton).size(buttonSize).pad(5).colspan(1);
			} else {
				table.add(resultButton).size(buttonSize * 2, buttonSize).pad(5).colspan(2);
			}
			
			if (i < 5) {
				String text = "";
				if (i < 3) {
					text = "+";
				} else if (i == 3) {
					text = "-";
				} else {
					text = "=";
				}
				
				Button operatorButton = new Button(text, Button.ButtonType.OPERATOR);
				operatorButton.setDisabled(true);
				resultButtons.add(operatorButton);
				
				table.add(operatorButton).size(buttonSize).pad(5).colspan(1);
			}
		}
		buttons.add(resultButtons);
		
		// Tabelle zur Stage hinzufügen
		stage.addActor(table);
		
		
		// Labels: Ausgabe der Zahlenwerte
		diceValues = new DiceValues();
		stage.addActor(diceValues);
		
		// Button zu den Würfeln hinzufügen
		Button.init(Color.WHITE);
		Button showDicesScreenButton = new Button("Würfel anzeigen", Button.ButtonType.NORMAL);
		showDicesScreenButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				DiceValues.saveValues();
				ScreenManager.showRollingDices();
			}
		});
		showDicesScreenButton.setSize((Gdx.graphics.getWidth() / 2) - Constants.BUTTONS_PADDING, (Gdx.graphics.getHeight() * Constants.EDGE_HEIGHT_PERCENT) - Constants.BUTTONS_PADDING);
		showDicesScreenButton.setPosition(0, 0);
		stage.addActor(showDicesScreenButton);
	}
	
	
	private void buttonClicked(Actor actor) {
		Button button = (Button) actor;
		
		for (int row = 0; row < buttons.size(); row++) {
			if (buttons.get(row).contains(button)) {
				if (!button.getText().toString().equals("+")) {
					if (!button.isChecked()) {
						// Button ist gecheckt - Buttons davor wieder aktivieren
						
						for (int i = buttons.get(row).indexOf(button) - 1; i >= 0; i--) {
							buttons.get(row).get(i).setDisabled(false);
							buttons.get(row).get(i).getStyle().down = buttons.get(row).get(i).getStyle().up;
							
							if (buttons.get(row).get(i).isChecked()) {
								break;
							}
						}
					} else {
						// Button ist nicht gecheckt
						
						// Button nicht checken, wenn rechts davon einer gecheckt ist
						for (int i = buttons.get(row).indexOf(button) + 1; i < buttons.get(row).size(); i++) {
							if (buttons.get(row).get(i).isChecked()) {
								if (button.isChecked()) {
									button.setChecked(false);
								}
								break;
							}
						}
						
						// Buttons links davon deaktivieren
						for (int i = buttons.get(row).indexOf(button) - 1; i >= 0; i--) {
							buttons.get(row).get(i).setDisabled(true);
							if (buttons.get(row).get(i).isChecked()) {
								buttons.get(row).get(i).getStyle().down = buttons.get(row).get(i).getStyle().checked;
							}
						}
					}
					if (button.getText().toString().equals("12")) {
						// Button 12
						
						if (button.isChecked()) {
							// Button ist nicht gecheckt - prüfen, ob Button gecheckt werden kann
							int buttonsChecked = 0;
							
							for (int i = 0; i < 11; i++) {
								if (buttons.get(row).get(i).isChecked()) {
									buttonsChecked++;
								}
							}
							
							if (buttonsChecked <= 5) {
								// Button nicht checken, wenn nicht mind. 5 Buttons vorher gecheckt sind
								button.setChecked(false);
							} else {
								// +-Button mitchecken
								buttons.get(row).get(buttons.get(row).size() - 1).setChecked(true);
								buttons.get(row).get(buttons.get(row).size() - 1).getStyle().down = buttons.get(row).get(buttons.get(row).size() - 1).getStyle().checked;
							}
						} else {
							// Button ist gecheckt - +-Button mit unchecken
							buttons.get(row).get(buttons.get(row).size() - 1).setChecked(false);
							buttons.get(row).get(buttons.get(row).size() - 1).getStyle().down = buttons.get(row).get(buttons.get(row).size() - 1).getStyle().up;
						}
					}
					
					
					int cross = 1;
					int result = 0;
					for (int i = 0; i < 12; i++) {
						if (buttons.get(row).get(i).isChecked()) {
							result += cross;
							cross++;
						}
					}
					
					buttons.get(5).get(row * 2).setText(String.valueOf(result));
					
					int res = Integer.parseInt(buttons.get(5).get(0).getText().toString())
							+ Integer.parseInt(buttons.get(5).get(2).getText().toString())
							+ Integer.parseInt(buttons.get(5).get(4).getText().toString())
							+ Integer.parseInt(buttons.get(5).get(6).getText().toString())
							+ Integer.parseInt(buttons.get(5).get(8).getText().toString());
					buttons.get(5).get(10).setText(String.valueOf(res));
					
					// Schleife abbrechen, Button wurde gefunden
					break;
				}
			}
		}
	}
	
	private void missButtonClicked(Actor actor) {
		Button button = (Button) actor;
		
		if (button.isChecked()) {
			for (int j = 0; j < buttons.get(4).indexOf(button); j++) {
				if (!buttons.get(4).get(j).isChecked()) {
					buttons.get(4).get(j).setChecked(true);
					button.setChecked(false);
					break;
				}
			}
		} else {
			for (int j = 3; j > buttons.get(4).indexOf(button); j--) {
				if (buttons.get(4).get(j).isChecked()) {
					buttons.get(4).get(j).setChecked(false);
					button.setChecked(true);
					break;
				}
			}
		}
		
		int result = 0;
		for (int i = 0; i < 4; i++) {
			if (buttons.get(4).get(i).isChecked()) {
				result -= 5;
			}
		}
		
		buttons.get(5).get(8).setText(String.valueOf(result));
		
		int res = Integer.parseInt(buttons.get(5).get(0).getText().toString())
				+ Integer.parseInt(buttons.get(5).get(2).getText().toString())
				+ Integer.parseInt(buttons.get(5).get(4).getText().toString())
				+ Integer.parseInt(buttons.get(5).get(6).getText().toString())
				+ Integer.parseInt(buttons.get(5).get(8).getText().toString());
		buttons.get(5).get(10).setText(String.valueOf(res));
	}
	
	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		diceValues.loadValues();
	}
	
	@Override
	public void render(float delta) {
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		//table.setSize(width - 40, height - 40);
		
		diceValues.resize();
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
}
