package model;

/**
 * Enumerates the different types of enemies in the game.
 * Each enum constant holds configuration data specific to that enemy type,
 * such as its hitbox dimensions and offsets.
 */
public enum EnemyType {
    STANDING_ROBOT(14, 21, 9, 11), 
    MOVING_ROBOT(14, 21, 9, 11);

    private final int width;
    private final int height;
    private final int hbOffsetX;
    private final int hbOffsetY;

    /**
     * @param width The logical width of the enemy's hitbox.
     * @param height The logical height of the enemy's hitbox.
     * @param hbOffsetX The horizontal offset of the hitbox relative to the enemy's position.
     * @param hbOffsetY The vertical offset of the hitbox relative to the enemy's position.
     */
    EnemyType(int width, int height, int hbOffsetX, int hbOffsetY) {
        this.width = width;
        this.height = height;
		this.hbOffsetX = hbOffsetX;
		this.hbOffsetY = hbOffsetY;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
	public int getHbOffsetX() { return hbOffsetX; }
	public int getHbOffsetY() { return hbOffsetY; }
}