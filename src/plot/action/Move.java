package plot.action;

import plot.Event;
import plot.people.People;
import plot.Place;
import plot.Util;
import plot.World;
import plot.goal.Ambition;
import plot.goal.GetRich;

public class Move extends Action {

    private final Place start;
    private final Place target;

    public Move(final Place start, final Place target) {
        this.start = start;
        this.target = target;
        this.type = ActionType.MOVE_TO_CITY;
    }

    @Override
    public void apply(final World world, People me) {
        start.residents.remove(me);
        target.residents.add(me);
        me.loseWealth(world.dist(start, target) * World.COST_TO_TRAVEL);
        world.addEvent(Event.moveToCity(me, start, target, false,  true));
    }

    @Override
    public void spawnGoals(final World world, People me) {
        // maybe with ambition
        if (me.isMoreOfAPersonnality(me.personnality.ambitious) && Util.testStat(me.personnality.ambitious)) {
            me.goals.add(new Ambition(target));
        }
        // or greed
        if (me.isMoreOfAPersonnality(me.personnality.greedy) && Util.testStat(me.personnality.greedy)) {
            me.goals.add(new GetRich(me.personnality.greedy, me.wealth));
        }
    }

}
