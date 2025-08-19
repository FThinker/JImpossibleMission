package model;

public final class TileFactory {
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
