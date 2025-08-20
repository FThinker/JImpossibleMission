package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import controller.LevelLoader;

/**
 * Represents the state of a single gameplay session.
 * This class is serializable, allowing game progress to be saved and loaded in the future.
 * It tracks lives, puzzle pieces, time, and the state of all visited levels.
 */
public class GameSession implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int lives;
    private int puzzlePiecesFound;
    
    private long startTime;
    private long finalPlaytimeMs = -1; // -1 means the timer is still running
    public static final long TIME_LIMIT_MS = 20 * 60 * 1000; // 20 minutes
    
    private GameState lastGameState = GameState.IN_ELEVATOR;
    private int currentLevelId = -1; // -1 means the player is in the elevator
    
    // A map to cache levels that have already been loaded in this session.
    private Map<Integer, Level> loadedLevels;

    /**
     * Constructs a new GameSession with default starting values.
     */
    public GameSession() {
        this.lives = 3;
        this.puzzlePiecesFound = 0;
        this.loadedLevels = new HashMap<>();
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * Stops the session timer and records the final playtime.
     */
    public void stopTimer() {
        if (finalPlaytimeMs == -1) {
            this.finalPlaytimeMs = System.currentTimeMillis() - startTime;
        }
    }

    /**
     * Gets the total elapsed time for this session.
     * @return The final playtime if the timer is stopped, otherwise the current elapsed time.
     */
    public long getElapsedTime() {
        if (finalPlaytimeMs != -1) {
            return finalPlaytimeMs;
        }
        return System.currentTimeMillis() - startTime;
    }

    /**
     * @return The amount of time remaining before the game is over, in milliseconds.
     */
    public long getTimeLeft() {
        return TIME_LIMIT_MS - getElapsedTime();
    }

    /**
     * Retrieves a level by its number.
     * If the level has been visited before in this session, it returns the cached version.
     * Otherwise, it loads the level from a file, caches it, and then returns it.
     * @param levelNumber The number of the level to retrieve.
     * @return The corresponding {@link Level} object.
     */
    public Level getLevel(int levelNumber) {
        if (loadedLevels.containsKey(levelNumber)) {
            return loadedLevels.get(levelNumber);
        } else {
            Level newLevel = LevelLoader.loadLevel("/levels/level" + levelNumber + ".txt");
            if (newLevel != null) {
                loadedLevels.put(levelNumber, newLevel);
            }
            return newLevel;
        }
    }

    /**
     * Updates the recorded location of the player within the session.
     * @param currentState The player's current GameState.
     * @param currentLevel The player's current Level, or null if in the elevator.
     */
    public void updatePlayerLocation(GameState currentState, Level currentLevel) {
        this.lastGameState = currentState;
        if (currentLevel != null) {
            for (Map.Entry<Integer, Level> entry : loadedLevels.entrySet()) {
                if (entry.getValue() == currentLevel) {
                    this.currentLevelId = entry.getKey();
                    return;
                }
            }
        } else {
            this.currentLevelId = -1;
        }
    }
    
    // --- GETTERS AND SETTERS ---
    
    public int getLives() { return lives; }
    public void loseLife() { this.lives--; }
    public void setLives(int lives) { this.lives = lives; }
    public int getPuzzlePiecesFound() { return puzzlePiecesFound; }
    public void addPuzzlePiece() { this.puzzlePiecesFound++; }
    public GameState getLastGameState() { return lastGameState; }
    public int getCurrentLevelId() { return currentLevelId; }
}