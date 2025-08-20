package model;

import static model.TileTypes.PLATFORM;
import java.awt.geom.Rectangle2D;

/**
 * Represents a solid platform tile that entities can stand on.
 * It is half as tall as a wall tile.
 */
public class PlatformTile extends Tile {
	private static final long serialVersionUID = 1L;

	public PlatformTile(int x, int y) {
		super(x, y);
		solid = true;
		initHitbox();
	}

	@Override
	protected void initHitbox() {
		hitbox = new Rectangle2D.Float(x, y, width, (height / 2) + 1);
	}

	@Override
	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}

	@Override
	public boolean isSolid() {
		return solid;
	}

	@Override
	public TileTypes getType() {
		return PLATFORM;
	}
}