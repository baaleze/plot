package plot.goal;

import plot.Entity;
import plot.Util;
import plot.World;
import plot.action.Action;
import plot.action.ActionType;
import plot.people.People;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static plot.action.ActionType.*;

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
                TRADE, SELL_ITEM, STEAL_PLACE, STEAL_VIOLENTLY_PLACE, MUG, FIND_JOB
        };
        if (me.isRelativelyGoodIn(me.skills.consort)) {
            possibleOptions.add(TRADE);
            possibleOptions.add(SELL_ITEM);
        } else {
            possibleOptions.add(HIRE_TO_TRADE);
            possibleOptions.add(HIRE_TO_SELL);
        }
        if (me.isRelativelyGoodIn(me.skills.sneak)) {
            possibleOptions.add(STEAL_PLACE);
        } else {
            possibleOptions.add(HIRE_TO_STEAL);
        }
        if (me.isRelativelyGoodIn(me.skills.skirmish) && me.isMoreOfAPersonnality(me.personnality.violent)) {
            possibleOptions.add(MUG);
        } else {
            possibleOptions.add(HIRE_TO_MUG);
        }
        if (me.isRelativelyGoodIn(me.skills.wreck) && me.isMoreOfAPersonnality(me.personnality.violent)) {
            possibleOptions.add(STEAL_VIOLENTLY_PLACE);
        } else {
            possibleOptions.add(HIRE_TO_PILLAGE);
        }
        possibleOptions.add(FIND_JOB);

        // choose one possible action
        ActionType choice = Util.randomIn(possibleOptions);
        if (choice == null) {
            choice = Util.randomIn(possibleOptionsIfNothingElse);
        }
        // test if it is possible to do right now or a new goal is needed
        return Action.isGoalNeeded(me, choice, world, null, this);
    }

    @Override
    public boolean isComplete(World world) {
        return owner.wealth > wealthNeed;
    }

    @Override
    public void setCompleted() {

    }

    @Override
    public void setTarget(Entity i) {

    }
}
