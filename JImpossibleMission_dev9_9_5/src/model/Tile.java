package model;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Observable;

import static model.GameConstants.*;

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
	
	protected abstract void initHitbox();
	
	public abstract Rectangle2D.Float getHitbox();
	
	public abstract boolean isSolid();

	public abstract TileTypes getType();
}
