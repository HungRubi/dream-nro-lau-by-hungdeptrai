package real.item;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import server.DBService;
import server.Util;

public class ItemOptionTemplateDAO {

    public static List<ItemOptionTemplate> getAll() {
        List<ItemOptionTemplate> list = new ArrayList<>();
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from item_option_template");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ItemOptionTemplate(rs.getInt(1), rs.getString(2), rs.getInt(3)));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            Util.log("Tải item option template thất bại!");
            e.printStackTrace();
            System.exit(0);
        }
        return list;
    }

    public static void insertList(ArrayList<ItemOptionTemplate> list) {
        System.out.println(list.size());
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("insert into item_option_template() values (?,?,?)");
            for (ItemOptionTemplate iot : list) {
                ps.setInt(1, iot.id);
                ps.setString(2, iot.name);
                ps.setInt(3, iot.type);
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
            System.out.println("done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readfile() throws FileNotFoundException{
        ArrayList<ItemOptionTemplate> list = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\adm\\Desktop\\nro qltk java by girlkun\\data x1\\itemoptions.txt"));
        try {
            while (true) {
                ItemOptionTemplate it = new ItemOptionTemplate();
                for (int i = 0; i < 3; i++) {
                    if (i == 0) {
                        it.id = Integer.parseInt(br.readLine().replaceAll("id: ", ""));
                    } else if (i == 1) {
                        it.name = br.readLine().replaceAll("name: ", "");
                    } else {
                        br.readLine();
                        list.add(it);
                    }
                }
            }
        } catch (Exception e) {
        }
        insertList(list);
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        readfile();
    }
}
