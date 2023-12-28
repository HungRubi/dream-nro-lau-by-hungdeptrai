package real.clan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import server.DBService;
import server.io.Session;

public class Member {
    public int id;
    
    public String name;
    
    public int head;
    
    public int body;
    
    public int leg;
    
    public byte role;
    
    public long powerPoint;
    
    public long donate;
    
    public long receiveDonate;
    
    public long clanPoint;
    
    public long currPoint;
    
    public int joinTime;
    
    public Session session;
}
