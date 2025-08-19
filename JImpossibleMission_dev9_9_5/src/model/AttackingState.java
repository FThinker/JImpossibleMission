package model;

public class AttackingState implements EnemyStateHandler {

    @Override
    public void enter(Enemy enemy) {
        enemy.setState(EnemyState.ATTACKING);
    }

    @Override
    public void update(Enemy enemy, EnemyBehavior context, long currentTime, Level levelData, Player player) {
        long attackDuration = context.getDurationForState(EnemyState.ATTACKING);

//        if (context.getElapsed(currentTime) >= attackDuration) {
//        	if (context instanceof StandingRobotBehavior) {
//                StandingRobotBehavior specificContext = (StandingRobotBehavior) context;
//                specificContext.changeState(new IdleState(), enemy, currentTime);
//                specificContext.setBehaviorStep(2);
//            }
//        }
        if (context.getElapsed(currentTime) >= attackDuration) {
            // L'attacco è finito. Chiediamo al contesto cosa fare ora.
            EnemyStateHandler nextState = getNextStateFor(context);
            context.changeState(nextState, enemy, currentTime);
        }
    }
    
    /**
     * Determina lo stato successivo in base al tipo di Behavior che sta usando questo stato.
     */
    private EnemyStateHandler getNextStateFor(EnemyBehavior context) {
        if (context instanceof StandingRobotBehavior) {
            // Dopo aver attaccato, lo StandingRobot torna IDLE e si prepara a girarsi.
            StandingRobotBehavior specificContext = (StandingRobotBehavior) context;
            specificContext.setBehaviorStep(2); // Aggiorna la sequenza interna
            return new IdleState();
        } 
        else if (context instanceof MovingRobotBehavior) {
            // Dopo aver attaccato, il MovingRobot riprende a muoversi.
            return new MovingState();
        }
        
        // Fallback di sicurezza: se il behavior è sconosciuto, torna IDLE.
        return new IdleState();
    }


    @Override
    public void exit(Enemy enemy) {}
}
