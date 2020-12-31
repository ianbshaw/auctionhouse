package ah;

/*class that acts as main controller for auction house*/
public class AuctionHouseClient{
    public AuctionHouseClient() {}

    public static void main(String[] args) {

        /*create auction house and run as thread*/
        AuctionHouse auction = new AuctionHouse();
        Thread t1 = new Thread(auction);
        t1.start();
    }
}
