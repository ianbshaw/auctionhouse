package itemServer;

import java.io.Serializable;
import java.util.ArrayList;

/*class to represent a request for the list of items for sale by auction house*/
public class ItemListRequest implements Serializable {
    private ArrayList<ItemInfo> items = new ArrayList<>();

    public ArrayList<ItemInfo> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemInfo> items) {
        this.items = items;
    }
}
