package view;

import model.Enemy;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import static model.Directions.*;
import static model.GameConstants.*;

/**
 * Renders an enemy entity on the screen.
 * This class selects the appropriate animation based on the enemy's current state
 * and calculates the correct animation frame to display.
 */
public class EnemyView {

	public EnemyView() {
	}

	/**
     * Draws a single enemy on the screen.
     *
     * @param g       The Graphics context to draw on.
     * @param enemy   The enemy object containing state and position data.
     * @param animSet The set of pre-loaded animations for this enemy type.
     */
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
			// Fallback to a default animation if state is unknown
			currentAnimation = animSet.getStandingAnimations();
			currentTotalFrames = animSet.getTotalStandingFrames();
			break;
		}

		if (currentAnimation == null || currentAnimation.length == 0 || enemy == null) {
			return;
		}

		int aniIndex = calculateAniIndex(enemy.getStateStartTime(), System.currentTimeMillis(),
				enemy.getDurationForState(enemy.getState()), currentTotalFrames);

		int frameToDraw = aniIndex % currentAnimation.length;
		int drawX = (int) (enemy.getX() * SCALE);
		int drawY = (int) (enemy.getY() * SCALE);
		int drawWidth = (int) (enemy.getWidth() * SCALE);
		int drawHeight = (int) (enemy.getHeight() * SCALE);
		
		switch (enemy.getState()) {
		case IDLE:
		case MOVING:
			if (enemy.getDirection() == RIGHT)
				g.drawImage(currentAnimation[frameToDraw], drawX, drawY, drawWidth, drawHeight, null);
			else // Flip horizontally for left direction
				g.drawImage(currentAnimation[frameToDraw], drawX + drawWidth, drawY, -drawWidth, drawHeight, null);
			break;
		case TURNING:
			if (enemy.getDirection() == LEFT)
				g.drawImage(currentAnimation[frameToDraw], drawX, drawY, drawWidth, drawHeight, null);
			else // Flip horizontally for right direction
				g.drawImage(currentAnimation[frameToDraw], drawX + drawWidth, drawY, -drawWidth, drawHeight, null);
			break;
		case ATTACKING:
			if (enemy.getDirection() == RIGHT) {
				g.drawImage(currentAnimation[frameToDraw], drawX, drawY, (int) (96 * 2 * SCALE), drawHeight, null);
			} else { // Flip horizontally for left direction
				g.drawImage(currentAnimation[frameToDraw], drawX + (int) ((96 - 64) * 2 * SCALE), drawY,
						-(int) (96 * 2 * SCALE), drawHeight, null);
			}
			break;
		default:
			break;
		}
	}

	/**
     * Calculates the current animation frame index based on the elapsed time in the current state.
     *
     * @param stateStartTime The timestamp when the current state began.
     * @param currentTime    The current timestamp.
     * @param totalDuration  The total duration of the animation for the current state.
     * @param totalFrames    The total number of frames in the animation sequence.
     * @return The calculated index of the frame to be displayed.
     */
	public int calculateAniIndex(long stateStartTime, long currentTime, long totalDuration, int totalFrames) {
		long elapsed = currentTime - stateStartTime;

		if (elapsed >= totalDuration) {
			return totalFrames - 1; // Hold the last frame if duration is exceeded
		}

		float fraction = (float) elapsed / totalDuration;
		return (int) (fraction * totalFrames);
	}
}