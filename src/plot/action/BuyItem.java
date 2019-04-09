package plot.action;

import plot.*;
import plot.goal.KillSomebody;
import plot.goal.Steal;
import plot.people.People;

public class BuyItem extends Action {
    private final Item item;
    private final Place place;
    private final People people;

    public BuyItem(Item i, Place place, People p) {
        this.item = i;
        this.place = place;
        this.people = p;
    }

    @Override
    public void apply(World world, People me) {
        if (people != null) {
            if (Util.testStat(me.skills.consort - people.skills.consort + world.getRelationScore(people, me))) {
                // success
                int price = item.getSellingPrice(people.skills.consort, me.skills.consort);
                me.loseWealth(price);
                people.wealth += price;
                me.items.add(item);
                people.items.remove(item);

                if (me.isMoreOfAPersonnality(me.personnality.honest)) {
                    world.updateRelation(people, me, RelationType.COLLEAGUE, 1, null, null, true);
                }
                if (people.isMoreOfAPersonnality(people.personnality.honest)) {
                    world.updateRelation(me, people, RelationType.COLLEAGUE, 1, null, null, true);
                }

            } else if (people.isMoreOfAPersonnality(people.personnality.greedy) && Util.testStat(people.skills.sway)) {
                // go stealed!
                int price = item.getSellingPrice(people.skills.sway, me.skills.consort);
                me.loseWealth(price);
                people.wealth += price;
                world.updateRelation(me, people, RelationType.STOLE, 2, null, null, true);
                // vengeance
                if (me.isMoreOfAPersonnality(me.personnality.vengeful) || me.isRelativelyGoodIn(me.skills.sneak)) {
                    me.newGoal(new Steal(people), null);
                } else if (me.isMoreOfAPersonnality(me.personnality.vengeful) && me.isMoreOfAPersonnality(me.personnality.violent)) {
                    me.newGoal(new KillSomebody(people), null);
                }
            }
        } else {
            if (Util.testStat(me.skills.consort - Place.PLACE_SKILL + world.getReputation(me, place))) {
                // success
                int price = item.getSellingPrice(Place.PLACE_SKILL, me.skills.consort);
                me.loseWealth(price);
                place.wealth += price;
                me.items.add(item);
                place.items.remove(item);

                if (me.isMoreOfAPersonnality(me.personnality.honest)) {
                    world.updateReputation(me, place, 1);
                }

            }
        }
    }

    @Override
    public void spawnGoals(World world, People me) {

    }


}
