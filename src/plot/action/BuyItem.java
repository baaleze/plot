package plot.action;

import plot.Entity;
import plot.Item;
import plot.People;
import plot.Place;

public class BuyItem extends StealItem {
    public BuyItem(Item i, Place myPlace, People p) {
        super(i, myPlace, p);
    }
}
