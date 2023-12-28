package server;

import java.io.DataInputStream;
import java.io.FileInputStream;
import server.io.Message;
import server.io.Session;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import real.boss.Broly;
import real.clan.ClanManager;
import real.clan.ClanService;
import real.clan.Member;
import real.func.ChangeMap;
import real.func.PVP;
import real.func.Shop;
import real.func.Transaction;
import real.func.UseItem;
import real.item.Item;
import real.item.ItemDAO;
import real.item.ItemData;
import real.item.ItemOption;
import real.map.ItemMap;
import real.map.Map;
import real.map.MapManager;
import real.map.MapService;
import real.pet.PetDAO;
import real.player.Player;
import real.player.PlayerDAO;
import real.player.PlayerManger;
import real.skill.Skill;
import real.skill.SkillShortCutDAO;

public class Controller {

    private static Controller instance;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public void onMessage(Session _session, Message _msg) {
        try {
            Player player = PlayerManger.gI().getPlayerByUserID(_session.userId);
            byte cmd = _msg.command;
            System.out.println("CMD receive: " + cmd);
            switch (cmd) {
                case -99:
                    player.listPlayer.controllerEnemy(_msg);
                    break;
                case 18:
                    player.listPlayer.goToPlayerWithYardrat(_msg);
                    break;
                case -72:
                    player.listPlayer.chatPrivate(_msg);
                    break;
                case -80:
                    player.listPlayer.controllerFriend(_msg);
                    break;
                case -59:
                    PVP.gI().controller(player, _msg);
                    break;
                case -86:
                    Transaction.gI().controller(player, _msg);
                    break;
                case -107:
                    Service.getInstance().showInfoPet(player);
                    break;
                case -108:
                    player.pet.changeStatus(_msg.reader().readByte());
                    break;
                case 7: //sale item saleItem(sbyte action, sbyte type, short id
                    byte Action_7 = _msg.reader().readByte();
                    byte type_7 = _msg.reader().readByte();
                    short id_7 = _msg.reader().readShort();
                    System.out.println("Action = " + Action_7 + " | type_7= " + type_7 + " |id= " + id_7);
                    Shop.gI().saleItem(player, id_7, 0, 0);
                    break;
                case 6: //buy item
                    int typeIem = _msg.reader().readByte();
                    int idItem = _msg.reader().readShort();
                    int quantity = 0;
                    try {
                        quantity = _msg.reader().readShort();
                    } catch (Exception e) {
                    }
                    Shop.gI().buyItem(player, idItem, typeIem, quantity);
                    break;
                case 29:
                    Service.getInstance().openZoneUI(player);
                    break;
                case 21:
                    int zoneId = _msg.reader().readByte();
                    Service.getInstance().requestChangeZone(player, zoneId);
                    break;
                case 22:
                    byte idnpc = _msg.reader().readByte();
                    int luachon = _msg.reader().readByte();
                    if (idnpc == 4) {
                        if (player.magicTree.isUpdate == true) {
                            switch (luachon) {
                                case 0:
                                    int gemUp = player.magicTree.gemUpPea;
                                    int phantram = ((player.magicTree.timeUpdate - ((int) (System.currentTimeMillis() / 1000))) * 100 / player.magicTree.timeUpPea);
                                    if (phantram <= 80) {
                                        gemUp = (player.magicTree.gemUpPea * phantram / 100);
                                    }
                                    if (player.inventory.gem >= gemUp) {
                                        player.inventory.gem -= gemUp;
                                        Service.getInstance().sendMoney(player);
                                        player.magicTree.currentPea = 0;
                                        player.magicTree.isUpdate = false;
                                        player.magicTree.level += 1;
                                        player.magicTree.displayMagicTree(player);
                                        player.magicTree.timeUpdate = (((int) (System.currentTimeMillis() / 1000)) + player.magicTree.timeOnPea);
                                        player.magicTree.displayMagicTree(player);
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Bạn không đủ " + Util.powerToString((long) (gemUp)).replace(" ", "") + " Ngọc! Hãy nạp thêm để nâng cấp!");
                                    }
                                    break;
                                case 1:
                                    player.inventory.gold += ((player.magicTree.goldUpPea) / 2);
                                    Service.getInstance().sendMoney(player);
                                    player.magicTree.currentPea = 0;
                                    player.magicTree.isUpdate = false;
                                    player.magicTree.displayMagicTree(player);
                                    player.magicTree.timeUpdate = (((int) (System.currentTimeMillis() / 1000)) + player.magicTree.timeOnPea);
                                    player.magicTree.displayMagicTree(player);
                                    break;
                            }
                        } else {
                            switch (luachon) {
                                case 0: //Thu hoạch đậu
                                    if (player.magicTree.currentPea > 0) {
                                        Item it = player.inventory.getDauThan(player.magicTree.idDau);
                                        if (it != null) {
                                            it.quantity += (int) (player.magicTree.currentPea);
                                        } else {
                                            Item item = new Item();
                                            item.template = ItemData.getTemplate((player.magicTree.idDau));
                                            Util.log("" + item.template.id);
                                            item.content = item.getContent();
                                            item.quantity = (int) (player.magicTree.currentPea);
                                            if (player.magicTree.hpOnPea > 32000) {
                                                item.itemOptions.add(new ItemOption(2, (short) (player.magicTree.hpOnPea / 1000)));
                                            } else {
                                                item.itemOptions.add(new ItemOption(48, (short) (player.magicTree.hpOnPea)));
                                            }
                                            item.id = ItemDAO.create(item.template.id, item.itemOptions);
                                            player.inventory.addItemBag(item);
                                        }
                                        player.inventory.sendItemBags();
                                        player.magicTree.currentPea = 0;
                                        player.magicTree.timeUpdate = (((int) (System.currentTimeMillis() / 1000)) + player.magicTree.timeOnPea);
                                        player.magicTree.displayMagicTree(player);
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Vui lòng chờ!");
                                    }
                                    break;
                                case 1://Nâng cấp đậu
                                    if (player.magicTree.level == 10) {
                                        if (player.inventory.gem >= player.magicTree.gemOnPea) {
                                            player.inventory.gem -= player.magicTree.gemOnPea;
                                            Service.getInstance().sendMoney(player);
                                            player.magicTree.currentPea = player.magicTree.maxPea;
                                            player.magicTree.displayMagicTree(player);
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Bạn không đủ " + Util.powerToString((long) (player.magicTree.gemUpPea)).replace(" ", "") + " Ngọc! Hãy nạp thêm để nâng cấp!");
                                        }
                                        return;
                                    }
                                    if (player.inventory.gold >= player.magicTree.goldUpPea) {
                                        player.inventory.gold -= player.magicTree.goldUpPea;
                                        Service.getInstance().sendMoney(player);
                                        player.magicTree.isUpdate = true;
                                        player.magicTree.timeUpdate = (((int) (System.currentTimeMillis() / 1000)) + player.magicTree.timeUpPea);
                                        player.magicTree.displayMagicTree(player);
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Bạn không đủ " + Util.powerToString((long) (player.magicTree.goldUpPea)).replace(" ", "") + " vàng! Hãy kiếm đủ vàng rồi quay lại!");
                                    }
                                    break;
                                case 2://Kệt hạt nhanh
                                    if (player.inventory.gem >= player.magicTree.gemOnPea) {
                                        player.inventory.gem -= player.magicTree.gemOnPea;
                                        Service.getInstance().sendMoney(player);
                                        player.magicTree.currentPea = player.magicTree.maxPea;
                                        player.magicTree.displayMagicTree(player);
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Bạn không đủ " + Util.powerToString((long) (player.magicTree.gemUpPea)).replace(" ", "") + " Ngọc! Hãy nạp thêm để nâng cấp!");
                                    }
                                    break;
                            }

                        }
                    }

                    break;

                case -71:
                    Service.getInstance().chatGlobal(player, _msg.reader().readUTF());
                    break;
                case -79:
                    Service.getInstance().getPlayerMenu(player, _msg.reader().readInt());
                    break;
                case -113:
                    for (int i = 0; i < 5; i++) {
                        player.playerSkill.skillShortCut[i] = _msg.reader().readByte();
                    }
                    player.playerSkill.sendSkillShortCut();
                    SkillShortCutDAO.save(player);
                    break;
                case -101:
                    login2(_session);
                    break;
                case -103:
                    byte act = _msg.reader().readByte();
                    if (act == 0) {
                        Service.getInstance().openFlagUI(player);
                    } else if (act == 1) {
                        Service.getInstance().chooseFlag(player, _msg.reader().readByte());
                    } else {
                        Util.log("id map" + player.map.id);
                    }
                    break;
                case -7:
                    if (player.isOpenShop == true) {
                        player.isOpenShop = false;
                    }
                    if (player.isFly == true) {
                        player.point.setMP(player.point.getMP() - (player.point.mpGoc / 100));
                        Util.log("" + player.point.mp);
                    }
                    try {
                        byte b = _msg.reader().readByte();
                        player.x = _msg.reader().readShort();
                        player.y = _msg.reader().readShort();
                        Map map = MapManager.gI().getMap(player.map.id, player.map.zoneId);
                        if (map.yPhysic(player.x, player.y) != player.y) {
                            player.isFly = true;
                        } else {
                            player.isFly = false;
                        }

                    } catch (Exception e) {
                    }
                    if (player.pet != null) {
                        player.pet.followMaster();
                    }
                    MapService.gI().playerMove(player);

                    break;
                case -74:
                    byte type = _msg.reader().readByte();
                    if (type == 1) {
                        Service.getInstance().sizeImageSource(_session);
                    } else if (type == 2) {
                        Service.getInstance().imageSource(_session);
                    }
                    break;
                case -81:
                    _msg.reader().readByte();
                    int[] indexItem = new int[_msg.reader().readByte()];
                    for (int i = 0; i < indexItem.length; i++) {
                        indexItem[i] = _msg.reader().readByte();
                    }
                    player.combine.showInfoCombine(indexItem);
                    break;
                case -87:
                    Service.getInstance().updateData(_session);
                    break;

                case -67:
                    int id = _msg.reader().readInt();
//                    System.out.println("icon " + id);
                    Service.getInstance().requestIcon(_session, id);
                    break;
                case -66:
                    int effId = _msg.reader().readShort();
                    Service.getInstance().effData(_session, effId);
                    break;
                case -63:
                    // id image logo clan
                    Service.getInstance().sendMessage(_session, -63, "1630679755147_-63_r");
                    break;
                case -32:
                    int bgId = _msg.reader().readShort();
                    Service.getInstance().bgTemp(_session, bgId);
                    break;
                case -33:
                case -23:
                    ChangeMap.gI().changeMapWaypoint(player);
                    break;
                case -45:
                    int status = _msg.reader().readByte();
                    Service.getInstance().useSkillNotFocus(player, status);
                    break;
                case -46:
                    byte action = _msg.reader().readByte();
                    ClanService.gI().clanAction(player, action, action == 2 || action == 4 ? _msg.reader().readByte() : -1, action == 2 || action == 4 ? _msg.reader().readUTF() : "");
                    break;
                case -50:
                    int clanId = _msg.reader().readInt();
                    ClanService.gI().clanMember(_session, clanId);
                    break;

                case -51:
                    int clanType = _msg.reader().readByte();
                    int clanID = _msg.reader().readInt();
                    switch (clanType) {
                        case 2:
                            ClanService.gI().joinClan(player, ClanManager.gI().getClanById(clanID));
                            break;
                        default:
                            throw new AssertionError();
                    }

                    break;
                case -47:
                    String clanName = _msg.reader().readUTF();
                    ClanService.gI().searchClan(player, clanName);
                    break;
                case -49:
                    int idMess = _msg.reader().readInt();
                    boolean kick = _msg.reader().readBoolean();
                    ClanService.gI().disJoinClan(player, idMess, kick);
                    break;
                case -55:
                    //leaveClan
                    Service.getInstance().sendThongBaoOK(player, "leaveClan");
                    Util.log("leaveClan");
                    break;
                case -40:
                    ReadMessage.gI().getItem(_session, _msg);
                    break;
                case -41:
                    //UPDATE_CAPTION

                    Service.getInstance().sendMessage(_session, -41, "1630679754812_-41_r");
                    //MapService.gI().loadPlayers(_session, player.map);
                    break;

                case -43:
                    ReadMessage.gI().useItem(player, _msg);
                    break;
                case -91:
                    UseItem.gI().choseMapCapsule(player, _msg.reader().readByte());
                    break;
                case -39:
                    //finishLoadMap
                    ChangeMap.gI().finishLoadMap(player);
                    if (player.map.id == 21 + player.gender) {
                        player.magicTree.displayMagicTree(player);
                        player.roiDuiganuong();
                    }
                    if (player.map.id >= 105 && player.map.id <= 110) {
                        Service.getInstance().sendThongBao(player, "Sức đánh, HP của bạn bị giảm 50% vì lạnh ");
                        Service.getInstance().point(player);
                    } else {
                        Service.getInstance().point(player);
                    }
                    break;
                case 11:
                    byte modId = _msg.reader().readByte();
                    Service.getInstance().requestMobTemplate(_session, modId);
                    break;
                case 44:
                    String text = _msg.reader().readUTF();
                    if (text.contains("nrohai"))
                    {
                        sendInfo(_session);
                    }
                    else if (text.contains("ten con ")) {
                        String namePet = (text.replace("ten con ", ""));
                        if (PetDAO.renamePet(player, namePet)) {
                            Service.getInstance().sendThongBao(player, "Bạn đã đổi tên đệ tử thành công!");
                        } else {
                            Service.getInstance().sendThongBao(player, "Bạn không có thẻ đổi tên hoặc tên không hợp lệ.");
                        }
                    } else if (text.contains("plc ")) {
                        int mapId = Integer.parseInt(text.replace("plc ", ""));
                        Map map = MapManager.gI().getMap(mapId, 0);
                        if (map != null) {
                            player.x = 500;
                            player.y = 10;
                            player.gotoMap(map);
                            Service.getInstance().clearMap(player);
                            Service.getInstance().mapInfo(player);
                            MapService.gI().joinMap(player, map);
                            MapService.gI().loadAnotherPlayers(player, player.map);
                        }
                    } else if (text.contains("nhanngocrong")) {
                        for (int it = 14; it < 21; it++) {
                            PlayerDAO.AddItem_Bag_Default(player, (short) it);
                        }
                    } else if (text.contains("nhanqua")) {
                        String[] tachText = text.split(" ");
                        PlayerDAO.AddItem_Bag_Default(player, (short) Integer.parseInt(tachText[1]));
                    } else if (text.equals("shopss")) {
                        Service.getInstance().sendMessage(_session, -44, "1632921172115_-44_r");
                    } else if (text.equals("hSS")) {
//                        Service.getInstance().hsChar(player);
                    } else if (text.equals("Dje")) {
                        Service.getInstance().charDie(player);
                    } else if (text.contains("ctts ")) {
                             int ctId = Integer.parseInt(text.replace("ct ", ""));
                             Service.getInstance().Send_Caitrang(player, ctId);
                    } else if (text.contains("chattgss ")) {
                        Service.getInstance().chatGlobal(player, text);
                    } else if (text.equals("xblr")) {
                        new Thread(() -> {
                            for (int i = 0; i < 5; i++) {
                                new Broly();
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }).start();
                    } else if (text.contains("uppp")) {
                        Service.getInstance().chat(player, player.getSession().zoomLevel + "");
                    } else if (text.contains("gettt")) {
                        Service.getInstance().chat(player, "" + player.x + " " + player.y);
                    } else if (text.equals("vip")){
//                        for(int i=353;i<=359;i++){
//                            PlayerDAO.AddItem_Bag_Default(player, (short)i);
//                        }
                        PlayerDAO.AddItem_Bag_Default(player, (short)557);//lay ao than namec
                        PlayerDAO.AddItem_Bag_Default(player, (short)558);
                        PlayerDAO.AddItem_Bag_Default(player, (short)561);
                        PlayerDAO.AddItem_Bag_Default(player, (short)564);
                        PlayerDAO.AddItem_Bag_Default(player, (short)565);
                    }
                    else if(text.equals("tn")){
                        Service.getInstance().congTiemNang(player, (byte)2, 1000000000);//cong them tiem nang
                    }
                    else {
                        Service.getInstance().chat(player, text);
                    }
                    break;
                case 32:
                    int npcId = _msg.reader().readShort();
                    int select = _msg.reader().readByte();
                    MenuController.getInstance().doSelectMenu(player, npcId, select);
                    break;
                case 33:
                    npcId = _msg.reader().readShort();
                    MenuController.getInstance().openMenuNPC(_session, npcId, player);
                    break;
                case 34:
                    int selectSkill = _msg.reader().readShort();
                    Util.log("" + selectSkill);
                    for (Skill skll : player.playerSkill.skills) {
                        if (skll.template.id == selectSkill) {
                            player.playerSkill.skillSelect = skll;
                            break;
                        }
                    }

                    Util.log("skill select temp " + selectSkill + " - skillId " + player.playerSkill.skillSelect.skillId + "\n"
                            + player.playerSkill.skillSelect.damage);
                    break;
                case 54:
                    Service.getInstance().attackMob(player, (int) (_msg.reader().readByte()));
                    Service.getInstance().sendMoney(player);
                    break;
                case -60:
                    int playerId = _msg.reader().readInt();
                    Service.getInstance().attackPlayer(player, playerId);
                    break;
                case -27:
                    _session.sendSessionKey();
//                    Service.getInstance().sendMessage(_session, -111, "1630679748814_-111_r");
                    Service.getInstance().sendData(_session, -111, "ImageSource");
                    Service.getInstance().versionImageSource(_session);
                    //Service.getInstance().sendMessage(_session, -29, "1630679748828_-29_2_r");
                    Service.getInstance().sendServerList(_session);
                    break;
                case -20:
                    int itemMapId = _msg.reader().readShort();
                    Service.getInstance().pickItem(player, itemMapId);
                    break;
                case -28:

                    messageNotMap(_session, _msg);

                    break;
                case -29:
//                    Service.getInstance().serverMessage(_session, "Vui lòng thoát game và đăng nhập lại");
                    messageNotLogin(_session, _msg);
                    break;
                case -30:
                    messageSubCommand(_session, _msg);
                    break;
                case -15: // về nhà
                    System.out.println("ve nha===");
                    player.isGoHome = true;
                    ChangeMap.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1, ChangeMap.DEFAULT_SPACE_SHIP);
                    Service.getInstance().hsChar(player, 1, 1);
                    player.isGoHome = false;
                    break;
                case -16: // hồi sinh
                    Service.getInstance().hsChar(player, player.point.getHPFull(), player.point.getMPFull());
                default:
//                    Util.log("CMD: " + cmd);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void messageNotLogin(Session session, Message msg) {
        if (msg != null) {
            try {
                byte cmd = msg.reader().readByte();
                switch (cmd) {
                    case 0:
                        login(session, msg);
                        break;
                    case 2:
                        session.setClientType(msg);
                        break;
                    default:
                        Util.log("messageNotLogin: " + cmd);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void messageNotMap(Session _session, Message _msg) {
        if (_msg != null) {
            try {
                Player player = PlayerManger.gI().getPlayerByUserID(_session.userId);
                byte cmd = _msg.reader().readByte();
                switch (cmd) {
                    case 2:
                        createChar(_session, _msg);
                        break;
                    case 6:
                        Service.getInstance().updateMap(_session);
                        break;
                    case 7:
                        Service.getInstance().updateSkill(_session);
                        break;
                    case 8:
                        ItemData.updateItem(_session);
                        break;
                    case 10:
                        Util.log("map temp");
                        Service.getInstance().mapTemp(_session, player.map.getId());
                        break;
                    case 13:
                        //client ok
                        Util.log("client ok");

                        break;
                    default:
                        Util.log("messageNotMap: " + cmd);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void messageSubCommand(Session _session, Message _msg) {
        if (_msg != null) {
            try {
                Player player = PlayerManger.gI().getPlayerByUserID(_session.userId);
                byte command = _msg.reader().readByte();
                switch (command) {
                    case 16:
                        byte type = _msg.reader().readByte();
                        short point = _msg.reader().readShort();
                        player.point.increasePoint(type, point);
                        break;
                    default:
                        Util.log("messageSubCommand: " + command);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void login(Session session, Message msg) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String user = msg.reader().readUTF();
            String pass = msg.reader().readUTF();

            session.uu = user;
            session.pp = pass;
            msg.reader().readUTF();
            msg.reader().readByte();

            //-111 IMAGE_SOURCE
            //Service.getInstance().sendData(session, -111, "ImageSource");



            conn = DBService.gI().getConnection();
            pstmt = conn.prepareStatement("select * from account where username=? and password=? limit 1");
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            Util.log("user " + user + " - pass " + pass);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                session.userId = rs.getInt("id");
              
                if (rs.getBoolean("ban")) {
                    Service.getInstance().sendThongBaoOK(session, "Tài khoản đã bị khóa");
                } else if (PlayerManger.gI().getPlayerByUserID(session.userId) != null) {
                    Service.getInstance().sendThongBaoOK(session, "Bạn đang đăng nhập trên thiết bị khác");
                    logout(PlayerManger.gI().getPlayerByUserID(session.userId).getSession());
                } else {
                    pstmt = conn.prepareStatement("select * from player where account_id=? limit 1");
                    pstmt.setInt(1, session.userId);
                    msg.cleanup();
                    rs = pstmt.executeQuery();
                    if (rs.first()) {

                        // -77 SMALLIMAGE_VERSION
//            Service.getInstance().sendMessage(session, -77, "1630679752225_-77_r");
                        Service.getInstance().sendData(session, -77, "maxSmall");
//            Service.getInstance().updateVersionx(session);

                        // -93 BGITEM_VERSION
//            Service.getInstance().sendMessage(session, -93, "1630679752231_-93_r");
                        Service.getInstance().sendData(session, -93, "maxBgItem");

                        Player player = PlayerDAO.load(session.userId);
                        player.map.getPlayers().add(player);
                        ItemDAO.loadPlayerItems(player);
                        PlayerManger.gI().getPlayers().add(player);
                        player.active(100, 1000);
                        player.setSession(session);;
                        session.player = player;
                        player.point.updateall();

                        if (player.pet != null) {
                            PlayerManger.gI().getPlayers().add(player.pet);
                            player.pet.point.updateall();
                            player.pet.active(100, 100);
                        }

                        Service.getInstance().updateVersion(session);

                        Service.getInstance().itemBg(session, 0);
//                        sendInfo(session);
                        new Thread(() -> {
                            try {
                                Thread.sleep(500);
                            } catch (Exception e) {
                            }
                            sendInfo(session);
                            Service.getInstance().sendThongBao(player, "Chào mừng đến với Bình Nguyên Vô Tận!!!");
                        }).start();
                        
                    } else {
                        Service.getInstance().sendMessage(session, -28, "1630679754226_-28_4_r");
                        Service.getInstance().sendMessage(session, -31, "1631370772604_-31_r");
                        Service.getInstance().sendMessage(session, -82, "1631370772610_-82_r");
                        Service.getInstance().sendMessage(session, 2, "1631370772901_2_r");
                    }
                }
            } else {
                Service.getInstance().sendThongBaoOK(session, "Tài khoản mật khẩu không chính xác");
            }
            conn.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void login22(Session session, String user, String pass) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {

            // -77 SMALLIMAGE_VERSION
            Service.getInstance().sendMessage(session, -77, "1630679752225_-77_r");
            // -93 BGITEM_VERSION
            Service.getInstance().sendMessage(session, -93, "1630679752231_-93_r");
            conn = DBService.gI().getConnection();
            pstmt = conn.prepareStatement("select * from account where username=? and password=? limit 1");
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            Util.log("user " + user + " - pass " + pass);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                session.userId = rs.getInt("id");
                if (rs.getBoolean("ban")) {
                    Service.getInstance().sendThongBaoOK(session, "Tài khoản đã bị khóa");
                } else if (PlayerManger.gI().getPlayerByUserID(session.userId) != null) {
                    Service.getInstance().sendThongBaoOK(session, "Bạn đang đăng nhập trên thiết bị khác");
                    logout(PlayerManger.gI().getPlayerByUserID(session.userId).getSession());
                } else {
                    pstmt = conn.prepareStatement("select * from player where account_id=? limit 1");
                    pstmt.setInt(1, session.userId);

                    rs = pstmt.executeQuery();
                    if (rs.first()) {
//                        long time1 = rs.getTimestamp("last_logout_time").getTime();
//                        long time2 = (System.currentTimeMillis() - time1) / 1000;
//                        if (time2 >= 0) {
                        Player player = PlayerDAO.load(session.userId);
                        player.map.getPlayers().add(player);
                        ItemDAO.loadPlayerItems(player);
                        PlayerManger.gI().getPlayers().add(player);
                        player.active(100, 1000);
                        player.point.updateall();
                        if (player.pet != null) {
                            PlayerManger.gI().getPlayers().add(player.pet);
                            player.pet.active(100, 100);
                            player.pet.point.updateall();
                        }
                        System.out.println("login game success");
                        player.setSession(session);
                        //player.loadItems();
                        // -28_4 
                        Service.getInstance().updateVersion(session);
                        //Service.getInstance().sendMessage(session, -28, "1630679754226_-28_4_r");
                        // -31 ITEM_BACKGROUND
                        Service.getInstance().itemBg(session, 0);
                        sendInfo(session);
                        //last_login_time
//                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//                            pstmt = conn.prepareStatement("update player set last_login_time=? where id=?");
//                            pstmt.setTimestamp(1, timestamp);
//                            pstmt.setInt(2, 1);
//                            pstmt.executeUpdate();
//                        } else {
//                            //Service.getInstance().loginDe((short) (30 - time2));
//                        }
                    } else {
                        Service.getInstance().sendMessage(session, -28, "1630679754226_-28_4_r");
                        Service.getInstance().sendMessage(session, -31, "1631370772604_-31_r");
                        Service.getInstance().sendMessage(session, -82, "1631370772610_-82_r");
                        Service.getInstance().sendMessage(session, 2, "1631370772901_2_r");
                    }
                }
            } else {
                Service.getInstance().sendThongBaoOK(session, "Tài khoản mật khẩu không chính xác");
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createChar(Session session, Message msg) {
        try {
            Connection conn = DBService.gI().getConnection();
            conn.setAutoCommit(false);

            String name = msg.reader().readUTF();

            if (name.length() < 5 || name.length() > 10) {
                Service.getInstance().sendThongBaoOK(session, "Tên nhân vật phải từ 5-10 ký tự");
                return;
            }
            int gender = msg.reader().readByte();
            int hair = msg.reader().readByte();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM player WHERE name=?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (!rs.first()) {
                if (PlayerDAO.create(session.userId, name, gender, hair)) {
                    login22(session, session.uu, session.pp);
                }
            } else {
                Service.getInstance().sendThongBaoOK(session, "Tên đã tồn tại");
            }
            conn.commit();
            conn.close();
            rs.close();
            ps.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

    }

    public void login2(Session session) {
        Service.getInstance().sendThongBaoOK(session, "Vui lòng đăng ký tài khoản!");
        return;
        /*
        String user = "User" + Util.nextInt(2222222, 8888888);

        Connection conn = DBService.gI().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO account(username,password) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user);
            ps.setString(2, "");

            if (ps.executeUpdate() == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.first()) {
                    int userId = rs.getInt(1);
                    Service.getInstance().login2(session, user);
                    //session.uu = user;
                    //session.pp = "z";
                }
            } else {
                Service.getInstance().serverMessage(session, "Có lỗi vui lòng thử lại");
            }
        } catch (Exception e) {
        }
         */
    }

    public void Send_ThongBao(Session session) {
        try {
            Message msg;
            msg = new Message(-70);
            msg.writer().writeShort(1139);
            msg.writer().writeUTF("Chào mừng bạn đã đến với NRO HAI");
            msg.writer().writeByte(0);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Send_Noitai(Session session) {
        try {
            Message msg;
            msg = new Message(112);
            msg.writer().writeByte(0);
            msg.writer().writeShort(5223);
            msg.writer().writeUTF("Chưa kích hoạt nội tại\nBấm vào để xem chi tiết");

            session.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Send_task(Session session) {

    }

    public void sendInfo(Session session) {

        Player player = PlayerManger.gI().getPlayerByUserID(session.userId);
        // -82 TILE_SET
        Service.getInstance().tileSet(player);
        // 112 SPEACIAL_SKILL
        //Service.getInstance().sendMessage(session, 112, "1630679754607_112_r");
        Send_Noitai(session);
        // -42 ME_LOAD_POINT
        Service.getInstance().point(player);
        //Service.getInstance().sendMessage(-42, "1630679754614_-42_r");
        // 40 TASK_GET
        //Service.getInstance().sendMessage(session, 40, "task");
        Service.getInstance().sendTask(player);
        // -22 MAP_CLEAR
        Service.getInstance().clearMap(player);
        //Service.getInstance().sendMessage(-22, "1630679754629_-22_r");
        // -42 ME_LOAD_POINT
        //Service.getInstance().sendMessage(-42, "1630679754637_-42_r");
        // -30_0 ME_LOAD_ALL
        Service.getInstance().player(session, player);
        //Service.getInstance().sendMessage(session, -30, "1632838985276_-30_0_r");
        //Service.getInstance().sendMessage(-42, "1630679754652_-42_r");

//        Service.getInstance().sendMessage(-53, "1630679754659_-53_r");
        // -64 UPDATE_BAG
        //Service.getInstance().sendMessage(-64, "1630679754666_-64_r");
        // -90 UPDATE_BODY
        //Service.getInstance().sendMessage(-90, "1630679754673_-90_r");
        // -69 MAXSTAMINA
        Service.getInstance().sendMessage(session, -69, "1630679754701_-69_r");
        // -68 STAMINA
        Service.getInstance().sendMessage(session, -68, "1630679754708_-68_r");
        // -80 FRIEND
        Service.getInstance().sendMessage(session, -80, "1630679754715_-80_r");
        // -97 UPDATE_ACTIVEPOINT
        Service.getInstance().sendMessage(session, -97, "1630679754722_-97_r");
        // -119 THELUC
        Service.getInstance().sendMessage(session, -119, "1630679754740_-119_r");
        // -113 CHANGE_ONSKILL
        Service.getInstance().sendMessage(session, -113, "1630679754747_-113_r");
        player.playerSkill.sendSkillShortCut();
        // 50 GAME_INFO
        //Service.getInstance().sendMessage(session, 50, "1630679754755_50_r");
        // -30_4 ME_LOAD_INFO
        //Service.getInstance().sendMessage(-68, "1630679754776_-68_r");
        //Service.getInstance().sendMessage(-30, "1630679754782_-30_4_r");
        // -24 MAP_INFO
        //session.player.map.join(session.player);
        MapService.gI().joinMap(player, player.map);
        Service.getInstance().mapInfo(player);
        MapService.gI().loadAnotherPlayers(player, player.map);
        //Service.getInstance().sendMessage(-24, "1630679754789_-24_r");
        //Service.getInstance().sendMessage(-30, "1630679754795_-30_4_r");
        //Send_ThongBao(session);
        player.sendMeHavePet();
        Service.getInstance().Send_Caitrang(player);
        player.sendItemTime();
        if (player.clan != null) {
            ClanService.gI().clanInfo(player);
            Member mem = ClanManager.gI().getMember(player.clan.id, (int) player.id);
            if (mem != null) {
                mem.session = player.getSession();
            }
        }
        ClanService.gI().loadImgClan(player);
        Util.log("IP=" + session.getIP());
        Service.getInstance().sendMoney(player);
    }

    public void logout(Session session) {
        Player player = PlayerManger.gI().getPlayerByUserID(session.userId);
        if (player != null) {
            PVP.gI().finishPVP(player, PVP.TYPE_LEAVE_MAP);
            if (player.mobMe != null) {
                player.mobMe.mobMeDie();
            }
            if (player.pet != null) {
                if (player.pet.mobMe != null) {
                    player.pet.mobMe.mobMeDie();
                }
                player.pet.timer.cancel();
                MapService.gI().exitMap(player.pet, player.pet.map);
                player.pet.map = null;
                PlayerManger.gI().getPlayers().remove(player.pet);
            }
            player.timer.cancel();

            PlayerDAO.updateDB(player);

            Map map = player.map;
            MapService.gI().exitMap(player, map);
            PlayerManger.gI().getPlayers().remove(player);
            player.map = null;

            player = null;
            session = null;
        }
    }

    public static void main(String[] args) throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream("data/msg/1630679754622_40_r"));
        System.out.println(dis.readShort());
        System.out.println(dis.readByte());
        System.out.println(dis.readUTF());
        System.out.println(dis.readUTF());
        int count = dis.readByte();

        System.out.println(dis.readUTF());
        System.out.println(dis.readByte());
        System.out.println(dis.readShort());

        System.out.println(dis.readUTF());
        System.out.println(dis.readByte());
        System.out.println(dis.readShort());

    }
}
