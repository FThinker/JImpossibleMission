// MainGamePanel.java (rinominato da GameView.java, nel package view)
package view;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import model.GameModel; // Importa GameModel
import model.GameState; // Importa GameState
import controller.GameoverHandler;
import controller.MainMenuHandler;
import controller.PausedHandler;
import controller.PopupHandler;
import controller.ProfileSelectionHandler;
import controller.StatsHandler;
import controller.TerminalHandler; // Importa TerminalHandler

import static model.GameConstants.*;

@SuppressWarnings({ "deprecation", "serial" })
public class MainGamePanel extends JPanel implements Observer { // Rinominato e implementa Observer

	private GameModel gameModel; // Osserva il GameModel per i cambi di stato
	private PlayingView playingView; // Istanza della PlayingView
	private TerminalView terminalView; // Istanza della TerminalView
	private PausedView pausedView;
	private GameoverView gameoverView;
	private VictoryView victoryView;
	private PopupView popupView; // NUOVO: istanza della PopupView
	private ElevatorView elevatorView; // Nuova istanza
	private ProfileSelectionView profileSelectionView;
	private MainMenuView mainMenuView;
	private LeaderboardView leaderboardView;
	private StatsView statsView;
	private HUDView hudView;

	private PopupHandler popupHandler;

	// COSTRUTTORE COMPLETAMENTE RIVISTO
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

	public JComponent getDrawingComponent() {
		return this; // Restituisce se stesso come componente su cui attaccare i listener
	}
	
//	// NUOVI METODI SETTER
//    public void setProfileSelectionHandler(ProfileSelectionHandler profileSelectionHandler) {
//        this.profileSelectionView = new ProfileSelectionView(this.gameModel, profileSelectionHandler);
//    }
//
//    public void setMainMenuHandler(MainMenuHandler mainMenuHandler) {
//        this.mainMenuView = new MainMenuView(this.gameModel, mainMenuHandler);
//    }
//    
//    public void setStatsHandler(StatsHandler statsHandler) {
//        this.statsView = new StatsView(this.gameModel, statsHandler);
//    }

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Delega il disegno alla View appropriata in base al GameState
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
			playingView.draw(g); // Disegna lo sfondo di gioco anche quando il terminale è aperto
			terminalView.draw(g); // Disegna il terminale sopra il gioco
			break;
		// Aggiungi altri stati se necessario (PAUSED, GAME_OVER, ecc.)
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
		
		// DOPO LO SWITCH, disegniamo l'HUD se siamo in uno stato di gioco attivo
        if (gameModel.getGameState() == GameState.PLAYING || gameModel.getGameState() == GameState.IN_ELEVATOR) {
            hudView.draw(g);
        }

		popupView.draw(g);
	}

	@Override
	public void update(Observable o, Object arg) {
		// Quando il GameModel notifica un cambiamento di stato, ri-disegna
//		if (o instanceof GameModel || o instanceof PopupHandler || o instanceof ProfileSelectionHandler) {
//			repaint();
//		}
		// Se Player o LiftTile notificano, PlayingView si occupa del loro repaint
		// attraverso il fatto che Player e LiftTile sono osservati anche da gameView
		// nel Controller
		// (che a sua volta chiama repaint() sul pannello principale)
		// Oppure, possiamo semplificare: dato che il MainGamePanel osserva GameModel, e
		// GameModel notifica
		// ogni volta che qualcosa nel suo stato cambia (es. player si muove, lift si
		// muove)
		// Potresti volere che Player e LiftTile continuino a notificare la GameView (il
		// pannello principale)
		// per un repaint più granulare.
		// O semplicemente affidarsi al fatto che GameModel notifica un repaint ad ogni
		// updateGame().
		// Per ora, lascio la logica esistente di notifica da Player e LiftTile se serve
		// una repaint più frequente.
		// Altrimenti, possiamo rimuovere gameView come observer di player e lift nel
		// GameController.

		// Se il GameModel cambia stato, ri-disegna.
		// Se un Player o LiftTile cambia, ri-disegna.
		// Questo `update` fa un `repaint()` generico.
		repaint();
	}
}