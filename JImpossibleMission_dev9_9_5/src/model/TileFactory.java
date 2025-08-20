package model;

/**
 * A factory class for creating different types of tiles.
 * This class encapsulates the instantiation logic for all concrete {@link Tile} subclasses,
 * providing a single point of creation based on a {@link TileTypes} enum.
 */
public final class TileFactory {
	
	/**
	 * Creates and returns a tile of the specified type at the given coordinates.
	 * * @param tileType The type of tile to create, as defined in the {@link TileTypes} enum.
	 * @param x The x-coordinate for the new tile.
	 * @param y The y-coordinate for the new tile.
	 * @return A new {@link Tile} instance of the specified type.
	 * @throws IllegalArgumentException if the provided tileType is not a valid enum constant.
	 */
	public static Tile getTile(final TileTypes tileType, int x, int y) {
		return switch(tileType) {
		case EMPTY -> new EmptyTile(x, y);
		case WALL -> new WallTile(x, y);
		case PLATFORM -> new PlatformTile(x, y);
		case LIFT -> new LiftTile(x, y);
		case PC -> new PcTile(x, y);
		case FURNITURE -> new FurnitureTile(x, y);
		default -> throw new IllegalArgumentException("No such tileType found!");
		};
	}
}