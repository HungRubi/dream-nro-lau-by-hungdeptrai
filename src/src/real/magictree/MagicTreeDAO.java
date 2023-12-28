package real.magictree;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import real.player.Player;
import server.DBService;

public class MagicTreeDAO {

    public static void loadMagicTree(Player pl) {
        try {
            Connection conn = DBService.gI().getConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement("select * from dau_than where player_id = ?");
            ps.setLong(1, pl.id);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                    pl.magicTree = new MagicTree(pl.id, rs.getInt(2), rs.getInt(3), rs.getInt(5) == 1, rs.getInt(4));
            }else{
                    create((int) pl.id);
                    pl.magicTree = new MagicTree(pl.id, 1, 0, false, 0);
            }
            conn.commit();
            conn.close();
            rs.close();
            ps.close();
        } catch (Exception e) {
        }
    }

    public static void create(int player_id) {
        try {
            Connection con = DBService.gI().getConnection();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement("insert into dau_than(player_id,quantity,time_update) values(?,?,?)");
            ps.setInt(1, player_id);
            ps.setInt(2, 5); //số lượng
            ps.setInt(3, 0); //time update
            ps.executeUpdate();
            con.commit();
            con.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
