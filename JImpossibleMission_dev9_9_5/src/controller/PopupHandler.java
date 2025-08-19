// PopupHandler.java (Modificato)
package controller;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Observable; // Importa Observable
import model.FurnitureTile;

@SuppressWarnings("deprecation")
public class PopupHandler extends Observable { // Estende Observable

    private String popupText = "";
    private float searchProgress = 0.0f;
    private boolean isSearchingPopup = false;
    private Timer popupTimer;
    
    // Riferimento al tile di arredamento che sta venendo cercato
    private FurnitureTile currentFurniture = null;

    public void showSearchPopup(FurnitureTile furniture) {
        isSearchingPopup = true;
        this.currentFurniture = furniture;
        this.popupText = "Searching...";
        
        // NUOVO: Notifica gli observer che il popup deve essere mostrato
        setChanged();
        notifyObservers();
    }

    public void showNotificationPopup(FurnitureTile furniture, String text, int durationMillis) {
        isSearchingPopup = false;
        this.currentFurniture = furniture;
        this.popupText = text;
        this.searchProgress = 0.0f;
        
        // Notifica subito per mostrare il popup
        setChanged();
        notifyObservers();
        
        // Mostra il pop-up per un certo periodo di tempo
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

    public void hidePopup() {
        popupText = "";
        isSearchingPopup = false;
        searchProgress = 0.0f;
        currentFurniture = null;
        
        // NUOVO: Notifica gli observer che il popup deve essere nascosto
        setChanged();
        notifyObservers();
    }
    
    // Metodo chiamato dal GameController ad ogni tick
    public void updateSearchProgress() {
        if (isSearchingPopup && currentFurniture != null) {
            // Nota: qui non notifichiamo ad ogni tick per evitare repaint eccessivi.
            // Il repaint verrà gestito dalla notifica del GameModel o da una
            // notifica del furniture tile stesso se cambia il suo progresso.
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