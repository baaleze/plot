package plot.action;

import plot.*;
import plot.goal.Steal;
import plot.people.People;

public class SellItem extends Action {
    private final Item item;

    public SellItem(Item item) {
        this.item = item;
    }

    @Override
    public void apply(World world, People me) {
        Place place = world.whereIs(me);
        // check if item is stolen
        if (!item.rightfulOwners.contains(me) && !Util.testStat(me.skills.sway - Place.PLACE_SKILL + world.getReputation(me, place))) {
            // busted!
            item.confiscate(me, place);
            world.updateReputation(me, place, -2);
            // TODO update secret!
            return;
        } else if (item.rightfulOwners.size() > 1) { // maybe it was owned by other people
            for(Entity otherOwner: item.rightfulOwners.subList(0, item.rightfulOwners.size() - 1)) {
                if (otherOwner instanceof Place && place.equals(otherOwner)) {
                    // this place owned it!!
                    item.confiscate(me, place);
                    world.updateReputation(me, place, -4);
                    // TODO update discredit
                    return;
                } else if (otherOwner instanceof People && Util.testStat(world.getReputation((People) otherOwner, place))) {
                    // a good citizen was stolen !
                    item.confiscate(me, place);
                    world.updateReputation(me, place, -1);
                    // TODO update discredit
                    return;
                }
            }
        }
        // normal transaction
        if (Util.testStat(me.skills.consort - Place.PLACE_SKILL + world.getReputation(me, place))) {
            // success
            int price = (me.skills.consort - Place.PLACE_SKILL) * 10 * (1 + place.population / 100);
            me.wealth += price;
            place.wealth = Math.max(0, place.wealth - price);

            // give item
            item.giveItem(me, place);

            world.addEvent(Event.sellItem(null, me, place, item, false, false, true, false));

            if (me.isMoreOfAPersonnality(me.personnality.honest)) {
                world.updateReputation(me, place, 1);
            }
        }
    }

    @Override
    public void spawnGoals(World world, People me) {
        // steal it back ???
        if (me.isMoreOfAPersonnality(me.personnality.greedy) && me.isRelativelyGoodIn(me.skills.sneak) && Util.testStat(me.skills.sneak)) {
            me.newGoal(new Steal(item), null);
        }
    }
}
