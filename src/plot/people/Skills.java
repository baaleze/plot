package plot.people;

import plot.Util;

public class Skills {
    private static final int MAX_SKILL = 6;
    public int sway;
    public int consort;
    public int skirmish;
    public int wreck;
    public int scout;
    public int sneak;
    public int management;
    public int craft;

    public Skills() {
        sway = Util.randomInt(MAX_SKILL);
        consort = Util.randomInt(MAX_SKILL);
        skirmish = Util.randomInt(MAX_SKILL);
        wreck = Util.randomInt(MAX_SKILL);
        skirmish = Util.randomInt(MAX_SKILL);
        scout = Util.randomInt(MAX_SKILL);
        sneak = Util.randomInt(MAX_SKILL);
        management = Util.randomInt(MAX_SKILL);
        craft = Util.randomInt(MAX_SKILL);
    }
}
