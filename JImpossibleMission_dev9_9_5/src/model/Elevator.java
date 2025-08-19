package model;

import java.util.Observable;

@SuppressWarnings("deprecation")
public class Elevator extends Observable {

    private int currentFloor;
    private final int totalFloors;
    private float yPosition; 
    private boolean isMoving;
    
    private int spawnOffset = 174;
    
    // Hard-coded: posizioni Y logiche per ogni piano. 
    // Indice 0 = Piano 1, Indice 1 = Piano 2, ecc.
    // L'ascensore scende, quindi Y aumenta.
    private final float[] floorYPositions = {
        0.0f,  
        1456.0f - spawnOffset,
        1945.0f - spawnOffset,
        3227.0f - spawnOffset
    };
    
    private float targetY;
    private final float movementSpeed = 10.0f; // Velocità di movimento in unità logiche

    // Costruttore
    public Elevator(int totalFloors, int initialFloor) {
        this.totalFloors = totalFloors;
        this.currentFloor = initialFloor;
        // Inizializza la posizione y in base al piano iniziale
        this.yPosition = floorYPositions[initialFloor - 1];
        this.isMoving = false;
    }

    // Metodi per il movimento
    public void moveUp() {
        if (currentFloor > 1 && !isMoving) {
            currentFloor--;
            targetY = floorYPositions[currentFloor - 1];
            isMoving = true;
            
            setChanged();
            notifyObservers();
        }
    }

    public void moveDown() {
        if (currentFloor < totalFloors - 1 && !isMoving) {
            currentFloor++;
            targetY = floorYPositions[currentFloor - 1];
            isMoving = true;
            
            setChanged();
            notifyObservers();
        }
    }
    
    // Aggiorna lo stato dell'ascensore, chiamato dal GameController
    public void update() {
        if (isMoving) {
            if (Math.abs(yPosition - targetY) < movementSpeed) {
                yPosition = targetY;
                isMoving = false;
            } else if (yPosition < targetY) {
                yPosition += movementSpeed;
            } else if (yPosition > targetY) {
                yPosition -= movementSpeed;
            }
            
            setChanged();
            notifyObservers();
        }
    }

    // Getter
    public int getCurrentFloor() {
        return currentFloor;
    }

    public float getYPosition() {
        return yPosition;
    }

    public boolean isMoving() {
        return isMoving;
    }
}