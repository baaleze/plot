package plot.action;

import plot.*;
import plot.goal.*;
import plot.people.People;

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
                return Optional.of(new Pillage(world.whereIs(me)));
            case STEAL_PLACE:
                // we can always steal a city
                return Optional.of(new StealPlace(world.whereIs(me)));
            case STEAL_ITEM:
                // do we know of an item that can be aquired ?
                return acquireItem(me, world, AcquisitionType.STEAL, target,  sourceGoal);
            case BUY_ITEM:
                // check wealth
                if (me.wealth < ((Item)target).worth * 1.5) {
                    me.newGoal(new GetRich(me.personnality.greedy, me.wealth), sourceGoal);
                } else {
                    return acquireItem(me, world, AcquisitionType.BUY, target, sourceGoal);
                }
            case TRADE:
                // you can always trade
                return Optional.of(new Trade());
            case LISTEN:
                // you can always get info
                return Optional.of(new Listen(target));
            case SELL_ITEM:
                if (me.items.isEmpty()) {
                    // no items!
                    me.newGoal(new GetItem(), sourceGoal);
                    break;
                } else {
                    // sell item to the place we live
                    if (target != null && target instanceof Item) {
                        return Optional.of(new SellItem((Item) target));
                    } else {
                        return Optional.of(new SellItem(Util.randomIn(me.items)));
                    }
                }
            case CRAFT_ITEM:
                // check wealth
                if (me.wealth > 5) {
                    // ok
                    return Optional.of(new CraftItem(me.skills.craft, me.wealth));
                } else {
                    me.newGoal(new GetRich(me.personnality.greedy, me.wealth), sourceGoal);
                    break;
                }
            case MOVE_TO_CITY:
                Place start = world.whereIs(me);
                Place finish = (Place) target;
                if (finish == null) {
                    // find a city to move to, nearest one
                    finish = world.getNearestPlace(world.whereIs(me));
                }
                if (me.wealth > world.dist(start, finish) * World.COST_TO_TRAVEL) {
                    // enough money
                    return Optional.of(new Move(start, finish));
                } else {
                    // not enough, need to get rich
                    me.newGoal(new GetRich(me.personnality.greedy, me.wealth), sourceGoal);
                    return Optional.empty();
                }
            case MUG:
                if (!(target instanceof People)) {
                    // find a target that I hate in the same city
                    People p = world.getHatedTarget(me, true);
                    if (p == null) {
                        // no one is here mug the city
                        return Optional.of(new Pillage(world.whereIs(me)));
                    } else {
                        return Optional.of(new Mug(p));
                    }
                } else {
                    People toMug = (People) target;
                    if (me.knownPeopleWithLocation.contains(toMug)) {
                        // ok I know him
                        return Optional.of(new Mug(toMug));
                    } else {
                        // find him
                        me.newGoal(new GetInfo(toMug), sourceGoal);
                        return Optional.empty();
                    }
                }
            case KILL:
                if (!(target instanceof People)) {
                    // find a target that I hate the most
                    People p = world.getHatedTarget(me, true);
                    if (p == null) {
                        // no one to kill move else where at random
                        me.newGoal(new Travel(null), sourceGoal);
                        return Optional.empty();
                    } else {
                        return Optional.of(new Kill(p));
                    }
                } else {
                    People toKill = (People) target;
                    if (me.knownPeopleWithLocation.contains(toKill)) {
                        // ok I know where he is
                        return Optional.of(new Kill(toKill));
                    } else {
                        // find him
                        me.newGoal(new GetInfo(toKill), sourceGoal);
                        return Optional.empty();
                    }
                }
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
                        me.newGoal(new Travel(itemPlace), sourceGoal);
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
                            me.newGoal(new Travel(world.whereIs(owner)),sourceGoal);
                            return Optional.empty();
                        }
                    } else {
                        // find him
                        me.newGoal(new GetInfo(owner),sourceGoal);
                        return Optional.empty();
                    }
                }
            } else {
                // find it
                me.newGoal(new GetInfo(null), sourceGoal);
                return Optional.empty();
            }
        } else {
            // determine target
            if (me.knownItems.isEmpty()) {// no
                me.newGoal(new GetInfo(null), sourceGoal);
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
                    me.newGoal(new Travel(itemPlace), sourceGoal);
                    return Optional.empty();
                }
                if (owner != null) {
                    if (me.knownPeopleWithLocation.contains(owner)) {
                        // new goal, go there !
                        me.newGoal(new Travel(world.whereIs(owner)), sourceGoal);
                        return Optional.empty();
                    } else {
                        // find him
                        me.newGoal(new GetInfo(owner), sourceGoal);
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
