package tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import real.magictree.MagicTreeDAO;
import real.pet.PetDAO;
import real.player.PlayerDAO;
import real.skill.SkillData;
import real.skill.SkillShortCutDAO;
import server.DBService;

public class CreateChar {

    public static void main(String[] args) {
       // createPlayer(createAccount("a", "a"), "Girlkun75", 2, 27);
      //  createPlayer(createAccount("b", "a"), "Girlkun75", 0, 64);
     //   createPlayer(createAccount("c", "a"), "Girlkun75", 1, 9);
      //  createPlayer(createAccount("d", "a"), "Girlkun75", 2, 27);
     //   createPlayer(createAccount("e", "a"), "Girlkun75", 2, 27);
     //   createPlayer(createAccount("f", "a"), "Girlkun75", 2, 27);
      //  createPlayer(createAccount("g", "a"), "Girlkun75", 2, 27);
       // createPlayer(createAccount("h", "a"), "Girlkun75", 2, 27);

        SkillData.loadSkill();
        PetDAO.create(PlayerDAO.load(1), 1);
        PetDAO.create(PlayerDAO.load(2), 2);
        PetDAO.create(PlayerDAO.load(3), 0);
        System.out.println("done");
        System.exit(0);
    }

    public static int createAccount(String user, String password) {
        int key = -1;
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from account where username = ?");
            ps.setString(1, user);
            if (ps.executeQuery().next()) {
                System.out.println("Tạo thất bại do tài khoản đã tồn tại");
            } else {
                ps = con.prepareStatement("insert into account(username,password) values (?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user);
                ps.setString(2, password);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.first();
                key = rs.getInt(1);
                System.out.println("Tạo tài khoản thành công!");
            }
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    public static boolean createPlayer(int userId, String name, int gender, int hair) {
        String CREATE_PLAYER = "INSERT INTO player(account_id,name,power,vang,luong,luong_khoa,gender,head,where_id,where_x,where_y) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        String CREATE_PLAYER_POINT = "INSERT INTO player_point(player_id,hp_goc,mp_goc,dam_goc,def_goc,crit_goc,tiem_nang) VALUES (?,?,?,?,?,?,?)";
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
            ps.setLong(3, 2000000000);
            ps.setInt(4, 0);
            ps.setInt(5, 0);
            ps.setLong(6, 0);
            ps.setInt(7, gender);
            ps.setInt(8, hair);
            ps.setInt(9, 5);
            ps.setInt(10, 10);
            ps.setInt(11, 10);
            if (ps.executeUpdate() == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.first()) {
                    int playerId = rs.getInt(1);
                    ps = conn.prepareStatement(CREATE_PLAYER_POINT);
                    ps.setInt(1, playerId);
                    switch (gender) {
                        case 0:
                            ps.setInt(2, 200);
                            ps.setInt(3, 100);
                            ps.setInt(4, 15);
                            break;
                        case 1:
                            ps.setInt(2, 200);
                            ps.setInt(3, 100);
                            ps.setInt(4, 15);
                            break;
                        case 2:
                            ps.setInt(2, 200);
                            ps.setInt(3, 100);
                            ps.setInt(4, 15);
                            break;
                    }
                    ps.setInt(5, 0);
                    ps.setInt(6, 0);
                    ps.setLong(7, 1200);
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
                        ps.setInt(1, playerId);
                        ps.setInt(2, phai[i]);
                        ps.setInt(3, 1);
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

}
