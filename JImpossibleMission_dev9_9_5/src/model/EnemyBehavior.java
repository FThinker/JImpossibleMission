package model;

/**
 * Defines the contract for an enemy's behavior strategy.
 * This interface is part of a State design pattern, where the behavior class acts as the "Context".
 * It orchestrates state transitions and holds shared data for the different states.
 */
public interface EnemyBehavior {
    
    /**
     * Updates the enemy's behavior logic.
     * @param enemy       The enemy entity.
     * @param currentTime The current system time.
     * @param levelData   The current level's data.
     * @param player      The player object.
     */
    void update(Enemy enemy, long currentTime, Level levelData, Player player);
    
    /**
     * @return The timestamp when the current state was entered.
     */
    long getStateStartTime();
    
    /**
     * Gets the configured duration for a specific state.
     * @param state The state to query.
     * @return The duration in milliseconds.
     */
    long getDurationForState(EnemyState state);
    
    /**
     * Changes the enemy's current state to a new state.
     * @param newState    The new state handler.
     * @param enemy       The enemy entity.
     * @param currentTime The current system time.
     */
    void changeState(EnemyStateHandler newState, Enemy enemy, long currentTime);
    
    /**
     * Calculates the time elapsed since the current state was entered.
     * @param currentTime The current system time.
     * @return The elapsed time in milliseconds.
     */
    long getElapsed(long currentTime);
    
	/**
     * Resets the behavior to its initial state.
     * @param enemy The enemy entity.
     */
	void resetBehavior(Enemy enemy);
}