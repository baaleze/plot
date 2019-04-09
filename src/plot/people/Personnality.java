package plot.people;

import plot.Util;

public class Personnality {

    private static final int MAX_PERSONNALITY = 6;
    public int honest;
    public int violent;
    public int racist;
    public int shy;
    public int greedy;
    public int envious;
    public int glutton;
    public int lust;
    public int ambitious;
    public int creative;
    public int vengeful;

    public Personnality() {
        honest = Util.randomInt(MAX_PERSONNALITY);
        violent = Util.randomInt(MAX_PERSONNALITY);
        racist = Util.randomInt(MAX_PERSONNALITY);
        shy = Util.randomInt(MAX_PERSONNALITY);
        greedy = Util.randomInt(MAX_PERSONNALITY);
        envious = Util.randomInt(MAX_PERSONNALITY);
        glutton = Util.randomInt(MAX_PERSONNALITY);
        lust = Util.randomInt(MAX_PERSONNALITY);
        ambitious = Util.randomInt(MAX_PERSONNALITY);
        creative = Util.randomInt(MAX_PERSONNALITY);
        vengeful = Util.randomInt(MAX_PERSONNALITY);
    }
}
