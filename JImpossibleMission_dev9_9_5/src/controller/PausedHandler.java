// in controller/PausedHandler.java
package controller;

import model.GameModel;
import model.GameState;

import static model.GameConstants.LOGIC_HEIGHT;
import static model.GameConstants.LOGIC_WIDTH;
import static model.GameConstants.SCALE;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class PausedHandler {

	private GameModel gameModel;
	private InputHandler inputHandler;

	// Aree cliccabili per i bottoni
	private Rectangle resumeButtonBounds = new Rectangle((int) (245 * SCALE), (int) (150 * SCALE), (int) (150 * SCALE),
			(int) (25 * SCALE));
	private Rectangle menuButtonBounds = new Rectangle((int) (245 * SCALE), (int) (185 * SCALE), (int) (150 * SCALE),
			(int) (25 * SCALE));

	// Stato per l'hover
	private boolean resumeHover = false;
	private boolean menuHover = false;
	
	private boolean wasAnyButtonHovered = false;

	public PausedHandler(GameModel gameModel, InputHandler inputHandler) {
		this.gameModel = gameModel;
		this.inputHandler = inputHandler;
	}

	public void handleInput() {
		int mx = inputHandler.getMouseX();
		int my = inputHandler.getMouseY();
		resumeHover = resumeButtonBounds.contains(mx, my);
		menuHover = menuButtonBounds.contains(mx, my);

		boolean isCurrentlyHovered = resumeHover || menuHover;

		// Riproduci il suono solo se ORA siamo in hover, ma PRIMA non lo eravamo
		if (isCurrentlyHovered && !wasAnyButtonHovered) {
			AudioManager.getInstance().play("click_2");
		}

		// Aggiorna la variabile di stato per il prossimo frame
		wasAnyButtonHovered = isCurrentlyHovered;

		if (inputHandler.isMouseButtonPressed(MouseEvent.BUTTON1)) {
			if (resumeHover) {
				AudioManager.getInstance().play("unpause");
				gameModel.setGameState(gameModel.getStateBeforePause());
			} else if (menuHover) {
				AudioManager.getInstance().play("click");
				endGameAndReturnToMenu();
			}
		}

		// Gestione tasto ESC
		if (inputHandler.isKeyPressed(KeyEvent.VK_ESCAPE)) {
			AudioManager.getInstance().play("unpause");
			gameModel.setGameState(gameModel.getStateBeforePause());
		}

		inputHandler.resetMouse();
		inputHandler.resetKeys();
	}

	// NUOVO METODO PRIVATO
	private void endGameAndReturnToMenu() {
		// Considera l'uscita come una partita persa con 0 punti
		if (gameModel.getActiveProfile() != null) {
			gameModel.getActiveProfile().endGame(false, 0, gameModel.getCurrentGameSession().getElapsedTime(), 0);
			ProfileManager.saveProfile(gameModel.getActiveProfile());
		}
		gameModel.getPlayer().teleport((int) (LOGIC_WIDTH / 2.24), (int) (LOGIC_HEIGHT / 4.1));
		gameModel.setGameState(GameState.HOMESCREEN);
		AudioManager.getInstance().loopMenuMusic("menu_theme");
	}

	// Getters per la View
	public Rectangle getResumeButtonBounds() {
		return resumeButtonBounds;
	}

	public Rectangle getMenuButtonBounds() {
		return menuButtonBounds;
	}

	public boolean isResumeHover() {
		return resumeHover;
	}

	public boolean isMenuHover() {
		return menuHover;
	}
}