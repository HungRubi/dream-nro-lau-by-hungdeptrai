package real.func;

import java.util.List;
import real.map.Map;
import real.map.MapManager;
import real.map.MapService;
import real.map.Mob;
import real.map.WayPoint;
import real.player.Player;
import server.Service;
import server.Util;
import server.io.Message;
import server.io.Session;

public class ChangeMap {

    public static final byte NON_SPACE_SHIP = 0;
    public static final byte DEFAULT_SPACE_SHIP = 1;
    public static final byte TELEPORT_YARDRAT = 2;
    public static final byte TENNIS_SPACE_SHIP = 3;

    private static ChangeMap instance;

    private ChangeMap() {

    }

    public static ChangeMap gI() {
        if (instance == null) {
            instance = new ChangeMap();
        }
        return instance;
    }

    //capsule, tàu vũ trụ
    public void changeMapBySpaceShip(Player pl, int mapId, int zone, int x, byte typeSpace) {
        List<Map> maps = MapManager.gI().getMapById(mapId);
        Map map = null;
        if (zone == -1) {
            for (Map m : maps) {
                if (m.getPlayers().size() <= 7) {
                    map = m;
                    break;
                }
            }
        } else {
            map = MapManager.gI().getMap(mapId, zone);
        }
        if (map != null) {
            PVP.gI().finishPVP(pl, PVP.TYPE_LEAVE_MAP);
            pl.setUseSpaceShip(typeSpace);
            pl.x = x != -1 ? x : Util.nextInt(100, map.mapWidth - 100);
            pl.y = 5;
            spaceShipArrive(pl, typeSpace);
            pl.gotoMap(map);
            Service.getInstance().clearMap(pl);
            Service.getInstance().mapInfo(pl); //-24
            MapService.gI().joinMap(pl, map);
            MapService.gI().loadAnotherPlayers(pl, pl.map);
            pl.setUseSpaceShip((byte) 0);
        }
    }

    public void changeMap(Player pl, int mapId, int zoneId, int x, int y, byte typeSpace) {
        List<Map> maps = MapManager.gI().getMapById(mapId);
        Map map = null;
        if (zoneId == -1) {
            for (Map m : maps) {
                if (m.getPlayers().size() <= 7) {
                    map = m;
                    break;
                }
            }
        } else {
            map = MapManager.gI().getMap(mapId, zoneId);
        }
        if (map != null) {
            pl.setUseSpaceShip(typeSpace);
            PVP.gI().finishPVP(pl, PVP.TYPE_LEAVE_MAP);
            pl.x = x;
            pl.y = y;
            pl.gotoMap(map);
            Service.getInstance().clearMap(pl);
            Service.getInstance().mapInfo(pl); //-24
            MapService.gI().joinMap(pl, map);
            MapService.gI().loadAnotherPlayers(pl, pl.map);
            pl.setUseSpaceShip((byte) 0);
        }
    }

    //chỉ dùng cho boss
    public void changeMapBySpaceShip(Player pl, Map mapJoin, byte typeSpace) {
        if (mapJoin != null) {
            pl.setUseSpaceShip(typeSpace);
            pl.x = Util.nextInt(100, mapJoin.mapWidth - 100);
            pl.y = 5;
            spaceShipArrive(pl, typeSpace);
            pl.gotoMap(mapJoin);
            Service.getInstance().clearMap(pl);
            Service.getInstance().mapInfo(pl); //-24
            MapService.gI().joinMap(pl, mapJoin);
            MapService.gI().loadAnotherPlayers(pl, pl.map);
            pl.setUseSpaceShip((byte) 0);
        }
    }

    public void changeMapWaypoint(Player player) {
                    if(player.map.id == 45 && player.y >= 520){
                        Map map = MapManager.gI().getMap(46, 0);
                        player.gotoMap(map);
                        player.x = 360;
                        player.y = 200;
                        Service.getInstance().clearMap(player);
                        Service.getInstance().mapInfo(player);
                        MapService.gI().joinMap(player, map);
                        MapService.gI().loadAnotherPlayers(player, player.map);
                        return;
                    }
                    if(player.map.id == 46 && player.y >= 530){
                        Map map = MapManager.gI().getMap(47, 0);
                        player.gotoMap(map);
                        player.x = 654;
                        player.y = 186;
                        Service.getInstance().clearMap(player);
                        Service.getInstance().mapInfo(player);
                        MapService.gI().joinMap(player, map);
                        MapService.gI().loadAnotherPlayers(player, player.map);
                        return;
                    }
        
        WayPoint wp = player.isInWaypoint();
        if (wp != null) {
            if (wp.goMap == 111) {
                Service.getInstance().resetPoint(player, player.x - 50, player.y);
                return;
            }
            List<Map> maps = MapManager.gI().getMapById(wp.goMap);
            Map map = null;
            int numsPlayer = 7;
            for (Map m : maps) {
                if (m.getPlayers().size() <= numsPlayer) {
                    map = m;
                    break;
                }
            }
            PVP.gI().finishPVP(player, PVP.TYPE_LEAVE_MAP);
            
            player.gotoMap(map);
            player.x = wp.goX;
            player.y = wp.goY;
            Service.getInstance().clearMap(player);
            Service.getInstance().mapInfo(player);
            MapService.gI().joinMap(player, map);
            MapService.gI().loadAnotherPlayers(player, player.map);
        } else {
            Service.getInstance().sendThongBaoOK(player, "Không thể vào map");
            Service.getInstance().resetPoint(player, player.x - 50, player.y);
        }
        return;
    }

    public void finishLoadMap(Player player) {
        if (player.map.id >= 21 && player.map.id <= 23) {
            //player.magicTree.loadDauThan();
        }
        sendEffectMapToMe(player);
        sendEffectMeToMap(player);
        Util.log("finishLoadMap");
    }

    private void sendEffectMeToMap(Player player) {
        Message msg;
        try {
            for (Mob mob : player.map.getMobs()) {
                if (mob.isDie()) {
                    msg = new Message(-12);
                    msg.writer().writeByte(mob.id);
                    player.sendMessage(msg);
                    msg.cleanup();
                }
                if (mob.isThoiMien) {
                    msg = new Message(-124);
                    msg.writer().writeByte(1); //b5
                    msg.writer().writeByte(1); //b6
                    msg.writer().writeByte(41); //num6
                    msg.writer().writeByte(mob.id); //b7
                    player.sendMessage(msg);
                    msg.cleanup();
                }
                if (mob.isSocola) {
                    msg = new Message(-112);
                    msg.writer().writeByte(1);
                    msg.writer().writeByte(mob.id); //b4
                    msg.writer().writeShort(4133);//b5
                    player.sendMessage(msg);
                    msg.cleanup();
                }
                if (mob.isStun || mob.isBlindDCTT) {
                    msg = new Message(-124);
                    msg.writer().writeByte(1);
                    msg.writer().writeByte(1);
                    msg.writer().writeByte(40);
                    msg.writer().writeByte(mob.id);
                    player.sendMessage(msg);
                    msg.cleanup();
                }
            }
        } catch (Exception e) {

        }
        try {
            if (player.playerSkill.isShielding) {
                msg = new Message(-124);
                msg.writer().writeByte(1);
                msg.writer().writeByte(0);
                msg.writer().writeByte(33);
                msg.writer().writeInt((int) player.id);
                Service.getInstance().sendMessAnotherNotMeInMap(player, msg);
                msg.cleanup();
            }

            if (player.mobMe != null) {
                msg = new Message(-95);
                msg.writer().writeByte(0);//type
                msg.writer().writeInt((int) player.id);
                msg.writer().writeShort(player.mobMe.tempId);
                msg.writer().writeInt(player.mobMe.gethp());// hp mob
                Service.getInstance().sendMessAnotherNotMeInMap(player, msg);
                msg.cleanup();
            }
            if (player.pet != null && player.pet.mobMe != null) {
                msg = new Message(-95);
                msg.writer().writeByte(0);//type
                msg.writer().writeInt((int) player.pet.mobMe.id);
                msg.writer().writeShort(player.pet.mobMe.tempId);
                msg.writer().writeInt(player.pet.mobMe.gethp());// hp mob
                Service.getInstance().sendMessAnotherNotMeInMap(player, msg);
                msg.cleanup();
            }
        } catch (Exception e) {
        }
    }

    private void sendEffectMapToMe(Player player) {
        Message msg;
        try {
            for (Player pl : player.map.players) {
                if (pl.playerSkill.isShielding) {
                    msg = new Message(-124);
                    msg.writer().writeByte(1);
                    msg.writer().writeByte(0);
                    msg.writer().writeByte(33);
                    msg.writer().writeInt((int) pl.id);
                    player.sendMessage(msg);
                    msg.cleanup();
                }
                if (pl.playerSkill.isThoiMien) {
                    msg = new Message(-124);
                    msg.writer().writeByte(1); //b5
                    msg.writer().writeByte(0); //b6
                    msg.writer().writeByte(41); //num3
                    msg.writer().writeInt((int) pl.id); //num4
                    player.sendMessage(msg);
                    msg.cleanup();
                }
                if (pl.playerSkill.isBlindDCTT || pl.playerSkill.isStun) {
                    msg = new Message(-124);
                    msg.writer().writeByte(1);
                    msg.writer().writeByte(0);
                    msg.writer().writeByte(40);
                    msg.writer().writeInt((int) pl.id);
                    msg.writer().writeByte(0);
                    msg.writer().writeByte(32);
                    player.sendMessage(msg);
                    msg.cleanup();
                }

                if (pl.playerSkill.useTroi) {
                    if (pl.playerSkill.plAnTroi != null) {
                        msg = new Message(-124);
                        msg.writer().writeByte(1); //b5
                        msg.writer().writeByte(0);//b6
                        msg.writer().writeByte(32);//num3
                        msg.writer().writeInt((int) pl.playerSkill.plAnTroi.id);//num4
                        msg.writer().writeInt((int) pl.id);//num9
                        player.sendMessage(msg);
                        msg.cleanup();
                    }
                    if (pl.playerSkill.mobAnTroi != null) {
                        msg = new Message(-124);
                        msg.writer().writeByte(1); //b4
                        msg.writer().writeByte(1);//b5
                        msg.writer().writeByte(32);//num8
                        msg.writer().writeByte(pl.playerSkill.mobAnTroi.id);//b6
                        msg.writer().writeInt((int) pl.id);//num9
                        player.sendMessage(msg);
                        msg.cleanup();
                    }
                }

                if (pl.mobMe != null) {
                    msg = new Message(-95);
                    msg.writer().writeByte(0);//type
                    msg.writer().writeInt((int) pl.id);
                    msg.writer().writeShort(pl.mobMe.tempId);
                    msg.writer().writeInt(pl.mobMe.gethp());// hp mob
                    player.sendMessage(msg);
                    msg.cleanup();
                }
            }
        } catch (Exception e) {
        }
    }

    public void spaceShipArrive(Player player, byte typeSpace) {
        Message msg;
        try {
            msg = new Message(-65);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeByte(typeSpace);
            Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();

        } catch (Exception e) {

        }
    }
}
