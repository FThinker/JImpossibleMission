package view;

import model.GameModel;
import model.UserProfile;
import controller.StatsHandler;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import static model.GameConstants.*;

/**
 * Renders the statistics screen for the currently active user profile.
 * Displays information such as level, XP, games played, wins, losses, and total score.
 */
public class StatsView {
    private GameModel gameModel;
    private StatsHandler handler;

    /**
     * Constructs a StatsView.
     *
     * @param gameModel The game model to retrieve the active profile from.
     * @param handler   The handler that manages input for this screen.
     */
    public StatsView(GameModel gameModel, StatsHandler handler) {
        this.gameModel = gameModel;
        this.handler = handler;
    }

    /**
     * Draws the statistics screen.
     *
     * @param g The Graphics context to draw on.
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(UIStyle.BACKGROUND);
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        g2d.setFont(UIStyle.TITLE_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        UIStyle.drawCenteredString(g2d, 
        		"Statistics", 
        		new Rectangle(0, (int)(25 * SCALE), GAME_WIDTH, (int)(25 * SCALE)), 
        		UIStyle.TITLE_FONT);

        if (gameModel.getActiveProfile() != null) {
        	UserProfile profile = gameModel.getActiveProfile();
        	
            g2d.setFont(UIStyle.TEXT_FONT);
            g2d.drawString("Level " + profile.getLevel(), (int)(175 * SCALE), (int)(80 * SCALE));
            
            drawXpBar(g2d, profile, (int)(175 * SCALE), (int)(85 * SCALE));
        	
            g2d.setFont(UIStyle.BUTTON_FONT);
            int y = (int)(125 * SCALE);
            long playtime = profile.getTotalPlaytimeMs();
            long minutes = (playtime / 1000) / 60;
            long seconds = (playtime / 1000) % 60;
            
            g2d.drawString("Games Played: " + profile.getGamesPlayed(), (int)(175 * SCALE), y);
            g2d.drawString("Wins: " + profile.getGamesWon(), (int)(175 * SCALE), y += (int)(25 * SCALE));
            g2d.drawString("Losses: " + profile.getGamesLost(), (int)(175 * SCALE), y += (int)(25 * SCALE));
            g2d.drawString("Total Score: " + profile.getTotalScore(), (int)(175 * SCALE), y += (int)(25 * SCALE));
            g2d.drawString(String.format("Total Playtime: %02d:%02d", minutes, seconds), (int)(175 * SCALE), y += (int)(25 * SCALE));
        }

        drawButton(g2d, "Back", handler.getBackButtonBounds(), handler.isBackHover());
    }
    
    /**
     * Helper method to draw the experience (XP) progress bar.
     *
     * @param g2d      The Graphics2D context.
     * @param profile  The user profile containing XP data.
     * @param x        The X coordinate of the bar.
     * @param y        The Y coordinate of the bar.
     */
    private void drawXpBar(Graphics2D g2d, UserProfile profile, int x, int y) {
        long currentXp = profile.getCurrentXp();
        long nextLevelXp = profile.getXpForNextLevel();
        double xpPercentage = (double) currentXp / nextLevelXp;
        
        int barWidth = (int) (260 * SCALE);
        int barHeight = (int) (10 * SCALE);
        
        // Bar background
        g2d.setColor(UIStyle.BUTTON_BG);
        g2d.fillRect(x, y, barWidth, barHeight);
        
        // Bar fill
        g2d.setColor(Color.GREEN);
        g2d.fillRect(x, y, (int) (barWidth * xpPercentage), barHeight);
        
        // Bar border
        g2d.setColor(UIStyle.BORDER);
        g2d.drawRect(x, y, barWidth, barHeight);
        
        // XP text (e.g., "1500 / 2000 XP")
        g2d.setFont(UIStyle.TEXT_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        String xpText = currentXp + " / " + nextLevelXp + " XP";
        int textWidth = g2d.getFontMetrics().stringWidth(xpText);
        g2d.drawString(xpText, x + barWidth - textWidth, y - 5);
    }
    
    /**
     * Helper method to draw a styled button.
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