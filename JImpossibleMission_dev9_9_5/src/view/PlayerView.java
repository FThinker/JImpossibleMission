package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import controller.AssetLoader;
import controller.AudioManager;
import model.GameState;
import model.Player;
import model.PlayerState;
import static model.GameConstants.*;
import static model.Directions.*;

/**
 * Renders the player entity on the screen.
 * This class handles loading player animations from sprite sheets, selecting the
 * appropriate animation based on the player's current state (e.g., running, idle, jumping),
 * and managing the timing for frame updates to create fluid motion.
 */
public class PlayerView {
	private BufferedImage runningAtlas;
	private BufferedImage idleAtlas;
	private BufferedImage jumpingAtlas;
	private BufferedImage searchingAtlas;

	private BufferedImage[] runningAnimations;
	private BufferedImage[] idleAnimations;
	private BufferedImage[] jumpingAnimations;
	private BufferedImage[] searchingAnimations;

	private final int spriteWidth = TILES_DEFAULT_SIZE;
	private final int spriteHeight = TILES_DEFAULT_SIZE;

	private int aniIndex;
	
	private int runningAniSpeed = 30;
	private int idleAniSpeed = 60;
	private int searchingAniSpeed = 50;
	private int jumpingAniSpeed = 25;

	private final int totalRunningFrames = 14;
	private final int totalIdleFrames = 1;
	private final int totalJumpingFrames = 13;
	private final int totalSearchingFrames = 1;
	
	private long lastFrameTime = 0;
	
	private final int STEP_FRAME_1 = 3;
    private final int STEP_FRAME_2 = 10;

	/**
     * Constructs a PlayerView and loads all necessary player animation assets.
     */
	public PlayerView() {
		this.runningAtlas = AssetLoader.getInstance().getImage("runningAtlas");
		this.idleAtlas = AssetLoader.getInstance().getImage("idleAtlas");
        this.jumpingAtlas = AssetLoader.getInstance().getImage("jumpingAtlas");
        this.searchingAtlas = AssetLoader.getInstance().getImage("searchingAtlas");

		if (this.runningAtlas == null || this.idleAtlas == null || this.jumpingAtlas == null || this.searchingAtlas == null) {
			System.err.println("Error: One or more player sprite atlases were not loaded!");
		} else {
			loadAnimations();
		}
	}

	/**
     * Slices the loaded sprite atlases into individual animation frames.
     */
	private void loadAnimations() {
        runningAnimations = new BufferedImage[totalRunningFrames];
        for (int i = 0; i < totalRunningFrames; i++) {
            runningAnimations[i] = runningAtlas.getSubimage(i * spriteWidth, 0, spriteWidth, spriteHeight);
        }

        idleAnimations = new BufferedImage[totalIdleFrames];
        for (int i = 0; i < totalIdleFrames; i++) {
            idleAnimations[i] = idleAtlas.getSubimage(i * spriteWidth, 0, spriteWidth, spriteHeight);
        }
        
        jumpingAnimations = new BufferedImage[totalJumpingFrames];
        for (int i = 0; i < totalJumpingFrames; i++) {
            jumpingAnimations[i] = jumpingAtlas.getSubimage(i * spriteWidth, 0, spriteWidth, spriteHeight);
        }
        
        searchingAnimations = new BufferedImage[totalSearchingFrames];
        for (int i = 0; i < totalSearchingFrames; i++) {
            searchingAnimations[i] = searchingAtlas.getSubimage(i * spriteWidth, 0, spriteWidth, spriteHeight);
        }
    }

	/**
     * Draws the player character on the screen.
     *
     * @param g          The Graphics context to draw on.
     * @param player     The player data object.
     * @param gameState  The current state of the game, used to determine if animations should update.
     */
	public void draw(Graphics g, Player player, GameState gameState) {
        BufferedImage[] currentAnimation = null;
        int currentAniSpeed = 0;
        int currentTotalFrames = 0;

        switch (player.getCurrentState()) {
            case RUNNING:
                currentAnimation = runningAnimations;
                currentAniSpeed = runningAniSpeed;
                currentTotalFrames = totalRunningFrames;
                break;
            case IDLE:
                currentAnimation = idleAnimations;
                currentAniSpeed = idleAniSpeed;
                currentTotalFrames = totalIdleFrames;
                break;
            case JUMPING:
                currentAnimation = jumpingAnimations;
                currentAniSpeed = jumpingAniSpeed;
                currentTotalFrames = totalJumpingFrames;
                break;
            case SEARCHING:
            	currentAnimation = searchingAnimations;
            	currentAniSpeed = searchingAniSpeed;
            	currentTotalFrames = totalSearchingFrames;
            	break;
            default:
                currentAnimation = idleAnimations; // Fallback to idle
                currentAniSpeed = idleAniSpeed;
                currentTotalFrames = totalIdleFrames;
                break;
        }

        if (currentAnimation == null || currentAnimation.length == 0 || player == null) {
            return;
        }
        
        // Update the animation tick only if the game is in an active state.
        if (gameState == GameState.PLAYING || gameState == GameState.IN_ELEVATOR) {
            updateAnimationTick(currentAniSpeed, currentTotalFrames, player.getCurrentState());
        }

        int frameToDraw = aniIndex % currentAnimation.length;
        int drawX = (int) (player.getX() * SCALE);
        int drawY = (int) (player.getY() * SCALE);
        int drawWidth = (int) (player.getWidth() * SCALE);
        int drawHeight = (int) (player.getHeight() * SCALE);
        if(player.getDirection() == RIGHT)
        	g.drawImage(currentAnimation[frameToDraw], drawX, drawY, drawWidth, drawHeight, null);
        else // Flip sprite horizontally if facing left
        	g.drawImage(currentAnimation[frameToDraw], drawX + drawWidth, drawY, -drawWidth, drawHeight, null);
    }
	
	/**
     * Updates the animation index based on a time delay.
     * It also triggers step sounds at specific frames of the running animation.
     *
     * @param frameDurationMillis The duration each frame should be displayed, in milliseconds.
     * @param totalFrames         The total number of frames in the current animation.
     * @param currentState        The current state of the player.
     */
	private void updateAnimationTick(int frameDurationMillis, int totalFrames, PlayerState currentState) {
	    long now = System.currentTimeMillis();

	    if (now - lastFrameTime >= frameDurationMillis) {
	        lastFrameTime = now;
	        int oldAniIndex = aniIndex;
	        aniIndex = (aniIndex + 1) % totalFrames;
	        
	        // Play step sounds on specific frames of the running animation
	        if (aniIndex != oldAniIndex && currentState == PlayerState.RUNNING) {
	            if (aniIndex == STEP_FRAME_1 || aniIndex == STEP_FRAME_2) {
	                AudioManager.getInstance().play("step_2");
	            }
	        }
	    }
	}
}