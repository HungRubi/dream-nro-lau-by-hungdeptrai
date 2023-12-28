package tool;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import real.item.ItemData;
import real.item.ItemOptionShopDAO;
import real.item.ItemOptionTemplateDAO;
import real.item.ItemShopDAO;
import real.item.ItemTemplate;
import real.item.ItemTemplateDAO;
import server.DBService;

public class Test {

    public static void main(String[] args) throws FileNotFoundException {
//        try {
//            int a = 5 / 0;
//        } catch (Exception e) {
//            StringWriter sw = new StringWriter();
//            PrintWriter pw = new PrintWriter(sw);
//            e.printStackTrace(pw);
//            String sStackTrace = sw.toString(); 
//            System.out.println("error"+sStackTrace);
//        }
//
//        if (true) {
//            return;
//        }

//        insertDataToDB();
//        if (true) {
//            return;
//        }
        try {
            ItemData.loadDataItems();
            List<Integer> itemTemplates = new ArrayList<>();
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("insert into item_shop values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
            int slot = 0;
//            int tab = 2;
            for (ItemTemplate it : ItemData.itemTemplates) {
                if (it.type == 5 && !it.name.equalsIgnoreCase("avatar")) {
                    if (con.prepareStatement("select * from item_shop where npc_id = 39 and item_template_id = " + it.id).executeQuery().next()) {
                        continue;
                    }
                    itemTemplates.add((int) it.id);
                    ps.setInt(1, 39);
                    ps.setInt(2, it.id);
                    ps.setInt(3, 0);
                    ps.setInt(4, 2);
                    ps.setString(5, "New tab");
                    ps.setInt(6, 1);
                    ps.setInt(7, 1);
                    ps.setInt(8, -1);
                    ps.setInt(9, -1);
                    ps.setInt(10, -1);
                    ps.setInt(11, -1);
                    ps.setInt(12, slot++);
                    ps.setInt(13, 1);
                    ps.addBatch();
//                    if(slot == 40){
//                        slot = 0;
//                        tab ++;
//                    }
                }
            }
            ps.executeBatch();

            ps = con.prepareStatement("insert into item_option_shop values (?,?,?,?)");
            for (Integer i : itemTemplates) {
                ps.setInt(1, 39);
                ps.setInt(2, i);
                ps.setInt(3, 73);
                ps.setInt(4, 0);
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
            con.close();
            System.out.println("done");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void insertDataToDB() throws FileNotFoundException {

    }

    public static String powerToString(long power) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat num = NumberFormat.getInstance(locale);
        num.setMaximumFractionDigits(1);
        if (power >= 1000000000) {
            return num.format((double) power / 1000000000) + " Tỷ";
        } else if (power >= 1000000) {
            return num.format((double) power / 1000000) + " Tr";
        } else if (power >= 1000) {
            return num.format((double) power / 1000) + " k";
        } else {
            return num.format(power);
        }
    }
}
