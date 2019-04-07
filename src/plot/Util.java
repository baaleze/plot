package plot;

import java.util.List;

public class Util {

    public static <T> T randomIn(final List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get((int) Math.floor(Math.random() * list.size()));
        }
    }

    public static <T> T randomIn(final T[] list) {
        if (list == null || list.length == 0) {
            return null;
        } else {
            return list[(int) Math.floor(Math.random() * list.length)];
        }
    }

    public static int randomInt(final int high) {
        return (int) Math.floor(Math.random() * high);
    }

    public static int randomInt(final int low, final int high) {
        return (int) Math.floor(Math.random() * (high - low)) + low;
    }

    public static boolean testStat(int score) {
        if (score <= 0 ){
            return false;
        } else {
            // scores up to 9 are too good, always 90% chance
            return Math.random() * 10 < Math.min(9, score);
        }
    }

}
