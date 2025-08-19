// Enemy.java (Nuovo file nel package model)
package model;

import java.awt.Point;
import java.awt.geom.Rectangle2D;

public abstract class Enemy extends Entity {
	private static final long serialVersionUID = 1L;
	protected EnemyType enemyType;
    protected int aniTick, aniIndex;
    protected Directions direction = Directions.RIGHT; // Direzione di default
    protected EnemyState currentState = EnemyState.IDLE;
    
    // Nuove proprietà per il comportamento
    protected boolean isFacingRight = true;
    protected long lastDirectionChangeTime;
    
    protected EnemyBehavior behavior;
    
    protected int speed = 0;

    public Enemy(int x, int y, int width, int height, EnemyType type) {
        super(new Point(x, y));
        this.width = width;
        this.height = height;
        this.enemyType = type;
        this.lastDirectionChangeTime = System.currentTimeMillis();
    }
    
    // Metodo astratto per aggiornare lo stato del nemico
    public abstract void update(long currentTime, Level levelData, Player player);
    
    public abstract void move();
    
    // Metodi getter per la vista
    public int getAniIndex() {
        return aniIndex;
    }

    public Directions getDirection() {
        return direction;
    }
    
    public EnemyType getEnemyType() {
        return enemyType;
    }
    
    public boolean isFacingRight() {
        return isFacingRight;
    }
    
    protected abstract void turn();
    
    public EnemyState getState() {
    	return currentState;
    }
    
    public int getSpeed() {
    	return speed;
    }
    
    public abstract void resetPosition();
    
    protected abstract void setState(EnemyState state);
    
	public abstract Rectangle2D.Float getAttackBox();
	
	public abstract long getDurationForState(EnemyState state);
	
	public abstract long getStateStartTime();
}