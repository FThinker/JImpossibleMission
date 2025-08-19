// in controller/StatsHandler.java
package controller;

import model.GameModel;
import model.GameState;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class StatsHandler {
    private GameModel gameModel;
    private InputHandler inputHandler;
    private Rectangle backButtonBounds = new Rectangle(490, 550, 300, 50);
    private boolean backHover = false;
    
    private boolean wasAnyButtonHovered = false;

    public StatsHandler(GameModel gameModel, InputHandler inputHandler) {
        this.gameModel = gameModel;
        this.inputHandler = inputHandler;
    }

    public void handleInput() {
        backHover = backButtonBounds.contains(inputHandler.getMouseX(), inputHandler.getMouseY());
        
        boolean isCurrentlyHovered = backHover;
		
		// Riproduci il suono solo se ORA siamo in hover, ma PRIMA non lo eravamo
        if (isCurrentlyHovered && !wasAnyButtonHovered) {
            AudioManager.getInstance().play("click_2");
        }
        
        // Aggiorna la variabile di stato per il prossimo frame
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