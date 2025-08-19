// EnemyView.java (Nuovo file nel package view)
package view;

import controller.AssetLoader;
import model.Enemy;
import model.EnemyType;
import model.Directions;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform; // Per il flipping dell'immagine
import java.awt.geom.Rectangle2D;

import static model.Directions.*;
import static model.GameConstants.*;
import static model.EnemyState.*;

public class EnemyView {

	// NIENTE PIÙ CAMPI PER GLI SPRITE! LA VIEW È "STUPIDA"!

	public EnemyView() {
	}

	public void draw(Graphics g, Enemy enemy, EnemyAnimationSet animSet) {

		BufferedImage[] currentAnimation = null;
		int currentTotalFrames = 0;

		switch (enemy.getState()) {
		case IDLE:
		case MOVING:
			currentAnimation = animSet.getStandingAnimations();
			currentTotalFrames = animSet.getTotalStandingFrames();
			break;
		case TURNING:
			currentAnimation = animSet.getTurningAnimations();
			currentTotalFrames = animSet.getTotalTurningFrames();
			break;
		case ATTACKING:
			currentAnimation = animSet.getAttackingAnimations();
			currentTotalFrames = animSet.getTotalAttackingFrames();
			break;
		default:
			// Fallback nel caso qualcosa vada storto
			currentAnimation = animSet.getStandingAnimations();
			currentTotalFrames = animSet.getTotalStandingFrames();
			break;
		}

		// Controlli di sicurezza
		if (currentAnimation == null || currentAnimation.length == 0 || enemy == null) {
			g.setColor(Color.PINK);
			g.fillRect(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
			return;
		}

//        currentAniSpeed = 100;
//        updateAnimationTick(currentAniSpeed, currentTotalFrames);
		int aniIndex = calculateAniIndex(enemy.getStateStartTime(), System.currentTimeMillis(),
				enemy.getDurationForState(enemy.getState()), currentTotalFrames);

		// Disegna lo sprite corrente dall'animazione selezionata
		// Assicurati che aniIndex non superi i limiti dell'array corrente
		int frameToDraw = aniIndex % currentAnimation.length; // Usa il modulo per sicurezza
		int drawX = (int) (enemy.getX() * SCALE);
		int drawY = (int) (enemy.getY() * SCALE);
		int drawWidth = (int) (enemy.getWidth() * SCALE);
		int drawHeight = (int) (enemy.getHeight() * SCALE);
		switch (enemy.getState()) {
		case IDLE:
		case MOVING:
			if (enemy.getDirection() == RIGHT)
				g.drawImage(currentAnimation[frameToDraw], drawX, drawY, drawWidth, drawHeight, null);
			else // flip left
				g.drawImage(currentAnimation[frameToDraw], drawX + drawWidth, drawY, -drawWidth, drawHeight, null);
//        	g.setColor(Color.GREEN);
//    		g.drawRect(drawX, drawY, drawWidth, drawHeight);
			break;
		case TURNING:
			if (enemy.getDirection() == LEFT)
				g.drawImage(currentAnimation[frameToDraw], drawX, drawY, drawWidth, drawHeight, null);
			else
				g.drawImage(currentAnimation[frameToDraw], drawX + drawWidth, drawY, -drawWidth, drawHeight, null);
//        	g.setColor(Color.GREEN);
//    		g.drawRect(drawX, drawY, drawWidth, drawHeight);
			break;
		case ATTACKING:
			if (enemy.getDirection() == RIGHT) {
				g.drawImage(currentAnimation[frameToDraw], drawX, drawY, (int) (96 * 2 * SCALE), drawHeight, null);
//        		g.setColor(Color.GREEN);
//        		g.drawRect(drawX, drawY, (int) (96 * 2 * SCALE), drawHeight);
			} else { // flip left
				g.drawImage(currentAnimation[frameToDraw], drawX + (int) ((96 - 64) * 2 * SCALE), drawY,
						-(int) (96 * 2 * SCALE), drawHeight, null);
//	        	g.setColor(Color.GREEN);
//	    		g.drawRect(drawX + (int) ((96-64) * 2 * SCALE), drawY, -(int) (96 * 2 * SCALE), drawHeight);
			}
			break;
		default:
		}

//		// Disegna la hitbox (per debug)
//		drawHitbox(g, enemy.getHitbox(), enemy.isFacingRight());
//		drawHitbox(g, enemy.getAttackBox(), enemy.isFacingRight());
	}

	public int calculateAniIndex(long stateStartTime, long currentTime, long totalDuration, int totalFrames) {
		long elapsed = currentTime - stateStartTime;

		if (elapsed >= totalDuration) {
			return totalFrames - 1; // Mostra l’ultimo frame (non ricomincia!)
		}

		float fraction = (float) elapsed / totalDuration;
		return (int) (fraction * totalFrames);
	}

	private void drawHitbox(Graphics g, Rectangle2D.Float hitbox, boolean facingRight) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(facingRight ? new Color(0, 255, 0) : new Color(0, 80, 0)); // Colore diverso per direzione
		g2d.setStroke(new BasicStroke(1));

		float x = hitbox.x;
		float y = hitbox.y;
		float width = hitbox.width;
		float height = hitbox.height;

		// Se la width è negativa, normalizza il rettangolo
		if (width < 0) {
			x += width;
			width = -width;
		}

		g2d.drawRect((int) (x * SCALE), (int) (y * SCALE), (int) (width * SCALE), (int) (height * SCALE));
	}
}