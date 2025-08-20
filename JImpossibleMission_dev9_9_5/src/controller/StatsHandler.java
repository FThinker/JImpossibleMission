package controller;

import model.GameModel;
import model.GameState;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/**
 * Handles user input for the statistics screen.
 * Its primary responsibility is to process clicks on the "Back" button
 * to return to the main menu.
 */
public class StatsHandler {
    private GameModel gameModel;
    private InputHandler inputHandler;
    private Rectangle backButtonBounds = new Rectangle(490, 550, 300, 50);
    private boolean backHover = false;
    private boolean wasAnyButtonHovered = false;

    /**
     * Constructs a StatsHandler.
     *
     * @param gameModel    The main game model.
     * @param inputHandler The handler for detecting user input.
     */
    public StatsHandler(GameModel gameModel, InputHandler inputHandler) {
        this.gameModel = gameModel;
        this.inputHandler = inputHandler;
    }

    /**
     * Handles user input on the stats screen.
     */
    public void handleInput() {
        backHover = backButtonBounds.contains(inputHandler.getMouseX(), inputHandler.getMouseY());
        boolean isCurrentlyHovered = backHover;
		
        if (isCurrentlyHovered && !wasAnyButtonHovered) {
            AudioManager.getInstance().play("click_2");
        }
        
        wasAnyButtonHovered = isCurrentlyHovered;
        
        if (inputHandler.isMouseButtonPressed(MouseEvent.BUTTON1) && backHover) {
        	AudioManager.getInstance().play("click");
            gameModel.setGameState(GameState.HOMESCREEN);
        }
        inputHandler.resetMouse();
    }
    
    public Rectangle getBackButtonBounds() { return backButtonBounds; }
    public boolean isBackHover() { return backHover; }
}