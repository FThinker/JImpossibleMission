package model;

import java.util.Observable;
import controller.AudioManager;

/**
 * Represents the game's elevator, managing its state, movement between floors, and position.
 * It extends {@link Observable} to notify observers (like the view) of any changes to its state,
 * such as when it starts or stops moving.
 */
@SuppressWarnings("deprecation")
public class Elevator extends Observable {

    private int currentFloor;
    private final int totalFloors;
    private float yPosition; 
    private boolean isMoving;
    
    private int spawnOffset = 174;
    
    // Hard-coded logical Y positions for each floor.
    // Index 0 = Floor 1, Index 1 = Floor 2, etc.
    // As the elevator goes down, its Y value increases.
    private final float[] floorYPositions = {
        0.0f,  
        1456.0f - spawnOffset,
        1945.0f - spawnOffset,
        3227.0f - spawnOffset
    };
    
    private float targetY;
    private final float movementSpeed = 10.0f;

    /**
     * Constructs an Elevator.
     *
     * @param totalFloors  The total number of floors the elevator can access.
     * @param initialFloor The floor where the elevator starts.
     */
    public Elevator(int totalFloors, int initialFloor) {
        this.totalFloors = totalFloors;
        this.currentFloor = initialFloor;
        this.yPosition = floorYPositions[initialFloor - 1];
        this.isMoving = false;
    }

    /**
     * Initiates movement to the floor above, if possible.
     */
    public void moveUp() {
        if (currentFloor > 1 && !isMoving) {
            currentFloor--;
            targetY = floorYPositions[currentFloor - 1];
            AudioManager.getInstance().loop("elevator_moving");
            isMoving = true;
            
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Initiates movement to the floor below, if possible.
     */
    public void moveDown() {
        if (currentFloor < totalFloors - 1 && !isMoving) {
            currentFloor++;
            targetY = floorYPositions[currentFloor - 1];
            AudioManager.getInstance().loop("elevator_moving");
            isMoving = true;
            
            setChanged();
            notifyObservers();
        }
    }
    
    /**
     * Updates the elevator's position on each game tick if it is moving.
     * This method smoothly interpolates the position towards the target floor.
     */
    public void update() {
        if (isMoving) {
            if (Math.abs(yPosition - targetY) < movementSpeed) {
                yPosition = targetY;
                isMoving = false;
                AudioManager.getInstance().stop("elevator_moving");
            } else if (yPosition < targetY) {
                yPosition += movementSpeed;
            } else if (yPosition > targetY) {
                yPosition -= movementSpeed;
            }
            
            setChanged();
            notifyObservers();
        }
    }

    // --- GETTERS ---

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