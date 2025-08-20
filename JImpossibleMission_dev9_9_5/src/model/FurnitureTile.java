package model;

import static model.TileTypes.FURNITURE;
import static model.FurnitureType.randomType;
import java.awt.geom.Rectangle2D;

/**
 * Represents an interactable piece of furniture in the game world.
 * Furniture can be searched by the player, may contain a puzzle piece,
 * and can "vanish" after being searched.
 */
@SuppressWarnings("deprecation")
public class FurnitureTile extends Tile {
	private static final long serialVersionUID = 1L;
	
	private final FurnitureType type;
    private boolean hasPuzzlePiece = false;
    private boolean isSearching = false;
    private float searchProgress = 0; // Progress from 0.0 to 1.0
    private final float searchTimeRequired = 3.0f; // Time in seconds
    private boolean isVanished = false;
    
    /**
     * Constructs a FurnitureTile at a given position with a random furniture type.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     */
    public FurnitureTile(int x, int y) {
        super(x, y);
        this.type = randomType();
        solid = false;
        initHitbox();
    }

    @Override
    protected void initHitbox() {
        hitbox = new Rectangle2D.Float(x + type.getOffsetX(), y + type.getOffsetY(), type.getWidth(), type.getHeight());
    }
    
    /**
     * Updates the search progress over time.
     * @param delta The time elapsed since the last frame, in seconds.
     */
    public void updateSearchProgress(float delta) {
        if (isSearching) {
            searchProgress += delta;
            setChanged();
            notifyObservers();
        }
    }
    
    /**
     * Makes the furniture tile disappear from the game.
     */
    public void vanish() {
        this.isVanished = true;
        setChanged();
        notifyObservers();
    }
    
    // --- GETTERS AND SETTERS ---

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
            searchProgress = 0; // Reset progress when searching stops
        }
    }

    public float getSearchProgress() {
        return searchProgress;
    }

    public float getSearchTimeRequired() {
        return searchTimeRequired;
    }

    public boolean isVanished() {
        return isVanished;
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
}