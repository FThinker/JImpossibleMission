package model;

/**
 * Enumerates the possible states for the player character.
 * These states determine the player's current action and animation.
 */
public enum PlayerState {
    IDLE,
    RUNNING,
    JUMPING,
    FALLING,
    SEARCHING
}