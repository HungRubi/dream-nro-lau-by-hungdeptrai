package server;
//share by chibikun

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import real.item.CaiTrangData;
import real.item.Item;
import real.item.ItemDAO;
import real.item.ItemOption;
import real.map.ItemMap;
import real.map.Map;
import real.map.MapManager;
import real.map.MapService;
import real.map.Mob;
import real.npc.Npc;
import real.map.WayPoint;
import real.pet.Pet;
import real.player.Player;
import real.player.PlayerManger;
import server.io.Session;
import real.skill.Skill;
import server.io.Message;
import server.ServiceDAO;

public class Service {

    private static Service instance;

    public static Service getInstance() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }

    public void sendServerList(Session session) {
        Message msg;
        try {
            msg = new Message(-29);
            msg.writer().writeByte(2);
            msg.writer().writeUTF("NTP 1:192.168.1.69:14445:0,NTP 2:192.168.1.69:14445:0,0,0");
            msg.writer().writeByte(1);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendMessAllPlayer(Message msg) {
        for (Player pl : PlayerManger.gI().getPlayers()) {
            if (!pl.isPet && !pl.isBoss) {
                pl.sendMessage(msg);
            }
        }
        msg.cleanup();
    }

    public void sendMessAllPlayerIgnoreMe(Player player, Message msg) {
        for (Player pl : PlayerManger.gI().getPlayers()) {
            if (!pl.isPet && !pl.isBoss && !player.equals(pl)) {
                pl.sendMessage(msg);
            }
        }
    }

    public void sendMessAllPlayerInMap(Map map, Message msg) {
        if (map != null) {
            for (Player pl : map.getPlayers()) {
                if (!pl.isPet && !pl.isBoss) {
                    pl.sendMessage(msg);
                }
            }
            msg.cleanup();
        } else {
            System.out.println("nul map");
        }
    }

    public void sendMessAnotherNotMeInMap(Player player, Message msg) {
        if (player.map != null) {
            for (Player pl : player.map.getPlayers()) {
                if (!pl.isPet && !pl.isBoss && pl != player) {
                    pl.sendMessage(msg);
                }
            }
            msg.cleanup();
        }
    }

    public void Send_Info_NV(Player pl) {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 14);//Cập nhật máu
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeInt(pl.point.getHP());
            msg.writer().writeByte(0);//Hiệu ứng Ăn Đậu
            msg.writer().writeInt(pl.point.getHPFull());
            sendMessAnotherNotMeInMap(pl, msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void loginDe(Session session, short second) {
        Message msg;
        try {
            msg = new Message(122);
            msg.writer().writeShort(second);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void requestIcon(Session session, int id) throws IOException {
        System.err.println("icon: " + id);
        Message msg;
        try {
            byte[] icon = FileIO.readFile("data/res_icon_new/x" + session.zoomLevel + "/" + id + ".png");
            msg = new Message(-67);
            msg.writer().writeInt(id);
            msg.writer().writeInt(icon.length);
            msg.writer().write(icon);
            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
//            try {
//                byte[] icon = FileIO.readFile("data/icon/thieuicon");
//                msg = new Message(-67);
//                msg.writer().writeInt(id);
//                msg.writer().writeInt(icon.length);
//                msg.writer().write(icon);
//                session.doSendMessage(msg);
//                msg.cleanup();
//            } catch (IOException ex) {
//            }
            byte[] icon = FileIO.readFile("data/icon/err");
            msg = new Message(-67);
            msg.writer().writeInt(id);
            msg.writer().writeInt(icon.length);
            msg.writer().write(icon);
            session.doSendMessage(msg);
            msg.cleanup();
        }
    }

    public void versionImageSource(Session session) {
        Message msg;
        try {
            msg = new Message(-74);
            msg.writer().writeByte(0);
            msg.writer().writeInt(5714013);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sizeImageSource(Session session) {
        Message msg;
        try {
            msg = new Message(-74);
            msg.writer().writeByte(1);
//            msg.writer().writeShort(957);
//            msg.writer().writeShort(90);
            msg.writer().writeShort(session.typeClient == 1 ? 90 : 957);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void imageSource(Session session) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg;
                try {
                    //for (final File fileEntry : new File(session.typeClient == 1 ? "data/resx1" : "data/resx2").listFiles()) {
                    for (final File fileEntry : new File("data/res_map_new/x" + session.zoomLevel).listFiles()) {
                        String original = fileEntry.getName();
                        byte[] res = FileIO.readFile(fileEntry.getAbsolutePath());
                        msg = new Message(-74);
                        msg.writer().writeByte(2);
                        msg.writer().writeUTF(original);
                        msg.writer().writeInt(res.length);
                        msg.writer().write(res);

                        session.sendMessage(msg);
                        msg.cleanup();
                        Thread.sleep(1);
                    }

                    msg = new Message(-74);
                    msg.writer().writeByte(3);
                    msg.writer().writeInt(5714013);
                    session.sendMessage(msg);
                    msg.cleanup();
                } catch (Exception e) {
                }
            }
        }).start();
    }

    public void itemBg(Session session, int id) {
        Message msg;
        try {
            byte[] item_bg = FileIO.readFile("data/map/item_bg/" + id);
            msg = new Message(-31);
            msg.writer().write(item_bg);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void bgTemp(Session session, int id) {
        Message msg;
        try {
            byte[] bg_temp = FileIO.readFile("data/bg_temp_new/x" + session.zoomLevel + "/" + id);
            msg = new Message(-32);
            msg.writer().write(bg_temp);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void effData(Session session, int id) {
        Message msg;
        try {
            byte[] eff_data = FileIO.readFile("data/eff_data_new/x" + session.zoomLevel + "/" + id);
            msg = new Message(-66);
            msg.writer().write(eff_data);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestMobTemplate(Session session, int id) {
        Message msg;
        try {
            byte[] mob = FileIO.readFile("data/res_mob_new/x" + session.zoomLevel + "/" + id);
            msg = new Message(11);
            msg.writer().write(mob);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void updateVersionx(Session session) {
        Message msg;
        try {
            msg = new Message(-77);
            msg.writer().write(FileIO.readFile("data/msg/maxSmall"));
//            int count = 9980;
//            msg.writer().writeShort(count);
//            for(int i = 0; i < count; i++){
//                msg.writer().writeByte(0);
//            }
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendMessage(Session session, int cmd, String filename) {
        Message msg;
        try {
            msg = new Message(cmd);
            msg.writer().write(FileIO.readFile("data/msg/" + filename));
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendData(Session session, int cmd, String filename) {
        Message msg;
        System.err.println("image: " + session.zoomLevel);
        try {
            msg = new Message(cmd);
            msg.writer().write(FileIO.readFile("data/data_new/" + session.zoomLevel + "/" + filename));
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void updateVersion(Session session) {
        Message msg;
        try {
            msg = messageNotMap((byte) 4);
            msg.writer().writeByte(ServerManager.vsData);
            msg.writer().writeByte(ServerManager.vsMap);
            msg.writer().writeByte(ServerManager.vsSkill);
            msg.writer().writeByte(ServerManager.vsItem);
            msg.writer().writeByte(0);

            long[] smtieuchuan = {100010000000L, 80010000000L, 70010000000L, 60010000000L, 50010000000L, 40000000000L, 10000000000L, 5000000000L, 1500000000L, 150000000L, 15000000L, 1500000L, 700000L, 340000L, 170000L, 90000L, 40000L, 15000L, 3000L, 1000L};
            msg.writer().writeByte(smtieuchuan.length);
            for (int i = 0; i < smtieuchuan.length; i++) {
                msg.writer().writeLong(smtieuchuan[i]);
            }
            session.sendMessage(msg);
            msg.cleanup();

            msg = new Message(-28);
            msg.writer().write(FileIO.readFile("data/1632811838304_-28_4_r"));
            session.sendMessage(msg);
            msg.cleanup();
            System.out.println("update ver");
        } catch (Exception e) {
        }
    }

    public void updateData(Session session) {
        Message msg;
        try {
            msg = new Message(-87);
//            msg.writer().write(FileIO.readFile("data/NRdata_v47"));
//            msg.writer().write(FileIO.readFile("data/1632811838531_-87_r"));
//
            msg.writer().writeByte(ServerManager.vsData);

            byte[] ab = FileIO.readFile("data/NR_dart");
            msg.writer().writeInt(ab.length);
            msg.writer().write(ab);
            ab = FileIO.readFile("data/NR_arrow");
            msg.writer().writeInt(ab.length);
            msg.writer().write(ab);
            ab = FileIO.readFile("data/NR_effect");
            msg.writer().writeInt(ab.length);
            msg.writer().write(ab);
            ab = FileIO.readFile("data/NR_image");
            msg.writer().writeInt(ab.length);
            msg.writer().write(ab);
            ab = FileIO.readFile("data/NR_part");
            msg.writer().writeInt(ab.length);
            msg.writer().write(ab);
            ab = FileIO.readFile("data/NR_skill");
            msg.writer().writeInt(ab.length);
            msg.writer().write(ab);

            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
        System.out.println("update data");
    }

    public void updateMap(Session session) {
        Message msg;
        try {
            //msg = messageNotMap((byte) 6);
            msg = new Message(-28);
//            msg.writer().write(FileIO.readFile("data/NRmap_v25"));
            msg.writer().write(FileIO.readFile("data/1632811838538_-28_6_r"));
            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
        System.out.println("update map");
    }

    public static void main(String[] args) throws Exception {
        DataInputStream dis = new DataInputStream(new FileInputStream("data/1632811838538_-28_6_r"));
        System.out.println(dis.readByte());
        System.out.println(dis.readByte());
        int mapSize = dis.readByte() + 256;
        for (int i = 0; i < mapSize; i++) {
//            System.out.println(i + " " + dis.readUTF());
            dis.readUTF();
        }

        int npcSize = dis.readByte();
        for (int i = 0; i < npcSize; i++) {
            System.out.println(i + " name: " + dis.readUTF());
            dis.readShort();
            dis.readShort();
            dis.readShort();
            int menu = dis.readByte();
            for (int j = 0; j < menu; j++) {
                int menuSize = dis.readByte();
                for (int k = 0; k < menuSize; k++) {
                    dis.readUTF();
                }
            }
        }
    }

    public void updateSkill(Session session) {
        Message msg;
        try {
            msg = new Message(-28);
            msg.writer().write(FileIO.readFile("data/1632811838545_-28_7_r"));
            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
        System.out.println("update skill");
    }

    public void tileSet(Player player) {
        Message msg;
        try {
            msg = new Message(-82);
            msg.writer().write(FileIO.readFile("data/map/tile_set/0"));
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

//    public static void main(String[] args) throws Exception {
//        DataInputStream dis = new DataInputStream(new FileInputStream("data/map/tile_set/0"));
//        int countTileId = dis.readByte();
//        System.out.println("count tile id: " + countTileId);
//
//        for (int i = 1; i <= countTileId; i++) {
//            int countTile = dis.readByte();
//            for (int j = 0; j < countTile; j++) {
//                int tileType = dis.readInt();
//                int countTileType = dis.readByte();
//
//                if (i == 2) {
//                    System.out.println(tileType);
//                }
//
//                for (int k = 0; k < countTileType; k++) {
//                    int tileIndex = dis.readByte();
////                    System.out.println(dis.readByte());
//                }
//            }
//        }
//    }
    public void mapInfo(Player pl) {
        Message msg;
        try {
            Map map = pl.map;
            msg = new Message(-24);
            msg.writer().writeByte(map.id);
            msg.writer().writeByte(map.planetId);
            msg.writer().writeByte(map.tileId);
            msg.writer().writeByte(map.bgId);
            msg.writer().writeByte(map.type);
            msg.writer().writeUTF(map.name);
            msg.writer().writeByte(map.zoneId);

            msg.writer().writeShort(pl.x);
            msg.writer().writeShort(pl.y);

            // waypoint
            ArrayList<WayPoint> wayPoints = map.wayPoints;
            msg.writer().writeByte(wayPoints.size());
            for (WayPoint wp : wayPoints) {
                msg.writer().writeShort(wp.minX);
                msg.writer().writeShort(wp.minY);
                msg.writer().writeShort(wp.maxX);
                msg.writer().writeShort(wp.maxY);
                msg.writer().writeBoolean(wp.isEnter);
                msg.writer().writeBoolean(wp.isOffline);
                msg.writer().writeUTF(wp.name);
            }
            // mob
            ArrayList<Mob> mobs = map.mobs;
            msg.writer().writeByte(mobs.size());
            for (Mob mob : mobs) {
                msg.writer().writeBoolean(false); //is disable
                msg.writer().writeBoolean(false); //is dont move
                msg.writer().writeBoolean(false); //is fire
                msg.writer().writeBoolean(false); //is ice
                msg.writer().writeBoolean(false); //is wind
                msg.writer().writeByte(mob.tempId);
                msg.writer().writeByte(0);
                msg.writer().writeInt(mob.gethp());
                msg.writer().writeByte(mob.level);
                msg.writer().writeInt((mob.getHpFull()));
                msg.writer().writeShort(mob.pointX);
                msg.writer().writeShort(mob.pointY);
                msg.writer().writeByte(5);
                msg.writer().writeByte(0);
                msg.writer().writeBoolean(false);
            }

            msg.writer().writeByte(0);

            // npc
            List<Npc> npcs = map.npcs;
            msg.writer().writeByte(npcs.size());
            for (Npc npc : npcs) {
                msg.writer().writeByte(npc.status);
                msg.writer().writeShort(npc.cx);
                msg.writer().writeShort(npc.cy);
                msg.writer().writeByte(npc.tempId);
                msg.writer().writeShort(npc.avartar);
            }

            msg.writer().writeByte(map.items.size());
            for (ItemMap it : map.items) {
                msg.writer().writeShort(it.itemMapId);
                msg.writer().writeShort(it.itemTemplate.id);
                msg.writer().writeShort(it.x);
                msg.writer().writeShort(it.y);
                msg.writer().writeInt((int) it.playerId);
//                msg.writer().writeInt((int) it.);
            }

            // bg item
            try {
                byte[] bgItem = FileIO.readFile("data/map/bg/" + map.id);
                msg.writer().write(bgItem);
            } catch (Exception e) {
                msg.writer().writeShort(0);
            }

            // eff item
            try {
                byte[] effItem = FileIO.readFile("data/map/eff/" + map.id);
                msg.writer().write(effItem);
            } catch (Exception e) {
                msg.writer().writeShort(0);
            }

            msg.writer().writeByte(map.bgType);
            msg.writer().writeByte(pl.getUseSpaceShip());
            msg.writer().writeByte(0);
            pl.sendMessage(msg);

            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removePlayer(Session session, Player player) {
        Message msg;
        try {
            msg = new Message(-6);
            msg.writer().writeInt((int) player.id);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void resetPoint(Player player, int x, int y) {
        Message msg;
        try {
            player.x = x;
            player.y = y;
            msg = new Message(46);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            player.sendMessage(msg);
            msg.cleanup();

        } catch (Exception e) {
        }
    }

    public void login2(Session session, String user) {
        Message msg;
        try {
            msg = new Message(-101);
            msg.writer().writeUTF(user);
            session.sendMessage(msg);
            msg.cleanup();

        } catch (Exception e) {
        }
    }

    public void mapTemp(Session session, int id) {
        Message msg;
        try {
            msg = new Message(-28);
            msg.writer().write(FileIO.readFile("data/map/temp/" + id));
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void updateBag(Session session) {
        Message msg;
        try {
            msg = new Message(-64);
            msg.writer().writeInt(0);// id char
            msg.writer().writeByte(0);// id bag
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void updateSloganClan(Session session, String text) {
        //ClanDB.updateSlogan(session.player.clanId, text);
        Message msg;
        try {
            msg = new Message(-46);
            msg.writer().writeByte(4);
            msg.writer().writeByte(0);
            msg.writer().writeUTF(text);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void clearMap(Player player) {
        Message msg;
        try {
            msg = new Message(-22);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void chat(Player pl, String text) {
        Message msg;
        try {
            msg = new Message(44);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeUTF(text);
            sendMessAllPlayerInMap(pl.map, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void chatJustForMe(Player me, Player plChat, String text) {
        Message msg;
        try {
            msg = new Message(44);
            msg.writer().writeInt((int) plChat.id);
            msg.writer().writeUTF(text);
            me.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void playerMove(Session session, Player pl) {
        Message msg;
        try {
            msg = new Message(-7);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeShort(pl.x);
            msg.writer().writeShort(pl.y);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void stamina(Session session) {
        Message msg;
        try {
            msg = new Message(-68);
            msg.writer().writeShort(10000);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void maxStamina(Session session) {
        Message msg;
        try {
            msg = new Message(-69);
            msg.writer().writeShort(10000);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void activePoint(Session session) {
        Message msg;
        try {
            msg = new Message(-97);
            msg.writer().writeInt(1000);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long exp_level1(long sucmanh) {
        if (sucmanh < 3000) {
            return 3000;
        } else if (sucmanh < 15000) {
            return 15000;
        } else if (sucmanh < 40000) {
            return 40000;
        } else if (sucmanh < 90000) {
            return 90000;
        } else if (sucmanh < 170000) {
            return 170000;
        } else if (sucmanh < 340000) {
            return 340000;
        } else if (sucmanh < 700000) {
            return 700000;
        } else if (sucmanh < 1500000) {
            return 1500000;
        } else if (sucmanh < 15000000) {
            return 15000000;
        } else if (sucmanh < 150000000) {
            return 150000000;
        } else if (sucmanh < 1500000000) {
            return 1500000000;
        } else if (sucmanh < 5000000000L) {
            return 5000000000L;
        } else if (sucmanh < 10000000000L) {
            return 10000000000L;
        } else if (sucmanh < 40000000000L) {
            return 40000000000L;
        } else if (sucmanh < 50010000000L) {
            return 50010000000L;
        } else if (sucmanh < 60010000000L) {
            return 60010000000L;
        } else if (sucmanh < 70010000000L) {
            return 70010000000L;
        } else if (sucmanh < 80010000000L) {
            return 80010000000L;
        } else if (sucmanh < 100010000000L) {
            return 100010000000L;
        }
        return 1000;
    }

    public void point(Player pl) {
        if (pl.isPet) {
//            Send_Info_NV(pl);
            return;
        }
        Message msg;
        try {
            Util.log("Đã load nv");
            msg = new Message(-42);
            msg.writer().writeInt(pl.point.hpGoc);
            msg.writer().writeInt(pl.point.mpGoc);
            msg.writer().writeInt(pl.point.dameGoc);
            msg.writer().writeInt(pl.point.getHPFull());// hp full
            msg.writer().writeInt(pl.point.getMPFull());// mp full
            msg.writer().writeInt(pl.point.getHP());// hp
            msg.writer().writeInt(pl.point.getMP());// mp
            msg.writer().writeByte(pl.point.getSpeed());// speed
            msg.writer().writeByte(20);
            msg.writer().writeByte(20);
            msg.writer().writeByte(1);
            msg.writer().writeInt(pl.point.getBaseDame());// dam base
            msg.writer().writeInt(pl.point.getDefFull());// def full
            msg.writer().writeByte(pl.point.getCritFull());// crit full
            msg.writer().writeLong(pl.point.tiemNang);
            msg.writer().writeShort(100);
            msg.writer().writeShort(pl.point.defGoc);
            msg.writer().writeByte(pl.point.critGoc);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void player(Session session, Player pl) {
        Message msg;
        try {
            msg = messageSubCommand((byte) 0);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeByte(pl.taskId);
            msg.writer().writeByte(pl.gender);
            msg.writer().writeShort(pl.head);
            msg.writer().writeUTF(pl.name);
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            msg.writer().writeLong(pl.point.power);
            msg.writer().writeShort(0);
            msg.writer().writeShort(0);
            msg.writer().writeByte(pl.gender);
            //--------skill---------

            ArrayList<Skill> skills = (ArrayList<Skill>) pl.playerSkill.skills;

            msg.writer().writeByte(skills.size());

            for (Skill skill : skills) {
                msg.writer().writeShort(skill.skillId);
            }

            //---vang---luong--luongKhoa
            msg.writer().writeInt(pl.inventory.gold);
            msg.writer().writeInt(pl.inventory.ruby);
            msg.writer().writeInt(pl.inventory.gem);

            //--------itemBody---------
            ArrayList<Item> itemsBody = (ArrayList<Item>) pl.inventory.itemsBody;
            msg.writer().writeByte(itemsBody.size());
            for (Item item : itemsBody) {
                if (item.id == -1) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    ArrayList<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeShort(itemOption.param);
                    }
                }

            }

            //--------itemBag---------
            ArrayList<Item> itemsBag = (ArrayList<Item>) pl.inventory.itemsBag;
            msg.writer().writeByte(itemsBag.size());
            for (int i = 0; i < itemsBag.size(); i++) {
                Item item = itemsBag.get(i);
                if (item.id == -1) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    ArrayList<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeShort(itemOption.param);
                    }
                }

            }

            //--------itemBox---------
            ArrayList<Item> itemsBox = (ArrayList<Item>) pl.inventory.itemsBox;
            msg.writer().writeByte(itemsBox.size());
            for (int i = 0; i < itemsBox.size(); i++) {
                Item item = itemsBox.get(i);
                if (item.id == -1) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    ArrayList<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeShort(itemOption.param);
                    }
                }
            }
            //-----------------
//            msg.writer().write(FileIO.readFile("data/head"));
            msg.writer().writeShort(CaiTrangData.avatars.size());
            for (CaiTrangData.Avatar avatar : CaiTrangData.avatars) {
                msg.writer().writeShort(avatar.getidHead());
                msg.writer().writeShort(avatar.getidAvatar());
            }
            //-----------------
            msg.writer().writeShort(514);
            msg.writer().writeShort(515);
            msg.writer().writeShort(537);
            msg.writer().writeByte(0);
            msg.writer().writeInt(1632811835);
            msg.writer().writeByte(0);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            System.out.print("info player: ");
            e.printStackTrace();
        }
    }

    private Message messageNotLogin(byte command) throws IOException {
        Message ms = new Message(-29);
        ms.writer().writeByte(command);
        return ms;
    }

    private Message messageNotMap(byte command) throws IOException {
        Message ms = new Message(-28);
        ms.writer().writeByte(command);
        return ms;
    }

    public Message messageSubCommand(byte command) throws IOException {
        Message ms = new Message(-30);
        ms.writer().writeByte(command);
        return ms;
    }

    public void congTiemNang(Player pl, byte type, int tiemnang) {
        Message msg;
        try {
            msg = new Message(-3);
            msg.writer().writeByte(type);// 0 là cộng sm, 1 cộng tn, 2 là cộng cả 2
            msg.writer().writeInt(tiemnang);// số tn cần cộng
            if (!pl.isPet) {
                pl.sendMessage(msg);
            } else {
                ((Pet) pl).master.point.powerUp(tiemnang);
                ((Pet) pl).master.point.tiemNangUp(tiemnang);
                ((Pet) pl).master.sendMessage(msg);
            }
            msg.cleanup();
            switch (type) {
                case 1:
                    pl.point.tiemNangUp(tiemnang);
                    break;
                case 2:
                    pl.point.powerUp(tiemnang);
                    pl.point.tiemNangUp(tiemnang);
                    break;
                default:
                    pl.point.powerUp(tiemnang);
                    break;
            }
        } catch (Exception e) {
        }
    }

    public String get_HanhTinh(int hanhtinh) {
        switch (hanhtinh) {
            case 0:
                return "Namek";

            case 1:
                return "Trái Đất";

            default:
                return "Xayda";
        }
    }

    public String exp_level(Player pl) {
        long sucmanh = pl.point.getPower();

        if (sucmanh < 3000) {
            return "Tân thủ";
        } else if (sucmanh < 15000) {
            return "Tập sự sơ cấp";
        } else if (sucmanh < 40000) {
            return "Tập sự trung cấp";
        } else if (sucmanh < 90000) {
            return "Tập sự cao cấp";
        } else if (sucmanh < 170000) {
            return "Tân binh";
        } else if (sucmanh < 340000) {
            return "Chiến binh";
        } else if (sucmanh < 700000) {
            return "Chiến binh cao cấp";
        } else if (sucmanh < 1500000) {
            return "Vệ binh";
        } else if (sucmanh < 15000000) {
            return "Vệ binh hoàng gia";
        } else if (sucmanh < 150000000) {
            return "Siêu " + get_HanhTinh(pl.gender) + " cấp 1";
        } else if (sucmanh < 1500000000) {
            return "Siêu " + get_HanhTinh(pl.gender) + " cấp 2";
        } else if (sucmanh < 5000000000L) {
            return "Siêu " + get_HanhTinh(pl.gender) + " cấp 3";
        } else if (sucmanh < 10000000000L) {
            return "Siêu " + get_HanhTinh(pl.gender) + " cấp 4";
        } else if (sucmanh < 40000000000L) {
            return "Thần " + get_HanhTinh(pl.gender) + " cấp 1";
        } else if (sucmanh < 50010000000L) {
            return "Thần " + get_HanhTinh(pl.gender) + " cấp 2";
        } else if (sucmanh < 60010000000L) {
            return "Thần " + get_HanhTinh(pl.gender) + " cấp 3";
        } else if (sucmanh < 70010000000L) {
            return "Giới Vương Thần cấp 11";
        } else if (sucmanh < 80010000000L) {
            return "Giới Vương Thần cấp 2";
        } else if (sucmanh < 100010000000L) {
            return "Giới Vương Thần cấp 3";
        } else if (sucmanh < 11100010000000L) {
            return "Thần Huỷ Diệt cấp 1";
        }
        return "Thần Huỷ Diệt cấp 2";
    }

    public int get_clevel(long sucmanh) {
        long[] smtieuchuan = {100010000000L, 80010000000L, 70010000000L, 60010000000L, 50010000000L, 40000000000L, 10000000000L, 5000000000L, 1500000000L, 150000000L, 15000000L, 1500000L, 700000L, 340000L, 170000L, 90000L, 40000L, 15000L, 3000L, 1000L};
        int clevel = 0;
        for (int i = 0; i < smtieuchuan.length; i++) {
            if ((sucmanh * 1L) >= smtieuchuan[i]) {
                clevel = ((smtieuchuan.length) - 1) - i;
                break;
            }
        }
        return clevel;
    }

    public void hsChar(Player pl, int hp, int mp) {
        Message msg;
        try {
            pl.setJustRevivaled();

//            pl.point.updateall();
            pl.point.setHpMp(hp, mp);
            if (!pl.isPet) {
                msg = new Message(-16);
                pl.sendMessage(msg);
                msg.cleanup();
                pl.sendInfo();
            }

            msg = messageSubCommand((byte) 15);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeInt(hp);
            msg.writer().writeInt(mp);
            msg.writer().writeShort(pl.x);
            msg.writer().writeShort(pl.y);
            sendMessAllPlayerInMap(pl.map, msg);
            msg.cleanup();

            Send_Info_NV(pl);
            pl.sendInfoHPMP();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void charDie(Player pl) {
        Message msg;
        try {
            pl.point.setHP(0);
            if (!pl.isPet) {
                msg = new Message(-17);
                msg.writer().writeByte((int) pl.id);
                msg.writer().writeShort(pl.x);
                msg.writer().writeShort(pl.y);
                pl.sendMessage(msg);
                msg.cleanup();
            } else {
                ((Pet) pl).lastTimeDie = System.currentTimeMillis();
            }

            msg = new Message(-8);
            msg.writer().writeShort((int) pl.id);
            msg.writer().writeByte(0); //cpk
            msg.writer().writeShort(pl.x);
            msg.writer().writeShort(pl.y);
            sendMessAnotherNotMeInMap(pl, msg);
            msg.cleanup();

            Send_Info_NV(pl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void attackMob(Player pl, int mobId) {
        pl.playerSkill.useSkill(null, pl.map.mobs.get(mobId));
    }

    public void Send_Caitrang(Player player) {
        Message msg;
        try {
//            if (player.itemsBody.get(5).id != -1) {
            msg = new Message(-90);
            msg.writer().writeByte(1);// check type
            msg.writer().writeInt((int) player.id); //id player
            short head = player.getHead();
            short body = player.getBody();
            short leg = player.getLeg();

            msg.writer().writeShort(head);//set head
            msg.writer().writeShort(body);//setbody
            msg.writer().writeShort(leg);//set leg
            msg.writer().writeByte(player.playerSkill.isMonkey ? 1 : 0);//set khỉ
            sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Send_Caitrang(Player player, int id) {
        Message msg;
        try {
//            if (player.itemsBody.get(5).id != -1) {
            msg = new Message(-90);
            msg.writer().writeByte(1);// check type
            msg.writer().writeInt((int) player.id); //id player
            short head = (short)id;
            short body = (short)(id+1);
            short leg = (short)(id+2);

            msg.writer().writeShort(head);//set head
            msg.writer().writeShort(body);//setbody
            msg.writer().writeShort(leg);//set leg
            msg.writer().writeByte(player.playerSkill.isMonkey ? 1 : 0);//set khỉ
            sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void sendThongBaoOK(Player pl, String text) {
        if (pl.isPet) {
            return;
        }
        Message msg;
        try {
            msg = new Message(-26);
            msg.writer().writeUTF(text);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendThongBaoOK(Session session, String text) {
        Message msg;
        try {
            msg = new Message(-26);
            msg.writer().writeUTF(text);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendThongBao(Player pl, String thongBao) {
        Message msg;
        try {
            msg = new Message(-25);
            msg.writer().writeUTF(thongBao);
            pl.sendMessage(msg);
            msg.cleanup();

        } catch (Exception e) {
        }
    }

    public void sendMoney(Player pl) {
        Message msg;
        try {
            msg = new Message(6);
            msg.writer().writeInt(pl.inventory.gold);
            msg.writer().writeInt(pl.inventory.gem);
            msg.writer().writeInt(pl.inventory.ruby);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void pickItem(Player pl, int itemMapId) {
        Message msg;
        Util.log(""+itemMapId);
        if(pl.map.id == (21+pl.gender) && itemMapId == 1){
            try {
                msg = new Message(-20);
                msg.writer().writeShort(itemMapId);
                msg.writer().writeUTF("Bạn nhận được Đùi gà nướng");
                msg.writer().writeShort(1);
                if (isItemMoney(27)) {
                    msg.writer().writeShort(1);
                }
                pl.sendMessage(msg);
                msg.cleanup();
                pl.hoiphuc(pl.point.getHPFull(), pl.point.getMPFull());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        boolean picked = false;
        ItemMap itm = null;
        for (ItemMap it : pl.map.items) {
            if (it.itemMapId == itemMapId) {
                if ((it.playerId == pl.id || it.playerId == -1)) {
                    itm = it;
                    Item item = new Item();
                    item.quantity = it.quantity;
                    item.template = it.itemTemplate;
                    item.content = item.getContent();
                    item.itemOptions = (ArrayList<ItemOption>) it.options;
                    item.id = ItemDAO.create(item.template.id, item.itemOptions);
                    picked = pl.inventory.addItemBag(item);
                } else {
                    sendThongBao(pl, "Không thể nhặt vật phẩm của người khác!");
                }
                break;
            }
        }
        if (picked) {
            pl.inventory.sendItemBags();

            
            try {
                msg = new Message(-20);
                msg.writer().writeShort(itemMapId);
                msg.writer().writeUTF("Bạn đã nhặt được " + itm.itemTemplate.name + "!");
                msg.writer().writeShort(itm.quantity);
                if (isItemMoney(itm.itemTemplate.type)) {
                    msg.writer().writeShort(itm.quantity);
                }
                pl.sendMessage(msg);
                msg.cleanup();
                sendToAntherMePickItem(pl, itemMapId);
                ItemMap.removeItemMap(itm);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void sendToAntherMePickItem(Player player, int itemMapId) {
        Message msg;
        try {
            msg = new Message(-19);
            msg.writer().writeShort(itemMapId);
            msg.writer().writeInt((int) player.id);
            for (Player pl : player.map.getPlayers()) {
                if (pl != player) {
                    pl.sendMessage(msg);
                }
            }
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public boolean isItemMoney(int type) {
        return type == 9 || type == 10 || type == 34;
    }

    public static String numberToMoney(long power) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat num = NumberFormat.getInstance(locale);
        num.setMaximumFractionDigits(1);
        if (power >= 1000000000) {
            return num.format((double) power / 1000000000) + " Tỷ";
        } else if (power >= 1000000) {
            return num.format((double) power / 1000000) + " Tr";
        } else if (power >= 1000) {
            return num.format((double) power / 1000) + " k";
        } else {
            return num.format(power);
        }
    }

    public void useSkillNotFocus(Player pl, int status) {
        pl.playerSkill.useSkill(null, null);
        lastTimeUseThisSkill(pl);
    }

    public void chatGlobal(Player pl, String text) {
        boolean canChat = false;
        int nxanh = pl.inventory.gem;
        int ndo = pl.inventory.ruby;
        if (ndo + xanhToDo(nxanh) >= 15) {
            canChat = true;
        }
        if (!ServiceDAO.logGlobalChat(pl, text)) {
            canChat = false;
        }
        if (canChat) {
            pl.inventory.ruby -= 15;
            if (pl.inventory.ruby < 0) {
                pl.inventory.gem -= doToXanh(-pl.inventory.ruby);
                pl.inventory.ruby = 0;
            }
            sendMoney(pl);

            Message msg;
            try {
                msg = new Message(92);
                msg.writer().writeUTF(pl.name);
                msg.writer().writeUTF("|5|" + text);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeShort(pl.getHead());
                msg.writer().writeShort(pl.getBody());
                msg.writer().writeShort(-1); //bag
                msg.writer().writeShort(pl.getLeg());
                msg.writer().writeByte(0);
                sendMessAllPlayer(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    private int tiLeXanhDo = 3;

    public int xanhToDo(int n) {
        return n * tiLeXanhDo;
    }

    public int doToXanh(int n) {
        return (int) n / tiLeXanhDo;
    }

    public static final int[] flagTempId = {363, 364, 365, 366, 367, 368, 369, 370, 371, 519, 520, 747};
    public static final int[] flagIconId = {2761, 2330, 2323, 2327, 2326, 2324, 2329, 2328, 2331, 4386, 4385, 2325};

    public void openFlagUI(Player pl) {
        Message msg;
        try {
            msg = new Message(-103);
            msg.writer().writeByte(0);
            msg.writer().writeByte(flagTempId.length);
            for (int i = 0; i < flagTempId.length; i++) {
                msg.writer().writeShort(flagTempId[i]);
                msg.writer().writeByte(1);
                switch (flagTempId[i]) {
                    case 363:
                        msg.writer().writeByte(73);
                        msg.writer().writeShort(0);
                        break;
                    case 371:
                        msg.writer().writeByte(88);
                        msg.writer().writeShort(10);
                        break;
                    default:
                        msg.writer().writeByte(88);
                        msg.writer().writeShort(5);
                        break;
                }
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void chooseFlag(Player pl, int index) {
        if (System.currentTimeMillis() - pl.lastTimeChangeFlag > 60000) {
            Message msg;
            try {
                pl.cFlag = (byte) index;
                msg = new Message(-103);
                msg.writer().writeByte(1);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeByte(index);
                Service.getInstance().sendMessAllPlayerInMap(pl.map, msg);
                msg.cleanup();

                msg = new Message(-103);
                msg.writer().writeByte(2);
                msg.writer().writeByte(index);
                msg.writer().writeShort(flagIconId[index]);
                Service.getInstance().sendMessAllPlayer(msg);
                msg.cleanup();

                if (pl.pet != null) {
                    pl.pet.cFlag = (byte) index;
                    msg = new Message(-103);
                    msg.writer().writeByte(1);
                    msg.writer().writeInt((int) pl.pet.id);
                    msg.writer().writeByte(index);
                    Service.getInstance().sendMessAllPlayerInMap(pl.pet.map, msg);
                    msg.cleanup();

                    msg = new Message(-103);
                    msg.writer().writeByte(2);
                    msg.writer().writeByte(index);
                    msg.writer().writeShort(flagIconId[index]);
                    Service.getInstance().sendMessAllPlayerInMap(pl.pet.map, msg);
                    msg.cleanup();
                }
                pl.lastTimeChangeFlag = System.currentTimeMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            sendThongBao(pl, "Không thể đổi cờ lúc này! Vui lòng đợi " + (60 - ((int) (System.currentTimeMillis() - pl.lastTimeChangeFlag) / 1000)) + " giây nữa!");
        }
    }

    public void attackPlayer(Player pl, int idPlAnPem) {
        pl.playerSkill.useSkill(pl.map.getPlayerInMap(idPlAnPem), null);
    }

    private void attPlayer(Player pl, int idPlAnPem) {
        Message msg;
        Player plAnPem = pl.map.getPlayerInMap(idPlAnPem);
        if (plAnPem == null) {
            return;
        }
        switch (pl.playerSkill.skillSelect.template.id) {
            case 7:
                plAnPem.hoiphuc(plAnPem.point.getHPFull() / 100 * 50, plAnPem.point.getMPFull() / 100 * 50);
                chat(plAnPem, "Cảm ơn " + pl.name + " đã cứu mình");
                pl.setMPUseSkill();
                try {
                    msg = Service.getInstance().messageSubCommand((byte) 14);//Cập nhật máu
                    msg.writer().writeInt((int) plAnPem.id);
                    msg.writer().writeInt(plAnPem.point.getHP());
                    msg.writer().writeByte(1);//Hiệu ứng Ăn Đậu
                    msg.writer().writeInt(plAnPem.point.getHPFull());
                    sendMessAllPlayerInMap(plAnPem.map, msg);
                    msg.cleanup();
                } catch (Exception e) {
                }
                break;
            default:
                try {
                    boolean phanSatThuong = false;
                    if (phanSatThuong) {
                        msg = new Message(56);
                        msg.writer().writeInt((int) pl.id);
                        int hpNguocLai = -pl.injured(null, 100, true);
                        msg.writer().writeInt(pl.point.getHP());
                        msg.writer().writeInt(hpNguocLai);
                        msg.writer().writeBoolean(false); //crit
                        msg.writer().writeByte(36); //hiệu ứng pst
                        Service.instance.sendMessAllPlayerInMap(plAnPem.map, msg);
                        msg.cleanup();
                    }

                    msg = new Message(-60);
                    msg.writer().writeInt((int) pl.id); //id pem
                    msg.writer().writeByte(pl.playerSkill.skillSelect.skillId); //skill pem
                    msg.writer().writeByte(1); //số người pem
                    msg.writer().writeInt((int) plAnPem.id); //id ăn pem

                    msg.writer().writeByte(1); //read continue
                    msg.writer().writeByte(0); //type skill

                    int dameHit = 3;
                    msg.writer().writeInt(dameHit); //dame ăn
                    msg.writer().writeBoolean(false); //is die
                    msg.writer().writeBoolean(pl.point.isCrit); //crit
                    Service.getInstance().sendMessAllPlayerInMap(plAnPem.map, msg);
                    msg.cleanup();
                    plAnPem.sendInfo();
                    if (plAnPem.point.getHP() <= 0) {
                        Service.getInstance().charDie(plAnPem);
                    }

                } catch (Exception e) {
                }
                break;

        }
    }

    public void openZoneUI(Player pl) {
        if (pl.map.id >= 45 && pl.map.id <= 47) {
            return;
        }
        if (pl.map.id == 21 || pl.map.id == 22 || pl.map.id == 23) {
            sendThongBaoOK(pl, "Không thể đổi khu vực trong map này");
            return;
        }
        Message msg;
        try {
            msg = new Message(29);
            List<Map> maps = MapManager.gI().getMapById(pl.map.id);
            msg.writer().writeByte(maps.size());
            for (Map map : maps) {
                msg.writer().writeByte(map.zoneId);
                int numPlayers = map.getNumPlayerInMap();
                msg.writer().writeByte((numPlayers < 5 ? 0 : (numPlayers < 8 ? 1 : 2)));
                msg.writer().writeByte(numPlayers);
                msg.writer().writeByte(map.maxPlayer);
                msg.writer().writeByte(0);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void requestChangeZone(Player pl, int zoneId) {
        if (pl.map.id == 21 || pl.map.id == 22 || pl.map.id == 23) {
            sendThongBaoOK(pl, "Không thể thực hiện");
            return;
        }
        Map map = MapManager.gI().getMap(pl.map.id, zoneId);
        if (map.getNumPlayerInMap() >= map.maxPlayer) {
            sendThongBaoOK(pl, "Khu vực đã đầy");
            return;
        }
        if (pl.playerSkill.isCharging) {
            pl.playerSkill.stopCharge();
        }
        if (pl.playerSkill.useTroi) {
            pl.playerSkill.removeUseTroi();
        }
        pl.gotoMap(map);
//        Service.getInstance().clearMap(pl);
//        Service.getInstance().tileSet(pl, map.id);
        mapInfo(pl);
        MapService.gI().joinMap(pl, map);
        MapService.gI().loadAnotherPlayers(pl, pl.map);
    }

    public void lastTimeUseThisSkill(Player pl) {
        Message msg;
        try {
            msg = new Message(-94);
            for (Skill skill : pl.playerSkill.skills) {
                msg.writer().writeShort(skill.skillId);
                msg.writer().writeInt(0);
            }
            pl.sendMessage(msg);
//            pl.point.setMP(pl.point.getMPFull());
//            point(pl);
            msg.cleanup();

        } catch (Exception e) {
        }
    }

    public void roiItem(Player pl, ItemMap item) {
        pl.map.addItem(item);
        Message msg;
        try {
            msg = new Message(68);
            msg.writer().writeShort(item.itemMapId);
            msg.writer().writeShort(item.itemTemplate.id);
            msg.writer().writeShort(item.x);
            msg.writer().writeShort(item.y);
            msg.writer().writeInt(3);//
            sendMessAllPlayerInMap(pl.map, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void showInfoPet(Player pl) {
        Message msg;
        try {
            msg = new Message(-107);
            msg.writer().writeByte(2);
            msg.writer().writeShort(pl.pet.getAvatar());
            msg.writer().writeByte(pl.pet.inventory.itemsBody.size());

            for (Item item : pl.pet.inventory.itemsBody) {
                if (item.id == -1) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());

                    int countOption = item.itemOptions.size();
                    msg.writer().writeByte(countOption);
                    for (ItemOption iop : item.itemOptions) {
                        msg.writer().writeByte(iop.optionTemplate.id);
                        msg.writer().writeShort(iop.param);
                    }
                }
            }

            msg.writer().writeInt(pl.pet.point.getHP()); //hp
            msg.writer().writeInt(pl.pet.point.getHPFull()); //hpfull
            msg.writer().writeInt(pl.pet.point.getMP()); //mp
            msg.writer().writeInt(pl.pet.point.getMPFull()); //mpfull
            msg.writer().writeInt(pl.pet.point.getBaseDame()); //damefull
            msg.writer().writeUTF(pl.pet.name); //name
            msg.writer().writeUTF("Siêu thần cấp 1"); //curr level
            msg.writer().writeLong(pl.pet.point.getPower()); //power
            msg.writer().writeLong(pl.pet.point.getTiemNang()); //tiềm năng
            msg.writer().writeByte(pl.pet.getStatus()); //status
            msg.writer().writeShort(1000); //stamina
            msg.writer().writeShort(1000); //stamina full
            msg.writer().writeByte(pl.pet.point.getCritFull()); //crit
            msg.writer().writeShort(pl.pet.point.getDefFull()); //def
            int sizeSkill = pl.pet.playerSkill.skills.size();
            msg.writer().writeByte(sizeSkill + (4 - sizeSkill)); //counnt pet skill
            for (Skill skill : pl.pet.playerSkill.skills) {
                msg.writer().writeShort(skill.skillId);
            }
            if (sizeSkill < 2) {
                msg.writer().writeShort(-1);
                msg.writer().writeUTF("Cần đạt sức mạnh 150tr để mở");
            }
            if (sizeSkill < 3) {
                msg.writer().writeShort(-1);
                msg.writer().writeUTF("Cần đạt sức mạnh 1tỷ5 để mở");
            }
            if (sizeSkill < 4) {
                msg.writer().writeShort(-1);
                msg.writer().writeUTF("Cần đạt sức mạnh tối thượng\nđể mở");
            }

            pl.sendMessage(msg);
            msg.cleanup();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendItemTime(Player pl, int itemId, int time) {
        Message msg;
        try {
            msg = new Message(-106);
            msg.writer().writeShort(itemId);
            msg.writer().writeShort(time);
            pl.sendMessage(msg);
        } catch (Exception e) {
        }
    }

    public void removeItemTime(Player pl, int itemTime) {
        sendItemTime(pl, itemTime, 0);
    }

    public void sendSpeedPlayer(Player pl, int speed) {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 8);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeByte(speed != -1 ? speed : pl.point.getSpeed());
            pl.sendMessage(msg);
//            Service.getInstance().sendMessAllPlayerInMap(pl.map, msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendTask(Player pl) {
        Message msg;
        try {
            msg = new Message(40);
            msg.writer().writeShort(27); //taskId
            msg.writer().writeByte(1); //taskIndex
            msg.writer().writeUTF("Chạm trán Rôbốt Sát Thủ lần 1"); //taskName
            msg.writer().writeUTF("Hãy đến tp phía nam\nđảo balê hoặc cao nguyên\nCùng 2 đồng bang diệt 900 Xên con cấp 3\nBáo với Bun ma tương lai\n" +
                    "Thưởng 1 Tr sức mạnh\n" +
                    "Thưởng 1 Tr tiềm năng"); //taskDesciption

            String[] listTaskName = {"Đến điểm hẹn tìm Rôbốt Sát thủ", "Tiêu diệt số 2 (Android 19)", "...", "...", "..."};
            String[] listTaskDescription = {"Bọn chúng sẽ xuất hiện 1 trong 3 địa điểm trên", "Bọn Rôbốt sát thủ kìa", "", "", ""};
            byte[] task = {-1, -1, 5, 5, 5};
            short[] mapTasks = {93, 94, 0, 0, 0};
            short count = 0;
            short[] listCount = {-1, -1, -1, -1, -1};

            msg.writer().writeByte(listTaskName.length);
            for (int i = 0; i < listTaskName.length; i ++) {
                msg.writer().writeUTF(listTaskName[i]);
                msg.writer().writeByte(task[i]);
                msg.writer().writeShort(mapTasks[i]);
                msg.writer().writeUTF(listTaskDescription[i]);
            }

            msg.writer().writeShort(count);
            for (int i = 0; i < listTaskName.length; i ++) {
                msg.writer().writeShort(listCount[i]);
            }

            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void setPos(Player player, int x, int y) {
        Message msg;
        try {
            msg = new Message(123);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            msg.writer().writeByte(1);
            sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendInfoChar30c4(Player player) {
        Message msg;
        try {
            msg = messageSubCommand((byte) 4);
            msg.writer().writeInt(player.inventory.gold);
            msg.writer().writeInt(player.inventory.ruby);
            msg.writer().writeInt(player.point.getHP());
            msg.writer().writeInt(player.point.getMP());
            msg.writer().writeInt(player.inventory.gem);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void changeTypePK(Player player, int type) {
        player.typePk = (byte) type;
        Message msg;
        try {
            msg = messageSubCommand((byte) 35);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeByte(type);
            sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void getPlayerMenu(Player player, int playerId) {
        Message msg;
        try {
            msg = new Message(-79);
            Player pl = player.map.getPlayerInMap(playerId);
            if (pl != null) {
                msg.writer().writeInt(playerId);
                msg.writer().writeLong(pl.point.getPower());
                msg.writer().writeUTF(Service.getInstance().exp_level(pl));
            } else {

            }
            player.sendMessage(msg);
            msg.cleanup();

            /* msg = messageSubCommand((byte) 63);
            msg.writer().writeByte(1);
            msg.writer().writeUTF("String 1");
            msg.writer().writeUTF("String 2");
            msg.writer().writeShort(4);
            player.sendMessage(msg);
            msg.cleanup();
             */
        } catch (Exception e) {

        }
    }

    public void hideInfoDlg(Player pl) {
        Message msg;
        try {
            msg = new Message(-99);
            msg.writer().writeByte(-1);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendThongBaoBenDuoi(String text) {
        Message msg;
        try {
            msg = new Message(93);
            msg.writer().writeUTF(text);
            sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void chatPrivate(Player plChat, Player plReceive, String text) {
        Message msg;
        try {
            msg = new Message(92);
            msg.writer().writeUTF(plChat.name);
            msg.writer().writeUTF("|5|" + text);
            msg.writer().writeInt((int) plChat.id);
            msg.writer().writeShort(plChat.getHead());
            msg.writer().writeShort(plChat.getBody());
            msg.writer().writeShort(-1); //bag
            msg.writer().writeShort(plChat.getLeg());
            msg.writer().writeByte(1);
            plChat.sendMessage(msg);
            plReceive.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    void serverMessage(Session _session, String vui_lòng_thoát_game_và_đăng_nhập_lại) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
