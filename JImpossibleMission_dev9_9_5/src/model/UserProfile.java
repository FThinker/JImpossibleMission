// In model/UserProfile.java
package model;

import java.io.Serializable;
import java.util.Objects;

public class UserProfile implements Serializable {
    // Per la serializzazione, è una buona pratica
    private static final long serialVersionUID = 1L;

    private String nickname;
    private String avatarId; // Per ora un ID, in futuro potresti mappare ID a immagini

    // Statistiche
    private int gamesPlayed = 0;
    private int gamesWon = 0;
    private int gamesLost = 0;
    private long totalScore = 0;
    private long totalPlaytimeMs = 0;
    private int level = 1;
    private long currentXp = 0;

    // Costanti per la formula del level up
    private static final long BASE_XP_THRESHOLD = 2000;
    private static final double LEVEL_UP_MULTIPLIER = 1.5;

    public UserProfile(String nickname, String avatarId) {
        this.nickname = nickname;
        this.avatarId = avatarId; // Avatar di default
    }
    
    public String getAvatarId() { return avatarId; }

    public void startGame(GameSession newGame) {
        this.gamesPlayed++;
    }

    public void endGame(boolean won, long score, long playtimeMs, long xpGained) {
        if (won) {
            this.gamesWon++;
        } else {
            this.gamesLost++;
        }
        this.totalScore += score;
        this.totalPlaytimeMs += playtimeMs; // Aggiorna il tempo totale
        this.currentXp += xpGained;
        
        // Controlla se il giocatore sale di livello (potrebbe salire più volte)
        while (this.currentXp >= getXpForNextLevel()) {
            this.currentXp -= getXpForNextLevel(); // Sottrai il costo del livello
            this.level++; // Aumenta il livello
            System.out.println(nickname + " è salito al livello " + this.level + "!");
        }
    }
    
    
    // Getters e Setters
    public String getNickname() { return nickname; }
    public int getGamesPlayed() { return gamesPlayed; }
    public int getGamesWon() { return gamesWon; }
    public int getGamesLost() { return gamesLost; }
    public long getTotalScore() { return totalScore; }
    public double getAverageScore() {
        return (gamesPlayed == 0) ? 0 : (double) totalScore / gamesPlayed;
    }
    public long getTotalPlaytimeMs() { return totalPlaytimeMs; }

    public int getLevel() {
        return level;
    }

    public long getCurrentXp() {
        return currentXp;
    }

    /**
     * Calcola l'XP necessario per il prossimo livello usando la progressione geometrica.
     */
    public long getXpForNextLevel() {
        return (long) (BASE_XP_THRESHOLD * Math.pow(LEVEL_UP_MULTIPLIER, this.level - 1));
    }
    
    // Necessari per confronti e per evitare duplicati
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