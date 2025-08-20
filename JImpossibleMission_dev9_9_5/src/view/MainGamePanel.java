package view;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;
import model.GameModel;
import model.GameState;

import static model.GameConstants.*;

/**
 * The main container panel for the entire game, extending {@link JPanel}.
 * It acts as the primary canvas where all game graphics are rendered.
 * This class observes the {@link GameModel} and routes the drawing commands to the
 * appropriate sub-view based on the current {@link GameState}.
 */
@SuppressWarnings({ "deprecation", "serial" })
public class MainGamePanel extends JPanel implements Observer {

	private GameModel gameModel;
	private PlayingView playingView;
	private TerminalView terminalView;
	private PausedView pausedView;
	private GameoverView gameoverView;
	private VictoryView victoryView;
	private PopupView popupView;
	private ElevatorView elevatorView;
	private ProfileSelectionView profileSelectionView;
	private MainMenuView mainMenuView;
	private LeaderboardView leaderboardView;
	private StatsView statsView;
	private HUDView hudView;

    /**
     * Constructs the MainGamePanel and initializes all the sub-views.
     *
     * @param gameModel The main game model.
     * @param playingView The view for the main gameplay state.
     * @param elevatorView The view for the elevator state.
     * @param terminalView The view for the terminal screen.
     * @param pausedView The view for the pause menu.
     * @param gameoverView The view for the game over screen.
     * @param profileSelectionView The view for the profile selection screen.
     * @param mainMenuView The view for the main menu.
     * @param statsView The view for the statistics screen.
     * @param popupView The view for in-game popups.
     * @param victoryView The view for the victory screen.
     * @param leaderboardView The view for the leaderboard screen.
     * @param hudView The view for the Heads-Up Display.
     */
    public MainGamePanel(GameModel gameModel, PlayingView playingView, ElevatorView elevatorView,
                         TerminalView terminalView, PausedView pausedView, GameoverView gameoverView,
                         ProfileSelectionView profileSelectionView, MainMenuView mainMenuView,
                         StatsView statsView, PopupView popupView, VictoryView victoryView, 
                         LeaderboardView leaderboardView, HUDView hudView) {
        
        this.gameModel = gameModel;
        this.playingView = playingView;
        this.elevatorView = elevatorView;
        this.terminalView = terminalView;
        this.pausedView = pausedView;
        this.gameoverView = gameoverView;
        this.victoryView = victoryView;
        this.profileSelectionView = profileSelectionView;
        this.mainMenuView = mainMenuView;
        this.statsView = statsView;
        this.popupView = popupView;
        this.leaderboardView = leaderboardView;
        this.hudView = hudView;
        
        setFocusable(true);
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
    }

	/**
     * Returns this panel as the component for attaching input listeners.
     * @return This JComponent.
     */
	public JComponent getDrawingComponent() {
		return this;
	}

	/**
     * The main rendering method, overridden from {@link JPanel}.
     * It delegates the drawing process to the appropriate view based on the current game state.
     * @param g The Graphics context.
     */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		switch (gameModel.getGameState()) {
		case PROFILE_SELECTION:
			profileSelectionView.draw(g);
			break;
		case HOMESCREEN:
			mainMenuView.draw(g);
			break;
		case PLAYING:
			playingView.draw(g);
			break;
		case TERMINAL_OPEN:
			playingView.draw(g); // Draw the game world behind the terminal
			terminalView.draw(g);
			break;
		case PAUSED:
            if (gameModel.getStateBeforePause() == GameState.IN_ELEVATOR) {
                elevatorView.draw(g);
            } else {
                playingView.draw(g); 
            }
            pausedView.draw(g);
			break;
		case GAMEOVER:
			playingView.draw(g);
			gameoverView.draw(g);
			break;
		case IN_ELEVATOR:
			elevatorView.draw(g);
			break;
		case STATS_SCREEN:
			statsView.draw(g);
			break;
		case VICTORY_SCREEN:
			playingView.draw(g);
            victoryView.draw(g);
            break;
		case LEADERBOARD_SCREEN:
			leaderboardView.draw(g);
			break;
		default:
			break;
		}
		
        // The HUD is drawn on top of active game states
        if (gameModel.getGameState() == GameState.PLAYING || gameModel.getGameState() == GameState.IN_ELEVATOR) {
            hudView.draw(g);
        }

		// The popup view is drawn last, so it appears on top of everything
		popupView.draw(g);
	}

	/**
     * This method is called by the {@link Observable} subjects (like GameModel) when their state changes.
     * It triggers a repaint of the panel to reflect the updated state.
     * @param o The observable object.
     * @param arg An argument passed to the notifyObservers method.
     */
	@Override
	public void update(Observable o, Object arg) {
		repaint();
	}
}