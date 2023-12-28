package real.clan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import server.DBService;
import server.io.Session;

public class ClanMess {
    
    public int id;
    public int clanID;
    public int playerID;
    public String name;
    public int xindau;
    public String content;
    public int type;
    public int time;
    
    public ClanMess(int id, int idclan, int idplayer, String name, int dauthan, String text, int type, int time)
    {
        this.id = id;
        this.clanID = idclan;
        this.playerID = idplayer;
        this.name = name;
        this.xindau = dauthan;
        this.content = text;
        this.type = type;
        this.time = time;
    }
}
