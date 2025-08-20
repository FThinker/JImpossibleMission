package model;

import static model.TileTypes.EMPTY;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

/**
 * Represents an empty, non-solid tile in the game world.
 * Entities can pass through it without collision.
 */
public class EmptyTile extends Tile {
	private static final long serialVersionUID = 1L;

	/**
     * Constructs an EmptyTile at the specified coordinates.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     */
	public EmptyTile(int x, int y) {
		super(x, y);
		solid = false;
		initHitbox();
	}

	@Override
	protected void initHitbox() {
		hitbox = new Rectangle2D.Float(x, y, width, height);
	}

	@Override
	public Float getHitbox() {
		return hitbox;
	}

	@Override
	public boolean isSolid() {
		return solid;
	}

	@Override
	public TileTypes getType() {
		return EMPTY;
	}
}