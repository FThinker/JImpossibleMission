// in controller/VictoryHandler.java
package controller;

import model.GameModel;
import model.GameState;

import static model.GameConstants.SCALE;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class VictoryHandler {
    private GameModel gameModel;
    private InputHandler inputHandler;
    private Rectangle menuButtonBounds = new Rectangle((int)(245 * SCALE), (int)(225 * SCALE), (int)(150 * SCALE), (int)(25 * SCALE));
    private boolean menuHover = false;

    public VictoryHandler(GameModel gameModel, InputHandler inputHandler) {
        this.gameModel = gameModel;
        this.inputHandler = inputHandler;
    }

    public void handleInput() {
        menuHover = menuButtonBounds.contains(inputHandler.getMouseX(), inputHandler.getMouseY());
        if (inputHandler.isMouseButtonPressed(MouseEvent.BUTTON1) && menuHover) {
            gameModel.setGameState(GameState.HOMESCREEN);
        }
        inputHandler.resetMouse();
    }

    public Rectangle getMenuButtonBounds() { return menuButtonBounds; }
    public boolean isMenuHover() { return menuHover; }
}