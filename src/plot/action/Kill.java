package plot.action;

import plot.RelationType;
import plot.Util;
import plot.goal.KillSomebody;
import plot.people.People;
import plot.World;
import plot.people.SecretKill;

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
                target.kill(me);
                // secret
                SecretKill secret = me.killedSomeone(target, world);
                if (!Util.testStat(me.skills.sneak)) {
                    // busted
                    secret.divulgate(world, world.whereIs(me));
                } // else ok got away with it
            } else {
                // failure
                if (!Util.testStat(me.skills.sneak)) {
                    // got found
                    world.updateReputation(me, world.whereIs(me), -1);
                    world.updateRelation(target, me, RelationType.ATTACKED_ME, 2, null, null, true);
                } // else ok got away with it
            }
        } else {
            // no stealth
            if (Util.testStat(me.skills.skirmish - target.skills.skirmish)) {
                // success
                target.kill(me);
                // secret
                SecretKill secret = new SecretKill(me, target);
                // immediately tell everyone since no stealth
                secret.divulgate(world, world.whereIs(me));
            } else {
                // failure
                if (Util.testStat(target.skills.skirmish - me.skills.skirmish)) {
                    // I got killed - no secret, self defense
                    me.kill(target);
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
