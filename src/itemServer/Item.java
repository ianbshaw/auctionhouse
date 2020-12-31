package itemServer;

import java.io.Serializable;

/*class to represent an item for auction*/
public class Item implements Serializable {
    private final String houseID;
    private final int itemID;
    private final int minBid;
    private int currentBid;
    private final String name;
    private String prevBidderName = "";
    private ItemStatus status = ItemStatus.AVAILABLE;

    public Item(String name, String houseID, int itemID, int minBid, int currentBid) {
        this.name = name;
        this.houseID = houseID;
        this.itemID = itemID;
        this.minBid = minBid;
        this.currentBid = currentBid;
    }

    public String getPrevBidderName() {
        return prevBidderName;
    }

    public void setPrevBidderName(String prevBidderName) {
        this.prevBidderName = prevBidderName;
    }

    public int getMinBid() {
        return minBid;
    }

    public int getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(int currentBid) {
        this.currentBid = currentBid;
    }

    public String getName() {
        return name;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public String getDesc() {
        String desc;
        switch (name) {
            case "The Stormlight Archive":
                desc = "Ongoing Epic Fantasy";
                break;
            case "Mistborn":
                desc = "Multiple collections of Fantasy/Heist";
                break;
            case "Elantris":
                desc = "Standalone Fantasy";
                break;
            case "Warbreaker":
                desc = "Standalone Fantasy/Romance";
                break;
            case "Dune":
                desc = "Sci-fi/Fantasy";
                break;
            case "The Expanse":
                desc = "Ongoing Sci-fi/Space Opera";
                break;
            case "A Song of Ice and Fire":
                desc = "Unfinished epic fantasy";
                break;
            case "The Wheel of Time":
                desc = "12 Book Epic Fantasy";
                break;
            case "Malazan the Fallen":
                desc = "Ongoing? Finished? Epic Fantasy";
                break;
            default:
                desc = "NULL";
                break;
        }
        return desc;
    }

    public String toString() {

        return ""
                + name
                + ", "
                + houseID
                + ", "
                + itemID
                + ", "
                + getDesc()
                + ", "
                + minBid
                + ", "
                + currentBid
                + ", "
                + status;
    }
}
