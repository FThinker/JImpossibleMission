package controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

// La classe ora estende KeyAdapter e implementa le interfacce del mouse
public class InputHandler extends KeyAdapter implements MouseListener, MouseMotionListener {
    // Stato dei tasti di movimento
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private boolean jumpPressed;
    private boolean ePressed;

    private Set<Integer> pressedKeys = new HashSet<>();

    // NUOVI CAMPI: Stato del mouse
    private int mouseX, mouseY;
    private boolean mouseButtonPressed;
    private int mouseButtonCode;

    public InputHandler() {
        resetAllInputs();
    }

    // Metodo per resettare TUTTI gli input (tastiera e mouse)
    public void resetAllInputs() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        jumpPressed = false;
        ePressed = false;
        pressedKeys.clear();
        resetMouse(); // Chiama il nuovo metodo di reset del mouse
    }
    
    // Metodo per resettare solo i tasti della tastiera
    public void resetKeys() {
        resetAllInputs(); // Per semplicità, ora questo resetta tutto
    }
    
    // NUOVO: Metodo specifico per resettare lo stato del mouse dopo un click
    public void resetMouse() {
        mouseButtonPressed = false;
        mouseButtonCode = -1; // -1 indica nessun bottone premuto
    }

    public void resetVerticalKeys() {
        upPressed = false;
        downPressed = false;
    }

    // --- GETTER TASTIERA ---
    public boolean isUpPressed() { return upPressed; }
    public boolean isDownPressed() { return downPressed; }
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isJumpPressed() { return jumpPressed; }
    public boolean isEPressed() { return ePressed; }
    public boolean isKeyPressed(int keyCode) { return pressedKeys.contains(keyCode); }

    // --- NUOVI GETTER MOUSE ---
    public int getMouseX() { return mouseX; }
    public int getMouseY() { return mouseY; }

    /**
     * Controlla se un bottone specifico del mouse è stato premuto.
     * @param buttonCode (es. MouseEvent.BUTTON1 per il click sinistro)
     * @return true se il bottone specificato è premuto, false altrimenti.
     */
    public boolean isMouseButtonPressed(int buttonCode) {
        return mouseButtonPressed && this.mouseButtonCode == buttonCode;
    }

    // --- METODI KEYLISTENER ---
    @Override
    public void keyPressed(KeyEvent e) {
        // ... il tuo codice esistente va benissimo qui ...
        int keyCode = e.getKeyCode();
        pressedKeys.add(keyCode);
        
        switch (keyCode) {
            case KeyEvent.VK_SPACE: jumpPressed = true; break;
            case KeyEvent.VK_UP: case KeyEvent.VK_W: upPressed = true; break;
            case KeyEvent.VK_DOWN: case KeyEvent.VK_S: downPressed = true; break;
            case KeyEvent.VK_LEFT: case KeyEvent.VK_A: leftPressed = true; break;
            case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: rightPressed = true; break;
            case KeyEvent.VK_E: ePressed = true; break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ... il tuo codice esistente va benissimo qui ...
        int keyCode = e.getKeyCode();
        pressedKeys.remove(keyCode);
        
        switch (keyCode) {
            case KeyEvent.VK_SPACE: jumpPressed = false; break;
            case KeyEvent.VK_UP: case KeyEvent.VK_W: upPressed = false; break;
            case KeyEvent.VK_DOWN: case KeyEvent.VK_S: downPressed = false; break;
            case KeyEvent.VK_LEFT: case KeyEvent.VK_A: leftPressed = false; break;
            case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: rightPressed = false; break;
            case KeyEvent.VK_E: ePressed = false; break;
        }
    }
    
    // --- NUOVI METODI MOUSELISTENER E MOUSEMOTIONLISTENER ---
    @Override
    public void mousePressed(MouseEvent e) {
        mouseButtonPressed = true;
        mouseButtonCode = e.getButton();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Potremmo resettare qui, ma è meglio farlo manualmente dagli handler
        // per essere sicuri che il click sia stato "consumato".
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Aggiorna le coordinate anche durante il trascinamento
        mouseX = e.getX();
        mouseY = e.getY();
    }

    // Metodi non utilizzati ma richiesti dalle interfacce
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
    
    /**
     * Controlla se un tasto è stato premuto e "consuma" l'evento,
     * rimuovendolo dalla lista. Ideale per azioni singole come navigare nei menu.
     * @param keyCode Il codice del tasto da controllare.
     * @return true se il tasto era premuto, false altrimenti.
     */
    public boolean consumeKeyPress(int keyCode) {
        if (pressedKeys.contains(keyCode)) {
            pressedKeys.remove(keyCode); // Rimuovi il tasto dopo averlo controllato
            return true;
        }
        return false;
    }
}