package view;

import model.FurnitureTile;
import model.Level;
import model.Tile;
import model.TileTypes;
import controller.AssetLoader;
import java.awt.*;
import java.awt.image.BufferedImage;
import static model.GameConstants.*;
import static model.TileTypes.*;

/**
 * Renders the game level, including all its tiles (walls, platforms, furniture, etc.).
 * This class iterates through the level data provided by the {@link GameModel} and draws
 * the corresponding sprite for each tile.
 */
public class LevelView {
    
	private BufferedImage wallTileImg;
	private BufferedImage platformTileImg;
	private BufferedImage liftTileImg;
	private BufferedImage pcTileImg;
	
	/**
     * Constructs a LevelView and pre-loads all necessary tile images.
     */
	public LevelView() {
		loadTileImages(); 
	}

	/**
     * Loads tile sprites from the {@link AssetLoader}.
     */
	private void loadTileImages() {
		wallTileImg = AssetLoader.getInstance().getImage("wallTile");
		platformTileImg = AssetLoader.getInstance().getImage("platformTile");
		liftTileImg = AssetLoader.getInstance().getImage("liftTile");
		pcTileImg = AssetLoader.getInstance().getImage("pcTile");
	}

	/**
	 * Draws the entire level onto the screen.
	 *
	 * @param g     The Graphics context to draw on.
     * @param level The current {@link Level} object to be rendered.
	 */
	public void draw(Graphics g, Level level) {
		if (level == null)
			return;

		Tile[][] levelData = level.getLevelData();

		for (int row = 0; row < levelData.length; row++) {
			for (int col = 0; col < levelData[0].length; col++) {
				Tile tile = levelData[row][col];
				
                if (tile.getType() == FURNITURE) {
                    FurnitureTile fTile = (FurnitureTile) tile;
                    if (fTile.isVanished()) {
                        continue; // Skip rendering vanished furniture
                    }
                }
				
				BufferedImage tileImage = null;

				switch (tile.getType()) {
				case WALL:
					tileImage = wallTileImg;
					break;
				case PLATFORM:
					tileImage = platformTileImg;
					break;
				case LIFT:
					tileImage = liftTileImg;
					break;
				case PC:
					tileImage = pcTileImg;
					break;
				case FURNITURE:
					FurnitureTile fTile = (FurnitureTile) tile;
					tileImage = AssetLoader.getInstance()
							.getImage(fTile.getFurnitureType().name().toLowerCase() + "Tile");
					break;
				case EMPTY:
				default:
					break;
				}

				if (tileImage != null) {
					if (tile.getType() == TileTypes.LIFT)
						g.drawImage(tileImage, (int) (col * TILES_SIZE), (int) (tile.getHitbox().y * SCALE), TILES_SIZE, TILES_SIZE, null);
					else if (tile.getType() == TileTypes.PC || tile instanceof FurnitureTile)
						g.drawImage(tileImage, (int) (tile.getHitbox().x * SCALE), (int) (tile.getHitbox().y * SCALE),
								(int) (tile.getHitbox().width * SCALE), (int) (tile.getHitbox().height * SCALE), null);
					else
						g.drawImage(tileImage, (int) (col * TILES_SIZE), (int) (row * TILES_SIZE), TILES_SIZE, TILES_SIZE, null);
				}
			}
		}
	}
}