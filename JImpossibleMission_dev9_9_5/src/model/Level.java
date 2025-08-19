package model;

import java.awt.Point; // Utile per le coordinate
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static model.TileTypes.*;

public class Level implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Tile[][] levelData; // Matrice 2D per i dati del livello
	private Point playerSpawn;
	private List<LiftTile> liftList = new ArrayList<>();
	private List<PcTile> pcList = new ArrayList<>();
	private List<FurnitureTile> furnitureList = new ArrayList<>();
	private List<Enemy> enemies = new ArrayList<>();

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
}