package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import controller.LevelLoader;

// NOTA IMPORTANTE: Per un futuro salvataggio su file, 
// questa classe dovrà implementare java.io.Serializable
public class GameSession implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int lives;
    private int puzzlePiecesFound;
    
    private long startTime;
    private long finalPlaytimeMs = -1; // NUOVO: -1 significa che il timer è ancora attivo
    public static final long TIME_LIMIT_MS = 20 * 60 * 1000; // 20 minuti
    
    // NUOVI CAMPI per salvare lo stato della posizione
    private GameState lastGameState = GameState.IN_ELEVATOR;
    private int currentLevelId = -1; // -1 significa che siamo nell'ascensore
    
    // Usiamo una Mappa per tenere traccia dei livelli già caricati.
    // La chiave è il numero del livello, il valore è l'oggetto Level stesso.
    private Map<Integer, Level> loadedLevels;

    public GameSession() {
        this.lives = 3; // Valori iniziali
        this.puzzlePiecesFound = 0;
        this.loadedLevels = new HashMap<>();
        this.startTime = System.currentTimeMillis();
    }
    
 // NUOVO METODO: Ferma il timer e salva il tempo finale
    public void stopTimer() {
        if (finalPlaytimeMs == -1) { // Ferma il timer solo una volta
            this.finalPlaytimeMs = System.currentTimeMillis() - startTime;
        }
    }

    // METODO MODIFICATO: Ora restituisce il tempo finale se il timer è fermo
    public long getElapsedTime() {
        if (finalPlaytimeMs != -1) {
            return finalPlaytimeMs; // Ritorna il tempo congelato
        }
        return System.currentTimeMillis() - startTime; // Altrimenti, calcola il tempo attuale
    }

    public long getTimeLeft() {
        return TIME_LIMIT_MS - getElapsedTime();
    }

    /**
     * Ottiene un livello. Se il livello è già stato caricato in questa sessione,
     * restituisce l'istanza esistente. Altrimenti, lo carica dal file,
     * lo memorizza nella mappa e poi lo restituisce.
     * @param levelNumber Il numero del livello da caricare.
     * @return L'oggetto Level corrispondente.
     */
    public Level getLevel(int levelNumber) {
        if (loadedLevels.containsKey(levelNumber)) {
            // Livello già visitato, restituisco l'istanza salvata
            System.out.println("Rientro nel livello " + levelNumber + " (già caricato).");
            return loadedLevels.get(levelNumber);
        } else {
            // Prima visita, carico il livello dal file
            System.out.println("Entro nel livello " + levelNumber + " per la prima volta.");
            Level newLevel = LevelLoader.loadLevel("/levels/level" + levelNumber + ".txt");
            if (newLevel != null) {
                loadedLevels.put(levelNumber, newLevel);
            }
            return newLevel;
        }
    }

    // Getters e Setters per lo stato globale
    public int getLives() {
        return lives;
    }

    public void loseLife() {
        this.lives--;
    }
    
    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getPuzzlePiecesFound() {
        return puzzlePiecesFound;
    }

    public void addPuzzlePiece() {
        this.puzzlePiecesFound++;
    }
    
    /**
     * NUOVO METODO: Aggiorna la posizione e lo stato del giocatore nella sessione.
     * Da chiamare ogni volta che si entra/esce da una stanza o si mette in pausa.
     */
    public void updatePlayerLocation(GameState currentState, Level currentLevel) {
        this.lastGameState = currentState;
        if (currentLevel != null) {
            // Se siamo in un livello, dobbiamo trovare il suo ID.
            // Questa è una logica un po' contorta, ma funziona: cerchiamo nei valori della mappa
            // l'oggetto livello e ne ricaviamo la chiave (il suo numero).
            for (Map.Entry<Integer, Level> entry : loadedLevels.entrySet()) {
                if (entry.getValue() == currentLevel) { // Confronto per riferimento
                    this.currentLevelId = entry.getKey();
                    return;
                }
            }
        } else {
            // Se currentLevel è null, significa che siamo nell'ascensore
            this.currentLevelId = -1;
        }
    }
    
 // Getters per i nuovi campi
    public GameState getLastGameState() {
        return lastGameState;
    }

    public int getCurrentLevelId() {
        return currentLevelId;
    }
}