package view;

import model.Enemy;
import model.EnemyType;
import model.GameModel;
import model.Level;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import controller.AssetLoader;

/**
 * Renders the main gameplay screen.
 * This class is responsible for orchestrating the drawing of the level, the player,
 * and all enemies by calling their respective view classes. It gets all necessary data
 * directly from the {@link GameModel}.
 */
public class PlayingView {

    private GameModel gameModel;
    
    private PlayerView playerRenderer;
    private LevelView levelRenderer;
    private EnemyView enemyRenderer;

    /**
     * Constructs a PlayingView.
     *
     * @param gameModel The central game model, which is the single source of truth for rendering.
     */
    public PlayingView(GameModel gameModel) {
        this.gameModel = gameModel;
        this.playerRenderer = new PlayerView();
        this.levelRenderer = new LevelView(); 
        this.enemyRenderer = new EnemyView();
    }

    /**
     * Draws the entire gameplay scene.
     *
     * @param g The Graphics context to draw on.
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw the background color
        g2d.setColor(new Color(30, 0, 50));
        g2d.fillRect(0, 0, model.GameConstants.GAME_WIDTH, model.GameConstants.GAME_HEIGHT);
        
        Level currentLevel = gameModel.getLevel();
        
        if (currentLevel == null) {
            return; // Do not attempt to draw if no level is loaded
        }

        // Delegate drawing to specialized renderers
        levelRenderer.draw(g2d, gameModel.getLevel());
        playerRenderer.draw(g2d, gameModel.getPlayer(), gameModel.getGameState());
        
        for (Enemy enemy : gameModel.getEnemies()) {
            EnemyType type = enemy.getEnemyType();
            EnemyAnimationSet animSet = AssetLoader.getInstance().getAnimationsFor(type);
            
            if (animSet != null) {
                enemyRenderer.draw(g2d, enemy, animSet);
            }
        }
    }
}