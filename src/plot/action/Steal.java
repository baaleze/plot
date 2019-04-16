package plot.action;

import plot.*;
import plot.goal.KillSomebody;
import plot.people.People;

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
                    if (g instanceof plot.goal.Steal && ((plot.goal.Steal)g).target.equals(people) && g.prerequisites.isEmpty()) {
                        g.setCompleted();
                    }
                });
                if (item != null) {
                    // transfer
                    item.stealItem(people, me);
                } else {
                    int stolen = Math.min(people.wealth, me.skills.sneak * SNEAK_FACTOR);
                    people.loseWealth(stolen);
                    me.wealth += stolen;
                }
                if (!Util.testStat(me.skills.sneak)) {
                    // got found
                    world.updateReputation(me, world.whereIs(me), -1);
                    world.updateRelation(people, me, RelationType.STOLE, 2, item, null, true);
                    // vengeance
                    if (people.isMoreOfAPersonnality(people.personnality.vengeful) || people.isRelativelyGoodIn(people.skills.sneak)) {
                        people.newGoal(new plot.goal.Steal(me), null);
                    } else if (people.isMoreOfAPersonnality(people.personnality.vengeful) && people.isMoreOfAPersonnality(people.personnality.violent)) {
                        people.newGoal(new KillSomebody(me), null);
                    }
                }
            }
        } else {
            // stealing place
            if (Util.testStat(me.skills.sneak - place.population / 100)) {
                // did it
                me.goals.forEach(g -> {
                    if (g instanceof plot.goal.Steal && ((plot.goal.Steal)g).target.equals(place) && g.prerequisites.isEmpty()) {
                        g.setCompleted();
                    }
                });
                if (item != null) {
                    // transfer
                    item.stealItem(place, me);
                } else {
                    int stolen = Math.min(place.wealth, me.skills.sneak * SNEAK_FACTOR);
                    place.wealth -= stolen;
                    me.wealth += stolen;
                }
                if (!Util.testStat(me.skills.sneak)) {
                    // got found
                    world.updateReputation(me, world.whereIs(me), -2);
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
