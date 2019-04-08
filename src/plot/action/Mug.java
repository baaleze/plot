package plot.action;

import plot.People;
import plot.RelationType;
import plot.Util;
import plot.World;
import plot.goal.Steal;

public class Mug extends Action {
    private final People target;

    public Mug(People target) {
        this.target = target;
    }

    @Override
    public void apply(World world, People me) {
        // uses skirmish
        if (Util.testStat(me.skirmish - target.skirmish)) {
            // success take half wealth
            me.wealth = target.wealth / 2;
            target.loseWealth(target.wealth / 2);
            // new relation
            world.updateRelation(target, me, RelationType.STOLE, 4, item, thirdParty);
            if (Util.testStat(target.greedy) || Util.testStat(target.vengeful)) {
                target.newGoal(new Steal(me), null);
            }

        } else {
            // failure

        }
    }

    @Override
    public void spawnGoals(World world, People me) {
        // TODO
    }
}
