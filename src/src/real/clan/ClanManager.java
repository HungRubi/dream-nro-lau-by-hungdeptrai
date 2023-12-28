package real.clan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import server.DBService;
import server.Util;

public class ClanManager {

    private static ArrayList<Clan> clans;
    public static ArrayList<ClanImage> clanImages;
    
    private static ClanManager instance;

    public static ClanManager gI() {
        if (instance == null) {
            instance = new ClanManager();
        }
        return instance;
    }

    public void init() {
        this.clans = ClanDAO.load();
        Util.log("Finish load clan: " + this.clans.size());
        this.clanImages = ClanDAO.loadClanImage();
    }

    public  void addClan(Clan clan){
        if(this.clans.contains(clan) == false){
            this.clans.add(clan);
        }
    }
    
    public ArrayList<Clan> getAllClan(){
        return  this.clans;
    }
    
    public Clan getClanById(int id) {
        for (Clan clan : this.clans) {
            if (clan.id == id) {
                return clan;
            }
        }
        return null;
    }
    
    public ArrayList<Clan> search(String text){
        ArrayList<Clan> listClan = new ArrayList<>();
        for (Clan clan : this.clans) {
            if (clan.name.startsWith(text)){
                listClan.add(clan);
            }
        }
        return listClan;
    }
    
    public ArrayList<Member> getMemberByIdClan(int id){
        for (Clan clan : clans) {
            if (clan.id == id) {
                return clan.members;
            }
        }
        return null;
    }
    public  Member getMember(int idClan, int idPlayer){
        Member m = null;
        for (Member mem : getMemberByIdClan(idClan)) {
            if(mem.id == idPlayer){
                return  mem;
            }
        }
        return m;
    }
    
}
