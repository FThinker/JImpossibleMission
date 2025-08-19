package view;

import model.FurnitureTile;
import model.Level;
import model.Tile;
import model.TileTypes;
import controller.AssetLoader;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static model.GameConstants.*;
import static model.TileTypes.*;

public class LevelView {
    // ✅ CAMBIAMENTO FONDAMENTALE #4: Rimuoviamo il riferimento al modello del livello.
    // private Level level; 
    
	private BufferedImage wallTileImg;
	private BufferedImage platformTileImg;
	private BufferedImage liftTileImg;
	private BufferedImage pcTileImg;
	
	public LevelView() {
        // Il costruttore ora carica solo le immagini, non ha bisogno del modello.
		loadTileImages(); 
	}

	private void loadTileImages() {
		wallTileImg = AssetLoader.getInstance().getImage("wallTile");
		platformTileImg = AssetLoader.getInstance().getImage("platformTile");
		liftTileImg = AssetLoader.getInstance().getImage("liftTile");
		pcTileImg = AssetLoader.getInstance().getImage("pcTile");
        // ... controlli null ...
	}

	/**
	 * Disegna il livello sulla superficie grafica.
	 * @param g Il contesto grafico su cui disegnare.
     * @param level L'oggetto Level da disegnare (passato al momento del disegno).
	 */
	public void draw(Graphics g, Level level) { // ✅ Il livello ora è un parametro
		if (level == null)
			return;

		Tile[][] levelData = level.getLevelData();

		for (int row = 0; row < levelData.length; row++) {
			for (int col = 0; col < levelData[0].length; col++) {
				Tile tile = levelData[row][col];
				
                if (tile.getType() == FURNITURE) {
                    FurnitureTile fTile = (FurnitureTile) tile;
                    if (fTile.isVanished()) {
                        continue;
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
					break;
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
				} else if (tile.getType() != EMPTY) {
					g.setColor(Color.MAGENTA);
					g.fillRect(col * TILES_SIZE, row * TILES_SIZE, TILES_SIZE, TILES_SIZE);
				}
			}
		}
	}
	
    // ... drawHitbox non cambia ...
	private void drawHitbox(Graphics g, Rectangle2D.Float hitbox, Color c) {
		Graphics2D g2d = (Graphics2D) g;
		int width = (int) (hitbox.getMaxX() - hitbox.getMinX());
		int height = (int) (hitbox.getMaxY() - hitbox.getMinY());
		g2d.setColor(c); // Colore per la hitbox
		g2d.setStroke(new BasicStroke(1)); // Linea sottile
		g2d.drawRect((int) (hitbox.getMinX() * SCALE), (int) (hitbox.getMinY() * SCALE), (int) (width * SCALE),
				(int) (height * SCALE));
		g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 50)); // Colore semitrasparente per riempire
		g2d.fillRect((int) (hitbox.getMinX() * SCALE), (int) (hitbox.getMinY() * SCALE), (int) (width * SCALE),
				(int) (height * SCALE));
	}
}