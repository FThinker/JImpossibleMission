package controller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AssetLoader {
    private static AssetLoader instance; // Per il pattern Singleton
    private Map<String, BufferedImage> images;
    
    public static Map<String, BufferedImage> avatars = new HashMap<>();
    private static final String[] AVATAR_IDS = {"avatar1", "avatar2", "avatar3", "avatar4", "avatar5", "avatar6"};

    private AssetLoader() {
        images = new HashMap<>();
        // Pre-carica le immagini all'avvio, o caricale on-demand
        loadAllAssets(); // Metodo per caricare tutto
        loadAvatars();
    }

    public static AssetLoader getInstance() {
        if (instance == null) {
            instance = new AssetLoader();
        }
        return instance;
    }

    private void loadAllAssets() {
        // Player
        loadAsset("/png/player/Running_Atlas_32x32.png", "runningAtlas");
        loadAsset("/png/player/Idle_32x32.png", "idleAtlas");
        loadAsset("/png/player/Jumping_Atlas_32x32.png", "jumpingAtlas");
        loadAsset("/png/player/Searching_Atlas_32x32.png", "searchingAtlas");
        // Tiles
        loadAsset("/png/tiles/Wall_Tile_32x32.png", "wallTile");
        loadAsset("/png/tiles/Platform_Tile_32x32.png", "platformTile");
        loadAsset("/png/tiles/Lift_Tile_32x32.png", "liftTile");
        loadAsset("/png/tiles/Computer_Tile_32x32.png", "pcTile");
        loadAsset("/png/tiles/Jukebox_Tile_32x32.png", "jukeboxTile");
        loadAsset("/png/tiles/Cigarette_Tile_32x32.png", "cigaretteTile");
        loadAsset("/png/tiles/Library_Tile_32x32.png", "libraryTile");
        loadAsset("/png/tiles/Candy_Tile_32x32.png", "candyTile");
        loadAsset("/png/tiles/Desk_Tile_32x32.png", "deskTile");
        // Enemies
        loadAsset("/png/enemies/Robot_Flashing_Atlas_32x32.png", "flashingAtlas");
        loadAsset("/png/enemies/Robot_Turning_Atlas_32x32.png", "turningAtlas");
        loadAsset("/png/enemies/Robot_Attacking_Atlas_96x32.png", "attackingAtlas");
        // Elevator
        loadAsset("/png/elevator/ElevatorCabin.png", "elevatorCabin");
        loadAsset("/png/elevator/ElevatorShaft.png", "elevatorShaft");
    }

    private void loadAsset(String path, String key) {
        InputStream is = getClass().getResourceAsStream(path);
        try {
            BufferedImage img = ImageIO.read(is);
            images.put(key, img);
//            System.out.println("Caricata immagine: " + key);
        } catch (IOException e) {
            System.err.println("Errore nel caricamento dell'immagine: " + path);
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public BufferedImage getImage(String key) {
        return images.get(key);
    }
    
    public void loadAvatars() {
        for (String id : AVATAR_IDS) {
            try {
                InputStream is = AssetLoader.class.getResourceAsStream("/avatars/" + id + ".png");
                avatars.put(id, ImageIO.read(is));
                System.out.println("Loaded avatar: " + id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public BufferedImage getAvatar(String id) {
        return avatars.get(id);
    }
}