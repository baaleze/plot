package plot;

public class Item extends Entity {

    public String name;
    public String description;
    public int worth;

    public int getSellingPrice(int sellerStat, int buyerStat) {
        return (int) Math.floor(worth * (1 + (sellerStat - buyerStat)/10.0));
    }

}
