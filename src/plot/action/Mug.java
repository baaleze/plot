package plot.action;

import plot.People;
import plot.World;

public class Mug extends Action {
    private final People target;

    public Mug(People target) {
        this.target = target;
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
