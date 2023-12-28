package tool;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.DBService;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN  💖
 *
 */
public class Test2 {
    
    private static BufferedWriter bw;
    static{
        try {
            bw = new BufferedWriter(new FileWriter("C:\\Users\\adm\\Desktop\\data.txt",true));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM item_template WHERE TYPE = 5 AND part != -1");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int id = rs.getInt(1);
                ps = con.prepareStatement("select * from item_shop where item_template_id = ?");
                ps.setInt(1, id);
                ResultSet rss = ps.executeQuery();
                if(rss.next()){
                    writeFile(id, rss.getInt(8), rss.getInt(9), rss.getInt(10), rs.getString(4), rs.getString(5));
                } else {
                    writeFile(id, -1, -1, -1, rs.getString(4), rs.getString(5));
                }
            }
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void writeFile(int id, int head, int body, int leg, String name,String moTa){
        System.out.println(id + " " + head + " " + body + " " + leg);
        try {
            bw.write(id + " " + head + " " + body + " " + leg + " (" + name + " - " +moTa +  ")\n");
            bw.flush();
        } catch (Exception e) {
        }
    }
}
