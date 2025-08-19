// WalkingState.java (Nuovo file)
package model;

import static model.GameConstants.*;

public class MovingState implements EnemyStateHandler {

	@Override
	public void enter(Enemy enemy) {
		enemy.setState(EnemyState.MOVING); // Dovrai aggiungere WALKING a EnemyState
	}

	@Override
	public void update(Enemy enemy, EnemyBehavior context, long currentTime, Level levelData, Player player) {
		// --- 1. Controllo degli eventi che causano una transizione di stato ---

		// Se l'attack box del nemico interseca l'hitbox del player...
		if (enemy.getAttackBox().intersects(player.getHitbox()) || player.getHitbox().intersects(enemy.getAttackBox())) {
			// ...si ferma in IDLE (per poi attaccare).
			context.changeState(new IdleState(), enemy, currentTime);
			return; // Usciamo subito per evitare di eseguire il movimento
		}

		// Se il nemico è al bordo di una piattaforma...
		if (isAtEdge(enemy, levelData) || isFacingWall(enemy, levelData)) {
			// ...si gira.
			context.changeState(new TurningState(), enemy, currentTime);
			return; // Usciamo subito
		}

		// --- 2. Azione dello stato ---

		// Se nessuna transizione è avvenuta, il nemico continua a muoversi.
		// (Dovrai implementare questo metodo in Enemy o nelle sue sottoclassi)
		enemy.move();
	}

	/**
	 * Controlla se il nemico si trova sul bordo di una piattaforma. Questa logica
	 * dipende da come è strutturato il tuo LevelData.
	 * 
	 * @return true se il nemico sta per cadere, altrimenti false.
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
		// Potresti voler fermare il movimento qui se necessario
	}
}