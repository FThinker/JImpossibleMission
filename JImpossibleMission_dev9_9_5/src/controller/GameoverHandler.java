package controller;

import model.GameModel;
import model.GameState;
import static model.GameConstants.SCALE;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/**
 * Handles user input and logic for the Game Over screen.
 * This class processes mouse interactions to allow the user to return to the main menu.
 */
public class GameoverHandler {
    
    private GameModel gameModel;
    private InputHandler inputHandler;
    
    private Rectangle menuButtonBounds = new Rectangle((int)(245 * SCALE), (int)(225 * SCALE), (int)(150 * SCALE), (int)(25 * SCALE));
    private boolean menuHover = false;
    private boolean wasAnyButtonHovered = false;
    
    /**
     * Constructs a GameoverHandler.
     *
     * @param gameModel    The main game model, used to change the game state.
     * @param inputHandler The input handler for detecting mouse events.
     */
    public GameoverHandler(GameModel gameModel, InputHandler inputHandler) {
        this.gameModel = gameModel;
        this.inputHandler = inputHandler;
    }
    
    /**
     * Handles user input on the game over screen.
     * Checks for mouse hovering and clicks on the "Return to Menu" button.
     */
    public void handleInput() {
        int mx = inputHandler.getMouseX();
        int my = inputHandler.getMouseY();
        menuHover = menuButtonBounds.contains(mx, my);
        
        boolean isCurrentlyHovered = menuHover;
		
        // Play a hover sound only when the mouse enters the button area
        if (isCurrentlyHovered && !wasAnyButtonHovered) {
            AudioManager.getInstance().play("click_2");
        }
        
        wasAnyButtonHovered = isCurrentlyHovered;
        
        if (inputHandler.isMouseButtonPressed(MouseEvent.BUTTON1)) {
            if (menuHover) {
            	AudioManager.getInstance().play("click");
                gameModel.setGameState(GameState.HOMESCREEN);
                AudioManager.getInstance().loopMenuMusic("menu_theme");
            }
            inputHandler.resetMouse();
        }
    }
    
    /**
     * @return The bounding box of the main menu button.
     */
    public Rectangle getMenuButtonBounds() { return menuButtonBounds; }
    
    /**
     * @return True if the mouse is currently hovering over the main menu button, false otherwise.
     */
    public boolean isMenuHover() { return menuHover; }
}