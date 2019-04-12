package plot.goal;

import plot.Entity;
import plot.Item;
import plot.World;
import plot.action.Action;
import plot.people.People;

import java.util.Optional;

public class Job extends Goal {

    private static final int KILL_REWARD = 200;
    private static final int DEFAULT_REWARD = 50;
    public People patron;
    public Goal task;
    public int reward;
    public Item itemReward;
    public boolean didComplete = false;
    public int timeTillJobNeedsToBeDoneAnyway = 24;

    public Job(People patron, Goal t, int r, Item i) {
        this.patron = patron;
        task = t;
        reward = r;
        itemReward = i;
        type = task.type;
        task.isDoneBySomeoneElse = true;
    }

    public static int getReward(Goal task, People me, World world) {
        switch (task.type) {
            case CRAFT_ITEM:
                // depends on item
                return (int) (((CraftSomething)task).target.worth * 1.5);
            case GET_ITEM:
                return (int) (((GetItem)task).target.worth * 1.5);
            case KILL_SOMEBODY:
                if (((KillSomebody)task).target == null){
                    return KILL_REWARD;
                } else {
                    int rep = world.getReputation(((KillSomebody)task).target, world.whereIs(me));
                    if (rep < 0) {
                        return KILL_REWARD * 2;
                    } else {
                        return KILL_REWARD;
                    }
                }
                default:
                    return DEFAULT_REWARD;
        }
    }

    @Override
    public Optional<? extends Action> generateAction(World world, People me) {
        return task.generateAction(world, me);
    }

    @Override
    public boolean isComplete(World world) {
        timeTillJobNeedsToBeDoneAnyway--;
        if (timeTillJobNeedsToBeDoneAnyway < 0) {
            // the one that created the job decides that nobody will do it, he might as well try again
            task.isDoneBySomeoneElse = false;
        }
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
