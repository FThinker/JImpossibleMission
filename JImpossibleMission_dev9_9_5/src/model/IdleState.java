package model;

/**
 * Implements the "Idle" state for an enemy's state machine.
 * In this state, the enemy waits for a specified duration before transitioning
 * to its next action, which is determined by its behavior context.
 */
public class IdleState implements EnemyStateHandler {

	@Override
	public void enter(Enemy enemy) {
		enemy.setState(EnemyState.IDLE);
	}

	@Override
	public void update(Enemy enemy, EnemyBehavior context, long currentTime, Level levelData, Player player) {
		long idleDuration = context.getDurationForState(EnemyState.IDLE);

		if (context.getElapsed(currentTime) >= idleDuration) {
            EnemyStateHandler nextState = getNextStateFor(context);
            context.changeState(nextState, enemy, currentTime);
        }
	}
	
    /**
     * Determines the next state for the enemy based on its behavior type.
     * @param context The behavior context of the enemy.
     * @return The next state handler for the enemy.
     */
    private EnemyStateHandler getNextStateFor(EnemyBehavior context) {
        if (context instanceof StandingRobotBehavior) {
            StandingRobotBehavior specificContext = (StandingRobotBehavior) context;
            int step = specificContext.getBehaviorStep();
            if (step == 0) {
                specificContext.setBehaviorStep(1);
                return new AttackingState();
            } else { // step == 2
                specificContext.setBehaviorStep(3);
                return new TurningState();
            }
        } else if (context instanceof MovingRobotBehavior) {
            // After being idle (e.g., after seeing the player), a moving robot will attack.
            return new AttackingState();
        }
        return this; // Default: remain in idle (should not happen with timed states)
    }

	@Override
	public void exit(Enemy enemy) {
		// No specific action needed when exiting idle state.
	}
}