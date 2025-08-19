package model;

import java.awt.Point;
import java.awt.geom.Rectangle2D;

import static model.Directions.*;

@SuppressWarnings("deprecation")
public class Player extends Entity {
	private static final long serialVersionUID = 1L;
	private final int speed = 2; // Velocità di movimento in unità logiche
    private PlayerState currentState;
    
    private int hitboxOffsetX = 10 * 2;
    private int hitboxOffsetY = 4 * 2;
    private int hitboxWidth = 12 * 2;
    private int hitboxHeight = 28 * 2;
    private Directions facingDirection = RIGHT;
    
 // Nuove variabili per l'hitbox in salto/caduta
    private int jumpHitboxOffsetX = 10 * 2; // Potresti volerlo uguale a quello normale o leggermente diverso
    private int jumpHitboxOffsetY = 11 * 2; // Sposta l'hitbox più in basso (dal lato superiore)
    private int jumpHitboxWidth = 12 * 2;   // Larghezza potrebbe rimanere la stessa
    private int jumpHitboxHeight = 12 * 2;  // Riduci l'altezza per renderla più piccola sotto e sopra
    
    private float yVelocity = 0;
    private boolean inAir = true;
    private static final float JUMP_FORCE = -1.7f; //-2.2faaaaa
    private static final float MAX_FALL_SPEED = 15f;
    
    private Point initialSpawn;

    public Player(Point initialSpawn) {
    	super(initialSpawn);
    	this.x = initialSpawn.x; //- hitboxOffsetX;
    	this.y = initialSpawn.y; //- 32;
    	setInitialSpawn(new Point(x, y));
    	this.width = 32 * 2;
    	this.height = 32 * 2;
        this.currentState = PlayerState.IDLE;
        
        initHitbox();
    }
    
    public Point getInitialSpawn() {
    	return this.initialSpawn;
    }
    
    public void setInitialSpawn(Point initialSpawn) {
    	this.initialSpawn = initialSpawn;
    }
    
    public void resetPosition() {
    	this.x = initialSpawn.x;
    	this.y = initialSpawn.y;
    	updateHitbox();
    	setChanged();
    	notifyObservers();
    }
    
    public int getSpeed() {
    	return speed;
    }
    
    public PlayerState getCurrentState() {
    	return currentState;
    }
    
    public void setDirection(Directions dir) {
    	this.facingDirection = dir;
    }
    
    public Directions getDirection() {
    	return this.facingDirection;
    }
    
    // Metodo per inizializzare o aggiornare l'hitbox
    @Override
    protected void initHitbox() { //float x, float y, int width, int height
        hitbox = new Rectangle2D.Float(x + hitboxOffsetX, y + hitboxOffsetY, hitboxWidth, hitboxHeight);
    }
    
    // Metodo per aggiornare la posizione dell'hitbox quando l'entità si muove
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
    
    // Getter per l'hitbox
    @Override
    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }
    
    // Metodo per cambiare lo stato del player e notificare gli observer
    private void setState(PlayerState newState) {
        if (this.currentState != newState) {
            this.currentState = newState;
            updateHitbox();
            setChanged();
            notifyObservers(); // Notifica ogni volta che lo stato cambia
        }
    }

 // Metodi di movimento aggiornati per gestire lo stato
    public void moveUp() {
        y -= speed;
        setState(PlayerState.JUMPING); // Se ci muoviamo, lo stato è RUNNING
        updateHitbox();
        setChanged(); // Notifica anche per l'aggiornamento della posizione
        notifyObservers();
    }

	public void moveDown() {
        y += speed;
        setState(PlayerState.FALLING);
        updateHitbox();
        setChanged();
        notifyObservers();
    }

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

    // Nuovo metodo per impostare lo stato su idle
    public void setIdle() {
    	if(!inAir)
    		setState(PlayerState.IDLE);
    }
    
    public void setSearching() {
    	setState(PlayerState.SEARCHING);
    }
    
 // Add gravity application method
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

    // Add jump method
    public void jump() {
        if (!inAir) {
            yVelocity = JUMP_FORCE;
            inAir = true;
            setState(PlayerState.JUMPING);
        }
    }
    
    // Add methods to handle ground contact
    public void landOnGround(float groundY) {
        y = (int) (groundY - height);
        yVelocity = 0;
        inAir = false;
        updateHitbox();
        setChanged();
        notifyObservers();
    }
    
    public void moveWithLift(int deltaY) {
        this.y += deltaY;
        updateHitbox();
        setChanged();
        notifyObservers();
    }

    public boolean isInAir() {
        return inAir;
    }

    public float getYVelocity() {
        return yVelocity;
    }

	public void setYVelocity(float velocity) {
		this.yVelocity = velocity;
	}

	public void setY(float y) {
		this.y = (int) y;
	}

	public void setInAir(boolean inAir) {
		this.inAir = inAir;
	}

	public void teleport(int x, int y) {
		this.x = x;
		this.y = y;
		updateHitbox();
	}
}