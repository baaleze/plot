package plot.action;

import plot.*;
import plot.people.People;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Getting info
 */
public class Listen extends Action {

    private Entity target;

    public Listen(Entity target) {
        this.target = target;
    }

    @Override
    public void apply(World world, People me) {
        // for each known person in the city
        Place myPlace = world.whereIs(me);
        me.knownPeopleWithLocation.stream().filter(p -> world.whereIs(p).equals(myPlace)).forEach(p -> {
            // test if we can gather some info about something he knows
            if (Util.testStat(me.skills.consort + world.getRelationScore(p, me))) {
                // about what ?
                if (Math.random() < 0.5) {
                    if (target instanceof People && !me.knownPeopleWithLocation.contains(target) && p.knownPeopleWithLocation.contains(target)) {
                        // learn about the thing!!
                        me.learnAbout((People) target);
                    } else {
                        // random people
                        me.learnAbout(Util.randomIn(p.knownPeopleWithLocation));
                    }
                } else {
                    if (target instanceof Item && !me.knownItems.contains(target) &&  p.knownItems.contains(target)) {
                        me.learnAbout((Item) target);
                    } else {
                        // random item
                        me.learnAbout(Util.randomIn(p.knownItems));
                    }
                }
            }
        });
        // learn from the place
        if (Util.testStat(me.skills.scout - myPlace.population / 100) || Util.testStat(me.skills.consort + world.getReputation(me, myPlace))) {
            if (Math.random() < 0.5) {
                if (target instanceof People && !me.knownPeopleWithLocation.contains(target) && myPlace.residents.contains(target)) {
                    me.learnAbout((People) target);
                } else {
                    // random people
                    me.learnAbout(Util.randomIn(myPlace.residents));
                }
            } else {
                if (target instanceof Item && !me.knownItems.contains(target) && myPlace.items.containsKey(target)) {
                    me.learnAbout((Item) target);
                } else {
                    // random item
                    me.learnAbout(Util.randomIn(myPlace.items.keySet().toArray(new Item[]{})));
                }
            }
        }

    }

    @Override
    public void spawnGoals(World world, People me) {

    }
}
