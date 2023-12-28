package tool;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import server.DBService;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN  💖
 *
 */
public class Tool {
    public static void main(String[] args) throws Exception{
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("C:\\Users\\adm\\Desktop\\item_template")));
        Connection con = DBService.gI().getConnection();
        PreparedStatement ps = con.prepareStatement("select * from item_template");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            dos.writeInt(rs.getInt(1));
            dos.writeUTF(rs.getString(4));
            dos.writeUTF(rs.getString(5));
            dos.writeInt(rs.getInt(6));
        }
        dos.flush();
        dos.close();
        con.close();
        
        System.out.println("done");

//        DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("C:\\Users\\adm\\Desktop\\item_option_template")));
//        Connection con = DBService.gI().getConnection();
//        PreparedStatement ps = con.prepareStatement("select * from item_option_template");
//        ResultSet rs = ps.executeQuery();
//        while(rs.next()){
//            dos.writeInt(rs.getInt(1));
//            dos.writeUTF(rs.getString(2));
//        }
//        dos.flush();
//        dos.close();
//        con.close();
//        
//        System.out.println("done");
    }
}
