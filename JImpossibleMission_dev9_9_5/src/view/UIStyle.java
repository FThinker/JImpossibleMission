package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import static model.GameConstants.SCALE;

/**
 * A final utility class that centralizes UI styling constants.
 * It defines a consistent theme for colors and fonts used across all menus and UI elements.
 * This class cannot be instantiated.
 */
public final class UIStyle {
    // Theme Colors
    public static final Color BACKGROUND = new Color(21, 21, 21);
    public static final Color FOREGROUND = new Color(230, 230, 230);
    public static final Color BUTTON_BG = new Color(45, 45, 45);
    public static final Color BUTTON_BG_HOVER = new Color(75, 75, 75);
    public static final Color BORDER = new Color(90, 90, 90);
    public static final Color DISABLED_FG = new Color(120, 120, 120);

    // Fonts
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, (int)(24 * SCALE));
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, (int)(12 * SCALE));
    public static final Font TEXT_FONT = new Font("Arial", Font.PLAIN, (int)(9 * SCALE));
    public static final Font TRASH_ICON_FONT = new Font("Arial", Font.BOLD, (int)(10 * SCALE));

    /**
     * A utility method to draw a string centered inside a rectangle.
     *
     * @param g    The Graphics context.
     * @param text The string to draw.
     * @param rect The rectangle in which to center the string.
     * @param font The font to use for the string.
     */
    public static void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
    }
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private UIStyle() {}
}