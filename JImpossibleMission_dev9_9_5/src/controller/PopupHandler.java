package controller;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Observable;
import model.FurnitureTile;

/**
 * Manages the state and logic for in-game popups, such as search progress bars and notifications.
 * It extends {@link Observable} to notify the {@link view.PopupView} when its state changes.
 */
@SuppressWarnings("deprecation")
public class PopupHandler extends Observable {

    private String popupText = "";
    private float searchProgress = 0.0f;
    private boolean isSearchingPopup = false;
    private Timer popupTimer;
    
    private FurnitureTile currentFurniture = null;

    /**
     * Shows a popup indicating that a search is in progress on a piece of furniture.
     *
     * @param furniture The {@link FurnitureTile} being searched.
     */
    public void showSearchPopup(FurnitureTile furniture) {
        isSearchingPopup = true;
        this.currentFurniture = furniture;
        this.popupText = "Searching...";
        
        setChanged();
        notifyObservers();
    }

    /**
     * Shows a temporary notification popup (e.g., "Piece found!").
     *
     * @param furniture The related furniture tile (for positioning).
     * @param text The text to display in the notification.
     * @param durationMillis The duration in milliseconds for which to show the popup.
     */
    public void showNotificationPopup(FurnitureTile furniture, String text, int durationMillis) {
        isSearchingPopup = false;
        this.currentFurniture = furniture;
        this.popupText = text;
        this.searchProgress = 0.0f;
        
        setChanged();
        notifyObservers();
        
        if (popupTimer != null) {
            popupTimer.cancel();
        }
        popupTimer = new Timer();
        popupTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                hidePopup();
            }
        }, durationMillis);
    }

    /**
     * Hides the currently active popup and resets its state.
     */
    public void hidePopup() {
        popupText = "";
        isSearchingPopup = false;
        searchProgress = 0.0f;
        currentFurniture = null;
        
        setChanged();
        notifyObservers();
    }
    
    /**
     * Updates the search progress based on the current furniture being searched.
     * This method is called repeatedly by the main game loop.
     */
    public void updateSearchProgress() {
        if (isSearchingPopup && currentFurniture != null) {
            this.searchProgress = currentFurniture.getSearchProgress();
        }
    }

    public String getPopupText() {
        return popupText;
    }

    public float getSearchProgress() {
        return searchProgress;
    }
    
    public boolean isSearchingPopupActive() {
        return isSearchingPopup;
    }
    
    public boolean isNotificationPopupActive() {
        return !popupText.isEmpty() && !isSearchingPopup;
    }
    
    public FurnitureTile getCurrentFurniture() {
        return currentFurniture;
    }
}