package itemServer;

import java.io.Serializable;
import java.util.ArrayList;

/*class that represents a request for an item when auction house is created*/
public class ItemRequest implements Serializable {

    private ArrayList<Item> items = new ArrayList<>();
    private String name;

    public ItemRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }
}
