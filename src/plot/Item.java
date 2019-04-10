package plot;

import java.util.List;

public class Item extends Entity {

    public String name;
    public String description;
    public int worth;
    public int mag;

    public Item(List<String> itemNames) {
        name = Util.randomIn(itemNames);
        mag = name.length() / 2;
        worth = 20 * mag;
    }

    public int getSellingPrice(int sellerStat, int buyerStat) {
        return (int) Math.floor(worth * (1 + (sellerStat - buyerStat)/10.0));
    }

}
