// in view/HUDView.java
package view;

import model.GameModel;
import model.GameSession;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import static model.GameConstants.*;

public class HUDView {
    private GameModel gameModel;

    public HUDView(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // 1. Disegna la "striscia" nera semi-trasparente in alto
        g2d.setColor(new Color(0, 0, 0, 100));
        int hudHeight = (int)(20 * SCALE);
        g2d.fillRect(0, 0, GAME_WIDTH, hudHeight);

        // 2. Prepara il font e il colore per il testo
        g2d.setFont(UIStyle.TEXT_FONT);
        g2d.setColor(Color.GREEN);

        // 3. Recupera i dati dal modello
        int fps = gameModel.getCurrentFps();
        int lives = gameModel.getLives();
        int pieces = gameModel.getPuzzlePiecesFound();
        
        GameSession session = gameModel.getCurrentGameSession();
        long timeLeftMs = (session != null) ? session.getTimeLeft() : 0;
        long minutes = (timeLeftMs / 1000) / 60;
        long seconds = (timeLeftMs / 1000) % 60;
        if (timeLeftMs < 0) { // Evita di mostrare tempo negativo
        	minutes = 0;
        	seconds = 0;
        }

        // 4. Crea le stringhe da visualizzare
        String fpsStr = "FPS: " + fps;
        String livesStr = "Lives: " + lives;
        String piecesStr = "Pieces: " + pieces;
        String timeStr = String.format("Time Left: %02d:%02d", minutes, seconds);

        // 5. Disegna le stringhe in orizzontale
        int xPadding = (int)(10 * SCALE);
        int yPosition = (int)(14 * SCALE);
        int xOffset = xPadding;

        g2d.drawString(fpsStr, xOffset, yPosition);
        
        // Spazia le altre scritte in modo uniforme
        xOffset = GAME_WIDTH / 4;
        g2d.drawString(livesStr, xOffset, yPosition);

        xOffset = GAME_WIDTH / 2;
        g2d.drawString(piecesStr, xOffset, yPosition);

        xOffset = GAME_WIDTH * 3 / 4;
        g2d.drawString(timeStr, xOffset, yPosition);
    }
}