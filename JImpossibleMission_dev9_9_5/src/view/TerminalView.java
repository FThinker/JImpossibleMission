package view;

import controller.TerminalHandler;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.BasicStroke;
import static model.GameConstants.*;

/**
 * Renders the in-game terminal screen.
 * This view displays a text-based interface on top of the game world,
 * showing options available to the player.
 */
public class TerminalView {

    private TerminalHandler terminalHandler;

    /**
     * Constructs a TerminalView.
     *
     * @param terminalHandler The handler that provides the text content for the terminal.
     */
    public TerminalView(TerminalHandler terminalHandler) {
        this.terminalHandler = terminalHandler;
    }

    /**
     * Draws the terminal window and its text.
     *
     * @param g The Graphics context to draw on.
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int terminalX = GAME_WIDTH / 4;
        int terminalY = GAME_HEIGHT / 4;
        int terminalWidth = GAME_WIDTH / 2;
        int terminalHeight = GAME_HEIGHT / 2;

        // Draw the semi-transparent background
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(terminalX, terminalY, terminalWidth, terminalHeight);

        // Draw the border
        g2d.setColor(Color.GREEN);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(terminalX, terminalY, terminalWidth, terminalHeight);

        // Draw the terminal text line by line
        g2d.setColor(Color.GREEN);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, (int)(9 * SCALE)));
        
        String terminalText = terminalHandler.getTerminalText();
        int yOffset = terminalY + 30;
        for (String line : terminalText.split("\n")) {
            g2d.drawString(line, terminalX + 20, yOffset);
            yOffset += 20;
        }
    }
}