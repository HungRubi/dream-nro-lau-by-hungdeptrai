package real.skill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import real.player.Player;
import server.DBService;
import server.Util;

public class SkillShortCutDAO {

    public static boolean isCreate(long player_id) {
        try {
            int dem = 0;
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from skill_shortcut where player_id = ?");
            ps.setLong(1, player_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                dem = 1;
            }
            rs.close();
            ps.close();
            if (dem == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    public static void create(long player_id, int gender) {
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("insert into skill_shortcut values(?,?,?,?,?,?)");
            ps.setLong(1, player_id);
            ps.setInt(2, gender == 0 ? 0 : (gender == 1 ? 2 : 4));
            ps.setInt(3, -1);
            ps.setInt(4, -1);
            ps.setInt(5, -1);
            ps.setInt(6, -1);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
        }
    }

    public static void load(Player pl) {
        if (isCreate(pl.id) == false) {
            create(pl.id, pl.gender);
        }
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from skill_shortcut where player_id = ?");
            ps.setLong(1, pl.id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                pl.playerSkill.skillShortCut[0] = rs.getByte(2);
                pl.playerSkill.skillShortCut[1] = rs.getByte(3);
                pl.playerSkill.skillShortCut[2] = rs.getByte(4);
                pl.playerSkill.skillShortCut[3] = rs.getByte(5);
                pl.playerSkill.skillShortCut[4] = rs.getByte(6);
                System.out.println(Arrays.toString(pl.playerSkill.skillShortCut));
            }
            rs.close();
            ps.close();
            int dem = 0;
            for (int i : pl.playerSkill.skillShortCut) {
                if (pl.playerSkill.getSkillbyId(i).damage > 0) {
                    pl.playerSkill.skillSelect = pl.playerSkill.getSkillbyId(i);
                    dem = 1;
                    break;
                }
            }
            if (dem == 0) {
                pl.playerSkill.skillSelect = pl.playerSkill.getSkillbyId(pl.gender == 0 ? 0 : (pl.gender == 1 ? 2 : 4));
            }
        } catch (Exception e) {
        }
    }

    public static void save(Player pl) {
        if (isCreate(pl.id) == false) {
            create(pl.id, pl.gender);
        }
        try {
            Connection conn = DBService.gI().getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE skill_shortcut SET skill_1= ?, skill_2= ?, skill_3 = ?, skill_4= ?, skill_5= ? WHERE player_id = ?");
            ps.setInt(1, (int) pl.playerSkill.skillShortCut[0]);
            ps.setInt(2, (int) pl.playerSkill.skillShortCut[1]);
            ps.setInt(3, (int) pl.playerSkill.skillShortCut[2]);
            ps.setInt(4, (int) pl.playerSkill.skillShortCut[3]);
            ps.setInt(5, (int) pl.playerSkill.skillShortCut[4]);
            ps.setLong(6, pl.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
