package view;

import model.GameModel;
import model.GameSession;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import static model.GameConstants.*;

/**
 * Renders the Heads-Up Display (HUD) on the screen during active gameplay.
 * The HUD shows real-time information such as FPS, lives, puzzle pieces, and time remaining.
 */
public class HUDView {
    private GameModel gameModel;

    /**
     * Constructs a HUDView.
     *
     * @param gameModel The game model from which to retrieve HUD data.
     */
    public HUDView(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    /**
     * Draws the HUD elements.
     *
     * @param g The Graphics context to draw on.
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw a semi-transparent black bar at the top for the background
        g2d.setColor(new Color(0, 0, 0, 100));
        int hudHeight = (int)(20 * SCALE);
        g2d.fillRect(0, 0, GAME_WIDTH, hudHeight);

        // Set font and color for the text
        g2d.setFont(UIStyle.TEXT_FONT);
        g2d.setColor(Color.GREEN);

        // Retrieve data from the model
        int fps = gameModel.getCurrentFps();
        int lives = gameModel.getLives();
        int pieces = gameModel.getPuzzlePiecesFound();
        
        GameSession session = gameModel.getCurrentGameSession();
        long timeLeftMs = (session != null) ? session.getTimeLeft() : 0;
        long minutes = (timeLeftMs / 1000) / 60;
        long seconds = (timeLeftMs / 1000) % 60;
        if (timeLeftMs < 0) { // Prevent displaying negative time
        	minutes = 0;
        	seconds = 0;
        }

        // Create the display strings
        String fpsStr = "FPS: " + fps;
        String livesStr = "Lives: " + lives;
        String piecesStr = "Pieces: " + pieces;
        String timeStr = String.format("Time Left: %02d:%02d", minutes, seconds);

        // Draw the strings across the HUD bar
        int xPadding = (int)(10 * SCALE);
        int yPosition = (int)(14 * SCALE);
        int xOffset = xPadding;

        g2d.drawString(fpsStr, xOffset, yPosition);
        
        xOffset = GAME_WIDTH / 4;
        g2d.drawString(livesStr, xOffset, yPosition);

        xOffset = GAME_WIDTH / 2;
        g2d.drawString(piecesStr, xOffset, yPosition);

        xOffset = GAME_WIDTH * 3 / 4;
        g2d.drawString(timeStr, xOffset, yPosition);
    }
}