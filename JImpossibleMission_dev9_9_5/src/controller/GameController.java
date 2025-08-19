package controller;

import javax.swing.Timer;

import model.Player;
import view.MainGamePanel;
import model.Directions;
import model.Elevator;
import model.Enemy;
import model.FurnitureTile;
import model.GameModel;
import model.GameSession;
import model.GameState;
import model.Level;
import model.LiftTile;
import model.PcTile;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JComponent; // Per attaccare i listener alla View

import model.PlayerState; // Importa il PlayerState
import model.Tile;
import model.UserProfile;

import static model.GameConstants.*;
import static model.EnemyState.*;

@SuppressWarnings("deprecation")
public class GameController implements Runnable {
	private GameModel gameModel;
	private InputHandler inputHandler;
	private TerminalHandler terminalHandler; // Aggiungi TerminalHandler
	private MainGamePanel mainGamePanel; // Riferimento al pannello principale per attach KeyListener
	private PopupHandler popupHandler;
	private PausedHandler pausedHandler;
	private GameoverHandler gameoverHandler;
	private VictoryHandler victoryHandler;
	private ProfileSelectionHandler profileSelectionHandler;
	private MainMenuHandler mainMenuHandler;
	private StatsHandler statsHandler;
	private LeaderboardHandler leaderboardHandler;

	private Elevator elevator;

	private static final double XP_PER_SCORE_POINT = 0.5; // 10% del punteggio diventa XP
	private static final int WIN_BONUS_XP = 1000;
	
	private boolean isStepSoundPlaying = false; 

	// COSTRUTTORE COMPLETAMENTE RIVISTO
	public GameController(GameModel gameModel, MainGamePanel mainGamePanel, InputHandler inputHandler,
			TerminalHandler terminalHandler, PausedHandler pausedHandler, GameoverHandler gameoverHandler,
			ProfileSelectionHandler profileSelectionHandler, MainMenuHandler mainMenuHandler, StatsHandler statsHandler,
			PopupHandler popupHandler, VictoryHandler victoryHandler, LeaderboardHandler leaderboardHandler) {

		// Assegna tutte le dipendenze ricevute
		this.gameModel = gameModel;
		this.mainGamePanel = mainGamePanel;
		this.inputHandler = inputHandler;
		this.terminalHandler = terminalHandler;
		this.pausedHandler = pausedHandler;
		this.gameoverHandler = gameoverHandler;
		this.victoryHandler = victoryHandler;
		this.profileSelectionHandler = profileSelectionHandler;
		this.mainMenuHandler = mainMenuHandler;
		this.statsHandler = statsHandler;
		this.popupHandler = popupHandler;
		this.leaderboardHandler = leaderboardHandler;

		this.elevator = gameModel.getElevator();

		// Ora che tutti i pezzi sono al loro posto, colleghiamo gli observer
		this.gameModel.addObserver(this.mainGamePanel);
		this.profileSelectionHandler.addObserver(this.mainGamePanel);
		gameModel.getPlayer().addObserver(this.mainGamePanel);
		this.elevator.addObserver(this.mainGamePanel);

		// Collega i listener di input
		this.mainGamePanel.getDrawingComponent().addKeyListener(this.inputHandler);
		this.mainGamePanel.addMouseListener(inputHandler);
		this.mainGamePanel.addMouseMotionListener(inputHandler);

		startGameLoop();
	}

	private void startGameLoop() {
		Thread gameThread = new Thread(this);
		gameThread.start();
	}

	public void run() {

		double timePerFrame = 1000000000.0 / FPS_SET;

		long previousTime = System.nanoTime();

		int frames = 0;
		long lastCheck = System.currentTimeMillis();

		double deltaF = 0;

		while (true) {
			long currentTime = System.nanoTime();

			deltaF += (currentTime - previousTime) / timePerFrame;
			previousTime = currentTime;

			if (deltaF >= 1) {
				updateGame();
				// ✅ CAMBIAMENTO FONDAMENTALE #1
				// Chiamiamo repaint() ad ogni aggiornamento logico.
				// Questo assicura che le animazioni (come quella del PlayerView)
				// funzionino sempre, anche quando il modello non cambia stato.
				mainGamePanel.repaint();
				deltaF--;
				frames++;
			}

			if (System.currentTimeMillis() - lastCheck >= 1000) {
				lastCheck = System.currentTimeMillis();
				System.out.println("FPS: " + frames);
				gameModel.setCurrentFps(frames);
				frames = 0;
			}
		}

	}

	public void gameLostFocus() {
		inputHandler.resetKeys();
	}

	// Metodo chiamato ad ogni tick del game loop
	private void updateGame() {

		// NUOVO: Controllo globale del timer per gli stati attivi
		if (gameModel.getGameState() == GameState.PLAYING || gameModel.getGameState() == GameState.IN_ELEVATOR) {
			GameSession session = gameModel.getCurrentGameSession();
			if (session != null && session.getTimeLeft() <= 0) {
				endGame(false); // Hai perso per tempo scaduto
				return; // Esci per questo frame
			}
		}

		switch (gameModel.getGameState()) {
		case PROFILE_SELECTION:
			profileSelectionHandler.handleInput();
			break;
		case HOMESCREEN:
			mainMenuHandler.handleInput();
			break;
		case IN_ELEVATOR:
			gameModel.getPlayer().setInAir(false);

			// NUOVO: Permetti di mettere in pausa dall'ascensore
			if (inputHandler.isKeyPressed(KeyEvent.VK_ESCAPE)) {
				AudioManager.getInstance().stopAllSounds();
				AudioManager.getInstance().play("pause");
				gameModel.setStateBeforePause(GameState.IN_ELEVATOR); // Salva lo stato attuale
				gameModel.setGameState(GameState.PAUSED);
				inputHandler.resetKeys();
				break; // Esce dallo switch per questo frame
			}

			// Se l'ascensore NON si sta muovendo, il player può muoversi
			if (!elevator.isMoving()) {

				// Horizontal Movement
				boolean anyMovementKeyPressed = inputHandler.isLeftPressed() || inputHandler.isRightPressed();
				if (anyMovementKeyPressed) {
					if (inputHandler.isLeftPressed()) {
						gameModel.getPlayer().moveLeft();
					}
					if (inputHandler.isRightPressed()) {
						gameModel.getPlayer().moveRight();
					}
				} else {
					// Se nessun tasto di movimento è premuto, il player va in idle
					gameModel.getPlayer().setIdle();
				}

				// Logica di transizione al livello quando il player tocca il bordo
				int currentFloor = elevator.getCurrentFloor();

				if (PhysicsHandler.isLeavingElevator(gameModel.getPlayer())) {
					if (gameModel.getPlayer().getDirection() == Directions.LEFT) {
						System.out.println("Entro nel livello " + (currentFloor * 2 - 1));
						gameModel.enterRoom(currentFloor * 2 - 1);
					} else {
						System.out.println("Entro nel livello " + (currentFloor * 2));
						gameModel.enterRoom(currentFloor * 2);
					}
					gameModel.getPlayer().setIdle();
				}

				// Se il player preme "UP" o "DOWN", muove l'ascensore solo se non sta già
				// andando.
				if (gameModel.getPlayer().getHitbox().x >= (LOGIC_WIDTH / 2.25)
						&& gameModel.getPlayer().getHitbox().getMaxX() <= (LOGIC_WIDTH / 1.77)) {
					if (inputHandler.isUpPressed()) {
						elevator.moveUp();
						inputHandler.resetVerticalKeys();
					} else if (inputHandler.isDownPressed()) {
						elevator.moveDown();
						inputHandler.resetVerticalKeys();
					}
				}

			}

			// Aggiorna la posizione dell'ascensore se è in movimento
			if (elevator.isMoving()) {
				gameModel.getPlayer().setIdle();
				elevator.update();
			}
			break;
		case PLAYING:

			if (inputHandler.isKeyPressed(KeyEvent.VK_ESCAPE)) {
				AudioManager.getInstance().stopAllSounds();
				AudioManager.getInstance().play("pause");
				gameModel.setStateBeforePause(GameState.PLAYING); // Salva lo stato attuale
				gameModel.setGameState(GameState.PAUSED);
				inputHandler.resetKeys();
				break; // Esce dallo switch per questo frame
			}

			// 1. Controllo collisioni con i nemici
			for (Enemy enemy : gameModel.getLevel().getEnemies()) {
				Rectangle2D.Float playerHb = gameModel.getPlayer().getHitbox();
				Rectangle2D.Float enemyHb = enemy.getHitbox();
				Rectangle2D.Float enemyAb = enemy.getAttackBox();
				if (playerHb.intersects(enemyHb) || (enemy.getState() == ATTACKING && playerHb.intersects(enemyAb))) {
					gameModel.loseLife();

					// Controlla se il gioco è finito!
					if (gameModel.getLives() <= 0) {
						endGame(false); // Chiama endGame per calcolare il punteggio e fermare il timer
						return; // Esci dall'update per questo frame
					}

					if (gameModel.getLives() > 0) {
						gameModel.getPlayer().resetPosition();
					}
					inputHandler.resetKeys();
					break;
				}
			}

			// 2. Controllo caduta "fuori dalla mappa"
			if (gameModel.getPlayer().getY() > GAME_HEIGHT) {
				gameModel.loseLife();

				if (gameModel.getLives() <= 0) {
					endGame(false);
					return;
				}

				if (gameModel.getLives() > 0) {
					gameModel.getPlayer().resetPosition();
				}
			}

			// Handle PCs
			for (PcTile pc : gameModel.getPcTiles()) {
				if (pc.getHitbox().contains(gameModel.getPlayer().getHitbox())) {
					if (inputHandler.isEPressed()) {
						AudioManager.getInstance().stopAllSounds();
						gameModel.setGameState(GameState.TERMINAL_OPEN); // Cambia stato
						AudioManager.getInstance().play("keystroke");
						inputHandler.resetKeys(); // Resetta i tasti per evitare input indesiderati all'apertura
					}
				}
			}

			// NUOVO: Gestione dell'interazione con l'arredamento (Furniture)
			boolean playerIsInteractingWithFurniture = false;
			for (FurnitureTile furniture : gameModel.getFurnitureTiles()) { // Assumi di avere getFurnitureTiles() nel
																			// GameModel
				if (!furniture.isVanished()) {
					if (furniture.getHitbox().intersects(gameModel.getPlayer().getHitbox())
							&& furniture.getHitbox().x <= gameModel.getPlayer().getHitbox().getX()
							&& furniture.getHitbox().getMaxX() >= gameModel.getPlayer().getHitbox().getMaxX()) {
						if (inputHandler.isEPressed()) {
							// Se il player preme E, avvia o continua la ricerca
							playerIsInteractingWithFurniture = true;
							furniture.setSearching(true);
							popupHandler.showSearchPopup(furniture);
							gameModel.getPlayer().setSearching();
							// La logica di aggiornamento del progresso avverrà nel ciclo di update
						} else {
							// Se il player rilascia E, interrompe la ricerca
							playerIsInteractingWithFurniture = false;
							furniture.setSearching(false);
							if (popupHandler.isSearchingPopupActive()) {
								popupHandler.hidePopup(); // Nascondi il pop-up se era attivo
							}
						}
					} else {
						// Se il player non è più a contatto con il mobile, interrompe la ricerca
						if (furniture.isSearching()) {
							playerIsInteractingWithFurniture = false;
							furniture.setSearching(false);
							// Nascondi il pop-up se era attivo per questo mobile
							if (popupHandler.getCurrentFurniture() == furniture) {
								popupHandler.hidePopup();
							}
						}
					}
				}

				// Aggiorna il progresso della ricerca in base al delta time (da implementare)
				if (furniture.isSearching()) {
					// Nota: Avrai bisogno di un delta time. Se non lo hai, possiamo calcolarlo.
					// Per ora, userò un valore fisso per semplicità.
					// Dovresti calcolare il tempo trascorso tra i frame.
					float deltaTime = 2.0f / FPS_SET; // Esempio: 1 secondo / 60 FPS
					furniture.updateSearchProgress(deltaTime);

					// Controlla se la ricerca è completata
					if (furniture.getSearchProgress() >= furniture.getSearchTimeRequired()) {
						furniture.setSearching(false); // Ricerca completata

						// Nascondi il popup di ricerca
						popupHandler.hidePopup();

						// Logica del risultato della ricerca
						if (furniture.hasPuzzlePiece()) {
							// Mostra un messaggio di successo per 1.5 secondi
							popupHandler.showNotificationPopup(furniture, "Piece found!", 1500);
							gameModel.addPuzzlePiece(); // Add puzzle piece
							furniture.setHasPuzzlePiece(false); // Rimuovi il pezzo dal mobile
						} else {
							// Mostra un messaggio di fallimento per 1.5 secondi
							popupHandler.showNotificationPopup(furniture, "Nothing here.", 1500);
						}

						furniture.vanish();
					}
				}
			}

			// Aggiorna lo stato del popup handler
			popupHandler.updateSearchProgress();

			// Apply gravity
			gameModel.getPlayer().applyGravity(GRAVITY);

			// Check if player is on ground
			if (!PhysicsHandler.isOnGround(gameModel.getPlayer(), gameModel.getLevel().getLevelData())) {
				gameModel.getPlayer().setInAir(true);
				gameModel.getPlayer().applyGravity(GRAVITY);
			}

			// Handle vertical collisions
			PhysicsHandler.handleVerticalCollisions(gameModel.getPlayer(), gameModel.getLevel().getLevelData());

			// Handle lifts
			for (LiftTile lift : gameModel.getLifts()) {
				// Se l'ascensore ha una destinazione e non è ancora arrivato
				if (lift.isMoving() && lift.getTargetY() != -1) {
					// Muovi l'ascensore di un passo verso la sua destinazione
					if (lift.getCurrentMovementDirection() == Directions.UP) {
						if (lift.getHitbox().y > lift.getTargetY()) {
							lift.moveUp();
							gameModel.getPlayer().moveWithLift(-lift.getSpeed());
							inputHandler.resetKeys();
						} else {
							lift.stop(); // Raggiunta la destinazione
						}
					} else if (lift.getCurrentMovementDirection() == Directions.DOWN) {
						if (lift.getHitbox().y < lift.getTargetY()) {
							lift.moveDown();
							gameModel.getPlayer().moveWithLift(lift.getSpeed());
							inputHandler.resetKeys();
						} else {
							lift.stop(); // Raggiunta la destinazione
						}
					}
				} else { // L'ascensore è fermo o non ha una destinazione
					List<Tile> stops = PhysicsHandler.getLiftStops(lift, gameModel.getLevel().getLevelData());
					if (PhysicsHandler.isOnLift(gameModel.getPlayer(), lift)) {
						// Player è sull'ascensore - gestisci il movimento dell'ascensore in base
						// all'input
						if (inputHandler.isUpPressed()) {
							PhysicsHandler.moveLiftToNextStop(lift, Directions.UP, gameModel.getLevel().getLevelData(),
									stops, gameModel.getPlayer());
							inputHandler.resetVerticalKeys(); // Resetta i tasti verticali per evitare attivazioni
																// multiple
						} else if (inputHandler.isDownPressed()) {
							PhysicsHandler.moveLiftToNextStop(lift, Directions.DOWN,
									gameModel.getLevel().getLevelData(), stops, gameModel.getPlayer());
							inputHandler.resetVerticalKeys(); // Resetta i tasti verticali per evitare attivazioni
																// multiple
						}
					}
				}
			}

			// Verifica se gli effetti di congelamento sono attivi sul livello corrente
			if (gameModel.getLevel() != null && !gameModel.getLevel().areEnemiesFrozen()) {
				// Aggiorna lo stato di tutti i nemici
				for (Enemy enemy : gameModel.getEnemies()) {
					enemy.update(System.currentTimeMillis(), gameModel.getLevel(), gameModel.getPlayer());
				}
			}

			// Handle player movement only if not interacting with furnitures
			if (!playerIsInteractingWithFurniture) {

				// Handle jump input
				if (inputHandler.isJumpPressed() && !gameModel.getPlayer().isInAir()) {
					gameModel.getPlayer().jump();
					AudioManager.getInstance().play("jump");
				}

				// Horizontal Movement
				boolean anyMovementKeyPressed = inputHandler.isLeftPressed() || inputHandler.isRightPressed();
				if (anyMovementKeyPressed) {
					if (inputHandler.isLeftPressed()) {
						if (PhysicsHandler.canMoveHere(gameModel.getPlayer(), Directions.LEFT,
								gameModel.getLevel().getLevelData())) {
							gameModel.getPlayer().moveLeft();
						} else
							gameModel.getPlayer().setIdle();
						if (PhysicsHandler.isLeavingLevel(gameModel.getPlayer())) {
							System.out.println("Entering Elevator!");
							gameModel.exitRoom(Directions.LEFT);
						}
					}
					if (inputHandler.isRightPressed()) {
						if (PhysicsHandler.canMoveHere(gameModel.getPlayer(), Directions.RIGHT,
								gameModel.getLevel().getLevelData())) {
							gameModel.getPlayer().moveRight();
						} else
							gameModel.getPlayer().setIdle();
						if (PhysicsHandler.isLeavingLevel(gameModel.getPlayer())) {
							System.out.println("Entering Elevator!");
							gameModel.exitRoom(Directions.RIGHT);
						}
					}
				} else {
					// Se nessun tasto di movimento è premuto, il player va in idle
					gameModel.getPlayer().setIdle();
				}
			}

			GameSession session = gameModel.getCurrentGameSession();

			// Controllo sconfitta per tempo scaduto
			if (session.getTimeLeft() <= 0) {
				endGame(false); // Hai perso
				return; // Esce dall'update per questo frame
			}

			// Controllo vittoria (es. ci sono 8 pezzi in totale)
			final int TOTAL_PUZZLE_PIECES = 8;
			if (session.getPuzzlePiecesFound() >= TOTAL_PUZZLE_PIECES) {
				endGame(true); // Hai vinto!
				return;
			}

			break;
		case TERMINAL_OPEN:
			terminalHandler.handleTerminalInput();
			break;
		case PAUSED:
			pausedHandler.handleInput();
			break;
		case GAMEOVER:
			gameoverHandler.handleInput();
			break;
		case STATS_SCREEN:
			statsHandler.handleInput();
			break;
		case VICTORY_SCREEN:
			victoryHandler.handleInput();
			break;
		case LEADERBOARD_SCREEN:
			leaderboardHandler.handleInput();
			break;
		default:
			break;
		}

	}

	private void endGame(boolean won) {
		GameSession session = gameModel.getCurrentGameSession();
		if (session == null)
			return;

		session.stopTimer(); // <-- PRIMA MODIFICA: Ferma il timer subito!

		UserProfile profile = gameModel.getActiveProfile();
		if (profile == null)
			return;

		long score = 0;
		long pointsPerPiece = 1000;
		long pointsPerSecondLeft = 10;
		if (won)
			score = (session.getPuzzlePiecesFound() * pointsPerPiece)
					+ (session.getTimeLeft() / 1000 * pointsPerSecondLeft);
		else
			score = (session.getPuzzlePiecesFound() * (pointsPerPiece / 2));

		// NUOVO: Calcolo dell'esperienza guadagnata
		long xpGained = (long) (score * XP_PER_SCORE_POINT);
		if (won) {
			xpGained += WIN_BONUS_XP;
		}

		// AGGIUNGI QUESTA RIGA
		gameModel.setLastScore(score);

		long playtime = session.getElapsedTime();
		profile.endGame(won, score, playtime, xpGained);
		ProfileManager.saveProfile(profile);

		gameModel.setGameState(won ? GameState.VICTORY_SCREEN : GameState.GAMEOVER);
	}
}