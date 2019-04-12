package plot.action;

import plot.World;
import plot.goal.Goal;
import plot.people.People;

public class HireFor extends Action {

    public Goal task;
    public People hiree;

    public HireFor(People toHire, Goal sourceGoal) {
        this.hiree = toHire;
        task = sourceGoal;
    }

    @Override
    public void apply(World world, People me) {
        // TODO
    }

    @Override
    public void spawnGoals(World world, People me) {

    }
}
