package plot.goal;

import plot.Entity;
import plot.Item;
import plot.World;
import plot.action.Action;
import plot.people.People;

import java.util.Optional;

public class Job extends Goal {

    public People patron;
    public Goal task;
    public int reward;
    public Item itemReward;
    public boolean didComplete = false;

    public Job(People patron, Goal t, int r, Item i) {
        this.patron = patron;
        task = t;
        reward = r;
        itemReward = i;
    }

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        // TODO
        return Optional.empty();
    }

    @Override
    public boolean isComplete(World world) {
        if (task.isComplete(world) && !didComplete) {
            // complete this job
            didComplete = true;
            // give reward
            if (itemReward != null) {
                patron.items.remove(itemReward);
                owner.items.add(itemReward);
            }
            patron.loseWealth(reward);
            owner.wealth += reward;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setCompleted() {

    }

    @Override
    public void setTarget(Entity i) {

    }

}
