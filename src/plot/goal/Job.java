package plot.goal;

import plot.World;
import plot.action.Action;
import plot.people.People;

import java.util.Optional;

public class Job<Target> extends Goal {

    public People patron;
    public Target target;
    public int reward;

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        // TODO
        return Optional.empty();
    }

}
