package view;

import controller.VictoryHandler;
import model.GameModel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import static model.GameConstants.*;

/**
 * Renders the "You Win!" screen upon successful completion of the game.
 * This view displays the final score and playtime, and provides a button to return to the main menu.
 */
public class VictoryView {
    
    private VictoryHandler handler;
    private GameModel gameModel;

    /**
     * Constructs a VictoryView.
     *
     * @param gameModel      The game model to retrieve final game statistics from.
     * @param victoryHandler The handler that manages input for this screen.
     */
    public VictoryView(GameModel gameModel, VictoryHandler victoryHandler) {
        this.gameModel = gameModel;
        this.handler = victoryHandler;
    }
    
    /**
     * Draws the victory screen.
     *
     * @param g The Graphics context to draw on.
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Dark overlay
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // "YOU WIN!" title
        g2d.setFont(UIStyle.TITLE_FONT);
        g2d.setColor(Color.YELLOW);
        String title = "YOU WIN!";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, (GAME_WIDTH - titleWidth) / 2, (int)(100 * SCALE));

        // Display final game stats
        g2d.setFont(UIStyle.TEXT_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        
        if (gameModel.getCurrentGameSession() != null) {
            long elapsedTime = gameModel.getCurrentGameSession().getElapsedTime();
            long minutes = (elapsedTime / 1000) / 60;
            long seconds = (elapsedTime / 1000) % 60;
            String timeStr = String.format("Playtime: %02d:%02d", minutes, seconds);
            int timeWidth = g2d.getFontMetrics().stringWidth(timeStr);
            g2d.drawString(timeStr, (GAME_WIDTH - timeWidth) / 2, 300);

            String scoreStr = "Final score: " + gameModel.getLastScore();
            int scoreWidth = g2d.getFontMetrics().stringWidth(scoreStr);
            g2d.drawString(scoreStr, (GAME_WIDTH - scoreWidth) / 2, 340);
        }

        // Button to return to menu
        drawButton(g2d, "Return to Menu", handler.getMenuButtonBounds(), handler.isMenuHover());
    }
    
    /**
     * A helper method to draw a styled button.
     *
     * @param g The Graphics2D context.
     * @param text The text to display on the button.
     * @param bounds The rectangle defining the button's position and size.
     * @param isHover True if the mouse is currently hovering over the button.
     */
    private void drawButton(Graphics2D g, String text, Rectangle bounds, boolean isHover) {
        g.setColor(isHover ? UIStyle.BUTTON_BG_HOVER : UIStyle.BUTTON_BG);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g.setColor(UIStyle.BORDER);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g.setColor(UIStyle.FOREGROUND);
        UIStyle.drawCenteredString(g, text, bounds, UIStyle.BUTTON_FONT);
    }
}