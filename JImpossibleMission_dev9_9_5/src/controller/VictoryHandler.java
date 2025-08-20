package controller;

import model.GameModel;
import model.GameState;
import static model.GameConstants.SCALE;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/**
 * Handles user input and logic for the Victory screen.
 * This class processes mouse interactions to allow the user to return to the main menu
 * after successfully completing the game.
 */
public class VictoryHandler {
	private GameModel gameModel;
	private InputHandler inputHandler;
	private Rectangle menuButtonBounds = new Rectangle((int) (245 * SCALE), (int) (225 * SCALE), (int) (150 * SCALE), (int) (25 * SCALE));
	private boolean menuHover = false;
	private boolean wasAnyButtonHovered = false;

	/**
     * Constructs a VictoryHandler.
     *
     * @param gameModel    The main game model.
     * @param inputHandler The handler for detecting user input.
     */
	public VictoryHandler(GameModel gameModel, InputHandler inputHandler) {
		this.gameModel = gameModel;
		this.inputHandler = inputHandler;
	}

	/**
	 * Handles user input on the victory screen.
	 * Checks for mouse hovering and clicks on the "Return to Menu" button.
	 */
	public void handleInput() {
		menuHover = menuButtonBounds.contains(inputHandler.getMouseX(), inputHandler.getMouseY());
		boolean isCurrentlyHovered = menuHover;

        if (isCurrentlyHovered && !wasAnyButtonHovered) {
			AudioManager.getInstance().play("click_2");
		}
		wasAnyButtonHovered = isCurrentlyHovered;

		if (inputHandler.isMouseButtonPressed(MouseEvent.BUTTON1) && menuHover) {
			AudioManager.getInstance().play("click");
			gameModel.setGameState(GameState.HOMESCREEN);
			AudioManager.getInstance().loopMenuMusic("menu_theme");
		}
		inputHandler.resetMouse();
	}

	public Rectangle getMenuButtonBounds() {
		return menuButtonBounds;
	}

	public boolean isMenuHover() {
		return menuHover;
	}
}