package plot.action;

import plot.Event;
import plot.Item;
import plot.Util;
import plot.World;
import plot.goal.*;
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
        switch (task.type) {
            case CRAFT_ITEM:
                world.addEvent(Event.craft(me, null, ((CraftSomething)task).target, false, true, true, true));
                break;
            case KILL_SOMEBODY:
                world.addEvent(Event.kill(me, null, ((KillSomebody)task).target, false, true, true, true));
                break;
            case GET_ITEM:
                world.addEvent(Event.getItem(me, null, ((GetItem)task).target, false, true, true, true));
                break;
            case GET_RICH:
                world.addEvent(Event.getRich(me, null, false, true, true, true));
                break;
        }
    }

    @Override
    public void spawnGoals(World world, People me) {

    }
}
