package real.func;

import real.item.Item;
import real.map.Map;
import real.map.MapManager;
import real.npc.Npc;
import real.npc.NpcFactory;
import real.player.Inventory;
import real.player.Player;
import real.player.Point;
import real.skill.Skill;
import real.skill.SkillUtil;
import server.ReadMessage;
import server.Service;
import server.Util;
import server.io.Message;

public class UseItem {

    private static UseItem instance;

    private UseItem() {

    }

    public static UseItem gI() {
        if (instance == null) {
            instance = new UseItem();
        }
        return instance;
    }

    public void useItem(Player pl, Item item, int indexBag) {
        switch (item.template.id) {
            case 457:// thỏi vàng
                if (!pl.inventory.existItemBag(Shop.idThoiVang)) {
                    Service.getInstance().sendThongBao(pl, "Bạn không có " + item.template.name);
                    return;
                }
                pl.inventory.banThoiVang(item);
                if (pl.inventory.gold > 1500000000) {
                    pl.inventory.gold = Inventory.LIMIT_GOLD;
                } else {
                    pl.inventory.gold += 500000000;
                }
                pl.sendInfo();
                Service.getInstance().sendThongBao(pl, "Bạn đã bán thành công " + item.template.name);
                break;
                          
            case 13:// Đậu thần cấp 1
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 352:
            case 523:
            case 595: //Đậu thần cấp 10
                if (pl.point.hp != pl.point.hpGoc || pl.point.mp != pl.point.mpGoc) {
                    Item it = pl.inventory.eatMagicTree(item);
                    if ((it.itemOptions.get(0).optionTemplate.id) == 2) {
                        pl.hoiphuc((it.itemOptions.get(0).param) * 1000, (it.itemOptions.get(0).param) * 1000);
//                        Service.getInstance().hsChar(this, Point.getHPFull(), Point.getMPFull());
                        Service.getInstance().chatJustForMe(pl, null, "Cam on su phu!");
                        
                    } else {
                        pl.hoiphuc(it.itemOptions.get(0).param, it.itemOptions.get(0).param);
                         pl.pet.hoiphuc(it.itemOptions.get(0).param, it.itemOptions.get(0).param);
                         Service.getInstance().chatJustForMe(pl, null, "cam on su phu");
                    }
                } else {
                    Service.getInstance().sendThongBao(pl, "Không thể dùng đậu khi HP và KI đạt 100%");
                }
                break;
            case 521://tdlt
                useTDLT(pl, item);
                break;
            case 454: //bông tai
                UseItem.gI().usePorata(pl);
                break;
            case 193: //gói 10 viên capsule
                pl.inventory.subQuantityItemsBag(item, 1);
            case 194: //capsule đặc biệt
                openCapsuleUI(pl);
                break;
            case 402: //sách nâng chiêu 1 đệ tử
            case 403: //sách nâng chiêu 2 đệ tử
            case 404: //sách nâng chiêu 3 đệ tử
            case 759: //sách nâng chiêu 4 đệ tử
                upSkillPet(pl, item);
                break;
            case 381: //Cuồng nộ
            case 382: //Bổ huyết
            case 383: //Bổ khí
                pl.inventory.setItemTime(item.template.id, 0, 10 * 60 * 1000);
                pl.inventory.subQuantityItemsBag(item, 1);
                Service.getInstance().point(pl);
                break;
            case 213:
            case 214:
            case 215:
            //case 216:
            case 217:
//            case 218:
            case 219:
            case 671:
            case 672:
                pl.inventory.setItemTime(item.template.id, 0, (long) 30 * 24 * 60 * 60 * 1000);
                pl.inventory.subQuantityItemsBag(item, 1);
                Service.getInstance().point(pl);
                break;
            default:
                switch (item.template.type) {
                    case 7: //sách học, nâng skill
                        learnSkill(pl, item);
                        break;
                    case 12: //ngọc rồng các loại
                        controllerCallRongThan(pl, item);
                        break;
                }
        }
        pl.inventory.sendItemBags();
    }

    private void controllerCallRongThan(Player pl, Item item) {
        int tempId = item.template.id;
        if (tempId >= SummonDragon.NGOC_RONG_1_SAO && tempId <= SummonDragon.NGOC_RONG_7_SAO) {
            switch (tempId) {
                case SummonDragon.NGOC_RONG_1_SAO:
                case SummonDragon.NGOC_RONG_2_SAO:
                case SummonDragon.NGOC_RONG_3_SAO:
                    SummonDragon.gI().openMenuSummonShenron(pl, (byte) (tempId - 13));
                    break;
                default:
                    Npc.createMenuConMeo(pl, NpcFactory.TUTORIAL_SUMMON_DRAGON, -1, "Bạn chỉ có thể gọi rồng từ ngọc 3 sao, 2 sao, 1 sao", "Hướng\ndẫn thêm\n(mới)", "OK");
                    break;
            }
        }
    }

    private void learnSkill(Player pl, Item item) {
        Message msg;
        try {
            if (item.template.gender == pl.gender || item.template.gender == 3) {
                String[] subName = item.template.name.split("");
                byte level = Byte.parseByte(subName[subName.length - 1]);
                Skill curSkill = SkillUtil.getSkillByItemID(pl, item.template.id);
                if (curSkill == null) {
                    if (level == 1) {
                        curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                        pl.playerSkill.skills.add(curSkill);
                        pl.inventory.subQuantityItemsBag(item, 1);
                        msg = Service.getInstance().messageSubCommand((byte) 23);
                        msg.writer().writeShort(curSkill.skillId);
                        pl.sendMessage(msg);
                        msg.cleanup();
                    } else {
                        Skill skillNeed = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                        Service.getInstance().sendThongBao(pl, "Vui lòng học " + skillNeed.template.name + " cấp " + skillNeed.point + " trước!");
                    }
                } else {
                    if (curSkill.point + 1 == level) {
                        curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                        SkillUtil.setSkill(pl, curSkill);
                        pl.inventory.subQuantityItemsBag(item, 1);
                        msg = Service.getInstance().messageSubCommand((byte) 62);
                        msg.writer().writeShort(curSkill.skillId);
                        pl.sendMessage(msg);
                        msg.cleanup();
                    } else {
                        if (curSkill.point < 7) {
                            Service.getInstance().sendThongBao(pl, "Vui lòng học " + curSkill.template.name + " cấp " + (curSkill.point + 1) + " trước!");
                        } else {
                            Service.getInstance().sendThongBao(pl, "Bạn đã học " + curSkill.template.name + " max 7 rồi!");
                        }
                    }
                }
                pl.inventory.sendItemBags();
            } else {
                Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            }
        } catch (Exception e) {
        }
    }

    private void useTDLT(Player pl, Item item) {
        Message msg;
        try {
            pl.isTdlt = !pl.isTdlt;
            msg = new Message(-116);
            msg.writer().writeByte(pl.isTdlt ? 1 : 0);
            pl.sendMessage(msg);
            if (pl.isTdlt) {
                if (item.itemOptions.get(0).param < 533) {
                    pl.timeTdlt = 60 * item.itemOptions.get(0).param;
                } else {
                    pl.timeTdlt = 60 * 533;
                }
                item.itemOptions.get(0).param -= pl.timeTdlt / 60;
            } else {
                item.itemOptions.get(0).param += (short) (pl.timeTdlt / 60);
                pl.timeTdlt = 0;
            }
            Service.getInstance().sendItemTime(pl, item.template.iconID, pl.timeTdlt);
            pl.inventory.sendItemBags();
        } catch (Exception e) {
        }
    }

    private void usePorata(Player pl) {
        if (pl.pet == null || pl.typeFusion == 4) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.typeFusion == 0) {
                pl.pet.fusion(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }

    private void openCapsuleUI(Player pl) {
        pl.mapCapsule = MapManager.gI().getMapCapsule(pl);
        Message msg;
        try {
            msg = new Message(-91);
            msg.writer().writeByte(pl.mapCapsule.size());
            for (int i = 0; i < pl.mapCapsule.size(); i++) {
                Map map = pl.mapCapsule.get(i);
                if (i == 0 && pl.zoneBeforeCapsuleId != -1) {
                    msg.writer().writeUTF("Quay Lại Map: " + map.name);
                } else if (map.name.equals("Về Nhà") || map.name.equals("Về Nhà") || map.name.equals("Về Nhà")) {
                    msg.writer().writeUTF("Về Nhà");
                } else {
                    msg.writer().writeUTF(map.name);
                }
                msg.writer().writeUTF(map.planetName);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void choseMapCapsule(Player pl, int index) {
        int zoneId = -1;
        if (index != 0) {
            pl.mapBeforeCapsuleId = pl.map.id;
            pl.zoneBeforeCapsuleId = pl.map.zoneId;
        } else {
            zoneId = pl.zoneBeforeCapsuleId;
            pl.mapBeforeCapsuleId = -1;
            pl.zoneBeforeCapsuleId = -1;
        }
        ChangeMap.gI().changeMapBySpaceShip(pl, pl.mapCapsule.get(index).id, zoneId, -1, ChangeMap.TENNIS_SPACE_SHIP);
    }

    private void upSkillPet(Player pl, Item item) {
        if (pl.pet == null) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        try {
            switch (item.template.id) {
                case 402:
                    if (SkillUtil.upSkill(pl.pet.playerSkill.skills, 0)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "cảm ơn sư phụ");
                        pl.inventory.subQuantityItemsBag(item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 403:
                    if (SkillUtil.upSkill(pl.pet.playerSkill.skills, 1)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "cảm ơn sư phụ");
                        pl.inventory.subQuantityItemsBag(item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 404:
                    if (SkillUtil.upSkill(pl.pet.playerSkill.skills, 2)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "cảm ơn sư phụ");
                        pl.inventory.subQuantityItemsBag(item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 759:
                    if (SkillUtil.upSkill(pl.pet.playerSkill.skills, 3)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "cảm ơn sư phụ");
                        pl.inventory.subQuantityItemsBag(item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
            }
        } catch (Exception e) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
        }
    }
}
