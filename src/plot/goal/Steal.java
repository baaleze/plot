package plot.goal;

import plot.people.People;
import plot.World;
import plot.action.Action;

import java.util.Optional;

public class Steal extends Goal {
    private final People target;

    public Steal(People target) {
        this.target = target;
    }

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        return Optional.empty();
    }
}
