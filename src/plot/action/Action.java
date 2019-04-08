package plot.action;

import plot.*;
import plot.goal.*;

import java.util.Optional;

public abstract class Action {

    protected int monthCount = 1;
    public ActionType type;

    /**
     * Checks what is needed to do an action, and EITHER <br>
     * returns the action if it is possible <br>
     * returns an empty option and adds a new goal as prerequisite of the sourceGoal
     *
     * @param me
     * @param choice
     * @param world
     * @param target
     * @param sourceGoal
     * @return
     */
    public static Optional<Action> isGoalNeeded(People me, ActionType choice, World world, Entity target, Goal sourceGoal) {
        switch (choice) {
            case STEAL_VIOLENTLY_PLACE:
                return Optional.of(new StealViolentlyPlace(world.whereIs(me)));
            case STEAL_PLACE:
                // we can always steal a city
                return Optional.of(new StealPlace(world.whereIs(me)));
            case STEAL_ITEM:
                // do we know of an item that can be aquired ?
                return acquireItem(me, world, AcquisitionType.STEAL, target,  sourceGoal);
            case BUY_ITEM:
                return acquireItem(me, world, AcquisitionType.BUY, target, sourceGoal);
            case SELL_ITEM:
                if (me.items.isEmpty()) {
                    // no items!
                    GetItem g = new GetItem();
                    sourceGoal.prerequisites.add(g);
                    me.goals.add(g);
                    break;
                } else {
                    // sell item to the place we live
                    return Optional.of(new SellItem(Util.randomIn(me.items)));
                }
            case CRAFT_ITEM:
                // check wealth
                if (me.wealth > 5) {
                    // ok
                    return Optional.of(new CraftItem(me.craft, me.wealth));
                } else {
                    GetRich g = new GetRich(me.greedy, me.wealth);
                    sourceGoal.prerequisites.add(g);
                    me.goals.add(g);
                    break;
                }
            case MOVE_TO_CITY:
                Place start = world.whereIs(me);
                Place finish = (Place) target;
                if (me.wealth > world.dist(start, finish) * World.COST_TO_TRAVEL) {
                    // enough money
                    return Optional.of(new Move(start, finish));
                } else {
                    // not enough, need to get rich
                    GetRich g = new GetRich(me.greedy, me.wealth);
                    me.goals.add(g);
                    // add as prerequisite to this goal
                    sourceGoal.prerequisites.add(g);
                    return Optional.empty();
                }
            case MUG:
        }
        return Optional.empty();
    }

    private static Optional<Action> acquireItem(People me, World world, AcquisitionType type, Entity target, Goal sourceGoal) {
        // target has already been decided
        if (target != null && target instanceof Item) {
            Item item = (Item) target;
            Place myPlace = world.whereIs(me);
            if(me.knownItems.contains(item)) {
                // I know it
                Place itemPlace = world.whereIs(item);
                People owner = world.whoHas(item);
                if (itemPlace != null) {
                    if (myPlace.equals(itemPlace)) {
                        return Optional.of(type == AcquisitionType.STEAL ?
                                new StealItem(item, myPlace, null) :
                                new BuyItem(item, myPlace, null));
                    } else {
                        Travel g = new Travel(itemPlace);
                        sourceGoal.prerequisites.add(g);
                        me.goals.add(g);
                        return Optional.empty();
                    }
                } else  {
                    // I know the owner then
                    if (me.knownPeopleWithLocation.contains(owner)) {
                        if (world.whereIs(owner).equals(myPlace)) {
                            return Optional.of(type == AcquisitionType.STEAL ?
                                    new StealItem(item, null, owner) :
                                    new BuyItem(item, null, owner));
                        } else {
                            // new goal, go there !
                            Travel g = new Travel(world.whereIs(owner));
                            sourceGoal.prerequisites.add(g);
                            me.goals.add(g);
                            return Optional.empty();
                        }
                    } else {
                        // find him
                        GetInfo g = new GetInfo(InfoType.PEOPLE_LOCATION, owner);
                        sourceGoal.prerequisites.add(g);
                        me.goals.add(g);
                        return Optional.empty();
                    }
                }
            } else {
                // find it
                GetInfo g = new GetInfo(InfoType.ITEM_LOCATION, null);
                sourceGoal.prerequisites.add(g);
                me.goals.add(g);
                return Optional.empty();
            }
        } else {
            // determine target
            if (me.knownItems.isEmpty()) {// no
                GetInfo g = new GetInfo(InfoType.ITEM_INFO, null);
                sourceGoal.prerequisites.add(g);
                me.goals.add(g);
                return Optional.empty();
            } else {// yes
                Place myPlace = world.whereIs(me);
                for (Item i : me.knownItems) {
                    Place itemPlace = world.whereIs(i);
                    People owner = world.whoHas(i);
                    // priority to items where we live
                    if (myPlace.equals(itemPlace)) {
                        // steal it from place
                        return Optional.of(type == AcquisitionType.STEAL ?
                                new StealItem(i, myPlace, null) :
                                new BuyItem(i, myPlace, null));
                    } else if (owner != null && me.knownPeopleWithLocation.contains(owner)) {
                        // someone has it and i know where he is
                        if (myPlace.equals(world.whereIs(owner))) { // same place GO!!
                            return Optional.of(type == AcquisitionType.STEAL ?
                                    new StealItem(i, null, owner) :
                                    new BuyItem(i, null, owner));
                        }
                    }
                }
                // no item found in this city, take one random known item and steal it or get info
                Item i = Util.randomIn(me.knownItems);
                Place itemPlace = world.whereIs(i);
                People owner = world.whoHas(i);
                if (itemPlace != null) {
                    // new goal, go there !
                    Travel g = new Travel(itemPlace);
                    sourceGoal.prerequisites.add(g);
                    me.goals.add(g);
                    return Optional.empty();
                }
                if (owner != null) {
                    if (me.knownPeopleWithLocation.contains(owner)) {
                        // new goal, go there !
                        Travel g = new Travel(world.whereIs(owner));
                        sourceGoal.prerequisites.add(g);
                        me.goals.add(g);
                        return Optional.empty();
                    } else {
                        // find him
                        GetInfo g = new GetInfo(InfoType.PEOPLE_LOCATION, owner);
                        sourceGoal.prerequisites.add(g);
                        me.goals.add(g);
                        return Optional.empty();
                    }
                } // else should not happen
            }
        }
        return Optional.empty();
    }

    public abstract void apply(World world, People me);

    public abstract void spawnGoals(World world, People me);

    public boolean isFinished() {
        monthCount--;
        return monthCount <= 0;
    }
}
