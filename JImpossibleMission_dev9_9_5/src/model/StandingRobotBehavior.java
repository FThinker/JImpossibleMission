package model;

/**
 * Implements the behavior context for a {@link StandingRobot}.
 * This class manages the state machine for the stationary robot, defining a fixed cycle
 * of idling, attacking, idling again, and then turning. A `behaviorStep` variable
 * is used to track the current position in this sequence.
 */
public class StandingRobotBehavior implements EnemyBehavior {

    private EnemyStateHandler currentState;
    private long stateStartTime;
    private int behaviorStep = 0;
    
    private long idleDuration, turnDuration, attackDuration;

    public StandingRobotBehavior() {
        currentState = new IdleState();
        stateStartTime = System.currentTimeMillis();
        idleDuration = 1000;
        turnDuration = 500;
        attackDuration = 750;
    }

    @Override
    public void update(Enemy enemy, long currentTime, Level levelData, Player player) {
        currentState.update(enemy, this, currentTime, levelData, player);
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