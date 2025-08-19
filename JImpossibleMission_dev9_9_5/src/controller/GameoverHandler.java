// in controller/GameoverHandler.java
package controller;

import model.GameModel;
import model.GameState;

import static model.GameConstants.SCALE;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class GameoverHandler {
    
    private GameModel gameModel;
    private InputHandler inputHandler;
    
    private Rectangle menuButtonBounds = new Rectangle((int)(245 * SCALE), (int)(225 * SCALE), (int)(150 * SCALE), (int)(25 * SCALE));
    private boolean menuHover = false;
    
    public GameoverHandler(GameModel gameModel, InputHandler inputHandler) {
        this.gameModel = gameModel;
        this.inputHandler = inputHandler;
    }
    
    public void handleInput() {
        int mx = inputHandler.getMouseX();
        int my = inputHandler.getMouseY();
        menuHover = menuButtonBounds.contains(mx, my);
        
        if (inputHandler.isMouseButtonPressed(MouseEvent.BUTTON1)) {
            if (menuHover) {
                gameModel.setGameState(GameState.HOMESCREEN);
            }
            inputHandler.resetMouse();
        }
    }
    
    // Getters per la View
    public Rectangle getMenuButtonBounds() { return menuButtonBounds; }
    public boolean isMenuHover() { return menuHover; }
}