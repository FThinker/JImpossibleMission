// in controller/LeaderboardHandler.java
package controller;

import model.GameModel;
import model.GameState;
import model.UserProfile;

import static model.GameConstants.SCALE;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LeaderboardHandler {
    private GameModel gameModel;
    private InputHandler inputHandler;
    private Rectangle backButtonBounds = new Rectangle((int)(245 * SCALE), (int)(300 * SCALE), (int)(150 * SCALE), (int)(25 * SCALE));
    private boolean backHover = false;

    // Liste per contenere le nostre classifiche Top 10
    private List<UserProfile> topByScore;
    private List<UserProfile> topByPlaytime;
    private List<UserProfile> topByAvgScore;

    public LeaderboardHandler(GameModel gameModel, InputHandler inputHandler) {
        this.gameModel = gameModel;
        this.inputHandler = inputHandler;
        // Inizializza le liste come vuote
        topByScore = Collections.emptyList();
        topByPlaytime = Collections.emptyList();
        topByAvgScore = Collections.emptyList();
    }

    /**
     * Il cuore della logica: carica tutti i profili e li ordina usando gli stream.
     * Verrà chiamato dal MainMenuHandler prima di entrare in questa schermata.
     */
    public void refreshLeaderboards() {
        List<UserProfile> allProfiles = ProfileManager.loadAllProfiles();

        // 1. Classifica Hi-Score (Punteggio Totale)
        topByScore = allProfiles.stream()
            .sorted(Comparator.comparingLong(UserProfile::getTotalScore).reversed())
            .limit(10)
            .collect(Collectors.toList());

        // 2. Classifica Playtime (Tempo di Gioco Totale)
        topByPlaytime = allProfiles.stream()
            .sorted(Comparator.comparingLong(UserProfile::getTotalPlaytimeMs).reversed())
            .limit(10)
            .collect(Collectors.toList());
            
        // 3. Classifica Punteggio Medio
        topByAvgScore = allProfiles.stream()
            .sorted(Comparator.comparingDouble(UserProfile::getAverageScore).reversed())
            .limit(10)
            .collect(Collectors.toList());
    }

    public void handleInput() {
        backHover = backButtonBounds.contains(inputHandler.getMouseX(), inputHandler.getMouseY());
        if (inputHandler.isMouseButtonPressed(MouseEvent.BUTTON1) && backHover) {
            gameModel.setGameState(GameState.HOMESCREEN);
        }
        inputHandler.resetMouse();
    }
    
    // Getters per la View
    public Rectangle getBackButtonBounds() { return backButtonBounds; }
    public boolean isBackHover() { return backHover; }
    public List<UserProfile> getTopByScore() { return topByScore; }
    public List<UserProfile> getTopByPlaytime() { return topByPlaytime; }
    public List<UserProfile> getTopByAvgScore() { return topByAvgScore; }
}