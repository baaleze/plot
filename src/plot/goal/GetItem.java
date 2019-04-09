package plot.goal;

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
        if (me.isRelativelyGoodIn(me.skills.craft) && me.isMoreOfAPersonnality(me.personnality.creative)) {
            possibleOptions.add(CRAFT_ITEM);
        }

        // choose one possible action
        ActionType choice = Util.randomIn(possibleOptions);
        if (choice == null) {
            choice = Util.randomIn(possibleOptionsIfNothingElse);
        }
        // test if it is possible to do right now or a new goal is needed
        return Action.isGoalNeeded(me, choice, world, null, this);
    }
}
