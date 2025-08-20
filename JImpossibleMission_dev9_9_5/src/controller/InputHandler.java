package controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Centralized input manager for handling all keyboard and mouse events.
 * It tracks the state of pressed keys and mouse buttons, providing a clean interface
 * for other parts of the game to query user input.
 */
public class InputHandler extends KeyAdapter implements MouseListener, MouseMotionListener {
    // Keyboard state flags
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private boolean jumpPressed;
    private boolean ePressed;
    private Set<Integer> pressedKeys = new HashSet<>();

    // Mouse state fields
    private int mouseX, mouseY;
    private boolean mouseButtonPressed;
    private int mouseButtonCode;

    /**
     * Constructs an InputHandler and initializes all input states.
     */
    public InputHandler() {
        resetAllInputs();
    }

    /**
     * Resets all keyboard and mouse input states to their default (unpressed) values.
     */
    public void resetAllInputs() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        jumpPressed = false;
        ePressed = false;
        pressedKeys.clear();
        resetMouse();
    }
    
    /**
     * Resets only the keyboard input states.
     * For simplicity, this currently resets all inputs.
     */
    public void resetKeys() {
        resetAllInputs();
    }
    
    /**
     * Resets the mouse button state after a click has been processed.
     */
    public void resetMouse() {
        mouseButtonPressed = false;
        mouseButtonCode = -1; // -1 indicates no button pressed
    }

    /**
     * Resets only the vertical movement keys (up/down).
     */
    public void resetVerticalKeys() {
        upPressed = false;
        downPressed = false;
    }

    // --- KEYBOARD GETTERS ---
    public boolean isUpPressed() { return upPressed; }
    public boolean isDownPressed() { return downPressed; }
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isJumpPressed() { return jumpPressed; }
    public boolean isEPressed() { return ePressed; }
    
    /**
     * Checks if a specific key is currently being held down.
     * @param keyCode The key code to check (e.g., {@link KeyEvent#VK_ESCAPE}).
     * @return True if the key is pressed, false otherwise.
     */
    public boolean isKeyPressed(int keyCode) { return pressedKeys.contains(keyCode); }

    // --- MOUSE GETTERS ---
    public int getMouseX() { return mouseX; }
    public int getMouseY() { return mouseY; }

    /**
     * Checks if a specific mouse button is currently pressed.
     * @param buttonCode The mouse button code (e.g., {@link MouseEvent#BUTTON1}).
     * @return True if the specified button is pressed, false otherwise.
     */
    public boolean isMouseButtonPressed(int buttonCode) {
        return mouseButtonPressed && this.mouseButtonCode == buttonCode;
    }

    // --- KEYLISTENER METHODS ---
    @Override
    public void keyPressed(KeyEvent e) {
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
    
    // --- MOUSELISTENER AND MOUSEMOTIONLISTENER METHODS ---
    @Override
    public void mousePressed(MouseEvent e) {
        mouseButtonPressed = true;
        mouseButtonCode = e.getButton();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Reset manually in handlers to ensure the click is "consumed".
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    // Unused but required by interfaces
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    
    /**
     * Checks if a key was pressed and then "consumes" the event by removing it from the set.
     * This is ideal for single-press actions like navigating menus.
     *
     * @param keyCode The key code to check.
     * @return True if the key was pressed, false otherwise.
     */
    public boolean consumeKeyPress(int keyCode) {
        if (pressedKeys.contains(keyCode)) {
            pressedKeys.remove(keyCode);
            return true;
        }
        return false;
    }
}