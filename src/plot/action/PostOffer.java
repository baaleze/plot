package plot.action;

import plot.World;
import plot.goal.Goal;
import plot.people.People;

public class PostOffer extends Action {
    private final Goal task;

    public PostOffer(Goal sourceGoal) {
        this.task = sourceGoal;
    }

    @Override
    public void apply(World world, People me) {
        // TODO
    }

    @Override
    public void spawnGoals(World world, People me) {

    }
}
