package model;

/**
 * Defines the contract for a specific state in an enemy's state machine.
 * Each implementation of this interface will manage the logic for one particular state,
 * such as Idle, Attacking, or Moving.
 */
public interface EnemyStateHandler {
    
    /**
     * Called once when the enemy enters this state.
     * @param enemy The enemy entering the state.
     */
    void enter(Enemy enemy);
    
    /**
     * Called on every game tick while the enemy is in this state.
     * @param enemy       The enemy to update.
     * @param context     The behavior context that manages this state.
     * @param currentTime The current system time.
     * @param levelData   The current level's data.
     * @param player      The player object.
     */
    void update(Enemy enemy, EnemyBehavior context, long currentTime, Level levelData, Player player);
    
    /**
     * Called once when the enemy exits this state.
     * @param enemy The enemy exiting the state.
     */
    void exit(Enemy enemy);
}