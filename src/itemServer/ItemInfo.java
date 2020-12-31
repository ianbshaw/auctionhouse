package itemServer;

import java.io.Serializable;

/*class that represents item information to be passed into a list to display later*/
public class ItemInfo implements Serializable {
    private final String desc;
    private final int minBid;
    private final int currentBid;
    private final String name;

    public ItemInfo(String name, String desc, int minBid, int currentBid) {
        this.name = name;
        this.desc = desc;
        this.minBid = minBid;
        this.currentBid = currentBid;
    }

    public String getName() {
        return name;
    }

    public int getMinBid() {
        return minBid;
    }

    public int getCurrentBid() {
        return currentBid;
    }

    public String toString() {
        return name  + "- " + desc + ", " + minBid + ", " + currentBid;
    }
}
