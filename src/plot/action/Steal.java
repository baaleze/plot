package plot.action;

import plot.*;
import plot.goal.KillSomebody;
import plot.goal.RecoverItem;
import plot.people.People;
import plot.people.SecretSteal;

public class Steal extends Action {
    private static final int SNEAK_FACTOR = 50;
    private final Item item;
    private final Place place;
    private final People people;

    public Steal(Item i, Place place, People people) {
        this.item = i;
        this.place = place;
        this.people = people;
    }

    @Override
    public void apply(World world, People me) {

        if (people != null) {
            // stealing people
            if (Util.testStat(me.skills.sneak - people.skills.sneak)) {
                // did it
                me.goals.forEach(g -> {
                    if (g instanceof plot.goal.Steal && ((plot.goal.Steal)g).target != null
                            && ((plot.goal.Steal)g).target.equals(place) && g.prerequisites.isEmpty()) {
                        g.setCompleted();
                    }
                });
                SecretSteal s;
                if (item != null) {
                    // transfer
                    item.stealItem(people, me);
                    // secret
                    s = me.stolenSomething(people, item, world.whereIs(people), 0, world);
                    // automatic recover it back goal
                    people.newGoal(new RecoverItem(item), null);
                    world.addEvent(Event.steal(null, me, people, null, item, 0, false, false, false, false));
                } else {
                    int stolen = Math.min(people.wealth, me.skills.sneak * SNEAK_FACTOR);
                    people.loseWealth(stolen);
                    me.wealth += stolen;
                    // secret
                    s = me.stolenSomething(people, null, world.whereIs(people), stolen, world);
                    world.addEvent(Event.steal(null, me, people, null, null, stolen, false, false, false, false));
                }
                if (!Util.testStat(me.skills.sneak)) {
                    // got found - secret is out!
                    s.divulgate(world, world.whereIs(me));
                }
            }
        } else {
            // stealing place
            if (Util.testStat(me.skills.sneak - place.population / 100)) {
                // did it
                me.goals.forEach(g -> {
                    if (g instanceof plot.goal.Steal && ((plot.goal.Steal)g).target != null
                            && ((plot.goal.Steal)g).target.equals(place) && g.prerequisites.isEmpty()) {
                        g.setCompleted();
                    }
                });
                SecretSteal s;
                if (item != null) {
                    // transfer
                    item.stealItem(place, me);
                    // secret
                    s = me.stolenSomething(null, item, place, 0, world);
                    world.addEvent(Event.steal(null, me, null, place, item, 0, false, false, false, false));
                } else {
                    int stolen = Math.min(place.wealth, me.skills.sneak * SNEAK_FACTOR);
                    place.wealth -= stolen;
                    me.wealth += stolen;
                    // secret
                    s = me.stolenSomething(null, null, place, stolen, world);
                    world.addEvent(Event.steal(null, me, null, place, null, stolen, false, false, false, false));
                }
                if (!Util.testStat(me.skills.sneak)) {
                    // got found
                    s.divulgate(world, place);
                }
            }
        }
    }

    @Override
    public void spawnGoals(World world, People me) {
        // steal more ?
        if (me.isRelativelyGoodIn(me.skills.sneak) && me.isMoreOfAPersonnality(me.personnality.greedy) && Util.testStat(me.personnality.greedy)) {
            me.newGoal(new plot.goal.Steal(null), null);
        }
    }
}
