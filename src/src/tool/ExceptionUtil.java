package tool;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import server.DBService;

public class ExceptionUtil {

    public static void logException(Exception ex) {
        try {
            String message = ex.getMessage();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String detail = sw.toString();
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("insert into exception(message,detail) values(?,?)");
            ps.setString(1, message);
            ps.setString(2, detail);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ex.printStackTrace();
    }
}
