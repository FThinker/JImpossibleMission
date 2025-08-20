package controller;

import static model.GameConstants.*;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.Directions;
import model.LiftTile;
import model.Player;
import model.Tile;
import static model.TileTypes.*;

/**
 * A utility class containing static methods for handling game physics.
 * This includes collision detection, movement validation, gravity effects,
 * and interactions with special tiles like lifts.
 */
public class PhysicsHandler {

	/**
     * Checks if the player can move to a new position without colliding with solid tiles.
     *
     * @param player    The player entity.
     * @param direction The direction of intended movement.
     * @param lvlData   The 2D array representing the level's tile data.
     * @return True if the move is possible, false otherwise.
     */
	public static boolean canMoveHere(Player player, Directions direction, Tile[][] lvlData) {
		Rectangle2D.Float hitbox = player.getHitbox();
		Point upperLeft = new Point((int) hitbox.x, (int) hitbox.y);
		Point upperRight = new Point((int) (hitbox.x + hitbox.width), (int) hitbox.y);
		Point halfLeft = new Point((int) hitbox.x, (int) (hitbox.y + (hitbox.height / 2)));
		Point halfRight = new Point((int) (hitbox.x + hitbox.width), (int) (hitbox.y + (hitbox.height / 2)));
		Point lowerLeft = new Point((int) hitbox.x, (int) (hitbox.y + hitbox.height));
		Point lowerRight = new Point((int) (hitbox.x + hitbox.width), (int) (hitbox.y + hitbox.height));

		Set<Tile> currentTiles = new HashSet<Tile>();
		currentTiles.add(lvlData[(int) upperLeft.y / TILES_DEFAULT_SIZE][(int) upperLeft.x / TILES_DEFAULT_SIZE]);
		currentTiles.add(lvlData[(int) upperRight.y / TILES_DEFAULT_SIZE][(int) upperRight.x / TILES_DEFAULT_SIZE]);
		currentTiles.add(lvlData[(int) halfLeft.y / TILES_DEFAULT_SIZE][(int) halfLeft.x / TILES_DEFAULT_SIZE]);
		currentTiles.add(lvlData[(int) halfRight.y / TILES_DEFAULT_SIZE][(int) halfRight.x / TILES_DEFAULT_SIZE]);
		currentTiles.add(lvlData[(int) lowerLeft.y / TILES_DEFAULT_SIZE][(int) lowerLeft.x / TILES_DEFAULT_SIZE]);
		currentTiles.add(lvlData[(int) lowerRight.y / TILES_DEFAULT_SIZE][(int) lowerRight.x / TILES_DEFAULT_SIZE]);

		switch (direction) {
		case LEFT:
			for (Tile t : currentTiles) {
				Tile adjacentLeft = lvlData[(int) (t.getHitbox().y
						/ TILES_DEFAULT_SIZE)][(int) ((t.getHitbox().x - player.getSpeed()) / TILES_DEFAULT_SIZE)];
				Rectangle2D.Float adjacentHitbox = adjacentLeft.getHitbox();
				Rectangle2D.Float movedHitbox = new Rectangle2D.Float(hitbox.x - player.getSpeed(), hitbox.y,
						hitbox.width, hitbox.height);

				if (adjacentHitbox.intersects(movedHitbox) || movedHitbox.intersects(adjacentHitbox)) {
					if (adjacentLeft.isSolid()) {
						return false;
					}
				}
			}

			break;
		case RIGHT:
			for (Tile t : currentTiles) {
				Tile adjacentLeft = lvlData[(int) (t.getHitbox().y / TILES_DEFAULT_SIZE)][(int) ((t.getHitbox().x + 1)
						/ TILES_DEFAULT_SIZE)];
				Rectangle2D.Float adjacentHitbox = adjacentLeft.getHitbox();
				Rectangle2D.Float movedHitbox = new Rectangle2D.Float(hitbox.x + 1, hitbox.y, hitbox.width,
						hitbox.height);

				if (adjacentHitbox.intersects(movedHitbox) || movedHitbox.intersects(adjacentHitbox)) {
					if (adjacentLeft.isSolid()) {
						return false;
					}
				}
			}
			break;
		case UP:
			for (Tile t : currentTiles) {
				Tile adjacentLeft = lvlData[(int) ((t.getHitbox().y - 1) / TILES_DEFAULT_SIZE)][(int) (t.getHitbox().x
						/ TILES_DEFAULT_SIZE)];
				Rectangle2D.Float adjacentHitbox = adjacentLeft.getHitbox();
				Rectangle2D.Float movedHitbox = new Rectangle2D.Float(hitbox.x, hitbox.y - 1, hitbox.width,
						hitbox.height);

				if (adjacentHitbox.intersects(movedHitbox) || movedHitbox.intersects(adjacentHitbox)) {
					if (adjacentLeft.isSolid()) {
						return false;
					}
				}
			}
			break;
		case DOWN:
			for (Tile t : currentTiles) {
				Tile adjacentLeft = lvlData[(int) ((t.getHitbox().y + 1) / TILES_DEFAULT_SIZE)][(int) (t.getHitbox().x
						/ TILES_DEFAULT_SIZE)];
				Rectangle2D.Float adjacentHitbox = adjacentLeft.getHitbox();
				Rectangle2D.Float movedHitbox = new Rectangle2D.Float(hitbox.x, hitbox.y + 1, hitbox.width,
						hitbox.height);

				if (adjacentHitbox.intersects(movedHitbox) || movedHitbox.intersects(adjacentHitbox)) {
					if (adjacentLeft.isSolid()) {
						return false;
					}
				}
			}
			break;
		default:
			return false;
		}

		return true;
	}

	/**
     * Handles vertical collisions for the player, such as landing on the ground or hitting a ceiling.
     *
     * @param player  The player entity.
     * @param lvlData The 2D array representing the level's tile data.
     */
	public static void handleVerticalCollisions(Player player, Tile[][] lvlData) {
		Rectangle2D.Float hitbox = player.getHitbox();
		float yVelocity = player.getYVelocity();

		float nextY = hitbox.y + yVelocity;
		Rectangle2D.Float testHitbox = new Rectangle2D.Float(hitbox.x, nextY, hitbox.width, hitbox.height);

		for (int row = 0; row < lvlData.length; row++) {
			for (int col = 0; col < lvlData[0].length; col++) {
				Tile tile = lvlData[row][col];
				if (tile.isSolid() && testHitbox.intersects(tile.getHitbox())) {
					if (yVelocity > 0) { // Falling
						player.landOnGround(tile.getHitbox().y);
					} else if (yVelocity < 0) { // Hitting ceiling
						player.setYVelocity(0);
						player.setY(tile.getHitbox().y + tile.getHitbox().height);
					}
					return;
				}
			}
		}

		if (player.getYVelocity() != 0) {
			player.setInAir(true);
		}
	}

	/**
     * Checks if the player is currently standing on a solid tile.
     *
     * @param player  The player entity.
     * @param lvlData The 2D array representing the level's tile data.
     * @return True if the player is on the ground, false otherwise.
     */
	public static boolean isOnGround(Player player, Tile[][] lvlData) {
		Rectangle2D.Float hitbox = player.getHitbox();
		Rectangle2D.Float groundCheck = new Rectangle2D.Float(hitbox.x, hitbox.y + 1,
				hitbox.width, hitbox.height);

		for (int row = 0; row < lvlData.length; row++) {
			for (int col = 0; col < lvlData[0].length; col++) {
				Tile tile = lvlData[row][col];
				if (tile.isSolid() && groundCheck.intersects(tile.getHitbox())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
     * Checks if the player is currently standing on a specific lift tile.
     *
     * @param player The player entity.
     * @param lift   The lift tile to check against.
     * @return True if the player is on the lift, false otherwise.
     */
	public static boolean isOnLift(Player player, LiftTile lift) {
		Rectangle2D.Float hitbox = player.getHitbox();
		Rectangle2D.Float groundCheck = new Rectangle2D.Float(hitbox.x, hitbox.y + 1,
				hitbox.width, hitbox.height);

		if (groundCheck.intersects(lift.getHitbox()))
			if (hitbox.x >= lift.getHitbox().x && hitbox.getMaxX() <= lift.getHitbox().getMaxX())
				return true;
		return false;
	}

	/**
     * Determines all possible stopping points for a lift along its vertical path.
     * A stop is defined as an empty space adjacent to a solid platform.
     *
     * @param lift    The lift tile.
     * @param lvlData The 2D array representing the level's tile data.
     * @return A sorted list of tiles representing the valid stops for the lift.
     */
	public static List<Tile> getLiftStops(Tile lift, Tile[][] lvlData) {
		int currentRow = (int)(lift.getHitbox().getY() / TILES_DEFAULT_SIZE);
		int currentCol = (int)(lift.getHitbox().getX() / TILES_DEFAULT_SIZE);
		
		List<Tile> stops = new ArrayList<>();
		
		for(int row = 0; row < TILES_IN_HEIGHT; row++) {
			if(lvlData[row][currentCol].getType() == EMPTY) {
				if(currentCol - 1 >= 0 && lvlData[row][currentCol-1].getType() == PLATFORM) {
					stops.add(lvlData[row][currentCol]);
				} else if (currentCol + 1 < lvlData[0].length && lvlData[row][currentCol+1].getType() == PLATFORM) {
                    stops.add(lvlData[row][currentCol]);
                } 
				else if (row == TILES_IN_HEIGHT-1 && lvlData[row][currentCol].getType() == EMPTY) {
                	stops.add(lvlData[row][currentCol]);
                }
			}
		}
		
		if(lvlData[currentRow][currentCol].getType() == LIFT) {
			stops.add(lvlData[currentRow][currentCol]);
		}

        boolean originalYAsStop = false;
        for (Tile stop : stops) {
            if (stop.getHitbox().y == ((LiftTile)lift).getOriginalY()) {
                originalYAsStop = true;
                break;
            }
        }
        if (!originalYAsStop) {
            int originalRow = ((LiftTile)lift).getOriginalY() / TILES_DEFAULT_SIZE;
            if (originalRow >= 0 && originalRow < lvlData.length && !lvlData[originalRow][currentCol].isSolid()) {
                stops.add(lvlData[originalRow][currentCol]);
            }
        }
		
        stops.sort((t1, t2) -> Float.compare(t1.getHitbox().y, t2.getHitbox().y));
		return stops;
	}

	/**
     * Moves a lift to the next available stop in the specified direction.
     *
     * @param lift      The lift to move.
     * @param direction The direction to move (UP or DOWN).
     * @param lvlData   The 2D array of level tiles.
     * @param stops     The pre-calculated list of valid stops for the lift.
     * @param player    The player entity.
     */
	public static void moveLiftToNextStop(LiftTile lift, Directions direction, Tile[][] lvlData, List<Tile> stops, Player player) {
		if (lift.getTargetY() != -1) {
            return; 
        }

		int currentRow = (int)(lift.getHitbox().getY() / TILES_DEFAULT_SIZE);
		int currentCol = (int)(lift.getHitbox().getX() / TILES_DEFAULT_SIZE);
		
		Tile currentStop = lvlData[currentRow][currentCol];
		
        if (!stops.contains(currentStop)) {
            lift.setTargetY(lift.getOriginalY());
            if (lift.getHitbox().y > lift.getOriginalY()) {
                lift.setCurrentMovementDirection(Directions.UP);
            } else {
                lift.setCurrentMovementDirection(Directions.DOWN);
            }
            return; 
        }

		int currentStopIndex = stops.indexOf(currentStop);
		
		switch(direction) {
		case UP:
			if(currentStopIndex > 0) {
				Tile nextStop = stops.get(currentStopIndex - 1);
				lift.setTargetY((int) nextStop.getHitbox().y);
				lift.setCurrentMovementDirection(Directions.UP);
			} else {
                lift.setTargetY(lift.getOriginalY());
                lift.setCurrentMovementDirection(Directions.UP);
            }
			break;
		case DOWN:
			if (currentStopIndex < stops.size() - 1) {
                Tile nextStop = stops.get(currentStopIndex + 1);
                lift.setTargetY((int) nextStop.getHitbox().y);
                lift.setCurrentMovementDirection(Directions.DOWN);
            } else {
                lift.setTargetY(lift.getOriginalY());
                lift.setCurrentMovementDirection(Directions.DOWN);
            }
			break;
		default:
			break;
		}
	}

	/**
     * Checks if the player is at the edge of the level, triggering a transition to the elevator.
     *
     * @param player The player entity.
     * @return True if the player is at the far left or far right of the screen, false otherwise.
     */
	public static boolean isLeavingLevel(Player player) {
		if(player.getHitbox().getX() - 1 <= 1)
			return true;
		if(player.getHitbox().getMaxX() + 1 >= LOGIC_WIDTH - 1)
			return true;
		return false;
	}

	/**
     * Checks if the player is at the edge of the elevator screen, triggering a transition to a level.
     *
     * @param player The player entity.
     * @return True if the player is at the far left or far right of the screen, false otherwise.
     */
	public static boolean isLeavingElevator(Player player) {
		 if (player.getHitbox().getX() <= 1)
			 return true;
	     if (player.getHitbox().getMaxX() >= LOGIC_WIDTH - 1)
	    	 return true;
		return false;
	}
}