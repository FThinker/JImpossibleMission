package model;

public interface EnemyBehavior {
    void update(Enemy enemy, long currentTime, Level levelData, Player player);
    long getStateStartTime();
    long getDurationForState(EnemyState state);
    
    // Metodi necessari agli stati per funzionare
    void changeState(EnemyStateHandler newState, Enemy enemy, long currentTime);
    long getElapsed(long currentTime);
	void resetBehavior(Enemy enemy);
}

