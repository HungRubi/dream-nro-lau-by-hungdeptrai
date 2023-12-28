package real.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import real.pet.Pet;
import real.player.Player;
import server.DBService;

public class ItemDAO {

    public static int create(int tempId, ArrayList<ItemOption> itemOptions) {
        Connection conn = DBService.gI().getConnection();
        int itemId = -1;
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO item(temp_id,quantity) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, tempId);
            ps.setInt(2, 1);
            if (ps.executeUpdate() == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.first()) {
                    itemId = rs.getInt(1);
                    ps = conn.prepareStatement("INSERT INTO item_option(item_id,option_id,param) VALUES(?,?,?)");
                    conn.setAutoCommit(false);
                    for (ItemOption itemOption : itemOptions) {
                        ps.setInt(1, itemId);
                        ps.setInt(2, itemOption.optionTemplate.id);
                        ps.setInt(3, itemOption.param);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    conn.commit();
                }
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemId;
    }

    public static Item findById(int id) {
        Connection conn = DBService.gI().getConnection();
        Item item = null;
        try {
            PreparedStatement ps = conn.prepareStatement("select * from item where id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                item = new Item();
                item.quantity = rs.getInt(3);
                item.template = ItemData.getTemplate(rs.getShort(2));
            }
            ps = conn.prepareStatement("select * from item_option WHERE item_id=?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                ItemOption option = new ItemOption();
                option.optionTemplate = ItemData.getItemOptionTemplate(rs.getInt(2));
                option.param = rs.getShort(3);
                item.itemOptions.add(option);
            }
        } catch (Exception e) {
        }
        return item;
    }

    public static void loadPetItems(Pet pet) {
        try {

            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from pet_body left join item on pet_body.item_id = item.id where pet_id = ? order by slot");
            ps.setLong(1, pet.id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int slot = rs.getInt("slot");
                Item item = new Item();
                item.id = rs.getInt("item_id") == 0 ? -1 : rs.getInt("item_id");
                if (item.id != -1) {
                    item.template = ItemData.getTemplate(rs.getShort("temp_id"));
                    item.quantity = rs.getInt("quantity");
                    loadOptionsItem(item);
                }
                pet.inventory.itemsBody.add(slot, item);
            }
            ps.close();
        } catch (Exception e) {
        }
    }

    public static void loadPlayerItems(Player player) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBService.gI().getConnection();
            ps = conn.prepareStatement("SELECT * FROM player_body LEFT JOIN item ON player_body.item_id=item.id WHERE player_id=? ORDER BY slot");
            ps.setLong(1, player.id);
            rs = ps.executeQuery();
            while (rs.next()) {
                int slot = rs.getInt("slot");
                Item item = new Item();
                item.id = rs.getInt("item_id") == 0 ? -1 : rs.getInt("item_id");
                if (item.id != -1) {
                    item.template = ItemData.getTemplate(rs.getShort("temp_id"));
                    item.quantity = rs.getInt("quantity");
                    loadOptionsItem(item);
                }
                player.inventory.itemsBody.add(slot, item);
            }
            ps = conn.prepareStatement("SELECT * FROM player_bag LEFT JOIN item ON player_bag.item_id=item.id WHERE player_id=? ORDER BY slot");
            ps.setLong(1, player.id);
            rs = ps.executeQuery();
            while (rs.next()) {
                int slot = rs.getInt("slot");
                Item item = new Item();
                item.id = rs.getInt("item_id") == 0 ? -1 : rs.getInt("item_id");
                if (item.id != -1) {
                    item.template = ItemData.getTemplate(rs.getShort("temp_id"));
                    item.quantity = rs.getInt("quantity");
                    loadOptionsItem(item);
                }
                player.inventory.itemsBag.add(slot, item);
            }
            ps = conn.prepareStatement("SELECT * FROM player_box LEFT JOIN item ON player_box.item_id=item.id WHERE player_id=? ORDER BY slot");
            ps.setLong(1, player.id);
            rs = ps.executeQuery();
            while (rs.next()) {
                int slot = rs.getInt("slot");
                Item item = new Item();
                item.id = rs.getInt("item_id") == 0 ? -1 : rs.getInt("item_id");
                if (item.id != -1) {
                    item.template = ItemData.getTemplate(rs.getShort("temp_id"));
                    item.quantity = rs.getInt("quantity");
                    loadOptionsItem(item);
                }
                player.inventory.itemsBox.add(slot, item);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadOptionsItem(Item item) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBService.gI().getConnection();
            ps = conn.prepareStatement("SELECT * FROM item_option WHERE item_id=?");
            ps.setInt(1, item.id);
            rs = ps.executeQuery();
            while (rs.next()) {
                ItemOption option = new ItemOption();
                option.optionTemplate = ItemData.getItemOptionTemplate(rs.getInt("option_id"));
                option.param = rs.getShort("param");
                item.itemOptions.add(option);
            }
//            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateDB(Item item) {
        try {
            Connection conn = DBService.gI().getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM item_option WHERE item_id=?");
            ps.setInt(1, item.id);
            if (ps.executeUpdate() == 1) {
                //System.out.println("delete option item " + item.id);
            }
            ps = conn.prepareStatement("UPDATE item SET quantity=? WHERE id=?");
            ps.setInt(1, item.quantity);
            ps.setInt(2, item.id);
            if (ps.executeUpdate() == 1) {
                //System.out.println("delete item " + item.id);
            }
            ps = conn.prepareStatement("INSERT INTO item_option(item_id,option_id,param) VALUES(?,?,?)");
            conn.setAutoCommit(false);
            for (ItemOption itemOption : item.itemOptions) {
                ps.setInt(1, item.id);
                ps.setInt(2, itemOption.optionTemplate.id);
                ps.setInt(3, itemOption.param);
                ps.addBatch();
            }
            ps.executeBatch();
//            conn.commit();
//            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateItem(Item item) {
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("delete item_option where item_id = ?");
            ps.setInt(1, item.id);
            ps.executeUpdate();
            ps = con.prepareStatement("insert into item_option values(?,?,?)");
            for (ItemOption itemOption : item.itemOptions) {
                ps.setInt(1, item.id);
                ps.setInt(2, itemOption.optionTemplate.id);
                ps.setInt(3, itemOption.param);
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
        } catch (Exception e) {
        }
    }

    public static void loadItemBody(Player player) {
        Connection conn = DBService.gI().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM player_body LEFT JOIN item ON player_body.item_id=item.id WHERE player_id=? ORDER BY slot");
            ps.setLong(1, player.id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int slot = rs.getInt("slot");
                Item item = new Item();
                item.id = rs.getInt("item_id") == 0 ? -1 : rs.getInt("item_id");
                if (item.id != -1) {
                    item.template = ItemData.getTemplate(rs.getShort("temp_id"));
                    item.quantity = rs.getInt("quantity");
                    loadOptionsItem(item);
                }
                player.inventory.itemsBody.add(slot, item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
