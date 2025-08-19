package model;

import static model.GameConstants.LOGIC_HEIGHT;
import static model.GameConstants.LOGIC_WIDTH;

import java.awt.Point;
import java.util.List;
import java.util.Observable; // Sarà osservato dalla View

import controller.AudioManager;

@SuppressWarnings("deprecation")
public class GameModel extends Observable {
    private Player player;
    private Elevator elevator;
    private Level level;
    private GameState gameState;
    private UserProfile activeProfile;
    
    // Variabili per il pop-up a nuvoletta
    private String popupMessage = null; // Il testo del messaggio da mostrare
    private float popupDisplayTime = 0; // Tempo rimanente di visualizzazione
    private final float POPUP_DURATION = 2.0f; // Durata del pop-up in secondi
    private FurnitureTile currentSearchingFurniture = null; // Riferimento al mobile in cui si sta cercando

    // Riferimento alla sessione di gioco corrente
    private GameSession currentGameSession; 

    private long lastScore = 0;
    
    private GameState stateBeforePause;
    
    private int currentFps = 0;
    
    public GameModel(Player player, Elevator elevator, GameSession session) {
        this.player = player;
        this.level = null;
        this.elevator = elevator;
        this.gameState = GameState.PROFILE_SELECTION; // Stato iniziale
        this.activeProfile = null;
        
        AudioManager.getInstance().loopMenuMusic("menu_theme");
    }
    
    public void setActiveProfile(UserProfile profile) {
        this.activeProfile = profile;
        setChanged();
        notifyObservers();
    }

    public UserProfile getActiveProfile() {
        return this.activeProfile;
    }

    public void startNewGame() {
        if (activeProfile != null) {
        	AudioManager.getInstance().stopMenuMusic();
        	
            GameSession newSession = new GameSession();
            activeProfile.startGame(newSession);
            this.currentGameSession = newSession;
            setGameState(GameState.IN_ELEVATOR); // Si parte dall'ascensore
        }
    }
    
    // Per entrare in una stanza dall'ascensore
    public void enterRoom(int lvlNumber) {
    	if (gameState == GameState.IN_ELEVATOR) {
            this.level = currentGameSession.getLevel(lvlNumber);
            
            if (this.level != null) {
                player.teleport(level.getPlayerSpawn().x, level.getPlayerSpawn().y);
                player.setInitialSpawn(new Point(level.getPlayerSpawn().x, level.getPlayerSpawn().y));
                setGameState(GameState.PLAYING);
                AudioManager.getInstance().stopAllSounds();
                AudioManager.getInstance().play("woosh");
                this.currentGameSession.updatePlayerLocation(GameState.PLAYING, this.level);
            } else {
                System.err.println("Errore: Impossibile caricare il livello " + lvlNumber);
            }
        }
    }
    
    public void exitRoom(Directions direction) {
        if (gameState == GameState.PLAYING) {
        	this.currentGameSession.updatePlayerLocation(GameState.IN_ELEVATOR, null);
        	
        	setGameState(GameState.IN_ELEVATOR);
        	AudioManager.getInstance().stopAllSounds();
        	AudioManager.getInstance().play("woosh");
        	
            if(direction == Directions.RIGHT)
            	player.teleport(1, (int)(LOGIC_HEIGHT / 4.1));
            else
            	player.teleport(LOGIC_WIDTH - 2 - (int)player.getHitbox().getWidth() - 10*2, (int)(LOGIC_HEIGHT / 4.1));
        }
    }
    
 // MODIFICA: I metodi ora delegano alla GameSession
    public int getPuzzlePiecesFound() {
        return currentGameSession.getPuzzlePiecesFound();
    }
    
    public void addPuzzlePiece() {
        currentGameSession.addPuzzlePiece();
        AudioManager.getInstance().play("piece_found");
        setChanged();
        notifyObservers();
    }
    
    public int getLives() {
        return currentGameSession.getLives();
    }
    
    public void loseLife() {
        currentGameSession.loseLife();
        resetAllEnemiesPosition();
        resetAllLiftsPosition();
        AudioManager.getInstance().stopAllSounds();
        AudioManager.getInstance().play("death");
        player.teleport(level.getPlayerSpawn().x, level.getPlayerSpawn().y);
        setChanged();
        notifyObservers();
    }
    
    public Elevator getElevator() {
    	return this.elevator;
    }

    public Player getPlayer() {
        return player;
    }

    public Level getLevel() {
        return level;
    }

    public List<LiftTile> getLifts() {
        if (level != null) return level.getLifts();
        return java.util.Collections.emptyList(); // Ritorna una lista vuota se non c'è un livello
    }
    
    public List<PcTile> getPcTiles() {
    	if (level != null) return level.getPcs();
        return java.util.Collections.emptyList();
    }
    
    public List<FurnitureTile> getFurnitureTiles() {
		if (level != null) return level.getFurniture();
        return java.util.Collections.emptyList();
	}
    
    public List<Enemy> getEnemies(){
    	if (level != null) return level.getEnemies();
        return java.util.Collections.emptyList();
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        setChanged();
        notifyObservers(); // Notifica la View del cambio di stato
    }
    
    // Metodo per resettare la posizione di tutti i lift
    public void resetAllLiftsPosition() {
    	for (LiftTile lift : level.getLifts()) {
    		lift.resetPosition();
    	}
    }
    
    public void resetAllEnemiesPosition() {
    	for (Enemy enemy : level.getEnemies()) {
    		enemy.resetPosition();
    	}
    }
    
    public void freezeEnemies(long durationSec) {
        if (this.level != null) {
            this.level.freezeEnemies(durationSec);
            AudioManager.getInstance().play("freeze");
            setChanged();
            notifyObservers();
        }
    }
    
 // NUOVI GETTER E SETTER per la logica dei pop-up
    public String getPopupMessage() {
        return popupMessage;
    }
    
    public void setPopupMessage(String message) {
        this.popupMessage = message;
        this.popupDisplayTime = POPUP_DURATION; // Avvia il timer del pop-up
        setChanged();
        notifyObservers();
    }
    
    public void updatePopupTimer(float delta) {
        if (popupDisplayTime > 0) {
            popupDisplayTime -= delta;
            if (popupDisplayTime <= 0) {
                popupMessage = null; // Rimuovi il messaggio
                setChanged();
                notifyObservers();
            }
        }
    }
    
    public FurnitureTile getCurrentSearchingFurniture() {
        return currentSearchingFurniture;
    }
    
    public void setCurrentSearchingFurniture(FurnitureTile furniture) {
        this.currentSearchingFurniture = furniture;
        setChanged();
        notifyObservers();
    }

	public GameSession getCurrentGameSession() {
		return this.currentGameSession;
	}
	
    public void setStateBeforePause(GameState state) {
        this.stateBeforePause = state;
    }

    public GameState getStateBeforePause() {
        return this.stateBeforePause;
    }
    
    public void setCurrentFps(int fps) {
        this.currentFps = fps;
    }

    public int getCurrentFps() {
        return this.currentFps;
    }
    
    public void setLastScore(long score) { this.lastScore = score; }
    public long getLastScore() { return this.lastScore; }
}