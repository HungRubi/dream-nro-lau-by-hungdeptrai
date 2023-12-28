package real.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import real.item.ItemDAO;
import real.player.Player;
import server.DBService;

public class ListPlayerDAO {

    private ListPlayerDAO() {

    }

    public static void loadFriend(ListPlayer listPlayer, Player player) {
        List<Player> listFriend = new ArrayList<>();
        List<Integer> listFriendId = new ArrayList<>();
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from friend where player_id = ?");
            ps.setInt(1, (int) player.id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listFriendId.add(rs.getInt(2));
            }

            for (Integer id : listFriendId) {
                ps = con.prepareStatement("select * from player where id = ?");
                ps.setInt(1, id);
                rs = ps.executeQuery();
                Player friend = new EntityList();
                friend.id = id;
                while (rs.next()) {
                    friend.name = rs.getString(3);
                    friend.point.power = rs.getLong(4);
                    friend.head = rs.getByte(10);
                    friend.gender = rs.getByte(11);
                    PreparedStatement psPet = con.prepareStatement("select * from pet where id = ?");
                    psPet.setInt(1, -id);
                    ResultSet rsPet = psPet.executeQuery();
                    if (rsPet.next()) {
                        friend.typeFusion = rsPet.getByte(7);
                    }
                }
                ps = con.prepareStatement("select * from player_body where player_id = ?");
                ps.setInt(1, id);
                ItemDAO.loadItemBody(friend);
                listFriend.add(friend);
            }
            listPlayer.addFriend(listFriend);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadEnemy(ListPlayer listPlayer, Player player) {
        List<Player> listEnemy = new ArrayList<>();
        List<Integer> listEnemyId = new ArrayList<>();
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from enemy where player_id = ?");
            ps.setInt(1, (int) player.id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listEnemyId.add(rs.getInt(2));
            }

            for (Integer id : listEnemyId) {
                ps = con.prepareStatement("select * from player where id = ?");
                ps.setInt(1, id);
                rs = ps.executeQuery();
                Player enemy = new EntityList();
                enemy.id = id;
                while (rs.next()) {
                    enemy.name = rs.getString(3);
                    enemy.point.power = rs.getLong(4);
                    enemy.head = rs.getByte(10);
                    enemy.gender = rs.getByte(11);
                    PreparedStatement psPet = con.prepareStatement("select * from pet where id = ?");
                    psPet.setInt(1, -id);
                    ResultSet rsPet = psPet.executeQuery();
                    if (rsPet.next()) {
                        enemy.typeFusion = rsPet.getByte(7);
                    }
                }
                ps = con.prepareStatement("select * from player_body where player_id = ?");
                ps.setInt(1, id);
                ItemDAO.loadItemBody(enemy);
                listEnemy.add(enemy);
            }
            listPlayer.addEnemy(listEnemy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
