package view;

import model.GameModel;
import model.UserProfile;
import controller.AssetLoader;
import controller.InputHandler;
import controller.ProfileSelectionHandler;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import static model.GameConstants.*;

/**
 * Renders the user profile selection screen.
 * This view displays a list of available profiles, allows for selection,
 * and provides options to create or delete profiles.
 */
public class ProfileSelectionView {
    private GameModel gameModel;
    private ProfileSelectionHandler psHandler;
    private InputHandler inputHandler;

    /**
     * Constructs a ProfileSelectionView.
     *
     * @param gameModel The main game model.
     * @param psHandler The handler that manages the logic for this screen.
     * @param inputHandler The handler to get mouse coordinates for hover effects.
     */
    public ProfileSelectionView(GameModel gameModel, ProfileSelectionHandler psHandler, InputHandler inputHandler) {
        this.gameModel = gameModel;
        this.psHandler = psHandler;
        this.inputHandler = inputHandler;
    }

    /**
     * Draws the profile selection screen.
     *
     * @param g The Graphics context to draw on.
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(UIStyle.BACKGROUND);
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        g2d.setFont(UIStyle.TITLE_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        String title = "Select Profile";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, (GAME_WIDTH - titleWidth) / 2, (int)(50 * SCALE));

        // Main popup area for the list
        int popupX = GAME_WIDTH / 2 - (int)(125 * SCALE);
        int popupY = (int)(75 * SCALE);
        int popupWidth = (int)(250 * SCALE);
        int popupHeight = (int)(200 * SCALE);
        g2d.setColor(UIStyle.BUTTON_BG);
        g2d.fillRect(popupX, popupY, popupWidth, popupHeight);
        g2d.setColor(UIStyle.BORDER);
        g2d.drawRect(popupX, popupY, popupWidth, popupHeight);

        // Draw the list of profiles inside the popup
        List<UserProfile> profiles = psHandler.getProfiles();
        List<Rectangle> currentDeleteBounds = new java.util.ArrayList<>();
        
        if (profiles.isEmpty()) {
            g2d.setFont(UIStyle.TEXT_FONT);
            g2d.setColor(UIStyle.DISABLED_FG);
            UIStyle.drawCenteredString(g2d, "No profiles found.", new Rectangle(popupX, popupY, popupWidth, popupHeight), UIStyle.TEXT_FONT);
        } else {
        	int itemY = popupY + (int)(10 * SCALE);
            int itemHeight = (int)(20 * SCALE);
            for (int i = 0; i < profiles.size(); i++) {
                if (i == psHandler.getSelectedIndex()) {
                    g2d.setColor(UIStyle.BUTTON_BG_HOVER);
                    g2d.fillRect(popupX + (int)(5 * SCALE), itemY, popupWidth - (int)(10 * SCALE), itemHeight);
                }
                
                BufferedImage avatarImg = AssetLoader.getInstance().getAvatar(profiles.get(i).getAvatarId());
                if (avatarImg != null) {
                    g2d.drawImage(avatarImg, popupX + (int)(12 * SCALE), itemY + (int)(2 * SCALE), (int)(15 * SCALE), (int)(15 * SCALE), null);
                }
                
                // Draw delete button (trash icon)
                int trashX = popupX + popupWidth - (int)(25 * SCALE);
                int trashY = itemY + (int)(2 * SCALE);
                Rectangle trashRect = new Rectangle(trashX, trashY, (int)(15 * SCALE), (int)(15 * SCALE));
                currentDeleteBounds.add(trashRect);
                
                boolean isHover = trashRect.contains(inputHandler.getMouseX(), inputHandler.getMouseY());
                g2d.setColor(isHover ? UIStyle.BUTTON_BG_HOVER : UIStyle.BUTTON_BG);
                g2d.fill(trashRect);
                g2d.setColor(UIStyle.BORDER);
                g2d.draw(trashRect);
                
                g2d.setColor(Color.RED);
                g2d.setFont(UIStyle.TRASH_ICON_FONT);
                g2d.drawString("X", trashX + (int)(4 * SCALE), trashY + (int)(11 * SCALE));
                
                g2d.setFont(UIStyle.BUTTON_FONT);
                g2d.setColor(UIStyle.FOREGROUND);
                g2d.drawString(profiles.get(i).getNickname(), popupX + (int)(32 * SCALE), itemY + (int)(15 * SCALE)); 
                itemY += itemHeight + (int)(2 * SCALE);
            }
        }
        
        psHandler.updateDeleteButtonBounds(currentDeleteBounds);
        
        // Draw instructions
        g2d.setFont(UIStyle.TEXT_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        String instructions = "Use ▲▼ to scroll, Enter to confirm";
        int instrWidth = g2d.getFontMetrics().stringWidth(instructions);
        g2d.drawString(instructions, (GAME_WIDTH - instrWidth) / 2, popupY + popupHeight + (int)(20 * SCALE));

        // Draw "Create New Profile" button
        drawButton(g2d, "Create New Profile", psHandler.getCreateButtonBounds(), psHandler.isCreateHover());
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