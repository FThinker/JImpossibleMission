package model;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import static model.Directions.*;

/**
 * Represents the player character controlled by the user.
 * This class manages the player's state, position, movement, physics (gravity, jumping),
 * and hitbox. It extends {@link Entity} and notifies observers of changes.
 */
@SuppressWarnings("deprecation")
public class Player extends Entity {
	private static final long serialVersionUID = 1L;
	private final int speed = 2;
    private PlayerState currentState;
    
    private int hitboxOffsetX = 10 * 2;
    private int hitboxOffsetY = 4 * 2;
    private int hitboxWidth = 12 * 2;
    private int hitboxHeight = 28 * 2;
    private Directions facingDirection = RIGHT;
    
    private int jumpHitboxOffsetX = 10 * 2;
    private int jumpHitboxOffsetY = 11 * 2;
    private int jumpHitboxWidth = 12 * 2;
    private int jumpHitboxHeight = 12 * 2;
    
    private float yVelocity = 0;
    private boolean inAir = true;
    private static final float JUMP_FORCE = -1.7f;
    private static final float MAX_FALL_SPEED = 15f;
    
    private Point initialSpawn;

    /**
     * Constructs the Player at a given initial spawn point.
     * @param initialSpawn The starting coordinates.
     */
    public Player(Point initialSpawn) {
    	super(initialSpawn);
    	this.x = initialSpawn.x;
    	this.y = initialSpawn.y;
    	setInitialSpawn(new Point(x, y));
    	this.width = 32 * 2;
    	this.height = 32 * 2;
        this.currentState = PlayerState.IDLE;
        initHitbox();
    }
    
    /**
     * Resets the player to their initial spawn point for the current level.
     */
    public void resetPosition() {
    	this.x = initialSpawn.x;
    	this.y = initialSpawn.y;
    	updateHitbox();
    	setChanged();
    	notifyObservers();
    }
    
    @Override
    protected void initHitbox() {
        hitbox = new Rectangle2D.Float(x + hitboxOffsetX, y + hitboxOffsetY, hitboxWidth, hitboxHeight);
    }
    
    @Override
    protected void updateHitbox() {
    	if(currentState == PlayerState.JUMPING || currentState == PlayerState.FALLING) {
    		hitbox.x = this.x + jumpHitboxOffsetX;
	        hitbox.y = this.y + jumpHitboxOffsetY;
	        hitbox.width = jumpHitboxWidth;
	        hitbox.height = jumpHitboxHeight;
    	}
    	else {
	        hitbox.x = this.x + hitboxOffsetX;
	        hitbox.y = this.y + hitboxOffsetY;
	        hitbox.width = hitboxWidth;
	        hitbox.height = hitboxHeight;
    	}
    }
    
    /**
     * Sets the player's state and notifies observers if the state has changed.
     * @param newState The new {@link PlayerState}.
     */
    private void setState(PlayerState newState) {
        if (this.currentState != newState) {
            this.currentState = newState;
            updateHitbox();
            setChanged();
            notifyObservers();
        }
    }

    // --- MOVEMENT METHODS ---

    public void moveLeft() {
        x -= speed;
        setDirection(LEFT);
        if(!inAir)
        	setState(PlayerState.RUNNING);
        updateHitbox();
        setChanged();
        notifyObservers();
    }

    public void moveRight() {
        x += speed;
        setDirection(RIGHT);
        if(!inAir)
        	setState(PlayerState.RUNNING);
        updateHitbox();
        setChanged();
        notifyObservers();
    }
    
    public void setIdle() {
    	if(!inAir)
    		setState(PlayerState.IDLE);
    }
    
    public void setSearching() {
    	setState(PlayerState.SEARCHING);
    }
    
    /**
     * Applies gravity to the player if they are in the air, updating their vertical velocity and position.
     * @param gravity The gravitational force to apply.
     */
    public void applyGravity(float gravity) {
        if (inAir) {
            yVelocity += gravity;
            if (yVelocity > MAX_FALL_SPEED) {
                yVelocity = MAX_FALL_SPEED;
            }
            y += yVelocity;
            updateHitbox();
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Makes the player jump if they are on the ground.
     */
    public void jump() {
        if (!inAir) {
            yVelocity = JUMP_FORCE;
            inAir = true;
            setState(PlayerState.JUMPING);
        }
    }
    
    /**
     * Called when the player lands on a solid surface.
     * @param groundY The y-coordinate of the ground surface.
     */
    public void landOnGround(float groundY) {
        y = (int) (groundY - height);
        yVelocity = 0;
        inAir = false;
        updateHitbox();
        setChanged();
        notifyObservers();
    }
    
    /**
     * Moves the player along with a lift.
     * @param deltaY The vertical distance the lift moved.
     */
    public void moveWithLift(int deltaY) {
        this.y += deltaY;
        updateHitbox();
        setChanged();
        notifyObservers();
    }
    
	/**
     * Instantly moves the player to a new location.
     * @param x The new x-coordinate.
     * @param y The new y-coordinate.
     */
	public void teleport(int x, int y) {
		this.x = x;
		this.y = y;
		updateHitbox();
	}
    
    // --- GETTERS AND SETTERS ---

    public Point getInitialSpawn() { return this.initialSpawn; }
    public void setInitialSpawn(Point initialSpawn) { this.initialSpawn = initialSpawn; }
    public int getSpeed() { return speed; }
    public PlayerState getCurrentState() { return currentState; }
    public Directions getDirection() { return this.facingDirection; }
    public void setDirection(Directions dir) { this.facingDirection = dir; }
    public boolean isInAir() { return inAir; }
    public void setInAir(boolean inAir) { this.inAir = inAir; }
    public float getYVelocity() { return yVelocity; }
	public void setYVelocity(float velocity) { this.yVelocity = velocity; }
	public void setY(float y) { this.y = (int) y; }
}