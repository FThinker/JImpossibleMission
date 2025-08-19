// in view/StatsView.java
package view;

import model.GameModel;
import model.UserProfile;
import controller.StatsHandler;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import static model.GameConstants.*;

public class StatsView {
    private GameModel gameModel;
    private StatsHandler handler;

    public StatsView(GameModel gameModel, StatsHandler handler) {
        this.gameModel = gameModel;
        this.handler = handler;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(UIStyle.BACKGROUND);
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        g2d.setFont(UIStyle.TITLE_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        UIStyle.drawCenteredString(g2d, 
        		"Statistiche", 
        		new Rectangle(0, (int)(25 * SCALE), GAME_WIDTH, (int)(25 * SCALE)), 
        		UIStyle.TITLE_FONT);

        if (gameModel.getActiveProfile() != null) {
        	UserProfile profile = gameModel.getActiveProfile();
        	
        	// Mostra il livello
            g2d.setFont(UIStyle.TEXT_FONT);
            g2d.drawString("Livello " + profile.getLevel(), (int)(175 * SCALE), (int)(80 * SCALE));
            
            // Disegna la barra dell'esperienza
            drawXpBar(g2d, profile, (int)(175 * SCALE), (int)(85 * SCALE));
        	
            g2d.setFont(UIStyle.BUTTON_FONT);
//            int y = (int)(100 * SCALE);
            int y = (int)(125 * SCALE);
            long playtime = profile.getTotalPlaytimeMs();
            long minutes = (playtime / 1000) / 60;
            long seconds = (playtime / 1000) % 60;
            
            g2d.drawString("Partite Giocate: " + profile.getGamesPlayed(), (int)(175 * SCALE), y);
            g2d.drawString("Vittorie: " + profile.getGamesWon(), (int)(175 * SCALE), y += (int)(25 * SCALE));
            g2d.drawString("Sconfitte: " + profile.getGamesLost(), (int)(175 * SCALE), y += (int)(25 * SCALE));
            g2d.drawString("Punteggio Totale: " + profile.getTotalScore(), (int)(175 * SCALE), y += (int)(25 * SCALE));
            g2d.drawString(String.format("Tempo Totale: %02d:%02d", minutes, seconds), (int)(175 * SCALE), y += (int)(25 * SCALE));
        }

        // Usa il nuovo metodo helper per disegnare il bottone correttamente
        drawButton(g2d, "Indietro", handler.getBackButtonBounds(), handler.isBackHover());
    }
    
    /**
     * NUOVO METODO HELPER: Disegna la barra dell'esperienza (XP).
     */
    private void drawXpBar(Graphics2D g2d, UserProfile profile, int x, int y) {
        long currentXp = profile.getCurrentXp();
        long nextLevelXp = profile.getXpForNextLevel();
        double xpPercentage = (double) currentXp / nextLevelXp;
        
        int barWidth = (int) (260 * SCALE);
        int barHeight = (int) (10 * SCALE);
        
        // Sfondo della barra
        g2d.setColor(UIStyle.BUTTON_BG);
        g2d.fillRect(x, y, barWidth, barHeight);
        
        // Riempimento della barra
        g2d.setColor(Color.GREEN);
        g2d.fillRect(x, y, (int) (barWidth * xpPercentage), barHeight);
        
        // Bordo della barra
        g2d.setColor(UIStyle.BORDER);
        g2d.drawRect(x, y, barWidth, barHeight);
        
        // Testo XP (es. "1500 / 2000 XP")
        g2d.setFont(UIStyle.TEXT_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        String xpText = currentXp + " / " + nextLevelXp + " XP";
        int textWidth = g2d.getFontMetrics().stringWidth(xpText);
        g2d.drawString(xpText, x + barWidth - textWidth, y - 5);
    }
    
    /**
     * NUOVO METODO HELPER: Disegna un bottone con sfondo, bordo e testo.
     * Identico a quello delle altre view per coerenza.
     */
    private void drawButton(Graphics2D g, String text, Rectangle bounds, boolean isHover) {
        // Disegna lo sfondo del bottone (più chiaro se in hover)
        g.setColor(isHover ? UIStyle.BUTTON_BG_HOVER : UIStyle.BUTTON_BG);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        // Disegna il bordo
        g.setColor(UIStyle.BORDER);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        // Disegna il testo al centro
        g.setColor(UIStyle.FOREGROUND);
        UIStyle.drawCenteredString(g, text, bounds, UIStyle.BUTTON_FONT);
    }
}