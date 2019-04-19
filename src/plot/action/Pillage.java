package plot.action;

import plot.*;
import plot.goal.KillSomebody;
import plot.goal.Steal;
import plot.people.People;

public class Pillage extends Action {

    private Place target;

    public Pillage(Place place) {
        this.target = place;
    }

    @Override
    public void apply(World world, People me) {
        // uses wreck
        if (Util.testStat(me.skills.wreck - Place.PLACE_SKILL)) {
            world.addEvent(Event.pillage(null, me, target, false, false, true, false));
            // success take half wealth
            me.wealth = target.wealth / 2;
            target.wealth -= (target.wealth / 2);
            // new relation
            world.updateReputation(me, target, -4);
        } else {
            // failure
            // TODO log failures
            world.updateReputation(me, target, -4);
        }
    }

    @Override
    public void spawnGoals(World world, People me) {
        // TODO
    }
}
