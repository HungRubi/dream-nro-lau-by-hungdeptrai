package real.item;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static real.item.ItemData.itemTemplates;
import static real.item.ItemTemplateDAO.insertList;
import server.DBService;

public class ItemShopDAO {

    public static Map loadItemShop() {
        Map map = new HashMap();
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from item_shop order by npc_id asc, tab asc, create_time desc");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ItemShop it = new ItemShop();
                it.npcId = rs.getInt(1);
                it.itemTemplate = ItemData.getTemplate(rs.getShort(2));
                it.typeShop = rs.getInt(3);
                it.tab = rs.getInt(4);
                it.tabName = rs.getString(5);
                it.itemNew = rs.getInt(6) == 1;
                it.isCaiTrang = CaiTrangData.getCaiTrangByTempID(rs.getShort(2)) != null;
                it.sell = rs.getInt(7) == 1;
                it.gold = rs.getInt(8);
                it.gem = rs.getInt(9);
                it.quantity = rs.getInt(10);
                it.itemOptions = ItemOptionShopDAO.getOption(it.npcId, it.itemTemplate.id);
                List<ItemShop> list = (List<ItemShop>) map.get(it.npcId);
                if (list == null) {
                    list = new ArrayList<ItemShop>();
                }
                list.add(it);
                map.put(it.npcId, list);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static ItemShop getByTemp(int tempId) {
        ItemShop it = new ItemShop();
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from item_shop where item_template_id = ?");
            ps.setInt(1, tempId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                it.npcId = rs.getInt(1);
                it.itemTemplate = ItemData.getTemplate(rs.getShort(2));
                it.typeShop = rs.getInt(3);
                it.tab = rs.getInt(4);
                it.tabName = rs.getString(5);
                it.itemNew = rs.getInt(6) == 1;
                it.gold = rs.getInt(8);
                it.gem = rs.getInt(9);
                it.quantity = rs.getInt(10);
                it.itemOptions = ItemOptionShopDAO.getOption(it.npcId, it.itemTemplate.id);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return it;
    }

}
