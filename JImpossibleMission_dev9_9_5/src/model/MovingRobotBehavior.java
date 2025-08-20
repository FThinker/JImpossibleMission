package model;

/**
 * Implements the behavior context for a {@link MovingRobot}.
 * This class manages the state machine for the moving robot, holding the durations for each state
 * (idle, turning, attacking) and orchestrating transitions between them.
 *
 * @see EnemyBehavior
 * @see EnemyStateHandler
 */
public class MovingRobotBehavior implements EnemyBehavior {

    private EnemyStateHandler currentState;
    private long stateStartTime;
    private int behaviorStep = 0;
    
    private long idleDuration, turnDuration, attackDuration;

    /**
     * Constructs a new MovingRobotBehavior, initializing its state machine to Idle
     * and setting the durations for its actions.
     */
    public MovingRobotBehavior() {
        currentState = new IdleState();
        stateStartTime = System.currentTimeMillis();
        idleDuration = 500;
        turnDuration = 500;
        attackDuration = 750;
    }

    @Override
    public void update(Enemy enemy, long currentTime, Level levelData, Player player) {
        currentState.update(enemy, this, currentTime, levelData, player);
    }

    @Override
    public long getDurationForState(EnemyState state) {
        switch (state) {
            case IDLE: return idleDuration;
            case TURNING: return turnDuration;
            case ATTACKING: return attackDuration;
            default: return 1000;
        }
    }

    @Override
    public long getStateStartTime() {
        return this.stateStartTime;
    }

	@Override
	public void changeState(EnemyStateHandler newState, Enemy enemy, long currentTime) {
        currentState.exit(enemy);
        currentState = newState;
        currentState.enter(enemy);
        stateStartTime = currentTime;
    }

    @Override
    public long getElapsed(long currentTime) {
        return currentTime - stateStartTime;
    }
    
    @Override
    public void resetBehavior(Enemy enemy) {
    	changeState(new IdleState(), enemy, System.currentTimeMillis());
    }
    
    public int getBehaviorStep() {
        return behaviorStep;
    }

    public void setBehaviorStep(int step) {
        this.behaviorStep = step;
    }
}