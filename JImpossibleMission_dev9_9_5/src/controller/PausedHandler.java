package controller;

import model.GameModel;
import model.GameState;
import static model.GameConstants.LOGIC_HEIGHT;
import static model.GameConstants.LOGIC_WIDTH;
import static model.GameConstants.SCALE;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Handles user input and logic for the pause menu.
 * This class processes interactions to either resume the game or exit to the main menu.
 */
public class PausedHandler {

	private GameModel gameModel;
	private InputHandler inputHandler;

	// Clickable areas for buttons
	private Rectangle resumeButtonBounds = new Rectangle((int) (245 * SCALE), (int) (150 * SCALE), (int) (150 * SCALE), (int) (25 * SCALE));
	private Rectangle menuButtonBounds = new Rectangle((int) (245 * SCALE), (int) (185 * SCALE), (int) (150 * SCALE), (int) (25 * SCALE));

	// Hover states for visual feedback
	private boolean resumeHover = false;
	private boolean menuHover = false;
	private boolean wasAnyButtonHovered = false;

	/**
     * Constructs a PausedHandler.
     *
     * @param gameModel    The main game model.
     * @param inputHandler The handler for detecting user input.
     */
	public PausedHandler(GameModel gameModel, InputHandler inputHandler) {
		this.gameModel = gameModel;
		this.inputHandler = inputHandler;
	}

	/**
	 * Handles user input on the pause screen.
	 * Checks for mouse clicks on the "Resume" and "Return to Menu" buttons,
	 * as well as the 'Escape' key to unpause.
	 */
	public void handleInput() {
		int mx = inputHandler.getMouseX();
		int my = inputHandler.getMouseY();
		resumeHover = resumeButtonBounds.contains(mx, my);
		menuHover = menuButtonBounds.contains(mx, my);

		boolean isCurrentlyHovered = resumeHover || menuHover;

        if (isCurrentlyHovered && !wasAnyButtonHovered) {
			AudioManager.getInstance().play("click_2");
		}
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

		// Also allow unpausing with the Escape key
		if (inputHandler.isKeyPressed(KeyEvent.VK_ESCAPE)) {
			AudioManager.getInstance().play("unpause");
			gameModel.setGameState(gameModel.getStateBeforePause());
		}

		inputHandler.resetMouse();
		inputHandler.resetKeys();
	}

	/**
	 * Ends the current game session and returns to the main menu.
	 * The game is considered lost, and the user's profile is saved.
	 */
	private void endGameAndReturnToMenu() {
		if (gameModel.getActiveProfile() != null) {
			// Consider quitting as a loss with 0 points
			gameModel.getActiveProfile().endGame(false, 0, gameModel.getCurrentGameSession().getElapsedTime(), 0);
			ProfileManager.saveProfile(gameModel.getActiveProfile());
		}
		gameModel.getPlayer().teleport((int) (LOGIC_WIDTH / 2.24), (int) (LOGIC_HEIGHT / 4.1));
		gameModel.setGameState(GameState.HOMESCREEN);
		AudioManager.getInstance().loopMenuMusic("menu_theme");
	}

	// --- GETTERS for the View ---
	public Rectangle getResumeButtonBounds() { return resumeButtonBounds; }
	public Rectangle getMenuButtonBounds() { return menuButtonBounds; }
	public boolean isResumeHover() { return resumeHover; }
	public boolean isMenuHover() { return menuHover; }
}