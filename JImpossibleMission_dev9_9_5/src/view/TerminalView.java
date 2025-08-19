package view;

import controller.TerminalHandler;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.BasicStroke; // Importa BasicStroke per il bordo

import static model.GameConstants.*;

public class TerminalView {

    private TerminalHandler terminalHandler;

    public TerminalView(TerminalHandler terminalHandler) {
        this.terminalHandler = terminalHandler;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Coordinate e dimensioni del terminale
        int terminalX = GAME_WIDTH / 4;
        int terminalY = GAME_HEIGHT / 4;
        int terminalWidth = GAME_WIDTH / 2;
        int terminalHeight = GAME_HEIGHT / 2;

        // Disegna lo sfondo semi-trasparente per il terminale
        g2d.setColor(new Color(0, 0, 0, 200)); // Nero semi-trasparente
        g2d.fillRect(terminalX, terminalY, terminalWidth, terminalHeight);

        // NUOVO: Disegna il contorno
        g2d.setColor(Color.GREEN); // Colore del bordo (puoi scegliere quello che preferisci)
        g2d.setStroke(new BasicStroke(3)); // Spessore del bordo (puoi cambiarlo)
        g2d.drawRect(terminalX, terminalY, terminalWidth, terminalHeight);

        // Disegna il testo del terminale
        g2d.setColor(Color.GREEN); // Colore del testo del terminale
        g2d.setFont(new Font("Monospaced", Font.PLAIN, (int)(9 * SCALE)));
        
        String terminalText = terminalHandler.getTerminalText();
        int yOffset = terminalY + 30; // Inizio del testo, con offset dal bordo superiore
        for (String line : terminalText.split("\n")) {
            g2d.drawString(line, terminalX + 20, yOffset); // Offset dal bordo sinistro
            yOffset += 20; // Spazio tra le righe
        }
    }
}