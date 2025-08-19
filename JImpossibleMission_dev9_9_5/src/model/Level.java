package model;

import java.awt.Point; // Utile per le coordinate
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static model.TileTypes.*;

public class Level implements Serializable {
	private static final long serialVersionUID = 1L;

	private Tile[][] levelData; // Matrice 2D per i dati del livello
	private Point playerSpawn;
	private List<LiftTile> liftList = new ArrayList<>();
	private List<PcTile> pcList = new ArrayList<>();
	private List<FurnitureTile> furnitureList = new ArrayList<>();
	private List<Enemy> enemies = new ArrayList<>();

	private long freezeStartTime = 0;
	private long freezeDuration = 0;

	public Level(Tile[][] levelData, Point playerSpawn, List<Enemy> enemies) {
		this.levelData = levelData;
		this.playerSpawn = playerSpawn;
		this.enemies = enemies;
		initLifts();
		initPcs();
		initFurniture();
		spawnPuzzlePiece();
	}

	private void spawnPuzzlePiece() {
		if (furnitureList.size() > 0) {
			FurnitureTile randomFurniture = furnitureList.get(new Random().nextInt(furnitureList.size()));
			randomFurniture.setHasPuzzlePiece(true);
		}
	}

	private void initLifts() {
		for (int row = 0; row < levelData.length; row++)
			for (int col = 0; col < levelData[0].length; col++)
				if (levelData[row][col].getType() == LIFT)
					liftList.add((LiftTile) levelData[row][col]);
	}

	private void initPcs() {
		for (int row = 0; row < levelData.length; row++)
			for (int col = 0; col < levelData[0].length; col++)
				if (levelData[row][col].getType() == PC)
					pcList.add((PcTile) levelData[row][col]);
	}

	private void initFurniture() {
		for (int row = 0; row < levelData.length; row++)
			for (int col = 0; col < levelData[0].length; col++)
				if (levelData[row][col].getType() == FURNITURE)
					furnitureList.add((FurnitureTile) levelData[row][col]);
	}

	public Tile[][] getLevelData() {
		return levelData;
	}

	public Point getPlayerSpawn() {
		return playerSpawn;
	}

	public List<LiftTile> getLifts() {
		return liftList;
	}

	public List<PcTile> getPcs() {
		return pcList;
	}

	public List<FurnitureTile> getFurniture() {
		return furnitureList;
	}

	public List<Enemy> getEnemies() {
		return enemies;
	}
	
	/**
     * NUOVO METODO: Attiva il congelamento per questo specifico livello.
     */
    public void freezeEnemies(long durationSec) {
        this.freezeStartTime = System.currentTimeMillis();
        this.freezeDuration = durationSec * 1000; // Converti in millisecondi
    }
    
    /**
     * NUOVO METODO: Controlla se i nemici in questo livello sono attualmente congelati.
     */
    public boolean areEnemiesFrozen() {
        if (freezeStartTime == 0) return false; // Mai stato congelato
        return (System.currentTimeMillis() - freezeStartTime) < freezeDuration;
    }
}