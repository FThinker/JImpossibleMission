package model;

import static model.GameConstants.SCALE;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

/**
 * Represents a mobile enemy robot that patrols an area.
 * This class extends the base {@link Enemy} and uses a {@link MovingRobotBehavior}
 * to define its AI, which includes walking, turning at edges, and attacking the player.
 */
@SuppressWarnings("deprecation")
public class MovingRobot extends Enemy {
	private static final long serialVersionUID = 1L;
	
	private int hbOffsetX = EnemyType.STANDING_ROBOT.getHbOffsetX() * 2;
	private int hbOffsetY = EnemyType.STANDING_ROBOT.getHbOffsetY() * 2;
	private int hbWidth = EnemyType.STANDING_ROBOT.getWidth() * 2;
	private int hbHeight = EnemyType.STANDING_ROBOT.getHeight() * 2;

	private Rectangle2D.Float attackBox;
	
	private int speed = 1;
	private Point initialSpawn;

	/**
     * Constructs a MovingRobot at a specific spawn point.
     *
     * @param initialSpawn The initial coordinates for the robot.
     * @param width The display width of the robot sprite.
     * @param height The display height of the robot sprite.
     */
	public MovingRobot(Point initialSpawn, int width, int height) {
		super(initialSpawn.x, initialSpawn.y, width, height, EnemyType.MOVING_ROBOT);
		this.x = initialSpawn.x - hbOffsetX;
    	this.y = initialSpawn.y - 32;
    	this.initialSpawn = new Point(x, y);
		this.behavior = new MovingRobotBehavior();
		initHitbox();
		initAttackBox();
	}
	
	@Override
	public void resetPosition() {
		this.x = initialSpawn.x;
    	this.y = initialSpawn.y;
    	setState(EnemyState.IDLE);
    	this.behavior.resetBehavior(this);
    	this.direction = Directions.RIGHT;
    	isFacingRight = true;
    	initHitbox();
    	initAttackBox();
    	setChanged();
    	notifyObservers();
	}

	@Override
	protected void initHitbox() {
		hitbox = new Rectangle2D.Float(x + hbOffsetX - SCALE, y + hbOffsetY - SCALE, hbWidth, hbHeight);
	}

	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x + 21 * 2, y + 11 * 2, 48 * 2, 9 * 2);
	}

	@Override
	public void update(long currentTime, Level levelData, Player player) {
		behavior.update(this, currentTime, levelData, player);
		updateHitbox();
		updateAttackBox();
		setChanged();
		notifyObservers();
	}

	@Override
	protected void updateHitbox() {
		hitbox.x = this.x + hbOffsetX;
		hitbox.y = this.y + hbOffsetY;
		hitbox.width = hbWidth;
		hitbox.height = hbHeight;
	}

	private void updateAttackBox() {
		if (isFacingRight) {
			attackBox.x = this.hitbox.x + hitbox.width - SCALE;
			attackBox.y = this.hitbox.y;
			attackBox.width = 48 * 2;
			attackBox.height = 9 * 2;
			attackBox.setRect(attackBox.x, attackBox.y, attackBox.width, attackBox.height);
		} else {
			attackBox.x = this.hitbox.x + SCALE;
			attackBox.y = this.hitbox.y;
			attackBox.width = -48 * 2;
			attackBox.height = 9 * 2;
			attackBox.setRect(attackBox.x + attackBox.width, attackBox.y, -attackBox.width, attackBox.height);
		}
	}

	@Override
	protected void turn() {
		isFacingRight = !isFacingRight;
		direction = isFacingRight ? Directions.RIGHT : Directions.LEFT;
		updateAttackBox();
	}

	@Override
	public void setState(EnemyState newState) {
		this.currentState = newState;
	}
    
    @Override
	public void move() {
		if (isFacingRight())
			x += speed;
		else
			x -= speed;
	}

    @Override
	public Rectangle2D.Float getAttackBox() {
		return attackBox;
	}

	@Override
	public long getDurationForState(EnemyState state) {
		return behavior.getDurationForState(state);
	}

	@Override
	public long getStateStartTime() {
		return behavior.getStateStartTime();
	}
}