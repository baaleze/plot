package plot.goal;

import plot.Entity;
import plot.people.People;
import plot.Place;
import plot.World;
import plot.action.Action;

import java.util.Optional;

public class Ambition extends Goal {

    private final Place place;

    public Ambition(Place target) {
        this.type = GoalType.AMBITION;
        this.place = target;
    }

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
