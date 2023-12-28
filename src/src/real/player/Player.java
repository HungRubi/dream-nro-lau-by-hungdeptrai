package real.player;

import real.map.Mob;
import real.pet.Pet;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import real.clan.Clan;
import real.entities.ListPlayer;
import real.func.Combine;
import real.func.PVP;
import real.item.CaiTrangData;
import real.item.Item;
import real.item.ItemTime;
import real.magictree.MagicTree;
import real.map.Map;
import real.map.MapService;
import real.map.WayPoint;
import real.pet.MobMe;
import real.pet.PetDAO;
import real.skill.Skill;
import server.Service;
import server.Util;
import server.io.Message;
import server.io.Session;

public class Player {

    public static final byte NON_PK = 0;
    public static final byte PK_PVP = 3;
    public static final byte PK_ALL = 5;

    public static final byte LUONG_LONG_NHAT_THE = 4;
    public static final byte HOP_THE_PORATA = 6;
    
    private Session session;

    public Map map;
    public long currenTimeUpdate = 0l;

    public boolean isTdlt;
    public int timeTdlt;

    public int mapBeforeCapsuleId = -1;
    public int zoneBeforeCapsuleId = -1;
    public List<Map> mapCapsule;

    public Player playerTrade;
    public MagicTree magicTree;
    public Pet pet;
    public MobMe mobMe;
    public boolean isPet;
    public byte typeFusion;

    public boolean isBoss;
    public byte typeBoss;
    
    public static final int timeFusion = 600000;
    public long lastTimeFusion;

    public ListPlayer listPlayer;
    public Point point;
    public Inventory inventory;
    public PlayerSkill playerSkill;
    public Combine combine;
    public Clan clan;

    public long id;
    public String name;
    public byte gender;
    public long[] timeLimit = new long[]{0, 0, 0, 0, 0};

    protected static final int[] HEADMONKEY = {192, 195, 196, 199, 197, 200, 198};
    public short head;

    public byte typePk;

    public byte cFlag;
    public long lastTimeChangeFlag;

    public short taskId;
    public byte taskIndex;

    public int x;
    public int y;

    private byte useSpaceShip;

    private short indexMenu;

    public boolean isGoHome;
    
    public Player endHit;
    
    public boolean isFly;
    public boolean isOpenShop;
    public boolean justRevived;
    public long lastTimeRevived;
    public boolean immortal;
    public long clanTime;

    public Player() {
        point = new Point(this);
        inventory = new Inventory(this);
        playerSkill = new PlayerSkill(this);
        combine = new Combine(this);
    }

    public void loadListPlayer() {
        listPlayer = new ListPlayer(this);
    }

    
    //--------------------------------------------------------------------------
    public void setUseSpaceShip(byte useSpaceShip) {
        // 0 - không dùng
        // 1 - tàu vũ trụ theo hành tinh
        // 2 - dịch chuyển tức thời
        // 3 - tàu tenis
        this.useSpaceShip = useSpaceShip;
    }

    public byte getUseSpaceShip() {
        return this.useSpaceShip;
    }

    public boolean isDie() {
        return this.point.getHP() <= 0;
    }

    public short getIndexMenu() {
        return indexMenu;
    }

    public void setIndexMenu(int indexMenu) {
        this.indexMenu = (short) indexMenu;
    }

    //--------------------------------------------------------------------------
    public void move(int _toX, int _toY) {
        if (_toX != this.x) {
            this.x = _toX;
        }
        if (_toY != this.y) {
            this.y = _toY;
        }
        MapService.gI().playerMove(this);
    }

    public void gotoMap(Map map) {
        if (map != null) {
            if (this.mobMe != null) {
                mobMe.goToMap(map);
            }
            exitMap();
            this.map = map;
            this.map.getPlayers().add(this);
        }
    }

    public void exitMap() {
        if (this.map != null) {
            this.map.getPlayers().remove(this);
            MapService.gI().exitMap(this, this.map);
        }
    }

    public WayPoint isInWaypoint() {
        for (WayPoint wp : map.wayPoints) {
            if (x >= wp.minX && x <= wp.maxX && y >= wp.minY && y <= wp.maxY) {
                return wp;
            }
        }
        return null;
    }

    //--------------------------------------------------------------------------
    public void setSession(Session session) {
        this.session = session;
    }

    public void sendMessage(Message msg) {
        if (this.session != null) {
            session.sendMessage(msg);
        }
    }

    public Session getSession() {
        return this.session;
    }

    public Timer timer;
    protected boolean actived = false;

    public void active(int delay, int period) {
        if (!actived) {
            actived = true;
            this.timer = new Timer();
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Player.this.update();

                }
            }, 1000L, 1000L);

        }
    }

    public void sendInfo() {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 4);
            msg.writer().writeInt(this.inventory.gold);//xu
            msg.writer().writeInt(this.inventory.gem);//luong
            msg.writer().writeInt(this.point.hp);//chp
            msg.writer().writeInt(this.point.mp);//cmp
            msg.writer().writeInt(this.inventory.ruby);//ruby
            this.sendMessage(msg);
        } catch (Exception e) {
        }
    }

    public void update() {
        if (!(this instanceof Pet)) {
//            System.out.println("this hp: " + this.point.getHP() + " mp: " + this.point.getMP());
        }
        if (System.currentTimeMillis() - currenTimeUpdate >= 1000) {
            if (isTdlt) {
                timeTdlt--;
                if (timeTdlt <= 0) {
                    try {
                        Message msg = new Message(-116);
                        msg.writer().writeShort(0);
                        session.sendMessage(msg);
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    isTdlt = false;
                    timeTdlt = 0;
                }
            }
            currenTimeUpdate = System.currentTimeMillis();
        }
        if ((System.currentTimeMillis() - lastTimeHoiPhuc) > 30000
                && !isDie() && (point.getHP() < point.getHPFull() || point.getMP() < point.getMPFull())) {
//            hoiphuc((int) point.getHPFull() / 100 * 10, (int) point.getMPFull() / 100 * 10);
            lastTimeHoiPhuc = System.currentTimeMillis();
        }
        if (typeFusion == LUONG_LONG_NHAT_THE && Util.canDoWithTime(lastTimeFusion, timeFusion)) {
            pet.unFusion();
        }
        playerSkill.update();
        
        for (int i = 0; i < this.inventory.getvItemTime().size(); i++) {
            ItemTime itemTime = this.inventory.getvItemTime().get(i);
            if (itemTime != null) {
                if (System.currentTimeMillis() >= itemTime.timeRemove) {
                    System.err.println(System.currentTimeMillis() + " - " + itemTime.timeRemove);
                    this.inventory.removeItemTime(itemTime.itemTempId);
                } else {
                    itemTime.timeStart += 1000;
                }
            }
        }
    }
    //--------------------------------------------------------------------------
    private static final short[][] idFusion = {{380, 381, 382}, {383, 384, 385}, {391, 392, 393}};

    public short getHead() {
        if (playerSkill.isMonkey) {
            return (short) HEADMONKEY[playerSkill.getLevelMonkey() - 1];
        } else if (playerSkill.isSocola) {
            return 412;
        } else if (typeFusion != 0) {
            if (typeFusion == LUONG_LONG_NHAT_THE) {
                return idFusion[this.gender == 1 ? 2 : 0][0];
            } else if (typeFusion == HOP_THE_PORATA) {
                return idFusion[this.gender == 1 ? 2 : 1][0];
            }
        } else if (inventory.itemsBody.get(5).id != -1) {
            CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(inventory.itemsBody.get(5).template.id);
            if (ct != null) {
                return (short) (ct.getID()[0] != -1 ? ct.getID()[0] : inventory.itemsBody.get(5).template.part);
            }
        }
        return head;
    }

    public short getBody() {
        if (playerSkill.isMonkey) {
            return 193;
        } else if (playerSkill.isSocola) {
            return 413;
        } else if (typeFusion != 0) {
            if (typeFusion == LUONG_LONG_NHAT_THE) {
                return idFusion[this.gender == 1 ? 2 : 0][1];
            } else if (typeFusion == HOP_THE_PORATA) {
                return idFusion[this.gender == 1 ? 2 : 1][1];
            }
        } else if (inventory.itemsBody.get(5).id != -1) {
            CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(inventory.itemsBody.get(5).template.id);
            if (ct != null && ct.getID()[1] != -1) {
                return (short) ct.getID()[1];
            }
        }
        if (inventory.itemsBody.get(0).id != -1) {
            return inventory.itemsBody.get(0).template.part;
        }
        return (short) (gender == 1 ? 59 : 57);
    }

    public short getLeg() {
        if (playerSkill.isMonkey) {
            return 194;
        } else if (playerSkill.isSocola) {
            return 414;
        } else if (typeFusion != 0) {
            if (typeFusion == LUONG_LONG_NHAT_THE) {
                return idFusion[this.gender == 1 ? 2 : 0][2];
            } else if (typeFusion == HOP_THE_PORATA) {
                return idFusion[this.gender == 1 ? 2 : 1][2];
            }
        } else if (inventory.itemsBody.get(5).id != -1) {
            CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(inventory.itemsBody.get(5).template.id);
            if (ct != null && ct.getID()[2] != -1) {
                return (short) ct.getID()[2];
            }
        }
        if (inventory.itemsBody.get(1).id != -1) {
            return inventory.itemsBody.get(1).template.part;
        }
        return (short) (gender == 1 ? 60 : 58);
    }

    public short getMount() {
        for (Item item : inventory.itemsBag) {
            if (item.id != -1) {
                if (item.template.type == 23 || item.template.type == 24) {
                    if (item.template.gender == 3 || item.template.gender == this.gender) {
                        return item.template.id;
                    } else {
                        return -1;
                    }
                }
            }
        }
        return -1;
    }

    //--------------------------------------------------------------------------
    public synchronized int injured(Player plAtt, int hp, boolean piercing, boolean... die) {
        if(this.typeBoss == 1){
            if(hp > 60000){
                hp = 60000;
            }
        }
        
        if (!piercing && playerSkill.isShielding) {
            if (hp > point.getHPFull()) {
                playerSkill.shieldDown();
                Service.getInstance().sendThongBao(this, "Khiên năng lượng đã bị vỡ!");
                Service.getInstance().removeItemTime(this, 3784);
            }
            if (hp > 0) {
                hp = 1;
            }
        }
        if (die.length != 0 && !die[0] && hp >= point.hp) {
            hp = point.hp - 1;
        }
        point.hp -= hp;
        if (point.hp <= 0) {
            point.hp = 0;
            point.mp = 0;
            Service.getInstance().charDie(this);

            if(this.typeBoss == 1){
                this.endHit = plAtt;
            }
            
            if (plAtt != null && !plAtt.isPet && !plAtt.isBoss) {
                this.listPlayer.addEnemy(plAtt);
            }
            PVP.gI().finishPVP(this, PVP.TYPE_DIE);
        }
        System.err.println("Quái pem: -" + hp);
        return hp;
    }

    public synchronized int injured(Player plAtt, int hp, boolean piercing, Mob mob, boolean... die) {
        if(this.typeBoss == 1){
            if(hp > 60000){
                hp = 60000;
            }
        }
        if (this.inventory.getItemTimeId(215) != null) {
            hp /= 2;
        }

        if (!piercing && playerSkill.isShielding) {
            if (hp > point.getHPFull()) {
                playerSkill.shieldDown();
                Service.getInstance().sendThongBao(this, "Khiên năng lượng đã bị vỡ!");
                Service.getInstance().removeItemTime(this, 3784);
            }
            if (hp > 0) {
                hp = 1;
            }
        }
        if (die.length != 0 && !die[0] && hp >= point.hp) {
            hp = point.hp - 1;
        }
        if (point.isSKH(131) && point.hp - hp <= 2) {
            hp = point.hp - 2;
        } else
        if (this.inventory.getItemTimeId(217) != null && point.hp - hp <= 1) {
            hp = point.hp - 1;
        }
        point.hp -= hp;
        if (point.hp <= 0) {
            point.hp = 0;
            point.mp = 0;
            Service.getInstance().charDie(this);

            if(this.typeBoss == 1){
                this.endHit = plAtt;
            }

            if (plAtt != null && !plAtt.isPet && !plAtt.isBoss) {
                this.listPlayer.addEnemy(plAtt);
            }
            PVP.gI().finishPVP(this, PVP.TYPE_DIE);
        }
        System.err.println("Quái pem: -" + hp);
        return hp;
    }

    public boolean setMPUseSkill() {
        if (playerSkill.skillSelect == null) {
            return false;
        }
        boolean canUseSkill = false;
        switch (playerSkill.skillSelect.template.manaUseType) {
            case 0:
                if (this.point.mp >= playerSkill.skillSelect.manaUse) {
                    this.point.mp -= playerSkill.skillSelect.manaUse;
                    canUseSkill = true;
                }
                break;
            case 1:
                int mpUse = (int) ((float) (this.point.getMPFull() / 100 * playerSkill.skillSelect.manaUse));
                if (this.point.mp >= mpUse) {
                    this.point.mp -= mpUse;
                    canUseSkill = true;
                }
                break;
            case 2:
                this.point.mp = 0;
                canUseSkill = true;
                break;
        }
        Service.getInstance().sendInfoChar30c4(this);
        return canUseSkill;
    }

    public boolean canUseSkill() {
        if (playerSkill.skillSelect == null) {
            return false;
        }
        if (playerSkill.skillSelect.template.id == Skill.KAIOKEN) {
            int hpUse = point.getHPFull() / 100 * 10;
            if (point.getHP() <= hpUse) {
                return false;
            }
        }
        boolean canUseSkill = false;
        switch (playerSkill.skillSelect.template.manaUseType) {
            case 0:
                if (this.point.mp >= playerSkill.skillSelect.manaUse) {
                    canUseSkill = true;
                }
                break;
            case 1:
                int mpUse = (int) ((float) (this.point.getMPFull() / 100 * playerSkill.skillSelect.manaUse));
                if (this.point.mp >= mpUse) {
                    canUseSkill = true;
                }
                break;
            case 2:
                canUseSkill = true;
                break;
        }
        return canUseSkill && Util.canDoWithTime(playerSkill.skillSelect.lastTimeUseThisSkill, playerSkill.skillSelect.coolDown);
    }

    public long lastTimeHoiPhuc;

    public void hoiphuc(int hp, int mp) { //Hồi phục máu + mana
        if (point.getHP() == 0 || isDie()) {
            return;
        }
        if (point.hp + hp >= point.getHPFull()) {
            point.hp = point.getHPFull();
        } else {
            point.hp += hp;
        }
        if (point.mp + mp >= point.getMPFull()) {
            point.mp = point.getMPFull();
        } else {
            point.mp += mp;
        }
        try {
            Service.getInstance().Send_Info_NV(this);
            if (!this.isPet) {
                sendInfoHPMP();
            }
        } catch (Exception e) {

        }
    }

    public void sendInfoHPMP() {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 5);
            msg.writer().writeInt(this.point.getHP());
            this.sendMessage(msg);
            msg.cleanup();
            msg = Service.getInstance().messageSubCommand((byte) 6);
            msg.writer().writeInt(this.point.getMP());
            this.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------
    public void sendItemTime() {
        if (this.typeFusion == LUONG_LONG_NHAT_THE) {
            Service.getInstance().sendItemTime(this, this.gender == 1 ? 3901 : 3790,
                    (int) ((Player.timeFusion - (System.currentTimeMillis() - this.lastTimeFusion)) / 1000));
        }
    }
    public void roiDuiganuong(){
        Util.log("Roi dui ga");
        Message msg;
        try{
            short x = 0;
            short y = 320;
           if(this.gender == 0){
                x = 633;
                y = 315;
            }else if(this.gender == 1){
                x = 56;
                y = 315;
            }else if(this.gender ==2){
                x = 633;
            }
             msg = new Message(68);
             msg.writer().writeShort(1);
             msg.writer().writeShort(74);
              msg.writer().writeShort(x);
               msg.writer().writeShort(y);
              msg.writer().writeInt(this.map.id);
             this.sendMessage(msg);
             msg.cleanup();
        }catch(Exception e){
            
        }
    } 
    
    public void createPET() {
        PetDAO.create(this, Util.nextInt(0, 2));
        PetDAO.loadForPlayer(this);
        sendMeHavePet();
        if (this.pet != null) {
            PlayerManger.gI().getPlayers().add(this.pet);
            this.pet.point.updateall();
            this.pet.active(100, 100);
        }
    }

    public void sendMeHavePet() {
        Message msg;
        try {
            msg = new Message(-107);
            msg.writer().writeByte(this.pet == null ? 0 : 1);
            this.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void setJustRevivaled() {
        this.justRevived = true;
        this.lastTimeRevived = System.currentTimeMillis();
        this.immortal = true;
    }

    public void attackPlayer(Player plAttack, boolean miss) {
        
        int dameHit = this.injured(plAttack, miss ? 0 : plAttack.point.getDameAttack(), false);
        
        Message msg;
        try {
//            if (!plAttack.isPet) {
//                msg = new Message(-60);
//                msg.writer().writeInt((int) plAttack.id);
//                msg.writer().writeByte(0);
//                msg.writer().writeByte(0);
//                msg.writer().writeInt((int) this.id);
//                msg.writer().writeByte(0);
//                plAttack.session.sendMessage(msg);
//                msg.cleanup();
//            }

//            boolean phanSatThuong = false;
//            if (phanSatThuong) {
//                msg = new Message(56);
//                msg.writer().writeInt((int) plAttack.id);
//                int hpNguocLai = -plAttack.injured(100);
//                msg.writer().writeInt(plAttack.point.getHP());
//                msg.writer().writeInt(hpNguocLai);
//                msg.writer().writeBoolean(false); //crit
//                msg.writer().writeByte(36); //hiệu ứng pst
//                Service.getInstance().sendMessAllPlayerInMap(this.map, msg);
//                msg.cleanup();
//            }
//            Service.getInstance().congTiemNang(this, (byte) 2, 1);
//            msg = new Message(-60);
//            msg.writer().writeInt((int) plAttack.id); //id pem
//            msg.writer().writeByte(plAttack.playerSkill.skillSelect.skillId); //skill pem
//            msg.writer().writeByte(1); //số người pem
//            msg.writer().writeInt((int) this.id); //id ăn pem
//            msg.writer().writeByte(1); //read continue // hụt
//            msg.writer().writeByte(1); //type skill
//            msg.writer().writeInt(dameHit); //dame ăn
//            msg.writer().writeBoolean(false); //is die
//            msg.writer().writeBoolean(plAttack.point.isCrit); //crit
//            Service.getInstance().sendMessAllPlayerInMap(this.map, msg);
//            msg.cleanup();
//
//            msg = new Message(-60);
//            msg.writer().writeInt((int) plAttack.id); //id pe
//            msg.writer().writeByte(plAttack.playerSkill.skillSelect.skillId); //skill pem
//            msg.writer().writeByte(1); //số người pem
//            msg.writer().writeInt((int) this.id); //id ăn pem
//            msg.writer().writeByte(1); //read continue // hụt
//            msg.writer().writeByte(0); //type skill
//            msg.writer().writeInt(dameHit); //dame ăn
//            msg.writer().writeBoolean(false); //is die
//            msg.writer().writeBoolean(plAttack.point.isCrit); //crit
//            plAttack.sendMessage(msg);
//            msg.cleanup();
            Service.getInstance().congTiemNang(this, (byte) 2, 1);

            msg = new Message(-60);
            msg.writer().writeInt((int) plAttack.id); //id pe
            msg.writer().writeByte(plAttack.playerSkill.skillSelect.skillId); //skill pem
            msg.writer().writeByte(1); //số người pem
            msg.writer().writeInt((int) this.id); //id ăn pem

            msg.writer().writeByte(1); //read continue
            msg.writer().writeByte(0); //type skill

            msg.writer().writeInt(dameHit); //dame ăn
            msg.writer().writeBoolean(false); //is die
            msg.writer().writeBoolean(plAttack.point.isCrit); //crit
//            for (Player pl : map.players) {
//                if (!pl.isPet && pl.id != plAttack.id) {
//                    pl.session.sendMessage(msg);
//                }
//            }
            Service.getInstance().sendMessAllPlayerInMap(this.map, msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
