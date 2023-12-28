package real.clan;
import java.util.ArrayList;
import real.player.Player;
import real.player.PlayerDAO;
import real.player.PlayerManger;
import server.Util;
import server.io.Message;
import server.io.Session;

public class ClanService {

    private static ClanService instance;

    public static ClanService gI() {
        if (instance == null) {
            instance = new ClanService();
        }
        return instance;
    }
    
    public void leaveClan(int playerID, int clanID){
        Clan clan = ClanManager.gI().getClanById(clanID);
        Member mem = ClanManager.gI().getMember(clanID, playerID);
        clan.members.remove(mem);
        Player player = PlayerManger.gI().getPlayerByID(playerID);
        if(player != null){
            player.clan = null;
        }
        for(Member m : clan.members){
            Player playermem = PlayerManger.gI().getPlayerByID(m.id);
            if(playermem != null){
                playermem.clan = clan;
                resetClanInfo(playermem);
                clanInfo(player);
            }
        }
        
    }
    
    public void disJoinClan(Player player, int idMess, boolean kick){
           ClanMess mess = ClanDAO.getMessage(idMess);
           Clan clan = ClanManager.gI().getClanById(mess.clanID);
           Player pl = PlayerDAO.loadbyid(mess.playerID);
           
        try{
            ClanDAO.delMess(idMess);
            if(kick == false){
                ClanDAO.addMember(mess.clanID, mess.playerID);
                Member member = new Member();
                        member.id = (int)pl.id;
                        member.powerPoint = pl.point.power;
                        member.clanPoint = 1;
                        member.joinTime = (int)(System.currentTimeMillis()/1000);
                        member.currPoint = 1;
                        member.role = 0;
                   if(clan.members.contains(member) == false){
                    clan.members.add(member);
                  }
                  if(PlayerManger.gI().getPlayerByID(mess.playerID) != null){
                        pl = PlayerManger.gI().getPlayerByID(mess.playerID);
                        pl.clan = clan;
                        clanInfo(pl);
                   }
            }
            resetClanInfo(player);
            clanInfo(player);
            
        }catch(Exception e){
            
        }
        
    }
   
    public void joinClan(Player pl, Clan clan){
        try{
             ClanMess  mess = ClanDAO.addJoinClan(clan, pl);
             Util.log(""+mess.name);
             if(mess != null){
                 Message msg = new Message(-51);
                 msg.writer().writeByte(2);
                 msg.writer().writeInt(mess.id);
                 msg.writer().writeInt(mess.playerID);
                 msg.writer().writeUTF(mess.name);
                 msg.writer().writeByte(2);
                 msg.writer().writeInt(mess.time);
                 
            for(Member m : clan.members){
                if(m.session != null){
                    m.session.sendMessage(msg);
                }
            }
                msg.cleanup();
            }
        }catch(Exception e){
            
        }
        
    }
    
    public void loadImgClan(Player pl){
        Message msg = null;
        try{
        for(int i = 0; i < ClanManager.gI().clanImages.size(); i++)
                {
                        ClanImage cl = ClanManager.gI().clanImages.get(i);
                        msg = new Message(-62);
                        msg.writer().writeByte(cl.ID);
                        msg.writer().writeByte(1);
                        msg.writer().writeShort((short)cl.img);
                        pl.sendMessage(msg);
                        msg.cleanup();
                }
        }catch(Exception e){
            
        }
    }
    
    public void clanAction(Player pl, byte action, int id, String text){
        Message msg;
        try{
            if(action == 1 || action == 3) // hiện bảng chọn biểu tượng :v
            {
                msg = new Message(-46);
                msg.writer().writeByte(action);
                msg.writer().writeByte(ClanManager.gI().clanImages.size());
                for(int i = 0; i < ClanManager.gI().clanImages.size(); i++)
                {
                    ClanImage cl = ClanManager.gI().clanImages.get(i);
                    if(cl.ID != -1)
                    {
                        msg.writer().writeByte(cl.ID);
                        msg.writer().writeUTF(cl.name);
                        msg.writer().writeInt(cl.xu);
                        msg.writer().writeInt(cl.luong);
                    }
                }
                pl.sendMessage(msg);
                msg.cleanup();
                ClanService.gI().loadImgClan(pl);
                
            }
            
            switch (action) {
                    case 2:
                        Clan clan = new Clan();
                        clan.name = text;
                        clan.slogan = "";
                        clan.imgID = (byte) id;
                        clan.powerPoint = 0l;
                        clan.leaderID = (int)pl.id;
                        clan.maxMember = 10;
                        clan.level = 1;
                        clan.clanPoint = 1;
                        clan.currMember = 1;
                        clan.time =  (int)(System.currentTimeMillis()/1000);
                        Member member = new Member();
                        member.id = (int)pl.id;
                        member.name = pl.name;
                        member.powerPoint = pl.point.power;
                        member.clanPoint = 1;
                        member.joinTime = (int)(System.currentTimeMillis()/1000);
                        member.currPoint = 1;
                        member.role = 0;
                        clan.members.add(member);
                        clan = ClanDAO.create(clan);
                        ClanManager.gI().addClan(clan);
                        pl.clan = clan;
                        clanInfo(pl);
                        
                        msg = new Message(-61);
                        msg.writer().writeInt((int)pl.id); // id leader
                         msg.writer().writeInt(clan.id);// id clan
                        pl.sendMessage(msg);
                        msg.cleanup();
                    break;
                    case 3:
                        msg = new Message(-46);
                        msg.writer().writeByte(4);
                        msg.writer().writeByte(id);
                        msg.writer().writeUTF(pl.clan.slogan);
                        pl.sendMessage(msg);
                        msg.cleanup();
                    break;
                    case 4:
                        msg = new Message(-46);
                        msg.writer().writeByte(4);
                        msg.writer().writeByte(pl.clan.imgID);
                        msg.writer().writeUTF(text);
                        pl.sendMessage(msg);
                        msg.cleanup();
                    break;
                    
                
            }
            
        }catch(Exception e){
            e.printStackTrace();    
        }
        
    }

    public void resetClanInfo(Player pl){
        Message msg;
        Clan clan = pl.clan;
        try {
            msg = new Message(-53);
            ArrayList<Member> members = clan.members;
            msg.writer().writeInt(-1);
            pl.sendMessage(msg);
            msg.cleanup();
           }catch(Exception e){
                
           }
    }
    public void clanInfo(Player pl) {
        Message msg;
        //resetClanInfo(pl);
        Clan clan = pl.clan;
        ClanDAO.loadMember(clan.id);
        try {
            msg = new Message(-53);
            ArrayList<Member> members = clan.members;
            msg.writer().writeInt(clan.id);
            msg.writer().writeUTF(clan.name);
            msg.writer().writeUTF(clan.slogan);
            msg.writer().writeByte(clan.imgID);
            msg.writer().writeUTF(Util.powerToString(clan.powerPoint));
            msg.writer().writeUTF(ClanManager.gI().getMember(clan.id,clan.leaderID).name);//leaderName
            msg.writer().writeByte(members.size());
            msg.writer().writeByte(clan.maxMember);
            msg.writer().writeByte(ClanManager.gI().getMember(clan.id, (int) pl.id).role);// role
            msg.writer().writeInt(clan.clanPoint);
            msg.writer().writeByte(clan.level);
            for (Member member : members) {
                msg.writer().writeInt((int)member.id);
                msg.writer().writeShort(member.head);
                msg.writer().writeShort(member.leg);
                msg.writer().writeShort(member.body);
                msg.writer().writeUTF(member.name);//playerName
                msg.writer().writeByte(member.role);
                msg.writer().writeUTF(Util.powerToString(member.powerPoint));
                msg.writer().writeInt(0);
                msg.writer().writeInt(0);
                msg.writer().writeInt(0);
                msg.writer().writeInt(0);
                msg.writer().writeInt(member.joinTime);
            }
            ArrayList<ClanMess> messs = ClanDAO.loadMessage(clan.id);
            msg.writer().writeByte(messs.size());
            for(ClanMess mess : messs){
                  msg.writer().writeByte(mess.type);
                 msg.writer().writeInt(mess.id);
                 msg.writer().writeInt(mess.playerID);
                 msg.writer().writeUTF(mess.name);
                 msg.writer().writeByte(2);
                 msg.writer().writeInt(mess.time);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clanMember(Session session, int clanId) {
        Message msg;
        ArrayList<Member> members = ClanManager.gI().getMemberByIdClan(clanId);
        try {
            msg = new Message(-50);
            msg.writer().writeByte(members.size());
            for (Member member : members) {
                msg.writer().writeShort(member.head);
                msg.writer().writeShort(member.leg);
                msg.writer().writeShort(member.body);
                msg.writer().writeUTF(member.name);//playerName
                msg.writer().writeByte(member.role);
                msg.writer().writeUTF(Util.powerToString(member.powerPoint));
                msg.writer().writeInt(0);
                msg.writer().writeInt(0);
                msg.writer().writeInt(0);
                msg.writer().writeInt(member.joinTime);
            }
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void searchClan(Player pl, String text) {
        Message msg;
        
        try {
            if(pl.clan != null){
                        msg = new Message(-61);
                        msg.writer().writeInt((int)pl.id); // id leader
                        msg.writer().writeInt(pl.clan.id);// id clan
                        pl.sendMessage(msg);
                        msg.cleanup();
                        
                        clanInfo(pl);
                        
                        return;
            }
            
            ArrayList<Clan> clans = ClanManager.gI().search(text);
            msg = new Message(-47);
            msg.writer().writeByte(clans.size());
            for (Clan clan : clans) {
                ClanDAO.loadMember(clan.id);
                msg.writer().writeInt(clan.id);
                msg.writer().writeUTF(clan.name);
                msg.writer().writeUTF(clan.slogan);
                msg.writer().writeByte(clan.imgID);
                String powerPoint = Util.powerToString(clan.powerPoint);
                msg.writer().writeUTF(powerPoint);
               msg.writer().writeUTF(ClanManager.gI().getMember(clan.id,clan.leaderID).name);//leaderName
                int currMember = clan.members.size();
                msg.writer().writeByte(currMember);
                msg.writer().writeByte(clan.maxMember);
                msg.writer().writeInt((int) clan.time);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            System.out.println("-47 " + e.toString());
        }
    }
}
