package real.map;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import real.item.ItemData;
import real.item.ItemOption;
import real.player.Inventory;
import real.player.Player;
import server.Service;
import server.Util;
import server.io.Message;

public class Mob {

    public boolean actived;
    public int tempId;
    protected Timer timer;
    public Map map;
    public byte level;
    public int id;
    protected int hp;
    protected int mindame;
    protected int maxdame;
    protected int maxHp;

    protected int dame;  // dame of mobme

    public long timeDie;
    public int antidelay;
    public short pointX;
    public short pointY;
    public int sieuquai = 0;

    public boolean isAnTroi;
    public long lastTimeAnTroi;
    public int timeAnTroi;

    public void setTroi(long lastTimeAnTroi, int timeAnTroi) {
        this.lastTimeAnTroi = lastTimeAnTroi;
        this.timeAnTroi = timeAnTroi;
        this.isAnTroi = true;
    }

    public void removeAnTroi() {
        isAnTroi = false;
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b4
            msg.writer().writeByte(1);//b5
            msg.writer().writeByte(32);//num8
            msg.writer().writeByte(id);//b6
            Service.getInstance().sendMessAllPlayerInMap(map, msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public boolean isSocola;
    private long lastTimeSocola;
    private int timeSocola;

    public void removeSocola() {
        Message msg;
        this.isSocola = false;
        try {
            msg = new Message(-112);
            msg.writer().writeByte(0);
            msg.writer().writeByte(this.id);
            Service.getInstance().sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void setSocola(long lastTimeSocola, int timeSocola) {
        this.lastTimeSocola = lastTimeSocola;
        this.timeSocola = timeSocola;
        this.isSocola = true;
    }

    public byte status;

    private List<Player> playerAttack = new LinkedList<>();
    private long lastTimeAttackPlayer;

    public int getHpFull() {
        return maxHp;
    }

    public void setHpFull(int hp) {
        this.maxHp = hp;
    }

    public boolean isDie() {
        return this.gethp() <= 0;
    }

    public void attackMob(Player plAttack, boolean miss) {
        if (plAttack.inventory.getItemTimeId(217) != null && plAttack.point.hp == 1) {
            Service.getInstance().sendThongBao(plAttack,"Bạn cần nhai đậu vào mồm mới có thể đánh");
            return;
        }
        Message msg;
        try {
            msg = new Message(54);
            msg.writer().writeInt((int) plAttack.id);
            msg.writer().writeByte(plAttack.playerSkill.skillSelect.skillId);
            msg.writer().writeByte(this.id);
            Service.getInstance().sendMessAllPlayerInMap(plAttack.map, msg);
            msg.cleanup();
            this.addPlayerAttack(plAttack);
            this.injured(plAttack, miss ? 0 : plAttack.point.getDameAttack());
        } catch (Exception e) {
            Util.log("CMD: " + e.getMessage());
        }
    }

    public synchronized void injured(Player pl, int hp) {// Hàm để nhận dame từ ng đánh
        if (this.hp <= 0) {
            return;
        }
        if (pl.inventory.getItemTimeId(214) != null) {
            hp = hp * 15 / 10;
        }
        if (this.tempId == 0) {
            hp = 10;
        }
        if (hp >= this.maxHp) {
            hp = this.maxHp - 1;
        }
//        if (hp >= this.hp) {
//            hp = this.hp - 1;
//        }
        if (this.hp == 1) {
            hp = 1;
        }
        this.sethp(this.hp -= hp);
        Message msg;
        try {
            if (this.gethp() > 0) {
                msg = new Message(-9);
                msg.writer().writeByte(this.id);
                msg.writer().writeInt(this.gethp());
                msg.writer().writeInt(hp);
                msg.writer().writeBoolean(pl.point.isCrit); // chí mạng
                msg.writer().writeInt(-1);
            } else {
                msg = new Message(-12);
                msg.writer().writeByte(this.id);
                msg.writer().writeInt(hp);
                msg.writer().writeBoolean(pl.point.isCrit); // crit
                if (this.tempId != 0 && Util.nextInt(10) >= 0) {
                    this.roiItem(pl, msg, this.maxHp);
                }
                this.setDie();
            }
            Service.getInstance().sendMessAllPlayerInMap(pl.map, msg);
            msg.cleanup();
//            if (pl.point.isCrit != true) {
//                Service.getInstance().congTiemNang(pl, (byte) 2, (((int) ((pl.point.getDameAttack()) / 100) * 25) > 0) ? ((int) ((pl.point.getDameAttack()) / 100) * 25) : 1);
//            } else {
//                Service.getInstance().congTiemNang(pl, (byte) 2, (((int) ((pl.point.getDameAttack()) / 100) * 25) > 0) Ï? ((int) ((pl.point.getDameAttack()) / 100) * 25) * 2 : 2);
//            }
            int x2ToanServer = 1000000;
            
            boolean x2 = pl.inventory.getItemTimeId(213) != null;
            boolean x3 = pl.inventory.getItemTimeId(671) != null;
            boolean x4 = pl.inventory.getItemTimeId(672) != null;

            if (x2 && !x3 && !x4) {
                x2ToanServer += 1;
            } else
            if (!x2 && x3 && !x4) {
                x2ToanServer += 2;
            } else
            if (!x2 && !x3 && x4) {
                x2ToanServer += 3;
            } else
            if (x2 && x3 && !x4) {
                x2ToanServer += 3;
            } else
            if (x2 && !x3 && x4) {
                x2ToanServer += 4;
            } else
            if (!x2 && x3 && x4) {
                x2ToanServer += 5;
            } else
            if (x2 && x3 && x4) {
                x2ToanServer += 6;
            }
            
            int rateMob = 2;
            if (this.maxHp >= 1000) {
                rateMob = this.maxHp / 700;
            }
            if (pl.point.isCrit != true) {
                Service.getInstance().congTiemNang(pl, (byte) 2, (this.getHpFull() > 100 ? ((hp * (this.getHpFull() * 1 / (100 * rateMob))) * x2ToanServer) : 1));
            } else {
                Service.getInstance().congTiemNang(pl, (byte) 2, (this.getHpFull() > 100 ? (((hp * (this.getHpFull() * 1 / (100 * rateMob))) * 2) * x2ToanServer) : 1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void roiItem(Player pl, Message msg, int HP) {
        try {
            int randomQua = Util.nextInt(0, 100);
            if (randomQua >= 30 && pl.inventory.gem < 100000) {
                pl.inventory.gem += 100;
                pl.sendInfo();
                Service.getInstance().sendThongBao(pl, "Bạn vừa nhặt được 100 ngọc.");
                pl.sendInfo();
            }
            if (randomQua >= 30) {
                int VangRoi = Util.nextInt((HP * 80 / 100), HP);
                pl.inventory.gold += VangRoi;
                if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
                    pl.inventory.gold = Inventory.LIMIT_GOLD;
                }
            }
            
            //roi skh
            if (50 > randomQua && pl.point.power <= 500_000_000 && pl.map.isMapSKH() && !pl.isPet) {
                int items = 1;
                msg.writer().writeByte(items); //sl item roi
                for (int i = 0; i < items; i++) {
                    int randomItem = Util.nextInt(0, Mob_ItemDAO.itemList_SKH_idItem[pl.gender].length);
                    short idItem = (short) Mob_ItemDAO.itemList_SKH_idItem[pl.gender][randomItem];
                    int optionItem = Mob_ItemDAO.itemList_SKH_Optione[pl.gender][randomItem];
                    int paramOption =  Mob_ItemDAO.itemList_SKH_maxValue[pl.gender][randomItem];
                    int paramOption_Random = Util.nextInt(Math.round(paramOption / 2), paramOption);
                    paramOption_Random = paramOption_Random == 0 ? 1 : paramOption_Random;
                    ItemMap itemMap = new ItemMap(pl.map, ItemData.getTemplate(idItem), 1, this.pointX + ((i % 2 == 0) ? (-i * 3) : (+i * 3)), this.map.yPhysic(this.pointX, this.pointY), (int) pl.id);
                    System.out.println("item: " + itemMap.itemTemplate.name + " - " + idItem + " - " + optionItem + " - "+paramOption_Random);
                    List<ItemOption> options = new ArrayList<>();
                    int ops = 1;
                    for (int j = 0; j < ops; j++) {
                        options.add(new ItemOption(optionItem, (short) paramOption_Random));
                    }
                    int randomOptions = Util.nextInt(0, Mob_ItemDAO.itemList_SKH_Optione_Name[pl.gender].length);
                    int optionSHKName = Mob_ItemDAO.itemList_SKH_Optione_Name[pl.gender][randomOptions];
                    int optionSHKValue = Mob_ItemDAO.itemList_SKH_Optione_Value[pl.gender][randomOptions];
                    options.add(new ItemOption(optionSHKName, (short) 1));
                    options.add(new ItemOption(optionSHKValue, (short) 1));
                    itemMap.options = options;
                    itemMap.playerId = Math.abs(pl.id);
                    pl.map.addItem(itemMap);

                    msg.writer().writeShort(itemMap.itemMapId);// itemmapid
                    msg.writer().writeShort(itemMap.itemTemplate.id); // id item
                    msg.writer().writeShort(itemMap.x); // xend item
                    msg.writer().writeShort(itemMap.y); // yend item
                    msg.writer().writeInt((int) itemMap.playerId); // id nhan nat
                    
                    if (pl.inventory.getItemTimeId(219) != null) {
                        new Thread(() -> {
                            try {
                                Thread.sleep(500);
                            } catch (Exception e) {
                            }
                            Service.getInstance().pickItem(pl, itemMap.itemMapId);
                        }).start();
                    }
                }
            }

            if (randomQua >= 50) { // set ntn để khỏi rơi đồ á.
                int items = 1;
                msg.writer().writeByte(items); //sl item roi
                for (int i = 0; i < items; i++) {
                    int randomItem = Util.nextInt(0, Mob_ItemDAO.itemList_RoiDo_idItem[pl.gender].length);
                    short idItem = (short) Mob_ItemDAO.itemList_RoiDo_idItem[pl.gender][randomItem];
                    int optionItem = Mob_ItemDAO.itemList_RoiDo_Optione[pl.gender][randomItem];
                    int paramOption =  Mob_ItemDAO.itemList_RoiDo_maxValue[pl.gender][randomItem];
                    int paramOption_Random = Util.nextInt(Math.round(paramOption / 2), paramOption);
                    paramOption_Random = paramOption_Random == 0 ? 1 : paramOption_Random;
                    ItemMap itemMap = new ItemMap(pl.map, ItemData.getTemplate(idItem), 1, this.pointX + ((i % 2 == 0) ? (-i * 3) : (+i * 3)), this.map.yPhysic(this.pointX, this.pointY), (int) pl.id);
                    System.out.println("item: " + itemMap.itemTemplate.name + " - " + idItem + " - " + optionItem + " - "+paramOption_Random);
                    List<ItemOption> options = new ArrayList<>();
                    int ops = 1;
                    for (int j = 0; j < ops; j++) {
                        options.add(new ItemOption(optionItem, (short) paramOption_Random));
                    }
                    itemMap.options = options;
                    itemMap.playerId = Math.abs(pl.id);
                    pl.map.addItem(itemMap);

                    msg.writer().writeShort(itemMap.itemMapId);// itemmapid
                    msg.writer().writeShort(itemMap.itemTemplate.id); // id item
                    msg.writer().writeShort(itemMap.x); // xend item
                    msg.writer().writeShort(itemMap.y); // yend item
                    msg.writer().writeInt((int) itemMap.playerId); // id nhan nat
                    
                    if (pl.inventory.getItemTimeId(219) != null) {
                        new Thread(() -> {
                            try {
                                Thread.sleep(500);
                            } catch (Exception e) {
                            }
                            Service.getInstance().pickItem(pl, itemMap.itemMapId);
                        }).start();
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public void update() {
        if (isDie() && (System.currentTimeMillis() - timeDie) > 5000) {
            Message msg;
            try {
                msg = new Message(-13);
                msg.writer().writeByte(this.id);
                msg.writer().writeByte(this.tempId);
                msg.writer().writeByte(0);
                msg.writer().writeInt((this.maxHp));
                this.hp = (this.maxHp);
                Service.getInstance().sendMessAllPlayerInMap(map, msg);
                msg.cleanup();
            } catch (Exception e) {

            }
        }
        if (isStun && (Util.canDoWithTime(lastTimeStun, timeStun) || isDie())) {
            removeStun();
        }
        if (isThoiMien && (Util.canDoWithTime(lastTimeThoiMien, timeThoiMien) || isDie())) {
            removeThoiMien();
        }
        if (isBlindDCTT && (Util.canDoWithTime(lastTimeBlindDCTT, timeBlindDCTT)) || isDie()) {
            removeBlindDCTT();
        }
        if (isSocola && (Util.canDoWithTime(lastTimeSocola, timeSocola) || isDie())) {
            removeSocola();
        }
        if (isAnTroi && (Util.canDoWithTime(lastTimeAnTroi, timeAnTroi) || isDie())) {
            removeAnTroi();
        }
        attackPlayer();
    }

    public void active() {
        if (!actived) {
            actived = true;
            this.timer = new Timer();
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Mob.this.update();

                }
            }, 500L, 1000L);
        }
    }

    private void attackPlayer() {
        if (!isDie() && !isHaveEffectSkill() && Util.canDoWithTime(lastTimeAttackPlayer, 2000)) {
            Message msg;
            Player plAttack = getPlayerCanAttack();
            try {
                if (tempId == 0) { //check Mộc nhân
                    return;
                }
                if (plAttack != null) {

                    int dame = plAttack.injured(null, Util.nextInt(this.mindame, this.maxdame), false, this);
                    if (!plAttack.isPet) {
                        msg = new Message(-11);
                        msg.writer().writeByte(this.id);
                        msg.writer().writeInt(dame); //dame
                        plAttack.sendMessage(msg);
                        msg.cleanup();
                    }
                    msg = new Message(-10);
                    msg.writer().writeByte(this.id);
                    msg.writer().writeInt((int) plAttack.id);

                    msg.writer().writeInt(plAttack.point.getHP());
                    Service.getInstance().sendMessAnotherNotMeInMap(plAttack, msg);
                    msg.cleanup();

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.lastTimeAttackPlayer = System.currentTimeMillis();
            }
        }
    }

    public Player getPlayerCanAttack() {
        int distance = 130;// khoang cach quai danh ng
        for (int i = playerAttack.size() - 1; i >= 0; i--) {
            try {
                Player pl = playerAttack.get(i);
                if (pl == null) {
                    playerAttack.remove(i);
                    continue;
                }
                if (pl.map.id == this.map.id && pl.map.zoneId == this.map.zoneId
                        && pl.point.getHP() > 0
                        && Util.getDistance(pl, this) <= distance
                        && !pl.isGoHome) {
                    return pl;
                }
            } catch (Exception e) {
                playerAttack.remove(i);
            }
        }
        distance = 130;// khoang cach quai danh ng
        Player plAttack = null;
        try {
            for (Player pl : map.getPlayers()) {
                if (pl.isDie() || pl.isBoss) {
                    continue;
                }
                int dis = Util.getDistance(pl, this);
                if (dis <= distance) {
                    plAttack = pl;
                    distance = dis;
                }
            }
        } catch (Exception e) {

        }
        return plAttack;
    }

    public void addPlayerAttack(Player pl) {
        for (Player player : playerAttack) {
            if (player.equals(pl)) {
                return;
            }
        }
        if (playerAttack.size() > 10) {
            playerAttack.remove(0);
        }
        playerAttack.add(pl);
    }

    public int gethp() {
        return hp;
    }

    public void sethp(int hp) {
        if (this.hp < 0) {
            this.hp = 0;
        } else {
            this.hp = hp;
        }
    }

    public void setDie() {
        this.timeDie = System.currentTimeMillis();
        playerAttack.clear();
    }

    //effect skill =============================================================
    //effect skill =============================================================
    public long lastTimeStun;
    public int timeStun;
    public boolean isStun;

    public void startStun(long lastTimeStartBlind, int timeBlind) {
        this.lastTimeStun = lastTimeStartBlind;
        this.timeStun = timeBlind;
        isStun = true;
    }

    private void removeStun() {
        isStun = false;
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0);
            msg.writer().writeByte(1);
            msg.writer().writeByte(40);
            msg.writer().writeByte(this.id);
            Service.getInstance().sendMessAllPlayerInMap(map, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public boolean isThoiMien;
    public long lastTimeThoiMien;
    public int timeThoiMien;

    public void setThoiMien(long lastTimeThoiMien, int timeThoiMien) {
        this.isThoiMien = true;
        this.lastTimeThoiMien = lastTimeThoiMien;
        this.timeThoiMien = timeThoiMien;
        System.out.println("set thoi mien");
    }

    public void removeThoiMien() {
        this.isThoiMien = false;
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b5
            msg.writer().writeByte(1); //b6
            msg.writer().writeByte(41); //num6
            msg.writer().writeByte(this.id); //b7
            Service.getInstance().sendMessAllPlayerInMap(map, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public boolean isBlindDCTT;
    public long lastTimeBlindDCTT;
    public int timeBlindDCTT;

    public void setStartBlindDCTT(long lastTimeBlindDCTT, int timeBlindDCTT) {
        this.isBlindDCTT = true;
        this.lastTimeBlindDCTT = lastTimeBlindDCTT;
        this.timeBlindDCTT = timeBlindDCTT;
    }

    public void removeBlindDCTT() {
        this.isBlindDCTT = false;
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0);
            msg.writer().writeByte(1);
            msg.writer().writeByte(40);
            msg.writer().writeByte(this.id);
            Service.getInstance().sendMessAllPlayerInMap(map, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private boolean isHaveEffectSkill() {
        return isAnTroi || isBlindDCTT || isStun || isThoiMien;
    }
}
