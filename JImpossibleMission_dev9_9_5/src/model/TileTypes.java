package model;

/**
 * Enumerates the different types of tiles that can exist in a game level.
 * This is used by the {@link TileFactory} to create the correct tile instances.
 */
public enum TileTypes {
	EMPTY,
	WALL,
	PLATFORM,
	LIFT,
	PC,
	FURNITURE
}