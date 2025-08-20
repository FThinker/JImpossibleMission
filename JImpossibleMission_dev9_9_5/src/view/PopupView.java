package view;

import controller.PopupHandler;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.BasicStroke;
import model.FurnitureTile;
import static model.GameConstants.*;

/**
 * Renders in-game popups, such as notifications and search progress bars.
 * It retrieves the state and content of the popup from the {@link PopupHandler}.
 */
public class PopupView {

    private PopupHandler popupHandler;

    /**
     * Constructs a PopupView.
     *
     * @param popupHandler The handler that manages the state of the popups.
     */
    public PopupView(PopupHandler popupHandler) {
        this.popupHandler = popupHandler;
    }

    /**
     * Draws the currently active popup, if any.
     *
     * @param g The Graphics context to draw on.
     */
    public void draw(Graphics g) {
        String text = popupHandler.getPopupText();
        if (text.isEmpty()) {
            return; // Do nothing if there is no popup to display
        }

        int popupX = 0;
        int popupY = 0;
        int popupWidth = 200;
        int popupHeight = 50;

        // Position the popup based on its type (search vs. notification)
        if (popupHandler.isSearchingPopupActive()) {
            FurnitureTile furniture = popupHandler.getCurrentFurniture();
            if (furniture != null) {
                popupX = (int) (furniture.getHitbox().x * SCALE);
                popupY = (int) (furniture.getHitbox().y * SCALE) - 60; // Position above the furniture
            } else {
                popupX = (GAME_WIDTH - popupWidth) / 2; // Fallback to center
                popupY = (GAME_HEIGHT - popupHeight) / 2;
            }
            popupHeight = 60; // Taller to accommodate progress bar
        } else if (popupHandler.isNotificationPopupActive() && popupHandler.getCurrentFurniture() != null) {
        	FurnitureTile furniture = popupHandler.getCurrentFurniture();
        	popupX = (int) (furniture.getHitbox().x * SCALE);
            popupY = (int) (furniture.getHitbox().y * SCALE) - 60;
        }

        // Draw the popup background
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(popupX, popupY, popupWidth, popupHeight);

        // Draw the popup border
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GREEN);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(popupX, popupY, popupWidth, popupHeight);

        // Draw the text
        g2d.setColor(Color.GREEN);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, (int)(9 * SCALE)));
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textX = popupX + (popupWidth - textWidth) / 2;
        int textY = popupY + metrics.getAscent() + (popupHeight - metrics.getHeight()) / 3;
        g2d.drawString(text, textX, textY);
        
        // If it's a searching popup, draw the progress bar
        if (popupHandler.isSearchingPopupActive()) {
            float progress = popupHandler.getSearchProgress();
            float maxTime = popupHandler.getCurrentFurniture().getSearchTimeRequired();
            float progressRatio = progress / maxTime;
            
            int barX = popupX + 20;
            int barY = popupY + 35;
            int barWidth = popupWidth - 40;
            int barHeight = 10;

            // Draw progress bar background and fill
            g2d.setColor(new Color(0, 0, 0, 0));
            g2d.fillRect(barX, barY, barWidth, barHeight);
            g2d.setColor(Color.GREEN);
            g2d.fillRect(barX, barY, (int) (barWidth * progressRatio), barHeight);
            
            // Draw progress bar border
            g2d.setColor(Color.GREEN);
            g2d.drawRect(barX, barY, barWidth, barHeight);
        }
    }
}