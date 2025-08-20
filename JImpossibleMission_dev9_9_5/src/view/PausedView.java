package view;

import controller.PausedHandler;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import static model.GameConstants.*;

/**
 * Renders the pause menu screen.
 * This view displays a semi-transparent overlay on top of the game,
 * along with options to resume or return to the main menu.
 */
public class PausedView {
    
    private PausedHandler handler;

    /**
     * Constructs a PausedView.
     *
     * @param pausedHandler The handler that manages input and button states for this screen.
     */
    public PausedView(PausedHandler pausedHandler) {
        this.handler = pausedHandler;
    }
    
    /**
     * Draws the pause screen.
     *
     * @param g The Graphics context to draw on.
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw a dark, semi-transparent overlay over the entire screen
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // Draw the "Paused" title
        g2d.setFont(UIStyle.TITLE_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        String title = "Paused";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, (GAME_WIDTH - titleWidth) / 2, (int)(100 * SCALE));

        // Draw the buttons
        drawButton(g2d, "Resume", handler.getResumeButtonBounds(), handler.isResumeHover());
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