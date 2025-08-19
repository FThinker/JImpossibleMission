package controller;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/*
 *  Versione migliorata di AudioManager: 
 *  pre-carica tutti gli effetti sonori all'avvio del gioco
 *  e gestisce separatamente la musica di sottofondo.
 */
public class AudioManager {
    private static AudioManager instance;

    // Mappa per pre-caricare gli effetti sonori (SFX)
    private Map<String, Clip> soundClips;
    // Clip separata per la musica, per poterla controllare (fermare, loopare)
    private Clip musicClip;
    
    private Clip menuMusicClip;

    private AudioManager() {
        soundClips = new HashMap<>();
        loadAllSounds();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
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
     * Carica un effetto sonoro nella mappa.
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
            System.err.println("Errore nel caricamento del suono: " + filename);
            e.printStackTrace();
        }
    }

    /**
     * Riproduce un effetto sonoro pre-caricato.
     */
    public void play(String name) {
        Clip clip = soundClips.get(name);
        if (clip != null) {
            // Se sta già suonando, lo ferma e lo riavvolge per permettere suoni sovrapposti
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0); // Riavvolge il suono all'inizio
            clip.start();
        }
    }
    
    /**
     * NUOVO METODO: Avvia un suono specifico in loop.
     * Se sta già suonando, non fa nulla.
     */
    public void loop(String name) {
        Clip clip = soundClips.get(name);
        if (clip != null && !clip.isRunning()) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    /**
     * NUOVO METODO: Ferma un suono specifico che era in loop.
     */
    public void stop(String name) {
        Clip clip = soundClips.get(name);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    /**
     * Carica e riproduce in loop una traccia musicale.
     */
    public void loopMusic(String filename) {
        // Ferma la musica precedente, se presente
        stopMusic();
        
        try {
            InputStream is = getClass().getResourceAsStream(filename);
            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioIn);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY); // Imposta il loop infinito
        } catch (Exception e) {
            System.err.println("Errore nel caricamento della musica: " + filename);
            e.printStackTrace();
        }
    }

    /**
     * Ferma la musica attualmente in riproduzione.
     */
    public void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
        }
    }
    
    /**
     * NUOVO METODO: Avvia la musica del menu in loop.
     */
    public void loopMenuMusic(String name) {
        stopAllMusic(); // Ferma qualsiasi musica prima di iniziare quella nuova
        
        menuMusicClip = soundClips.get(name);
        if (menuMusicClip != null && !menuMusicClip.isRunning()) {
            menuMusicClip.setFramePosition(0);
            menuMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * NUOVO METODO: Ferma specificamente la musica del menu.
     */
    public void stopMenuMusic() {
        if (menuMusicClip != null && menuMusicClip.isRunning()) {
            menuMusicClip.stop();
        }
    }

    /**
     * NUOVO METODO HELPER: Ferma tutta la musica (menu e gioco).
     */
    private void stopAllMusic() {
        stopMusic();
        stopMenuMusic();
    }
    
    public void stopAllSounds() {
        // Itera su tutti i clip pre-caricati
        for (Clip clip : soundClips.values()) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        }
    }
}