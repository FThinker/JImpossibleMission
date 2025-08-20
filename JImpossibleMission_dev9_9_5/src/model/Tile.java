package model;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Observable;
import static model.GameConstants.*;

/**
 * An abstract base class for all tiles that make up the game world's structure.
 * It provides fundamental properties like position, dimensions, solidity, and a hitbox.
 * It can be observed, which is particularly useful for dynamic tiles like lifts.
 */
@SuppressWarnings("deprecation")
public abstract class Tile extends Observable implements Serializable{
	private static final long serialVersionUID = 1L;
	protected int x, y;
	protected int width, height;
	protected boolean solid;
	protected Rectangle2D.Float hitbox;
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
		this.width = TILES_DEFAULT_SIZE;
		this.height = TILES_DEFAULT_SIZE;
	}
	
	/**
     * Initializes the tile's hitbox. Must be implemented by subclasses.
     */
	protected abstract void initHitbox();
	
	/**
     * @return The rectangular hitbox for this tile used for collision detection.
     */
	public abstract Rectangle2D.Float getHitbox();
	
	/**
     * @return True if the tile is solid and collidable, false otherwise.
     */
	public abstract boolean isSolid();

	/**
     * @return The {@link TileTypes} enum constant representing the type of this tile.
     */
	public abstract TileTypes getType();
}