package plot;

import plot.people.People;
import plot.ui.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class World {

    public static final int COST_TO_TRAVEL = 10;
    private static final int NB_CITIES = 10;
    private static final int NB_PEOPLE = 25;
    private static final int WORLD_DIM = 10;

    List<People> people = new LinkedList<>();
    List<Place> places = new LinkedList<>();
    Map<People, List<Relation>> relations = new HashMap<>();
    Map<People, Map<Place, Integer>> reputations = new HashMap<>();
    List<String> itemNames = new LinkedList<>();
    private static String[] loots = {
            "Sewer", "Crypt", "Dungeon", "Castle", "Bank", "Museum", "Tower", "Graveyard"
    };
    private String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public Place[][] map = new Place[WORLD_DIM][WORLD_DIM];
    private Log log;

    public void create() {
        // load items
        loadItems();
        // generate cities
        for(int i = 0; i < NB_CITIES; i++) {
            Place p = new Place(Util.randomIn(itemNames));
            for (int n = 0; n < Util.randomInt(4); n++) {
                p.items.put(new Item(itemNames), Util.randomIn(loots));
            }
            places.add(p);
            // place on map
            while (true) {
                int x = Util.randomInt(WORLD_DIM);
                int y = Util.randomInt(WORLD_DIM);
                if (map[x][y] == null) { // space is free!
                    map[x][y] = p;
                    p.x = x;
                    p.y = y;
                    break;
                }
            }
        }
        // generate people
        while (people.size() < NB_PEOPLE) {
            Place place = Util.randomIn(places);
            // generate a family
            People f = newPeople(true, place);
            People m = newPeople(false, place);
            updateRelation(f, m, RelationType.MARRIAGE, 3, null, null);
            updateRelation(m, f, RelationType.MARRIAGE, 3, null, null);
            updateRelation(f, m, RelationType.LOVER, 5, null, null);
            updateRelation(m, f, RelationType.LOVER, 5, null, null);
            int familySize = Util.randomInt(3);
            for(int n = 0; n < familySize; n++) {
                // children
                People child = newPeople(Math.random() < 0.5, place);
                updateRelation(child, m, RelationType.FAMILY, 5, null, null);
                updateRelation(m, child, RelationType.FAMILY, 5, null, null);
            }
        }
    }

    private People newPeople(boolean male, Place place) {
        People p = new People("Mr " + Util.randomIn(alphabet.split(""))+ Util.randomIn(alphabet.split("")), male);
        if (Math.random() < 0.2) {
            p.items.add(new Item(itemNames));
        }
        place.residents.add(p);
        people.add(p);
        return p;
    }

    private void loadItems() {
        //read file into stream, try-with-resources
        try (Stream<String> stream = Files.lines(Paths.get("items.txt"))) {
            itemNames = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (relations == null) {
            relations = new LinkedList<>();
            this.relations.put(from, relations);
        }
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
        return new Item(itemNames);
    }

    public void updateReputation(People me, Place p, int diff) {
        if (!reputations.containsKey(me)) {
            reputations.put(me, new HashMap<>());
        }
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

    public void setLog(Log log) {
        this.log = log;
    }

    public void log(String s) {
        this.log.log(s);
    }
}
