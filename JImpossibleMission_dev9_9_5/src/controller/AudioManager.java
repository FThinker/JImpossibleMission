package controller;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Manages all audio playback for the game, including sound effects (SFX) and music.
 * This class follows the Singleton pattern to provide a centralized point of control for audio.
 * It pre-loads all sound effects to ensure low-latency playback during gameplay.
 */
public class AudioManager {
    private static AudioManager instance;

    /**
     * A map to store pre-loaded sound effect clips for quick access.
     */
    private Map<String, Clip> soundClips;
    
    /**
     * A dedicated clip for handling background music, allowing it to be looped and stopped independently.
     */
    private Clip musicClip;
    
    /**
     * A dedicated clip for handling menu music.
     */
    private Clip menuMusicClip;

    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes the sound clips map and pre-loads all sounds.
     */
    private AudioManager() {
        soundClips = new HashMap<>();
        loadAllSounds();
    }

    /**
     * Returns the single instance of the AudioManager, creating it if necessary.
     *
     * @return The singleton instance of AudioManager.
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    /**
     * Loads all sound effects and music tracks into memory at startup.
     */
    private void loadAllSounds() {
        loadSound("jump", "/audio/sfx/jump.wav");
        loadSound("step", "/audio/sfx/step.wav");
        loadSound("step_2", "/audio/sfx/step_2.wav");
        loadSound("death", "/audio/sfx/death.wav");
        loadSound("freeze", "/audio/sfx/freeze.wav");
        loadSound("hover", "/audio/sfx/hover.wav");
        loadSound("confirm", "/audio/sfx/confirm.wav");
        loadSound("pause", "/audio/sfx/pause.wav");
        loadSound("unpause", "/audio/sfx/unpause.wav");
        loadSound("piece_found", "/audio/sfx/piece_found.wav");
        loadSound("click", "/audio/sfx/click.wav");
        loadSound("click_2", "/audio/sfx/click_2.wav");
        loadSound("woosh", "/audio/sfx/woosh.wav");
        loadSound("keystroke", "/audio/sfx/keystroke.wav");
        loadSound("energy_beam", "/audio/sfx/energy_beam.wav");
        loadSound("elevator_moving", "/audio/sfx/elevator_moving.wav");
        loadSound("robot_bleep", "/audio/sfx/robot_bleep.wav");
        loadSound("menu_theme", "/audio/bgm/theme1.wav");
    }

    /**
     * Loads a single sound file from the given path and stores it as a Clip in the soundClips map.
     *
     * @param name     The key to associate with the sound.
     * @param filename The resource path to the audio file.
     */
    public void loadSound(String name, String filename) {
        try {
            InputStream is = getClass().getResourceAsStream(filename);
            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            soundClips.put(name, clip);
        } catch (Exception e) {
            System.err.println("Error loading sound: " + filename);
            e.printStackTrace();
        }
    }

    /**
     * Plays a pre-loaded sound effect once.
     * If the sound is already playing, it is stopped and restarted from the beginning.
     *
     * @param name The key of the sound to play.
     */
    public void play(String name) {
        Clip clip = soundClips.get(name);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0); // Rewind to the start
            clip.start();
        }
    }
    
    /**
     * Plays a pre-loaded sound effect in a continuous loop.
     * If the sound is already looping, this method does nothing.
     *
     * @param name The key of the sound to loop.
     */
    public void loop(String name) {
        Clip clip = soundClips.get(name);
        if (clip != null && !clip.isRunning()) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    /**
     * Stops a sound effect that is currently playing or looping.
     *
     * @param name The key of the sound to stop.
     */
    public void stop(String name) {
        Clip clip = soundClips.get(name);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    /**
     * Streams and plays a music file in a continuous loop.
     * If other music is already playing, it will be stopped first.
     *
     * @param filename The resource path to the music file.
     */
    public void loopMusic(String filename) {
        stopMusic();
        
        try {
            InputStream is = getClass().getResourceAsStream(filename);
            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioIn);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println("Error loading music: " + filename);
            e.printStackTrace();
        }
    }

    /**
     * Stops the currently playing background music.
     */
    public void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
        }
    }
    
    /**
     * Plays the main menu music in a loop.
     * Stops any other playing music before starting.
     *
     * @param name The key of the menu music clip.
     */
    public void loopMenuMusic(String name) {
        stopAllMusic();
        
        menuMusicClip = soundClips.get(name);
        if (menuMusicClip != null && !menuMusicClip.isRunning()) {
            menuMusicClip.setFramePosition(0);
            menuMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * Stops the main menu music if it is playing.
     */
    public void stopMenuMusic() {
        if (menuMusicClip != null && menuMusicClip.isRunning()) {
            menuMusicClip.stop();
        }
    }

    /**
     * A helper method to stop all currently playing music (both in-game and menu).
     */
    private void stopAllMusic() {
        stopMusic();
        stopMenuMusic();
    }
    
    /**
     * Stops all currently playing sound effects and music clips.
     * This is useful when pausing the game or transitioning between states.
     */
    public void stopAllSounds() {
        for (Clip clip : soundClips.values()) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        }
    }
}