package model;

import static model.TileTypes.LIFT;
import java.awt.geom.Rectangle2D;

/**
 * Represents a solid, vertically moving lift (elevator platform) within a level.
 * It is an {@link Observable} entity, notifying observers when its position changes.
 */
@SuppressWarnings("deprecation")
public class LiftTile extends Tile {
	private static final long serialVersionUID = 1L;
	
	private final int speed = 2;
    private boolean moving = false;
    private final int originalY;
    
    private int targetY = -1; // -1 means no destination is set
    private Directions currentMovementDirection = null;

	/**
     * Constructs a LiftTile at the specified coordinates.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     */
	public LiftTile(int x, int y) {
		super(x, y);
		solid = true;
		originalY = y;
		initHitbox();
	}

	@Override
	protected void initHitbox() {
		hitbox = new Rectangle2D.Float(x, y, width, (height / 2) + 1);
	}
	
	private void updateHitbox() {
        hitbox.x = this.x;
        hitbox.y = this.y;
    }
	
	/**
     * Stops all movement and resets the lift's target destination.
     */
	public void stop() {
        moving = false;
        targetY = -1;
        currentMovementDirection = null;
        updateHitbox();
        setChanged();
        notifyObservers();
    }
    
    /**
     * Moves the lift up by its speed value and notifies observers.
     */
    public void moveUp() {
        y -= getSpeed();
        updateHitbox();
        setChanged();
        notifyObservers();
    }
    
    /**
     * Moves the lift down by its speed value and notifies observers.
     */
    public void moveDown() {
        y += getSpeed();
        updateHitbox();
        setChanged();
        notifyObservers();
    }

    /**
     * Resets the lift to its original starting position.
     */
	public void resetPosition() {
        y = originalY;
        updateHitbox();
        setChanged();
        notifyObservers();
    }
	
	// --- GETTERS AND SETTERS ---

    public boolean isMoving() { return moving; }
    public int getOriginalY() { return originalY; }
    public int getTargetY() { return targetY; }
    public Directions getCurrentMovementDirection() { return currentMovementDirection; }
	public int getSpeed() { return speed; }

    public void setTargetY(int targetY) {
        this.targetY = targetY;
        this.moving = true;
        setChanged();
        notifyObservers();
    }
    
    public void setCurrentMovementDirection(Directions direction) {
        this.currentMovementDirection = direction;
    }

	@Override
	public Rectangle2D.Float getHitbox() { return hitbox; }

	@Override
	public boolean isSolid() { return solid; }

	@Override
	public TileTypes getType() { return LIFT; }
}