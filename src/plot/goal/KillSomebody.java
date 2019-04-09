package plot.goal;

import plot.Util;
import plot.World;
import plot.action.Action;
import plot.action.ActionType;
import plot.people.People;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static plot.action.ActionType.*;

public class KillSomebody extends Goal {

    private final People target;

    public KillSomebody(People target) {
        this.target = target;
        this.type = GoalType.KILL_SOMEBODY;
    }

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        // action could be Stealing, trading, selling,
        List<ActionType> possibleOptions = new LinkedList<>();
        ActionType[] possibleOptionsIfNothingElse = {
                KILL
        };
        if (me.isRelativelyGoodIn(me.skills.sneak) || me.isRelativelyGoodIn(me.skills.skirmish)) {
            possibleOptions.add(ActionType.KILL);
        }
        // TODO add hire hitman

        // choose one possible action
        ActionType choice = Util.randomIn(possibleOptions);
        if (choice == null) {
            choice = Util.randomIn(possibleOptionsIfNothingElse);
        }
        // test if it is possible to do right now or a new goal is needed
        return Action.isGoalNeeded(me, choice, world, null, this);
    }
}
