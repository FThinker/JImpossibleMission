package controller;

import model.GameModel;
import model.GameState;
import model.UserProfile;
import static model.GameConstants.SCALE;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the logic for the leaderboard screen.
 * This class is responsible for loading all user profiles, sorting them based on different criteria
 * (high score, playtime, average score), and handling user input to navigate away from the screen.
 */
public class LeaderboardHandler {
	private GameModel gameModel;
	private InputHandler inputHandler;
	private Rectangle backButtonBounds = new Rectangle((int) (245 * SCALE), (int) (300 * SCALE), (int) (150 * SCALE),
			(int) (25 * SCALE));
	private boolean backHover = false;
	private boolean wasAnyButtonHovered = false;

	// Lists to hold the top 10 rankings
	private List<UserProfile> topByScore;
	private List<UserProfile> topByPlaytime;
	private List<UserProfile> topByAvgScore;

	/**
     * Constructs a LeaderboardHandler.
     *
     * @param gameModel    The main game model, used to change the game state.
     * @param inputHandler The input handler for detecting mouse events.
     */
	public LeaderboardHandler(GameModel gameModel, InputHandler inputHandler) {
		this.gameModel = gameModel;
		this.inputHandler = inputHandler;
		topByScore = Collections.emptyList();
		topByPlaytime = Collections.emptyList();
		topByAvgScore = Collections.emptyList();
	}

	/**
	 * Loads all user profiles and sorts them to generate the top 10 leaderboards.
	 * This method should be called before transitioning to the leaderboard screen.
	 */
	public void refreshLeaderboards() {
		List<UserProfile> allProfiles = ProfileManager.loadAllProfiles();

		// 1. Hi-Score Leaderboard (Total Score)
		topByScore = allProfiles.stream()
				.sorted(Comparator.comparingLong(UserProfile::getTotalScore).reversed())
				.limit(10)
				.collect(Collectors.toList());

		// 2. Playtime Leaderboard (Total Playtime)
		topByPlaytime = allProfiles.stream()
				.sorted(Comparator.comparingLong(UserProfile::getTotalPlaytimeMs).reversed())
				.limit(10)
				.collect(Collectors.toList());

		// 3. Average Score Leaderboard
		topByAvgScore = allProfiles.stream()
				.sorted(Comparator.comparingDouble(UserProfile::getAverageScore).reversed())
				.limit(10)
				.collect(Collectors.toList());
	}

	/**
	 * Handles user input on the leaderboard screen, checking for clicks on the back button.
	 */
	public void handleInput() {
		backHover = backButtonBounds.contains(inputHandler.getMouseX(), inputHandler.getMouseY());
		boolean isCurrentlyHovered = backHover;

        if (isCurrentlyHovered && !wasAnyButtonHovered) {
			AudioManager.getInstance().play("click_2");
		}
		wasAnyButtonHovered = isCurrentlyHovered;

		if (inputHandler.isMouseButtonPressed(MouseEvent.BUTTON1) && backHover) {
			AudioManager.getInstance().play("click");
			gameModel.setGameState(GameState.HOMESCREEN);
		}
		inputHandler.resetMouse();
	}

	// --- GETTERS for the View ---
	public Rectangle getBackButtonBounds() { return backButtonBounds; }
	public boolean isBackHover() { return backHover; }
	public List<UserProfile> getTopByScore() { return topByScore; }
	public List<UserProfile> getTopByPlaytime() { return topByPlaytime; }
	public List<UserProfile> getTopByAvgScore() { return topByAvgScore; }
}