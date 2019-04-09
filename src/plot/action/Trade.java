package plot.action;

import plot.Place;
import plot.Util;
import plot.people.People;
import plot.World;

public class Trade extends Action {
    @Override
    public void apply(World world, People me) {
        Place place = world.whereIs(me);
        if (Util.testStat(me.skills.consort - Place.PLACE_SKILL + world.getReputation(me, place))) {
            // success
            int price = (me.skills.consort - Place.PLACE_SKILL) * 10 * (1 + place.population / 100);
            me.wealth += price;
            place.wealth = Math.max(0, place.wealth - price);

            if (me.isMoreOfAPersonnality(me.personnality.honest)) {
                world.updateReputation(me, place, 1);
            }
        }
    }

    @Override
    public void spawnGoals(World world, People me) {

    }
}
