package real.player;

import real.pet.Pet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import real.clan.ClanManager;
import real.item.Item;
import real.item.ItemDAO;
import real.item.ItemData;
import real.item.ItemOption;
import real.item.ItemTime;
import real.magictree.MagicTreeDAO;
import real.map.Map;
import real.map.MapManager;
import real.pet.PetDAO;
import real.skill.Skill;
import real.skill.SkillData;
import real.skill.SkillShortCutDAO;
import real.skill.SkillUtil;
import server.DBService;
import server.Service;
import server.Util;

public class PlayerDAO {

    public static void AddItem_Bag_Default(Player player, short idItem) {
        Item item = new Item();
        item.template = ItemData.getTemplate(idItem);
        item.content = item.getContent();
        item.quantity = 1;
//        item.itemOptions = item.template.options;
        item.itemOptions.add(new ItemOption(73, (short) -1));
        item.id = ItemDAO.create(item.template.id, item.itemOptions);
        player.inventory.addItemBag(item);
        player.inventory.sendItemBags();
    }

    public static boolean create(int userId, String name, int gender, int hair) {
        String CREATE_PLAYER = "INSERT INTO player(account_id,name,power,vang,luong,luong_khoa,gender,head,where_id,where_x,where_y) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        String CREATE_PLAYER_POINT = "INSERT INTO player_point(player_id,hp_goc, mp_goc,hp, mp,dam_goc,def_goc,crit_goc,tiem_nang) VALUES (?,?,?,?,?,?,?,?,?)";
        String CREATE_PLAYER_BODY = "INSERT INTO player_body(player_id,item_id,slot) VALUES (?,?,?)";
        String CREATE_PLAYER_BAG = "INSERT INTO player_bag(player_id,item_id,slot) VALUES (?,?,?)";
        String CREATE_PLAYER_BOX = "INSERT INTO player_box(player_id,item_id,slot) VALUES (?,?,?)";
        String CREATE_PLAYER_SKILL = "INSERT INTO player_skill(player_id,temp_id,level) VALUES (?,?,?)";
        boolean check = false;
        Connection conn = DBService.gI().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(CREATE_PLAYER, Statement.RETURN_GENERATED_KEYS);
            conn.setAutoCommit(false);
            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setLong(3, 1200);
            ps.setInt(4, 2000000000);
            ps.setInt(5, 100000);
            ps.setInt(6, 0);
            ps.setInt(7, gender);
            ps.setInt(8, hair);
            ps.setInt(9, gender + 21);
            ps.setInt(10, 180);
            ps.setInt(11, 384);
            if (ps.executeUpdate() == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.first()) {
                    int playerId = rs.getInt(1);
                    ps = conn.prepareStatement(CREATE_PLAYER_POINT);
                    ps.setInt(1, playerId);
                    switch (gender) {
                        case 0:
                            ps.setInt(2, 100);
                            ps.setInt(3, 100);
                            ps.setInt(4, 100);
                            ps.setInt(5, 100);
                            ps.setInt(6, 15);
                            break;
                        case 1:
                            ps.setInt(2, 100);
                            ps.setInt(3, 100);
                            ps.setInt(4, 100);
                            ps.setInt(5, 100);
                            ps.setInt(6, 15);
                            break;
                        case 2:
                            ps.setInt(2, 100);
                            ps.setInt(3, 100);
                            ps.setInt(4, 100);
                            ps.setInt(5, 100);
                            ps.setInt(6, 15);
                            break;
                    }
                    ps.setInt(7, 0);
                    ps.setInt(8, 0);
                    ps.setLong(9, 1200);
                    ps.executeUpdate();
                    ps = conn.prepareStatement(CREATE_PLAYER_BAG);
                    for (int i = 0; i < 20; i++) {
                        ps.setInt(1, playerId);
                        ps.setObject(2, null);
                        ps.setInt(3, i);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    ps = conn.prepareStatement(CREATE_PLAYER_BOX);
                    for (int i = 0; i < 20; i++) {
                        ps.setInt(1, playerId);
                        ps.setObject(2, null);
                        ps.setInt(3, i);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    ps = conn.prepareStatement(CREATE_PLAYER_BODY);
                    for (int i = 0; i < 7; i++) {
                        ps.setInt(1, playerId);
                        ps.setObject(2, null);
                        ps.setInt(3, i);
                        ps.addBatch();
                    }
                    ps.executeBatch();

                    ps = conn.prepareStatement(CREATE_PLAYER_SKILL);
                    int[] phai = null;
                    int[] traidat = {0, 1, 6, 9, 10, 20, 22, 19};
                    int[] namec = {2, 3, 7, 11, 12, 17, 18, 19};
                    int[] xayda = {4, 5, 8, 13, 14, 21, 23, 19};

                    if (gender == 0) {
                        phai = traidat;
                    } else if (gender == 1) {
                        phai = namec;
                    } else {
                        phai = xayda;
                    }

                    for (int i = 0; i < 8; i++) {
                        int level = 0;
                        if (i == 0) {
                            level = 1;
                        }
                        ps.setInt(1, playerId);
                        ps.setInt(2, phai[i]);
                        ps.setInt(3, level);
                        ps.addBatch();
                    }
                    ps.executeBatch();

                    MagicTreeDAO.create(playerId);
                    SkillShortCutDAO.create(playerId, gender);
                    check = true;
                }
            }

            conn.commit();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return check;
    }

    public static Player load(int userId) {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Player player = null;
        try {
            conn = DBService.gI().getConnection();
            ps = conn.prepareStatement("SELECT * FROM player WHERE account_id=? LIMIT 1");
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.first()) {
                player = new Player();
                player.id = rs.getInt("id");
                player.taskId = rs.getByte("task_id");
                player.name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");
                player.point.power = rs.getLong("power");
                player.inventory.gold = rs.getInt("vang");
                player.inventory.gem = rs.getInt("luong");
                player.inventory.ruby = rs.getInt("luong_khoa");
                player.x = rs.getShort("where_x");
                player.y = rs.getShort("where_y");
                if (rs.getInt("clan_id") != -1) {
                    player.clan = ClanManager.gI().getClanById(rs.getInt("clan_id"));
                }
                JSONArray jar = (JSONArray) JSONValue.parse(rs.getString("itemTime"));
                try {
                    if (jar != null) {
                        for (int i = 0; i < jar.size(); i++) {
                            ItemTime itemTime = ItemTime.fromJSONObject((JSONObject) jar.get(i));
                            player.inventory.addItemTime(itemTime);
                        }
                    }
                } catch (Exception e) {

                }
                List<Map> maps = MapManager.gI().getMapById(rs.getInt("where_id"));
                Map map = null;
                for (Map m : maps) {
                    if (m.getPlayers().size() <= 7) {
                        map = m;
                        break;
                    }
                }
                player.map = map;
            }
            ps = conn.prepareStatement("SELECT * FROM player_point WHERE player_id=? LIMIT 1");
            ps.setLong(1, player.id);
            rs = ps.executeQuery();
            if (rs.first()) {
                player.point.hpGoc = rs.getInt("hp_goc");
                player.point.mpGoc = rs.getInt("mp_goc");
                player.point.dameGoc = rs.getInt("dam_goc");
                player.point.defGoc = rs.getShort("def_goc");
                player.point.critGoc = rs.getByte("crit_goc");
                player.point.tiemNang = rs.getLong("tiem_nang");
                player.point.limitPower = rs.getByte("limit_power");
                player.point.hp = (rs.getInt("hp") == 0 ? 1 : rs.getInt("hp"));
                player.point.mp = (rs.getInt("mp") == 0 ? 1 : rs.getInt("mp"));
            }
            rs.close();
            ps.close();
            ps = conn.prepareStatement("SELECT * FROM player_skill WHERE player_id=?");
            ps.setLong(1, player.id);
            rs = ps.executeQuery();
            while (rs.next()) {
                int tempId = rs.getInt("temp_id");
                int level = rs.getInt("level");
                try {
                    Skill skill = SkillUtil.createSkill(tempId, level);
                    player.playerSkill.skills.add(skill);
                } catch (Exception e) {

                }
            }
            player.loadListPlayer();
            MagicTreeDAO.loadMagicTree(player);
            SkillShortCutDAO.load(player);
            PetDAO.loadForPlayer(player);
//            player.map.getPlayers().add(player);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return player;
    }

    public static Player loadbyid(int _userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Player player = null;
        try {
            conn = DBService.gI().getConnection();
            ps = conn.prepareStatement("SELECT * FROM player WHERE id=? LIMIT 1");
            ps.setInt(1, _userId);
            rs = ps.executeQuery();
            if (rs.first()) {
                player = new Player();
                player.id = rs.getInt("id");
                player.taskId = rs.getByte("task_id");
                player.name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");
                player.point.power = rs.getLong("power");
                player.inventory.gold = rs.getInt("vang");
                player.inventory.gem = rs.getInt("luong");
                player.inventory.ruby = rs.getInt("luong_khoa");
                player.x = rs.getShort("where_x");
                player.y = rs.getShort("where_y");
                if (rs.getInt("clan_id") != -1) {
                    player.clan = ClanManager.gI().getClanById(rs.getInt("clan_id"));
                }
                
                JSONArray jar = (JSONArray) JSONValue.parse(rs.getString("itemTime"));
                try {
                    if (jar != null) {
                        for (int i = 0; i < jar.size(); i++) {
                            ItemTime itemTime = ItemTime.fromJSONObject((JSONObject) jar.get(i));
                            player.inventory.addItemTime(itemTime);
                        }
                    }
                } catch (Exception e) {

                }

                List<Map> maps = MapManager.gI().getMapById(rs.getByte("where_id"));
                Map map = null;
                for (Map m : maps) {
                    if (m.getPlayers().size() <= 7) {
                        map = m;
                        break;
                    }
                }
                player.map = map;
            }
            ps = conn.prepareStatement("SELECT * FROM player_point WHERE player_id=? LIMIT 1");
            ps.setLong(1, player.id);
            rs = ps.executeQuery();
            if (rs.first()) {
                player.point.hpGoc = rs.getInt("hp_goc");
                player.point.mpGoc = rs.getInt("mp_goc");
                player.point.dameGoc = rs.getInt("dam_goc");
                player.point.defGoc = rs.getShort("def_goc");
                player.point.critGoc = rs.getByte("crit_goc");
                player.point.tiemNang = rs.getLong("tiem_nang");
                player.point.limitPower = rs.getByte("limit_power");
                player.point.hp = rs.getInt("hp");
                player.point.mp = rs.getInt("mp");
            }
            ps = conn.prepareStatement("SELECT * FROM player_skill WHERE player_id=?");
            ps.setLong(1, player.id);
            rs = ps.executeQuery();
            while (rs.next()) {
                int tempId = rs.getInt("temp_id");
                int level = rs.getInt("level");

                Skill skill = SkillUtil.createSkill(tempId, level);
                if (level == 0) {
                    skill.point = 0;
                    skill.damage = 100;
                }
                player.playerSkill.skills.add(skill);
            }

            player.loadListPlayer();
            MagicTreeDAO.loadMagicTree(player);
            SkillShortCutDAO.load(player);
            PetDAO.loadForPlayer(player);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return player;
    }

    public static Player getInfobyID(int _userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Player player = null;
        try {
            if (PlayerManger.gI().getPlayerByID(_userId) != null) {
                return PlayerManger.gI().getPlayerByID(_userId);
            }
            conn = DBService.gI().getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement("SELECT name.player, power, gender, head  FROM player, player_info WHERE id = player_id AND player_id = ? LIMIT 1");
            ps.setInt(1, _userId);
            rs = ps.executeQuery();
            if (rs.first()) {
                player = new Player();
                player.id = _userId;
                player.name = rs.getString(1);
                player.point.power = rs.getLong(2);
                player.gender = rs.getByte(3);
                player.head = rs.getByte(4);
            }
            conn.commit();
            rs.close();
            ps.close();
            conn.close();
            ItemDAO.loadItemBody(player);
        } catch (Exception e) {

        }
        return player;
    }

    public static void updateDB(Player player) {
        String UPDATE_PLAYER = "UPDATE player SET power=?,vang=?,luong=?,luong_khoa=?,clan_id=?,task_id=?,head=?,where_id=?,where_x=?,where_y=?,last_logout_time=?,itemTime=?  WHERE id=?";
        String UPDATE_PLAYER_POINT = "UPDATE player_point SET hp_goc=?,mp_goc=?,dam_goc=?,def_goc=?,crit_goc=?,tiem_nang=?, hp=?, mp=? WHERE player_id=?";
        //String UPDATE_INFO_CLAN = "UPDATE clan_member SET head=?,body=?,leg=? WHERE clan_id=? AND player_id=?";
        String UPDATE_PLAYER_BODY = "UPDATE player_body SET item_id=? WHERE player_id=? AND slot=?";
        String UPDATE_PLAYER_BAG = "UPDATE player_bag SET item_id=? WHERE player_id=? AND slot=?";
        String UPDATE_PLAYER_BOX = "UPDATE player_box SET item_id=? WHERE player_id=? AND slot=?";
        Connection conn;
        PreparedStatement ps;
        try {
            conn = DBService.gI().getConnection();
            ps = conn.prepareStatement(UPDATE_PLAYER);
            ps.setLong(1, player.point.power);
            ps.setInt(2, player.inventory.gold);
            ps.setInt(3, player.inventory.gem);
            ps.setInt(4, player.inventory.ruby);
            if (player.clan != null) {
                ps.setInt(5, player.clan.id);
            } else {
                ps.setInt(5, -1);
            }
            ps.setInt(6, player.taskId);
            ps.setInt(7, player.head);
            ps.setInt(8, player.map.id);
            ps.setInt(9, player.x);
            ps.setInt(10, player.y);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(11, timestamp);
            JSONArray jarr = new JSONArray();
            for (ItemTime effect : player.inventory.getvItemTime()) {
//                    jarr.add(effect.toJSONObject());
            }
            ps.setString(12, jarr.toJSONString());

            ps.setLong(13, player.id);
            if (ps.executeUpdate() == 1) {
                //Util.log("Update player info " + player.id + " on data base");
            }
            ps = conn.prepareStatement(UPDATE_PLAYER_POINT);
            ps.setInt(1, player.point.hpGoc);
            ps.setInt(2, player.point.mpGoc);
            ps.setInt(3, player.point.dameGoc);
            ps.setInt(4, player.point.defGoc);
            ps.setInt(5, player.point.critGoc);
            ps.setLong(6, player.point.tiemNang);
            ps.setInt(7, player.point.hp);
            ps.setInt(8, player.point.mp);
            ps.setLong(9, player.id);
            if (ps.executeUpdate() == 1) {
                //Util.log("Update player info " + player.id + " on data base");
            }
            //------------------------------------------------------------------

            conn.setAutoCommit(false);
            ps = conn.prepareStatement(UPDATE_PLAYER_BODY);
            for (int i = 0; i < player.inventory.itemsBody.size(); i++) {
                Item item = player.inventory.itemsBody.get(i);
                ps.setObject(1, item.id != -1 ? item.id : null);
                ps.setLong(2, player.id);
                ps.setInt(3, i);
                ps.addBatch();
                if (item.id != -1) {
                    ItemDAO.updateDB(item);
                }
            }
            ps.executeBatch();
            //------------------------------------------------------------------
            ps = conn.prepareStatement(UPDATE_PLAYER_BAG);
            for (int i = 0; i < player.inventory.itemsBag.size(); i++) {
                Item item = player.inventory.itemsBag.get(i);
                ps.setObject(1, item.id != -1 ? item.id : null);
                ps.setLong(2, player.id);
                ps.setInt(3, i);
                ps.addBatch();
                if (item.id != -1) {
                    ItemDAO.updateDB(item);
                }
            }
            ps.executeBatch();
            //------------------------------------------------------------------
            ps = conn.prepareStatement(UPDATE_PLAYER_BOX);
            for (int i = 0; i < player.inventory.itemsBox.size(); i++) {
                Item item = player.inventory.itemsBox.get(i);
                ps.setObject(1, item.id != -1 ? item.id : null);
                ps.setLong(2, player.id);
                ps.setInt(3, i);
                ps.addBatch();
                if (item.id != -1) {
                    ItemDAO.updateDB(item);
                }
            }
            ps.executeBatch();
            //------------------------------------------------------------------
            ps = conn.prepareStatement("delete from player_skill where player_id = ?");
            ps.setInt(1, (int) player.id);
            ps.executeUpdate();

            ps = conn.prepareStatement("insert into player_skill (player_id,temp_id,level) values(?,?,?)");
            for (Skill skill : player.playerSkill.skills) {
                ps.setInt(1, (int) player.id);
                ps.setInt(2, skill.template.id);
                ps.setInt(3, skill.point);
                ps.addBatch();
            }
            ps.executeBatch();

            //------------------------------------------------------------------
            ps = conn.prepareStatement("delete from friend where player_id = ?");
            ps.setInt(1, (int) player.id);
            ps.executeUpdate();

            ps = conn.prepareStatement("insert into friend values(?,?)");
            for (Player friend : player.listPlayer.getFriends()) {
                ps.setInt(1, (int) player.id);
                ps.setInt(2, (int) friend.id);
                ps.addBatch();
            }
            ps.executeBatch();
            //------------------------------------------------------------------

            ps = conn.prepareStatement("SELECT * FROM player_info WHERE player_id=? LIMIT 1");
            ps.setInt(1, (int) player.id);
            ResultSet rss = ps.executeQuery();

            if (rss.first()) {
                ps = conn.prepareStatement("UPDATE player_info SET head=?, body=?, leg=? WHERE player_id=?");
                ps.setInt(1, (int) player.getHead());
                ps.setInt(2, (int) player.getBody());
                ps.setInt(3, (int) player.getLeg());
                ps.setInt(4, (int) player.id);
                ps.executeUpdate();
                //Util.log("Update player_info " + player.id + " on data base");
            } else {
                ps = conn.prepareStatement("insert into player_info values(?,?,?,?)");
                ps.setInt(1, (int) player.id);
                ps.setInt(2, (int) player.getHead());
                ps.setInt(3, (int) player.getBody());
                ps.setInt(4, (int) player.getLeg());
                ps.executeUpdate();
                // Util.log("Insert player_info " + player.id + " on data base");
            }

            ps = conn.prepareStatement("UPDATE `dau_than` SET `level_tree` = ?, `quantity` = ?, `time_update` = ?, `isUpdate` = ? WHERE `player_id` = ?");
            ps.setInt(1, player.magicTree.level);
            ps.setInt(2, player.magicTree.currentPea);
            ps.setInt(3, player.magicTree.timeUpdate);
            ps.setInt(4, (player.magicTree.isUpdate == true ? 1 : 0));
            ps.setInt(5, (int) player.id);
            if (ps.executeUpdate() == 1) {
                Util.log("Update dau_than where " + player.id + " on data base");
            }

            conn.commit();
            if (player.pet != null) {
                PetDAO.updateBD(player.pet);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePlayersInfo(ArrayList<Player> players) {
        String UPDATE_PLAYER = "UPDATE player SET power=?,vang=?,luong=?,luong_khoa=?,clan_id=?,task_id=?,head=?,where_id=?,where_x=?,where_y=?,last_logout_time=? WHERE id=?";
        String UPDATE_PLAYER_POINT = "UPDATE player_point SET hp_goc=?,mp_goc=?,dam_goc=?,def_goc=?,crit_goc=?,tiem_nang=? WHERE player_id=?";
        Connection conn;
        PreparedStatement ps;
        try {
            for (Player player : players) {
                updateDB(player);
            }

        } catch (Exception e) {
        }
    }

}
