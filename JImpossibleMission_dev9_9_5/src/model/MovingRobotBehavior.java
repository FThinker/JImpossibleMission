package model;

public class MovingRobotBehavior implements EnemyBehavior {

    private EnemyStateHandler currentState;
    private long stateStartTime;
    private int behaviorStep = 0;
    
    private long idleDuration, turnDuration, attackDuration;


    public MovingRobotBehavior() {
        currentState = new IdleState(); // Inizia in Idle
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

	public void changeState(EnemyStateHandler newState, Enemy enemy, long currentTime) {
        currentState.exit(enemy);
        currentState = newState;
        currentState.enter(enemy);
        stateStartTime = currentTime;
    }

    public long getElapsed(long currentTime) {
        return currentTime - stateStartTime;
    }
    
    public int getBehaviorStep() {
        return behaviorStep;
    }

    public void setBehaviorStep(int step) {
        this.behaviorStep = step;
    }
    
    @Override
    public void resetBehavior(Enemy enemy) {
    	changeState(new IdleState(), enemy, System.currentTimeMillis());
    }
}
