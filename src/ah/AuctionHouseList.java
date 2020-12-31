package ah;

import java.io.Serializable;
import java.util.ArrayList;

/*method to handle auction house list request from client*/
public class AuctionHouseList implements Serializable {
    String sql = "";

    ArrayList<ArrayList<String>> arrayList = null;

    public AuctionHouseList() {
        setAccountSql();
    }

    public ArrayList<ArrayList<String>> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<ArrayList<String>> arrayList) {
        this.arrayList = arrayList;
    }

    public void setAccountSql() {
        this.sql = "SELECT * FROM auctions";
    }

    public String getSql() {
        return sql;
    }
}
