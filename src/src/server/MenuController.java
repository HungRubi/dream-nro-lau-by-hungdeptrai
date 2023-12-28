package server;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import real.func.ChangeMap;
import real.func.Shop;
import real.item.Item;
import real.item.ItemDAO;
import real.item.ItemData;
import real.item.ItemOption;
import real.item.ItemOptionShop;
import real.npc.Npc;
import real.npc.NpcFactory;
import server.io.Message;
import server.io.Session;
import real.player.Player;

public class MenuController {

    private static MenuController instance;

    public static MenuController getInstance() {
        if (instance == null) {
            instance = new MenuController();
        }
        return instance;
    }

    public void saiHanhTinh(Session session, int idnpc) {
        Message msg;
        try {
            msg = new Message(38);
            msg.writer().writeShort(idnpc);
            msg.writer().writeUTF("Con hãy về hành tinh của con mà thể hiện!");
            msg.writer().writeByte(1);
            msg.writer().writeUTF("Đóng");
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void openMenuNPC(Session session, int idnpc, Player player) {
        Npc.getByIdAndMap(idnpc, player.map.id).openMenu(player);
        player.setIndexMenu(0);
    }

    public void doSelectMenu(Player player, int npcId, int select) throws IOException {
        if (true) {
            Npc npc = null;

            if (npcId == NpcFactory.CON_MEO) {
                npc = Npc.getNpc(NpcFactory.CON_MEO);
            } else if (npcId == NpcFactory.RONG_THIENG) {
                npc = Npc.getNpc(NpcFactory.RONG_THIENG);
            } else {
                npc = Npc.getByIdAndMap(npcId, player.map.id);
            }
            if (npc != null) {
                npc.confirmMenu(player, select);
            }
            return;
        }

        switch (npcId) {
//            case 21:
//                if (player.map.id == 5) {
//                    if (player.combine.isCombine() && select == 0) {
//                        player.combine.combine(player.combine.isSuccessCombine(), "", "");
//                        player.combine.combine(1, "", "");
//                    } else if (select == 0) {
//                        player.combine.setTypeCombine((byte) 0);
//                        player.combine.combine(0, "Vào hành trang\n"
//                                + "Chọn trang bị\n"
//                                + "(Áo, quần, găng, giày hoặc rada) có ô đặt sao pha lê\n"
//                                + "Chọn loại sao pha lê\n"
//                                + "Sau đó chọn 'Nâng cấp'", "Ta sẽ phù phép\n"
//                                + "cho trang bị của ngươi\n"
//                                + "trở nên mạnh mẽ");
//                    } else if (select == 1) {
//                        player.combine.setTypeCombine((byte) 1);
//                        player.combine.combine(0, "Vào hành trang\n"
//                                + "Chọn trang bị\n"
//                                + "(Áo, quần, găng, giày hoặc rada)\n"
//                                + "Sau đó chọn 'Nâng cấp'", "Ta sẽ phù phép\n"
//                                + "cho trang bị của ngươi\n"
//                                + "trở thành trang bị pha lê");
//                    } else if (select == 2) {
//                        player.combine.setTypeCombine((byte) 2);
//                        player.combine.combine(0, "Vào hành trang\n"
//                                + "Chọn trang bị gốc\n"
//                                + "(Áo,quần,găng,giày hoặc rada)\n"
//                                + "từ cấp [+4] trở lên\n"
//                                + "Chọn tiếp trang bị mới\n"
//                                + "chưa nâng cấp cần nhập thể\n"
//                                + "sau đó chọn 'Nâng cấp'", "Lưu ý trang bị mới\n"
//                                + "phải hơn trang bị gốc\n"
//                                + "1 bậc");
//                    }
//                } else if (player.map.id == 42 || player.map.id == 43 || player.map.id == 44) {
//                    if (select == 0) {
//                        player.combine.setTypeCombine((byte) 3);
//                        player.combine.combine(0, "Vào hành trang\n"
//                                + "Chọn 2 trang bị giống nhau\n"
//                                + "(Áo, quần, găng, giày hoặc rada)\n"
//                                + "Sau đó chọn 'Nâng cấp'", "Ta sẽ ép\n"
//                                + "thành 1 và sẽ có chỉ số\n"
//                                + "mạnh hơn");
//                    }
//                }
//                break;
            case 7:
            case 8:
            case 9:
            case 16:
            case 39:
                if (select == 0) {
                    Shop.gI().openShop(player, npcId);
                }
                break;
            case 13:
                if (select == 1) {
                    Shop.gI().openShop(player, npcId);
                }
                break;
            case 10:
            case 11:
            case 12:
                break;
            case 24:
                String result = null;
                if (select == 0) {
                    Item item = new Item();
                    item.template = ItemData.getTemplate((short) (227 + player.gender));
                    item.content = item.getContent();
                    item.itemOptions.add(new ItemOption(77, (short) Util.nextInt(5, 25)));
                    item.itemOptions.add(new ItemOption(97, (short) Util.nextInt(1, 10)));
                    item.quantity = 1;
                    item.id = ItemDAO.create(item.template.id, item.itemOptions);
                    player.inventory.addItemBag(item);
                    player.inventory.sendItemBags();
//                    Service.getInstance().callDragon(player, false, 0);
                    result = "Chúc mừng ngươi nhận được Cải trang Đẹp trai nhất Vũ Trụ";
                } else if (select == 1) {
                    player.inventory.gold += 200000000;
                    Service.getInstance().sendMoney(player);
//                    Service.getInstance().callDragon(player, false, 0);
                    result = "Chúc mừng ngươi nhận được 200Tr Vàng";
                } else if (select == 2) {
                    player.inventory.gem += 1500;
                    Service.getInstance().sendMoney(player);
//                    Service.getInstance().callDragon(player, false, 0);
                    result = "Chúc mừng ngươi nhận được 1500 Ngọc";
                } else if (select == 3) {
                    Service.getInstance().congTiemNang(player, (byte) 2, 200000000);
//                    Service.getInstance().callDragon(player, false, 0);
                    result = "Chúc mừng ngươi nhận được 200Tr Sức Mạnh và Tiềm năng";
                }
                Service.getInstance().sendThongBao(player, result);
                //player.inventory.removeNgocRong();
                break;
        }
    }
}
