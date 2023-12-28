package real.clan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import real.player.Player;
import real.player.PlayerManger;
import server.DBService;
import server.Service;
import server.Util;

public class ClanDAO {
    public static ArrayList<ClanImage> loadClanImage()
    {
      ArrayList<ClanImage> clanImages = new ArrayList<>();  
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBService.gI().getConnection();
            ps = conn.prepareStatement("SELECT * FROM clan_image ORDER BY id ASC");
            rs = ps.executeQuery();
            while (rs.next()) {
                clanImages.add(new ClanImage(rs.getInt(1), rs.getNString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5)));
            }
            conn.close();
            Util.log("Tải Clan Image Thành Công! (" + clanImages.size() + ")" );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clanImages;
    }
    
    public static void leaveClan(int idPlayer, int idClan){
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            removeClanMember(idPlayer);
            conn = DBService.gI().getConnection();
            conn.setAutoCommit(false);
            
                    ps = conn.prepareStatement("UPDATE `player` SET clan_id = -1 WHERE player_id =?");
                    ps.setInt(1, idPlayer);
                    ps.executeUpdate();
             
        }catch(Exception e){
            
        }
    }
    public static ClanMess addJoinClan(Clan clan, Player pl){
        Connection conn = null;
        PreparedStatement ps = null;
        ClanMess mess = null;
        try {
            int timeCur = (int)(System.currentTimeMillis()/1000);
            if(System.currentTimeMillis() - pl.clanTime >= 10000){
            int id = 0;
             conn = DBService.gI().getConnection();
             conn.setAutoCommit(false);
             ps = conn.prepareStatement("SELECT * FROM `clan_message` WHERE  player_id = ? AND type = ? LIMIT 1");
              ps.setInt(1, (int) pl.id);
              ps.setInt(2, 2);
             ResultSet rs = ps.executeQuery();
             if(rs.first()){
                 id = rs.getInt(1);
                 if(rs.getInt(2) != clan.id){
                    ps = conn.prepareStatement("UPDATE `clan_message` SET time = ?, clan_id = ? WHERE id =?");
                    ps.setInt(1, timeCur);
                    ps.setInt(2, clan.id);
                    ps.setInt(3, id);
                    ps.executeUpdate();
                  }else{
                     Service.getInstance().sendThongBao(pl, "Ngươi đã xin vào bang hội này rồi!");
                 }
                  rs.close();
             }else{
                 rs.close();
                 ps = conn.prepareStatement("INSERT INTO `clan_message`(`clan_id`, `player_id`, `name`, `xin_dau`, `content`, `type`, `time`) VALUES (?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
                 ps.setInt(1, clan.id);
                 ps.setInt(2, (int) pl.id);
                 ps.setString(3, pl.name);
                 ps.setInt(4, 0);
                 ps.setString(5, "Xin vào bang");
                 ps.setInt(6, 2);
                 ps.setInt(7,(int)(System.currentTimeMillis()/1000) );
                  if (ps.executeUpdate() == 1) {
                ResultSet rss = ps.getGeneratedKeys();
                if (rss.first()) {
                    id = rss.getInt(1);
                    
                }
             }
             }
             mess = new ClanMess(id, clan.id, (int) pl.id, pl.name, 0, "Xin vào bang", 2, timeCur);
             conn.commit();
             conn.close();
             pl.clanTime = System.currentTimeMillis();
             }else{
                Service.getInstance().sendThongBao(pl, "Không thể thực hiện vì thao tác quá nhanh!");
                return  null;
            }
            }catch(Exception e){
                e.printStackTrace();
            }
        return mess;
    }
    public static void addMember(int idClan, int idPlayer){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBService.gI().getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement("DELETE FROM `clan_member`  WHERE player_id =?");
              ps.setInt(1, idPlayer);
              ps.executeUpdate();
              
             ps = conn.prepareStatement("INSERT INTO clan_member(clan_id, player_id, role, join_time, update_time) VALUES(?,?,?,?,?)");
             ps.setInt(1, idClan);
             ps.setInt(2, idPlayer);
             ps.setByte(3, (byte) 2);
             Timestamp timestamp = new Timestamp(System.currentTimeMillis());
             ps.setTimestamp(4, timestamp);
             ps.setTimestamp(5, timestamp);
             ps.executeUpdate();
             
              ps = conn.prepareStatement("UPDATE `player` SET clan_id = ? WHERE id =?");
              ps.setInt(1, idClan);
              ps.setInt(2, idPlayer);
              ps.executeUpdate();
             
             conn.commit();
             conn.close();
        }catch(Exception e){
            
        }
        
    }
    
    public static ClanMess getMessage(int idMess) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ClanMess mess = null;
        try {
            conn = DBService.gI().getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement("SELECT * FROM clan_message WHERE id = ? LIMIT 1");
            ps.setInt(1, idMess);
            rs = ps.executeQuery();
            if(rs.first()) {
                mess = new ClanMess(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getInt(5), rs.getString(6), rs.getInt(7), rs.getInt(8));
            }
            conn.commit();
            rs.close();
            ps.close();
            conn.close();
            }catch(Exception e){
                
            }
        return mess;
    }
    
    public static void delMess(int idMess){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBService.gI().getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement("DELETE FROM `clan_message` WHERE id = ?");
            ps.setInt(1, idMess);
            ps.executeUpdate();
          }catch(Exception e){ 
          
          }
    }
    
    public static ArrayList<ClanMess> loadMessage(int idClan) {
        ArrayList<ClanMess> mess = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBService.gI().getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement("SELECT * FROM clan_message WHERE clan_id = ? ORDER BY time DESC LIMIT 10");
            ps.setInt(1, idClan);
            rs = ps.executeQuery();
            while (rs.next()) {
                mess.add(new ClanMess(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getInt(5), rs.getString(6), rs.getInt(7), rs.getInt(8)));
            }
            conn.commit();
            rs.close();
            ps.close();
            conn.close();
            }catch(Exception e){
                
            }
        return mess;
    }
    
    
    public static Clan create(Clan clan) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBService.gI().getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement("INSERT INTO clan(name, leader_id, img_id, create_time) VALUES(?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, clan.name);
            ps.setInt(2, clan.leaderID);
            ps.setInt(3, clan.imgID);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(4, timestamp);
            if (ps.executeUpdate() == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.first()) {
                            clan.id = rs.getInt(1);
                            ps = conn.prepareStatement("INSERT INTO clan_member(clan_id, player_id, role, join_time, update_time) VALUES(?,?,?,?,?)");
                            ps.setInt(1, clan.id);
                            ps.setInt(2, clan.leaderID);
                            ps.setByte(3, (byte) 0);
                            ps.setTimestamp(4, timestamp);
                            ps.setTimestamp(5, timestamp);
                            ps.executeUpdate();
                }
            
            conn.commit();
            ps.close();
            conn.close();
            }
            return  clan;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
     public static  void loadMember(int idClan) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBService.gI().getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement("SELECT player.name,player.clan_id,  player_info.* FROM `player_info`, `player` WHERE player_info.player_id = player.id AND id > 0 AND clan_id = ?");
            ps.setInt(1, idClan);
            rs = ps.executeQuery();
            while (rs.next()) {
                Member member = ClanManager.gI().getMember(idClan,rs.getInt("player_id"));
                member.name = rs.getString("name");
                member.head = rs.getInt("head");
                member.body = rs.getInt("body");
                member.leg = rs.getInt("leg");
            }
            conn.commit();
            rs.close();
            ps.close();
            conn.close();
         }catch(Exception e){
             e.printStackTrace();
         }
     }
    
    public static ArrayList<Clan> load() {
        ArrayList<Clan> clans = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBService.gI().getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement("SELECT * FROM clan");
            rs = ps.executeQuery();
            while (rs.next()) {
                Clan clan = new Clan();
                clan.id = rs.getInt("id");
                clan.name = rs.getString("name");// name
                clan.slogan = rs.getString("slogan");// slogan
                clan.imgID = rs.getByte("img_id");// img id
                clan.level = rs.getByte("level");// level
                clan.powerPoint = rs.getLong("power_point");// power point
                clan.leaderID = rs.getInt("leader_id");// leader id
                clan.clanPoint = rs.getInt("clan_point");// clan point
                clan.currMember = 1;// curr mem
                clan.maxMember = rs.getByte("max_member");// max mem
                clan.time = (rs.getTimestamp("create_time").getTime()/1000);// time
                clans.add(clan);
            }
            conn.commit();
            conn.close();
            for(Clan clan : clans){
                clan.members = getMembers(clan.id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clans;
    }

    public static ArrayList<Member> getAllMembers() {
        ArrayList<Member> members = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBService.gI().getConnection();
            ps = conn.prepareStatement("SELECT * FROM clan_member");
            rs = ps.executeQuery();
            while (rs.next()) {
                Member member = new Member();
                member.id = rs.getInt("player_id");
                member.role = rs.getByte("role");
                member.donate = rs.getInt("donate");
                member.receiveDonate = rs.getInt("receive_donate");
                member.clanPoint = rs.getInt("clan_point");
                member.currPoint = rs.getInt("curr_point");
                member.joinTime = (int)( rs.getTimestamp("join_time").getTime() / 1000);
                members.add(member);
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("error getMembers: " + e.toString());
        }
        return members;
    }
    
    public static ArrayList<Member> getMembers(int clanId) {
        ArrayList<Member> members = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBService.gI().getConnection();
            ps = conn.prepareStatement("SELECT * FROM clan_member WHERE clan_id=?");
            ps.setInt(1, clanId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Member member = new Member();
                member.id = rs.getInt("player_id");
                Player pl = PlayerManger.gI().getPlayerByID(member.id);
                member.role = rs.getByte("role");
                member.donate = rs.getInt("donate");
                member.receiveDonate = rs.getInt("receive_donate");
                member.clanPoint = rs.getInt("clan_point");
                member.currPoint = rs.getInt("curr_point");
                member.joinTime = (int)( rs.getTimestamp("join_time").getTime() / 1000);
                member.session = null;
                if(pl !=null){
                    member.session = pl.getSession();
                }
                members.add(member);
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("error getMembers: " + e.toString());
        }
        return members;
    }

//    public static byte getRole(int playerId) {
//        byte role = -1;
//        Connection conn = null;
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        try {
//            conn = DBService.gI().getConnection();
//            ps = conn.prepareStatement("SELECT role FROM clan_member WHERE player_id=? LIMIT 1");
//            ps.setInt(1, playerId);
//            rs = ps.executeQuery();
//            if (rs.first()) {
//                role = rs.getByte("role");
//            }
//            rs.close();
//        } catch (Exception e) {
//            System.out.println("error getMembers: " + e.toString());
//        } finally {
//            try {
//                if (ps != null) {
//                    ps.close();
//                    ps = null;
//                }
//                if (conn != null) {
//                    conn.close();
//                    conn = null;
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        return role;
//    }
//
//    public static void updateSlogan(int clanId, String slogan) {
//        Connection conn = null;
//        PreparedStatement ps = null;
//        try {
//            conn = DBService.gI().getConnection();
//            ps = conn.prepareStatement("UPDATE clan SET slogan=? WHERE id=? LIMIT 1");
//            ps.setString(1, slogan);
//            ps.setInt(2, clanId);
//            ps.executeUpdate();
//        } catch (Exception ex) {
//            System.out.println(ex);
//        } finally {
//            try {
//                if (ps != null) {
//                    ps.close();
//                }
//                if (conn != null) {
//                    conn.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static void changeRole(int playerId, int role) {
//        Connection conn = null;
//        PreparedStatement ps = null;
//        try {
//            conn = DBService.gI().getConnection();
//            ps = conn.prepareStatement("UPDATE clan_member SET role=?,update_time=? WHERE player_id=? LIMIT 1");
//            ps.setInt(1, role);
//            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
//            ps.setInt(3, playerId);
//            ps.executeUpdate();
//        } catch (Exception ex) {
//            System.out.println(ex);
//        } finally {
//            try {
//                if (ps != null) {
//                    ps.close();
//                }
//                if (conn != null) {
//                    conn.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static void transferLeader(int clanId, int oldLeaderId, int newLeaderId) {
//        Connection conn = null;
//        PreparedStatement ps = null;
//        try {
//            conn = DBService.gI().getConnection();
//            conn.setAutoCommit(false);
//            ps = conn.prepareStatement("UPDATE clan_member SET role=?,update_time=? WHERE player_id=? LIMIT 1");
//            ps.setInt(1, ClanRole.PRESIDENT.value());
//            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
//            ps.setInt(3, newLeaderId);
//            ps.addBatch();
//            ps.setInt(1, ClanRole.OFFICER.value());
//            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
//            ps.setInt(3, oldLeaderId);
//            ps.addBatch();
//            ps.executeBatch();
//            conn.commit();
//            conn.setAutoCommit(true);
//            ps.close();
//            ps = null;
//            ps = conn.prepareStatement("UPDATE clan SET leader_id=?,update_time=? WHERE id=? LIMIT 1");
//            ps.setInt(1, newLeaderId);
//            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
//            ps.setInt(3, clanId);
//            ps.executeUpdate();
//        } catch (Exception ex) {
//            System.out.println(ex);
//        } finally {
//            try {
//                if (ps != null) {
//                    ps.close();
//                }
//                if (conn != null) {
//                    conn.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static void distory(int _guildID) {
//        Connection conn = null;
//        PreparedStatement ps = null;
//        try {
//            conn = DBService.gI().getConnection();
//            ps = conn.prepareStatement("DELETE FROM clan_member WHERE clan_id=?");
//            ps.setInt(1, _guildID);
//            ps.executeUpdate();
//            ps.close();
//            ps = null;
//            ps = conn.prepareStatement("DELETE FROM clan WHERE id=? LIMIT 1");
//            ps.setInt(1, _guildID);
//            ps.executeUpdate();
//        } catch (Exception ex) {
//            System.out.println(ex);
//        } finally {
//            try {
//                if (ps != null) {
//                    ps.close();
//                }
//                if (conn != null) {
//                    conn.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static boolean add(int clanId, int playerId) {
//        Connection conn = null;
//        PreparedStatement ps = null;
//        try {
//            conn = DBService.gI().getConnection();
//            ps = conn.prepareStatement("INSERT INTO clan_member(clan_id, player_id, role, create_time, update_time) VALUES(?,?,?,?,?)");
//            ps.setInt(1, clanId);
//            ps.setInt(2, playerId);
//            ps.setByte(3, ClanRole.NORMAL.value());
//            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
//            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
//            ps.executeUpdate();
//            return true;
//        } catch (Exception ex) {
//            System.out.println(ex);
//        } finally {
//            try {
//                if (ps != null) {
//                    ps.close();
//                }
//                if (conn != null) {
//                    conn.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }

    public static void removeClanMember(int _userID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBService.gI().getConnection();
            ps = conn.prepareStatement("DELETE FROM clan_member WHERE player_id=? LIMIT 1");
            ps.setInt(1, _userID);
            ps.executeUpdate();
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<Clan> searchClan(String text) {
        ArrayList<Clan> clans = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBService.gI().getConnection();
            ps = conn.prepareStatement("select * from clan where name like ? limit 10");
            ps.setString(1, text + "%");
            rs = ps.executeQuery();
            if (DBService.resultSize(rs) != 0) {
                while (rs.next()) {
                    Clan clan = new Clan();
                    clan.id = rs.getByte("id");// id
                    clan.name = rs.getString("name");// name
                    clan.slogan = rs.getString("slogan");// slogan
                    clan.imgID = rs.getByte("img_id");// img id
                    clan.powerPoint = rs.getLong("power_point");// power point
                    clan.leaderID = rs.getInt("leader_id");// leader id
                    clan.currMember = 1;// curr mem
                    clan.maxMember = 10;// max mem
                    clan.time = (int) System.currentTimeMillis();// time
                    clans.add(clan);
                }
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("error: " + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return clans;
    }
    
    public void clanDB(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBService.gI().getConnection();
            conn.setAutoCommit(false);
            for(Clan clan : ClanManager.gI().getAllClan()){
                
            }
            conn.commit();
            conn.close();
        }catch(Exception e){
            
        }
        
    }
}
