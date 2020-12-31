package bank;

import java.sql.*;
import java.util.ArrayList;

/*class to act as controller for sql db*/
public class DatabaseController {

    private Connection db = null;
    private final String filename;

    public DatabaseController(String filename) throws SQLException {
        this.filename = filename;
    }

    /*utility methods for sql lite*/

    private Connection connect() {
        Connection connection = null;

        try {
            DriverManager.registerDriver(new org.sqlite.JDBC());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String url = "jdbc:sqlite:" + filename;
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.db = connection;
        return connection;
    }

    public void insert(String sql) {

        try {
            db = connect();
            db.setAutoCommit(false);
            Statement stmt = db.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            db.commit();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String sql) {
        PreparedStatement pst;
        db = connect();
        try {
            pst = db.prepareStatement(sql);
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*method to return list of items and auction houses*/
    public ArrayList<ArrayList<String>> get(String sql) {
        ArrayList<ArrayList<String>> a = new ArrayList<>();

        PreparedStatement pst;
        db = connect();
        try {
            pst = db.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ArrayList<String> temp = new ArrayList<>();
                for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
                    temp.add(rs.getString(i));
                }
                a.add(temp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return a;
    }
}
