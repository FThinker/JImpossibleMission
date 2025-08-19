package controller;

import model.GameModel; // Necessario per accedere al GameModel
import model.GameState;

import java.awt.event.KeyEvent; // Per gestire l'input da tastiera

public class TerminalHandler {

    private GameModel gameModel;
    private InputHandler inputHandler; // Per leggere l'input

    public TerminalHandler(GameModel gameModel, InputHandler inputHandler) {
        this.gameModel = gameModel;
        this.inputHandler = inputHandler;
    }

    public void handleTerminalInput() {
        if (inputHandler.isKeyPressed(KeyEvent.VK_1)) {
            System.out.println("Opzione 1 selezionata: Resetting Lifts");
            AudioManager.getInstance().play("keystroke");
            gameModel.resetAllLiftsPosition();
            inputHandler.resetKeys(); // Resetta i tasti per evitare input multipli
            gameModel.setGameState(GameState.PLAYING); // Torna al gioco normale
        } else if (inputHandler.isKeyPressed(KeyEvent.VK_2)) {
            System.out.println("Opzione 2 selezionata: Freezing Enemies");
            AudioManager.getInstance().play("keystroke");
            gameModel.freezeEnemies(15); // Congela per 15 secondi
            inputHandler.resetKeys();
            gameModel.setGameState(GameState.PLAYING); // Torna al gioco normale
        } else if (inputHandler.isKeyPressed(KeyEvent.VK_ESCAPE) || inputHandler.isKeyPressed(KeyEvent.VK_Q) || inputHandler.isKeyPressed(KeyEvent.VK_ENTER)) {
            // Usa ESC o INVIO per fare il "Log Off"
            System.out.println("Log Off dal terminale.");
            AudioManager.getInstance().play("keystroke");
            inputHandler.resetKeys();
            gameModel.setGameState(GameState.PLAYING); // Torna al gioco normale
        }
    }

    // Metodi per ottenere le informazioni da disegnare nel terminale (la View li userà)
    public String getTerminalText() {
        // Qui puoi costruire il testo da mostrare nel terminale
        return "*** SECURITY TERMINAL " + generateTerminalAlias() + " ***\n" +
               "> SELECT FUNCTION\n\n" +
               "> 1 - RESET LIFTING PLATFORMS IN THIS ROOM.\n" +
               "> 2 - TEMPORARILY DISABLE ROBOTS IN THIS ROOM.\n\n" +
               "> ==> LOG OFF. (Press ESC, Q or Enter)";
    }
    
    private String generateTerminalAlias() {
    	// poor man's hash function
    	int id = gameModel.getLevel().getLifts().size() * (int)gameModel.getLevel().getPlayerSpawn().getX() 
    			+ gameModel.getLevel().getPcs().size() * (int)gameModel.getLevel().getPlayerSpawn().getY();
    	String prefix = "" + gameModel.getLevel().getLevelData()[5][9].getType().name().charAt(0)
    			+ gameModel.getLevel().getLevelData()[10][3].getType().name().charAt(1);
    	
    	return prefix + "-" + id;
    }
}