package controller;

import model.Enemy;
import model.EnemyFactory;
import model.EnemyType;
import model.Level;
import model.Tile;
import model.TileFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import static model.GameConstants.*;
import static model.TileTypes.*;

/**
 * A utility class for loading game levels from text files.
 * It parses a character-based representation of a level to create a {@link Level} object,
 * populating it with tiles, enemies, and the player's spawn point.
 */
public class LevelLoader {

    /**
     * Loads a level from a specified resource file path.
     * The file should contain a grid of characters, where each character represents a
     * different type of tile or entity (e.g., '#' for wall, 'P' for player spawn).
     *
     * @param levelFilePath The resource path to the level's text file.
     * @return A new {@link Level} object, or null if the file cannot be read.
     */
    public static Level loadLevel(String levelFilePath) {
        List<String> rawLevelLines = new ArrayList<>();
        Point playerSpawn = null;
        List<Enemy> enemies = new ArrayList<>();
        int maxCols = 0;

        try (InputStream is = LevelLoader.class.getResourceAsStream(levelFilePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            if (is == null) {
                System.err.println("Error: Level file not found at " + levelFilePath);
                return null;
            }

            String line;
            int currentRow = 0;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue; // Ignore empty lines
                }

                // Find the player spawn point ('P')
                int playerCharIndex = line.indexOf('P');
                if (playerCharIndex != -1) {
                	int initialX = (playerCharIndex * TILES_DEFAULT_SIZE);
                	int centerOffsetX = (TILES_DEFAULT_SIZE - 12*2) / 2;
                	int hbOffsetX = 10 * 2;
                	playerSpawn = new Point(initialX - hbOffsetX + centerOffsetX, currentRow * TILES_DEFAULT_SIZE - 32);
                    // Replace 'P' with a space to process it as an empty tile
                    line = line.replace('P', ' ');
                }

                rawLevelLines.add(line);
                maxCols = Math.max(maxCols, line.length());
                currentRow++;
            }

        } catch (IOException e) {
            System.err.println("Failed to read level file: " + levelFilePath);
            e.printStackTrace();
            return null;
        }

        if (rawLevelLines.isEmpty()) {
            System.err.println("Level file is empty or contains no valid data: " + levelFilePath);
            return null;
        }

        int numRows = rawLevelLines.size();
        int numCols = maxCols;
        Tile[][] levelData = new Tile[numRows][numCols];

        // Convert the character grid into a 2D array of Tile objects
        for (int r = 0; r < numRows; r++) {
            String line = rawLevelLines.get(r);
            for (int c = 0; c < numCols; c++) {
                if (c < line.length()) {
                    char tileChar = line.charAt(c);
                    switch (tileChar) {
                        case '#':
                            levelData[r][c] = TileFactory.getTile(WALL, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                            break;
                        case '=':
                            levelData[r][c] = TileFactory.getTile(PLATFORM, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                            break;
                        case 'L':
                        	levelData[r][c] =TileFactory.getTile(LIFT, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                        	break;
                        case 'E': // 'E' is used as a redundant character for empty space in level doors
                        case ' ':
                            levelData[r][c] = TileFactory.getTile(EMPTY, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                            break;
                        case 'T':
                        	levelData[r][c] = TileFactory.getTile(PC, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                        	break;
                        case 'O':
                        	levelData[r][c] = TileFactory.getTile(FURNITURE, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                        	break;
                        case 'R': // Standing Robot
                        	levelData[r][c] = TileFactory.getTile(EMPTY, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                        	int x = (c * TILES_DEFAULT_SIZE);
                        	int centerOffset = (TILES_DEFAULT_SIZE - 14*2) / 2;
                        	enemies.add(EnemyFactory.createEnemy(EnemyType.STANDING_ROBOT, x + centerOffset, r * TILES_DEFAULT_SIZE));
                        	break;
                        case 'M': // Moving Robot
                        	levelData[r][c] = TileFactory.getTile(EMPTY, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                        	int xM = (c * TILES_DEFAULT_SIZE);
                        	int centerOffsetM = (TILES_DEFAULT_SIZE - 14*2) / 2;
                        	enemies.add(EnemyFactory.createEnemy(EnemyType.MOVING_ROBOT, xM + centerOffsetM, r * TILES_DEFAULT_SIZE));
                        	break;
                        default:
                            System.err.println("Unknown character in level file: '" + tileChar + "' at row " + r + ", col " + c + ". Treating as EMPTY.");
                            levelData[r][c] = TileFactory.getTile(EMPTY, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                            break;
                    }
                } else {
                    // If the line is shorter than maxCols, fill the rest with empty tiles
                    levelData[r][c] = TileFactory.getTile(EMPTY, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                }
            }
        }

        if (playerSpawn == null) {
            System.err.println("Warning: Player spawn point 'P' not found in level file. Using default (0,0).");
            playerSpawn = new Point(0, 0);
        }
        
        if (enemies.isEmpty()) {
        	System.err.println("Warning: No enemies found in level file.");
        }

        return new Level(levelData, playerSpawn, enemies);
    }
}