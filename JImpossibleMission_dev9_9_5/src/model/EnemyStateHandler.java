package model;

public interface EnemyStateHandler {
    void enter(Enemy enemy);
    void update(Enemy enemy, EnemyBehavior context, long currentTime, Level levelData, Player player);
    void exit(Enemy enemy);
}