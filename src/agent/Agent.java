package agent;

import ah.AuctionHouseList;
import bank.Account;
import bank.Bank;
import itemServer.Item;
import itemServer.ItemInfo;
import itemServer.ItemListRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.*;

/*class to handle agent logic*/
public class Agent {
    private static Account account;
    private ArrayList<ArrayList<String>> ahList;
    private static AuctionHouseList auctionHouseList;
    private static Socket auctionSocket;
    private static BidRequest bid;
    private static boolean isBiddingFlag = false;
    private static String name;
    private static String bankIp = "127.0.0.1";
    private static final int bankPort = 4500;
    private static String auctionIp;
    private static int auctionPort = 4600;
    private static ItemListRequest itemList;

    /*constructor that creates agent account*/
    public Agent() {
        Random r = new Random();
        int id = r.nextInt(6000);
        name = "agent" + id;

        createAccount();
    }

    /*method to connect to a server with host and port, returns socket*/
    public static Socket serverConnect(String host, int port) {
        Socket server = null;
        try {
            server = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Connected to " + host + ":" + port);
        return server;
    }

    /*method to listen to responses from server*/
    public boolean listen(Socket server) {
        ObjectInputStream in = null;
        Object o;

        try {
            in = new ObjectInputStream(server.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            assert in != null;
            o = in.readObject();

            if (o instanceof AuctionHouseList) {
                auctionHouseList = (AuctionHouseList) o;
                ahList = auctionHouseList.getArrayList();
                System.out.println("Received Auction Houses...");
            } else if (o instanceof ItemListRequest) {
                itemList = (ItemListRequest) o;
                System.out.println("Received Items from Auction House...");
            } else if (o instanceof BidRequest) {
                bid = (BidRequest) o;
                System.out.println("Received Bid Response");

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (bid.getStatus() != BidStatus.OUTBID)
                            bid.setStatus(BidStatus.WIN);
                    }
                }, 10);

                if (bid.getStatus().equals(BidStatus.OUTBID)) {
                    System.out.println("You have been Outbid");
                    return false;
                } else if (bid.getStatus().equals(BidStatus.WIN)) {
                    System.out.println("Congrats you won the " +
                            bid.getItemInfo().getName());
                    return true;
                }
            } else if (o instanceof Item) {
                Item item = (Item) o;
                System.out.println("Thank you for the " + item.getName() + "!");
                isBiddingFlag = false;
            } else if (o instanceof Account) {
                account = (Account) o;
                if (account.getBalanceFlag()) {
                    System.out.println(
                            "Received Account Balance $" + account.getBalance());
                } else {
                    System.out.println(
                            "Received Account Number " +
                                    account.getAccount_number());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return true;
    }

    /*method to create an account with bank, sends request to bank server*/
    public void createAccount() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Registering with Bank");
        Random r = new Random();
        account = new Account(name, r.nextInt(1000));
        System.out.println("Enter the bank IP address:");
        bankIp = scanner.nextLine();
        Socket server = serverConnect(bankIp, bankPort);
        Bank.send(account, server);
        listen(server);
        System.out.println();
    }

    /*method to retrieve a list of auction houses, sends request to bank server and waits for response*/
    public void requestAuctionHouses() {
        System.out.println("Requesting Auction Houses");
        auctionHouseList = new AuctionHouseList();
        ahList = auctionHouseList.getArrayList();
        Socket server = serverConnect(bankIp, bankPort);
        Bank.send(auctionHouseList, server);
        listen(server);
        System.out.println("\nAuction Houses:");
        for (ArrayList<String> ar : ahList) {
            System.out.println("Name: " + ar.get(0) + " IP: "
                    + ar.get(1) + " Port: " + ar.get(2));
        }
        System.out.println();
    }

    /*method to retrieve a list of items from an auction house, sends request to auction house and waits for response*/
    public void requestItems() {
        System.out.println("Requesting Items from Auction House");
        itemList = new ItemListRequest();
        Bank.send(itemList, auctionSocket);
        listen(auctionSocket);
        System.out.println("\nItems:");
        int count = 0;
        for (ItemInfo info : itemList.getItems()) {
            System.out.println("Item #" + count + " : " + info.toString());
            count++;
        }
        System.out.println();
    }

    /*method to handle item selection*/
    public void chooseItem(int itemNumber) {
        String itemChoice = itemList.getItems().get(itemNumber).getName();
        System.out.println("You chose " + itemChoice);
    }

    /*method to retrieve account balance from bank server*/
    public void checkBalance() {
        account.setBalanceFlag(true);
        Socket server = serverConnect(bankIp, bankPort);
        Bank.send(account, server);
        listen(server);
    }

    /*method to connect to an auction house based on hostname and port*/
    public boolean connect(String name) {
        for (ArrayList<String> arr : ahList) {
            if (arr.get(0).equals(name)) {
                String auctionName = arr.get(0);
                auctionIp = arr.get(1);
                auctionPort = Integer.parseInt(arr.get(2));
                System.out.println("Connecting to " + auctionName + " on " +
                                    auctionIp + ":" + auctionPort);
                auctionSocket = serverConnect("localhost", 4800);
                return true;
            }
        }
        return false;
    }

    /*method to handle an agent bid, sent to auction house and listens for response*/
    public void bidItem(int bidPrice, int itemChoice) {
        bid = new BidRequest();
        isBiddingFlag = true;
        System.out.println("Choosing item...\n"
                + itemList.getItems().get(itemChoice).toString());
        bid.setBidPrice(bidPrice);
        System.out.println("Choosing a bid price... $" + bidPrice);
        bid.setFrom(name);
        bid.setItemInfo(itemList.getItems().get(itemChoice));

        Socket auctionSocket = serverConnect(auctionIp, auctionPort);
        System.out.println("Sending bid to Auction House...\n");
        Bank.send(bid, auctionSocket);
        listen(auctionSocket);
        if (bid.getStatus().equals(BidStatus.REJECTED)) {
            System.out.println("Bid was rejected.\n");
        } else if (bid.getStatus().equals(BidStatus.ACCEPTED)) {
            System.out.println("Bid was accepted!");
            System.out.println("Waiting for outbid...");

            if (!listen(auctionSocket)) {
                System.out.println("Outbid in bidItem\n");
            }
        }
    }

    /*method to handle agent disconnect from bank*/
    public void exit() {
        if (isBiddingFlag) {
            System.out.println("Unable to disconnect with an open bid");
        } else {
            System.out.println("Disconnected");
            account.setDeleteFlag(true);
            Socket server = serverConnect(bankIp, bankPort);
            Bank.send(account, server);
            listen(server);
        }
    }
}
