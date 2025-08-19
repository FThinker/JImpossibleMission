package controller;

import model.Enemy;
import model.EnemyFactory;
import model.EnemyType;
import model.Level;
import model.Tile;
import model.TileFactory;
import model.TileTypes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import static model.GameConstants.*;
import static model.TileTypes.*;

public class LevelLoader {

    public static Level loadLevel(String levelFilePath) {
        List<String> rawLevelLines = new ArrayList<>();
        Point playerSpawn = null;
        List<Enemy> enemies = new ArrayList<>();
        int maxCols = 0; // Per tenere traccia della larghezza massima

        System.out.println("Attempting to load level from: " + levelFilePath);

        try (InputStream is = LevelLoader.class.getResourceAsStream(levelFilePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            if (is == null) {
                System.err.println("Error: Level file not found at " + levelFilePath);
                return null;
            }

            String line;
            int currentRow = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // Ignora le righe vuote
                }

                // Cerca il punto di spawn del player ('P')
                int playerCharIndex = line.indexOf('P');
                if (playerCharIndex != -1) {
                    // Imposta il punto di spawn in coordinate pixel
//                	System.out.println("Found player spawn at: " + playerCharIndex + " | " + currentRow);
                	int initialX = (playerCharIndex * TILES_DEFAULT_SIZE);
                	int centerOffsetX = (TILES_DEFAULT_SIZE - 12*2) / 2;
                	int hbOffsetX = 10 * 2;
//                    playerSpawn = new Point(centerX + offsetX, currentRow * TILES_DEFAULT_SIZE - 32);
                	playerSpawn = new Point(initialX - hbOffsetX + centerOffsetX, currentRow * TILES_DEFAULT_SIZE - 32);
                    // Rimuovi 'P' dalla riga per la successiva elaborazione dei tile
                    line = line.replace('P', ' ');
                }

                rawLevelLines.add(line);
                maxCols = Math.max(maxCols, line.length()); // Aggiorna la larghezza massima
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
        // Usa maxCols per assicurare che la matrice abbia la larghezza massima trovata
        int numCols = maxCols;
        Tile[][] levelData = new Tile[numRows][numCols];

        // Converte i caratteri letti in valori numerici per la matrice del livello
        for (int r = 0; r < numRows; r++) {
            String line = rawLevelLines.get(r);
            for (int c = 0; c < numCols; c++) {
                if (c < line.length()) { // Assicurati di non andare fuori dai limiti della riga attuale
                    char tileChar = line.charAt(c);
                    switch (tileChar) {
                        case '#': // Wall
                            levelData[r][c] = TileFactory.getTile(WALL, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                            break;
                        case '=': // Platform
                            levelData[r][c] = TileFactory.getTile(PLATFORM, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                            break;
                        case 'L': // Lift
                        	levelData[r][c] =TileFactory.getTile(LIFT, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                        	break;
                        case 'E':
                        case ' ': // Empty tile
                            levelData[r][c] = TileFactory.getTile(EMPTY, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                            break;
                        case 'T': // PC
                        	levelData[r][c] = TileFactory.getTile(PC, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                        	break;
                        case 'O': // Furniture
                        	levelData[r][c] = TileFactory.getTile(FURNITURE, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                        	break;
                        case 'R': // Robot
                        	levelData[r][c] = TileFactory.getTile(EMPTY, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                        	// Centering Enemy
                        	int x = (c * TILES_DEFAULT_SIZE);
                        	int centerOffset = (TILES_DEFAULT_SIZE - 14*2) / 2;
                        	enemies.add(EnemyFactory.createEnemy(EnemyType.STANDING_ROBOT, x + centerOffset, r * TILES_DEFAULT_SIZE));
                        	break;
                        case 'M': // Moving Robot
                        	levelData[r][c] = TileFactory.getTile(EMPTY, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                        	// Centering Enemy
                        	int xM = (c * TILES_DEFAULT_SIZE);
                        	int centerOffsetM = (TILES_DEFAULT_SIZE - 14*2) / 2;
                        	enemies.add(EnemyFactory.createEnemy(EnemyType.MOVING_ROBOT, xM + centerOffsetM, r * TILES_DEFAULT_SIZE));
                        	break;
                        default:
                            System.err.println("Unknown character in level file: '" + tileChar + "' at row " + r + ", col " + c + ". Treating as EMPTY.");
                            levelData[r][c] = TileFactory.getTile(EMPTY, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE); // Fallback to empty for unknown characters
                            break;
                    }
                } else {
                    // Se la riga è più corta di maxCols, riempi con EMPTY
                    levelData[r][c] = TileFactory.getTile(EMPTY, c * TILES_DEFAULT_SIZE, r * TILES_DEFAULT_SIZE);
                }
            }
        }

        // Se il punto di spawn non è stato trovato, usa un default o segnala un errore critico
        if (playerSpawn == null) {
            System.err.println("Warning: Player spawn point 'P' not found in level file. Using default (0,0).");
            playerSpawn = new Point(0, 0); // Default spawn
        }
        
        if (enemies.size() <= 0) {
        	System.err.println("Warning: No enemies found in level file.");
        }

        // Crea e restituisci il nuovo oggetto Level
        return new Level(levelData, playerSpawn, enemies);
    }
}