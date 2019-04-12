package plot.goal;

import plot.Entity;
import plot.World;
import plot.action.Action;
import plot.people.People;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class Goal {

    public GoalType type;
    public People owner;
    public List<Goal> prerequisites = new LinkedList<>();
    public boolean isDoneBySomeoneElse = false;

    public abstract Optional<? extends Action> generateAction(World world, People me);

    public abstract boolean isComplete(World world);
    public abstract void setCompleted();

    public abstract void setTarget(Entity i);

    public void setOwner(People p) {
        owner = p;
    }
}
