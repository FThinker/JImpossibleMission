package model;

import static model.TileTypes.FURNITURE;
import static model.FurnitureType.randomType;

import java.awt.geom.Rectangle2D;

@SuppressWarnings("deprecation")
public class FurnitureTile extends Tile {
	private static final long serialVersionUID = 1L;
	
	private final FurnitureType type;
    private boolean hasPuzzlePiece = false;
    private boolean isSearching = false;
    private float searchProgress = 0; // 0.0 - 1.0 (0% - 100%)
    private final float searchTimeRequired = 3.0f;
    private boolean isVanished = false;
    
    public FurnitureTile(int x, int y) {
        super(x, y);
        this.type = randomType();
//        this.x = x + type.getOffsetX();
//        this.y = y + type.getOffsetY();
        solid = false;
        initHitbox();
    }

    @Override
    protected void initHitbox() {
        // Usa le dimensioni del tipo di arredamento per l'hitbox
        hitbox = new Rectangle2D.Float(x + type.getOffsetX(), y + type.getOffsetY(), type.getWidth(), type.getHeight());
    }
    
    // Getter per il tipo di arredamento
    public FurnitureType getFurnitureType() {
        return type;
    }
    
    public boolean hasPuzzlePiece() {
    	return hasPuzzlePiece;
    }
    
    public void setHasPuzzlePiece(boolean hasPuzzlePiece) {
    	this.hasPuzzlePiece = hasPuzzlePiece;
    }
    
    public boolean isSearching() {
        return isSearching;
    }

    public void setSearching(boolean searching) {
        isSearching = searching;
        if (!searching) {
            searchProgress = 0; // Resetta il progresso quando si smette di cercare
        }
    }

    public float getSearchProgress() {
        return searchProgress;
    }

    public float getSearchTimeRequired() {
        return searchTimeRequired;
    }
    
    // Metodo per aggiornare il progresso di ricerca
    public void updateSearchProgress(float delta) {
        if (isSearching) {
            searchProgress += delta;
            // Notifica gli observer (GameController/View) del progresso
            setChanged();
            notifyObservers();
        }
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
        return FURNITURE;
    }
    
    // NUOVO: Metodo per far sparire il mobile
    public void vanish() {
        this.isVanished = true;
        
        // Notifichiamo gli observer (la View) che il suo stato è cambiato
        setChanged();
        notifyObservers();
    }
    
    // NUOVO: Getter per lo stato di sparizione
    public boolean isVanished() {
        return isVanished;
    }

}
