package view;

import model.UserProfile;
import controller.LeaderboardHandler;
import model.GameModel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import controller.AssetLoader;
import static model.GameConstants.*;

/**
 * Renders the leaderboard screen.
 * This view displays multiple top-10 lists based on different player statistics,
 * such as total score, total playtime, and average score.
 */
public class LeaderboardView {
    private LeaderboardHandler handler;

    /**
     * Constructs a LeaderboardView.
     *
     * @param gameModel The game model (not directly used for drawing but often passed for consistency).
     * @param handler   The handler that provides the sorted leaderboard data and manages input.
     */
    public LeaderboardView(GameModel gameModel, LeaderboardHandler handler) {
        this.handler = handler;
    }

    /**
     * Draws the leaderboard screen.
     *
     * @param g The Graphics context to draw on.
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(UIStyle.BACKGROUND);
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        g2d.setFont(UIStyle.TITLE_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        
        UIStyle.drawCenteredString(g2d, "Leaderboards", new Rectangle(0, (int)(10 * SCALE), GAME_WIDTH, (int)(25 * SCALE)), UIStyle.TITLE_FONT);
        drawLeaderboardColumn(g2d, "Hi-Score", handler.getTopByScore(), (int)(25 * SCALE), "score");
        drawLeaderboardColumn(g2d, "Playtime", handler.getTopByPlaytime(), (int)(225 * SCALE), "time");
        drawLeaderboardColumn(g2d, "Average Score", handler.getTopByAvgScore(), (int)(425 * SCALE), "avg");

        drawButton(g2d, "Back", handler.getBackButtonBounds(), handler.isBackHover());
    }

    /**
     * Draws a single column of the leaderboard.
     *
     * @param g2d      The Graphics2D context.
     * @param title    The title of the column (e.g., "Hi-Score").
     * @param profiles The sorted list of user profiles to display.
     * @param x        The starting X coordinate for the column.
     * @param type     A string key ("score", "time", "avg") to format the value correctly.
     */
    private void drawLeaderboardColumn(Graphics2D g2d, String title, List<UserProfile> profiles, int x, String type) {
        g2d.setFont(UIStyle.BUTTON_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        g2d.drawString(title, x + (int)(25 * SCALE), (int)(50 * SCALE));

        int y = (int)(70 * SCALE);
        int rank = 1;
        for (UserProfile profile : profiles) {
            g2d.setFont(UIStyle.TEXT_FONT);
            g2d.drawString(rank + ".", x, y + (int)(10 * SCALE));

            BufferedImage avatarImg = AssetLoader.getInstance().getAvatar(profile.getAvatarId());
            if (avatarImg != null) {
            	g2d.drawImage(avatarImg, x + (int)(15 * SCALE), y, (int)(15 * SCALE), (int)(15 * SCALE), null);
            }

            g2d.drawString(profile.getNickname(), x + (int)(35 * SCALE), y + (int)(10 * SCALE));

            String scoreText = "";
            switch (type) {
                case "score":
                    scoreText = String.format("%,d", profile.getTotalScore());
                    break;
                case "time":
                    long playtime = profile.getTotalPlaytimeMs();
                    long minutes = (playtime / 1000) / 60;
                    long seconds = (playtime / 1000) % 60;
                    scoreText = String.format("%02d:%02d", minutes, seconds);
                    break;
                case "avg":
                    scoreText = String.format("%,.0f", profile.getAverageScore());
                    break;
            }
            int scoreWidth = g2d.getFontMetrics().stringWidth(scoreText);
            g2d.drawString(scoreText, x + (int)(175 * SCALE) - scoreWidth, y + (int)(10 * SCALE));
            
            y += (int)(20 * SCALE);
            rank++;
        }
    }

    /**
     * A helper method to draw a styled button.
     * @param g The Graphics2D context.
     * @param text The text for the button.
     * @param bounds The button's position and size.
     * @param isHover True if the mouse is hovering over the button.
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