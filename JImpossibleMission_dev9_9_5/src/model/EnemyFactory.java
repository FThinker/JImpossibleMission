// EnemyFactory.java (Nuovo file nel package model)
package model;

import java.awt.Point;

public class EnemyFactory {

	// Metodo statico per creare un nemico
	public static Enemy createEnemy(EnemyType type, int x, int y) {
        return switch(type){
        case STANDING_ROBOT -> new StandingRobot(new Point(x, y), 32 * 2, 32 * 2);
        case MOVING_ROBOT -> new MovingRobot(new Point(x, y), 32 * 2, 32 * 2);
        default -> throw new IllegalArgumentException("Unknown enemy type: " + type);
        };
    }
}