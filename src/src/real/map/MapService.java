package real.map;

import real.pet.Pet;
import real.player.Player;
import real.player.PlayerDAO;
import real.player.PlayerManger;
import real.skill.SkillUtil;
import server.Util;
import server.io.Message;
import server.io.Session;
import server.Service;

public class MapService {

    private static MapService instance;

    public static MapService gI() {
        if (instance == null) {
            instance = new MapService();
        }
        return instance;
    }

    public void joinMap(Player plJoin, Map map) {
        try {
            if (map.id == 21 || map.id == 22 || map.id == 23) {
                if (plJoin.isPet && plJoin.map == ((Pet) plJoin).master.map) {
                    infoPlayer(((Pet) plJoin).master, plJoin);
                }
                return;
            }
            for (Player pl : map.getPlayers()) {
                if (!pl.isPet && plJoin != pl) {
                    infoPlayer(pl, plJoin);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAnotherPlayers(Player plReceive, Map map) { //load những player trong map và gửi cho player vào map
        try {
            if (map.id == 21 || map.id == 22 || map.id == 23) {
                for (Player pl : map.getPlayers()) {
                    if (pl.id == -plReceive.id) {
                        infoPlayer(plReceive, pl);
                        break;
                    }
                }
                return;
            }
            for (Player pl : map.getPlayers()) {
                if (plReceive != pl) {
                    infoPlayer(plReceive, pl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void infoPlayer(Player plReceive, Player plInfo) {
        Message msg;
        try {
            msg = new Message(-5);
            msg.writer().writeInt((int) plInfo.id);
            if (plInfo.clan != null) {
                msg.writer().writeInt(plInfo.clan.id);
            } else {
                msg.writer().writeInt(-1);
            }
            msg.writer().writeByte(Service.getInstance().get_clevel(plInfo.point.getPower()));
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(plInfo.typePk);
            msg.writer().writeByte(plInfo.gender);
            msg.writer().writeByte(plInfo.gender);
            msg.writer().writeShort(plInfo.getHead());
            msg.writer().writeUTF(plInfo.name);
            msg.writer().writeInt(plInfo.point.getHP());
            msg.writer().writeInt(plInfo.point.getHPFull());
            msg.writer().writeShort(plInfo.getBody());
            msg.writer().writeShort(plInfo.getLeg());
            msg.writer().writeByte(8);
            msg.writer().writeByte(-1);
            msg.writer().writeShort(plInfo.x);
            msg.writer().writeShort(plInfo.y);
            msg.writer().writeShort(0);
            msg.writer().writeShort(0); //
            msg.writer().writeByte(0);
            
            msg.writer().writeByte(plInfo.getUseSpaceShip());
            
            msg.writer().writeByte(plInfo.playerSkill.isMonkey ? 1:0);
            msg.writer().writeShort(plInfo.getMount());
            msg.writer().writeByte(plInfo.cFlag);
            msg.writer().writeByte(0);
            plReceive.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (plInfo.isDie()) {
                msg = new Message(-8);
                msg.writer().writeInt((int) plInfo.id);
                msg.writer().writeByte(0);
                msg.writer().writeShort(plInfo.x);
                msg.writer().writeShort(plInfo.y);
                plReceive.sendMessage(msg);
                msg.cleanup();
            }
        } catch (Exception e) {

        }
    }

    public void exitMap(Player playerExit, Map map) {
        if (map != null) {
            Message msg;
            try {
                msg = new Message(-6);
                msg.writer().writeInt((int) playerExit.id);
                Service.getInstance().sendMessAnotherNotMeInMap(playerExit, msg);
                msg.cleanup();
                map.players.remove(playerExit);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playerMove(Player player) {
        if (player.playerSkill.isCharging) {
            player.playerSkill.stopCharge();
        }
        if (player.playerSkill.useTroi) {
            player.playerSkill.removeUseTroi();
        }
        Map map = player.map;
        Message msg;
        try {
            for (Player pl : map.getPlayers()) {
                if (!pl.isPet && pl != player) {
                    msg = new Message(-7);
                    msg.writer().writeInt((int) player.id);
                    msg.writer().writeShort(player.x);
                    msg.writer().writeShort(player.y);
                    pl.sendMessage(msg);
                    msg.cleanup();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
