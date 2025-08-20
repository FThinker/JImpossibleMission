package view;

import java.awt.image.BufferedImage;

/**
 * A data container that holds pre-sliced animation frames for a specific type of enemy.
 * This class loads sprite sheets (atlases) and splits them into individual frames
 * for different animations like standing, turning, and attacking.
 */
public class EnemyAnimationSet {
    private final int SPRITE_WIDTH = 32;
    private final int SPRITE_HEIGHT = 32;
    private final int TOTAL_STANDING_FRAMES = 4;
    private final int TOTAL_TURNING_FRAMES = 7;
    private final int TOTAL_ATTACKING_FRAMES = 8;
    private final int ATTACKING_SPRITE_WIDTH = 96;

    private BufferedImage[] standingAnimations;
    private BufferedImage[] turningAnimations;
    private BufferedImage[] attackingAnimations;

    /**
     * Constructs an EnemyAnimationSet by loading and slicing the provided sprite atlases.
     *
     * @param standingAtlas  The sprite sheet for the standing/moving animation.
     * @param turningAtlas   The sprite sheet for the turning animation.
     * @param attackingAtlas The sprite sheet for the attacking animation.
     */
    public EnemyAnimationSet(BufferedImage standingAtlas, BufferedImage turningAtlas, BufferedImage attackingAtlas) {
        loadAnimations(standingAtlas, turningAtlas, attackingAtlas);
    }

    /**
     * Slices the sprite atlases into individual animation frames.
     *
     * @param standing  The standing animation atlas.
     * @param turning   The turning animation atlas.
     * @param attacking The attacking animation atlas.
     */
    private void loadAnimations(BufferedImage standing, BufferedImage turning, BufferedImage attacking) {
        standingAnimations = new BufferedImage[TOTAL_STANDING_FRAMES];
        for (int i = 0; i < TOTAL_STANDING_FRAMES; i++) {
            standingAnimations[i] = standing.getSubimage(i * SPRITE_WIDTH, 0, SPRITE_WIDTH, SPRITE_HEIGHT);
        }

        turningAnimations = new BufferedImage[TOTAL_TURNING_FRAMES];
        for (int i = 0; i < TOTAL_TURNING_FRAMES; i++) {
            turningAnimations[i] = turning.getSubimage(i * SPRITE_WIDTH, 0, SPRITE_WIDTH, SPRITE_HEIGHT);
        }

        attackingAnimations = new BufferedImage[TOTAL_ATTACKING_FRAMES];
        for (int i = 0; i < TOTAL_ATTACKING_FRAMES; i++) {
            attackingAnimations[i] = attacking.getSubimage(i * ATTACKING_SPRITE_WIDTH, 0, ATTACKING_SPRITE_WIDTH, SPRITE_HEIGHT);
        }
    }

    // --- GETTERS for animation frames and constants ---
    public BufferedImage[] getStandingAnimations() { return standingAnimations; }
    public BufferedImage[] getTurningAnimations() { return turningAnimations; }
    public BufferedImage[] getAttackingAnimations() { return attackingAnimations; }
    
    public int getTotalStandingFrames() { return TOTAL_STANDING_FRAMES; }
    public int getTotalTurningFrames() { return TOTAL_TURNING_FRAMES; }
    public int getTotalAttackingFrames() { return TOTAL_ATTACKING_FRAMES; }
}