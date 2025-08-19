package model;

public class IdleState implements EnemyStateHandler {

	@Override
	public void enter(Enemy enemy) {
		enemy.setState(EnemyState.IDLE);
	}

	@Override
	public void update(Enemy enemy, EnemyBehavior context, long currentTime, Level levelData, Player player) {
		long idleDuration = context.getDurationForState(EnemyState.IDLE);

		if (context.getElapsed(currentTime) >= idleDuration) {
//			if (context instanceof StandingRobotBehavior) {
//				StandingRobotBehavior specificContext = (StandingRobotBehavior) context;
//				int step = specificContext.getBehaviorStep();
//				if (step == 0) {
//					specificContext.changeState(new AttackingState(), enemy, currentTime);
//					specificContext.setBehaviorStep(1);
//				} else if (step == 2) {
//					specificContext.changeState(new TurningState(), enemy, currentTime);
//					specificContext.setBehaviorStep(3);
//				}
//			}
			if (context.getElapsed(currentTime) >= idleDuration) {
	            // Chiediamo al contesto qual è lo stato successivo
	            EnemyStateHandler nextState = getNextStateFor(context);
	            context.changeState(nextState, enemy, currentTime);
	        }
		}
	}
	
	// Metodo helper per determinare lo stato successivo in base al tipo di behavior
    private EnemyStateHandler getNextStateFor(EnemyBehavior context) {
        if (context instanceof StandingRobotBehavior) {
            // Logica specifica per lo StandingRobot
            StandingRobotBehavior specificContext = (StandingRobotBehavior) context;
            int step = specificContext.getBehaviorStep();
            if (step == 0) {
                specificContext.setBehaviorStep(1);
                return new AttackingState();
            } else { // step == 2
                specificContext.setBehaviorStep(3);
                return new TurningState();
            }
        } else if (context instanceof MovingRobotBehavior) {
            // Logica specifica per il MovingRobot
            // Dopo essere stato in idle (perché ha visto il player), attacca.
            // Se l'idle era quello iniziale, inizia a camminare.
            // (Questa logica va affinata in MovingRobotBehavior)
            return new AttackingState(); // Esempio semplice
        }
        return this; // Default: non fare nulla
    }

	@Override
	public void exit(Enemy enemy) {
		// Niente
	}
}
