package real.pet;

import java.util.Timer;
import java.util.TimerTask;
import real.item.CaiTrangData;
import real.map.MapManager;
import real.map.MapService;
import real.map.Mob;
import real.player.Player;
import real.skill.Skill;
import real.skill.SkillUtil;
import server.Service;
import server.Util;
import server.io.Message;

public class Pet extends Player {

    public Player master;

    private static final short ARANGE_FIND_MOB = 200;

    public static final byte FOLLOW = 0;
    public static final byte PROTECT = 1;
    public static final byte ATTACK = 2;
    public static final byte GOHOME = 3;
    public static final byte FUSION = 4;

    public byte status = 0;

    public long lastTimeDie;

    private boolean goingHome;

    private Mob mobAttack;
    private Player playerAttack;

    public byte getStatus() {
        return this.status;
    }

    public Pet(Player master) {
        this.master = master;
        this.isPet = true;
    }

    public void changeStatus(byte status) {
        if (goingHome || master.typeFusion != 0) {
            Service.getInstance().sendThongBao(master, "Không thể thực hiện");
            return;
        }
        Service.getInstance().chatJustForMe(master, this, getTextStatus(status));
        if (status == GOHOME) {
            goHome();
        } else if (status == FUSION) {
            fusion(false);
        }
        this.status = status;
    }

    private void joinMapMaster() {
        if (status != GOHOME && status != FUSION && !isDie()) {
            this.x = master.x + Util.nextInt(-10, 10);
            this.y = master.y;
            this.gotoMap(master.map);
            MapService.gI().joinMap(this, master.map);
        }
    }

    public void goHome() {
        if (this.status == GOHOME) {
            return;
        }
        goingHome = true;
        new Thread(() -> {
            try {
                Pet.this.status = Pet.ATTACK;
                Thread.sleep(2000);
            } catch (Exception e) {
            }
            this.gotoMap(MapManager.gI().getMapById(master.gender + 21).get(0));
            MapService.gI().joinMap(this, MapManager.gI().getMapById(master.gender + 21).get(0));
            Pet.this.status = Pet.GOHOME;
            goingHome = false;
        }).start();
    }

    private String getTextStatus(byte status) {
        switch (status) {
            case FOLLOW:
                return "Ok con lạy sư phụ, theo sư phụ";
            case PROTECT:
                return "dạ ! Con sẽ bảo vệ đến cọng lông sư phụ";
            case ATTACK:
                return "Ok sư phụ mấy thằng con lợn để con lo cho :D";
            case GOHOME:
                return "Ok con về, bai bai sư phụ :3";
            default:
                return "";
        }
    }

    public void fusion(boolean porata) {
        if (this.isDie()) {
            Service.getInstance().sendThongBao(master, "Không thể thực hiện");
            return;
        }
        if (porata) {
            master.typeFusion = Player.HOP_THE_PORATA;
        } else {
            master.lastTimeFusion = System.currentTimeMillis();
            master.typeFusion = Player.LUONG_LONG_NHAT_THE;
            Service.getInstance().sendItemTime(master, master.gender == 1 ? 3901 : 3790, Player.timeFusion / 1000);
        }
        this.status = FUSION;
        master.point.updateall();
        exitMapFusion();
        fusionEffect(master.typeFusion);
        Service.getInstance().Send_Caitrang(master);
        Service.getInstance().point(master);
    }

    public void unFusion() {
        master.typeFusion = 0;
        this.status = PROTECT;
        Service.getInstance().point(master);
        joinMapMaster();
        fusionEffect(master.typeFusion);
        Service.getInstance().Send_Caitrang(master);
        Service.getInstance().point(master);
    }

    private void fusionEffect(int type) {
        Message msg;
        try {
            msg = new Message(125);
            msg.writer().writeByte(type);
            msg.writer().writeInt((int) master.id);
            Service.getInstance().sendMessAllPlayerInMap(master.map, msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    private void exitMapFusion() {
        if (this.map != null) {
            MapService.gI().exitMap(this, this.map);
        }
    }

    private long lastTimeMoveIdle;
    private int timeMoveIdle;
    public boolean idle;

    private void moveIdle() {
        if (status == GOHOME || status == FUSION) {
            return;
        }
        if (idle && Util.canDoWithTime(lastTimeMoveIdle, timeMoveIdle)) {
            int dir = this.x - master.x <= 0 ? -1 : 1;
            move(master.x + Util.nextInt(dir == -1 ? 0 : -50, dir == -1 ? 50 : 0), master.y);
            lastTimeMoveIdle = System.currentTimeMillis();
            timeMoveIdle = Util.nextInt(5000, 8000);
        }
    }

    private long lastTimeMoveAtHome;
    private byte directAtHome = -1;

    @Override
    public void update() {
        try {
            super.update();
            increasePoint(); //cộng chỉ số
            updatePower(); //check mở skill...

            if (isDie()) {
                if (System.currentTimeMillis() - lastTimeDie > 50000) {
                    Service.getInstance().hsChar(this, point.getHPFull(), point.getMPFull());
                } else {
                    return;
                }
            }
            if (justRevived && this.map == master.map) {
                Service.getInstance().chatJustForMe(master, this, "Sư phụ ơi, con đây rồi hehehhehe!");
                justRevived = false;
            }
            if (this.map == null || this.map != master.map) {
                joinMapMaster();
            }
            if (master.isDie() || this.isDie() || playerSkill.isHaveEffectSkill()) {
                return;
            }
            moveIdle();
            switch (status) {
                case FOLLOW:
//                    followMaster(60);
                    break;
                case PROTECT:
                case ATTACK:
                    if (useSkill3() || useSkill4()) {
                        break;
                    }
                    mobAttack = this.findMobAttack(ARANGE_FIND_MOB);
                    if (mobAttack == null) {
                        break;
                    }
                    if (Util.getDistance(this, mobAttack) > 55) {
                        playerSkill.skillSelect = getSkill(2);
                        if (playerSkill.skillSelect == null) {
                            playerSkill.skillSelect = getSkill(1);
                        }
                    } else {
                        playerSkill.skillSelect = getSkill(1);
                    }
                    int dis = Util.getDistance(this, mobAttack);
                    switch (playerSkill.getIndexSkillSelect()) {
                        case 1: //skill đấm
                            if (dis > 50) {
                                if (dis < 80) {
                                    move(mobAttack.pointX, mobAttack.pointY);
                                }
                            } else {
                                if (canUseSkill()) {
                                    move(mobAttack.pointX + Util.nextInt(-20, 20), mobAttack.pointY);
                                }
                            }
                            playerSkill.useSkill(playerAttack, mobAttack);
                            break;
                        case 2: //skill chưởng
                            if (dis <= 50) {
                                playerSkill.skillSelect = playerSkill.skills.get(0);
                            }
                            playerSkill.useSkill(playerAttack, mobAttack);
                            break;
                    }
                    break;

                case GOHOME:
                    if (this.map.id == 21 || this.map.id == 22 || this.map.id == 23) {
                        if (System.currentTimeMillis() - lastTimeMoveAtHome <= 5000) {
                            return;
                        } else {
                            if (this.map.id == 21) {
                                if (directAtHome == -1) {
                                    move(250, 336);
                                    directAtHome = 1;
                                } else {
                                    move(200, 336);
                                    directAtHome = -1;
                                }
                            } else if (this.map.id == 22) {
                                if (directAtHome == -1) {
                                    move(500, 336);
                                    directAtHome = 1;
                                } else {
                                    move(452, 336);
                                    directAtHome = -1;
                                }
                            } else if (this.map.id == 22) {
                                if (directAtHome == -1) {
                                    move(250, 336);
                                    directAtHome = 1;
                                } else {
                                    move(200, 336);
                                    directAtHome = -1;
                                }
                            }
                            Service.getInstance().chatJustForMe(master, this, "Sư phụ chơi đồ, chơi cỏ một mình đấy hả!");
                            lastTimeMoveAtHome = System.currentTimeMillis();
                        }
                    }
                    break;
            }
        } catch (Exception e) {
        }

        if (mobAttack == null || (mobAttack != null && mobAttack.isDie())) {
            idle = true;
        }
    }

    private long lastTimeAskPea;

    public void askPea() {
        if (Util.canDoWithTime(lastTimeAskPea, 10000)) {
            Service.getInstance().chatJustForMe(master, this, "Sư phụ ơi cho con đậu thần");
            lastTimeAskPea = System.currentTimeMillis();
        }
    }

    private boolean useSkill3() {
        try {
            playerSkill.skillSelect = getSkill(3);
            if (playerSkill.skillSelect == null) {
                return false;
            }
            switch (this.playerSkill.skillSelect.template.id) {
                case Skill.THAI_DUONG_HA_SAN:
                    if (this.canUseSkill()) {
                        this.playerSkill.useSkill(null, null);
                        Service.getInstance().chatJustForMe(master, this, "Thái dương hạ san");
                        return true;
                    }
                    return false;
                case Skill.TAI_TAO_NANG_LUONG:
                    if (canUseSkill() && ((float) this.point.getHP() / this.point.getHPFull() * 100) <= 20) {
                        this.playerSkill.useSkill(null, null);
                        return true;
                    }
                    return false;
                case Skill.KAIOKEN:
                    if (canUseSkill()) {
                        mobAttack = this.findMobAttack(ARANGE_FIND_MOB);
                        if (mobAttack == null) {
                            return false;
                        }
                        int dis = Util.getDistance(this, mobAttack);
                        if (dis > 50) {
                            move(mobAttack.pointX, mobAttack.pointY);
                        } else {
                            if (canUseSkill()) {
                                move(mobAttack.pointX + Util.nextInt(-20, 20), mobAttack.pointY);
                            }
                        }
                        playerSkill.useSkill(playerAttack, mobAttack);
                        getSkill(1).lastTimeUseThisSkill = System.currentTimeMillis();
                        return true;
                    }
                    return false;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean useSkill4() {
        try {
            this.playerSkill.skillSelect = getSkill(4);
            switch (this.playerSkill.skillSelect.template.id) {
                case Skill.BIEN_KHI:
                    if (!this.playerSkill.isMonkey && this.canUseSkill()) {
                        this.playerSkill.useSkill(null, null);
                        return true;
                    }
                    return false;
                case Skill.KHIEN_NANG_LUONG:
                    if (!this.playerSkill.isShielding && this.canUseSkill()) {
                        this.playerSkill.useSkill(null, null);
                        return true;
                    }
                    return false;
                case Skill.DE_TRUNG:
                    if (this.mobMe == null && this.canUseSkill()) {
                        this.playerSkill.useSkill(null, null);
                        return true;
                    }
                    return false;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private long lastTimeIncreasePoint;

    private void increasePoint() {
        if (System.currentTimeMillis() - lastTimeIncreasePoint > 10) {
            this.point.increasePoint((byte) Util.nextInt(0, 4), (short) 1);
            lastTimeIncreasePoint = System.currentTimeMillis();
        }
    }

    public void followMaster() {
        if (this.isDie() || playerSkill.isHaveEffectSkill()) {
            return;
        }
        switch (this.status) {
            case ATTACK:
                if ((mobAttack != null && Util.getDistance(this, master) <= 500)) {
                    break;
                }
            case FOLLOW:
            case PROTECT:
                followMaster(60);
                break;
        }
    }

    private void followMaster(int dis) {
        int mX = master.x;
        int mY = master.y;
        int disX = this.x - mX;
        if (Math.sqrt(Math.pow(mX - this.x, 2) + Math.pow(mY - this.y, 2)) >= dis) {
            if (disX < 0) {
                this.x = mX - Util.nextInt(0, dis);
            } else {
                this.x = mX + Util.nextInt(0, dis);
            }
            this.y = mY;
            this.move(this.x, this.y);
        }
    }

    private static final short[][] petID = {{285, 286, 287}, {288, 289, 290}, {282, 283, 284}, {304, 305, 303}};

    public short getAvatar() {
        return petID[3][this.gender];
    }

    @Override
    public short getHead() {
        if (playerSkill.isMonkey) {
            return (short) HEADMONKEY[playerSkill.getLevelMonkey() - 1];
        } else if (playerSkill.isSocola) {
            return 412;
        } else if (inventory.itemsBody.get(5).id != -1) {
            CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(inventory.itemsBody.get(5).template.id);
            if (ct != null) {
                return (short) ((short) ct.getID()[0] != -1 ? ct.getID()[0] : inventory.itemsBody.get(5).template.part);
            }
        }
        if (this.point.getPower() < 1500000) {
            return petID[this.gender][0];
        } else {
            return petID[3][this.gender];
        }
    }

    @Override
    public short getBody() {
        if (playerSkill.isMonkey) {
            return 193;
        } else if (playerSkill.isSocola) {
            return 413;
        } else if (inventory.itemsBody.get(5).id != -1) {
            CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(inventory.itemsBody.get(5).template.id);
            if (ct != null && ct.getID()[1] != -1) {
                return (short) ct.getID()[1];
            }
        }
        if (inventory.itemsBody.get(0).id != -1) {
            return inventory.itemsBody.get(0).template.part;
        }
        if (this.point.getPower() < 1500000) {
            return petID[this.gender][1];
        } else {
            return (short) (gender == 1 ? 59 : 57);
        }
    }

    @Override
    public short getLeg() {
        if (playerSkill.isMonkey) {
            return 194;
        } else if (playerSkill.isSocola) {
            return 414;
        } else if (inventory.itemsBody.get(5).id != -1) {
            CaiTrangData.CaiTrang ct = CaiTrangData.getCaiTrangByTempID(inventory.itemsBody.get(5).template.id);
            if (ct != null && ct.getID()[2] != -1) {
                return (short) ct.getID()[2];
            }
        }
        if (inventory.itemsBody.get(1).id != -1) {
            return inventory.itemsBody.get(1).template.part;
        }

        if (this.point.getPower() < 1500000) {
            return petID[this.gender][2];
        } else {
            return (short) (gender == 1 ? 60 : 58);
        }
    }

    @Override
    public void move(int _toX, int _toY) {
        super.move(_toX, _toY);
        idle = false;
    }

    private Mob findMobAttack(int distance) {
        Mob mobAtt = null;
        for (Mob mob : map.mobs) {
            int dis = Util.getDistance(this, mob);
            if (!mob.isDie() && dis <= distance) {
                distance = dis;
                mobAtt = mob;
            }
        }
        return mobAtt;
    }

    private void updatePower() {
        switch (this.playerSkill.skills.size()) {
            case 1:
                if (this.point.getPower() >= 150000000) {
                    openSkill2();
                }
                break;
            case 2:
                if (this.point.getPower() >= 1500000000) {
                    openSkill3();
                }
                break;
            case 3:
                if (this.point.getPower() >= 20000000000L) {
                    openSkill4();
                }
                break;
        }
    }

    private void openSkill2() {
        Skill skill = null;
        int tiLeKame = 30;
        int tiLeMasenko = 50;
        int tiLeAntomic = 20;

        int rd = Util.nextInt(1, 100);
        if (rd <= tiLeKame) {
            skill = SkillUtil.createSkill(Skill.KAMEJOKO, 1);
        } else if (rd <= tiLeKame + tiLeMasenko) {
            skill = SkillUtil.createSkill(Skill.MASENKO, 1);
        } else if (rd <= tiLeKame + tiLeMasenko + tiLeAntomic) {
            skill = SkillUtil.createSkill(Skill.ANTOMIC, 1);
        }
        this.playerSkill.skills.add(skill);
    }

    private void openSkill3() {
        Skill skill = null;
        int tiLeTDHS = 10;
        int tiLeTTNL = 70;
        int tiLeKOK = 20;

        int rd = Util.nextInt(1, 100);
        if (rd <= tiLeTDHS) {
            skill = SkillUtil.createSkill(Skill.THAI_DUONG_HA_SAN, 1);
        } else if (rd <= tiLeTDHS + tiLeTTNL) {
            skill = SkillUtil.createSkill(Skill.TAI_TAO_NANG_LUONG, 1);
        } else if (rd <= tiLeTDHS + tiLeTTNL + tiLeKOK) {
            skill = SkillUtil.createSkill(Skill.KAIOKEN, 1);
        }
        this.playerSkill.skills.add(skill);
    }

    private void openSkill4() {
        Skill skill = null;
        int tiLeBienKhi = 10;
        int tiLeDeTrung = 70;
        int tiLeKNL = 20;

        int rd = Util.nextInt(1, 100);
        if (rd <= tiLeBienKhi) {
            skill = SkillUtil.createSkill(Skill.BIEN_KHI, 1);
        } else if (rd <= tiLeBienKhi + tiLeDeTrung) {
            skill = SkillUtil.createSkill(Skill.DE_TRUNG, 1);
        } else if (rd <= tiLeBienKhi + tiLeDeTrung + tiLeKNL) {
            skill = SkillUtil.createSkill(Skill.KHIEN_NANG_LUONG, 1);
        }
        this.playerSkill.skills.add(skill);
    }

    private Skill getSkill(int indexSkill) {
        switch (indexSkill) {
            case 1:
                for (Skill skill : playerSkill.skills) {
                    byte temp = skill.template.id;
                    if (temp == 0 || temp == 2 || temp == 4) {
                        return skill;
                    }
                }
                return null;
            case 2:
                for (Skill skill : playerSkill.skills) {
                    byte temp = skill.template.id;
                    if (temp == 1 || temp == 3 || temp == 5) {
                        return skill;
                    }
                }
                return null;
            case 3:
                for (Skill skill : playerSkill.skills) {
                    byte temp = skill.template.id;
                    if (temp == 6 || temp == 8 || temp == 9) {
                        return skill;
                    }
                }
                return null;
            case 4:
                for (Skill skill : playerSkill.skills) {
                    byte temp = skill.template.id;
                    if (temp == 12 || temp == 13 || temp == 19) {
                        return skill;
                    }
                }
                return null;
            default:
                return null;
        }
    }
}
