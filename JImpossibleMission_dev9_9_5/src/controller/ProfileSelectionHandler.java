// in controller/ProfileSelectionHandler.java
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

@SuppressWarnings("deprecation")
public class ProfileSelectionHandler extends Observable {

    private GameModel gameModel;
    private InputHandler inputHandler;
    private List<UserProfile> profiles;
    private int selectedIndex = 0;

    private Rectangle createButtonBounds = new Rectangle((int)(195 * SCALE), (int)(300 * SCALE), (int)(250 * SCALE), (int)(25 * SCALE));
    private boolean createHover = false;
    
    // NUOVO: Lista per le aree cliccabili dei cestini
    private List<Rectangle> deleteButtonBounds = new java.util.ArrayList<>();
    
    private boolean needsRefresh = true;
    
    private boolean wasAnyButtonHovered = false;
    
    public ProfileSelectionHandler(GameModel gameModel, InputHandler inputHandler) {
        this.gameModel = gameModel;
        this.inputHandler = inputHandler;
        // Carichiamo subito i profili all'inizio
        refreshProfiles();
        setChanged();
        notifyObservers();
    }
    
    // NUOVO: Metodo per dire dall'esterno che la vista ha bisogno di un refresh
    public void setNeedsRefresh(boolean needsRefresh) {
        this.needsRefresh = needsRefresh;
    }

    public void refreshProfiles() {
        this.profiles = ProfileManager.loadAllProfiles();
        if (selectedIndex >= profiles.size()) {
            selectedIndex = Math.max(0, profiles.size() - 1);
        }
        setChanged();
        notifyObservers();
    }


    public void handleInput() {
    	// Ora ricarichiamo la lista solo se necessario!
        if (needsRefresh) {
            refreshProfiles();
            needsRefresh = false; // Aggiornamento completato
        }
        
        createHover = createButtonBounds.contains(inputHandler.getMouseX(), inputHandler.getMouseY());
        
        boolean isCurrentlyHovered = createHover;
		
		// Riproduci il suono solo se ORA siamo in hover, ma PRIMA non lo eravamo
        if (isCurrentlyHovered && !wasAnyButtonHovered) {
            AudioManager.getInstance().play("click_2");
        }
        
        // Aggiorna la variabile di stato per il prossimo frame
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

        // Gestione conferma con Invio
        if (inputHandler.consumeKeyPress(KeyEvent.VK_ENTER)) {
        	AudioManager.getInstance().play("confirm");
            if (!profiles.isEmpty()) {
                loadSelectedProfile();
            }
        }

        // Gestione click del mouse per il bottone "Crea"
        if (inputHandler.isMouseButtonPressed(MouseEvent.BUTTON1)) {
        	if (createHover) {
        		AudioManager.getInstance().play("click");
                createNewProfile();
            } else {
                // NUOVO: Controlla se è stato cliccato un cestino
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

    private void loadSelectedProfile() {
        UserProfile selectedProfile = profiles.get(selectedIndex);
        gameModel.setActiveProfile(selectedProfile);
        gameModel.setGameState(GameState.HOMESCREEN);
        System.out.println("Profilo caricato: " + selectedProfile.getNickname());
    }

    private void createNewProfile() {
        String nickname = JOptionPane.showInputDialog(null, "Inserisci il nickname:", "Crea Nuovo Profilo", JOptionPane.PLAIN_MESSAGE);
        
        if (nickname != null && !nickname.trim().isEmpty()) {
            boolean exists = profiles.stream().anyMatch(p -> p.getNickname().equalsIgnoreCase(nickname));
            if (exists) {
                JOptionPane.showMessageDialog(null, "Nickname già in uso.", "Errore", JOptionPane.ERROR_MESSAGE);
            } else {
            	// NUOVO: Fai scegliere l'avatar
                Object[] avatarOptions = {"Avatar 1", "Avatar 2", "Avatar 3", "Avatar 4", "Avatar 5", "Avatar 6"};
                String selectedAvatar = (String) JOptionPane.showInputDialog(
                    null, 
                    "Scegli il tuo avatar:", 
                    "Selezione Avatar", 
                    JOptionPane.PLAIN_MESSAGE, 
                    null, 
                    avatarOptions, 
                    avatarOptions[0]
                );

                // Se l'utente chiude la finestra, usa un avatar di default
                String avatarId = "avatar1";
                if (selectedAvatar != null) {
                    if (selectedAvatar.equals("Avatar 2")) avatarId = "avatar2";
                    if (selectedAvatar.equals("Avatar 3")) avatarId = "avatar3";
                    if (selectedAvatar.equals("Avatar 4")) avatarId = "avatar4";
                    if (selectedAvatar.equals("Avatar 5")) avatarId = "avatar5";
                    if (selectedAvatar.equals("Avatar 6")) avatarId = "avatar6";
                }

                // Passa l'ID dell'avatar al costruttore del profilo
                UserProfile newProfile = new UserProfile(nickname, avatarId);
                ProfileManager.saveProfile(newProfile);
                
                setNeedsRefresh(true);
                
//                // Seleziona automaticamente il nuovo profilo creato
//                for(int i = 0; i < profiles.size(); i++) {
//                    if(profiles.get(i).getNickname().equals(newProfile.getNickname())) {
//                        selectedIndex = i;
//                        break;
//                    }
//                }
                
                // Seleziona automaticamente il nuovo profilo creato
                selectedIndex = profiles.size(); // Sarà l'ultimo indice dopo il refresh
                
                // FORZA IL REFRESH E LA NOTIFICA SUBITO DOPO LA CREAZIONE
                refreshProfiles(); 
                
                System.out.println("Nuovo profilo creato: " + nickname);
            }
        }
    }
    
    private void deleteProfile(int profileIndex) {
        UserProfile profileToDelete = profiles.get(profileIndex);

        // Mostra il dialogo di conferma
        int choice = JOptionPane.showConfirmDialog(
            null,
            "Sei sicuro di voler eliminare il profilo '" + profileToDelete.getNickname() + "'?\nQuesta azione è irreversibile.",
            "Conferma Eliminazione",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            // Se l'utente conferma, procedi con l'eliminazione
            ProfileManager.deleteProfile(profileToDelete);
            
            // Rinfresca la lista per mostrare il cambiamento
            refreshProfiles();
        }
    }
    
    /**
     * NUOVO METODO: Aggiorna le posizioni dei rettangoli dei cestini.
     * Verrà chiamato dalla View ogni volta che disegna la lista.
     * @param bounds La lista dei rettangoli calcolati dalla View.
     */
    public void updateDeleteButtonBounds(List<Rectangle> bounds) {
        this.deleteButtonBounds = bounds;
    }

    // Getters per la View
    public List<UserProfile> getProfiles() { return profiles; }
    public int getSelectedIndex() { return selectedIndex; }
    public Rectangle getCreateButtonBounds() { return createButtonBounds; }
    public boolean isCreateHover() { return createHover; }
}