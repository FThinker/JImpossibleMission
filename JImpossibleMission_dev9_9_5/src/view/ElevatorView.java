package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

import controller.AssetLoader;
import model.Elevator;
import model.GameModel;
import model.Player;

import static model.GameConstants.*;
public class ElevatorView {

	private GameModel gameModel;
    private Elevator elevatorModel;
    private BufferedImage shaftBackground; // Sfondo dello shaft
    private BufferedImage cabinImage;      // Immagine della cabina
    private Player player;
    private PlayerView playerRenderer;
    

    public ElevatorView(GameModel gameModel) {
    	this.gameModel = gameModel;
        this.elevatorModel = gameModel.getElevator();
        this.player = gameModel.getPlayer();
        this.playerRenderer = new PlayerView();
        loadImages();
    }

    private void loadImages() {
        this.shaftBackground = AssetLoader.getInstance().getImage("elevatorShaft");
        this.cabinImage = AssetLoader.getInstance().getImage("elevatorCabin");
        if (this.shaftBackground == null || this.cabinImage == null) {
            System.err.println("Errore: Immagini dell'ascensore non caricate!");
        }
    }

    // Metodo chiamato dalla MainGamePanel per disegnare l'ascensore
    public void draw(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g;
        // Calcola l'offset di disegno dello sfondo in base alla posizione logica del model
        // L'idea è che la posizione logica yPosition indica "a che punto dell'albero dell'ascensore siamo".
        // La view trasla l'intero sfondo per simulare lo scorrimento
        int shaftOffsetY = (int) (elevatorModel.getYPosition() * SCALE); 
        
     // Posizione fissa della cabina all'interno della view
        int cabinDrawX = (GAME_WIDTH / 2) - (int)((cabinImage.getWidth() * SCALE / 2)); 
        int cabinDrawY = (int)((GAME_HEIGHT / 7.5) - (3 * SCALE)); // Centrata verticalmente
        
        // Disegna lo sfondo (shaft) con l'offset
        g.drawImage(shaftBackground, 0, -shaftOffsetY, GAME_WIDTH, (int)(shaftBackground.getHeight() * SCALE), null);

        // Disegna la cabina in una posizione fissa dello schermo
        g.drawImage(cabinImage, cabinDrawX, cabinDrawY, (int) (cabinImage.getWidth() * SCALE), (int) (cabinImage.getHeight() * SCALE), null);
        
        playerRenderer.draw(g2d, player, gameModel.getGameState());
        
    }
}