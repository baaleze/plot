package plot.goal;

import plot.Entity;
import plot.Item;
import plot.Util;
import plot.World;
import plot.action.Action;
import plot.action.ActionType;
import plot.people.People;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static plot.action.ActionType.*;

public class RecoverItem extends Goal {

    public Item item;

    public RecoverItem(Item i) {
        this.item = i;
    }

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        // action could be Stealing, trading, selling,
        List<ActionType> possibleOptions = new LinkedList<>();
        ActionType[] possibleOptionsIfNothingElse = {
                BUY_ITEM, STEAL_ITEM
        };
        if (me.isRelativelyGoodIn(me.skills.consort)) {
            possibleOptions.add(ActionType.BUY_ITEM);
        } else {
            possibleOptions.add(HIRE_TO_BUY);
        }
        if (me.isRelativelyGoodIn(me.skills.sneak)) {
            possibleOptions.add(ActionType.STEAL_ITEM);
        } else {
            possibleOptions.add(HIRE_TO_STEAL);
        }

        // choose one possible action
        ActionType choice = Util.randomIn(possibleOptions);
        if (choice == null) {
            choice = Util.randomIn(possibleOptionsIfNothingElse);
        }

        // test if it is possible to do right now or a new goal is needed
        return Action.isGoalNeeded(me, choice, world, item, this);
    }

    @Override
    public boolean isComplete(World world) {
        return owner.items.contains(item);// got it back!
    }

    @Override
    public void setCompleted() {

    }

    @Override
    public void setTarget(Entity i) {

    }
}
