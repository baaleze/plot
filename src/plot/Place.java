package plot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Place {

    private static final int MIN_POP = 50;

    private static final int MAX_POP = 1000;

    public static long nextId = 0;

    public final List<Integer> popHistory = new LinkedList<>();

    public final long id;
    public String name;
    public int population;
    public List<People> residents = new LinkedList<>();
    public Map<Item, String> items = new HashMap<>();

    public int x;
    public int y;

    public Place(final String name) {
        this.id = nextId++;
        this.name = name;
        this.population = ((int) Math.random() * (MAX_POP - MIN_POP)) + MIN_POP;
    }

    public void update() {
        // store old pop
        this.popHistory.add(this.population);
        // update!
    }

    public boolean livesHere(final People p) {
        return this.residents.contains(p);
    }

}
