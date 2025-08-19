package controller;

import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.List;

import javax.swing.*;

import model.Level;
import model.LiftTile;
import model.PcTile;
import model.Player;
import view.ElevatorView;
import view.GameoverView;
import view.HUDView;
import view.LeaderboardView;
import view.MainGamePanel;
import view.MainMenuView;
import view.PausedView;
import view.PlayingView;
import view.PopupView;
import view.ProfileSelectionView;
import view.StatsView;
import view.TerminalView;
import view.VictoryView;
import model.Elevator;
import model.GameModel;
import model.GameSession;

import static model.GameConstants.*;

public class JImpossibleMission {
    public static void main(String[] args) {
    	
        // Assicurati che l'interfaccia utente venga creata sul thread di dispatch degli eventi di Swing
        SwingUtilities.invokeLater(() -> {
        	
        	// === 1. CREAZIONE DEL MODELLO DATI ===
            AssetLoader.getInstance();
            AudioManager.getInstance();
            Elevator elevator = new Elevator(5, 1);
            Player player = new Player(new Point((int)(LOGIC_WIDTH / 2.24), (int)(LOGIC_HEIGHT / 4.1)));
            GameSession session = new GameSession(); // Crea la sessione
            GameModel gameModel = new GameModel(player, elevator, session); // Ora il costruttore è più semplice

            
            // === 2. CREAZIONE DI TUTTI GLI HANDLER (CONTROLLER PARZIALI) ===
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

            
            // === 3. CREAZIONE DI TUTTE LE VIEW ===
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

            
            // === 4. ASSEMBLAGGIO DELLA VIEW PRINCIPALE ===
            MainGamePanel mainGamePanel = new MainGamePanel(
                gameModel, playingView, elevatorView, terminalView, pausedView,
                gameoverView, profileSelectionView, mainMenuView, statsView, popupView, 
                victoryView, leaderboardView, hudView
            );

            
            // === 5. CREAZIONE DEL CONTROLLER PRINCIPALE ===
            GameController gameController = new GameController(
                gameModel, mainGamePanel, inputHandler, terminalHandler, pausedHandler,
                gameoverHandler, profileSelectionHandler, mainMenuHandler, statsHandler, popupHandler, 
                victoryHandler, leaderboardHandler
            );

            
            // === 6. CONFIGURAZIONE DELLA FINESTRA (JFrame) ===
            JFrame frame = new JFrame("JImpossibleMission");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(mainGamePanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            
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
            mainGamePanel.requestFocusInWindow(); // Assicurati che la View abbia il focus per ricevere input
            
        });
    }
}