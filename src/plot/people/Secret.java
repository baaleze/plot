package plot.people;

import plot.Item;
import plot.Place;
import plot.World;

public class Secret {
    public final int money;
    public final People owner;
    public final Item item;
    public final People people;
    public final Place place;

    protected Secret(People me, Item i, People p, Place pl, int money) {
        owner = me;
        item = i;
        people = p;
        place = pl;
        this.money = money;
    }

    public void divulgate(World world, Place p) {
        clearSecret(world);
    }

    public void clearSecret(World world) {
        // by default when a secret is divulgated it has no more power after
        // so we delete it from everywhere
        owner.secrets.remove(this);
        world.people.forEach(dude ->  {
            dude.knownSecrets.remove(this);
        });
    }

    public void divulgateToConcernedPeople(World world) {

    }
}
