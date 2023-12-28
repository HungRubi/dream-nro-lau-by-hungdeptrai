package server;

import real.func.UseItem;
import real.item.Item;
import real.player.Player;
import real.player.PlayerManger;
import server.io.Message;
import server.io.Session;

public class ReadMessage {

    private static ReadMessage instance;

    public static ReadMessage gI() {
        if (instance == null) {
            instance = new ReadMessage();
        }
        return instance;
    }

    public void getItem(Session session, Message msg) {
        Player player = PlayerManger.gI().getPlayerByUserID(session.userId);
        try {
            int type = msg.reader().readByte();
            int index = msg.reader().readByte();

            Util.log("type = " + type);
            switch (type) {
                case 4:
                    player.inventory.itemBagToBody(index);
                    break;
                case 5:
                    player.inventory.itemBodyToBag(index);
                    break;
                case 6:
                    player.inventory.itemBagToPetBody(index);
                    break;
                case 7:
                    player.inventory.itemPetBodyToBag(index);
                    break;
            }
        } catch (Exception e) {
        }
    }

    public void useItem(Player player, Message _msg) {
        Message msg;
        try {
            int type = _msg.reader().readByte();
            int where = _msg.reader().readByte();
            int index = _msg.reader().readByte();
            System.out.println(type + " " + where + " " + index);
            if(index == -1){// ăn đậu
                if(player.point.hp != player.point.hpGoc || player.point.mp != player.point.mpGoc){
                        Item it = player.inventory.eatMagicTree();
                        if((it.itemOptions.get(0).optionTemplate.id) == 2 ){
                                player.hoiphuc((it.itemOptions.get(0).param)*1000, (it.itemOptions.get(0).param)*1000);
                        }else{
                                player.hoiphuc(it.itemOptions.get(0).param, it.itemOptions.get(0).param);
                        }
                }else{
                    Service.getInstance().sendThongBao(player, "Không thể dùng đậu khi HP và KI đạt 100%");
                }
                return;
            }
            switch (type) {
                case 0:
                    Util.log("skill type" + player.inventory.itemsBag.get(index).template.type);
                    if (player.inventory.itemsBag.get(index).template.type == 7) {
                        msg = new Message(-43);
                        msg.writer().writeByte(type);
                        msg.writer().writeByte(where);
                        msg.writer().writeByte(index);
                        msg.writer().writeUTF("Bạn chắc chắn học " + player.inventory.itemsBag.get(index).template.name + "?");
                        player.sendMessage(msg);
                    } else {
                        UseItem.gI().useItem(player, player.inventory.itemsBag.get(index), index);
                    }
                    break;
                case 1:
                    Item item = player.inventory.itemsBag.get(index);
                    msg = new Message(-43);
                    msg.writer().writeByte(type);
                    msg.writer().writeByte(where);
                    msg.writer().writeByte(index);
                    msg.writer().writeUTF("Bạn chắc chắn muốn bỏ " + item.template.name + "?");
                    player.sendMessage(msg);
                    break;
                case 2:
                    player.inventory.throwItem(where, index);
                    Service.getInstance().point(player);
                    player.inventory.sendItemBags();
                    break;
                case 3:
                    UseItem.gI().useItem(player, player.inventory.itemsBag.get(index), index);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
