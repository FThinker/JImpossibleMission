package controller;

import static model.GameConstants.LOGIC_HEIGHT;
import static model.GameConstants.LOGIC_WIDTH;
import static model.GameConstants.SCALE;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import model.GameModel;
import model.GameState;

public class MainMenuHandler {
	private GameModel gameModel;
	private InputHandler inputHandler;
	private ProfileSelectionHandler profileSelectionHandler;
	private LeaderboardHandler leaderboardHandler;

	// Aree cliccabili
	private Rectangle newGameBounds = new Rectangle((int) (195 * SCALE), (int) (100 * SCALE), (int) (250 * SCALE),
			(int) (25 * SCALE));
	private Rectangle statsBounds = new Rectangle((int) (195 * SCALE), (int) (135 * SCALE), (int) (250 * SCALE),
			(int) (25 * SCALE));
	private Rectangle leaderboardBounds = new Rectangle((int) (195 * SCALE), (int) (170 * SCALE), (int) (250 * SCALE),
			(int) (25 * SCALE));
	private Rectangle changeProfileBounds = new Rectangle((int) (195 * SCALE), (int) (215 * SCALE), (int) (250 * SCALE),
			(int) (25 * SCALE));

	// Stato hover
	private boolean newGameHover, statsHover, leaderboardHover, changeProfileHover;
	
	private boolean wasAnyButtonHovered = false;

	public MainMenuHandler(GameModel gameModel, InputHandler inputHandler, ProfileSelectionHandler psh,
			LeaderboardHandler lh) {
		this.gameModel = gameModel;
		this.inputHandler = inputHandler;
		this.profileSelectionHandler = psh;
		this.leaderboardHandler = lh;
	}

	public void handleInput() {
		int mx = inputHandler.getMouseX();
		int my = inputHandler.getMouseY();

		newGameHover = newGameBounds.contains(mx, my);
		statsHover = statsBounds.contains(mx, my);
		leaderboardHover = leaderboardBounds.contains(mx, my);
		changeProfileHover = changeProfileBounds.contains(mx, my);
		
		boolean isCurrentlyHovered = newGameHover || statsHover || leaderboardHover || changeProfileHover;
		
		// Riproduci il suono solo se ORA siamo in hover, ma PRIMA non lo eravamo
        if (isCurrentlyHovered && !wasAnyButtonHovered) {
            AudioManager.getInstance().play("click_2");
        }
        
        // Aggiorna la variabile di stato per il prossimo frame
        wasAnyButtonHovered = isCurrentlyHovered;

		if (inputHandler.isMouseButtonPressed(MouseEvent.BUTTON1)) {
			if (newGameHover) {
				AudioManager.getInstance().play("click");
				gameModel.startNewGame();
				gameModel.getPlayer().teleport((int) (LOGIC_WIDTH / 2.24), (int) (LOGIC_HEIGHT / 4.1));
			} else if (statsHover) {
				AudioManager.getInstance().play("click");
				gameModel.setGameState(GameState.STATS_SCREEN);
			} else if (leaderboardHover) {
				AudioManager.getInstance().play("click");
				leaderboardHandler.refreshLeaderboards();
				gameModel.setGameState(GameState.LEADERBOARD_SCREEN);
			} else if (changeProfileHover) {
				AudioManager.getInstance().play("click");
				profileSelectionHandler.setNeedsRefresh(true);
				gameModel.setActiveProfile(null);
				gameModel.setGameState(GameState.PROFILE_SELECTION);
			}
			inputHandler.resetMouse();
		}
	}

	// Getters per la View
	public Rectangle getNewGameBounds() {
		return newGameBounds;
	}

	public Rectangle getStatsBounds() {
		return statsBounds;
	}

	public Rectangle getLeaderboardBounds() {
		return leaderboardBounds;
	}

	public Rectangle getChangeProfileBounds() {
		return changeProfileBounds;
	}

	public boolean isNewGameHover() {
		return newGameHover;
	}

	public boolean isStatsHover() {
		return statsHover;
	}

	public boolean isLeaderboardHover() {
		return leaderboardHover;
	}

	public boolean isChangeProfileHover() {
		return changeProfileHover;
	}
}