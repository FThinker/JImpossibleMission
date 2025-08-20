package controller;

import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.*;
import view.*;
import model.*;
import static model.GameConstants.*;

/**
 * The main entry point for the JImpossibleMission game.
 * This class is responsible for initializing all core components of the game,
 * including the model, view, and controller, and setting up the main application window.
 * It follows the Model-View-Controller (MVC) architectural pattern.
 */
public class JImpossibleMission {
    
    /**
     * The main method that starts the application.
     * It ensures that the GUI is created on the Event Dispatch Thread (EDT) for thread safety.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(() -> {
        	
        	// 1. MODEL CREATION
            // Initialize singletons for asset and audio management first.
            AssetLoader.getInstance();
            AudioManager.getInstance();
            
            Elevator elevator = new Elevator(5, 1);
            Player player = new Player(new Point((int)(LOGIC_WIDTH / 2.24), (int)(LOGIC_HEIGHT / 4.1)));
            GameSession session = new GameSession();
            GameModel gameModel = new GameModel(player, elevator, session);

            
            // 2. HANDLERS (PARTIAL CONTROLLERS) CREATION
            InputHandler inputHandler = new InputHandler();
            PopupHandler popupHandler = new PopupHandler();
            TerminalHandler terminalHandler = new TerminalHandler(gameModel, inputHandler);
            PausedHandler pausedHandler = new PausedHandler(gameModel, inputHandler);
            GameoverHandler gameoverHandler = new GameoverHandler(gameModel, inputHandler);
            VictoryHandler victoryHandler = new VictoryHandler(gameModel, inputHandler);
            ProfileSelectionHandler profileSelectionHandler = new ProfileSelectionHandler(gameModel, inputHandler);
            StatsHandler statsHandler = new StatsHandler(gameModel, inputHandler);
            LeaderboardHandler leaderboardHandler = new LeaderboardHandler(gameModel, inputHandler);
            MainMenuHandler mainMenuHandler = new MainMenuHandler(gameModel, inputHandler, profileSelectionHandler, leaderboardHandler);

            
            // 3. VIEWS CREATION
            PlayingView playingView = new PlayingView(gameModel);
            ElevatorView elevatorView = new ElevatorView(gameModel);
            TerminalView terminalView = new TerminalView(terminalHandler);
            PausedView pausedView = new PausedView(pausedHandler);
            LeaderboardView leaderboardView = new LeaderboardView(gameModel, leaderboardHandler);
            GameoverView gameoverView = new GameoverView(gameModel, gameoverHandler); 
            VictoryView victoryView = new VictoryView(gameModel, victoryHandler);
            ProfileSelectionView profileSelectionView = new ProfileSelectionView(gameModel, profileSelectionHandler, inputHandler);
            MainMenuView mainMenuView = new MainMenuView(gameModel, mainMenuHandler);
            StatsView statsView = new StatsView(gameModel, statsHandler);
            PopupView popupView = new PopupView(popupHandler);
            HUDView hudView = new HUDView(gameModel);

            
            // 4. MAIN VIEW ASSEMBLY
            // The MainGamePanel acts as the primary container for all other views.
            MainGamePanel mainGamePanel = new MainGamePanel(
                gameModel, playingView, elevatorView, terminalView, pausedView,
                gameoverView, profileSelectionView, mainMenuView, statsView, popupView, 
                victoryView, leaderboardView, hudView
            );

            
            // 5. MAIN CONTROLLER CREATION
            // The GameController orchestrates the entire game flow.
            GameController gameController = new GameController(
                gameModel, mainGamePanel, inputHandler, terminalHandler, pausedHandler,
                gameoverHandler, profileSelectionHandler, mainMenuHandler, statsHandler, popupHandler, 
                victoryHandler, leaderboardHandler
            );

            
            // 6. WINDOW (JFRAME) CONFIGURATION
            JFrame frame = new JFrame("JImpossibleMission");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(mainGamePanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            
            // Add a focus listener to handle when the game window loses focus.
            frame.addWindowFocusListener(new WindowFocusListener() {
				@Override
				public void windowGainedFocus(WindowEvent e) {
				}

				@Override
				public void windowLostFocus(WindowEvent e) {
					gameController.gameLostFocus();
				}
            });
            
            frame.setVisible(true);
            mainGamePanel.requestFocusInWindow(); // Ensure the panel has focus to receive input
        });
    }
}