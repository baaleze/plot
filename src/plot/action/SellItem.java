package plot.action;

import plot.Item;
import plot.People;
import plot.World;

public class SellItem extends Action {
    private final Item item;

    public SellItem(Item item) {
        this.item = item;
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
