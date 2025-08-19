package model;

import static model.TileTypes.WALL;

import java.awt.geom.Rectangle2D;

public class WallTile extends Tile {
	private static final long serialVersionUID = 1L;

	public WallTile(int x, int y) {
		super(x, y);
		solid = true;
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
		return WALL; 
	}

}
