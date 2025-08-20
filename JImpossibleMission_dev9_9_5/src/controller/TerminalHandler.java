package controller;

import model.GameModel;
import model.GameState;
import java.awt.event.KeyEvent;

/**
 * Handles user input and logic for the in-game terminal screen.
 * This class processes keyboard inputs to execute terminal commands, such as
 * resetting lifts or disabling enemies, and provides the text to be displayed.
 */
public class TerminalHandler {

    private GameModel gameModel;
    private InputHandler inputHandler;

    /**
     * Constructs a TerminalHandler.
     *
     * @param gameModel    The main game model to interact with.
     * @param inputHandler The handler for detecting keyboard input.
     */
    public TerminalHandler(GameModel gameModel, InputHandler inputHandler) {
        this.gameModel = gameModel;
        this.inputHandler = inputHandler;
    }

    /**
     * Handles keyboard input when the terminal is open.
     * Listens for specific keys (1, 2, Escape, etc.) to trigger actions.
     */
    public void handleTerminalInput() {
        if (inputHandler.isKeyPressed(KeyEvent.VK_1)) {
            gameModel.resetAllLiftsPosition();
            AudioManager.getInstance().play("keystroke");
            inputHandler.resetKeys();
            gameModel.setGameState(GameState.PLAYING);
        } else if (inputHandler.isKeyPressed(KeyEvent.VK_2)) {
            gameModel.freezeEnemies(15); // Freeze for 15 seconds
            AudioManager.getInstance().play("keystroke");
            inputHandler.resetKeys();
            gameModel.setGameState(GameState.PLAYING);
        } else if (inputHandler.isKeyPressed(KeyEvent.VK_ESCAPE) || inputHandler.isKeyPressed(KeyEvent.VK_Q) || inputHandler.isKeyPressed(KeyEvent.VK_ENTER)) {
            AudioManager.getInstance().play("keystroke");
            inputHandler.resetKeys();
            gameModel.setGameState(GameState.PLAYING);
        }
    }

    /**
     * Generates the text to be displayed on the terminal screen.
     *
     * @return A formatted string with the terminal's content and options.
     */
    public String getTerminalText() {
        return "*** SECURITY TERMINAL " + generateTerminalAlias() + " ***\n" +
               "> SELECT FUNCTION\n\n" +
               "> 1 - RESET LIFTING PLATFORMS IN THIS ROOM.\n" +
               "> 2 - TEMPORARILY DISABLE ROBOTS IN THIS ROOM.\n\n" +
               "> ==> LOG OFF. (Press ESC, Q or Enter)";
    }
    
    /**
     * Generates a pseudo-unique alias for the current terminal based on level properties.
     * @return A string representing the terminal's ID.
     */
    private String generateTerminalAlias() {
    	// Poor man's hash function
    	int id = gameModel.getLevel().getLifts().size() * (int)gameModel.getLevel().getPlayerSpawn().getX() 
    			+ gameModel.getLevel().getPcs().size() * (int)gameModel.getLevel().getPlayerSpawn().getY();
    	String prefix = "" + gameModel.getLevel().getLevelData()[5][9].getType().name().charAt(0)
    			+ gameModel.getLevel().getLevelData()[10][3].getType().name().charAt(1);
    	
    	return prefix + "-" + id;
    }
}