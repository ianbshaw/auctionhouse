package agent;

import itemServer.ItemInfo;

import java.io.Serializable;

/*class to represent a bid request from a client*/
public class BidRequest implements Serializable {
    private int bidPrice;
    private ItemInfo item;
    private BidStatus status = BidStatus.ACTIVE;
    private String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public BidStatus getStatus() {
        return status;
    }

    public void setStatus(BidStatus status) {
        this.status = status;
    }

    public int getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(int bidPrice) {
        this.bidPrice = bidPrice;
    }

    public ItemInfo getItemInfo() {
        return item;
    }

    public void setItemInfo(ItemInfo item) {
        this.item = item;
    }

    @Override
    public java.lang.String toString() {
        return "bidPrice="
                + bidPrice
                + ", item="
                + item.getName()
                + ", status="
                + status
                + ", from='"
                + from;
    }
}
