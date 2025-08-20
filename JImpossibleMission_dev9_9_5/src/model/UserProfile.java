package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a user's profile, storing all their progress and statistics.
 * This class is serializable to allow for saving and loading player data to/from disk.
 * It tracks game stats, level progression, and experience points (XP).
 */
public class UserProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nickname;
    private String avatarId;

    // Statistics
    private int gamesPlayed = 0;
    private int gamesWon = 0;
    private int gamesLost = 0;
    private long totalScore = 0;
    private long totalPlaytimeMs = 0;
    private int level = 1;
    private long currentXp = 0;

    // Constants for the level-up formula
    private static final long BASE_XP_THRESHOLD = 2000;
    private static final double LEVEL_UP_MULTIPLIER = 1.5;

    /**
     * Constructs a new UserProfile.
     * @param nickname The user's unique nickname.
     * @param avatarId The ID of the user's chosen avatar.
     */
    public UserProfile(String nickname, String avatarId) {
        this.nickname = nickname;
        this.avatarId = avatarId;
    }
    
    /**
     * Called when the user starts a new game session.
     * @param newGame The new game session being started.
     */
    public void startGame(GameSession newGame) {
        this.gamesPlayed++;
    }

    /**
     * Called at the end of a game to update the user's statistics and XP.
     * @param won True if the game was won, false otherwise.
     * @param score The score achieved in the game.
     * @param playtimeMs The duration of the game in milliseconds.
     * @param xpGained The amount of XP earned.
     */
    public void endGame(boolean won, long score, long playtimeMs, long xpGained) {
        if (won) {
            this.gamesWon++;
        } else {
            this.gamesLost++;
        }
        this.totalScore += score;
        this.totalPlaytimeMs += playtimeMs;
        this.currentXp += xpGained;
        
        // Check for level ups (a user can level up multiple times at once)
        while (this.currentXp >= getXpForNextLevel()) {
            this.currentXp -= getXpForNextLevel();
            this.level++;
        }
    }
    
    /**
     * Calculates the XP required to reach the next level using a geometric progression.
     * @return The total XP needed for the next level-up.
     */
    public long getXpForNextLevel() {
        return (long) (BASE_XP_THRESHOLD * Math.pow(LEVEL_UP_MULTIPLIER, this.level - 1));
    }
    
    // --- GETTERS AND SETTERS ---

    public String getNickname() { return nickname; }
    public String getAvatarId() { return avatarId; }
    public int getGamesPlayed() { return gamesPlayed; }
    public int getGamesWon() { return gamesWon; }
    public int getGamesLost() { return gamesLost; }
    public long getTotalScore() { return totalScore; }
    public long getTotalPlaytimeMs() { return totalPlaytimeMs; }
    public int getLevel() { return level; }
    public long getCurrentXp() { return currentXp; }
    
    public double getAverageScore() {
        return (gamesPlayed == 0) ? 0 : (double) totalScore / gamesPlayed;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return nickname.equalsIgnoreCase(that.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname.toLowerCase());
    }
}