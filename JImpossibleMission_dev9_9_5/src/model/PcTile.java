package model;

import static model.TileTypes.PC;

import java.awt.geom.Rectangle2D;

public class PcTile extends Tile {
	private static final long serialVersionUID = 1L;
	private int width = (int) (32 * 2);
	private int height = (int) (32 * 2);

	public PcTile(int x, int y) {
		super(x, y - 32);
		solid = false;
		initHitbox();
	}

	@Override
	protected void initHitbox() {
		hitbox = new Rectangle2D.Float(x, y, width, height);

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
		return PC;
	}
}
