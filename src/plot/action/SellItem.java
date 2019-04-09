package plot.action;

import plot.Item;
import plot.Place;
import plot.Util;
import plot.goal.Steal;
import plot.people.People;
import plot.World;

public class SellItem extends Action {
    private final Item item;

    public SellItem(Item item) {
        this.item = item;
    }

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
        // steal it back ???
        if (me.isMoreOfAPersonnality(me.personnality.greedy) && me.isRelativelyGoodIn(me.skills.sneak) && Util.testStat(me.skills.sneak)) {
            me.newGoal(new Steal(item), null);
        }
    }
}
