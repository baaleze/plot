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
            // check if item is stolen
            if (!item.rightfulOwners.contains(people) && !Util.testStat(people.skills.sway)) {
                // busted!
                People originalOwner = null;
                if(item.rightfulOwners.get(0) instanceof People) {
                    originalOwner = (People) item.rightfulOwners.get(0);
                }
                world.updateRelation(me, people, RelationType.STOLE, 2, item, originalOwner, true);
                // do i know the owner ?
                if (originalOwner != null && me.knownPeopleWithLocation.contains(originalOwner)) {
                    // I tell him
                    world.updateRelation(originalOwner, people, RelationType.STOLE, 3, item, null, true);
                }
                // no transaction is made
                // TODO update discredit
                return;
            } else if (item.rightfulOwners.size() > 1) { // maybe it was owned by other people
                for(Entity otherOwner: item.rightfulOwners.subList(0, item.rightfulOwners.size() - 1)) {
                    if (otherOwner instanceof People && otherOwner.equals(me)) {
                        // it's mine!!! I take it without paying
                        item.giveItem(people, me);
                        world.updateRelation(me, people, RelationType.STOLE, 4, item, null, true);
                        // TODO update discredit
                        return;
                    }
                }
            }
            if (Util.testStat(me.skills.consort - people.skills.consort + world.getRelationScore(people, me))) {
                // success
                int price = item.getSellingPrice(people.skills.consort, me.skills.consort);
                me.loseWealth(price);
                people.wealth += price;
                item.giveItem(people, me);
                world.addEvent(Event.buy(null, me, people, null, item, false, false, false, true));

                if (me.isMoreOfAPersonnality(me.personnality.honest)) {
                    world.updateRelation(people, me, RelationType.COLLEAGUE, 1, null, null, true);
                }
                if (people.isMoreOfAPersonnality(people.personnality.honest)) {
                    world.updateRelation(me, people, RelationType.COLLEAGUE, 1, null, null, true);
                }

            } else if (people.isMoreOfAPersonnality(people.personnality.greedy) && Util.testStat(people.skills.sway)) {
                // go stealed! no item is gained
                int price = item.getSellingPrice(people.skills.sway, me.skills.consort);
                me.loseWealth(price);
                people.wealth += price;
                world.updateRelation(me, people, RelationType.STOLE, 2, null, null, true);
                world.updateReputation(people, world.whereIs(me), -1);
                world.addEvent(Event.steal(null, people, me, null, null, price, false, false, true, true));
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
                item.giveItem(place, me);
                world.addEvent(Event.buy(null, me, null, place, item, false, false, true, true));

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
