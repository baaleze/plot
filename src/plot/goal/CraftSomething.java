package plot.goal;

import plot.World;
import plot.action.Action;
import plot.action.ActionType;
import plot.people.People;

import java.util.Optional;

public class CraftSomething extends Goal {

    public CraftSomething() {
        this.type = GoalType.CRAFT_ITEM;
    }

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        return Action.isGoalNeeded(me, ActionType.CRAFT_ITEM, world, null, this);
    }
}
