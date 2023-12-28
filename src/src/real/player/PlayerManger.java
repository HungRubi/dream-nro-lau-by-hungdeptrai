package real.player;
import java.util.ArrayList;
import java.util.Timer;
import real.item.ItemDAO;

public class PlayerManger {

    private static PlayerManger instance;

    private ArrayList<Player> players;

    public PlayerManger() {
        this.players = new ArrayList<>();
    }
    
    public static PlayerManger gI(){
        if (instance == null){
            instance = new PlayerManger();
        }
        return instance;
    }
    

    public Player getPlayerByUserID(int _userID) {
        for (Player player : players) {
            if (!player.isPet && player.getSession().userId == _userID){
                return player;
            }
        }
        return null;
    }
    
    public Player getPlayerByID(int playerId){
        for(Player player : players){
            if(player.id == playerId){
                return player;
            }
        }
        return null;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
    
    public int size(){
        return players.size();
    }

}
