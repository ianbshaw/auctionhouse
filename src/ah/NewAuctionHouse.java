package ah;

import java.io.Serializable;

/*class that handles a request to save server information for auction house*/
public class NewAuctionHouse implements Serializable {
    private final String name;
    private final String ip;
    private final int port;

    public NewAuctionHouse(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
