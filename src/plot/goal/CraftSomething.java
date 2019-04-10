package plot.goal;

import plot.Entity;
import plot.Item;
import plot.World;
import plot.action.Action;
import plot.action.ActionType;
import plot.people.People;

import java.util.Optional;

public class CraftSomething extends Goal {

    public Item target;

    public CraftSomething(World world) {
        this.type = GoalType.CRAFT_ITEM;
        target = world.generateNewItem();
    }

    private boolean completed = false;

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        return Action.isGoalNeeded(me, ActionType.CRAFT_ITEM, world, target, this);
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
    public void setTarget(Entity i) {}
}
