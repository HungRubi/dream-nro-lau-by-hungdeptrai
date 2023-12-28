package server;

import java.net.ServerSocket;
import java.net.Socket;
import real.clan.ClanManager;
import real.item.CaiTrangData;
import real.item.ItemData;
import real.map.MapManager;
import real.map.Mob_ItemDAO;
import real.npc.BoMongDAO;
import real.npc.Npc;
import real.part.PartData;
import real.player.PlayerManger;
import real.skill.SkillData;
import server.io.Session;

public class ServerManager {

    public static final int port = 14445;

    private Controller controller;

    private static ServerManager instance;

    public static byte vsData = 47;

    public static byte vsMap = 25;

    public static byte vsSkill = 5;

    public static byte vsItem = 90;

    long[] smtieuchuan = {100010000000L, 80010000000L, 70010000000L, 60010000000L, 50010000000L, 40000000000L, 10000000000L, 5000000000L, 1500000000L, 150000000L, 15000000L, 1500000L, 700000L, 340000L, 170000L, 90000L, 40000L, 15000L, 3000L, 1000L};

    public void init() {
        PartData.loadPart();
        CaiTrangData.loadCaiTrang();
        CaiTrangData.loadAvatar();
        Npc.loadNPC();
        MapManager.gI().init();
        ClanManager.gI().init();
        SkillData.loadSkill();
        ItemData.loadDataItems();
        Mob_ItemDAO.getItemRoi();
        //OnloadDB.active(5000, 5000);
        BoMongDAO.active(10000, 10000);
        this.controller = new Controller();
    }

    public static ServerManager gI() {
        if (instance == null) {
            instance = new ServerManager();
            instance.init();
        }
        return instance;
    }

    public static void main(String[] args) {
        ServerManager.gI().run();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000);
                        Util.log("Player: " + PlayerManger.gI().size());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void run() {
        ServerSocket listenSocket = null;
        try {
            Util.log("Start server...");
            listenSocket = new ServerSocket(port);
            while (true) {
                Socket sc = listenSocket.accept();
                Session session = new Session(sc, controller);
                session.start();
                Util.log("Accept socket listen " + sc.getPort());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
