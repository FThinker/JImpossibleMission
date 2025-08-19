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

    public StatsHandler(GameModel gameModel, InputHandler inputHandler) {
        this.gameModel = gameModel;
        this.inputHandler = inputHandler;
    }

    public void handleInput() {
        backHover = backButtonBounds.contains(inputHandler.getMouseX(), inputHandler.getMouseY());
        if (inputHandler.isMouseButtonPressed(MouseEvent.BUTTON1) && backHover) {
            gameModel.setGameState(GameState.HOMESCREEN);
        }
        inputHandler.resetMouse();
    }
    
    public Rectangle getBackButtonBounds() { return backButtonBounds; }
    public boolean isBackHover() { return backHover; }
}