package model;

/**
 * Implements the "Turning" state for an enemy's state machine.
 * In this state, the enemy performs a turn animation over a fixed duration
 * and then transitions to the next appropriate state based on its behavior.
 */
public class TurningState implements EnemyStateHandler {

	@Override
	public void enter(Enemy enemy) {
		enemy.setState(EnemyState.TURNING);
		enemy.turn();
	}

	@Override
	public void update(Enemy enemy, EnemyBehavior context, long currentTime, Level levelData, Player player) {
		long turnDuration = context.getDurationForState(EnemyState.TURNING);

		if (context.getElapsed(currentTime) >= turnDuration) {
            // The turn action is complete. Decide what to do next.
            EnemyStateHandler nextState = getNextStateFor(context);
            context.changeState(nextState, enemy, currentTime);
        }
	}
	
	/**
     * Determines the next state based on the enemy's behavior context.
     * @param context The behavior context of the enemy.
     * @return The next state handler for the enemy.
     */
    private EnemyStateHandler getNextStateFor(EnemyBehavior context) {
        if (context instanceof StandingRobotBehavior) {
            // After turning, a StandingRobot goes back to Idle to restart its cycle.
            StandingRobotBehavior specificContext = (StandingRobotBehavior) context;
            specificContext.setBehaviorStep(0); // Reset the behavior sequence
            return new IdleState();
        } 
        else if (context instanceof MovingRobotBehavior) {
            // After turning, a MovingRobot starts walking in the new direction.
            return new MovingState();
        }
        
        // Fallback
        return new IdleState();
    }

	@Override
	public void exit(Enemy enemy) {
		// No specific action needed when exiting the turning state.
	}
}