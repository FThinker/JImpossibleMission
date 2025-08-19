package model;

public class TurningState implements EnemyStateHandler {

	@Override
	public void enter(Enemy enemy) {
		enemy.setState(EnemyState.TURNING);
		enemy.turn(); // Deve essere accessibile
	}

	@Override
	public void update(Enemy enemy, EnemyBehavior context, long currentTime, Level levelData, Player player) {
		long turnDuration = context.getDurationForState(EnemyState.TURNING);

//		if (context.getElapsed(currentTime) >= turnDuration) {
//			if (context instanceof StandingRobotBehavior) {
//				StandingRobotBehavior specificContext = (StandingRobotBehavior) context;
//				specificContext.changeState(new IdleState(), enemy, currentTime);
//				specificContext.setBehaviorStep(0);
//			}
//		}
		if (context.getElapsed(currentTime) >= turnDuration) {
            // L'azione di girarsi è finita. Decidiamo cosa fare dopo.
            EnemyStateHandler nextState = getNextStateFor(context);
            context.changeState(nextState, enemy, currentTime);
        }
	}
	
	/**
     * Determina lo stato successivo in base al contesto.
     */
    private EnemyStateHandler getNextStateFor(EnemyBehavior context) {
        if (context instanceof StandingRobotBehavior) {
            // Dopo essersi girato, lo StandingRobot torna IDLE per ricominciare il ciclo.
            StandingRobotBehavior specificContext = (StandingRobotBehavior) context;
            specificContext.setBehaviorStep(0); // Resetta la sequenza
            return new IdleState();
        } 
        else if (context instanceof MovingRobotBehavior) {
            // Dopo essersi girato, il MovingRobot inizia a muoversi nella nuova direzione.
            return new MovingState();
        }
        
        // Fallback
        return new IdleState();
    }

	@Override
	public void exit(Enemy enemy) {
	}
}
