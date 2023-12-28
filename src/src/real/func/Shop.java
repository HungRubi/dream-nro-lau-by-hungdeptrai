package real.func;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import real.item.CaiTrangData;
import real.item.Item;
import real.item.ItemDAO;
import real.item.ItemData;
import real.item.ItemOption;
import real.item.ItemOptionShop;
import real.item.ItemShop;
import real.item.ItemShopDAO;
import real.player.Inventory;
import real.player.Player;
import server.Service;
import server.io.Message;
import server.io.Session;

public class Shop {

    public Map itemShops = null;
    private static Shop instance;
    public static int idThoiVang = 457;

    private Shop() {

    }

    public static Shop gI() {
        if (instance == null) {
            instance = new Shop();
        }
        return instance;
    }

    private int getCountTab(List<ItemShop> list) {
        int count = 0;
        int curr = -1;
        for (ItemShop it : list) {
            if (it.tab != curr) {
                count++;
                curr = it.tab;
            }
        }
        return count;
    }

    private List<ItemShop> getListShopTab(int npc, int tab, int gender) {
        List<ItemShop> list = new ArrayList<>();
        for (ItemShop it : getItemsShop(npc)) {
            if (list.size() < 75
                    && it.npcId == npc && it.tab == tab
                    && (it.itemTemplate.gender == gender
                    || it.itemTemplate.gender > 2) && it.sell) {
                list.add(it);
            }
        }
        return list;
    }

    private List<ItemShop> getListShop(int npc, int gender) {
        List<ItemShop> list = new ArrayList<>();
        for (ItemShop it : getItemsShop(npc)) {
            if (it.npcId == npc && (it.itemTemplate.gender == gender || it.itemTemplate.gender > 2)) {
                list.add(it);
            }
        }
        return list;
    }

    private List<ItemShop> getItemsShop(int npc) {
        List<ItemShop> list = (List<ItemShop>) itemShops.get(npc);
        return list;
    }

    public void openShop(Player pl, int npcId) {
        pl.isOpenShop = true;
        if (((npcId == 7 || npcId == 8 || npcId == 9)
                && (pl.map.id != 0 && pl.map.id != 7 && pl.map.id != 14 && pl.map.id != 84))
                || (npcId == 16 && (pl.map.id != 24 && pl.map.id != 25 && pl.map.id != 26 && pl.map.id != 84))
                || (npcId == 39) && (pl.map.id != 5 && pl.map.id != 13 &&  pl.map.id == 20 )) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện!");
            return;
        }
        try {
            List<ItemShop> list = getListShop(npcId, pl.gender);
            Message msg = new Message(-44);
            msg.writer().writeByte(list.get(0).typeShop);

            int tabs = getCountTab(list);
            msg.writer().writeByte(tabs);

            for (int i = 0; i < tabs; i++) {
                list = getListShopTab(npcId, i, pl.gender);
                System.out.println("size list " + list.size());
                msg.writer().writeUTF(list.get(0).tabName);
                int items = list.size();
                msg.writer().writeByte(items);
                for (int j = 0; j < items; j++) {
                    ItemShop it = list.get(j);
                    msg.writer().writeShort(it.itemTemplate.id);
                    msg.writer().writeInt(it.gold);
                    msg.writer().writeInt(it.gem);
                    int options = it.itemOptions.size();
                    msg.writer().writeByte(options);
                    if (options != 0) {
                        for (int k = 0; k < options; k++) {
                            msg.writer().writeByte(it.itemOptions.get(k).optionId);
                            msg.writer().writeShort(it.itemOptions.get(k).param);
                        }
                    }
                    msg.writer().writeByte(it.itemNew ? 1 : 0);
                    int isCaiTrang = it.isCaiTrang ? 1 : 0;
                    msg.writer().writeByte(isCaiTrang);
                    if (isCaiTrang == 1) {
                        CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(it.itemTemplate.id);
                        msg.writer().writeShort(ct.getID()[0]);
                        msg.writer().writeShort(ct.getID()[1]);
                        msg.writer().writeShort(ct.getID()[2]);
                        msg.writer().writeShort(ct.getID()[3]);
                    }
                }
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buyItem(Player pl, int itemID, int type, int quantity) {
        boolean isNotBuy = false;
        try {
            ItemShop itemShop = ItemShopDAO.getByTemp(itemID);
            if (itemShop.gold > 0) {
                if (pl.inventory.gold < itemShop.gold) {
                    Service.getInstance().sendThongBao(pl, "Bạn không đủ vàng!");
                    isNotBuy = true;
                } else {
                    pl.inventory.gold -= itemShop.gold;
                }
            } else {
                if (pl.inventory.gem < itemShop.gem) {
                    Service.getInstance().sendThongBao(pl, "Không đủ Ngọc");
                    isNotBuy = true;
                } else {
                    pl.inventory.gem -= itemShop.gem;
                }
            }
            Service.getInstance().sendMoney(pl);
            if (isNotBuy != true) {
                Item item = new Item();
                item.template = ItemData.getTemplate((short) itemID);
                item.content = item.getContent();
                if (itemShop.itemOptions.size() > 0) {
                    for (ItemOptionShop ios : itemShop.itemOptions) {
                        item.itemOptions.add(new ItemOption(ios.optionId, (short) ios.param));
                    }
                } else {
                    item.itemOptions.add(new ItemOption(73, (short) 0));
                }
                if (item.template.id == 193 || item.template.id == 361) {
                    item.quantity = 10;
                } else {
                    item.quantity = 1;
                }
                item.id = ItemDAO.create(item.template.id, item.itemOptions);
                pl.inventory.addItemBag(item);
                pl.inventory.sortItemBag();
                pl.inventory.sendItemBags();
                Service.getInstance().sendThongBao(pl, "Chúc mừng bạn đã nhận được " + itemShop.itemTemplate.name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saleItem(Player pl, int itemID, int type, int quantity) {
        try {
            Item item = new Item();
            item.template = ItemData.getTemplate((short) itemID);
            if (item.template.id == idThoiVang) {
                if (!pl.inventory.existItemBag(Shop.idThoiVang)) {
                    Service.getInstance().sendThongBao(pl, "Bạn không có " + item.template.name);
                    return;
                }
                pl.inventory.banThoiVang(item);
                pl.inventory.gold += 500000000;
                if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
                    pl.inventory.gold = Inventory.LIMIT_GOLD;
                }
                pl.sendInfo();
                Service.getInstance().sendThongBao(pl, "Bạn đã bán thành công " + item.template.name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
