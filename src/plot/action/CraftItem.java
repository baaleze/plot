package plot.action;


import plot.Event;
import plot.Item;
import plot.Util;
import plot.World;
import plot.goal.GetRich;
import plot.people.People;

public class CraftItem extends Action {

    public static final int COST_FACTOR = 10;
    private Item target;


    public CraftItem(int craft, int wealth, Item target) {
        // compute magnitude of the item crafted
        this.target = target;
        this.monthCount = target.mag - (craft - target.mag); // faster to do lesser item when skilled
    }

    @Override
    public void apply(World world, People me) {
        // new item is created!
        me.loseWealth(target.mag * COST_FACTOR);
        me.items.add(target);
        world.addEvent(Event.craft(null, me, target, false, false, true, false));
    }

    @Override
    public void spawnGoals(World world, People me) {
        if (me.isMoreOfAPersonnality(me.personnality.greedy) && Util.testStat(me.personnality.greedy)) {
            me.newGoal(new GetRich(me.personnality.greedy, me.wealth), null);
        }
    }
}
