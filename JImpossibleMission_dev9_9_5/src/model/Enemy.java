package model;

import java.awt.Point;
import java.awt.geom.Rectangle2D;

/**
 * An abstract base class for all enemy entities in the game.
 * It defines common properties such as type, state, direction, and behavior,
 * and declares abstract methods that specific enemy implementations must provide.
 */
public abstract class Enemy extends Entity {
	private static final long serialVersionUID = 1L;
	protected EnemyType enemyType;
    protected int aniTick, aniIndex;
    protected Directions direction = Directions.RIGHT;
    protected EnemyState currentState = EnemyState.IDLE;
    
    protected boolean isFacingRight = true;
    protected long lastDirectionChangeTime;
    
    protected EnemyBehavior behavior;
    
    protected int speed = 0;

    /**
     * Constructs a new Enemy.
     *
     * @param x      The initial x-coordinate.
     * @param y      The initial y-coordinate.
     * @param width  The width of the enemy.
     * @param height The height of the enemy.
     * @param type   The {@link EnemyType} of this enemy.
     */
    public Enemy(int x, int y, int width, int height, EnemyType type) {
        super(new Point(x, y));
        this.width = width;
        this.height = height;
        this.enemyType = type;
        this.lastDirectionChangeTime = System.currentTimeMillis();
    }
    
    /**
     * Updates the enemy's state and logic. This method is called on every game tick.
     * @param currentTime The current system time in milliseconds.
     * @param levelData   The data for the current level.
     * @param player      The player object, for interaction checks.
     */
    public abstract void update(long currentTime, Level levelData, Player player);
    
    /**
     * Executes the enemy's movement logic.
     */
    public abstract void move();
    
    /**
     * Makes the enemy turn around to face the opposite direction.
     */
    protected abstract void turn();
    
    /**
     * Resets the enemy to its initial position and state.
     */
    public abstract void resetPosition();
    
    /**
     * Sets the enemy's current state.
     * @param state The new {@link EnemyState}.
     */
    protected abstract void setState(EnemyState state);
    
	/**
     * @return The hitbox used for attack collisions.
     */
	public abstract Rectangle2D.Float getAttackBox();
	
	/**
     * @param state The state for which to get the duration.
     * @return The duration in milliseconds for the given state.
     */
	public abstract long getDurationForState(EnemyState state);
	
	/**
     * @return The timestamp when the current state was entered.
     */
	public abstract long getStateStartTime();
    
    // --- GETTERS ---
    
    public int getAniIndex() { return aniIndex; }
    public Directions getDirection() { return direction; }
    public EnemyType getEnemyType() { return enemyType; }
    public boolean isFacingRight() { return isFacingRight; }
    public EnemyState getState() { return currentState; }
    public int getSpeed() { return speed; }
}