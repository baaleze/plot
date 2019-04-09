package plot.action;

import plot.*;
import plot.goal.KillSomebody;
import plot.goal.Steal;
import plot.people.People;

public class StealItem extends Action {
    private final Item item;
    private final Place place;
    private final People people;

    public StealItem(Item i, Place place, People people) {
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
                people.items.remove(item);
                me.items.add(item);
                if (!Util.testStat(me.skills.sneak)) {
                    // got found
                    world.updateReputation(me, world.whereIs(me), -1);
                    world.updateRelation(people, me, RelationType.STOLE, 2, item, null, true);
                    // vengeance
                    if (people.isMoreOfAPersonnality(people.personnality.vengeful) || people.isRelativelyGoodIn(people.skills.sneak)) {
                        people.newGoal(new Steal(me), null);
                    } else if (people.isMoreOfAPersonnality(people.personnality.vengeful) && people.isMoreOfAPersonnality(people.personnality.violent)) {
                        people.newGoal(new KillSomebody(me), null);
                    }
                }
            }
        } else {
            // stealing place
            if (Util.testStat(me.skills.sneak - place.population / 100)) {
                // did it
                place.items.remove(item);
                me.items.add(item);
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
            me.newGoal(new Steal(null), null);
        }
    }
}
