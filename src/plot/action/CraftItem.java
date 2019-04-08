package plot.action;

import plot.People;
import plot.World;

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
        // TODO
    }

    @Override
    public void spawnGoals(World world, People me) {
        // TODO
    }
}
