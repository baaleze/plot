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
    public final Entity target;
    private boolean completed = false;

    public Steal(Entity target) {
        this.target = target;
    }

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        if (me.isRelativelyGoodIn(me.skills.sneak)) {
            if (target instanceof Item) {
                return Action.isGoalNeeded(me, ActionType.STEAL_ITEM, world, target, this);
            } else if (target instanceof Place) {
                return Action.isGoalNeeded(me, ActionType.STEAL_PLACE, world, target, this);
            } else {
                return Action.isGoalNeeded(me, ActionType.STEAL_PEOPLE, world, target, this);
            }
        } else {
            return Action.isGoalNeeded(me, ActionType.HIRE_TO_STEAL, world, target, null);
        }
    }

    @Override
    public boolean isComplete(World world) {
        if (target instanceof Item) {
            return owner.items.contains(target);
        } else {
            return completed;
        }
    }

    @Override
    public void setCompleted() {
        completed = true;
    }

    @Override
    public void setTarget(Entity i) {

    }
}
