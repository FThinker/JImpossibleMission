// StandingRobot.java (Nuovo file nel package model)
package model;

import java.awt.Point;
import java.awt.geom.Rectangle2D;

import static model.GameConstants.*;

@SuppressWarnings("deprecation")
public class StandingRobot extends Enemy {
	private static final long serialVersionUID = 1L;
	
	private int hbOffsetX = EnemyType.STANDING_ROBOT.getHbOffsetX() * 2;
    private int hbOffsetY = EnemyType.STANDING_ROBOT.getHbOffsetY() * 2;
    private int hbWidth = EnemyType.STANDING_ROBOT.getWidth() * 2;
    private int hbHeight = EnemyType.STANDING_ROBOT.getHeight() * 2;
    
    private Rectangle2D.Float attackBox; //48x9, x=21, y=11
	private Point initialSpawn;

    public StandingRobot(Point initialSpawn, int width, int height) {
        super(initialSpawn.x, initialSpawn.y, width, height, EnemyType.STANDING_ROBOT);
        this.x = initialSpawn.x - hbOffsetX;
    	this.y = initialSpawn.y - 32;
    	this.initialSpawn = new Point(x, y);
    	this.behavior = new StandingRobotBehavior();
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
        // Usa le dimensioni del tipo di nemico per l'hitbox
        hitbox = new Rectangle2D.Float(x + hbOffsetX - SCALE, y + hbOffsetY - SCALE, hbWidth, hbHeight);
    }
    
    private void initAttackBox() {
        // Usa le dimensioni del tipo di nemico per l'hitbox
        attackBox = new Rectangle2D.Float(x + 21*2, y + 11*2, 48*2, 9*2);
    }
    
    protected void updateHitbox() {
    	hitbox.x = this.x + hbOffsetX;
        hitbox.y = this.y + hbOffsetY;
        hitbox.width = hbWidth;
        hitbox.height = hbHeight;
    }
    
    private void updateAttackBox() {
    	if(isFacingRight()) {
    		attackBox.x = this.hitbox.x + hitbox.width - SCALE;
    		attackBox.y = this.hitbox.y;
    		attackBox.width = 48*2;
    		attackBox.height = 9*2;
    		
    		attackBox.setRect(attackBox.x, attackBox.y, attackBox.width, attackBox.height);
    	}
    	else {
    		attackBox.x = this.hitbox.x + SCALE;
    		attackBox.y = this.hitbox.y;
    		attackBox.width = -48*2;
    		attackBox.height = 9*2;
    		
			attackBox.setRect(attackBox.x + attackBox.width, attackBox.y, -attackBox.width, attackBox.height);
    	}
    }
    
    
    @Override
    public void setState(EnemyState newState) {
        this.currentState = newState;
    }
    
    @Override
    public void update(long currentTime, Level levelData, Player player) {
        behavior.update(this, currentTime, levelData, player);
        updateHitbox();
        updateAttackBox();
        setChanged();
        notifyObservers();
    }
    
    protected void turn() {
        isFacingRight = !isFacingRight;
        direction = isFacingRight ? Directions.RIGHT : Directions.LEFT;
        updateAttackBox();
    }
    
    @Override
    public long getDurationForState(EnemyState state) {
    	return behavior.getDurationForState(state);
    }
    
    @Override
    public long getStateStartTime() {
    	return behavior.getStateStartTime();
    }

    
    public Rectangle2D.Float getAttackBox() {
		return attackBox;
    	
    }

	@Override
	public void move() {
	}
}