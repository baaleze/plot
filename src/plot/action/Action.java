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
            // we must find a people to hire
            case HIRE_TO_KILL:
                return hire(me, world, target, sourceGoal, ActionType.KILL);
            case HIRE_TO_BUY:
                return hire(me, world, target, sourceGoal, ActionType.BUY_ITEM);
            case HIRE_TO_CRAFT:
                return hire(me, world, target, sourceGoal, ActionType.CRAFT_ITEM);
            case HIRE_TO_GET_SECRET:
                return hire(me, world, target, sourceGoal, ActionType.GET_SECRET);
            case HIRE_TO_MUG:
                return hire(me, world, target, sourceGoal, ActionType.MUG);
            case HIRE_TO_PILLAGE:
                return hire(me, world, target, sourceGoal, ActionType.STEAL_VIOLENTLY_PLACE);
            case HIRE_TO_SELL:
                return hire(me, world, target, sourceGoal, ActionType.SELL_ITEM);
            case HIRE_TO_STEAL:
                return hire(me, world, target, sourceGoal, ActionType.STEAL_PEOPLE);
            case HIRE_TO_TRADE:
                return hire(me, world, target, sourceGoal, ActionType.TRADE);
            case STEAL_VIOLENTLY_PLACE:
                return Optional.of(new Pillage(world.whereIs(me)));
            case STEAL_PLACE:
                // we can always steal a city
                Place toSteal = (Place) target;
                if (toSteal == null) {
                    toSteal = world.whereIs(me);
                    sourceGoal.setTarget(toSteal);
                }
                return Optional.of(new Steal(null, toSteal, null));
            case STEAL_PEOPLE:
                // steal money or item from someone
                if (me.knownPeopleWithLocation.contains(target)) {
                    // I know where he is
                    if (world.whereIs(me).equals(world.whereIs((People) target))) {
                        // already good place
                        People p = (People) target;
                        if (!p.items.isEmpty() && Math.random() < 0.5) {
                            return Optional.of(new Steal(Util.randomIn(p.items), null, p));
                        } else {
                            return Optional.of(new Steal(null, null, p));
                        }
                    } else {
                        // move there
                        me.newGoal(new Travel(world.whereIs((People) target)), sourceGoal);
                        break;
                    }
                } else {
                    // need info
                    me.newGoal(new GetInfo(target), sourceGoal);
                    break;
                }
            case STEAL_ITEM:
                // do we know of an item that can be aquired ?
                return acquireItem(me, world, AcquisitionType.STEAL, target,  sourceGoal);
            case BUY_ITEM:
                // check wealth
                if (target != null && me.wealth < ((Item)target).worth * 1.5) {
                    me.newGoal(new GetRich(me.personnality.greedy, me.wealth), sourceGoal);
                    break;
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
                    if (target instanceof Item) {
                        return Optional.of(new SellItem((Item) target));
                    } else {
                        Item i = Util.randomIn(me.items);
                        sourceGoal.setTarget(i);
                        return Optional.of(new SellItem(i));
                    }
                }
            case CRAFT_ITEM:
                // check wealth
                if (me.wealth > ((Item)target).mag * CraftItem.COST_FACTOR) {
                    // ok
                    return Optional.of(new CraftItem(me.skills.craft, me.wealth, (Item) target));
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
                    break;
                }
            case MUG:
                if (!(target instanceof People)) {
                    // find a target that I hate in the same city
                    People p = world.getHatedTarget(me, true);
                    if (p == null) {
                        // no one is here mug the city
                        return Optional.of(new Pillage(world.whereIs(me)));
                    } else {
                        sourceGoal.setTarget(p);
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
                        break;
                    } else {
                        sourceGoal.setTarget(p);
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
                        break;
                    }
                }
        }
        return Optional.empty();
    }

    private static Optional<Action> hire(People me, World world, Entity target, Goal sourceGoal, ActionType actionType) {
        People toHire = findPeopleToHireFor(actionType, me, world, target);
        if (toHire != null) {
            // found it
            return Optional.of(new HireFor(toHire, sourceGoal));
        } else {
            // post a job offer
            return Optional.of(new PostOffer(sourceGoal));
        }
    }

    private static People findPeopleToHireFor(ActionType kill, People me, World world, Entity target) {
        // find someone we know in the city
        for(People p: me.knownPeopleWithLocation) {
            if (canDoTheJob(kill, p)) {
                return p;
            }
        }
        return null;
    }

    private static boolean canDoTheJob(ActionType kill, People p) {
        switch (kill) {
            case KILL:
                return p.isRelativelyGoodIn(p.skills.sneak) || p.isRelativelyGoodIn(p.skills.skirmish);
            case MUG:
            case STEAL_VIOLENTLY_PLACE:
                return p.isRelativelyGoodIn(p.skills.skirmish);
            case CRAFT_ITEM:
                return p.isRelativelyGoodIn(p.skills.craft);
            case STEAL_ITEM:
            case STEAL_PLACE:
            case STEAL_PEOPLE:
            case GET_SECRET:
                return p.isRelativelyGoodIn(p.skills.sneak);
            case BUY_ITEM:
            case SELL_ITEM:
            case TRADE:
                return p.isRelativelyGoodIn(p.skills.consort);

            default: return false;
        }

    }

    private static Optional<Action> acquireItem(People me, World world, AcquisitionType type, Entity target, Goal sourceGoal) {
        // target has already been decided
        if (target instanceof Item) {
            Item item = (Item) target;
            Place myPlace = world.whereIs(me);
            if(me.knownItems.contains(item)) {
                // I know it
                Place itemPlace = world.whereIs(item);
                People owner = world.whoHas(item);
                if (itemPlace != null) {
                    if (myPlace.equals(itemPlace)) {
                        return Optional.of(type == AcquisitionType.STEAL ?
                                new Steal(item, myPlace, null) :
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
                                    new Steal(item, null, owner) :
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
                        sourceGoal.setTarget(i);
                        // steal it from place
                        return Optional.of(type == AcquisitionType.STEAL ?
                                new Steal(i, myPlace, null) :
                                new BuyItem(i, myPlace, null));
                    } else if (owner != null && me.knownPeopleWithLocation.contains(owner)) {
                        // someone has it and i know where he is
                        if (myPlace.equals(world.whereIs(owner))) { // same place GO!!
                            sourceGoal.setTarget(i);
                            return Optional.of(type == AcquisitionType.STEAL ?
                                    new Steal(i, null, owner) :
                                    new BuyItem(i, null, owner));
                        }
                    }
                }
                // no item found in this city, take one random known item and steal it or get info
                Item i = Util.randomIn(me.knownItems);
                Place itemPlace = world.whereIs(i);
                People owner = world.whoHas(i);
                if (i == null) {
                    me.newGoal(new GetInfo(null),sourceGoal);
                    return Optional.empty();
                } else {
                    sourceGoal.setTarget(i);
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
