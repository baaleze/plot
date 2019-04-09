package plot.goal;

import plot.World;
import plot.action.Action;
import plot.people.People;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class Goal {

    public GoalType type;
    public List<Goal> prerequisites = new LinkedList<>();

    public abstract Optional<? extends Action> generateAction(World world, People me);

}
