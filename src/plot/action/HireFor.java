package plot.action;

import plot.Event;
import plot.Item;
import plot.Util;
import plot.World;
import plot.goal.*;
import plot.people.People;

public class HireFor extends Action {

    public Goal task;
    public People hiree;

    public HireFor(People toHire, Goal sourceGoal) {
        this.hiree = toHire;
        task = sourceGoal;
    }

    @Override
    public void apply(World world, People me) {
        // test if the hiree accepts
        if(Util.testStat(me.skills.consort) || Util.testStat(hiree.personnality.greedy)) {
            // YES
            int reward = Job.getReward(task, me, world);
            Item item = null;
            if (reward > me.wealth && !me.isMoreOfAPersonnality(me.personnality.greedy)) {
                item = Util.randomIn(me.items);
                reward = me.wealth;
            }
            hiree.newGoal(new Job(me, task, reward, item), null);
            switch (task.type) {
                case CRAFT_ITEM:
                    world.addEvent(Event.craft(me, hiree, ((CraftSomething)task).target, true, false, false, true));
                    break;
                case KILL_SOMEBODY:
                    world.addEvent(Event.kill(me, hiree, ((KillSomebody)task).target, true, false, false, true));
                    break;
                case GET_ITEM:
                    world.addEvent(Event.getItem(me, hiree, ((GetItem)task).target, true, false, false, true));
                    break;
                case GET_RICH:
                    world.addEvent(Event.getRich(me, hiree, true, false, false, true));
                    break;
            }

        } // else he refused
    }

    @Override
    public void spawnGoals(World world, People me) {

    }
}
