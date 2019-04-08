package plot.action;

import plot.*;

public class StealItem extends Action {
    private final Item item;
    private final Place place;
    private final People people;

    public StealItem(Item i, Place place, People people) {
        this.item = i;
        this.place = place;
        this.people = people;
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
