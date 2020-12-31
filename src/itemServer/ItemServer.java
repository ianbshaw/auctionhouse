package itemServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/*class that handles item server*/
public class ItemServer {
    private static final ArrayList<Item> items = new ArrayList<>();

    public ItemServer() {}

    /*method to setup item server given a port, returns a serversocket*/
    private static ServerSocket setup(int port) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return serverSocket;
    }

    /*method to handle client requests from auction house*/
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

    /*method to send request to client*/
    public static void serverPort(Object o, Socket client) {
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

    /*method to create items upon auction house creation*/
    private static void initItems(String name) {
        Random r = new Random();
        int minItems = r.nextInt(4) + 3;
        for (int i = 0; i < minItems; ++i) {
            items.add( new Item(ItemTypes.values()[r.nextInt(ItemTypes.values().length)].getItemType(),
                            name, i, r.nextInt(10),0));
        }
        for (Item item : items) {
            System.out.println(item.toString());
        }
    }

    /*main method to act as controller for item server*/
    public static void main(String[] args) {
        int port = 4700;
        ServerSocket server = setup(port);

        Socket client;
        Object o = null;
        ObjectInputStream in = null;
        ItemRequest ir;

        /*listen for requests from auction house*/
        while (true) {
            System.out.println("Waiting for Auctions...");
            client = getNextClient(server);

            try {
                in = new ObjectInputStream(client.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            try {
                o = in.readObject();
            } catch (IOException | ClassNotFoundException ioe) {
                ioe.printStackTrace();
            }

            if (o instanceof ItemRequest) {
                ir = (ItemRequest) o;
                initItems(ir.getName());
                ir.setItems(items);
                serverPort(ir, client);
            }
        }
    }
}
