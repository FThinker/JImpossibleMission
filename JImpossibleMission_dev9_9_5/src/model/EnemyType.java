// EnemyType.java (Nuovo file nel package model)
package model;

public enum EnemyType {
    STANDING_ROBOT(14, 21, 9, 11), 
    MOVING_ROBOT(14, 21, 9, 11), // Larghezza, Altezza, hitboxOffsetX, hitboxOffsetY
	WALTZING_ROBOT(14, 21, 9, 11);

    private final int width;
    private final int height;
    private final int hbOffsetX;
    private final int hbOffsetY;

    EnemyType(int width, int height, int hbOffsetX, int hbOffsetY) {
        this.width = width;
        this.height = height;
		this.hbOffsetX = hbOffsetX;
		this.hbOffsetY = hbOffsetY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

	public int getHbOffsetX() {
		return hbOffsetX;
	}

	public int getHbOffsetY() {
		return hbOffsetY;
	}
}