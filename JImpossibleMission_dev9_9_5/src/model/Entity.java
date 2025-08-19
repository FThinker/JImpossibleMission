package model;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Observable;

import static model.GameConstants.*;

@SuppressWarnings("deprecation")
public abstract class Entity extends Observable implements Serializable {
	private static final long serialVersionUID = 1L;
	protected int x, y; // Usa float per maggiore precisione nelle posizioni
    protected int width, height;
    protected Rectangle2D.Float hitbox; // Dichiarazione dell'hitbox

    public Entity(Point initialSpawn) { //int width, int height
        this.x = initialSpawn.x;
        this.y = initialSpawn.y;
        this.width = 32 * (int)SCALE;
        this.height = 32 * (int)SCALE;
    }

    // Metodo per inizializzare o aggiornare l'hitbox
    protected abstract void initHitbox();

    // Metodo per aggiornare la posizione dell'hitbox quando l'entità si muove
    protected void updateHitbox() {
        hitbox.x = x;
        hitbox.y = y;
    }
    
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

    // Getter per l'hitbox
    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }
}