package real.item;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import server.DBService;

public class ItemOptionShopDAO {

    public static void insertList(ArrayList<ItemOptionShop> list) {
        System.out.println(list.size());
        int[] success = new int[0];
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("insert into item_option_shop values (?,?,?,?)");
            for (ItemOptionShop it : list) {
                ps.setInt(1, it.npcId);
                ps.setInt(2, it.itemTemplateId);
                ps.setInt(3, it.optionId);
                ps.setInt(4, it.param);
                ps.addBatch();
            }
            success = ps.executeBatch();
            System.out.println("success: " + success.length);
        } catch (Exception e) {
            System.out.println("fails: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
    }

    public static List<ItemOptionShop> getOption(int npc, int itemTemplate) {
        List<ItemOptionShop> list = new ArrayList<>();
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from item_option_shop where npc_id = ? and item_template_id = ?");
            ps.setInt(1, npc);
            ps.setInt(2, itemTemplate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ItemOptionShop(npc, itemTemplate, rs.getInt(3), rs.getInt(4)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
