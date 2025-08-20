package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import controller.AssetLoader;
import model.Elevator;
import model.GameModel;
import model.Player;
import static model.GameConstants.*;

/**
 * Renders the elevator scene, including the moving shaft background, the static cabin,
 * and the player.
 */
public class ElevatorView {

	private GameModel gameModel;
    private Elevator elevatorModel;
    private BufferedImage shaftBackground;
    private BufferedImage cabinImage;
    private Player player;
    private PlayerView playerRenderer;
    
    /**
     * Constructs an ElevatorView.
     *
     * @param gameModel The main game model containing the elevator and player data.
     */
    public ElevatorView(GameModel gameModel) {
    	this.gameModel = gameModel;
        this.elevatorModel = gameModel.getElevator();
        this.player = gameModel.getPlayer();
        this.playerRenderer = new PlayerView();
        loadImages();
    }

    /**
     * Loads the required images for the elevator from the AssetLoader.
     */
    private void loadImages() {
        this.shaftBackground = AssetLoader.getInstance().getImage("elevatorShaft");
        this.cabinImage = AssetLoader.getInstance().getImage("elevatorCabin");
        if (this.shaftBackground == null || this.cabinImage == null) {
            System.err.println("Error: Elevator images not loaded!");
        }
    }

    /**
     * Draws the elevator view onto the screen.
     * It simulates movement by translating the background shaft image vertically
     * based on the elevator's logical Y position, while the cabin remains stationary.
     *
     * @param g The Graphics context to draw on.
     */
    public void draw(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g;
        
        // The logical yPosition from the model dictates the vertical offset for the background shaft.
        // This creates the illusion of the camera moving up or down the elevator shaft.
        int shaftOffsetY = (int) (elevatorModel.getYPosition() * SCALE); 
        
        // The cabin is drawn at a fixed position on the screen.
        int cabinDrawX = (GAME_WIDTH / 2) - (int)((cabinImage.getWidth() * SCALE / 2)); 
        int cabinDrawY = (int)((GAME_HEIGHT / 7.5) - (3 * SCALE));
        
        // Draw the shaft background with the calculated offset.
        g.drawImage(shaftBackground, 0, -shaftOffsetY, GAME_WIDTH, (int)(shaftBackground.getHeight() * SCALE), null);

        // Draw the cabin at its fixed position.
        g.drawImage(cabinImage, cabinDrawX, cabinDrawY, (int) (cabinImage.getWidth() * SCALE), (int) (cabinImage.getHeight() * SCALE), null);
        
        // Draw the player inside the elevator.
        playerRenderer.draw(g2d, player, gameModel.getGameState());
    }
}