package de.pantle.qwixx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.ArrayList;

public class Scorecard {
	private ArrayList<ArrayList<Button>> buttons;
	
	public Scorecard(Stage stage) {
		
		// Tabelle initialisieren
		Table table = new Table();
		table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() * Constants.EDGE_HEIGHT_PERCENT * 2));
		table.setPosition(0, Gdx.graphics.getHeight() * Constants.EDGE_HEIGHT_PERCENT);
		
		float buttonSize = (table.getHeight() / Constants.ROW_COUNT_TOTAL) - (Constants.BUTTONS_PADDING * 2);
		
		// enthält alle Felder
		buttons = new ArrayList<ArrayList<Button>>();
		
		// Felder für die 4 Farben hinzufügen
		for (int row = 0; row < Constants.COLORS_COUNT; row++) {
			ArrayList<Button> colorRowButtons = new ArrayList<Button>();
			
			int number;
			// Felder hinzufügen
			for (int i = 2; i <= Constants.BUTTONS_PER_COLOR_COUNT + 1; i++) {
				if (row < Constants.COLORS_COUNT / 2) {
					number = i;
				}
				else {
					number = 14 - i;
				}
				Button colorButton = new Button(String.valueOf(number), Button.ButtonType.values()[row]);
				colorButton.setSize(buttonSize, buttonSize);
				
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
		
		
		// Fehlwurf-Felder hinzufügen
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
		
		
		// Auswertungs-Felder hinzufügen
		ArrayList<Button> resultButtons = new ArrayList<Button>();
		for (int i = 0; i < 6; i++) {
			Button resultButton = new Button("0", Button.ButtonType.values()[i]);
			resultButton.setDisabled(true);
			resultButtons.add(resultButton);
			
			if (i < 5) {
				table.add(resultButton).size(buttonSize).pad(5).colspan(1);
			}
			else {
				table.add(resultButton).size(buttonSize * 2, buttonSize).pad(5).colspan(2);
			}
			
			if (i < 5) {
				String text;
				if (i < 3) {
					text = "+";
				}
				else if (i == 3) {
					text = "-";
				}
				else {
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
	}
	
	
	private void buttonClicked(Actor actor) {
		Button button = (Button) actor;
		
		for (int row = 0; row < buttons.size(); row++) {
			if (buttons.get(row).contains(button)) {
				if (!button.getText().toString().equals("+")) {
					if (!button.isChecked()) {
						// Feld ist angekreuzt - Felder davor wieder aktivieren
						
						for (int i = buttons.get(row).indexOf(button) - 1; i >= 0; i--) {
							buttons.get(row).get(i).setDisabled(false);
							buttons.get(row).get(i).getStyle().down = buttons.get(row).get(i).getStyle().up;
							
							if (buttons.get(row).get(i).isChecked()) {
								break;
							}
						}
					}
					else {
						// Feld ist nicht angekreuzt
						
						// Feld nicht ankreuzen, wenn rechts davon ein Feld angekreuzt ist ist
						for (int i = buttons.get(row).indexOf(button) + 1; i < buttons.get(row).size(); i++) {
							if (buttons.get(row).get(i).isChecked()) {
								if (button.isChecked()) {
									button.setChecked(false);
								}
								break;
							}
						}
						
						
						// Felder links davon deaktivieren
						for (int i = buttons.get(row).indexOf(button) - 1; i >= 0; i--) {
							buttons.get(row).get(i).setDisabled(true);
							if (buttons.get(row).get(i).isChecked()) {
								buttons.get(row).get(i).getStyle().down = buttons.get(row).get(i).getStyle().checked;
							}
						}
					}
					
					// Feld 12
					if ((row < Constants.COLORS_COUNT / 2 && button.getText().toString().equals("12"))
							|| (row >= Constants.COLORS_COUNT / 2 && button.getText().toString().equals("2"))) {
						
						if (button.isChecked()) {
							// Feld ist nicht angekreuzt - prüfen, ob Feld angekreuzt werden kann
							int buttonsChecked = 0;
							
							for (int i = 0; i < 11; i++) {
								if (buttons.get(row).get(i).isChecked()) {
									buttonsChecked++;
								}
							}
							
							if (buttonsChecked <= 5) {
								// Feld nicht ankreuzen, wenn nicht mind. 5 Felder vorher angekreuzt sind
								button.setChecked(false);
							}
							else {
								// +-Felder mit-ankreuzen
								buttons.get(row).get(buttons.get(row).size() - 1).setChecked(true);
								buttons.get(row).get(buttons.get(row).size() - 1).getStyle().down = buttons.get(row).get(buttons.get(row).size() - 1).getStyle().checked;
							}
						}
						else {
							// Feld ist angekreuzt - Kreuz auf +-Feld mit-entfernen
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
		}
		else {
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
}
