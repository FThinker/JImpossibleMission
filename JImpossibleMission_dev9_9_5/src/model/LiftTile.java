package model;

import static model.TileTypes.LIFT;

import java.awt.geom.Rectangle2D;

@SuppressWarnings("deprecation")
public class LiftTile extends Tile {
	private static final long serialVersionUID = 1L;
	
	private final int speed = 2;
    private boolean moving = false;
    private boolean movingUp = false;
    private boolean movingDown = false;
    private final int originalY; // Store original position
    
    // Nuove variabili per la gestione del movimento a destinazione
    private int targetY = -1; // -1 indica nessuna destinazione impostata
    private Directions currentMovementDirection = null; // Direzione attuale di movimento dell'ascensore

	public LiftTile(int x, int y) {
		super(x, y);
		solid = true;
		originalY = y;
		initHitbox();
	}
	
	public boolean isMoving() {
        return moving;
    }
    
    public boolean isMovingUp() {
        return movingUp;
    }
    
    public boolean isMovingDown() {
        return movingDown;
    }
    
    public int getOriginalY() {
    	return originalY;
    }

    // Nuovo getter per targetY
    public int getTargetY() {
        return targetY;
    }

    // Nuovo setter per targetY
    public void setTargetY(int targetY) {
        this.targetY = targetY;
        this.moving = true; // L'ascensore inizia a muoversi verso la destinazione
        setChanged();
        notifyObservers();
    }
    
    public void setCurrentMovementDirection(Directions direction) {
        this.currentMovementDirection = direction;
    }

    public Directions getCurrentMovementDirection() {
        return currentMovementDirection;
    }

	@Override
	protected void initHitbox() {
		hitbox = new Rectangle2D.Float(x, y, width, (height / 2) + 1);
		
	}
	
	private void updateHitbox() {
        hitbox.x = this.x;
        hitbox.y = this.y;
    }
	
	public void stop() {
        moving = false;
        movingUp = false;
        movingDown = false;
        targetY = -1; // Resetta la destinazione quando si ferma
        currentMovementDirection = null; // Resetta la direzione di movimento
        updateHitbox();
        setChanged();
        notifyObservers();
    }
    
    public void startMovingUp() {
        moving = true;
        movingUp = true;
        movingDown = false;
    }
    
    public void startMovingDown() {
        moving = true;
        movingDown = true;
        movingUp = false;
    }
    
    public void moveUp() {
        y -= getSpeed();
        updateHitbox();
        setChanged();
        notifyObservers();
    }
    
    public void moveDown() {
        y += getSpeed();
        updateHitbox();
        setChanged();
        notifyObservers();
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
		return LIFT;
	}
	
	public void resetPosition() {
        y = originalY;
        updateHitbox();
        setChanged();
        notifyObservers();
    }

	public int getSpeed() {
		return speed;
	}

}