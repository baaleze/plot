package plot.goal;

import plot.Util;
import plot.World;
import plot.action.Action;
import plot.action.ActionType;
import plot.people.People;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static plot.action.ActionType.MUG;

public class GetRich extends Goal {

    private static final int MONEY_GOAL_MULT = 100;
    public final int wealthNeed;

    public GetRich(int greed, int currentWealth) {
        this.type = GoalType.GET_RICH;
        this.wealthNeed = currentWealth + greed * MONEY_GOAL_MULT;
    }

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        // action could be Stealing, trading, selling,
        List<ActionType> possibleOptions = new LinkedList<>();
        ActionType[] possibleOptionsIfNothingElse = {
                ActionType.TRADE, ActionType.SELL_ITEM, ActionType.STEAL_PLACE, ActionType.STEAL_VIOLENTLY_PLACE, MUG
        };
        if (me.isRelativelyGoodIn(me.skills.consort)) {
            possibleOptions.add(ActionType.TRADE);
            possibleOptions.add(ActionType.SELL_ITEM);
        }
        if (me.isRelativelyGoodIn(me.skills.sneak)) {
            possibleOptions.add(ActionType.STEAL_PLACE);
        }
        if (me.isRelativelyGoodIn(me.skills.skirmish) && me.isMoreOfAPersonnality(me.personnality.violent)) {
            possibleOptions.add(MUG);
        }
        if (me.isRelativelyGoodIn(me.skills.wreck) && me.isMoreOfAPersonnality(me.personnality.violent)) {
            possibleOptions.add(ActionType.STEAL_VIOLENTLY_PLACE);
        }
        // TODO also GET A JOB

        // choose one possible action
        ActionType choice = Util.randomIn(possibleOptions);
        if (choice == null) {
            choice = Util.randomIn(possibleOptionsIfNothingElse);
        }
        // test if it is possible to do right now or a new goal is needed
        return Action.isGoalNeeded(me, choice, world, null, this);
    }
}
