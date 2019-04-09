package plot.goal;

import plot.Entity;
import plot.people.People;
import plot.World;
import plot.action.Action;

import java.util.Optional;

public class Steal extends Goal {
    private final Entity target;

    public Steal(Entity target) {
        this.target = target;
    }

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        // TODO depending on target type
        return Optional.empty();
    }
}
