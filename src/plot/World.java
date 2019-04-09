package plot;

import plot.people.People;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class World {

    public static final int COST_TO_TRAVEL = 10;

    List<People> people;
    List<Place> places;
    List<Item> items;
    Map<People, List<Relation>> relations;
    Map<People, Map<Place, Integer>> reputations;

    public void create() {

    }

    /**
     * Update for 1 month.
     */
    public void update() {
        // All that aren't doing anything people think and decide
        this.people.forEach(p -> p.decideWhatToDo(this));
        // Apply what people are doing (relations change)
        this.people.forEach(p -> p.applyAction(this));
        // Things happen that could impact people
        this.happenings();
        // Update places
        this.places.forEach(Place::update);

    }

    private void happenings() {
        // TODO Auto-generated method stub

    }

    public int dist(final Place p1, final Place p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    public People whoHas(Item i) {
        for(People p: this.people) {
            if (p.items.contains(i)) {
                return p;
            }
        }
        return null;
    }

    public Place whereIs(final People p) {
        for (final Place place : this.places) {
            if (place.livesHere(p)) {
                return place;
            }
        }
        return null;
    }

    public Place whereIs(final Item i) {
        for (Place place: this.places) {
            if (place.items.containsKey(i)) {
                return place;
            }
        }
        return null;
    }

    public Place getNearestPlace(Place start) {
        Place nearest = null;
        int dist = Integer.MAX_VALUE;

        for(Place p : places) {
            int d = dist(start, p);
            if (d < dist) {
                nearest = p;
                dist = d;
            }
        }
        return nearest;
    }

    public void updateRelation(People from, People to, RelationType relationType, int diff, Item item, People thirdParty) {
        updateRelation(from, to, relationType, diff, item, thirdParty, false);
    }

    public void updateRelation(People from, People to, RelationType relationType, int diff, Item item, People thirdParty, boolean knowsLocation) {
        List<Relation> relations = this.relations.get(from);
        for(Relation r: relations) {
            if (r.type == relationType
                && (item == null || item.equals(r.item))
                && (thirdParty == null || thirdParty.equals(r.item))
            ) {
                r.intensity += diff;
                return;
            }
        }
        // not found
        relations.add(new Relation(from, to, relationType, diff, item, thirdParty));

        // whenever relations are updated, the from guy knows the other
        from.knownPeople.add(to);
        if (knowsLocation) {
            from.knownPeopleWithLocation.add(to);
        }
    }

    public int getRelationScore(People from, People to) {
        List<Relation> relations = this.relations.get(from);
        int score = 0;
        for(Relation r: relations) {
            switch (r.type) {
                case HATE:
                case STOLE:
                case KILLED_RELATIVE:
                case ATTACKED_ME:
                    score -= r.intensity;
                    break;
                case ACCOMPLICE:
                case COLLEAGUE:
                case LOVER:
                case FAMILY:
                case FRIEND:
                case MENTOR:
                case MARRIAGE:
                case SUPERIOR:
                    score += r.intensity;
                    break;
                default:
                    break;
            }
        }
        return score;
    }
    public People getHatedTarget(People me, boolean inSameCity) {
        People target = null;
        int relation = Integer.MAX_VALUE;
        if (inSameCity) {
            Place myPlace = whereIs(me);
            // check in people where I know where they are
            for(People p: me.knownPeopleWithLocation) {
                int r = getRelationScore(me, p);
                if (whereIs(p).equals(myPlace) && r < relation) {
                    // found one in same city that I hate more
                    target = p;
                    relation = r;
                }
            }
        } else {
            for(People p: me.knownPeopleWithLocation) {
                int r = getRelationScore(me, p);
                if (r < relation) {
                    // found one in same city that I hate more
                    target = p;
                    relation = r;
                }
            }
        }
        return target;
    }

    public Item generateNewItem() {
        // TODO
        return new Item();
    }

    public void updateReputation(People me, Place p, int diff) {
        Map<Place, Integer> reps = reputations.get(me);
        if (reps.containsKey(p)) {
            reps.put(p, reps.get(p) + diff);
        } else {
            reps.put(p, diff);
        }
    }

    public List<People> getAllRelatives(People people) {
        return this.relations.get(people).stream().filter(r -> r.type.isRelative()).map(r -> r.p2).collect(Collectors.toList());
    }

    public int getReputation(People me, Place place) {
        return reputations.get(me).getOrDefault(place, 0);
    }
}
