package plot.goal;

import plot.People;
import plot.World;
import plot.action.Action;

import java.util.Optional;

public class Negociation<Target> extends Goal {

    public Target target;

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        return Optional.empty();
    }

    @Override
    public Goal generateGoalToAchieve(World world, People me) {
        return null;
    }
}
