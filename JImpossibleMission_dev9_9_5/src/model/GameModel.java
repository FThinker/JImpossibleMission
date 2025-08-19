package model;

import static model.GameConstants.LOGIC_HEIGHT;
import static model.GameConstants.LOGIC_WIDTH;

import java.awt.Point;
import java.util.List;
import java.util.Observable; // Sarà osservato dalla View

@SuppressWarnings("deprecation")
public class GameModel extends Observable {
    private Player player;
    private Elevator elevator;
    private Level level;
    private GameState gameState;
    private UserProfile activeProfile; // NUOVO: Il profilo attualmente caricato
    
    // NUOVO: Variabili per il pop-up a nuvoletta
    private String popupMessage = null; // Il testo del messaggio da mostrare
    private float popupDisplayTime = 0; // Tempo rimanente di visualizzazione
    private final float POPUP_DURATION = 2.0f; // Durata del pop-up in secondi
    private FurnitureTile currentSearchingFurniture = null; // Riferimento al mobile in cui si sta cercando

 // NUOVO: Riferimento alla sessione di gioco corrente
    private GameSession currentGameSession; 
    
    private long freezeStartTime = 0;
    private long freezeDuration = 0;

    private long lastScore = 0;
    
    private GameState stateBeforePause;
    
    private int currentFps = 0;
    
    public GameModel(Player player, Elevator elevator, GameSession session) {
        this.player = player;
        this.level = null; // All'inizio non siamo in nessun livello
        this.elevator = elevator;
        this.gameState = GameState.PROFILE_SELECTION; // Nuovo stato iniziale
        this.activeProfile = null;
    }
    
    // NUOVI METODI per gestire il profilo
    public void setActiveProfile(UserProfile profile) {
        this.activeProfile = profile;
        setChanged();
        notifyObservers();
    }

    public UserProfile getActiveProfile() {
        return this.activeProfile;
    }

    // Ora la logica di creazione della partita non è più nel GameModel,
    // ma verrà gestita da un Handler che interagirà con il profilo.
    // Per esempio, quando si clicca "Nuova Partita":
    public void startNewGame() {
        if (activeProfile != null) {
            GameSession newSession = new GameSession(); // Crea una nuova sessione
            activeProfile.startGame(newSession); // Associa la sessione al profilo
            // Ora il GameModel deve "caricare" questa sessione
            this.currentGameSession = newSession; // Assumendo che GameModel abbia un campo `currentGameSession`
            setGameState(GameState.IN_ELEVATOR); // Si parte dall'ascensore
        }
    }
    
    // NUOVO METODO: Per entrare in una stanza dall'ascensore
    public void enterRoom(int lvlNumber) {
    	if (gameState == GameState.IN_ELEVATOR) {
            // 1. Chiedi alla sessione di darti il livello
            this.level = currentGameSession.getLevel(lvlNumber);
            
            if (this.level != null) {
                player.teleport(level.getPlayerSpawn().x, level.getPlayerSpawn().y);
                player.setInitialSpawn(new Point(level.getPlayerSpawn().x, level.getPlayerSpawn().y));
                setGameState(GameState.PLAYING);
                this.currentGameSession.updatePlayerLocation(GameState.PLAYING, this.level);
            } else {
                System.err.println("Errore: Impossibile caricare il livello " + lvlNumber);
            }
        }
    }
    
 // MODIFICA: exitRoom non deve più "distruggere" il livello
    public void exitRoom(Directions direction) {
        if (gameState == GameState.PLAYING) {
            // 1. NON impostare più this.level = null!
            // L'oggetto Level rimane memorizzato nella GameSession.
            
            // ... il resto del codice per teletrasportare il player non cambia ...
        	
        	this.currentGameSession.updatePlayerLocation(GameState.IN_ELEVATOR, null);
        	
        	setGameState(GameState.IN_ELEVATOR);
        	
            if(direction == Directions.RIGHT)
            	player.teleport(1, (int)(LOGIC_HEIGHT / 4.1));
            else
            	player.teleport(LOGIC_WIDTH - 2 - (int)player.getHitbox().getWidth() - 10*2, (int)(LOGIC_HEIGHT / 4.1));
        }
    }
    
 // MODIFICA: I metodi ora delegano alla GameSession
    public int getPuzzlePiecesFound() {
        return currentGameSession.getPuzzlePiecesFound();
    }
    
    public void addPuzzlePiece() {
        currentGameSession.addPuzzlePiece();
        setChanged();
        notifyObservers();
    }
    
    public int getLives() {
        return currentGameSession.getLives();
    }
    
    public void loseLife() {
        currentGameSession.loseLife();
        resetAllEnemiesPosition();
        resetAllLiftsPosition();
        player.teleport(level.getPlayerSpawn().x, level.getPlayerSpawn().y);
        setChanged();
        notifyObservers();
    }
    
    public Elevator getElevator() {
    	return this.elevator;
    }

    public Player getPlayer() {
        return player;
    }

    public Level getLevel() {
        return level;
    }

    public List<LiftTile> getLifts() {
        if (level != null) return level.getLifts();
        return java.util.Collections.emptyList(); // Ritorna una lista vuota se non c'è un livello
    }
    
    public List<PcTile> getPcTiles() {
    	if (level != null) return level.getPcs();
        return java.util.Collections.emptyList();
    }
    
    public List<FurnitureTile> getFurnitureTiles() {
		if (level != null) return level.getFurniture();
        return java.util.Collections.emptyList();
	}
    
    public List<Enemy> getEnemies(){
    	if (level != null) return level.getEnemies();
        return java.util.Collections.emptyList();
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        setChanged();
        notifyObservers(); // Notifica la View del cambio di stato
    }
    
    // Metodo per resettare la posizione di tutti i lift
    public void resetAllLiftsPosition() {
    	for (LiftTile lift : level.getLifts()) {
    		lift.resetPosition();
    	}
    }
    
    public void resetAllEnemiesPosition() {
    	for (Enemy enemy : level.getEnemies()) {
    		enemy.resetPosition();
    	}
    }
    
    // Metodo per "congelare" i nemici (da implementare logicamente quando avrai i nemici)
    public void freezeEnemies(long durationSec) {
    	// Qui dovrai implementare la logica per congelare i nemici.
    	// Ad esempio, potresti avere un elenco di oggetti Enemy e impostare una loro variabile 'frozen' a true
    	// e avviare un timer per scongelarli dopo 'durationSeconds'.
//    	System.out.println("Nemici congelati per " + durationSec + " secondi!");
    	this.freezeStartTime = System.currentTimeMillis();
        this.freezeDuration = durationSec * 1000;
        setChanged();
        notifyObservers();
    }
    
    public boolean isEnemiesFrozen(long currentTime) {
        return (currentTime - freezeStartTime) < freezeDuration;
    }
    
 // NUOVI GETTER E SETTER per la logica dei pop-up
    public String getPopupMessage() {
        return popupMessage;
    }
    
    public void setPopupMessage(String message) {
        this.popupMessage = message;
        this.popupDisplayTime = POPUP_DURATION; // Avvia il timer del pop-up
        setChanged();
        notifyObservers();
    }
    
    public void updatePopupTimer(float delta) {
        if (popupDisplayTime > 0) {
            popupDisplayTime -= delta;
            if (popupDisplayTime <= 0) {
                popupMessage = null; // Rimuovi il messaggio
                setChanged();
                notifyObservers();
            }
        }
    }
    
    public FurnitureTile getCurrentSearchingFurniture() {
        return currentSearchingFurniture;
    }
    
    public void setCurrentSearchingFurniture(FurnitureTile furniture) {
        this.currentSearchingFurniture = furniture;
        setChanged();
        notifyObservers();
    }

	public GameSession getCurrentGameSession() {
		return this.currentGameSession;
	}
	
    public void setStateBeforePause(GameState state) {
        this.stateBeforePause = state;
    }

    public GameState getStateBeforePause() {
        return this.stateBeforePause;
    }
    
    public void setCurrentFps(int fps) {
        this.currentFps = fps;
    }

    public int getCurrentFps() {
        return this.currentFps;
    }
    
    public void setLastScore(long score) { this.lastScore = score; }
    public long getLastScore() { return this.lastScore; }
}