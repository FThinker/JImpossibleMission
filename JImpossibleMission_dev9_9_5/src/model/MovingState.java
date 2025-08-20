package model;

import static model.GameConstants.*;
import controller.AudioManager;

/**
 * Implements the "Moving" state for an enemy's state machine.
 * In this state, the enemy moves forward until it detects a reason to change state,
 * such as seeing the player, reaching an edge, or hitting a wall.
 */
public class MovingState implements EnemyStateHandler {

	@Override
	public void enter(Enemy enemy) {
		enemy.setState(EnemyState.MOVING);
		AudioManager.getInstance().loop("robot_bleep");
	}

	@Override
	public void update(Enemy enemy, EnemyBehavior context, long currentTime, Level levelData, Player player) {
		// --- 1. Check for events that trigger a state transition ---

		// If the enemy's attack box intersects with the player, stop to attack.
		if (enemy.getAttackBox().intersects(player.getHitbox()) || player.getHitbox().intersects(enemy.getAttackBox())) {
			context.changeState(new IdleState(), enemy, currentTime);
			return;
		}

		// If the enemy is at the edge of a platform or facing a wall, turn around.
		if (isAtEdge(enemy, levelData) || isFacingWall(enemy, levelData)) {
			context.changeState(new TurningState(), enemy, currentTime);
			return;
		}

		// --- 2. Perform the state's action ---

		// If no transition occurred, continue moving.
		enemy.move();
	}
    
	/**
	 * Checks if the enemy is at the edge of a platform.
	 * @return True if the enemy is about to fall, otherwise false.
	 */
	private boolean isAtEdge(Enemy enemy, Level levelData) {
		if (enemy.isFacingRight()) {
			int nextPosY = (int) (enemy.getHitbox().getMaxY()  / TILES_DEFAULT_SIZE);
			int nextPosX = (int) ((enemy.getHitbox().getMaxX() + enemy.getSpeed())  / TILES_DEFAULT_SIZE);
			Tile edgeTile = levelData.getLevelData()[nextPosY][nextPosX];
			if(nextPosX == 0 || nextPosX == TILES_IN_WIDTH - 1)
				return true;
			if (enemy.getHitbox().getMaxX() + enemy.getSpeed() >= edgeTile.getHitbox().getX() && (!edgeTile.isSolid() || edgeTile.getType() == TileTypes.LIFT))
				return true;
		} else {
			int nextPosY = (int) (enemy.getHitbox().getMaxY()  / TILES_DEFAULT_SIZE);
			int nextPosX = (int) ((enemy.getHitbox().getX() - enemy.getSpeed()) / TILES_DEFAULT_SIZE);
			Tile edgeTile = levelData.getLevelData()[nextPosY][nextPosX];
			if(nextPosX == 0 || nextPosX == TILES_IN_WIDTH - 1)
				return true;
			if (enemy.getHitbox().getX() - enemy.getSpeed() <= edgeTile.getHitbox().getMaxX() && (!edgeTile.isSolid() || edgeTile.getType() == TileTypes.LIFT))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if the enemy is facing a wall.
	 * @return True if a wall is directly in front of the enemy, otherwise false.
	 */
	private boolean isFacingWall(Enemy enemy, Level levelData) {
		if (enemy.isFacingRight()) {
			int nextPosY = (int) (enemy.getHitbox().getY()  / TILES_DEFAULT_SIZE);
			int nextPosX = (int) ((enemy.getHitbox().getMaxX() + enemy.getSpeed())  / TILES_DEFAULT_SIZE);
			Tile edgeTile = levelData.getLevelData()[nextPosY][nextPosX];
			if (enemy.getHitbox().getMaxX() + enemy.getSpeed() >= edgeTile.getHitbox().getX() && edgeTile.getType() == TileTypes.WALL)
				return true;
		} else {
			int nextPosY = (int) (enemy.getHitbox().getY()  / TILES_DEFAULT_SIZE);
			int nextPosX = (int) ((enemy.getHitbox().getX() - enemy.getSpeed()) / TILES_DEFAULT_SIZE);
			Tile edgeTile = levelData.getLevelData()[nextPosY][nextPosX];
			if (enemy.getHitbox().getX() - enemy.getSpeed() <= edgeTile.getHitbox().getMaxX() && edgeTile.getType() == TileTypes.WALL)
				return true;
		}
		return false;
	}

	@Override
	public void exit(Enemy enemy) {
		AudioManager.getInstance().stop("robot_bleep");
	}
}