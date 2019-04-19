package plot;

import plot.people.People;

public class Event {
    private final boolean forAll;
    private final boolean forConcerned;
    Entity[] args;
    int amount;
    EventType type;
    boolean hired;
    boolean posted;

    public Event(EventType t, boolean hired, boolean posted, boolean forAll, boolean forConcerned,  int a, Entity... entites) {
        type = t;
        amount = a;
        args = entites;
        this.hired = hired;
        this.posted = posted;
        this.forAll = forAll;
        this.forConcerned = forConcerned;
    }

    public boolean canSee(People p) {
        if (p == null) {
            return true;
        }
        if (forAll) {
            return true;
        }
        if (forConcerned) {
            for(Entity e: args) {
                if (e.equals(p)) {
                    return true;
                } else if (e instanceof Place) {
                    return ((Place)e).residents.contains(p);
                }
            }
        }
        return p.equals(args[0]);
    }

    public enum EventType {
        STOLE_ITEM_FROM_PEOPLE, STOLE_ITEM_FROM_PLACE, STOLE_MONEY_FROM_PEOPLE, STOLE_MONEY_FROM_PLACE,
        KILL_PEOPLE, MUG_PEOPLE, PILLAGE_PLACE, MOVE_TO_CITY, LISTEN_INFO, BUY_ITEM_FROM_PLACE, BUY_ITEM_FROM_PEOPLE,
        SELL_ITEM_TO_PLACE, TRADE, GET_ITEM, GET_MONEY, CRAFTED_ITEM
    }

    public static Event moveToCity(People mover, Place from, Place to, boolean forAll, boolean forConcerned) {
        return new Event(EventType.MOVE_TO_CITY, false, false, forAll, forConcerned,  0, mover, from, to);
    }

    public static Event listenToInfo(People mover, Place from, boolean forAll, boolean forConcerned) {
        return new Event(EventType.LISTEN_INFO, false, false, forAll, forConcerned,  0, mover, from);
    }

    public static Event kill(People jobOwner, People killer, People people, boolean hired, boolean posted, boolean forAll, boolean forConcerned) {
        if (hired) {
            return new Event(EventType.KILL_PEOPLE, true, posted, forAll, forConcerned,  0, jobOwner, killer, people);
        } else if (posted) {
            return new Event(EventType.KILL_PEOPLE, false, true, forAll, forConcerned,  0, jobOwner, people);
        } else {
            return new Event(EventType.KILL_PEOPLE, false, false, forAll, forConcerned,  0, killer, people);
        }
    }

    public static Event mug(People jobOwner, People mugger, People people, Place place, boolean hired, boolean posted, boolean forAll, boolean forConcerned) {
            return new Event(EventType.MUG_PEOPLE, false, false, forAll, forConcerned, 0, mugger, people);
    }

    public static Event craft(People jobOwner, People crafter, Item item, boolean hired, boolean posted, boolean forAll, boolean forConcerned) {
        if (hired) {
            return new Event(EventType.CRAFTED_ITEM, true, posted, forAll, forConcerned, 0, jobOwner, crafter, item);
        } else if (posted) {
            return new Event(EventType.CRAFTED_ITEM, false, true, forAll, forConcerned, 0, jobOwner, item);
        } else {
            return new Event(EventType.CRAFTED_ITEM, false, false, forAll, forConcerned, 0, crafter, item);
        }
    }

    public static Event trade(People jobOwner, People trader, Place place, boolean hired, boolean posted, boolean forAll, boolean forConcerned) {
        if (hired) {
            return new Event(EventType.TRADE, true, posted, forAll, forConcerned, 0, jobOwner, trader, place);
        } else if (posted) {
            return new Event(EventType.TRADE, false, true, forAll, forConcerned,  0, jobOwner, place);
        } else {
            return new Event(EventType.TRADE, false, false, forAll, forConcerned,  0, trader, place);
        }
    }
    public static Event pillage(People jobOwner, People pillager, Place place, boolean hired, boolean posted, boolean forAll, boolean forConcerned) {
        return new Event(EventType.PILLAGE_PLACE, false, false, forAll, forConcerned,  0, pillager, place);
    }

    public static Event sellItem(People jobOwner, People seller, Place place, Item item, boolean hired, boolean posted, boolean forAll, boolean forConcerned) {
        return new Event(EventType.SELL_ITEM_TO_PLACE, false, false, forAll, forConcerned,  0, seller, item, place);
    }

    public static Event getItem(People jobOwner, People buyer, Item item, boolean hired, boolean posted, boolean forAll, boolean forConcerned) {
        // item
        if (hired) {
            return new Event(EventType.GET_ITEM, true, posted, forAll, forConcerned,  0, jobOwner, buyer, item);
        } else {
            return new Event(EventType.GET_ITEM, false, true, forAll, forConcerned,  0, jobOwner, item);
        }
    }
    public static Event getRich(People jobOwner, People buyer, boolean hired, boolean posted, boolean forAll, boolean forConcerned) {
        // item
        if (hired) {
            return new Event(EventType.GET_MONEY, true, posted, forAll, forConcerned,  0, jobOwner, buyer);
        } else {
            return new Event(EventType.GET_MONEY, false, true, forAll, forConcerned,  0, jobOwner);
        }
    }

    public static Event buy(People jobOwner, People buyer, People people, Place place, Item item, boolean hired, boolean posted, boolean forAll, boolean forConcerned) {
        // item
        if (people != null) {
            return new Event(EventType.BUY_ITEM_FROM_PEOPLE, false, false, forAll, forConcerned,  0, buyer, item, people);
        } else {
            // place
            return new Event(EventType.BUY_ITEM_FROM_PLACE, false, false, forAll, forConcerned, 0, buyer, item, place);
        }
    }

    public static Event steal(People jobOwner, People thief, People people, Place place, Item item, int amount, boolean hired, boolean posted, boolean forAll, boolean forConcerned) {
        // item
        if (item != null) {
            if (people != null) {
                // from people
                return new Event(EventType.STOLE_ITEM_FROM_PEOPLE, false, false, forAll, forConcerned,  0, thief, item, people);
            } else {
                // place
                return new Event(EventType.STOLE_ITEM_FROM_PLACE, false, false, forAll, forConcerned,  0, thief, item, place);
            }
        } else { // money
            if (people != null) {
                // from people
                return new Event(EventType.STOLE_MONEY_FROM_PEOPLE, false, false, forAll, forConcerned,  amount, thief, people);
            } else {
                // place
                return new Event(EventType.STOLE_MONEY_FROM_PLACE, false, false, forAll, forConcerned,  amount, thief, place);
            }
        }
    }


    @Override
    public String toString() {
        switch (type) {
            case TRADE:
                if (hired) {
                    return String.format("%s has hired %s to trade in %s", args[0], args[1], args[2]);
                } else if (posted) {
                    return String.format("%s has posted a job offer to trade in %s", args[0], args[1]);
                } else {
                    return String.format("%s has traded in %s", args[0], args[1]);
                }
            case MUG_PEOPLE:
                return String.format("%s has mugged %s of % gold", args[0], args[1], amount);
            case KILL_PEOPLE:
                if (hired) {
                    return String.format("%s has hired %s to kill %s", args[0], args[1], args[2]);
                } else if (posted) {
                    return String.format("%s has posted a job offer to kill %s", args[0], args[1]);
                } else {
                    return String.format("%s has killed %s", args[0], args[1]);
                }
            case LISTEN_INFO:
                return String.format("%s has listened for info in %s", args[0], args[1]);
            case MOVE_TO_CITY:
                return String.format("%s has moved from %s to %s", args[0], args[1], args[2]);
            case PILLAGE_PLACE:
                return String.format("%s has pillaged %s in %s", args[0], amount, args[1]);
            case BUY_ITEM_FROM_PEOPLE:
            case BUY_ITEM_FROM_PLACE:
                return String.format("%s has bought %s from %s", args[0], args[1], args[2]);
            case SELL_ITEM_TO_PLACE:
                return String.format("%s has sold %s in %s", args[0], args[1], args[2]);
            case STOLE_ITEM_FROM_PEOPLE:
            case STOLE_ITEM_FROM_PLACE:
                    return String.format("%s has stolen %s from %s", args[0], amount, args[1]);
            case STOLE_MONEY_FROM_PEOPLE:
            case STOLE_MONEY_FROM_PLACE:
                return String.format("%s has stolen %s gold from %s", args[0], args[1], args[2]);
            case CRAFTED_ITEM:
                if (hired) {
                    return String.format("%s has hired %s to craft %s", args[0], args[1], args[2]);
                } else if (posted) {
                    return String.format("%s has posted a job offer to craft %s", args[0], args[1]);
                } else {
                    return String.format("%s has crafted %s", args[0], args[1]);
                }
            case GET_ITEM:
                if (hired) {
                    return String.format("%s has hired %s to get %s", args[0], args[1], args[2]);
                } else  {
                    return String.format("%s has posted a job offer to get %s", args[0], args[1]);
                }
            case GET_MONEY:
                if (hired) {
                    return String.format("%s has hired %s to get money", args[0], args[1]);
                } else  {
                    return String.format("%s has posted a job offer to get money", args[0]);
                }
        }
        return "MISSING EVENT TYPE IN SWITCH";
    }
}
