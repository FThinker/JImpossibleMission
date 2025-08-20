package model;

/**
 * An abstract class that holds global, static constants for the game.
 * This centralizes key configuration values like frame rate, scaling factors,
 * and physics constants to make them easily accessible and modifiable.
 */
public abstract class GameConstants {
    
	public final static int FPS_SET = 120;
	
	public final static int TILES_DEFAULT_SIZE = 32;
	public final static float SCALE = 2f;
	public final static int TILES_IN_WIDTH = 19;
	public final static int TILES_IN_HEIGHT = 14;
	public final static int LOGIC_WIDTH = TILES_DEFAULT_SIZE * TILES_IN_WIDTH;
	public final static int LOGIC_HEIGHT = TILES_DEFAULT_SIZE * TILES_IN_HEIGHT;
	public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
	public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
	public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;
	
	public final static float GRAVITY = 0.045f;
}