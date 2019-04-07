package plot.action;

import plot.People;
import plot.World;

public abstract class Action {

    public ActionType type;

    public abstract void apply(World world, People me);

    public abstract void spawnGoals(World world, People me);

}
