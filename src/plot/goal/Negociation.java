package plot.goal;

import plot.Entity;
import plot.people.People;
import plot.World;
import plot.action.Action;

import java.util.Optional;

public class Negociation<Target> extends Goal {

    public Target target;

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        // TODO
        return Optional.empty();
    }

    @Override
    public boolean isComplete(World world) {
        return false;
    }

    @Override
    public void setCompleted() {

    }

    @Override
    public void setTarget(Entity i) {

    }
}
