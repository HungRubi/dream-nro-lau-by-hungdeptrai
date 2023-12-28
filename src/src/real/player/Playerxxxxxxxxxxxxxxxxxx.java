//package real.player;
////share by chibikun
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import real.clan.Clan;
//import real.item.Item;
//import real.item.ItemShopDAO;
//import real.magictree.MagicTree;
//import real.map.ItemMap;
//import real.map.Map;
//import real.map.MapService;
//import real.map.Mob;
//import real.map.Npc;
//import real.map.WayPoint;
//import real.skill.NClass;
//import real.skill.Skill;
//import server.Service;
//import server.Util;
//import server.io.Message;
//import server.io.Session;
//
//public class Player {
//
//    public int id;
//    public short x;
//    public short y;
//    public String name;
//    public byte gender;
//    public long power;
//    public long tiemNang;
//    public int hpGoc;
//    public int mpGoc;
//    private int hp;
//    private int mp;
//    public int damGoc;
//    public short defGoc;
//    public byte critGoc;
//    public int vang;
//    public int ngocXanh;
//    public int ngocDo;
//
//    public boolean isCombine = false;
//    public int typeCombine;
//    public int tiLeCombine;
//    public int giaCombine;
//    public short head;
//
//    public short taskId;
//    public byte taskIndex;
//    public Map map;
//    public Session session;
//    public List<Item> itemsCombine;
//
//    public int teleport;
//    public int sendteleport;
//    public byte typePk;
//    public byte cFlag;
//
//    public int die;
//    public byte limitPower;
//
//    public ArrayList<Skill> skills;
//    public NClass nClass;
//    public Clan clan;
//    public Skill selectSkill;
//    public ArrayList<Item> itemsBody;
//
//    public ArrayList<Item> itemsBag;
//
//    public ArrayList<Item> itemsBox;
//    public int openMenu = 0;
//    
//    public MagicTree magicTree;
//    
//    
//    public int getHpMpLimit() {
//        if (limitPower == 0) {
//            return 220000;
//        }
//        if (limitPower == 1) {
//            return 240000;
//        }
//        if (limitPower == 2) {
//            return 300000;
//        }
//        if (limitPower == 3) {
//            return 350000;
//        }
//        if (limitPower == 4) {
//            return 400000;
//        }
//        if (limitPower == 5) {
//            return 450000;
//        }
//        return 0;
//    }
//
//    public int getDamLimit() {
//        if (limitPower == 0) {
//            return 11000;
//        }
//        if (limitPower == 1) {
//            return 12000;
//        }
//        if (limitPower == 2) {
//            return 15000;
//        }
//        if (limitPower == 3) {
//            return 18000;
//        }
//        if (limitPower == 4) {
//            return 20000;
//        }
//        if (limitPower == 5) {
//            return 22000;
//        }
//        return 0;
//    }
//
//    public short getDefLimit() {
//        if (limitPower == 0) {
//            return 550;
//        }
//        if (limitPower == 1) {
//            return 600;
//        }
//        if (limitPower == 2) {
//            return 700;
//        }
//        if (limitPower == 3) {
//            return 800;
//        }
//        if (limitPower == 4) {
//            return 100;
//        }
//        if (limitPower == 5) {
//            return 22000;
//        }
//        return 0;
//    }
//
//    public byte getCrifLimit() {
//        if (limitPower == 0) {
//            return 5;
//        }
//        if (limitPower == 1) {
//            return 6;
//        }
//        if (limitPower == 2) {
//            return 7;
//        }
//        if (limitPower == 3) {
//            return 8;
//        }
//        if (limitPower == 4) {
//            return 9;
//        }
//        if (limitPower == 5) {
//            return 10;
//        }
//        return 0;
//    }
//    
//    
//
//    public void setUncombine() {
//        isCombine = false;
//        typeCombine = -1;
//        tiLeCombine = -1;
//        giaCombine = -1;
//    }
//    
//    
//
//    public void setItemBag(Item item) {
//        for (int i = 0; i < itemsBag.size(); i++) {
//            if (itemsBag.get(i).id == item.id) {
//                itemsBag.set(i, item);
//                break;
//            }
//        }
//    }
//    
//    
//
//    public long getPower() {
//        return power * 1L;
//    }
//
//    public void setPower(long power) {
//        this.power = power;
//    }
//    
//    
//    public int getSendteleport() {
//        return sendteleport;
//    }
//
//    public void setSendteleport(int sendteleport) {
//        this.sendteleport = sendteleport;
//    }
//
//    public int getTeleport() {
//        return teleport;
//    }
//
//    public void setTeleport(int teleport) {
//        this.teleport = teleport;
//    }
//    
//    public int getDie() {
//        return die;
//    }
//
//    public void setDie(int die) {
//        this.die = die;
//    }
//    
//    
//
//    public int getHp() {
//        return hp <= getHpFull() ? hp : getHpFull();
//    }
//
//    public void setHp(int hp) {
//        this.hp = hp;
//    }
//    public int getMp() {
//        return mp;
//    }
//
//    public void setMp(int mp) {
//        this.mp = mp;
//    }
//    
//    
//
//    public int injured(int hp) {
//        if (isShielding) {
//            if (hp > getHpFull()) {
//                shieldDown();
//                Service.getInstance().sendThongBao(session, "Khiên năng lượng đã bị vỡ!");
//                Service.getInstance().sendItemTime(this, 3784, 0);
//            }
//            hp = 1;
//        }
//        this.hp -= hp;
//        if (this.hp <= 0) {
//            this.hp = 0;
//            Service.getInstance().charDie(session, this);
//        }
//        Service.getInstance().point(session, this);
//        Service.getInstance().Send_Info_NV(session, this);
//        return hp;
//    }
//
//    public void set_mana_skill(int mp) {
//        this.mp -= mp;
//    }
//    
//    
//
//    public short getHead() {
//        if (levelMonkey != 0) {
//            return (short) HEADMONKEY[levelMonkey - 1];
//        } else if (this.itemsBody.get(5).id != -1) {
//            if (ItemShopDAO.getByTemp(itemsBody.get(5).template.id).head != -1) {
//                return (short) ItemShopDAO.getByTemp(itemsBody.get(5).template.id).head;
//            } else if (itemsBody.get(5).template.part != -1) {
//                return (short) itemsBody.get(5).template.part;
//            }
//        }
//        return head;
//    }
//
//    public short getBody() {
//        if (levelMonkey != 0) {
//            return 193;
//        } else if (this.itemsBody.get(5).id != -1) {
//            if (ItemShopDAO.getByTemp(itemsBody.get(5).template.id).body != -1) {
//                return (short) ItemShopDAO.getByTemp(itemsBody.get(5).template.id).body;
//            }
//        }
//        if (itemsBody.get(0).id != -1) {
//            return itemsBody.get(0).template.part;
//        }
//        return (short) (gender == 1 ? 59 : 57);
//    }
//
//    public short getLeg() {
//        if (levelMonkey != 0) {
//            return 194;
//        } else if (this.itemsBody.get(5).id != -1) {
//            if (ItemShopDAO.getByTemp(itemsBody.get(5).template.id).leg != -1) {
//                return (short) ItemShopDAO.getByTemp(itemsBody.get(5).template.id).leg;
//            }
//        }
//        if (itemsBody.get(1).id != -1) {
//            return itemsBody.get(1).template.part;
//        }
//        return (short) (gender == 1 ? 60 : 58);
//    }
//
//    public short getMount() {
//        for (Item item : itemsBag) {
//            if (item.id != -1) {
//                if (item.template.type == 23 || item.template.type == 24) {
//                    if (item.template.gender == 3 || item.template.gender == this.gender) {
//                        return item.template.id;
//                    } else {
//                        return -1;
//                    }
//                }
//            }
//        }
//        return -1;
//    }
//    
//
//    public void set_tiemnang(int diem) {
//        this.tiemNang = this.tiemNang + diem;
//    }
//
//    public void set_sucmanh(int diem) {
//        this.power = this.power + diem;
//    }
//    
//    
//
//    public int getHpFull() {
//        int hpFull = this.hpGoc;
//        if (this.levelMonkey != 0) {
//            int percent = getPercentHpMonkey(this.levelMonkey);
//            hpFull = hpFull + (hpFull * percent) / 100;
//        }
//        hpFull += (hpFull * this.tiLeHPHuytSao) / 100;
//        return hpFull;
//    }
//
//    public int getMpFull() {
//        return mpGoc;
//    }
//    
//
//    public int getDamFull() {
//        return damGoc;
//    }
//
//    public short getDefFull() {
//        return defGoc;
//    }
//
//    public byte getCritFull() {
//        if (this.levelMonkey != 0) {
//            return 110;
//        }
//        return critGoc;
//    }
//
//    public byte getSpeed() {
//        return 7;
//    }    
//    
//
//    public boolean addItemBag(Item item) {
//        for (int i = 0; i < itemsBag.size(); i++) {
//            if (itemsBag.get(i).id == -1) {
//                itemsBag.set(i, item);
//                return true;
//            }
//        }
//        Service.getInstance().sendThongBao(this.session, "Hành trang không đủ chỗ trống!");
//        return false;
//    }
//    
//
//    public void setItemBody(Item item) {
//        int index = -1;
//        if (item.id != -1) {
//            if (item.template.gender == this.gender || item.template.gender == 3) {
//                if (item.template.strRequire <= this.power) {
//                    if (item.template.type >= 0 && item.template.type <= 5) {
//                        index = item.template.type;
//                    }
//                    if (item.template.type == 32) {
//                        index = 6;
//                    }
//                } else {
//                    addItemBag(item);
//                    Service.getInstance().serverMessage(this.session, "Sức mạnh không đủ yêu cầu");
//                }
//            } else {
//                addItemBag(item);
//                Service.getInstance().serverMessage(this.session, "Sai hành tinh");
//            }
//        }
//        if (index != -1) {
//            if (this.itemsBody.get(index).id != -1) {
//                addItemBag(this.itemsBody.get(index));
//            }
//            this.itemsBody.set(index, item);
//        }
//    }
//    
//    
//
//    public void itemBodyToBag(int index) {
//        if (checkBag() == 0) {
//            Service.getInstance().serverMessage(this.session, "Cần ít nhất 1 ô trống tại hành trang");
//            return;
//        }
//        Item _item = this.itemsBody.get(index);
//        removeItemBody(index);
//        for (int i = 0; i < this.itemsBag.size(); i++) {
//            Item item = this.itemsBag.get(i);
//            if (item.id == -1) {
//                addItemBag(_item);
////                Service.getInstance().updateChar(session, this);
//                Service.getInstance().updateItemBody(session, this);
//                Service.getInstance().updateItemBag(session, this);
//                Service.getInstance().Send_Caitrang(this);
//                break;
//            }
//        }
//    }
//
//    public void itemBagToBody(int index) {
//        Item item = this.itemsBag.get(index);
//        removeItemBag(index);
//        if (item.template.gender != this.gender && item.template.gender != 3) {
//            Service.getInstance().serverMessage(this.session, "Sai hành tinh");
//        } else if (item.template.strRequire > this.power) {
//            Service.getInstance().serverMessage(this.session, "Sức mạnh không đủ yêu cầu");
//        } else {
//            setItemBody(item);
//
//            Service.getInstance().updateItemBag(session, this);
//            Service.getInstance().updateItemBody(session, this);
//            Service.getInstance().Send_Caitrang(this);
//        }
//    }
//
//    public void removeItemBag(int index) {
//        Item item = new Item();
//        item.id = -1;
//        this.itemsBag.set(index, item);
//    }
//
//    public void removeItemBody(int index) {
//        Item item = new Item();
//        item.id = -1;
//        this.itemsBody.set(index, item);
//    }
//
//    public void removeItemBox(int index) {
//        Item item = new Item();
//        item.id = -1;
//        this.itemsBody.set(index, item);
//    }
//    
//
//    public int checkBag() {
//        int dem = 0;
//        for (int i = 0; i < this.itemsBag.size(); i++) {
//            Item item = this.itemsBag.get(i);
//            if (item.id == -1) {
//                dem += 1;
//            }
//        }
//        return dem;
//    }
//    
//    public void increasePoint(byte type, short point) {
//        if (point <= 0) {
//            return;
//        }
//        long tiemNangUse = 0;
//        if (type == 0) {
//            int pointHp = point * 20;
//            tiemNangUse = point * (2 * (this.hpGoc + 1000) + pointHp - 20) / 2;
//            if ((this.hpGoc + pointHp) <= getHpMpLimit()) {
//                if (useTiemNang(tiemNangUse)) {
//                    hpGoc += pointHp;
//                }
//            } else {
//                Service.getInstance().serverMessage(this.session, "Vui lòng mở giới hạn sức mạnh");
//                return;
//            }
//        }
//        if (type == 1) {
//            int pointMp = point * 20;
//            tiemNangUse = point * (2 * (this.mpGoc + 1000) + pointMp - 20) / 2;
//            if ((this.mpGoc + pointMp) <= getHpMpLimit()) {
//                if (useTiemNang(tiemNangUse)) {
//                    mpGoc += pointMp;
//                }
//            } else {
//                Service.getInstance().serverMessage(this.session, "Vui lòng mở giới hạn sức mạnh");
//                return;
//            }
//        }
//        if (type == 2) {
//            tiemNangUse = point * (2 * this.damGoc + point - 1) / 2 * 100;
//            if ((this.damGoc + point) <= getDamLimit()) {
//                if (useTiemNang(tiemNangUse)) {
//                    damGoc += point;
//                }
//            } else {
//                Service.getInstance().serverMessage(this.session, "Vui lòng mở giới hạn sức mạnh");
//                return;
//            }
//        }
//        if (type == 3) {
//            tiemNangUse = 2 * (this.defGoc + 5) / 2 * 100000;
//            if ((this.defGoc + point) <= getDefLimit()) {
//                if (useTiemNang(tiemNangUse)) {
//                    defGoc += point;
//                }
//            } else {
//                Service.getInstance().serverMessage(this.session, "Vui lòng mở giới hạn sức mạnh");
//                return;
//            }
//        }
//        if (type == 4) {
//            tiemNangUse = 50000000L;
//            for (int i = 0; i < this.critGoc; i++) {
//                tiemNangUse *= 5L;
//            }
//            if ((this.critGoc + point) <= getCrifLimit()) {
//                if (useTiemNang(tiemNangUse)) {
//                    critGoc += point;
//                }
//            } else {
//                Service.getInstance().serverMessage(this.session, "Vui lòng mở giới hạn sức mạnh");
//                return;
//            }
//        }
//        Service.getInstance().point(this.session, this);
//    }
//    
//
//    
//
//    public void move(short _toX, short _toY) {
//        if (_toX != this.x) {
//            this.x = _toX;
//        }
//        if (_toY != this.y) {
//            this.y = _toY;
//        }
//        MapService.gI().playerMove(this);
//    }
//    
//
//    public WayPoint isInWaypoint() {
//        for (WayPoint wp : map.wayPoints) {
//            if (x >= wp.minX && x <= wp.maxX && y >= wp.minY && y <= wp.maxY) {
//                return wp;
//            }
//        }
//        return null;
//    }
//    
//    
//
//    public void gotoMap(Map _map) {
//        if (this.map != null && _map != null) {
//            this.map.getPlayers().remove(this);
//            if (this.map != _map) {
//                //send msg exit to player in map
//                MapService.gI().exitMap(this, this.map);
//            }
//            this.map = _map;
//            this.map.getPlayers().add(this);
//        }
//    }
//    
//
//    public boolean useTiemNang(long tiemNang) {
//        if (this.tiemNang < tiemNang) {
//            Service.getInstance().serverMessage(this.session, "Bạn không đủ tiềm năng");
//            return false;
//        }
//        if (this.tiemNang >= tiemNang) {
//            this.tiemNang -= tiemNang;
//            return true;
//        }
//        return false;
//    }
//    
//    
//    public void hoiphuc(int hp, int mp) { //Hồi phục máu + mana
//        if (this.getHp() == 0 || this.die == 1) {
//            return;
//        }
//        if (this.hp + hp >= this.getHpFull()) {
//            this.hp = this.getHpFull();
//        } else {
//            this.hp += hp;
//        }
//        if (this.mp + mp >= this.getMpFull()) {
//            this.mp = this.getMpFull();
//        } else {
//            this.mp += mp;
//        }
//        try {
//            Message msg;
//            msg = Service.getInstance().messageSubCommand1((byte) 5);
//            msg.writer().writeInt(this.hp);
//            this.session.sendMessage(msg);
//            msg.cleanup();
//            msg = Service.getInstance().messageSubCommand1((byte) 6);
//            msg.writer().writeInt(this.mp);
//            this.session.sendMessage(msg);
//            msg.cleanup();
//        } catch (IOException e) {
//
//        }
//    }
//    
//
//    public Skill getSkillbyId(int id) {
//        for (Skill skill : skills) {
//            if (skill.template.id == id) {
//                return skill;
//            }
//        }
//        return null;
//    }
//    
//    
//
//    public void throwItem(int where, int index) {
//        Item itemThrow = null;
//        if (where == 0) {
//            itemThrow = itemsBody.get(index);
//            removeItemBody(index);
//        } else if (where == 1) {
//            Service.getInstance().updateItemBody(session, this);
//        } else if (where == 1) {
//            itemThrow = itemsBag.get(index);
//            removeItemBag(index);
//            Service.getInstance().updateItemBag(session, this);
//        }
//        if (itemThrow == null) {
//            return;
//        }
//
//        ItemMap itemMap = new ItemMap(map, itemThrow.template, itemThrow.quantity, this.x, this.y, this.id);
//        itemMap.options = itemThrow.itemOptions;
//        Service.getInstance().roiItem(this, itemMap);
//        Service.getInstance().Send_Caitrang(this);
//
//    }
//    
//
//    public void update() {
//        //Service.getInstance().point(session, this);
//        Service.getInstance().Send_Info_NV(session, this);
//        //System.out.println("hp: " + this.getHp());
//        if ((System.currentTimeMillis() - lastTimeHoiPhuc) > 30000 && this.die == 0 && (this.hp < this.getHpFull() || this.mp < this.getMpFull())) {
//            try {
////                hoiphuc((int) this.getHpFull() / 100 * 10, (int) this.getMpFull() / 100 * 10);
//                this.lastTimeHoiPhuc = System.currentTimeMillis();
//            } catch (Exception e) {
//
//            }
//        }
//        if (levelMonkey != 0 && (System.currentTimeMillis() - lastTimeUpMonkey >= getTimeMonkey(levelMonkey) || getHp() <= 0)) {
//            monkeyDown();
//        }
//        if (isShielding && ((System.currentTimeMillis() - lastTimeShieldUp) > getTimeShield(shield.point) || getHp() <= 0)) {
//            shieldDown();
//        }
//        if (useTroi && (System.currentTimeMillis() - lastTimeTroi) >= getTimeTroi(troi.point) || getHp() <= 0) {
//            removeTroi();
//        }
//        if (anTroi && (System.currentTimeMillis() - lastTimeAnTroi) >= timeAnTroi) {
//            removeTroi();
//        }
//        if (this.isBlinding && ((System.currentTimeMillis() - lastTimeStartBlind) > timeBlind || getHp() <= 0)) {
//            endBlind();
//        }
//        if (isThoiMien && (System.currentTimeMillis() - lastTimeThoiMien) > timeThoiMien || getHp() <= 0) {
//            removeThoiMien();
//        }
//        if (tiLeHPHuytSao != 0 && (System.currentTimeMillis() - lastTimeHuytSao > 30000)) {
//            removeHuytSao();
//        }
//
//    }
//    //--------------------------------------------------------------------------Biến khỉ
//    private static final int[] HEADMONKEY = {192, 195, 196, 199, 197, 200, 198};
//    public byte levelMonkey;
//    public long lastTimeUpMonkey;
//
//    public byte getMonkey() {
//        if (this.gender != 2) {
//            return 0;
//        }
//        return levelMonkey;
//    }
//
//    private void monkeyDown() {
//        levelMonkey = 0;
//        Message msg;
//        try {
//            Skill monkeySkill = null;
//            for (Skill skill : skills) {
//                if (skill.template.id == 13) {
//                    monkeySkill = skill;
//                    break;
//                }
//            }
//            msg = new Message(-45);
//            msg.writer().writeByte(5);
//            msg.writer().writeInt(id);
//            msg.writer().writeShort(monkeySkill.skillId);
//            Service.getInstance().sendMessAllPlayer(this, msg);
//            msg.cleanup();
//
//            msg = new Message(-45);
//            msg.writer().writeByte(6);
//            msg.writer().writeInt(id);
//            msg.writer().writeShort(monkeySkill.skillId);
//            Service.getInstance().sendMessAllPlayer(this, msg);
//            msg.cleanup();
//
//            msg = new Message(-90);
//            msg.writer().writeByte(-1);
//            msg.writer().writeInt(id);
//            Service.getInstance().sendMessAllPlayer(this, msg);
//            msg.cleanup();
//            Service.getInstance().Send_Caitrang(this);
//            msg = Service.getInstance().messageSubCommand1((byte) 8);
//            msg.writer().writeByte(0);
//            session.sendMessage(msg);
//            msg.cleanup();
//            Service.getInstance().point(session, this);
//
//            msg = Service.getInstance().messageSubCommand1((byte) 5);
//            msg.writer().writeInt(getHpFull());
//            session.sendMessage(msg);
//            msg.cleanup();
//
//        } catch (Exception e) {
//        }
//    }
//
//    public void monkeyUp() {
//        Skill monkeySkill = getSkillbyId(13);
//        if (monkeySkill != null) {
//            Message msg;
//            try {
//                msg = new Message(-45);
//                msg.writer().writeByte(6);
//                msg.writer().writeInt(id);
//                msg.writer().writeShort(monkeySkill.skillId);
//                session.sendMessage(msg);
//                msg.cleanup();
//
//                Thread.sleep(2000);
//                lastTimeUpMonkey = System.currentTimeMillis();
//                this.levelMonkey = (byte) monkeySkill.point;
//                Service.getInstance().Send_Caitrang(this);
//
//                this.hp = getHpFull();
//                msg = Service.getInstance().messageSubCommand1((byte) 8);
//                msg.writer().writeInt(id);
//                msg.writer().writeByte(0);
//                session.sendMessage(msg);
//                msg.cleanup();
//
//                msg = Service.getInstance().messageSubCommand1((byte) 5);
//                msg.writer().writeInt(getHpFull());
//                session.sendMessage(msg);
//                msg.cleanup();
//
//                Service.getInstance().point(session, this);
//                Service.getInstance().Send_Info_NV(session, this);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//    //--------------------------------------------------------------------------Tái tạo năng lượng
//    public boolean isCharging;
//
//    public void startCharge() {
//        Skill ttnl = getSkillbyId(8);
//        Message msg = new Message(-45);
//        try {
//            msg.writer().writeByte(1);
//            msg.writer().writeInt(id);
//            msg.writer().writeShort(ttnl.skillId);
//            Service.getInstance().sendMessAllPlayer(this, msg);
//            msg.cleanup();
//        } catch (IOException ex) {
//            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public void charge() {
//        if (isCharging) {
//            return;
//        }
//        isCharging = true;
//        new Thread(() -> {
//            Skill ttnl = getSkillbyId(8);
//            if (ttnl != null) {
//                startCharge();
//                for (int i = 0; i < 100; i++) {
//                    if (isCharging && (getHp() < getHpFull() || getMp() < getMpFull())) {
//                        hoiphuc(getHpFull() / 10, getMpFull() / 10);
//                        Service.getInstance().point(session, this);
//                        System.out.println("HP: " + this.getHp());
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException ex) {
//                        }
//                    } else {
//
//                        break;
//                    }
//                }
//                stopCharge();
//            }
//        }).start();
//    }
//
//    public void stopCharge() {
//        if (!isCharging) {
//            return;
//        }
//        try {
//            Message msg = new Message(-45);
//            msg.writer().writeByte(3);
//            msg.writer().writeInt(id);
//            msg.writer().writeShort(-1);
//            session.sendMessage(msg);
//            Service.getInstance().sendMessAllPlayer(this, msg);
//            msg.cleanup();
//            isCharging = false;
//        } catch (IOException e) {
//        }
//        isCharging = false;
//    }
//
//    //--------------------------------------------------------------------------Thái dương hạ san
//    public void blindUp() {
//        Skill tdhs = getSkillbyId(6);
//        if (tdhs != null) {
//            List<Mob> mobs = new ArrayList<>();
//            List<Player> players = new ArrayList<>();
//
//            if (this.cFlag != 0) {
//                for (Player pl : map.getPlayers()) {
//                    if (pl != this && pl.cFlag != 0 && (pl.cFlag != this.cFlag || this.cFlag == 8 || pl.cFlag == 8)) {
//                        if (Util.getDistance(this.x, this.y, pl.x, pl.y) <= getRangeBlind(tdhs.point)) {
//                            players.add(pl);
//                        }
//                    }
//                }
//            }
//
//            for (Mob mob : map.mobs) {
//                if (Util.getDistance(this.x, this.y, mob.pointX, mob.pointY) <= getRangeBlind(tdhs.point)) {
//                    mobs.add(mob);
//                }
//            }
//
//            Message msg;
//            try {
//                msg = new Message(-45);
//                msg.writer().writeByte(0);
//                msg.writer().writeInt(this.id);
//                msg.writer().writeShort(tdhs.skillId);
//
//                int blindTime = getTimeBlind(tdhs.point);
//                msg.writer().writeByte(mobs.size());
//                for (Mob mob : mobs) {
//                    msg.writer().writeByte(mob.id);
//                    msg.writer().writeByte(blindTime / 1000);
//                }
//                msg.writer().writeByte(players.size());
//                for (Player pl : players) {
//                    msg.writer().writeInt(pl.id);
//                    msg.writer().writeByte(blindTime / 1000);
//                }
//                Service.getInstance().sendMessAllPlayer(this, msg);
//                msg.cleanup();
//
//                for (Player pl : players) {
//                    pl.startBlind(System.currentTimeMillis(), blindTime);
//                }
//
//                for (Mob mob : mobs) {
//                    mob.startBlind(System.currentTimeMillis(), blindTime);
//                }
//            } catch (Exception e) {
//            }
//        }
//    }
//
//    public boolean isBlinding = false;
//    public long lastTimeStartBlind;
//    public int timeBlind;
//
//    private int getTimeBlind(int levelTdhs) {
//        return levelTdhs * 10000;
//    }
//
//    public void startBlind(long lastTimeStartBlind, int timeBlind) {
//        this.lastTimeStartBlind = lastTimeStartBlind;
//        this.timeBlind = timeBlind;
//        isBlinding = true;
////        Message msg;
////        try {
////            msg = new Message(-124);
////            msg.writer().writeByte(1); //b5
////            msg.writer().writeByte(0); //b6
////            msg.writer().writeByte(40); //num3
////            msg.writer().writeInt(id); //num4
////            Service.getInstance().sendMessAllPlayer(this, msg);
////            
////        } catch (Exception e) {
////        }
//    }
//
//    private void endBlind() {
//        isBlinding = false;
//    }
//
//    
//    //--------------------------------------------------------------------------Khiên năng lượng
//    public boolean isShielding = false;
//    private Skill shield;
//    private long lastTimeShieldUp;
//
//    public void shieldUp() {
//        Skill shield = getSkillbyId(19);
//        if (shield != null) {
//            isShielding = true;
//            lastTimeShieldUp = System.currentTimeMillis();
//            Message msg;
//            try {
//                msg = new Message(-124);
//                msg.writer().writeByte(1); //b5
//                msg.writer().writeByte(0); //b6
//                msg.writer().writeByte(33); //num3
//                msg.writer().writeInt(id); //num4
//                Service.getInstance().sendMessAllPlayer(this, msg);
//                msg.cleanup();
//
//                Service.getInstance().sendItemTime(this, 3784, getTimeShield(shield.point) / 1000);
//            } catch (Exception e) {
//            }
//        }
//    }
//
//    private void shieldDown() {
//        isShielding = false;
//        Message msg;
//        try {
//            msg = new Message(-124);
//            msg.writer().writeByte(0); //b5
//            msg.writer().writeByte(0); //b6
//            msg.writer().writeByte(33); //num3
//            msg.writer().writeInt(id); //num4
//            Service.getInstance().sendMessAllPlayer(this, msg);
//            msg.cleanup();
//        } catch (Exception e) {
//        }
//    }
//
//    private int getTimeShield(int levelShield) {
//        return 50 * levelShield * 1000;
//    }
//
//    private int getRangeBlind(int levelTDHS) {
//        return 120 + levelTDHS * 30;
//    }
//
//    private int getRangeBom(int levelBom) {
//        return 200 + levelBom * 30;
//    }
//
//    //--------------------------------------------------------------------------Trói
//    public long lastTimeAnTroi;
//    public int timeAnTroi;
//    public boolean anTroi;
//
//    public boolean useTroi;
//    public long lastTimeTroi;
//    public Player plAnTroi;
//    public Mob mobAnTroi;
//    public Skill troi;
//
//    public void removeTroi() {
//        if (this.mobAnTroi != null) {
//            mobAnTroi.removeTroi();
//        }
//        if (this.plAnTroi != null) {
//            plAnTroi.removeTroi();
//        }
//        Message msg;
//        try {
//            msg = new Message(-124);
//            msg.writer().writeByte(2); //b4
//            msg.writer().writeByte(0);//b5
//            msg.writer().writeInt(id);//b5
//            msg.writer().writeInt(0);
//            msg.writer().writeInt(0);
//            Service.getInstance().sendMessAllPlayer(this, msg);
//            msg.cleanup();
//
//        } catch (Exception e) {
//
//        }
//        this.anTroi = false;
//        this.useTroi = false;
//        this.mobAnTroi = null;
//        this.plAnTroi = null;
//    }
//
//    public void setTroi(long lastTimeAnTroi, int timeAnTroi) {
//        this.lastTimeAnTroi = lastTimeAnTroi;
//        this.timeAnTroi = timeAnTroi;
//        this.anTroi = true;
//    }
//
//    public void troi(int idHold, boolean isChar) {
//        Message msg;
//        try {
//            troi = getSkillbyId(23);
//            if (isChar) {
//                plAnTroi = map.getPlayerInMap(idHold);
//                msg = new Message(-124);
//                msg.writer().writeByte(1); //b5
//                msg.writer().writeByte(0);//b6
//                msg.writer().writeByte(32);//num3
//                msg.writer().writeInt(idHold);//num4
//                msg.writer().writeInt(this.id);//num9
//                Service.getInstance().sendMessAllPlayer(this, msg);
//                msg.cleanup();
//                plAnTroi.setTroi(System.currentTimeMillis(), getTimeTroi(troi.point));
//            } else {
//                mobAnTroi = map.getMobs().get(idHold);
//                msg = new Message(-124);
//                msg.writer().writeByte(1); //b4
//                msg.writer().writeByte(1);//b5
//                msg.writer().writeByte(32);//num8
//                msg.writer().writeByte(idHold);//b6
//                msg.writer().writeInt(this.id);//num9
//                Service.getInstance().sendMessAllPlayer(this, msg);
//                msg.cleanup();
//                mobAnTroi.setTroi(System.currentTimeMillis(), getTimeTroi(troi.point));
//            }
//            this.useTroi = true;
//            this.lastTimeTroi = System.currentTimeMillis();
//        } catch (Exception e) {
//        }
//    }
//
//    public int getTimeTroi(int levelTroi) {
//        return levelTroi * 5000;
//    }
//
//    public void bienSocola(int idHold, boolean isChar) {
//        Message msg;
//        try {
//            Skill socola = getSkillbyId(18);
//            long timeskill = System.currentTimeMillis() + socola.coolDown;
//            if (isChar) {
//
//            } else {
//                msg = new Message(-112);
//                msg.writer().writeByte(1);
//                msg.writer().writeByte(idHold); //b4
//                msg.writer().writeShort(4133);//b5
//                Service.getInstance().sendMessAllPlayer(this, msg);
//                msg.cleanup();
//                this.map.mobs.get(idHold).setSocola(timeskill);
//            }
//        } catch (Exception e) {
//        }
//    }
//
//    //--------------------------------------------------------------------------Thôi miên
//    public boolean isThoiMien;
//    public long lastTimeThoiMien;
//    public int timeThoiMien;
//    public Skill thoiMien;
//
//    public void thoiMien(int id, boolean isChar) {
//        Message msg;
//        try {
//            for (Skill skill : skills) {
//                if (skill.template.id == 22) {
//                    thoiMien = skill;
//                    break;
//                }
//            }
//            if (isChar) {
//                msg = new Message(-124);
//                msg.writer().writeByte(1); //b5
//                msg.writer().writeByte(0); //b6
//                msg.writer().writeByte(41); //num3
//                msg.writer().writeInt(id); //num4
//                Service.getInstance().sendMessAllPlayer(this, msg);
//                msg.cleanup();
//                Player plAnThoiMien = map.getPlayerInMap(id);
//                plAnThoiMien.setThoiMien(System.currentTimeMillis(), getTimeThoiMien(thoiMien.point));
//                Service.getInstance().sendItemTime(plAnThoiMien, 3782, getTimeTroi(thoiMien.point) / 1000);
//
//            } else {
//                msg = new Message(-124);
//                msg.writer().writeByte(1); //b5
//                msg.writer().writeByte(1); //b6
//                msg.writer().writeByte(41); //num6
//                msg.writer().writeByte(id); //b7
//                Service.getInstance().sendMessAllPlayer(this, msg);
//                msg.cleanup();
//                map.getMobs().get(id).setThoiMien(System.currentTimeMillis(), getTimeThoiMien(thoiMien.point));
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void setThoiMien(long lastTimeThoiMien, int timeThoiMien) {
//        this.isThoiMien = true;
//        this.lastTimeThoiMien = lastTimeThoiMien;
//        this.timeThoiMien = timeThoiMien;
//    }
//
//    public void removeThoiMien() {
//        this.isThoiMien = false;
//        Message msg;
//        try {
//            msg = new Message(-124);
//            msg.writer().writeByte(0); //b5
//            msg.writer().writeByte(0); //b6
//            msg.writer().writeByte(41); //num3
//            msg.writer().writeInt(id); //num4
//            Service.getInstance().sendMessAllPlayer(this, msg);
//            msg.cleanup();
//        } catch (Exception e) {
//        }
//    }
//
//    public int getTimeThoiMien(int levelThoiMien) {
//        return levelThoiMien * 3000;
//    }
//
//    //--------------------------------------------------------------------------Trị thương
//    public void triThuong(Player pl, int point) {
//        Message msg;
//        MapService.gI().chat(this.session, "Cảm ơn " + pl.name + " đã cứu mình");
//        if (this.die == 1) {
//            Service.getInstance().hsChar(this.session, this);
//        } else {
//            pl.hoiphuc(pl.getHpFull() / 100 * (point * 5 + 45), 0);
//
//            this.hoiphuc(this.getHpFull() / 100 * (point * 5 + 45), this.getMpFull() / 100 * (point * 5 + 45));
//
//            pl.set_mana_skill(pl.selectSkill.coolDown);
//            try {
//                msg = Service.getInstance().messageSubCommand1((byte) 14);//Cập nhật máu
//                msg.writer().writeInt(this.id);
//                msg.writer().writeInt(this.getHp());
//                msg.writer().writeByte(1);//Hiệu ứng Ăn Đậu
//                msg.writer().writeInt(this.getHpFull());
//                Service.getInstance().sendMessAllPlayer(this, msg);
//                msg.cleanup();
//            } catch (Exception e) {
//            }
//        }
//    }
//
//    //--------------------------------------------------------------------------Đẻ trứng
//    public boolean useDeTrung;
//    public long timeDeTrung;
//
//    public void deTrung() {
//
//        Message msg;
//        try {
//            Skill skill = getSkillbyId(12);
//            if (skill != null) {
//                msg = new Message(-95);
//                msg.writer().writeByte(0);//b47
//                msg.writer().writeInt(this.id);//num117
//                msg.writer().writeShort(50);//templeid
//                msg.writer().writeInt((this.hp / 100) * 90);// num118
//                Service.getInstance().sendMessAllPlayer(this, msg);
//                this.useDeTrung = true;
//            }
//        } catch (Exception e) {
//
//        }
//    }
//
//    public void chimAttack(int mobId) {
//        Message msg;
//
//        Util.log("Chim danh");
//        try {
//            Player pl = this.map.getPlayerInMap(mobId);
//            if (pl != null) {
//
//            } else {
//                if (this.map.mobs.get(mobId).gethp() > 0) {
//                    msg = new Message(-95);
//                    msg.writer().writeByte(3);//b47
//                    msg.writer().writeInt(this.id);//num117
//                    msg.writer().writeInt(mobId);//
//                    msg.writer().writeInt(this.map.getMobs().get(mobId).gethp() - (damGoc / 2));//
//                    msg.writer().writeInt(damGoc / 2);//
//                    Service.getInstance().sendMessAllPlayer(this, msg);
//                }
//            }
//        } catch (Exception e) {
//
//        }
//    }
//
//    //--------------------------------------------------------------------------Tự sát
//    public void bom() {
//        new Thread(() -> {
//
//            Message msg;
//            try {
//                Skill bom = getSkillbyId(14);
//                if (bom != null) {
//
//                    List<Player> players = new ArrayList<>();
//                    List<Mob> mobs = new ArrayList<>();
//                    for (Mob mob : map.mobs) {
//                        if (Util.getDistance(this.x, this.y, mob.pointX, mob.pointY) <= getRangeBom(bom.point) && mob.tempId != 0) {
//                            mobs.add(mob);
//                        }
//                    }
//
//                    if (this.cFlag != 0) {
//                        for (Player pl : map.getPlayers()) {
//                            if (pl != this && pl.cFlag != 0 && (pl.cFlag != this.cFlag || this.cFlag == 8 || pl.cFlag == 8)) {
//                                if (Util.getDistance(this.x, this.y, pl.x, pl.y) <= getRangeBom(bom.point)) {
//                                    players.add(pl);
//                                }
//                            }
//                        }
//                    }
//
//                    msg = new Message(-45);
//                    msg.writer().writeByte(7);
//                    msg.writer().writeInt(this.id);
//                    msg.writer().writeShort(bom.skillId);
//                    msg.writer().writeShort(2000);
//                    Service.getInstance().sendMessAllPlayer(this, msg);
//                    msg.cleanup();
//                    Thread.sleep(2000);
//                    for (Player pl : players) {
//                        pl.injured(this.getHpFull());
//                    }
//                    for (Mob mob : mobs) {
//                        mob.injured(this, this.getHpFull());
//                    }
//                    this.injured(this.getHpFull());
//                }
//            } catch (Exception e) {
//            }
//        }).start();
//    }
//
//    //--------------------------------------------------------------------------Huýt sáo
//    public int tiLeHPHuytSao;
//    public long lastTimeHuytSao;
//
//    public void setHuytSao(int tiLeHP) {
//        this.tiLeHPHuytSao = tiLeHP;
//        this.setHp(this.getHp() + (this.getHp() * tiLeHP / 100));
//        lastTimeHuytSao = System.currentTimeMillis();
//    }
//
//    public int getTiLeHPHuytSao(int levelHuytSao) {
//        return (13 - (10 - levelHuytSao)) * 10;
//    }
//
//    public void huytSao() {
//        Skill skill = getSkillbyId(21);
//        if (skill != null) {
//            for (Player pl : map.getPlayers()) {
//                if (pl.gender != 1) {
//                    pl.setHuytSao(getTiLeHPHuytSao(skill.point));
//
//                    Message msg;
//                    try {
//                        msg = new Message(-124);
//                        msg.writer().writeByte(1); //b5
//                        msg.writer().writeByte(0); //b6
//                        msg.writer().writeByte(39); //num3
//                        msg.writer().writeInt(pl.id); //num4
//                        Service.getInstance().sendMessAllPlayer(this, msg);
//                        msg.cleanup();
//
//                        msg = Service.getInstance().messageSubCommand1((byte) 8);
//                        msg.writer().writeInt(pl.id);
//                        msg.writer().writeByte(0);
//                        Service.getInstance().sendMessAllPlayer(this, msg);
//                        msg.cleanup();
//
//                        Service.getInstance().point(pl.session, pl);
//                        Service.getInstance().Send_Info_NV(pl.session, pl);
//                        Service.getInstance().sendItemTime(pl, 3781, 30);
//
//                        msg = Service.getInstance().messageSubCommand1((byte) 5);
//                        msg.writer().writeInt(pl.getHp());
//                        pl.session.sendMessage(msg);
//                        msg.cleanup();
//
//                    } catch (Exception e) {
//                    }
//                }
//            }
//
//        }
//    }
//
//    private void removeHuytSao() {
//        this.tiLeHPHuytSao = 0;
//        Message msg;
//        try {
//            msg = new Message(-124);
//            msg.writer().writeByte(0); //b5
//            msg.writer().writeByte(0); //b6
//            msg.writer().writeByte(39); //num3
//            msg.writer().writeInt(this.id); //num4
//            Service.getInstance().sendMessAllPlayer(this, msg);
//            msg.cleanup();
//
//            Service.getInstance().point(session, this);
//            Service.getInstance().Send_Info_NV(session, this);
//        } catch (Exception e) {
//        }
//    }
//
//    //--------------------------------------------------------------------------Quả cầu kênh khi
//    public void cauKenhKhi() {
//        Message msg;
//        try {
//            msg = new Message(-45);
//            msg.writer().writeByte(0);
//            msg.writer().writeInt(this.id);
//            msg.writer().writeShort(getSkillbyId(4).skillId);
//            msg.writer().writeShort(2000);
//            Service.getInstance().sendMessAllPlayer(this, msg);
//            msg.cleanup();
//        } catch (Exception e) {
//
//        }
//    }
//    
//    public boolean isVeNha;
//    public long lastTimeChangeFlag;
//    
//    
//
//
//    public long lastTimeHoiPhuc;
//    
//    public void loadDauThan() {
//        Message msg;
//        Npc dauthan = null;
//        for (Npc dt : this.map.getNpcs()) {
//            if (dt.tempId == 4) {
//                dauthan = dt;
//                break;
//            }
//        }
//        int[] iddauthan;
//        if (this.gender == 0) {
//            iddauthan = new int[]{84, 85, 86, 87, 88, 89, 90};
//        } else if (this.gender == 1) {
//            iddauthan = new int[]{371, 372, 373, 374, 375, 376, 377};
//        } else {
//            iddauthan = new int[]{378, 379, 380, 381, 382, 383, 384};
//        }
//
//        try {
//            msg = new Message(-34);
//            msg.writer().writeByte(0);
//            msg.writer().writeShort(iddauthan[6]);
//            msg.writer().writeUTF("Đậu thần cấp 6");
//            msg.writer().writeShort(dauthan.cx);
//            msg.writer().writeShort(dauthan.cy);
//            msg.writer().writeByte(111);//cấp đậu
//            msg.writer().writeShort(15);//số lượng hiện có
//            msg.writer().writeShort(15);//max đậu
//            msg.writer().writeUTF("Đang kết hạt\nCây lớn sinh nhiều hạt hơn");
//            msg.writer().writeInt(Util.nextInt(30, 60));//time ra hạt đậu hoặc update
//            msg.writer().writeByte(0);//vị trí đậu
//            for (int i = 0; i < 0; i++) {
//
//                msg.writer().writeByte(1);//vị trí đậu
//                String[] vitri = new String[]{"32 86", "5 77", "5 77", "8 89", "29 68", "4 63", "18 61", "33 53", "8 48", "26 39", "11 36", "33 23", "18 25", "4 20", "26 12", "12 7", "19 0"};
//                for (int ix = 0; i < 1; i++) {
//                    msg.writer().writeByte((Integer.parseInt((vitri[i].split(" "))[0])));// x
//                    msg.writer().writeByte((Integer.parseInt((vitri[i].split(" "))[1])));// y
//                }
//                msg.writer().writeBoolean(false);// update đậu
//                this.session.sendMessage(msg);
//                msg.cleanup();
//            }
//        } catch (Exception e) {
//
//        }
//    }
//    
//
//
//    private int getTimeMonkey(int level) {
//        return level * 100000;
//    }
//
//    private int getPercentHpMonkey(int level) {
//        return (level + 3) * 10;
//    }
//    
//    public byte[] skillShortCut = new byte[5];
//
//    public void sendSkillShortCut() {
//        Message msg;
//        try {
//            msg = Service.getInstance().messageSubCommand1((byte) 61);
//            msg.writer().writeUTF("KSkill");
//            msg.writer().writeInt(skillShortCut.length);
//            msg.writer().write(skillShortCut);
//            session.sendMessage(msg);
//            msg.cleanup();
//            msg = Service.getInstance().messageSubCommand1((byte) 61);
//            msg.writer().writeUTF("OSkill");
//            msg.writer().writeInt(skillShortCut.length);
//            msg.writer().write(skillShortCut);
//            session.sendMessage(msg);
//            msg.cleanup();
//            System.out.println("send skill");
//        } catch (Exception e) {
//        }
//    }
//    
//    public Timer timer;
//    public void active() {
//        this.timer = new Timer();
//        this.timer.schedule(new PlayerTask(), 10000L, 30000L);
//    }
//    class PlayerTask extends TimerTask {
//
//        @Override
//        public void run() {
//            Player.this.update();
//        }
//
//    }
//    
//    public void updateall() {
//        this.mp = this.getMpFull();
//        this.hp = this.getHpFull();
//        Service.getInstance().point(this.session, this);
//    }
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    //--------------------------------------------------------------------------
//    //--------------------------------------------------------------------------
//
//
//
//
//    public ArrayList<Player> nearPlayers;
//
//
//
//
////    public static short[][] infoId = {{281, 361, 351}, {512, 513, 536}, {514, 515, 537}};
//    public Player() {
//        this.itemsBody = new ArrayList<>();
//        this.itemsBag = new ArrayList<>();
//        this.itemsBox = new ArrayList<>();
//        this.nearPlayers = new ArrayList<>();
//        this.skills = new ArrayList<>();
//        this.timer = new Timer();
//        this.itemsCombine = new ArrayList<>();
//    }
//
//
//    
//
//
////    public void updatehp() {
////        if (this.hp < this.getHpFull()) {
////            this.hp = this.getHpFull();
////            Service.getInstance().point(this.session, this);
////        }
////    }
//
//
//    
//
//    
//
//    //--------------------------------------------------------------------------
//    
//}
