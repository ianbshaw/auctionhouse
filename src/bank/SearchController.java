package bank;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

/*class that contains utilty function of sql db to ensure file and data stream*/
public class SearchController {

    private DatabaseController dbc;

    public SearchController() {
        dbc = checkDBExists();
        if (dbc == null) {
            System.out.println("Unable to open DB controller");
            System.exit(1);
        }
    }

    public ArrayList query(String sql) {
        return dbc.get(sql);
    }

    public DatabaseController checkDBExists() {
        try {
            File prop = new File("src/bank/accounts.db");
            if (!prop.exists()) {
                dbc = new DatabaseController("src/bank/accounts");
                return dbc;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            dbc = new DatabaseController("src/bank/accounts.db");
            return dbc;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
