// in view/ProfileSelectionView.java
package view;

import model.GameModel;
import model.UserProfile;
import controller.AssetLoader;
import controller.InputHandler;
import controller.ProfileSelectionHandler; // Assicurati di importarlo

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import static model.GameConstants.*;

public class ProfileSelectionView {
    private GameModel gameModel;
    private ProfileSelectionHandler psHandler;
    private InputHandler inputHandler;

    public ProfileSelectionView(GameModel gameModel, ProfileSelectionHandler psHandler, InputHandler inputHandler) {
        this.gameModel = gameModel;
        this.psHandler = psHandler;
        this.inputHandler = inputHandler;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // 1. Sfondo e Titolo (invariati)
        g2d.setColor(UIStyle.BACKGROUND);
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        g2d.setFont(UIStyle.TITLE_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        String title = "Seleziona Profilo";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, (GAME_WIDTH - titleWidth) / 2, (int)(50 * SCALE));

        // POPUP
        int popupX = GAME_WIDTH / 2 - (int)(125 * SCALE);
        int popupY = (int)(75 * SCALE);
        int popupWidth = (int)(250 * SCALE);
        int popupHeight = (int)(200 * SCALE);
        g2d.setColor(UIStyle.BUTTON_BG);
        g2d.fillRect(popupX, popupY, popupWidth, popupHeight);
        g2d.setColor(UIStyle.BORDER);
        g2d.drawRect(popupX, popupY, popupWidth, popupHeight);

        // 3. Disegna la LISTA dei profili all'interno del popup
        List<UserProfile> profiles = psHandler.getProfiles();
        
        // NUOVO: Lista temporanea per i rettangoli dei cestini
        List<Rectangle> currentDeleteBounds = new java.util.ArrayList<>();
        
        if (profiles.isEmpty()) {
            g2d.setFont(UIStyle.TEXT_FONT);
            g2d.setColor(UIStyle.DISABLED_FG);
            UIStyle.drawCenteredString(g2d, "Nessun profilo trovato.", new Rectangle(popupX, popupY, popupWidth, popupHeight), UIStyle.TEXT_FONT);
        } else {
        	int itemY = popupY + (int)(10 * SCALE);
            int itemHeight = (int)(20 * SCALE);
            for (int i = 0; i < profiles.size(); i++) {
                // Se l'elemento è quello selezionato, disegna un rettangolo di evidenziazione
                if (i == psHandler.getSelectedIndex()) {
                    g2d.setColor(UIStyle.BUTTON_BG_HOVER);
                    g2d.fillRect(popupX + (int)(5 * SCALE), itemY, popupWidth - (int)(10 * SCALE), itemHeight);
                }
                
                // Disegna l'avatar
                BufferedImage avatarImg = AssetLoader.getInstance().getAvatar(profiles.get(i).getAvatarId());
                if (avatarImg != null) {
                    g2d.drawImage(avatarImg, popupX + (int)(12 * SCALE), itemY + (int)(2 * SCALE), (int)(15 * SCALE), (int)(15 * SCALE), null);
                }
                
                // Trash
                int trashX = popupX + popupWidth - (int)(25 * SCALE);
                int trashY = itemY + (int)(2 * SCALE);
                Rectangle trashRect = new Rectangle(trashX, trashY, (int)(15 * SCALE), (int)(15 * SCALE));
                currentDeleteBounds.add(trashRect); // Aggiungi il rettangolo alla lista
                
                // Controlla l'hover del mouse direttamente qui per un feedback visivo
                boolean isHover = trashRect.contains(inputHandler.getMouseX(), inputHandler.getMouseY());
                g2d.setColor(isHover ? UIStyle.BUTTON_BG_HOVER : UIStyle.BUTTON_BG);
                g2d.fill(trashRect);
                g2d.setColor(UIStyle.BORDER);
                g2d.draw(trashRect);
                
                // Disegna una "X" per rappresentare il cestino
                g2d.setColor(Color.RED);
                g2d.setFont(UIStyle.TRASH_ICON_FONT);
                g2d.drawString("X", trashX + (int)(4 * SCALE), trashY + (int)(11 * SCALE));
                
                g2d.setFont(UIStyle.BUTTON_FONT);
                g2d.setColor(UIStyle.FOREGROUND);
                g2d.drawString(profiles.get(i).getNickname(), popupX + (int)(32 * SCALE), itemY + (int)(15 * SCALE)); 
                itemY += itemHeight + (int)(2 * SCALE);
            }
        }
        
        // Passa i rettangoli aggiornati all'handler
        psHandler.updateDeleteButtonBounds(currentDeleteBounds);
        
        // 4. Disegna le istruzioni
        g2d.setFont(UIStyle.TEXT_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        String instructions = "Usa ▲▼ per scorrere, Invio per confermare";
        int instrWidth = g2d.getFontMetrics().stringWidth(instructions);
        g2d.drawString(instructions, (GAME_WIDTH - instrWidth) / 2, popupY + popupHeight + (int)(20 * SCALE));

        // 5. Disegna il bottone "Crea Nuovo Profilo" in basso
        drawButton(g2d, "Crea Nuovo Profilo", psHandler.getCreateButtonBounds(), psHandler.isCreateHover());
    }

    // Aggiungi un overload al metodo drawButton per gestire lo stato disabilitato
    private void drawButton(Graphics2D g, String text, Rectangle bounds, boolean isHover, boolean enabled) {
        g.setColor(isHover && enabled ? UIStyle.BUTTON_BG_HOVER : UIStyle.BUTTON_BG);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g.setColor(UIStyle.BORDER);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g.setColor(enabled ? UIStyle.FOREGROUND : UIStyle.DISABLED_FG);
        UIStyle.drawCenteredString(g, text, bounds, UIStyle.BUTTON_FONT);
    }
    
    // Il vecchio metodo chiama il nuovo con enabled = true
    private void drawButton(Graphics2D g, String text, Rectangle bounds, boolean isHover) {
        drawButton(g, text, bounds, isHover, true);
    }
}