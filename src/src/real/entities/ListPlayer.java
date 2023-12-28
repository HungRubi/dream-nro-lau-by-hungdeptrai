package real.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import real.func.ChangeMap;
import real.func.PVP;
import real.item.Item;
import real.map.MapManager;
import real.npc.Npc;
import real.npc.NpcFactory;
import real.player.Player;
import real.player.PlayerManger;
import server.Service;
import server.Util;
import server.io.Message;

public class ListPlayer {

    private static final byte OPEN_LIST = 0;

    private static final byte MAKE_FRIEND = 1;
    private static final byte REMOVE_FRIEND = 2;

    private static final byte REVENGE = 1;
    private static final byte REMOVE_ENEMY = 2;

    private Player player;
    private List<Player> friends;
    private List<Player> enemies;
    private int playerMakeFriend;

    public ListPlayer(Player player) {
        this.player = player;
        this.friends = new ArrayList<>();
        this.enemies = new ArrayList<>();
        ListPlayerDAO.loadFriend(this, player);
        ListPlayerDAO.loadEnemy(this, player);
    }

    public void controllerFriend(Message msg) {
        try {
            byte action = msg.reader().readByte();
            switch (action) {
                case OPEN_LIST:
                    openListFriend();
                    break;
                case MAKE_FRIEND:
                    makeFriend(msg.reader().readInt());
                    break;
                case REMOVE_FRIEND:
                    removeFriend(msg.reader().readInt());
                    break;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void controllerEnemy(Message msg) {
        try {
            byte action = msg.reader().readByte();
            System.out.println("enemy action: " + action);
            switch (action) {
                case OPEN_LIST:
                    openListEnemy();
                    break;
                case REVENGE:
                    Player enemy = PlayerManger.gI().getPlayerByID(msg.reader().readInt());
                    if (enemy != null) {
                        PVP.gI().openSelectRevenge(player, enemy);
                    } else {
                        Service.getInstance().sendThongBao(player, "Đang offline");
                    }
                    break;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void reloadFriend() {
        for (int i = 0; i < friends.size(); i++) {
            Player pl = PlayerManger.gI().getPlayerByID((int) friends.get(i).id);
            if (pl != null) {
                friends.set(i, pl);
            }
        }
    }

    private void reloadEnemy() {
        for (int i = 0; i < enemies.size(); i++) {
            Player pl = PlayerManger.gI().getPlayerByID((int) enemies.get(i).id);
            if (pl != null) {
                enemies.set(i, pl);
            }
        }
    }

    private void openListFriend() {
        reloadFriend();
        Message msg;
        try {
            msg = new Message(-80);
            msg.writer().writeByte(OPEN_LIST);
            msg.writer().writeByte(friends.size());
            for (Player pl : friends) {
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeShort(pl.getHead());
                msg.writer().writeShort(pl.getBody());
                msg.writer().writeShort(pl.getLeg());
                msg.writer().writeByte(0);
                msg.writer().writeUTF(pl.name);
                msg.writer().writeBoolean(PlayerManger.gI().getPlayerByID((int) pl.id) != null);
                msg.writer().writeUTF(Service.numberToMoney(pl.point.power));
            }
            this.player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private void openListEnemy() {
        reloadEnemy();
        Message msg;
        try {
            msg = new Message(-99);
            msg.writer().writeByte(OPEN_LIST);
            msg.writer().writeByte(enemies.size());
            for (Player pl : enemies) {
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeShort(pl.getHead());
                msg.writer().writeShort(pl.getBody());
                msg.writer().writeShort(pl.getLeg());
                msg.writer().writeShort(0);
                msg.writer().writeUTF(pl.name);
                msg.writer().writeUTF(Service.numberToMoney(pl.point.power));
                msg.writer().writeBoolean(PlayerManger.gI().getPlayerByID((int) pl.id) != null);
            }
            this.player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeFriend(int playerId) {
        boolean madeFriend = false;
        for (Player friend : friends) {
            if (friend.id == playerId) {
                Service.getInstance().sendThongBao(player, "Đã có trong danh sách bạn bè");
                madeFriend = true;
                break;
            }
        }
        if (!madeFriend) {
            this.playerMakeFriend = playerId;
            Player pl = PlayerManger.gI().getPlayerByID(playerId);
            String npcSay;
            if (friends.size() >= 5) {
                npcSay = "Bạn có muốn kết bạn với " + pl.name + " với phí là 5 ngọc ?";
            } else {
                npcSay = "Bạn có muốn kết bạn với " + pl.name + " ?";
            }
            Npc.createMenuConMeo(player, NpcFactory.MAKE_FRIEND, -1, npcSay, "Đồng ý", "Từ chối");
        }
    }

    private void removeFriend(int playerId) {
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).id == playerId) {
                Service.getInstance().sendThongBao(player, "Đã xóa thành công " + friends.get(i).name + " khỏi danh sách bạn");
                Message msg;
                try {
                    msg = new Message(-80);
                    msg.writer().writeByte(REMOVE_FRIEND);
                    msg.writer().writeInt((int) friends.get(i).id);
                    player.sendMessage(msg);
                    msg.cleanup();
                } catch (Exception e) {
                }
                friends.remove(i);
                break;
            }
        }
    }

    public void chatPrivate(Message msg) {
        try {
            int playerId = msg.reader().readInt();
            String text = msg.reader().readUTF();
            Player pl = PlayerManger.gI().getPlayerByID(playerId);
            if (pl != null) {
                Service.getInstance().chatPrivate(player, pl, text);
            }
        } catch (Exception e) {
        }
    }

    public void acceptMakeFriend() {
        Player pl = PlayerManger.gI().getPlayerByID(playerMakeFriend);
        if (pl != null) {
            friends.add(pl);
            Service.getInstance().sendThongBao(player, "Kết bạn thành công");
            Service.getInstance().chatPrivate(player, pl, player.name + " vừa mới kết bạn với " + pl.name);
        } else {
            Service.getInstance().sendThongBao(player, "Không tìm thấy hoặc đang Offline, vui lòng thử lại sau");
        }
    }

    public void goToPlayerWithYardrat(Message msg) {
        try {
            Player pl = PlayerManger.gI().getPlayerByID(msg.reader().readInt());
            if (pl != null) {
                Item ct = player.inventory.itemsBody.get(5);
                if (ct.id != -1 && (ct.template.id == 592 || ct.template.id == 593 || ct.template.id == 594)) {
                    ChangeMap.gI().changeMap(player, pl.map.id, pl.map.zoneId, pl.x + Util.nextInt(-5, 5), pl.y, ChangeMap.TELEPORT_YARDRAT);
                } else {
                    Service.getInstance().sendThongBao(player, "Yêu cầu trang bị có khả năng dịch chuyển tức thời");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void addFriend(Player player) {
        this.friends.add(player);
    }

    public void addEnemy(Player player) {
        this.enemies.add(player);
    }

    public void addFriend(List<Player> players) {
        this.friends.addAll(players);
    }

    public void addEnemy(List<Player> players) {
        this.enemies.addAll(players);
    }

    public List<Player> getFriends() {
        return this.friends;
    }

    public List<Player> getEnemies() {
        return this.enemies;
    }
}
