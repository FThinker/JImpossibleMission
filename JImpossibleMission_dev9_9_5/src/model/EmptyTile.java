package model;

import static model.TileTypes.EMPTY;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

public class EmptyTile extends Tile {
	private static final long serialVersionUID = 1L;

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
