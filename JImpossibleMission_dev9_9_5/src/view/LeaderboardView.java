// in view/LeaderboardView.java
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

public class LeaderboardView {
    private LeaderboardHandler handler;

    public LeaderboardView(GameModel gameModel, LeaderboardHandler handler) {
        this.handler = handler;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(UIStyle.BACKGROUND);
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        g2d.setFont(UIStyle.TITLE_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        
        UIStyle.drawCenteredString(g2d, "Classifiche", new Rectangle(0, (int)(10 * SCALE), GAME_WIDTH, (int)(25 * SCALE)), UIStyle.TITLE_FONT);
        drawLeaderboardColumn(g2d, "Hi-Score", handler.getTopByScore(), (int)(25 * SCALE), "score");
        drawLeaderboardColumn(g2d, "Playtime", handler.getTopByPlaytime(), (int)(225 * SCALE), "time");
        drawLeaderboardColumn(g2d, "Punteggio Medio", handler.getTopByAvgScore(), (int)(425 * SCALE), "avg");

        // Bottone per tornare indietro
        drawButton(g2d, "Indietro", handler.getBackButtonBounds(), handler.isBackHover());
    }

    private void drawLeaderboardColumn(Graphics2D g2d, String title, List<UserProfile> profiles, int x, String type) {
        g2d.setFont(UIStyle.BUTTON_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        g2d.drawString(title, x + (int)(25 * SCALE), (int)(50 * SCALE));

        int y = (int)(70 * SCALE);
        int rank = 1;
        for (UserProfile profile : profiles) {
            // Rank
            g2d.setFont(UIStyle.TEXT_FONT);
            g2d.drawString(rank + ".", x, y + (int)(10 * SCALE));

            // Avatar
            BufferedImage avatarImg = AssetLoader.getInstance().getAvatar(profile.getAvatarId());
            if (avatarImg != null) {
            	g2d.drawImage(avatarImg, x + (int)(15 * SCALE), y, (int)(15 * SCALE), (int)(15 * SCALE), null);
            }

            // Nickname
            g2d.drawString(profile.getNickname(), x + (int)(35 * SCALE), y + (int)(10 * SCALE));

            // Score (formattato in base al tipo)
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

    private void drawButton(Graphics2D g, String text, Rectangle bounds, boolean isHover) {
        g.setColor(isHover ? UIStyle.BUTTON_BG_HOVER : UIStyle.BUTTON_BG);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g.setColor(UIStyle.BORDER);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g.setColor(UIStyle.FOREGROUND);
        UIStyle.drawCenteredString(g, text, bounds, UIStyle.BUTTON_FONT);
    }
}