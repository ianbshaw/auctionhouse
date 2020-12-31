package ah;

import agent.Agent;
import agent.BidRequest;
import agent.BidStatus;
import bank.Account;
import bank.LockFundsRequest;
import itemServer.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;
import static itemServer.ItemServer.serverPort;

/*class to handle logic for auction house server*/
public class AuctionHouse implements Runnable {
    private static String bankIp;
    private static final int bankPort = 4500;
    private static String name;

    private static Socket prevBidder;
    private static ArrayList<Item> items = new ArrayList<>();
    private static Account account = null;

    private static boolean insufficientFunds = false;
    private final ScheduledExecutorService ses = Executors.newScheduledThreadPool(5);
   // private ScheduledFuture scheduledFuture;

    /*constructor that creates auction house account and saves server info*/
    public AuctionHouse() {
        Random r = new Random();
        int id = r.nextInt(6000);
        try {

            InetAddress ipv4 = InetAddress.getLocalHost();
            String ip = ipv4.toString();
            ip = ip.substring(8);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        name = "ah" + id;
        createAccount();
        configInfo();
    }

    /*method to create auction house server with a port and returns socket*/
    private static ServerSocket setup(int port) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {

            System.exit(1);
        }
        return serverSocket;
    }

    /*method to send request to bank server*/
    public static void sendBankOneWay(Object o) {
        Socket server = Agent.serverConnect(bankIp, bankPort);
        serverPort(o, server);

        try {
            server.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /*method to send request to item server*/
    public static void sendOneWay(Object o, Socket client) {
        serverPort(o, client);
    }

    public static void send(Object o, Socket server) {
        serverPort(o, server);
        listen(server);
    }

    /*method to handle requests from agent and bank*/
    public static void listen(Socket server) {
        ObjectInputStream in = null;
        Object response;
        try {
            in = new ObjectInputStream(server.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert in != null;
            response = in.readObject();

            if (response instanceof Account) {
                account = (Account) response;
            }else if( response instanceof ItemRequest){
                ItemRequest ir = (ItemRequest) response;
                items = ir.getItems();
            }else if( response instanceof LockFundsRequest){
                LockFundsRequest lfr = (LockFundsRequest) response;
                if(lfr.isInsufficientFunds()){
                    insufficientFunds = true;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /*method to add items to auction house*/
    private static void initItems() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setName(name);
        int itemPort = 4700;
        Socket server = Agent.serverConnect(bankIp, itemPort);
        send(itemRequest, server);
    }

    /*method to create auction house account with bank*/
    public static void createAccount() {
        account = new Account(name, 0);
        System.out.println("Server IP of Bank?");
        Scanner scanner = new Scanner(System.in);
        bankIp = scanner.nextLine();
        Socket server = Agent.serverConnect(bankIp, bankPort);
        send(account, server);
    }

    /*method to handle bid win*/
    public static void win(Item biddingItem) {
        biddingItem.setStatus(ItemStatus.SOLD);
        sendOneWay(biddingItem, prevBidder);
        items.remove(biddingItem);
        initItems();
    }

    /*method to register auction house server information*/
    public void configInfo() {
        int port = 4800;
        NewAuctionHouse auctionHouseInfo = new NewAuctionHouse(name, bankIp, port);
        sendBankOneWay(auctionHouseInfo);
    }

    /*method to handle bid requests, sends requests to bank and modifies bid status*/
    public void processBid(BidRequest bid, Socket client) {
        String itemName = bid.getItemInfo().getName();
        Item biddingItem = null;
        for (Item item : items) {
            if (item.getName().equals(itemName)) {
                biddingItem = item;
                break;
            }
        }
        int bidPrice = bid.getBidPrice();
        if (bidPrice > bid.getItemInfo().getMinBid() &&
                bidPrice > bid.getItemInfo().getCurrentBid()) {
            LockFundsRequest lfr = new LockFundsRequest();
            lfr.setFrom(name);
            lfr.setQuantity(bidPrice);
            lfr.setTo(bid.getFrom());

            Socket server = Agent.serverConnect(bankIp, bankPort);
            send(lfr, server);
            if(!insufficientFunds){
                sendOneWay(bid, client);
                bid.setStatus(BidStatus.ACCEPTED);
                biddingItem.setCurrentBid(bidPrice);

                if (!biddingItem.getPrevBidderName().equals("")) {
                    bid.setStatus(BidStatus.OUTBID);
                    sendOneWay(bid, prevBidder);
                    lfr.setUnlockFlag(true);
                    sendBankOneWay(lfr);
                  //  scheduledFuture.cancel(true);
                }

                biddingItem.setPrevBidderName(bid.getFrom());
                prevBidder = client;
                /*scheduledFuture =
                        ses.schedule(
                                (Callable) () -> {
                                    win(biddingItem);
                                    return "Called!";
                                },
                                30,
                                TimeUnit.SECONDS);*/
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (bid.getStatus() != BidStatus.OUTBID)
                            bid.setStatus(BidStatus.WIN);
                    }
                }, 10);
            }
        } else {
            bid.setStatus(BidStatus.REJECTED);
            sendOneWay(bid, client);
            System.exit(0);
        }
    }

    /*method to handle server acceptance from agents*/
    public static Socket getNextClient(ServerSocket server) {
        Socket client = null;
        try {
            client = server.accept();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return client;
    }

    /*runnable method for thread runtime*/
    public void run() {
        ServerSocket server = setup(4800);

        Socket client;
        Object o = null;
        ObjectInputStream in = null;
        ItemListRequest itemList;
        BidRequest bid;

        initItems();

        while (true) {
            /*handle client requests*/
            client = getNextClient(server);

            try {
                in = new ObjectInputStream(client.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            try {
                o = in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (o instanceof BidRequest) {
                bid = (BidRequest) o;
                processBid(bid, client);

            } else if (o instanceof ItemListRequest) {
                itemList = (ItemListRequest) o;

                ArrayList<ItemInfo> itemsList = new ArrayList<>();
                for (Item item : items) {
                    itemsList.add(
                            new ItemInfo(
                                    item.getName(),
                                    item.getDesc(),
                                    item.getMinBid(),
                                    item.getCurrentBid()));
                }
                itemList.setItems(itemsList);
                sendOneWay(itemList, client);
            }
        }
    }
}
