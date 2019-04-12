package plot.action;

import plot.Item;
import plot.Util;
import plot.World;
import plot.goal.Goal;
import plot.goal.Job;
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
        }
    }

    @Override
    public void spawnGoals(World world, People me) {

    }
}
