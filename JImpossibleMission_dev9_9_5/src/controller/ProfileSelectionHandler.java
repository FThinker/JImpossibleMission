package controller;

import model.GameModel;
import model.GameState;
import model.UserProfile;
import javax.swing.JOptionPane;
import static model.GameConstants.SCALE;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Observable;

/**
 * Handles user input and logic for the profile selection screen.
 * This class manages loading profiles, navigating the list, creating new profiles,
 * and deleting existing ones. It extends {@link Observable} to notify the view of changes.
 */
@SuppressWarnings("deprecation")
public class ProfileSelectionHandler extends Observable {

    private GameModel gameModel;
    private InputHandler inputHandler;
    private List<UserProfile> profiles;
    private int selectedIndex = 0;

    private Rectangle createButtonBounds = new Rectangle((int)(195 * SCALE), (int)(300 * SCALE), (int)(250 * SCALE), (int)(25 * SCALE));
    private boolean createHover = false;
    
    private List<Rectangle> deleteButtonBounds = new java.util.ArrayList<>();
    
    private boolean needsRefresh = true;
    private boolean wasAnyButtonHovered = false;
    
    /**
     * Constructs a ProfileSelectionHandler.
     *
     * @param gameModel    The main game model.
     * @param inputHandler The handler for detecting user input.
     */
    public ProfileSelectionHandler(GameModel gameModel, InputHandler inputHandler) {
        this.gameModel = gameModel;
        this.inputHandler = inputHandler;
        refreshProfiles();
        setChanged();
        notifyObservers();
    }
    
    /**
     * Flags that the profile list needs to be reloaded from disk.
     * @param needsRefresh True to force a refresh on the next update.
     */
    public void setNeedsRefresh(boolean needsRefresh) {
        this.needsRefresh = needsRefresh;
    }

    /**
     * Reloads the list of user profiles from disk and updates the view.
     */
    public void refreshProfiles() {
        this.profiles = ProfileManager.loadAllProfiles();
        if (selectedIndex >= profiles.size()) {
            selectedIndex = Math.max(0, profiles.size() - 1);
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Handles user input for profile selection, creation, and deletion.
     */
    public void handleInput() {
        if (needsRefresh) {
            refreshProfiles();
            needsRefresh = false;
        }
        
        createHover = createButtonBounds.contains(inputHandler.getMouseX(), inputHandler.getMouseY());
        boolean isCurrentlyHovered = createHover;
		
        if (isCurrentlyHovered && !wasAnyButtonHovered) {
            AudioManager.getInstance().play("click_2");
        }
        
        wasAnyButtonHovered = isCurrentlyHovered;

        if (inputHandler.consumeKeyPress(KeyEvent.VK_DOWN)) {
            if (!profiles.isEmpty()) {
            	AudioManager.getInstance().play("click");
                selectedIndex = (selectedIndex + 1) % profiles.size();
                setChanged();
                notifyObservers();
            }
        }
        if (inputHandler.consumeKeyPress(KeyEvent.VK_UP)) {
            if (!profiles.isEmpty()) {
            	AudioManager.getInstance().play("click");
                selectedIndex = (selectedIndex - 1 + profiles.size()) % profiles.size();
                setChanged();
                notifyObservers();
            }
        }

        if (inputHandler.consumeKeyPress(KeyEvent.VK_ENTER)) {
        	AudioManager.getInstance().play("confirm");
            if (!profiles.isEmpty()) {
                loadSelectedProfile();
            }
        }

        if (inputHandler.isMouseButtonPressed(MouseEvent.BUTTON1)) {
        	if (createHover) {
        		AudioManager.getInstance().play("click");
                createNewProfile();
            } else {
                int mx = inputHandler.getMouseX();
                int my = inputHandler.getMouseY();
                for (int i = 0; i < deleteButtonBounds.size(); i++) {
                    if (deleteButtonBounds.get(i).contains(mx, my)) {
                    	AudioManager.getInstance().play("click");
                        deleteProfile(i);
                        break;
                    }
                }
            }
            inputHandler.resetMouse();
        }
    }

    /**
     * Loads the currently selected profile and transitions to the main menu.
     */
    private void loadSelectedProfile() {
        UserProfile selectedProfile = profiles.get(selectedIndex);
        gameModel.setActiveProfile(selectedProfile);
        gameModel.setGameState(GameState.HOMESCREEN);
        System.out.println("Profile loaded: " + selectedProfile.getNickname());
    }

    /**
     * Opens dialogs to prompt the user for a new profile nickname and avatar,
     * then creates and saves the new profile.
     */
    private void createNewProfile() {
        String nickname = JOptionPane.showInputDialog(null, "Enter nickname:", "Create New Profile", JOptionPane.PLAIN_MESSAGE);
        
        if (nickname != null && !nickname.trim().isEmpty()) {
            boolean exists = profiles.stream().anyMatch(p -> p.getNickname().equalsIgnoreCase(nickname));
            if (exists) {
                JOptionPane.showMessageDialog(null, "Nickname already in use.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Object[] avatarOptions = {"Avatar 1", "Avatar 2", "Avatar 3", "Avatar 4", "Avatar 5", "Avatar 6"};
                String selectedAvatar = (String) JOptionPane.showInputDialog(
                    null, 
                    "Choose your avatar:", 
                    "Avatar Selection", 
                    JOptionPane.PLAIN_MESSAGE, 
                    null, 
                    avatarOptions, 
                    avatarOptions[0]
                );

                String avatarId = "avatar1";
                if (selectedAvatar != null) {
                    avatarId = "avatar" + selectedAvatar.replaceAll("[^0-9]", "");
                }

                UserProfile newProfile = new UserProfile(nickname, avatarId);
                ProfileManager.saveProfile(newProfile);
                
                setNeedsRefresh(true);
                selectedIndex = profiles.size();
                refreshProfiles(); 
                
                System.out.println("New profile created: " + nickname);
            }
        }
    }
    
    /**
     * Prompts the user for confirmation and deletes the selected profile.
     * @param profileIndex The index of the profile to delete from the list.
     */
    private void deleteProfile(int profileIndex) {
        UserProfile profileToDelete = profiles.get(profileIndex);

        int choice = JOptionPane.showConfirmDialog(
            null,
            "Are you sure you want to delete profile '" + profileToDelete.getNickname() + "'?\nThis action is irreversible.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            ProfileManager.deleteProfile(profileToDelete);
            refreshProfiles();
        }
    }
    
    /**
     * Updates the clickable bounds for the delete buttons.
     * This method is called by the view during its draw cycle.
     * @param bounds A list of rectangles representing the delete button areas.
     */
    public void updateDeleteButtonBounds(List<Rectangle> bounds) {
        this.deleteButtonBounds = bounds;
    }

    // --- GETTERS for the View ---
    public List<UserProfile> getProfiles() { return profiles; }
    public int getSelectedIndex() { return selectedIndex; }
    public Rectangle getCreateButtonBounds() { return createButtonBounds; }
    public boolean isCreateHover() { return createHover; }
}