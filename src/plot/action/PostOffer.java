package plot.action;

import plot.Item;
import plot.Util;
import plot.World;
import plot.goal.Goal;
import plot.goal.Job;
import plot.people.People;

import java.util.List;

public class PostOffer extends Action {
    private final Goal task;

    public PostOffer(Goal sourceGoal) {
        this.task = sourceGoal;
    }

    @Override
    public void apply(World world, People me) {
        // put it in the current city job offer listing
        List<Job> jobOffers = world.whereIs(me).jobOffers;
        for(Job j: jobOffers){
            if (j.task.equals(task)) {
                // already posted
                return;
            }
        }
        int reward = Job.getReward(task, me, world);
        Item item = null;
        if (reward > me.wealth && !me.isMoreOfAPersonnality(me.personnality.greedy)) {
            item = Util.randomIn(me.items);
            reward = me.wealth;
        }
        jobOffers.add(new Job(me, task, reward, item));

    }

    @Override
    public void spawnGoals(World world, People me) {

    }
}
