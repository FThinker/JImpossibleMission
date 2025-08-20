package controller;

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
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.JComponent;
import model.PlayerState;
import model.Tile;
import model.UserProfile;

import static model.GameConstants.*;
import static model.EnemyState.*;

/**
 * The main controller of the game.
 * It contains the central game loop, processes user input, updates the game model,
 * and coordinates interactions between different game components based on the current game state.
 *
 * @see GameModel
 * @see MainGamePanel
 * @see GameState
 */
@SuppressWarnings("deprecation")
public class GameController implements Runnable {
	private GameModel gameModel;
	private InputHandler inputHandler;
	private TerminalHandler terminalHandler;
	private MainGamePanel mainGamePanel;
	private PopupHandler popupHandler;
	private PausedHandler pausedHandler;
	private GameoverHandler gameoverHandler;
	private VictoryHandler victoryHandler;
	private ProfileSelectionHandler profileSelectionHandler;
	private MainMenuHandler mainMenuHandler;
	private StatsHandler statsHandler;
	private LeaderboardHandler leaderboardHandler;

	private Elevator elevator;

	private static final double XP_PER_SCORE_POINT = 0.5;
	private static final int WIN_BONUS_XP = 1000;

    /**
     * Constructs the main game controller and wires up all dependencies.
     * This constructor initializes all handlers, sets up observers between the model and view,
     * attaches input listeners to the main panel, and starts the game loop.
     *
     * @param gameModel              The main data model of the game.
     * @param mainGamePanel          The main view component (JPanel) for rendering.
     * @param inputHandler           The handler for keyboard and mouse inputs.
     * @param terminalHandler        The handler for terminal screen logic.
     * @param pausedHandler          The handler for the paused menu.
     * @param gameoverHandler        The handler for the game over screen.
     * @param profileSelectionHandler The handler for the profile selection screen.
     * @param mainMenuHandler        The handler for the main menu screen.
     * @param statsHandler           The handler for the statistics screen.
     * @param popupHandler           The handler for in-game popups.
     * @param victoryHandler         The handler for the victory screen.
     * @param leaderboardHandler     The handler for the leaderboard screen.
     */
	public GameController(GameModel gameModel, MainGamePanel mainGamePanel, InputHandler inputHandler,
			TerminalHandler terminalHandler, PausedHandler pausedHandler, GameoverHandler gameoverHandler,
			ProfileSelectionHandler profileSelectionHandler, MainMenuHandler mainMenuHandler, StatsHandler statsHandler,
			PopupHandler popupHandler, VictoryHandler victoryHandler, LeaderboardHandler leaderboardHandler) {

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

		// Connect observers to their subjects
		this.gameModel.addObserver(this.mainGamePanel);
		this.profileSelectionHandler.addObserver(this.mainGamePanel);
		gameModel.getPlayer().addObserver(this.mainGamePanel);
		this.elevator.addObserver(this.mainGamePanel);

		// Attach input listeners to the view
		this.mainGamePanel.getDrawingComponent().addKeyListener(this.inputHandler);
		this.mainGamePanel.addMouseListener(inputHandler);
		this.mainGamePanel.addMouseMotionListener(inputHandler);

		startGameLoop();
	}

    /**
     * Initializes and starts the main game loop in a new thread.
     */
	private void startGameLoop() {
		Thread gameThread = new Thread(this);
		gameThread.start();
	}

    /**
     * The core game loop, which runs continuously in a dedicated thread.
     * It manages game updates and rendering to achieve a consistent frame rate (FPS).
     */
	public void run() {

		double timePerFrame = 1000000000.0 / FPS_SET;
		long previousTime = System.nanoTime();
		long lastCheck = System.currentTimeMillis();
		int frames = 0;
		double deltaF = 0;

		while (true) {
			long currentTime = System.nanoTime();

			deltaF += (currentTime - previousTime) / timePerFrame;
			previousTime = currentTime;

			if (deltaF >= 1) {
				updateGame();
                // The repaint() method is called on every logic update. This ensures that animations
                // in the view (like PlayerView) are always smooth, even if the model's state
                // hasn't changed.
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

    /**
     * Resets player movement keys when the game window loses focus
     * to prevent unwanted continuous movement.
     */
	public void gameLostFocus() {
		inputHandler.resetKeys();
	}

    /**
     * Updates the game's logic based on the current {@link GameState}.
     * This method is called on every tick of the game loop and delegates input handling
     * and state updates to the appropriate handler.
     */
	private void updateGame() {
		// Check the game session timer if the game is in an active state
		if (gameModel.getGameState() == GameState.PLAYING || gameModel.getGameState() == GameState.IN_ELEVATOR) {
			GameSession session = gameModel.getCurrentGameSession();
			if (session != null && session.getTimeLeft() <= 0) {
				endGame(false); // Game over due to timeout
				return;
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

			// Allow pausing from the elevator
			if (inputHandler.isKeyPressed(KeyEvent.VK_ESCAPE)) {
				AudioManager.getInstance().stopAllSounds();
				AudioManager.getInstance().play("pause");
				gameModel.setStateBeforePause(GameState.IN_ELEVATOR);
				gameModel.setGameState(GameState.PAUSED);
				inputHandler.resetKeys();
				break;
			}

			// Player can move if the elevator is not moving
			if (!elevator.isMoving()) {

				boolean anyMovementKeyPressed = inputHandler.isLeftPressed() || inputHandler.isRightPressed();
				if (anyMovementKeyPressed) {
					if (inputHandler.isLeftPressed()) {
						gameModel.getPlayer().moveLeft();
					}
					if (inputHandler.isRightPressed()) {
						gameModel.getPlayer().moveRight();
					}
				} else {
					gameModel.getPlayer().setIdle();
				}

				// Transition to a level when the player touches the screen edge
				int currentFloor = elevator.getCurrentFloor();
				if (PhysicsHandler.isLeavingElevator(gameModel.getPlayer())) {
					if (gameModel.getPlayer().getDirection() == Directions.LEFT) {
						gameModel.enterRoom(currentFloor * 2 - 1);
					} else {
						gameModel.enterRoom(currentFloor * 2);
					}
					gameModel.getPlayer().setIdle();
				}

				// Move the elevator if the player is in the control area and presses UP/DOWN
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

			// Update elevator position if it's in motion
			if (elevator.isMoving()) {
				gameModel.getPlayer().setIdle();
				elevator.update();
			}
			break;
		case PLAYING:

			if (inputHandler.isKeyPressed(KeyEvent.VK_ESCAPE)) {
				AudioManager.getInstance().stopAllSounds();
				AudioManager.getInstance().play("pause");
				gameModel.setStateBeforePause(GameState.PLAYING);
				gameModel.setGameState(GameState.PAUSED);
				inputHandler.resetKeys();
				break;
			}

			// Check for collisions with enemies
			for (Enemy enemy : gameModel.getLevel().getEnemies()) {
				Rectangle2D.Float playerHb = gameModel.getPlayer().getHitbox();
				Rectangle2D.Float enemyHb = enemy.getHitbox();
				Rectangle2D.Float enemyAb = enemy.getAttackBox();
				if (playerHb.intersects(enemyHb) || (enemy.getState() == ATTACKING && playerHb.intersects(enemyAb))) {
					gameModel.loseLife();
					popupHandler.hidePopup();

					if (gameModel.getLives() <= 0) {
						endGame(false);
						return;
					}

					if (gameModel.getLives() > 0) {
						gameModel.getPlayer().resetPosition();
					}
					inputHandler.resetKeys();
					break;
				}
			}

			// Check for falling out of the map
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

			// Handle interaction with PCs (terminals)
			for (PcTile pc : gameModel.getPcTiles()) {
				if (pc.getHitbox().contains(gameModel.getPlayer().getHitbox())) {
					if (inputHandler.isEPressed()) {
						AudioManager.getInstance().stopAllSounds();
						gameModel.setGameState(GameState.TERMINAL_OPEN);
						AudioManager.getInstance().play("keystroke");
						inputHandler.resetKeys();
					}
				}
			}

			// Handle interaction with furniture
			boolean playerIsInteractingWithFurniture = false;
			for (FurnitureTile furniture : gameModel.getFurnitureTiles()) {
				if (!furniture.isVanished()) {
					if (furniture.getHitbox().intersects(gameModel.getPlayer().getHitbox())
							&& furniture.getHitbox().x <= gameModel.getPlayer().getHitbox().getX()
							&& furniture.getHitbox().getMaxX() >= gameModel.getPlayer().getHitbox().getMaxX()) {
						if (inputHandler.isEPressed()) {
							playerIsInteractingWithFurniture = true;
							furniture.setSearching(true);
							popupHandler.showSearchPopup(furniture);
							gameModel.getPlayer().setSearching();
						} else {
							playerIsInteractingWithFurniture = false;
							furniture.setSearching(false);
							if (popupHandler.isSearchingPopupActive()) {
								popupHandler.hidePopup();
							}
						}
					} else {
						if (furniture.isSearching()) {
							playerIsInteractingWithFurniture = false;
							furniture.setSearching(false);
							if (popupHandler.getCurrentFurniture() == furniture) {
								popupHandler.hidePopup();
							}
						}
					}
				}

				// Update search progress
				if (furniture.isSearching()) {
					float deltaTime = 2.0f / FPS_SET;
					furniture.updateSearchProgress(deltaTime);

					// Check if search is complete
					if (furniture.getSearchProgress() >= furniture.getSearchTimeRequired()) {
						furniture.setSearching(false);
						popupHandler.hidePopup();

						if (furniture.hasPuzzlePiece()) {
							popupHandler.showNotificationPopup(furniture, "Piece found!", 1500);
							gameModel.addPuzzlePiece();
							furniture.setHasPuzzlePiece(false);
						} else {
							popupHandler.showNotificationPopup(furniture, "Nothing here.", 1500);
						}
						furniture.vanish();
					}
				}
			}

			popupHandler.updateSearchProgress();
			gameModel.getPlayer().applyGravity(GRAVITY);

			if (!PhysicsHandler.isOnGround(gameModel.getPlayer(), gameModel.getLevel().getLevelData())) {
				gameModel.getPlayer().setInAir(true);
				gameModel.getPlayer().applyGravity(GRAVITY);
			}

			PhysicsHandler.handleVerticalCollisions(gameModel.getPlayer(), gameModel.getLevel().getLevelData());

			// Handle lifts
			for (LiftTile lift : gameModel.getLifts()) {
				if (lift.isMoving() && lift.getTargetY() != -1) {
					if (lift.getCurrentMovementDirection() == Directions.UP) {
						if (lift.getHitbox().y > lift.getTargetY()) {
							lift.moveUp();
							gameModel.getPlayer().moveWithLift(-lift.getSpeed());
							inputHandler.resetKeys();
						} else {
							lift.stop();
						}
					} else if (lift.getCurrentMovementDirection() == Directions.DOWN) {
						if (lift.getHitbox().y < lift.getTargetY()) {
							lift.moveDown();
							gameModel.getPlayer().moveWithLift(lift.getSpeed());
							inputHandler.resetKeys();
						} else {
							lift.stop();
						}
					}
				} else {
					List<Tile> stops = PhysicsHandler.getLiftStops(lift, gameModel.getLevel().getLevelData());
					if (PhysicsHandler.isOnLift(gameModel.getPlayer(), lift)) {
						if (inputHandler.isUpPressed()) {
							PhysicsHandler.moveLiftToNextStop(lift, Directions.UP, gameModel.getLevel().getLevelData(),
									stops, gameModel.getPlayer());
							inputHandler.resetVerticalKeys();
						} else if (inputHandler.isDownPressed()) {
							PhysicsHandler.moveLiftToNextStop(lift, Directions.DOWN,
									gameModel.getLevel().getLevelData(), stops, gameModel.getPlayer());
							inputHandler.resetVerticalKeys();
						}
					}
				}
			}

			// Update enemies if they are not frozen
			if (gameModel.getLevel() != null && !gameModel.getLevel().areEnemiesFrozen()) {
				for (Enemy enemy : gameModel.getEnemies()) {
					enemy.update(System.currentTimeMillis(), gameModel.getLevel(), gameModel.getPlayer());
				}
			}

			// Handle player movement if not interacting with furniture
			if (!playerIsInteractingWithFurniture) {
				if (inputHandler.isJumpPressed() && !gameModel.getPlayer().isInAir()) {
					gameModel.getPlayer().jump();
					AudioManager.getInstance().play("jump");
				}

				boolean anyMovementKeyPressed = inputHandler.isLeftPressed() || inputHandler.isRightPressed();
				if (anyMovementKeyPressed) {
					if (inputHandler.isLeftPressed()) {
						if (PhysicsHandler.canMoveHere(gameModel.getPlayer(), Directions.LEFT,
								gameModel.getLevel().getLevelData())) {
							gameModel.getPlayer().moveLeft();
						} else
							gameModel.getPlayer().setIdle();
						if (PhysicsHandler.isLeavingLevel(gameModel.getPlayer())) {
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
							gameModel.exitRoom(Directions.RIGHT);
						}
					}
				} else {
					gameModel.getPlayer().setIdle();
				}
			}

			GameSession session = gameModel.getCurrentGameSession();

			if (session.getTimeLeft() <= 0) {
				endGame(false);
				return;
			}

			final int TOTAL_PUZZLE_PIECES = 8;
			if (session.getPuzzlePiecesFound() >= TOTAL_PUZZLE_PIECES) {
				endGame(true);
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

    /**
     * Finalizes the game session when the player wins or loses.
     * This method stops the session timer, calculates the final score and experience points (XP),
     * updates the active user profile, and transitions the game to the appropriate
     * victory or game over screen.
     *
     * @param won True if the player won the game, false otherwise.
     */
	private void endGame(boolean won) {
		GameSession session = gameModel.getCurrentGameSession();
		if (session == null)
			return;

		session.stopTimer();

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

		long xpGained = (long) (score * XP_PER_SCORE_POINT);
		if (won) {
			xpGained += WIN_BONUS_XP;
		}
        
		gameModel.setLastScore(score);

		long playtime = session.getElapsedTime();
		profile.endGame(won, score, playtime, xpGained);
		ProfileManager.saveProfile(profile);

		gameModel.setGameState(won ? GameState.VICTORY_SCREEN : GameState.GAMEOVER);
	}
}