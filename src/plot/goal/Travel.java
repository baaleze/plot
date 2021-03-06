package plot.goal;

import plot.Entity;
import plot.people.People;
import plot.Place;
import plot.World;
import plot.action.Action;
import plot.action.ActionType;

import java.util.Optional;

public class Travel extends Goal {
    public Place target;

    public Travel(final Place t) {
        this.type = GoalType.TRAVEL_TO;
        this.target = t;
    }

    @Override
    public Optional<? extends Action> generateAction(final World world, final People me) {
        return Action.isGoalNeeded(me, ActionType.MOVE_TO_CITY, world, target, this);
    }

    @Override
    public boolean isComplete(World world) {
        return world.whereIs(owner).equals(target);
    }

    @Override
    public void setCompleted() {

    }

    @Override
    public void setTarget(Entity i) {

    }

}
