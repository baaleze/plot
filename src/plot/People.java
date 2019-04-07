package plot;

import plot.action.Action;
import plot.goal.Goal;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class People {

    public static long nextId = 0;

    public String name;
    public final long id;

    // STATS
    public int sway;
    public int consort;
    public int skirmish;
    public int wreck;
    public int scout;
    public int sneak;
    public int management;

    // personality spectrum
    public int honest;
    public int violent;
    public int racist;
    public int shy;
    public int greedy;
    public int envious;
    public int glutton;
    public int lust;
    public int ambitious;

    // Goals
    public List<Goal> goals = new LinkedList<>();

    // current action
    public Action action;

    // resources
    public int wealth;

    public People(final String name) {
        this.name = name;
        this.id = nextId++;
    }

    public boolean isGoodIn(int statToCompare) {
        int betterStatNb = 0;
        betterStatNb += statToCompare < sway ? 1 : 0;
        betterStatNb += statToCompare < skirmish ? 1 : 0;
        betterStatNb += statToCompare < consort ? 1 : 0;
        betterStatNb += statToCompare < wreck ? 1 : 0;
        betterStatNb += statToCompare < scout ? 1 : 0;
        betterStatNb += statToCompare < sneak ? 1 : 0;
        betterStatNb += statToCompare < management ? 1 : 0;
        betterStatNb += statToCompare < honest ? 1 : 0;
        betterStatNb += statToCompare < violent ? 1 : 0;
        betterStatNb += statToCompare < racist ? 1 : 0;
        betterStatNb += statToCompare < shy ? 1 : 0;
        betterStatNb += statToCompare < greedy ? 1 : 0;
        betterStatNb += statToCompare < envious ? 1 : 0;
        betterStatNb += statToCompare < glutton ? 1 : 0;
        betterStatNb += statToCompare < lust ? 1 : 0;
        betterStatNb += statToCompare < ambitious ? 1 : 0;
        return betterStatNb < 4;
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
        this.action.apply(world, this);
        // spawn new goals ??
        this.action.spawnGoals(world, this);
    }

    public void loseWealth(int cost) {
        this.wealth = Math.max(0, wealth - cost);
    }

}
