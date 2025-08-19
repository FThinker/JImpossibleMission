// in view/EnemyAnimationSet.java
package view;

import java.awt.image.BufferedImage;

public class EnemyAnimationSet {
    // Le stesse costanti che erano in EnemyView
    private final int SPRITE_WIDTH = 32;
    private final int SPRITE_HEIGHT = 32;
    private final int TOTAL_STANDING_FRAMES = 4;
    private final int TOTAL_TURNING_FRAMES = 7;
    private final int TOTAL_ATTACKING_FRAMES = 8;
    private final int ATTACKING_SPRITE_WIDTH = 96;

    // I campi per contenere le animazioni caricate
    private BufferedImage[] standingAnimations;
    private BufferedImage[] turningAnimations;
    private BufferedImage[] attackingAnimations;

    public EnemyAnimationSet(BufferedImage standingAtlas, BufferedImage turningAtlas, BufferedImage attackingAtlas) {
        loadAnimations(standingAtlas, turningAtlas, attackingAtlas);
    }

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

    // Getters per accedere alle animazioni
    public BufferedImage[] getStandingAnimations() { return standingAnimations; }
    public BufferedImage[] getTurningAnimations() { return turningAnimations; }
    public BufferedImage[] getAttackingAnimations() { return attackingAnimations; }
    
    // Getters per le costanti dei frame
    public int getTotalStandingFrames() { return TOTAL_STANDING_FRAMES; }
    public int getTotalTurningFrames() { return TOTAL_TURNING_FRAMES; }
    public int getTotalAttackingFrames() { return TOTAL_ATTACKING_FRAMES; }
}