package model;

import controller.AudioManager;

/**
 * Implements the "Attacking" state for an enemy's state machine.
 * In this state, the enemy performs its attack animation and logic for a fixed duration.
 *
 * @see EnemyStateHandler
 * @see EnemyBehavior
 */
public class AttackingState implements EnemyStateHandler {

    /**
     * Called when the enemy enters the Attacking state.
     * Sets the enemy's state enum and starts the attack sound loop.
     * @param enemy The enemy entering this state.
     */
    @Override
    public void enter(Enemy enemy) {
        enemy.setState(EnemyState.ATTACKING);
        AudioManager.getInstance().loop("energy_beam");
    }

    /**
     * Updates the enemy's logic while in the Attacking state.
     * After the attack duration has passed, it transitions the enemy to the next appropriate state
     * as determined by its behavior context.
     * @param enemy The enemy to update.
     * @param context The behavior context managing the state transitions.
     * @param currentTime The current system time in milliseconds.
     * @param levelData The current level's data.
     * @param player The player object.
     */
    @Override
    public void update(Enemy enemy, EnemyBehavior context, long currentTime, Level levelData, Player player) {
        long attackDuration = context.getDurationForState(EnemyState.ATTACKING);

        if (context.getElapsed(currentTime) >= attackDuration) {
            // The attack is finished. The context will decide the next state.
            EnemyStateHandler nextState = getNextStateFor(context);
            context.changeState(nextState, enemy, currentTime);
        }
    }
    
    /**
     * Determines the next state for the enemy based on its behavior type.
     * For example, a standing robot might become idle, while a moving robot might resume moving.
     * @param context The behavior context of the enemy.
     * @return The next state handler for the enemy.
     */
    private EnemyStateHandler getNextStateFor(EnemyBehavior context) {
        if (context instanceof StandingRobotBehavior) {
            // After attacking, a StandingRobot goes back to IDLE to prepare for turning.
            StandingRobotBehavior specificContext = (StandingRobotBehavior) context;
            specificContext.setBehaviorStep(2); // Update internal sequence
            return new IdleState();
        } 
        else if (context instanceof MovingRobotBehavior) {
            // After attacking, a MovingRobot resumes its movement.
            return new MovingState();
        }
        
        // Safety fallback: if the behavior is unknown, return to IDLE.
        return new IdleState();
    }


    /**
     * Called when the enemy exits the Attacking state.
     * Stops the attack sound loop.
     * @param enemy The enemy exiting this state.
     */
    @Override
    public void exit(Enemy enemy) {
    	AudioManager.getInstance().stop("energy_beam");
    }
}