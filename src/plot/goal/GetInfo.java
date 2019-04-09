package plot.goal;

import plot.Entity;
import plot.Item;
import plot.people.People;
import plot.World;
import plot.action.Action;
import plot.action.ActionType;

import java.util.Optional;

public class GetInfo extends Goal {

    /**
     * Can be {@link People} or {@link Item} or null.
     */
    private final Entity target;

    public GetInfo(Entity target) {
        this.target = target;
        this.type = GoalType.GET_INFO;
    }

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        return Action.isGoalNeeded(me, ActionType.LISTEN, world, target,this );
    }
}
