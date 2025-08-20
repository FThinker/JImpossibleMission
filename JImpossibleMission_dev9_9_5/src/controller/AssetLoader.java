package controller;

import javax.imageio.ImageIO;
import model.EnemyType;
import view.EnemyAnimationSet;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all game assets, such as images and animations.
 * This class follows the Singleton pattern to ensure that assets are loaded only once
 * and are accessible globally from a single point.
 */
public class AssetLoader {
    private static AssetLoader instance;
    private Map<String, BufferedImage> images;
    
    /**
     * A map to store user avatars, publicly accessible for convenience.
     */
    public static Map<String, BufferedImage> avatars = new HashMap<>();
    private static final String[] AVATAR_IDS = {"avatar1", "avatar2", "avatar3", "avatar4", "avatar5", "avatar6"};
    private static Map<EnemyType, EnemyAnimationSet> enemyAnimations = new HashMap<>();

    /**
     * Private constructor to prevent instantiation from outside the class.
     * Initializes asset collections and triggers the loading of all game resources.
     */
    private AssetLoader() {
        images = new HashMap<>();
        loadAllAssets();
        loadAvatars();
        loadEnemyAnimations();
    }

    /**
     * Returns the single instance of the AssetLoader, creating it if it doesn't exist.
     *
     * @return The singleton instance of AssetLoader.
     */
    public static AssetLoader getInstance() {
        if (instance == null) {
            instance = new AssetLoader();
        }
        return instance;
    }

    /**
     * Loads all primary game assets into memory. This includes sprites for the player,
     * tiles, enemies, and elevator components.
     */
    private void loadAllAssets() {
        // Player sprites
        loadAsset("/png/player/Running_Atlas_32x32.png", "runningAtlas");
        loadAsset("/png/player/Idle_32x32.png", "idleAtlas");
        loadAsset("/png/player/Jumping_Atlas_32x32.png", "jumpingAtlas");
        loadAsset("/png/player/Searching_Atlas_32x32.png", "searchingAtlas");
        
        // Tile sprites
        loadAsset("/png/tiles/Wall_Tile_32x32.png", "wallTile");
        loadAsset("/png/tiles/Platform_Tile_32x32.png", "platformTile");
        loadAsset("/png/tiles/Lift_Tile_32x32.png", "liftTile");
        loadAsset("/png/tiles/Computer_Tile_32x32.png", "pcTile");
        loadAsset("/png/tiles/Jukebox_Tile_32x32.png", "jukeboxTile");
        loadAsset("/png/tiles/Cigarette_Tile_32x32.png", "cigaretteTile");
        loadAsset("/png/tiles/Library_Tile_32x32.png", "libraryTile");
        loadAsset("/png/tiles/Candy_Tile_32x32.png", "candyTile");
        loadAsset("/png/tiles/Desk_Tile_32x32.png", "deskTile");
        
        // Enemy sprites (standard and white variants)
        loadAsset("/png/enemies/Robot_Flashing_Atlas_32x32.png", "flashingAtlas");
        loadAsset("/png/enemies/Robot_Turning_Atlas_32x32.png", "turningAtlas");
        loadAsset("/png/enemies/Robot_Attacking_Atlas_96x32.png", "attackingAtlas");
        loadAsset("/png/enemies/Robot_Flashing_Atlas_32x32_WHITE.png", "flashingAtlasWHITE");
        loadAsset("/png/enemies/Robot_Turning_Atlas_32x32_WHITE.png", "turningAtlasWHITE");
        loadAsset("/png/enemies/Robot_Attacking_Atlas_96x32_WHITE.png", "attackingAtlasWHITE");
        
        // Elevator sprites
        loadAsset("/png/elevator/ElevatorCabin.png", "elevatorCabin");
        loadAsset("/png/elevator/ElevatorShaft.png", "elevatorShaft");
    }

    /**
     * Loads a single image asset from the given path and stores it in the images map with a specified key.
     *
     * @param path The resource path to the image file.
     * @param key  The key to associate with the loaded image for later retrieval.
     */
    private void loadAsset(String path, String key) {
        InputStream is = getClass().getResourceAsStream(path);
        try {
            BufferedImage img = ImageIO.read(is);
            images.put(key, img);
        } catch (IOException e) {
            System.err.println("Error loading image: " + path);
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Retrieves a pre-loaded image by its key.
     *
     * @param key The key of the image to retrieve.
     * @return The BufferedImage associated with the key, or null if not found.
     */
    public BufferedImage getImage(String key) {
        return images.get(key);
    }
    
    /**
     * Loads all user avatar images from the resources.
     */
    public void loadAvatars() {
        for (String id : AVATAR_IDS) {
            try {
                InputStream is = AssetLoader.class.getResourceAsStream("/avatars/" + id + ".png");
                avatars.put(id, ImageIO.read(is));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Retrieves a pre-loaded avatar image by its ID.
     *
     * @param id The ID of the avatar to retrieve (e.g., "avatar1").
     * @return The BufferedImage for the avatar, or null if not found.
     */
    public BufferedImage getAvatar(String id) {
        return avatars.get(id);
    }
    
    /**
     * Loads and creates animation sets for all enemy types.
     * This method retrieves the necessary sprite sheets and assembles them into
     * {@link EnemyAnimationSet} objects, which are then stored for each enemy type.
     */
    public void loadEnemyAnimations() {
        // Standard robot (STANDING_ROBOT) animation set
        BufferedImage standingAtlas = getImage("flashingAtlas");
        BufferedImage turningAtlas = getImage("turningAtlas");
        BufferedImage attackingAtlas = getImage("attackingAtlas");
        EnemyAnimationSet standardRobotSet = new EnemyAnimationSet(standingAtlas, turningAtlas, attackingAtlas);
        enemyAnimations.put(EnemyType.STANDING_ROBOT, standardRobotSet);
        
        // White robot (MOVING_ROBOT) animation set
        BufferedImage standingAtlasWhite = getImage("flashingAtlasWHITE");
        BufferedImage turningAtlasWhite = getImage("turningAtlasWHITE");
        BufferedImage attackingAtlasWhite = getImage("attackingAtlasWHITE");
        EnemyAnimationSet whiteRobotSet = new EnemyAnimationSet(standingAtlasWhite, turningAtlasWhite, attackingAtlasWhite);
        enemyAnimations.put(EnemyType.MOVING_ROBOT, whiteRobotSet);
    }
    
    /**
     * Retrieves the complete set of animations for a specific enemy type.
     *
     * @param type The {@link EnemyType} for which to get the animations.
     * @return The corresponding {@link EnemyAnimationSet}.
     */
    public EnemyAnimationSet getAnimationsFor(EnemyType type) {
        return enemyAnimations.get(type);
    }
}