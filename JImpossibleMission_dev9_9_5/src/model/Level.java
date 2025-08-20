package model;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static model.TileTypes.*;

/**
 * Represents a single game level.
 * This class holds the 2D tile map, the player's spawn point, and lists of
 * all entities present in the level, such as enemies, lifts, and furniture.
 */
public class Level implements Serializable {
	private static final long serialVersionUID = 1L;

	private Tile[][] levelData;
	private Point playerSpawn;
	private List<LiftTile> liftList = new ArrayList<>();
	private List<PcTile> pcList = new ArrayList<>();
	private List<FurnitureTile> furnitureList = new ArrayList<>();
	private List<Enemy> enemies = new ArrayList<>();

	private long freezeStartTime = 0;
	private long freezeDuration = 0;

	/**
     * Constructs a new Level.
     *
     * @param levelData   The 2D array of tiles that form the level's structure.
     * @param playerSpawn The starting coordinates for the player in this level.
     * @param enemies     A list of enemies present in this level.
     */
	public Level(Tile[][] levelData, Point playerSpawn, List<Enemy> enemies) {
		this.levelData = levelData;
		this.playerSpawn = playerSpawn;
		this.enemies = enemies;
		initLifts();
		initPcs();
		initFurniture();
		spawnPuzzlePiece();
	}

	/**
     * Randomly places a single puzzle piece inside one of the furniture items in the level.
     */
	private void spawnPuzzlePiece() {
		if (furnitureList.size() > 0) {
			FurnitureTile randomFurniture = furnitureList.get(new Random().nextInt(furnitureList.size()));
			randomFurniture.setHasPuzzlePiece(true);
		}
	}

	/**
     * Scans the level data to find and cache all LiftTile objects.
     */
	private void initLifts() {
		for (int row = 0; row < levelData.length; row++)
			for (int col = 0; col < levelData[0].length; col++)
				if (levelData[row][col].getType() == LIFT)
					liftList.add((LiftTile) levelData[row][col]);
	}

	/**
     * Scans the level data to find and cache all PcTile objects.
     */
	private void initPcs() {
		for (int row = 0; row < levelData.length; row++)
			for (int col = 0; col < levelData[0].length; col++)
				if (levelData[row][col].getType() == PC)
					pcList.add((PcTile) levelData[row][col]);
	}

	/**
     * Scans the level data to find and cache all FurnitureTile objects.
     */
	private void initFurniture() {
		for (int row = 0; row < levelData.length; row++)
			for (int col = 0; col < levelData[0].length; col++)
				if (levelData[row][col].getType() == FURNITURE)
					furnitureList.add((FurnitureTile) levelData[row][col]);
	}
    
    /**
     * Activates the "frozen" effect for all enemies in this level.
     * @param durationSec The duration of the freeze in seconds.
     */
    public void freezeEnemies(long durationSec) {
        this.freezeStartTime = System.currentTimeMillis();
        this.freezeDuration = durationSec * 1000;
    }
    
    /**
     * Checks if the enemies in this level are currently under the "frozen" effect.
     * @return True if enemies are frozen, false otherwise.
     */
    public boolean areEnemiesFrozen() {
        if (freezeStartTime == 0) return false;
        return (System.currentTimeMillis() - freezeStartTime) < freezeDuration;
    }

    // --- GETTERS ---
	
	public Tile[][] getLevelData() { return levelData; }
	public Point getPlayerSpawn() { return playerSpawn; }
	public List<LiftTile> getLifts() { return liftList; }
	public List<PcTile> getPcs() { return pcList; }
	public List<FurnitureTile> getFurniture() { return furnitureList; }
	public List<Enemy> getEnemies() { return enemies; }
}