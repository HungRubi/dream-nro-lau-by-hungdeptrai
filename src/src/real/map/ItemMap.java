package real.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import real.item.ItemOption;
import real.item.ItemTemplate;
import real.player.Player;
import server.Service;
import server.io.Message;

public class ItemMap {

    private static int countItemAppeaerd = 0;
    public Map map;
    public int itemMapId;
    public ItemTemplate itemTemplate;
    public int quantity;

    public int x;
    public int y;
    public long playerId;
    public List<ItemOption> options;

    private long createTime;

    public ItemMap(Map map, ItemTemplate itemTemp, int quantity, int x, int y, long playerId) {
        this.map = map;
        this.itemMapId = countItemAppeaerd++;
        this.itemTemplate = itemTemp;
        this.quantity = quantity;
        this.x = x;
        this.y = y;
        this.playerId = playerId;
        this.createTime = System.currentTimeMillis();
        this.options = new ArrayList<>();
    }

    public void update() {
        if ((System.currentTimeMillis() - createTime) >= 60000) {
            removeItemMap(this);
        }
        if ((System.currentTimeMillis() - createTime) >= 45000) {
            playerId = -1;
        }
    }

    public static void removeItemMap(ItemMap itemMap) {
//        System.out.println("remove item map: " + itemMap.itemTemplate.name);
        Message msg;
        try {
            msg = new Message(-21);
            msg.writer().writeShort(itemMap.itemMapId);
            Service.getInstance().sendMessAllPlayerInMap(MapManager.gI().getMap(itemMap.map.id, itemMap.map.zoneId), msg);
            msg.cleanup();
            MapManager.gI().getMap(itemMap.map.id, itemMap.map.zoneId).items.remove(itemMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
