package plot.goal;

import plot.People;
import plot.Place;
import plot.World;
import plot.action.Action;
import plot.action.Move;

import java.util.Optional;

public class Travel extends Goal {
    public Place target;

    public Travel(final Place t) {
        this.type = GoalType.TRAVEL_TO;
        this.target = t;
    }

    @Override
    public Optional<? extends Action> generateAction(final World world, final People me) {
        final Place start = world.whereIs(me);
        if (me.wealth > world.dist(start, this.target) * World.COST_TO_TRAVEL) {
            // enough money
            return Optional.of(new Move(start, this.target));
        } else {
            // not enough, need to get rich
            GetRich g = new GetRich(me.greedy, me.wealth);
            me.goals.add(g);
            // add as prerequisite to this goal
            this.prerequisites.add(g);
            return Optional.empty();
        }
    }

}
