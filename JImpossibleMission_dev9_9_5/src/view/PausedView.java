// in view/PausedView.java
package view;

import controller.PausedHandler;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import static model.GameConstants.*;

public class PausedView {
    
    private PausedHandler handler;

    public PausedView(PausedHandler pausedHandler) {
        this.handler = pausedHandler;
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // 1. Disegna un velo scuro semi-trasparente su tutto lo schermo
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // 2. Disegna il titolo "In Pausa"
        g2d.setFont(UIStyle.TITLE_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        String title = "In Pausa";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, (GAME_WIDTH - titleWidth) / 2, (int)(100 * SCALE));

        // 3. Disegna i bottoni
        drawButton(g2d, "Riprendi", handler.getResumeButtonBounds(), handler.isResumeHover());
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