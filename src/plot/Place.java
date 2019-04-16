package plot;

import plot.goal.Goal;
import plot.goal.Job;
import plot.people.People;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Place extends Entity {

    private static final int MIN_POP = 50;

    private static final int MAX_POP = 1000;
    public static final int PLACE_SKILL = 3;

    public static long nextId = 0;

    public final List<Integer> popHistory = new LinkedList<>();
    public final List<Integer> wealthHistory = new LinkedList<>();

    public final long id;
    public String name;
    public int population;
    public int wealth;
    public List<People> residents = new LinkedList<>();
    public Map<Item, String> items = new HashMap<>();
    public List<Job> jobOffers = new LinkedList<>();

    public int x;
    public int y;

    public Place(final String name) {
        this.id = nextId++;
        this.name = name;
        this.population = (int)( Math.random() * (MAX_POP - MIN_POP)) + MIN_POP;
        this.wealth = population - 1;
    }

    public void update() {
        // store old pop
        this.popHistory.add(this.population);
        this.wealthHistory.add(this.wealth);
        // update!
        if (this.wealth > this.population) {
            this.wealth -= this.population / 10;
            this.population += this.population / 10;
        } else {
            this.wealth += this.population / 10;
        }
    }

    public boolean livesHere(final People p) {
        return this.residents.contains(p);
    }

    public void addItemToDefaultCoffer(Item item) {
        items.put(item, World.loots[0]);
    }
}
