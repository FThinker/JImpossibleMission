package view;

import model.GameModel;
import model.UserProfile;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import controller.AssetLoader;
import controller.MainMenuHandler;
import static model.GameConstants.*;

/**
 * Renders the main menu screen of the game.
 * This view displays the game title, a welcome message for the active profile,
 * and navigation buttons.
 */
public class MainMenuView {
    private GameModel gameModel;
    private MainMenuHandler handler;

    /**
     * Constructs a MainMenuView.
     *
     * @param gameModel The game model to get the active profile from.
     * @param handler   The handler that manages input and button states for this menu.
     */
    public MainMenuView(GameModel gameModel, MainMenuHandler handler) {
        this.gameModel = gameModel;
        this.handler = handler;
    }

    /**
     * Draws the main menu.
     *
     * @param g The Graphics context to draw on.
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(UIStyle.BACKGROUND);
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        g2d.setFont(UIStyle.TITLE_FONT);
        g2d.setColor(UIStyle.FOREGROUND);
        String title = "JImpossibleMission";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, (GAME_WIDTH - titleWidth) / 2, (int)(50 * SCALE));

        if (gameModel.getActiveProfile() != null) {
        	UserProfile profile = gameModel.getActiveProfile();
            BufferedImage avatarImg = AssetLoader.getInstance().getAvatar(profile.getAvatarId());
            if (avatarImg != null) {
                g2d.drawImage(avatarImg, (GAME_WIDTH / 2) - (int)(75 * SCALE), (int)(62 * SCALE), (int)(25 * SCALE), (int)(25 * SCALE), null);
            }

            g2d.setFont(UIStyle.TEXT_FONT);
            String welcome = "Welcome back, " + profile.getNickname() + "!";
            int welcomeWidth = g2d.getFontMetrics().stringWidth(welcome);
            g2d.drawString(welcome, (GAME_WIDTH - welcomeWidth) / 2 + (int)(15 * SCALE), (int)(75 * SCALE));
        }

        drawButton(g2d, "New Game", handler.getNewGameBounds(), handler.isNewGameHover());
        drawButton(g2d, "Statistics", handler.getStatsBounds(), handler.isStatsHover());
        drawButton(g2d, "Leaderboard", handler.getLeaderboardBounds(), handler.isLeaderboardHover());
        drawButton(g2d, "Change Profile", handler.getChangeProfileBounds(), handler.isChangeProfileHover());
    }

    /**
     * A helper method to draw a styled button, optionally in a disabled state.
     * @param g The Graphics2D context.
     * @param text The text for the button.
     * @param bounds The button's position and size.
     * @param isHover True if the mouse is hovering over the button.
     * @param enabled True if the button is enabled.
     */
    private void drawButton(Graphics2D g, String text, Rectangle bounds, boolean isHover, boolean enabled) {
        g.setColor(isHover && enabled ? UIStyle.BUTTON_BG_HOVER : UIStyle.BUTTON_BG);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g.setColor(UIStyle.BORDER);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g.setColor(enabled ? UIStyle.FOREGROUND : UIStyle.DISABLED_FG);
        UIStyle.drawCenteredString(g, text, bounds, UIStyle.BUTTON_FONT);
    }
    
    /**
     * Overloaded helper method to draw a standard, enabled button.
     */
    private void drawButton(Graphics2D g, String text, Rectangle bounds, boolean isHover) {
        drawButton(g, text, bounds, isHover, true);
    }
}