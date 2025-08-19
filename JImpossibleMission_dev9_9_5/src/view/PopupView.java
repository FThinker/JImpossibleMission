// PopupView.java (Nuovo file nel package view)
package view;

import controller.PopupHandler;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;
import java.awt.BasicStroke;

import model.FurnitureTile;

import static model.GameConstants.*;

public class PopupView {

    private PopupHandler popupHandler;

    public PopupView(PopupHandler popupHandler) {
        this.popupHandler = popupHandler;
    }

    public void draw(Graphics g) {
        String text = popupHandler.getPopupText();
        if (text.isEmpty()) {
            return; // Non disegnare nulla se non c'è testo
        }

        int popupX = 0;
        int popupY = 0;
        int popupWidth = 200; // Larghezza fissa
        int popupHeight = 50; // Altezza fissa

        // Calcola la posizione del pop-up in base al suo tipo
        if (popupHandler.isSearchingPopupActive()) {
            // Posiziona il popup di ricerca sopra la furniture
            FurnitureTile furniture = popupHandler.getCurrentFurniture();
            if (furniture != null) {
                popupX = (int) (furniture.getHitbox().x * SCALE);
                popupY = (int) (furniture.getHitbox().y * SCALE) - 60; // 60 pixel sopra il mobile
            } else {
                // Fallback al centro dello schermo se non c'è una furniture
                popupX = (GAME_WIDTH - popupWidth) / 2;
                popupY = (GAME_HEIGHT - popupHeight) / 2;
            }
            popupHeight = 60; // Aumento l'altezza per la barra di progresso
        } else if (popupHandler.isNotificationPopupActive() && popupHandler.getCurrentFurniture() != null) {
            // Posiziona il popup di notifica al centro dello schermo
        	FurnitureTile furniture = popupHandler.getCurrentFurniture();
        	popupX = (int) (furniture.getHitbox().x * SCALE);
            popupY = (int) (furniture.getHitbox().y * SCALE) - 60; // 60 pixel sopra il mobile
        }

        // Disegna lo sfondo della nuvoletta
        g.setColor(new Color(0, 0, 0, 200)); // Bianco semi-trasparente
        g.fillRect(popupX, popupY, popupWidth, popupHeight); // Bordo arrotondato

        // Disegna il contorno della nuvoletta
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GREEN);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(popupX, popupY, popupWidth, popupHeight);

        // Disegna il testo
        g2d.setColor(Color.GREEN);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, (int)(9 * SCALE)));
        
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textX = popupX + (popupWidth - textWidth) / 2;
        int textY = popupY + metrics.getAscent() + (popupHeight - metrics.getHeight()) / 3;
        g2d.drawString(text, textX, textY);
        
        // Disegna la barra di progresso se è un pop-up di ricerca
        if (popupHandler.isSearchingPopupActive()) {
            float progress = popupHandler.getSearchProgress();
            float maxTime = popupHandler.getCurrentFurniture().getSearchTimeRequired();
            float progressRatio = progress / maxTime;
            
            // Disegna il background della barra
            g2d.setColor(new Color(0, 0, 0, 0));
            int barX = popupX + 20;
            int barY = popupY + 35;
            int barWidth = popupWidth - 40;
            int barHeight = 10;
            g2d.fillRect(barX, barY, barWidth, barHeight);
            
            // Disegna la barra di progresso
            g2d.setColor(Color.GREEN);
            g2d.fillRect(barX, barY, (int) (barWidth * progressRatio), barHeight);
            
            // Disegna il contorno della barra di progresso
            g2d.setColor(Color.GREEN);
            g2d.drawRect(barX, barY, barWidth, barHeight);
        }
    }
}