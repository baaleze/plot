package plot.goal;

import plot.Entity;
import plot.Item;
import plot.people.People;
import plot.Util;
import plot.World;
import plot.action.Action;
import plot.action.ActionType;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static plot.action.ActionType.*;

public class GetItem extends Goal {

    private boolean completed = false;
    public Item target;

    public GetItem() {
        this.type = GoalType.GET_ITEM;
    }

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        // action could be Stealing, trading, selling,
        List<ActionType> possibleOptions = new LinkedList<>();
        ActionType[] possibleOptionsIfNothingElse = {
                BUY_ITEM, STEAL_ITEM, CRAFT_ITEM
        };
        if (me.isRelativelyGoodIn(me.skills.consort)) {
            possibleOptions.add(ActionType.BUY_ITEM);
        }
        if (me.isRelativelyGoodIn(me.skills.sneak)) {
            possibleOptions.add(ActionType.STEAL_ITEM);
        }
        if (me.isRelativelyGoodIn(me.skills.craft) && me.isMoreOfAPersonnality(me.personnality.creative) && target == null) {
            possibleOptions.add(CRAFT_ITEM);
        }
        if (!me.isRelativelyGoodIn(me.skills.consort) && !me.isRelativelyGoodIn(me.skills.sneak)
            && !me.isRelativelyGoodIn(me.skills.craft)) {
            if (target == null) {
                // this option is only here if there is no target
                possibleOptions.add(HIRE_TO_CRAFT);
            }
            possibleOptions.add(HIRE_TO_BUY);
            possibleOptions.add(HIRE_TO_STEAL);
        }

        // choose one possible action
        ActionType choice = Util.randomIn(possibleOptions);
        if (choice == null) {
            choice = Util.randomIn(possibleOptionsIfNothingElse);
        }
        if (choice == CRAFT_ITEM) {
            CraftSomething craft = new CraftSomething(world);
            me.newGoal(craft, this);
            setTarget(craft.target);
            return Optional.empty();
        } else {
            // test if it is possible to do right now or a new goal is needed
            return Action.isGoalNeeded(me, choice, world, target, this);
        }
    }

    @Override
    public boolean isComplete(World world) {
        return owner.items.contains(target);
    }

    @Override
    public void setCompleted() {}

    @Override
    public void setTarget(Entity i) {
        if (target == null && i instanceof Item) {
            target = (Item) i;
        }
    }
}
