package controller;

import static model.GameConstants.*;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import model.Directions;
import model.GameConstants;
import model.Level;
import model.LiftTile;
import model.Player;
import model.Tile;

import static model.TileTypes.*;

public class PhysicsHandler {

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

	public static void handleVerticalCollisions(Player player, Tile[][] lvlData) {
		Rectangle2D.Float hitbox = player.getHitbox();
		float yVelocity = player.getYVelocity();

		// Calculate next position
		float nextY = hitbox.y + yVelocity;
		Rectangle2D.Float testHitbox = new Rectangle2D.Float(hitbox.x, nextY, hitbox.width, hitbox.height);

		// Check for collisions
		for (int row = 0; row < lvlData.length; row++) {
			for (int col = 0; col < lvlData[0].length; col++) {
				Tile tile = lvlData[row][col];
				if (tile.isSolid() && testHitbox.intersects(tile.getHitbox())) {
					if (yVelocity > 0) {
						// Falling - land on top
						player.landOnGround(tile.getHitbox().y);
					} else if (yVelocity < 0) {
						// Hit ceiling
						player.setYVelocity(0);
						player.setY(tile.getHitbox().y + tile.getHitbox().height);
//						player.setY((float) (tile.getHitbox().getMaxY() + 1));
					}
					return;
				}
			}
		}

		// If no collision found, player is in air
		if (player.getYVelocity() != 0) {
			player.setInAir(true);
		}
	}

	// Add method to check if on ground
	public static boolean isOnGround(Player player, Tile[][] lvlData) {
		Rectangle2D.Float hitbox = player.getHitbox();
		Rectangle2D.Float groundCheck = new Rectangle2D.Float(hitbox.x, hitbox.y + 1, // Check 1 pixel below
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

	public static boolean isOnLift(Player player, LiftTile lift) {
		Rectangle2D.Float hitbox = player.getHitbox();
		Rectangle2D.Float groundCheck = new Rectangle2D.Float(hitbox.x, hitbox.y + 1, // Check 1 pixel below
				hitbox.width, hitbox.height);

		if (groundCheck.intersects(lift.getHitbox()))
			if (hitbox.x >= lift.getHitbox().x && hitbox.getMaxX() <= lift.getHitbox().getMaxX())
				return true;
		return false;
	}

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
		
		// Aggiungi la posizione corrente dell'ascensore come fermata
		if(lvlData[currentRow][currentCol].getType() == LIFT) {
			stops.add(lvlData[currentRow][currentCol]);
		}

		// Assicurati che la posizione originale dell'ascensore sia una fermata
        boolean originalYAsStop = false;
        for (Tile stop : stops) {
            if (stop.getHitbox().y == ((LiftTile)lift).getOriginalY()) {
                originalYAsStop = true;
                break;
            }
        }
        if (!originalYAsStop) {
            // Crea un tile "fittizio" o usa il tile esistente alla posizione originale se appropriato
            // Per semplicità, assumiamo che la posizione originale sia un tile EMPTY o LIFT
            // e lo aggiungiamo se non è già tra le fermate
            int originalRow = ((LiftTile)lift).getOriginalY() / TILES_DEFAULT_SIZE;
            if (originalRow >= 0 && originalRow < lvlData.length && !lvlData[originalRow][currentCol].isSolid()) {
                stops.add(lvlData[originalRow][currentCol]);
            }
        }
		
		// Ordina le fermate per la coordinata Y
        stops.sort((t1, t2) -> Float.compare(t1.getHitbox().y, t2.getHitbox().y));

		return stops;
	}

	public static void moveLiftToNextStop(LiftTile lift, Directions direction, Tile[][] lvlData, List<Tile> stops, Player player) {
		// Non muovere l'ascensore se ha già una destinazione
		if (lift.getTargetY() != -1) {
            return; 
        }

		int currentRow = (int)(lift.getHitbox().getY() / TILES_DEFAULT_SIZE);
		int currentCol = (int)(lift.getHitbox().getX() / TILES_DEFAULT_SIZE);
		
		Tile currentStop = lvlData[currentRow][currentCol];
		
		// Assicurati che la posizione attuale dell'ascensore sia una fermata valida
        if (!stops.contains(currentStop)) {
            // Se l'ascensore non è su una fermata, torniamo alla posizione originale
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
			if(currentStopIndex > 0) { // Se non siamo alla prima fermata
				Tile nextStop = stops.get(currentStopIndex - 1);
				lift.setTargetY((int) nextStop.getHitbox().y);
				lift.setCurrentMovementDirection(Directions.UP);
			} else {
                // Se siamo alla fermata più alta, torna alla posizione originale
                lift.setTargetY(lift.getOriginalY());
                lift.setCurrentMovementDirection(Directions.UP);
            }
			break;
		case DOWN:
			if (currentStopIndex < stops.size() - 1) { // Se non siamo all'ultima fermata
                Tile nextStop = stops.get(currentStopIndex + 1);
                lift.setTargetY((int) nextStop.getHitbox().y);
                lift.setCurrentMovementDirection(Directions.DOWN);
            } else {
                // Se siamo alla fermata più bassa, torna alla posizione originale
                lift.setTargetY(lift.getOriginalY());
                lift.setCurrentMovementDirection(Directions.DOWN);
            }
			break;
		default:
			break;
		}
	}

	public static boolean isLeavingLevel(Player player) {
		if(player.getHitbox().getX() - 1 <= 1)
			return true;
		if(player.getHitbox().getMaxX() + 1 >= LOGIC_WIDTH - 1)
			return true;
		return false;
	}

	public static boolean isLeavingElevator(Player player) {
		 if (player.getHitbox().getX() <= 1)
			 return true;
	     if (player.getHitbox().getMaxX() >= LOGIC_WIDTH - 1)
	    	 return true;
		return false;
	}
}