package plot.action;

import plot.people.People;
import plot.Place;
import plot.World;

public class StealPlace extends Action {

    protected final Place target;

    public StealPlace(Place place) {
        this.target = place;
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
