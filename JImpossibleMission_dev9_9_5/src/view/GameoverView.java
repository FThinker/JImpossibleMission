// in view/GameoverView.java
package view;

import controller.GameoverHandler;
import model.GameModel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import static model.GameConstants.*;

public class GameoverView {
    
    private GameoverHandler handler;
    private GameModel gameModel;

    public GameoverView(GameModel gameModel, GameoverHandler gameoverHandler) {
        this.gameModel = gameModel;
        this.handler = gameoverHandler;
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Velo scuro
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // Titolo GAME OVER
        g2d.setFont(UIStyle.TITLE_FONT);
        g2d.setColor(Color.RED); // Un tocco di colore per il game over
        String title = "GAME OVER";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, (GAME_WIDTH - titleWidth) / 2, (int)(100 * SCALE));

        // Mostra statistiche partita
        g2d.setFont(UIStyle.TEXT_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        
        if (gameModel.getCurrentGameSession() != null) {
            long elapsedTime = gameModel.getCurrentGameSession().getElapsedTime();
            long minutes = (elapsedTime / 1000) / 60;
            long seconds = (elapsedTime / 1000) % 60;
            String timeStr = String.format("Tempo di gioco: %02d:%02d", minutes, seconds);
            int timeWidth = g2d.getFontMetrics().stringWidth(timeStr);
            g2d.drawString(timeStr, (GAME_WIDTH - timeWidth) / 2, 300);

            // Per il punteggio, dobbiamo trovare un modo per salvarlo al momento del game over
            // Lo aggiungeremo al GameModel per semplicità
            String scoreStr = "Punteggio finale: " + gameModel.getLastScore();
            int scoreWidth = g2d.getFontMetrics().stringWidth(scoreStr);
            g2d.drawString(scoreStr, (GAME_WIDTH - scoreWidth) / 2, 340);
        }

        // Bottone per tornare al menu
        drawButton(g2d, "Torna al Menu", handler.getMenuButtonBounds(), handler.isMenuHover());
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