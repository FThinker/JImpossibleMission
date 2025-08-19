package view;

import model.Enemy;
import model.EnemyType;
import model.GameModel;
import model.Player;
import model.Level;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.List;

import controller.AssetLoader;

public class PlayingView {

    // ✅ CAMBIAMENTO FONDAMENTALE #2
    // Rimuoviamo i campi locali per player, level, enemies.
    // Manteniamo solo il riferimento al GameModel, la nostra unica "fonte di verità".
    private GameModel gameModel;
    
    private PlayerView playerRenderer;
    private LevelView levelRenderer;
    private EnemyView enemyRenderer;

    public PlayingView(GameModel gameModel) {
        this.gameModel = gameModel; // Salviamo il riferimento al model

        // I renderer vengono creati una sola volta.
        this.playerRenderer = new PlayerView();
        // Il costruttore di LevelView ora non ha più bisogno del livello.
        this.levelRenderer = new LevelView(); 
        this.enemyRenderer = new EnemyView();
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Disegna lo sfondo
        g2d.setColor(new Color(30, 0, 50));
        g2d.fillRect(0, 0, model.GameConstants.GAME_WIDTH, model.GameConstants.GAME_HEIGHT);
        
        // ✅ CAMBIAMENTO FONDAMENTALE #3
        // Chiediamo i dati aggiornati al GameModel OGNI volta che dobbiamo disegnare.
        Level currentLevel = gameModel.getLevel();
        
        // Aggiungiamo un controllo di sicurezza. Se il livello non è caricato, non proviamo a disegnarlo.
        if (currentLevel == null) {
            return;
        }

        // Passiamo il livello corrente al metodo draw del levelRenderer.
        levelRenderer.draw(g2d, gameModel.getLevel());

        // Disegniamo il player.
        playerRenderer.draw(g2d, gameModel.getPlayer(), gameModel.getGameState());
        
     // Disegniamo i nemici del livello corrente.
        for (Enemy enemy : gameModel.getEnemies()) {
            // 1. Ottieni il tipo di nemico
            EnemyType type = enemy.getEnemyType();
            // 2. Chiedi all'AssetLoader il pacchetto di animazioni corretto
            EnemyAnimationSet animSet = AssetLoader.getInstance().getAnimationsFor(type);
            
            // 3. Se il pacchetto esiste, disegna il nemico usandolo
            if (animSet != null) {
                enemyRenderer.draw(g2d, enemy, animSet);
            }
        }
    }
}