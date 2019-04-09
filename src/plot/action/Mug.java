package plot.action;

import plot.goal.KillSomebody;
import plot.people.People;
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
        if (Util.testStat(me.skills.skirmish - target.skills.skirmish)) {
            // success take half wealth
            me.wealth = target.wealth / 2;
            target.loseWealth(target.wealth / 2);
            // new relation
            world.updateRelation(target, me, RelationType.STOLE, 4, null, null);
            if (target.isMoreOfAPersonnality(target.personnality.greedy) || target.isMoreOfAPersonnality(target.personnality.vengeful)) {
                target.newGoal(new Steal(me), null);
            }
            if (target.isMoreOfAPersonnality(target.personnality.violent) || target.isMoreOfAPersonnality(target.personnality.vengeful)) {
                target.newGoal(new KillSomebody(me), null);
            }

        } else {
            // failure
            if (Util.testStat(target.skills.skirmish - me.skills.skirmish)) {
                // I got killed
                me.killer = target;
                for(People relative: world.getAllRelatives(me)) {
                    // add or amplify relation
                    world.updateRelation(relative, target, RelationType.KILLED_RELATIVE, 4, null, me);
                    // maybe vengeance ?
                    if (relative.isMoreOfAPersonnality(relative.personnality.vengeful) && Util.testStat(relative.personnality.vengeful)) {
                        relative.newGoal(new KillSomebody(target), null);
                    }
                }
            } else {
                // got found anyway
                world.updateReputation(me, world.whereIs(me), -2);
                world.updateRelation(target, me, RelationType.ATTACKED_ME, 2, null, null, true);
            }

        }
    }

    @Override
    public void spawnGoals(World world, People me) {
        // TODO
    }
}
