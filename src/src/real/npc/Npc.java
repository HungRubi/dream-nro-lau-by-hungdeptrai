package real.npc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import real.map.Map;
import real.player.Player;
import server.DBService;
import server.Util;
import server.io.Message;

public abstract class Npc implements IAtionNpc {

    private static List<Npc> NPCS = new ArrayList<>();

    public int mapId;

    public int status;

    public int cx;

    public int cy;

    public int tempId;

    public int avartar;

    public Menu mainMenu;

    public Menu[] subMenu;

    protected Npc(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        this.mapId = mapId;
        this.status = status;
        this.cx = cx;
        this.cy = cy;
        this.tempId = tempId;
        this.avartar = avartar;
        Npc.NPCS.add(this);
    }

    private void initMenu(String text) {
        text = text.substring(1);
        String[] data = text.split("\\|");
        int indexMenu = Integer.parseInt(data[0]);
        if (indexMenu == -1) {
            mainMenu = new Menu();
            mainMenu.npcId = tempId;
            mainMenu.npcSay = data[1];
            mainMenu.menuSelect = new String[data.length - 2];
            for (int i = 0; i < mainMenu.menuSelect.length; i++) {
                mainMenu.menuSelect[i] = data[i + 2].replaceAll("<>", "\n");
            }
        } else {
            if (subMenu == null) {
                subMenu = new Menu[mainMenu.menuSelect.length];
            }
            subMenu[indexMenu] = new Menu();
            subMenu[indexMenu].npcId = tempId;
            subMenu[indexMenu].npcSay = data[1];
            subMenu[indexMenu].menuSelect = new String[data.length - 2];
            for (int i = 0; i < subMenu[indexMenu].menuSelect.length; i++) {
                subMenu[indexMenu].menuSelect[i] = data[i + 2].replaceAll("<>", "\n");
            }
        }
    }

    public void createOtherMenu(Player player, int indexMenu, String npcSay, String... menuSelect) {
        Message msg;
        try {
            player.setIndexMenu(indexMenu);
            msg = new Message(32);
            msg.writer().writeShort(tempId);
            msg.writer().writeUTF(npcSay);
            msg.writer().writeByte(menuSelect.length);
            for (String menu : menuSelect) {
                msg.writer().writeUTF(menu);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public static void createMenuMagicTree(Player player, int indexMenu, String... menuSelect){
        Message msg;
        try {
            msg = new Message(-34);
            msg.writer().writeByte(1);
            for (String menu : menuSelect) {
                msg.writer().writeUTF(menu);
            }
            Util.log(""+player.name);
            (player.getSession()).sendMessage(msg);
            
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void createMenuRongThieng(Player player, int indexMenu, String npcSay, String... menuSelect) {
        createMenu(player, indexMenu, NpcFactory.RONG_THIENG, 123, npcSay, menuSelect);
    }

    public static void createMenuConMeo(Player player, int indexMenu, int avatar, String npcSay, String... menuSelect) {
        createMenu(player, indexMenu, NpcFactory.CON_MEO, avatar, npcSay, menuSelect);
    }

    private static void createMenu(Player player, int indexMenu, byte npcTempId, int avatar, String npcSay, String... menuSelect) {
        Message msg;
        try {
            player.setIndexMenu(indexMenu);
            msg = new Message(32);
            msg.writer().writeShort(npcTempId);
            msg.writer().writeUTF(npcSay);
            msg.writer().writeByte(menuSelect.length);
            for (String menu : menuSelect) {
                msg.writer().writeUTF(menu);
            }
            if (avatar != -1) {
                msg.writer().writeShort(avatar);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createTutorial(Player player, int avatar, String npcSay) {
        Message msg;
        try {
            msg = new Message(38);
            msg.writer().writeShort(NpcFactory.CON_MEO);
            msg.writer().writeUTF(npcSay);
            if (avatar != -1) {
                msg.writer().writeShort(avatar);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    @Override
    public void openMenu(Player player) {
        try {
            if (mainMenu != null) {
                mainMenu.openMenu(player);
            } else {
                Message msg;
                msg = new Message(32);
                msg.writer().writeShort(tempId);
                msg.writer().writeUTF("NPC["+ this.tempId +"][" + this.mapId + "]\n Đang cập nhật...");
                msg.writer().writeByte(1);
                msg.writer().writeUTF("Đóng");
                player.sendMessage(msg);
                msg.cleanup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Npc getByIdAndMap(int id, int mapId) {
        for (Npc npc : NPCS) {
            if (npc.tempId == id && npc.mapId == mapId) {
                return npc;
            }
        }
        return null;
    }

    public static Npc getNpc(byte tempId) {
        for (Npc npc : NPCS) {
            if (npc.tempId == tempId) {
                return npc;
            }
        }
        return null;
    }

    public static List<Npc> getByMap(Map map) {
        List<Npc> list = new ArrayList<>();
        for (Npc npc : NPCS) {
            if (npc.mapId == map.id) {
                list.add(npc);
            }
        }
        return list;
    }

    public static void loadNPC() {
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from map_npc");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                NpcFactory.createNPC(rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7));
            }
            rs.close();
            ps.close();
            NpcFactory.createNpcConMeo();
            NpcFactory.createNpcRongThieng();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
