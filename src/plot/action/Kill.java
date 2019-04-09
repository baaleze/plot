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
                    world.updateRelation(target, me, RelationType.ATTACKED_ME, -2, null, null, true);
                } // else ok got away with it
            }
        } else {
            // no stealth
            if (Util.testStat(me.skills.skirmish - target.skills.skirmish)) {
                // success
                target.killer = me;
                // city got alerted anyway
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
            } else {
                // failure
                if (!Util.testStat(target.skills.skirmish - me.skills.skirmish)) {
                    // I got killed
                    me.killer = target;
                    for(People relative: world.getAllRelatives(me)) {
                        // add or amplify relation
                        world.updateRelation(relative, target, RelationType.KILLED_RELATIVE, -4, null, me);
                        // maybe vengeance ?
                        if (relative.isMoreOfAPersonnality(relative.personnality.vengeful) && Util.testStat(relative.personnality.vengeful)) {
                            relative.newGoal(new KillSomebody(target), null);
                        }
                    }
                } else {
                    // got found anyway
                    world.updateReputation(me, world.whereIs(me), -2);
                    world.updateRelation(target, me, RelationType.ATTACKED_ME, -2, null, null, true);
                }
            }
        }

    }

    @Override
    public void spawnGoals(World world, People me) {
        // more bloodshed ?
        if(me.isMoreOfAPersonnality(me.personnality.violent) && Util.testStat(me.personnality.violent)) {
            me.newGoal(new KillSomebody(null), null);
        }
        // TODO become a hitman?
    }
}
