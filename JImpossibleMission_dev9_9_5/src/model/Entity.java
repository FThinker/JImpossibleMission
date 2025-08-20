package model;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Observable;
import static model.GameConstants.*;

/**
 * An abstract base class for all dynamic game objects, such as the player and enemies.
 * It provides fundamental properties like position, dimensions, and a hitbox.
 * It extends {@link Observable} to allow other parts of the application (like the view)
 * to be notified of its state changes.
 */
@SuppressWarnings("deprecation")
public abstract class Entity extends Observable implements Serializable {
	private static final long serialVersionUID = 1L;
	protected int x, y;
    protected int width, height;
    protected Rectangle2D.Float hitbox;

    /**
     * Constructs an Entity at a given initial spawn point.
     * @param initialSpawn The starting coordinates for the entity.
     */
    public Entity(Point initialSpawn) {
        this.x = initialSpawn.x;
        this.y = initialSpawn.y;
        this.width = 32 * (int)SCALE;
        this.height = 32 * (int)SCALE;
    }

    /**
     * Initializes the entity's hitbox. Concrete subclasses must implement this method
     * to define the specific shape and size of their hitbox.
     */
    protected abstract void initHitbox();

    /**
     * Updates the hitbox's position to match the entity's current coordinates.
     * This should be called whenever the entity moves.
     */
    protected void updateHitbox() {
        hitbox.x = x;
        hitbox.y = y;
    }
    
    // --- GETTERS ---

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }
}