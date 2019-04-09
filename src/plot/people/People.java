package plot.people;

import plot.Entity;
import plot.Item;
import plot.Place;
import plot.World;
import plot.action.Action;
import plot.goal.Goal;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class People extends Entity {

    public static final int DEFAULT_NB_SKILLS = 3;
    public static final int DEFAULT_NB_PERSONNALITY = 4;
    public static long nextId = 0;

    public String name;
    public final long id;

    public People killer;

    // skills
    public Skills skills;

    // personality spectrum
    public Personnality personnality;

    // Goals
    public List<Goal> goals = new LinkedList<>();

    // current action
    public Action action;

    // resources
    public int wealth;
    public List<Item> items = new LinkedList<>();

    // knowledge
    public List<Item> knownItems = new LinkedList<>();
    public List<People> knownPeople = new LinkedList<>();
    public List<People> knownPeopleWithLocation = new LinkedList<>();



    public People(final String name) {
        this.name = name;
        this.id = nextId++;
    }

    /**
     * Returns true if the skill value is at least the 4th best stat when compared to other.
     * @param statToCompare
     * @return
     */
    public boolean isRelativelyGoodIn(int statToCompare) {
        return this.isRelativelyGoodIn(statToCompare, DEFAULT_NB_SKILLS);
    }

    public boolean isMoreOfAPersonnality(int statToCompare) {
        return this.isMoreOfAPersonnality(statToCompare, DEFAULT_NB_PERSONNALITY);
    }

    /**
     * Returns true if the skill is at least the 'nbStatsThatCanBeHigher'th stat when compared to other.
     * The lower the number, the better the stat must be. With 1, the stat must be the best stat.
     * @param statToCompare
     * @param nbStatsThatCanBeHigher
     * @return
     */
    public boolean isRelativelyGoodIn(int statToCompare, int nbStatsThatCanBeHigher) {
        int betterStatNb = 0;
        betterStatNb += statToCompare < skills.sway ? 1 : 0;
        betterStatNb += statToCompare < skills.skirmish ? 1 : 0;
        betterStatNb += statToCompare < skills.consort ? 1 : 0;
        betterStatNb += statToCompare < skills.wreck ? 1 : 0;
        betterStatNb += statToCompare < skills.scout ? 1 : 0;
        betterStatNb += statToCompare < skills.sneak ? 1 : 0;
        betterStatNb += statToCompare < skills.management ? 1 : 0;
        betterStatNb += statToCompare < skills.craft ? 1 : 0;
        return betterStatNb < nbStatsThatCanBeHigher;
    }

    public boolean isMoreOfAPersonnality(int statToCompare, int nbStatsThatCanBeHigher) {
        int betterStatNb = 0;
        betterStatNb += statToCompare < personnality.honest ? 1 : 0;
        betterStatNb += statToCompare < personnality.violent ? 1 : 0;
        betterStatNb += statToCompare < personnality.racist ? 1 : 0;
        betterStatNb += statToCompare < personnality.shy ? 1 : 0;
        betterStatNb += statToCompare < personnality.greedy ? 1 : 0;
        betterStatNb += statToCompare < personnality.envious ? 1 : 0;
        betterStatNb += statToCompare < personnality.glutton ? 1 : 0;
        betterStatNb += statToCompare < personnality.lust ? 1 : 0;
        betterStatNb += statToCompare < personnality.ambitious ? 1 : 0;
        betterStatNb += statToCompare < personnality.vengeful ? 1 : 0;
        betterStatNb += statToCompare < personnality.creative ? 1 : 0;
        return betterStatNb < nbStatsThatCanBeHigher;
    }

    public void decideWhatToDo(final World world) {
        while (this.action == null) {
            // only if currently doing nothing
            final Optional<Goal> goal = this.getFirstAchievableGoal();
            if (goal.isPresent()) {
                // how can we achieve it ?
                final Optional<? extends Action> action = goal.get().generateAction(world, this);
                this.action = action.orElse(null);
                // if action is null, can't do it but a new goal was set
            } // no goal do nothing
        }
    }

    private Optional<Goal> getFirstAchievableGoal() {
        return this.goals.stream().filter(g -> g.prerequisites.isEmpty()).findFirst();
    }

    private List<Goal> getAchievableGoals() {
        return this.goals.stream().filter(g -> g.prerequisites.isEmpty()).collect(Collectors.toList());
    }

    public void applyAction(final World world) {
        // finished ?
        if (this.action.isFinished()) {
            this.action.apply(world, this);
            // spawn new goals ??
            this.action.spawnGoals(world, this);
            // done, remove it
            this.action = null;
        } // else the action is not yet done nothing happens yet
    }

    public void loseWealth(int cost) {
        this.wealth = Math.max(0, wealth - cost);
    }

    public void invalidateKnowledge(Item item) {
        this.knownItems.remove(item);
    }
    public void invalidateKnowledge(People p) {
        // Cannot forget people, only where they are
        this.knownPeopleWithLocation.remove(p);
    }

    public void kill(People killer) {
        this.killer = killer;
    }

    public void newGoal(Goal goal, Goal sourceGoal) {
        if (sourceGoal != null) {
            sourceGoal.prerequisites.add(goal);
        }
        goals.add(goal);
    }

    public void learnAbout(People people) {
        if (!knownPeople.contains(people)) {
            knownPeople.add(people);
        }
        if (!knownPeopleWithLocation.contains(people)) {
            knownPeopleWithLocation.add(people);
        }
    }
    public void learnAbout(Item item) {
        if (!knownItems.contains(item)) {
            knownItems.add(item);
        }
    }
}
