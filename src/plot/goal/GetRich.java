package plot.goal;

import plot.People;
import plot.World;
import plot.action.Action;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
        List<Action> possibleOptions = new LinkedList<>();
        if (me.isGoodIn(me.consort)) {

        }
        return Optional.empty();
    }
}
