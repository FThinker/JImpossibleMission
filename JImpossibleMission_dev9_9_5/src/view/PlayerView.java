package view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import controller.AssetLoader;
import controller.AudioManager;
import model.GameState;
import model.Player; // La PlayerView ha bisogno di PlayerModel per la posizione
import model.PlayerState; // Importa il PlayerState

import static model.GameConstants.*;
import static model.Directions.*;

public class PlayerView { // NON estende JPanel e NON implementa Observer
	private BufferedImage runningAtlas;
	private BufferedImage idleAtlas; // Nuovo atlas per idle
	private BufferedImage jumpingAtlas;
	private BufferedImage searchingAtlas;

	private BufferedImage[] runningAnimations;
	private BufferedImage[] idleAnimations;
	private BufferedImage[] jumpingAnimations;
	private BufferedImage[] searchingAnimations;

	private final int spriteWidth = TILES_DEFAULT_SIZE;
	private final int spriteHeight = TILES_DEFAULT_SIZE;
	

	private int aniIndex; // Contatori per l'animazione
	
	private int runningAniSpeed = 30; // Velocità dell'animazione (più basso = più veloce)
	private int idleAniSpeed = 60; // Velocità animazione idle (può essere diversa)
	private int searchingAniSpeed = 50;
	private int jumpingAniSpeed = 25;

	private final int totalRunningFrames = 14; // Numero totale di frame nell'atlas di corsa
	private final int totalIdleFrames = 1; // Numero di frame per l'idle (esempio)
	private final int totalJumpingFrames = 13;
	private final int totalSearchingFrames = 1;
	
	private long lastFrameTime = 0;
	
	private final int STEP_FRAME_1 = 3;
    private final int STEP_FRAME_2 = 10;

	public PlayerView() {
		this.runningAtlas = AssetLoader.getInstance().getImage("runningAtlas");
		this.idleAtlas = AssetLoader.getInstance().getImage("idleAtlas"); // Carica l'atlas idle
        this.jumpingAtlas = AssetLoader.getInstance().getImage("jumpingAtlas");
        this.searchingAtlas = AssetLoader.getInstance().getImage("searchingAtlas");


		if (this.runningAtlas == null || this.idleAtlas == null || this.jumpingAtlas == null || this.searchingAtlas == null) {
			System.err.println("Errore: Uno o più Sprite Atlas del player non caricati!");
			// Gestisci l'errore in modo appropriato
		} else {
			loadAnimations();
		}
	}

	private void loadAnimations() {
        // Carica animazioni di corsa
        runningAnimations = new BufferedImage[totalRunningFrames];
        for (int i = 0; i < totalRunningFrames; i++) {
            runningAnimations[i] = runningAtlas.getSubimage(i * spriteWidth, 0, spriteWidth, spriteHeight);
        }

        // Carica animazioni di idle
        idleAnimations = new BufferedImage[totalIdleFrames];
        for (int i = 0; i < totalIdleFrames; i++) {
            idleAnimations[i] = idleAtlas.getSubimage(i * spriteWidth, 0, spriteWidth, spriteHeight);
        }
        
        // Load jumping animations
        jumpingAnimations = new BufferedImage[totalJumpingFrames];
        for (int i = 0; i < totalJumpingFrames; i++) {
            jumpingAnimations[i] = jumpingAtlas.getSubimage(i * spriteWidth, 0, spriteWidth, spriteHeight);
        }
        
     // Load Searching animations
        searchingAnimations = new BufferedImage[totalSearchingFrames];
        for (int i = 0; i < totalSearchingFrames; i++) {
            searchingAnimations[i] = searchingAtlas.getSubimage(i * spriteWidth, 0, spriteWidth, spriteHeight);
        }
    }

	// Questo metodo verrà chiamato dalla GameView
	public void draw(Graphics g, Player player, GameState gameState) {
        // Seleziona l'array di animazione corrente e la sua velocità in base allo stato del Model
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
                currentAnimation = idleAnimations; // Fallback
                currentAniSpeed = idleAniSpeed;
                currentTotalFrames = totalIdleFrames;
                break;
        }

        // Controlli di sicurezza
        if (currentAnimation == null || currentAnimation.length == 0 || player == null) {
            g.setColor(Color.RED);
            g.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
            return;
        }

        // Aggiorna l'indice dell'animazione solo se lo stato non è IDLE
        // O se l'animazione di idle è essa stessa animata (come nel tuo caso)
//        if (player.getCurrentState() != PlayerState.IDLE || player.getCurrentState() != PlayerState.SEARCHING || totalIdleFrames > 1) { // Anima l'idle se ha più di 1 frame
//            updateAnimationTick(currentAniSpeed, currentTotalFrames);
//        } else {
//            aniIndex = 0; // Se è un frame singolo per idle, resetta sempre a 0
//        }
        
        // 3. Aggiorna l'animazione SOLO se il gioco è in uno stato attivo
        if (gameState == GameState.PLAYING || gameState == GameState.IN_ELEVATOR) {
            updateAnimationTick(currentAniSpeed, currentTotalFrames, player.getCurrentState());
        }

        // Disegna lo sprite corrente dall'animazione selezionata
        // Assicurati che aniIndex non superi i limiti dell'array corrente
        int frameToDraw = aniIndex % currentAnimation.length; // Usa il modulo per sicurezza
        int drawX = (int) (player.getX() * SCALE);
        int drawY = (int) (player.getY() * SCALE);
        int drawWidth = (int) (player.getWidth() * SCALE);
        int drawHeight = (int) (player.getHeight() * SCALE);
        if(player.getDirection() == RIGHT)
        	g.drawImage(currentAnimation[frameToDraw], drawX, drawY, drawWidth, drawHeight, null);
        else // flip left
        	g.drawImage(currentAnimation[frameToDraw], drawX + drawWidth, drawY, -drawWidth, drawHeight, null);
        // Disegna hitbox sprite per debug visivo
//        drawHitbox(g, player.getX(), player.getY(), player.getWidth(), player.getHeight(), new Color(0, 255, 0, 50));
//        drawHitbox(g, hbX, hbY, hbWidth, hbHeight, new Color(255, 0, 0, 50));
        
        
        // ------- HITBOX PER DEBUG
//        Rectangle2D.Float playerHitbox = player.getHitbox();
//        drawHitbox(g, playerHitbox, new Color(255, 0, 0, 50)); // hitbox LOGICA
    }
	
	private void drawHitbox(Graphics g, int drawX, int drawY, int width, int height, Color color) {
		//Hitbox
//		System.out.println(""+ drawX + " | " + drawY + " | " + width + " | " + height);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GREEN); // Colore per la hitbox
        g2d.setStroke(new BasicStroke(1)); // Linea sottile
        g2d.drawRect(drawX, drawY, width, height);
        g2d.setColor(color); // Colore semitrasparente per riempire
        g2d.fillRect(drawX, drawY, width, height);
	}
	
	private void drawHitbox(Graphics g, Rectangle2D.Float hitbox, Color color) {
		//Hitbox
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED); // Colore per la hitbox
        g2d.setStroke(new BasicStroke(1)); // Linea sottile
        g2d.drawRect((int)(hitbox.x * SCALE), (int)(hitbox.y * SCALE), (int)(hitbox.width * SCALE), (int)(hitbox.height * SCALE));
        g2d.setColor(color); // Colore semitrasparente per riempire
        g2d.fillRect((int)(hitbox.x * SCALE), (int)(hitbox.y * SCALE), (int)(hitbox.width * SCALE), (int)(hitbox.height * SCALE));
	}
	

	// Aggiorna il tick e l'indice dell'animazione con parametri dinamici
//    private void updateAnimationTick(int speed, int totalFrames) {
//        aniTick++;
//        if (aniTick >= speed) {
//            aniTick = 0;
//            aniIndex++;
//            if (aniIndex >= totalFrames) {
//                aniIndex = 0;
//            }
//        }
//    }
	
	private void updateAnimationTick(int frameDurationMillis, int totalFrames, PlayerState currentState) {
	    long now = System.currentTimeMillis();

	    if (now - lastFrameTime >= frameDurationMillis) {
	        lastFrameTime = now;
	        int oldAniIndex = aniIndex; // Memorizziamo il frame precedente
	        aniIndex = (aniIndex + 1) % totalFrames;
	        
	        // Riproduci il suono solo se il frame è cambiato e siamo nello stato di corsa
	        if (aniIndex != oldAniIndex && currentState == PlayerState.RUNNING) {
	            // Controlla se il nuovo frame è uno di quelli che triggerano il passo
	            if (aniIndex == STEP_FRAME_1 || aniIndex == STEP_FRAME_2) {
	                AudioManager.getInstance().play("step_2");
	            }
	        }
	    }
	}
}