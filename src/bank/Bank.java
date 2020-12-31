package bank;

import ah.AuctionHouseList;
import ah.NewAuctionHouse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

/*class that handles bank server logic as well as the controller*/
public class Bank {
    private static final int port = 4500;
    private static int accountID = 0;
    private static DatabaseController dbc = null;

    public Bank() {}

    /*method to setup bank server and returns socket*/
    private static ServerSocket setup() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return serverSocket;
    }

    /*method handle incoming client connections from auction house and agents*/
    private static Socket getNextClient(ServerSocket server) {
        Socket client = null;
        try {
            client = server.accept();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return client;
    }

    /*method to send request to target client*/
    public static void send(Object o, Socket client) {
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            out.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*main method to act as controller for server application*/
    public static void main(String[] Args) {
        ServerSocket server = setup();
        Socket client;
        Object o = null;
        ObjectInputStream in = null;
        AuctionHouseList auctionHouseList;
        Account account;
        NewAuctionHouse auctionHouseInfo;
        LockFundsRequest lfr;
        SearchController searchController = new SearchController();

        /*flush any stale data from db*/
        try {
            dbc = new DatabaseController("src/bank/accounts.db");
            dbc.delete("DELETE FROM auctions");
            dbc.delete("DELETE FROM accounts");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*listen for requests*/
        while (true) {
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

            if (o instanceof Account) {
                account = (Account) o;
                if(account.getBalanceFlag()){
                    account.setBalance(getBalance(account.getName()));
                    send(account, client);
                } else if(account.isDeleteFlag()){
                    deleteAccount(account.getName());
                    if(account.getName().startsWith("au")){
                        deleteAuction(account.getName());
                    }
                } else{
                    insertAccount(account.getName(), account.getBalance());
                    account.setAccount_number(accountID);
                    send(account, client);
                }
            } else if (o instanceof AuctionHouseList) {
                auctionHouseList = (AuctionHouseList) o;
                auctionHouseList.setArrayList(searchController.query(auctionHouseList.getSql()));
                send(auctionHouseList, client);
            } else if (o instanceof NewAuctionHouse) {
                auctionHouseInfo = (NewAuctionHouse) o;
                insertAuctionHouse(auctionHouseInfo.getName(), auctionHouseInfo.getIp(), auctionHouseInfo.getPort());
            } else if (o instanceof LockFundsRequest) {
                lfr = (LockFundsRequest) o;
                if (lfr.isUnlockFlag()) {
                    if(transfer(lfr.getQuantity(), lfr.getFrom(), lfr.getTo())){
                        lfr.setInsufficientFunds(true);
                    }
                } else {
                    if(transfer(lfr.getQuantity(), lfr.getTo(),
                            lfr.getFrom())){
                        lfr.setInsufficientFunds(true);
                    }
                }
                send(lfr, client);
            }
        }
    }

    /*method to add account to database*/
    private static void insertAccount(String name, int initBalance) {
        String sql =
                "INSERT INTO accounts "
                        + "(name, accountNumber, balance)"
                        + "Values ('"
                        + name
                        + "','"
                        + accountID
                        + "','"
                        + initBalance
                        + "');";
        dbc.insert(sql);
        accountID++;
    }

    /*method to add auction house to database*/
    private static void insertAuctionHouse(String name, String ip, int port) {
        String sql =
                "INSERT INTO auctions "
                        + "(Name, IP, Port)"
                        + "Values ('"
                        + name
                        + "','"
                        + ip
                        + "','"
                        + port
                        + "');";

        System.out.println("Inserting Auction ('" + name + "', '" + ip + "', '" + port + ")");
        dbc.insert(sql);
        accountID++;
    }

    /*method to handle sql logic for a movement of money between accounts*/
    public static boolean transfer(int quantity, String from, String to) {

        if (checkAvailableFunds(quantity, from)) {
            String sql =
                    "UPDATE accounts "
                            + "SET balance = "
                            + quantity
                            + " WHERE name = '"
                            + to
                            + "';";
            dbc.insert(sql);
            sql =
                    "UPDATE accounts "
                            + "SET balance = balance -"
                            + quantity
                            + " WHERE name = '"
                            + from
                            + "';";

            dbc.insert(sql);
            return false;
        }
        return true;
    }

    /*method to check if agent has available funds from sql db for bid request*/
    public static boolean checkAvailableFunds(int quantity, String from) {
        String sql = "SELECT balance FROM accounts WHERE name = '" + from + "';";
        ArrayList<ArrayList<String>> balance = dbc.get(sql);
        String balanceValue = "";
        for (ArrayList<String> arr : balance) {
            for (String s : arr) {
                balanceValue = s;
            }
        }
        System.out.println(balanceValue + " >= " + quantity);
        return Integer.parseInt(balanceValue) >= quantity;
    }

    /*method to handle deletion of account from sql db*/
    public static void deleteAccount(String name) {
        String sql = "DELETE FROM accounts WHERE name='" + name + "';";
        dbc.delete(sql);
    }

    /*method to handle request for balance from sql db*/
    public static int getBalance(String name){
        String sql = "SELECT balance FROM accounts WHERE name = '" + name + "';";
        ArrayList<ArrayList<String>> temp = dbc.get(sql);
        return Integer.parseInt(temp.get(0).get(0));
    }

    /*method to handle deletion of auction house from sql db*/
    public static void deleteAuction(String name) {
        String sql = "DELETE FROM auctions WHERE name='" + name + "';";
        dbc.delete(sql);
    }
}
