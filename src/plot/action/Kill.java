package plot.action;

import plot.RelationType;
import plot.Util;
import plot.goal.KillSomebody;
import plot.people.People;
import plot.World;

public class Kill extends Action {
    private final People target;

    public Kill(People target) {
        this.target = target;
    }

    @Override
    public void apply(World world, People me) {
        boolean useStealth;
        if (me.isRelativelyGoodIn(me.skills.sneak) && me.isRelativelyGoodIn(me.skills.skirmish)) {
            useStealth = Math.random() < 0.5;
        } else {
            useStealth = me.isRelativelyGoodIn(me.skills.sneak);
        }
        if (useStealth) {
            if (Util.testStat(me.skills.sneak - target.skills.sneak)) {
                // success
                target.killer = me;
                if (!Util.testStat(me.skills.sneak)) {
                    // city got alerted
                    world.updateReputation(me, world.whereIs(me), -2);
                    // relatives too
                    for(People relative: world.getAllRelatives(target)) {
                        // add or amplify relation
                        world.updateRelation(relative, me, RelationType.KILLED_RELATIVE, -4, null, target);
                        // maybe vengeance ?
                        if (relative.isMoreOfAPersonnality(relative.personnality.vengeful) && Util.testStat(relative.personnality.vengeful)) {
                            relative.newGoal(new KillSomebody(me), null);
                        }
                    }
                } // else ok got away with it
            } else {
                // failure
                if (!Util.testStat(me.skills.sneak)) {
                    // got found
                    world.updateReputation(me, world.whereIs(me), -1);
                    world.updateRelation(target, me, RelationType.ATTACKED_ME, -2, null, null);
                } // else ok got away with it
            }
        }

    }

    @Override
    public void spawnGoals(World world, People me) {
        // TODO
    }
}
