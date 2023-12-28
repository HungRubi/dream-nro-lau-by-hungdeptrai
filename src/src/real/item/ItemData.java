package real.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import real.func.Shop;
import server.ServerManager;
import server.Service;
import server.Util;
import server.io.Message;
import server.io.Session;

public class ItemData {

    public static List<ItemOptionTemplate> itemOptionTemplates = new ArrayList<>();
    public static List<ItemTemplate> itemTemplates = new ArrayList<>();

    public static ItemOptionTemplate getItemOptionTemplate(int id) {
        return itemOptionTemplates.get(id);
    }

    public static void add(ItemTemplate it) {
        itemTemplates.add(it.id, it);
    }

    public static ItemTemplate getTemplate(short id) {
        return itemTemplates.get(id);
    }

    public static short getPart(short itemTemplateID) {
        return getTemplate(itemTemplateID).part;
    }

    public static short getIcon(short itemTemplateID) {
        return getTemplate(itemTemplateID).iconID;
    }

    public static void loadDataItems() {
        itemOptionTemplates = ItemOptionTemplateDAO.getAll();
        Util.log("Tải item option template thành công! (" + itemOptionTemplates.size() + ")");
        itemTemplates = (ArrayList<ItemTemplate>) ItemTemplateDAO.getAll();
        Util.log("Tải item template thành công! (" + itemTemplates.size() + ")");
        Shop.gI().itemShops = (Map) ItemShopDAO.loadItemShop();
        Util.log("Tải dữ liệu cửa hàng thành công! (" + Shop.gI().itemShops.size() + ")");
    }

    public static void updateItem(Session session) {
        updateItemOptionItemplate(session);
        int count = 800;
        updateItemTemplate(session, count);
        updateItemTemplate(session, count, itemTemplates.size());
        System.out.println("update item");
    }

    private static void updateItemOptionItemplate(Session session) {
        Message msg;
        try {
            msg = new Message(-28);
            msg.writer().writeByte(8);
            msg.writer().writeByte(ServerManager.vsItem); //vcitem
            msg.writer().writeByte(0); //update option
            msg.writer().writeByte(ItemData.itemOptionTemplates.size());
            for (ItemOptionTemplate io : ItemData.itemOptionTemplates) {
                msg.writer().writeUTF(io.name);
                msg.writer().writeByte(io.type);
            }
            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    private static void updateItemTemplate(Session session, int count) {
        Message msg;
        try {
            msg = new Message(-28);
            msg.writer().writeByte(8);

            msg.writer().writeByte(ServerManager.vsItem); //vcitem
            msg.writer().writeByte(1); //reload itemtemplate
            msg.writer().writeShort(count);
            for (int i = 0; i < count; i++) {
                msg.writer().writeByte(ItemData.itemTemplates.get(i).type);
                msg.writer().writeByte(ItemData.itemTemplates.get(i).gender);
                msg.writer().writeUTF(ItemData.itemTemplates.get(i).name);
                msg.writer().writeUTF(ItemData.itemTemplates.get(i).description);
                msg.writer().writeByte(ItemData.itemTemplates.get(i).level);
                msg.writer().writeInt(ItemData.itemTemplates.get(i).strRequire);
                msg.writer().writeShort(ItemData.itemTemplates.get(i).iconID);
                msg.writer().writeShort(ItemData.itemTemplates.get(i).part);
                msg.writer().writeBoolean(ItemData.itemTemplates.get(i).isUpToUp);
            }
            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    private static void updateItemTemplate(Session session, int start, int end) {
        Message msg;
        try {
            msg = new Message(-28);
            msg.writer().writeByte(8);

            msg.writer().writeByte(ServerManager.vsItem); //vcitem
            msg.writer().writeByte(2); //add itemtemplate
            msg.writer().writeShort(start);
            msg.writer().writeShort(end);
            for (int i = start; i < ItemData.itemTemplates.size(); i++) {
                msg.writer().writeByte(ItemData.itemTemplates.get(i).type);
                msg.writer().writeByte(ItemData.itemTemplates.get(i).gender);
                msg.writer().writeUTF(ItemData.itemTemplates.get(i).name);
                msg.writer().writeUTF(ItemData.itemTemplates.get(i).description);
                msg.writer().writeByte(ItemData.itemTemplates.get(i).level);
                msg.writer().writeInt(ItemData.itemTemplates.get(i).strRequire);
                msg.writer().writeShort(ItemData.itemTemplates.get(i).iconID);
                msg.writer().writeShort(ItemData.itemTemplates.get(i).part);
                msg.writer().writeBoolean(ItemData.itemTemplates.get(i).isUpToUp);
            }
            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }
    public static ArrayList<ItemOption> parseOption(String optionsString) {
        ArrayList<ItemOption> optionNew = new ArrayList<ItemOption>();
        JSONArray Option = (JSONArray) JSONValue.parse(optionsString);
        for (int k = 0; k < Option.size(); ++k) {
            final JSONObject job = (JSONObject) Option.get(k);
            final ItemOption option = new ItemOption(Integer.parseInt(job.get((Object) "id").toString()), Short.parseShort(job.get((Object) "param").toString()));
            optionNew.add(option);
        }
        return optionNew;
    }
}
