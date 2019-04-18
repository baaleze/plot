package plot;

import plot.people.People;

import java.util.LinkedList;
import java.util.List;

public class Item extends Entity {

    public String name;
    public String description;
    public int worth;
    public int mag;

    public List<Entity> rightfulOwners = new LinkedList<>();
    public People thief;

    public Item(List<String> itemNames) {
        name = Util.randomIn(itemNames);
        mag = name.length() / 2;
        worth = 20 * mag;
    }

    public int getSellingPrice(int sellerStat, int buyerStat) {
        return (int) Math.floor(worth * (1 + (sellerStat - buyerStat)/10.0));
    }

    public void giveItem(Entity from, Entity to) {
        if (from instanceof People) {
            ((People)from).items.remove(this);
        } else {
            ((Place)from).items.remove(this);
        }
        if (to.equals(thief)) {
            // the thief succesfully sold its stolen item
            this.thief = null;
        } else {
            rightfulOwners.remove(from);
        }
        rightfulOwners.add(to);
        if (from instanceof People) {
            ((People)to).items.add(this);
        } else {
            ((Place)to).addItemToDefaultCoffer(this);
        }
    }


    public void stealItem(Entity from, People to) {
        if (from instanceof People) {
            ((People)from).items.remove(this);
        } else {
            ((Place)from).items.remove(this);
        }
        this.thief = to;
        to.items.add(this);
    }

    public void confiscate(People me, Place place) {
        if (me.equals(thief)) {
            thief = null;
        }
        me.items.remove(this);
        place.addItemToDefaultCoffer(this);
    }
}
