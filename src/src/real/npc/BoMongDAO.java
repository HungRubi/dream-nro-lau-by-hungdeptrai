package real.npc;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Timer;
import java.util.TimerTask;
import real.player.Player;
import real.player.PlayerDAO;
import real.player.PlayerManger;
import server.DBService;
import server.Service;


/**
 *
 * @author Yuu
 */
public class BoMongDAO {
    public static void BoMongAuto() {
        Connection conn;
        PreparedStatement ps;
        try {
                conn = DBService.gI().getConnection();
                conn.setAutoCommit(false);
                ps = conn.prepareStatement("SELECT * FROM bo_mong");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    long playerID = rs.getLong("player_id");
                    int ruby =  rs.getInt("ruby");
                    int gem =  rs.getInt("gem");
                    int gold =  rs.getInt("gold");
                    String content = rs.getString("content");
                    
                    Player pl = PlayerManger.gI().getPlayerByID((int)playerID);
                    if(pl != null){
                            pl.inventory.gem += gem;
                            pl.inventory.ruby += ruby;
                            pl.inventory.gold += gold;
                            Service.getInstance().sendMoney(pl);
                            Service.getInstance().sendThongBao(pl, content);
                            ps = conn.prepareStatement("DELETE FROM bo_mong WHERE id = ?");
                            ps.setLong(1, id);
                            ps.executeUpdate();
                    }
                }
                rs.close();
                ps.close();
                conn.commit();
                conn.close();
            }catch(Exception e){
                
            }
    }
    static Timer timer;
    static protected boolean actived = false;

    public static void active(int delay, int period) {
        if (!actived) {
            actived = true;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    BoMongAuto();
                }
            }, delay*1L, period*1L);
            
            
        }
    }
}
    
