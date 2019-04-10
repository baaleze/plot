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
    private Entity target;

    private boolean completed = false;

    public GetInfo(Entity target) {
        this.target = target;
        this.type = GoalType.GET_INFO;
    }

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        return Action.isGoalNeeded(me, ActionType.LISTEN, world, target,this );
    }

    @Override
    public boolean isComplete(World world) {
        return completed;
    }

    @Override
    public void setCompleted() {
        completed = true;
    }

    @Override
    public void setTarget(Entity i) {
        if (target == null) {
            target = i;
        }
    }
}
