package plot;

import java.util.List;
import java.util.Map;

public class World {

    public static final int COST_TO_TRAVEL = 10;

    List<People> people;
    List<Place> places;
    List<Item> items;
    Map<People, Relation> relations;
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

    public Place whereIs(final People p) {
        for (final Place place : this.places) {
            if (place.livesHere(p)) {
                return place;
            }
        }
        return null;
    }

}
