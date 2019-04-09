package plot.goal;

import plot.Entity;
import plot.Item;
import plot.Place;
import plot.action.ActionType;
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
        if (target instanceof Item) {
            return Action.isGoalNeeded(me, ActionType.STEAL_ITEM, world, target, this);
        } else if (target instanceof Place) {
            return Action.isGoalNeeded(me, ActionType.STEAL_PLACE, world, target, this);
        } else {
            return Action.isGoalNeeded(me, ActionType.STEAL_PEOPLE, world, target, this);
        }
    }
}
