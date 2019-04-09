package plot.goal;

import plot.World;
import plot.action.Action;
import plot.people.People;

import java.util.Optional;

public class KillSomebody extends Goal {

    private final People target;

    public KillSomebody(People target) {
        this.target = target;
    }

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        return Optional.empty();
    }
}
