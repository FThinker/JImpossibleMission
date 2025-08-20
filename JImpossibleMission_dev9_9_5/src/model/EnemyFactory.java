package model;

import java.awt.Point;

/**
 * A factory class for creating different types of enemies.
 * This class encapsulates the instantiation logic for all concrete {@link Enemy} subclasses.
 */
public class EnemyFactory {

	/**
     * Creates an enemy of a specific type at the given coordinates.
     *
     * @param type The {@link EnemyType} to create.
     * @param x    The initial x-coordinate.
     * @param y    The initial y-coordinate.
     * @return A new instance of the specified enemy.
     * @throws IllegalArgumentException if the enemy type is unknown.
     */
	public static Enemy createEnemy(EnemyType type, int x, int y) {
        return switch(type){
        case STANDING_ROBOT -> new StandingRobot(new Point(x, y), 32 * 2, 32 * 2);
        case MOVING_ROBOT -> new MovingRobot(new Point(x, y), 32 * 2, 32 * 2);
        default -> throw new IllegalArgumentException("Unknown enemy type: " + type);
        };
    }
}