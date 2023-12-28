package server;
//share by chibikun

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBService {

    private static DBService instance;
    Connection conn = null;

    public static DBService gI() {
        if (instance == null) {
            instance = new DBService();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                String url = "jdbc:mysql://127.0.0.1/nrohp?useUnicode=true&characterEncoding=utf-8";
                String user = "root";
                String password = "hungkaka1";
                conn = DriverManager.getConnection(url, user, password);
            }
        } catch (Exception e) {
        }
        return conn;
    }

    public static void main(String[] args) {
        Connection cn = DBService.gI().getConnection();
        if (cn != null) {
            System.out.println("ok");
        } else {
            System.out.println("lỗi");
        }
    }

    public static int resultSize(ResultSet rs) {
        int count = 0;
        try {
            while (rs.next()) {
                count++;
            }
            rs.beforeFirst();
        } catch (SQLException e) {
        }
        return count;
    }
}
