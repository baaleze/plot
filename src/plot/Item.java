package plot;

import java.util.List;

public class Item extends Entity {

    public String name;
    public String description;
    public int worth;

    public Item(List<String> itemNames) {
        name = Util.randomIn(itemNames);
        worth = 20 * name.length();
    }

    public int getSellingPrice(int sellerStat, int buyerStat) {
        return (int) Math.floor(worth * (1 + (sellerStat - buyerStat)/10.0));
    }

}
