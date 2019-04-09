package plot.action;


import plot.Item;
import plot.Util;
import plot.World;
import plot.goal.GetRich;
import plot.people.People;

public class CraftItem extends Action {

    private static final int COST_FACTOR = 10;
    private int mag;

    public CraftItem(int craft, int wealth) {
        // compute magnitude of the item crafted
        mag = craft;
        if (mag * COST_FACTOR > wealth) {
            mag = (int) Math.floor(wealth / COST_FACTOR);
        }
        this.monthCount = mag - (craft - mag); // faster to do lesser item when skilled
    }

    @Override
    public void apply(World world, People me) {
        // new item is created!
        me.loseWealth(mag * COST_FACTOR);
        Item item = world.generateNewItem();
        me.items.add(item);
    }

    @Override
    public void spawnGoals(World world, People me) {
        if (me.isMoreOfAPersonnality(me.personnality.greedy) && Util.testStat(me.personnality.greedy)) {
            me.newGoal(new GetRich(me.personnality.greedy, me.wealth), null);
        }
    }
}
