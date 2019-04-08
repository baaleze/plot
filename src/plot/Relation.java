package plot;

public class Relation {

    public People p1;
    public People p2;
    public RelationType type;
    public int intensity;
    public People thirdParty;
    public Item item;

    public Relation(People from, People to, RelationType relationType, int diff, Item item, People thirdParty) {
        this.p1 = from;
        this.p2 = to;
        this.type = relationType;
        this.thirdParty = thirdParty;
        this.item = item;
        this.intensity = diff;
    }
}
