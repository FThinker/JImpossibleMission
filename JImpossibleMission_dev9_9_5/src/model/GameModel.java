package model;

import static model.GameConstants.LOGIC_WIDTH;
import static model.GameConstants.LOGIC_HEIGHT;
import java.awt.Point;
import java.util.List;
import java.util.Observable;
import controller.AudioManager;


/**
 * The central data model for the entire game.
 * It holds the state of all major game components, including the player, elevator,
 * current level, and active game session. It acts as the "single source of truth".
 * It extends {@link Observable} and notifies its observers (typically the main view)
 * whenever the game state changes.
 */
@SuppressWarnings("deprecation")
public class GameModel extends Observable {
    private Player player;
    private Elevator elevator;
    private Level level;
    private GameState gameState;
    private UserProfile activeProfile;

    private GameSession currentGameSession; 
    private long lastScore = 0;
    private GameState stateBeforePause;
    private int currentFps = 0;
    
    
    /**
     * Constructs the main game model.
     * @param player The player entity.
     * @param elevator The elevator entity.
     * @param session The initial game session object.
     */
    public GameModel(Player player, Elevator elevator, GameSession session) {
        this.player = player;
        this.level = null;
        this.elevator = elevator;
        this.gameState = GameState.PROFILE_SELECTION;
        this.activeProfile = null;
        
        AudioManager.getInstance().loopMenuMusic("menu_theme");
    }
    
    /**
     * Starts a new game for the currently active profile.
     * This creates a new {@link GameSession} and sets the game state to IN_ELEVATOR.
     */
    public void startNewGame() {
        if (activeProfile != null) {
        	AudioManager.getInstance().stopMenuMusic();
        	
            GameSession newSession = new GameSession();
            activeProfile.startGame(newSession);
            this.currentGameSession = newSession;
            setGameState(GameState.IN_ELEVATOR);
        }
    }
    
    /**
     * Transitions the player from the elevator into a specific room (level).
     * @param lvlNumber The number of the level to enter.
     */
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
                System.err.println("Error: Could not load level " + lvlNumber);
            }
        }
    }
    
    /**
     * Transitions the player from a room back into the elevator.
     * @param direction The direction the player is exiting from.
     */
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
    
    /**
     * Adds a puzzle piece to the current session's inventory.
     */
    public void addPuzzlePiece() {
        currentGameSession.addPuzzlePiece();
        AudioManager.getInstance().play("piece_found");
        setChanged();
        notifyObservers();
    }
    
    /**
     * Decrements a life from the current session and resets player/enemy positions.
     */
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
    
    /**
     * Resets the positions of all lifts in the current level to their starting points.
     */
    public void resetAllLiftsPosition() {
    	for (LiftTile lift : level.getLifts()) {
    		lift.resetPosition();
    	}
    }
    
    /**
     * Resets the positions of all enemies in the current level to their starting points.
     */
    public void resetAllEnemiesPosition() {
    	for (Enemy enemy : level.getEnemies()) {
    		enemy.resetPosition();
    	}
    }
    
    /**
     * Freezes all enemies in the current level for a specified duration.
     * @param durationSec The duration of the freeze in seconds.
     */
    public void freezeEnemies(long durationSec) {
        if (this.level != null) {
            this.level.freezeEnemies(durationSec);
            AudioManager.getInstance().play("freeze");
            setChanged();
            notifyObservers();
        }
    }
    
// --- GETTERS AND SETTERS ---
    
    public void setActiveProfile(UserProfile profile) {
        this.activeProfile = profile;
        setChanged();
        notifyObservers();
    }

    public UserProfile getActiveProfile() { return this.activeProfile; }
    public int getPuzzlePiecesFound() { return currentGameSession.getPuzzlePiecesFound(); }
    public int getLives() { return currentGameSession.getLives(); }
    public Elevator getElevator() { return this.elevator; }
    public Player getPlayer() { return player; }
    public Level getLevel() { return level; }
    public List<LiftTile> getLifts() { return (level != null) ? level.getLifts() : java.util.Collections.emptyList(); }
    public List<PcTile> getPcTiles() { return (level != null) ? level.getPcs() : java.util.Collections.emptyList(); }
    public List<FurnitureTile> getFurnitureTiles() { return (level != null) ? level.getFurniture() : java.util.Collections.emptyList(); }
    public List<Enemy> getEnemies(){ return (level != null) ? level.getEnemies() : java.util.Collections.emptyList(); }
    public GameState getGameState() { return gameState; }
    public GameSession getCurrentGameSession() { return this.currentGameSession; }
    public GameState getStateBeforePause() { return this.stateBeforePause; }
    public int getCurrentFps() { return this.currentFps; }
    public long getLastScore() { return this.lastScore; }
    
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        setChanged();
        notifyObservers();
    }
    
    public void setStateBeforePause(GameState state) { this.stateBeforePause = state; }
    public void setCurrentFps(int fps) { this.currentFps = fps; }
    public void setLastScore(long score) { this.lastScore = score; }
}