package model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum FurnitureType {
//	JUKEBOX(32 * 2, 32 * 2, -8 * 2, -32),
	JUKEBOX(32 * 2.5, 32 * 2, -32*(2.5 / 4), -32),
//	CIGARETTE(32 * 2.5, 32 * 2, -8 * 2, -32);
	CIGARETTE(32 * 2.5, 32 * 2, -32*(2.5 / 4), -32),
    LIBRARY(32 * 2.5, 32 * 2, -32*(2.5/ 4), -32),
    CANDY(32 * 2.5, 32 * 2, -32*(2.5 / 4), - 32),
    DESK(32 * 2.5, 32 * 2, -32*(2.5 / 4), -32);
//    SOFA(96, 32, 0, 0);

    private final int width;
    private final int height;
    private final int offsetX;
    private final int offsetY;

    FurnitureType(double width, double height, double offsetX, double offsetY) {
        this.width = (int)width;
        this.height = (int)height;
        this.offsetX = (int)offsetX;
        this.offsetY = (int)offsetY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public int getOffsetX() {
		return offsetX;
	}
    
    public int getOffsetY() {
		return offsetY;
	}
    
    private static final List<FurnitureType> TYPES = Collections.unmodifiableList(Arrays.asList(values()));
    private static Random rnd = new Random();
    private static final int SIZE = TYPES.size();
    
    public static FurnitureType randomType() {
    	return TYPES.get(rnd.nextInt(SIZE));
    }
}
