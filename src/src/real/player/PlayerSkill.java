package real.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import real.func.PVP;
import real.map.Map;
import real.map.MapService;
import real.map.Mob;
import real.pet.MobMe;
import real.pet.Pet;
import real.player.Player;
import real.skill.NClass;
import real.skill.Skill;
import real.skill.SkillData;
import real.skill.SkillUtil;
import server.Service;
import server.Util;
import server.io.Message;

public class PlayerSkill {

    private Player player;
    public List<Skill> skills;
    public Skill skillSelect;

    public PlayerSkill(Player player) {
        this.player = player;
        skills = new ArrayList<>();
    }

    public Skill getSkillbyId(int id) {
        for (Skill skill : skills) {
            if (skill.template.id == id) {
                return skill;
            }
        }
        return null;
    }

    public byte[] skillShortCut = new byte[5];

    public void sendSkillShortCut() {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 61);
            msg.writer().writeUTF("KSkill");
            msg.writer().writeInt(skillShortCut.length);
            msg.writer().write(skillShortCut);
            player.sendMessage(msg);
            msg.cleanup();
            msg = Service.getInstance().messageSubCommand((byte) 61);
            msg.writer().writeUTF("OSkill");
            msg.writer().writeInt(skillShortCut.length);
            msg.writer().write(skillShortCut);
            player.sendMessage(msg);
            msg.cleanup();
            System.out.println("send skill");
        } catch (Exception e) {
        }
    }

    public void update() {
        if (isMonkey && (Util.canDoWithTime(lastTimeUpMonkey, timeMonkey) || player.isDie())) {
            monkeyDown();
        }
        if (isShielding && (Util.canDoWithTime(lastTimeShieldUp, timeShield) || player.isDie())) {
            shieldDown();
        }
        if (useTroi && (Util.canDoWithTime(lastTimeTroi, timeTroi) || player.isDie())) {
            removeUseTroi();
        }
        if (anTroi && (Util.canDoWithTime(lastTimeAnTroi, timeAnTroi) || player.isDie())) {
            removeAnTroi();
        }
        if (isStun && (Util.canDoWithTime(lastTimeStartStun, timeStun) || player.isDie())) {
            removeStun();
        }
        if (isThoiMien && (Util.canDoWithTime(lastTimeThoiMien, timeThoiMien) || player.isDie())) {
            removeThoiMien();
        }
        if (isBlindDCTT && (Util.canDoWithTime(lastTimeBlindDCTT, timeBlindDCTT) || player.isDie())) {
            removeBlindDCTT();
        }
        if (isSocola && (Util.canDoWithTime(lastTimeSocola, timeSocola))) {
            removeSocola();
        }

        if (tiLeHPHuytSao != 0 && Util.canDoWithTime(lastTimeHuytSao, 30000)) {
            removeHuytSao();
        }
    }

    //--------------------------------------------------------------------------Biến Socola
//    public void setSocola(long timeskill) {
//        this.isSocola = true;
//        this.timeSocola = timeskill;
//    }
//
//    public void removeSocola() {
//        Message msg;
//        try {
//            msg = new Message(-90);
//            msg.writer().writeByte(1);// check type
//            msg.writer().writeInt((int) player.id); //id player
//            msg.writer().writeShort(player.getHead());//set head
//            msg.writer().writeShort(player.getBody());//setbody
//            msg.writer().writeShort(player.getLeg());//set leg
//            msg.writer().writeByte(player.playerSkill.getMonkey());//set khỉ
//            Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
//            msg.cleanup();
//            this.isSocola = false;
//            this.timeSocola = 0;
//        } catch (Exception e) {
//
//        }
//    }
//
//    public void bienSocola(int idHold, boolean isChar) {
//        Message msg;
//        try {
//            socola = getSkillbyId(18);
//            long timeskill = System.currentTimeMillis() + socola.coolDown;
//            if (isChar) {
//                Player pl = player.map.getPlayerInMap(idHold);
//                msg = new Message(-90);
//                msg.writer().writeByte(1);// check type
//                msg.writer().writeInt((int) pl.id); //id player
//                msg.writer().writeShort(412);//set head
//                msg.writer().writeShort(413);//setbody
//                msg.writer().writeShort(414);//set leg
//                msg.writer().writeByte(0);//set khỉ
//                Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
//                pl.playerSkill.setSocola(timeskill);
//                msg.cleanup();
//            } else {
//                msg = new Message(-112);
//                msg.writer().writeByte(1);
//                msg.writer().writeByte(idHold); //b4
//                msg.writer().writeShort(4133);//b5
//                Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
//                msg.cleanup();
//                player.map.mobs.get(idHold).setSocola(timeskill);
//            }
//        } catch (Exception e) {
//        }
//    }
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    public void useSkill(Player plTarget, Mob mobTarget) {
        if (player.isDie()) {
            return;
        }
        if (isHaveEffectSkill()) {
            return;
        }
        if (plTarget != null && plTarget.isDie() || mobTarget != null && mobTarget.isDie()) {
            return;
        }
        if (!Util.canDoWithTime(skillSelect.lastTimeUseThisSkill, skillSelect.coolDown)) {
            return;
        }
        byte skillId = skillSelect.template.id;
        switch (skillId) {
            case Skill.QUA_CAU_KENH_KHI:
            case Skill.TU_SAT:
            case Skill.MAKANKOSAPPO:
                if (nemQCKK && tuSat && laze) {
                    if (player.setMPUseSkill()) {
                        skillSelect.lastTimeUseThisSkill = System.currentTimeMillis();
                    } else {
                        return;
                    }
                }
                break;
            default:
                if (player.setMPUseSkill()) {
                    skillSelect.lastTimeUseThisSkill = System.currentTimeMillis();
                } else {
                    if (player.isPet) {
                        ((Pet) player).askPea();
                    }
                    return;
                }
                break;
        }

//        if (!nemQCKK && !tuSat && !laze) {
//            if (!Util.canDoWithTime(skillSelect.lastTimeUseThisSkill, skillSelect.coolDown)) {
//                return;
//            }
//            if (!player.setMPUseSkill()) {
//                if (player.isPet) {
//                    ((Pet) player).askPea();
//                }
//                return;
//            } else {
//                skillSelect.lastTimeUseThisSkill = System.currentTimeMillis();
//            }
//        }
        Message msg;
        List<Mob> mobs;
        List<Player> players;
        boolean miss = false;
        switch (skillSelect.template.id) {
            case Skill.KAIOKEN: //kaioken
                int hpUse = player.point.getHPFull() / 100 * 10;
                if (player.point.getHP() <= hpUse) {
                    break;
                } else {
                    player.point.setHP(player.point.getHP() - hpUse);
                    Service.getInstance().sendInfoChar30c4(player);
                    Service.getInstance().Send_Info_NV(player);
                }
            case Skill.DRAGON: //đấm dragon
            case Skill.DEMON: //đấm demon
            case Skill.GALIK: //đấm galick
            case Skill.LIEN_HOAN: //liên hoàn
                if (plTarget != null) {
                    if (Util.getDistance(player, plTarget) > 35) {
                        miss = true;
                    }
                }
                if (mobTarget != null) {
                    if (Util.getDistance(player, mobTarget) > 35) {
                        miss = true;
                    }
                }
            case Skill.KAMEJOKO: //kamejoko
            case Skill.MASENKO: //masenko
            case Skill.ANTOMIC: //antomic
                if (plTarget != null) {
                    Util.log(player.name);
                    plTarget.attackPlayer(player, miss);
                }
                if (mobTarget != null) {
                    mobTarget.attackMob(player, miss);
                }
                if (player.mobMe != null) {
                    player.mobMe.attack(plTarget, mobTarget);
                }

                break;

            case Skill.THAI_DUONG_HA_SAN: //thái dương hạ san
                mobs = new ArrayList<>();
                players = new ArrayList<>();

                if (player.cFlag != 0) {
                    for (Player pl : player.map.getPlayers()) {
                        if (pl != player && pl.cFlag != 0 && (pl.cFlag != player.cFlag || player.cFlag == 8 || pl.cFlag == 8)) {
                            if (Util.getDistance(player, pl) <= SkillUtil.getRangeStun(skillSelect.point)) {
                                players.add(pl);
                            }
                        }
                    }
                }

                for (Mob mob : player.map.mobs) {
                    if (Util.getDistance(player, mob) <= SkillUtil.getRangeStun(skillSelect.point)) {
                        mobs.add(mob);
                    }
                }
                try {
                    msg = new Message(-45);
                    msg.writer().writeByte(0);
                    msg.writer().writeInt((int) player.id);
                    msg.writer().writeShort(skillSelect.skillId);

                    int blindTime = SkillUtil.getTimeStun(skillSelect.point);
                    msg.writer().writeByte(mobs.size());
                    for (Mob mob : mobs) {
                        msg.writer().writeByte(mob.id);
                        msg.writer().writeByte(blindTime / 1000);
                        mob.startStun(System.currentTimeMillis(), blindTime);
                    }

                    msg.writer().writeByte(players.size());
                    for (Player pl : players) {
                        msg.writer().writeInt((int) pl.id);
                        msg.writer().writeByte(blindTime / 1000);
                        pl.playerSkill.startStun(System.currentTimeMillis(), blindTime);
                    }
                    Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
                    msg.cleanup();
                } catch (Exception e) {
                }

                break;
            case Skill.TRI_THUONG: //trị thương
                int percentTriThuong = SkillUtil.getPercentTriThuong(skillSelect.point);
                Service.getInstance().chat(plTarget, "Cảm ơn " + player.name + " đã cứu mình");
                try {
                    msg = new Message(-60);
                    msg.writer().writeInt((int) player.id); //id pem
                    msg.writer().writeByte(skillSelect.skillId); //skill pem
                    msg.writer().writeByte(1); //số người pem
                    msg.writer().writeInt((int) plTarget.id); //id ăn pem

                    msg.writer().writeByte(0); //read continue
                    msg.writer().writeByte(2); //type skill
                    Service.getInstance().sendMessAllPlayerInMap(plTarget.map, msg);
                    msg.cleanup();

                    boolean isDie = plTarget.isDie();
                    player.point.setHP(player.point.getHP() + (player.point.getHPFull() * percentTriThuong / 100));
                    plTarget.point.setHP(plTarget.point.getHP() + (plTarget.point.getHPFull() * percentTriThuong / 100));
                    plTarget.point.setMP(plTarget.point.getMP() + (plTarget.point.getMPFull() * percentTriThuong / 100));
                    if (isDie) {
                        Service.getInstance().Send_Info_NV(player);
                        Service.getInstance().hsChar(plTarget, plTarget.point.getHP(), plTarget.point.getMP());
                        Service.getInstance().sendInfoChar30c4(plTarget);
                        player.sendInfoHPMP();
                    } else {
                        Service.getInstance().Send_Info_NV(player);
                        plTarget.sendInfoHPMP();
                        player.sendInfoHPMP();
                    }
                    Service.getInstance().Send_Info_NV(plTarget);
                    System.out.println("tri thuonogggggggggggg");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Service.getInstance().lastTimeUseThisSkill(player);
                break;
            case Skill.TAI_TAO_NANG_LUONG: //tái tạo năng lượng
                if (player.point.mp <= 0) {
                    Service.getInstance().sendThongBao(player, "Không đủ KI để sử dụng kĩ năng này");
                } else {
                    charge();
                }
                break;
            case Skill.QUA_CAU_KENH_KHI: //quả cầu kênh khi
                if (nemQCKK) {
                    nemQCKK = false;
                    if (plTarget != null) {
                        try {
                            boolean phanSatThuong = false;
                            if (phanSatThuong) {
                                msg = new Message(56);
                                msg.writer().writeInt((int) player.id);
                                int hpNguocLai = -player.injured(null, 100, true);
                                msg.writer().writeInt(player.point.getHP());
                                msg.writer().writeInt(hpNguocLai);
                                msg.writer().writeBoolean(false); //crit
                                msg.writer().writeByte(36); //hiệu ứng pst
                                Service.getInstance().sendMessAllPlayerInMap(plTarget.map, msg);
                                msg.cleanup();
                            }

                            msg = new Message(-60);
                            msg.writer().writeInt((int) player.id); //id pem
                            msg.writer().writeByte(skillSelect.skillId); //skill pem
                            msg.writer().writeByte(1); //số người pem
                            msg.writer().writeInt((int) plTarget.id); //id ăn pem

                            msg.writer().writeByte(1); //read continue
                            msg.writer().writeByte(0); //type skill

                            int dameHit = plTarget.injured(player, player.point.getDameAttack(), false);
                            msg.writer().writeInt(dameHit); //dame ăn
                            msg.writer().writeBoolean(false); //is die
                            msg.writer().writeBoolean(player.point.isCrit); //crit
                            Service.getInstance().sendMessAllPlayerInMap(plTarget.map, msg);
                            msg.cleanup();
                            plTarget.sendInfo();

                        } catch (Exception e) {
                        }
                    }
                    if (mobTarget != null) {
                        mobTarget.attackMob(player, false);
                    }
                } else {
                    try {
                        nemQCKK = true;
                        msg = new Message(-45);
                        msg.writer().writeByte(4);
                        msg.writer().writeInt((int) player.id);
                        msg.writer().writeShort(skillSelect.skillId);
                        msg.writer().writeShort(3000);
                        Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
                        msg.cleanup();
                    } catch (Exception e) {
                    }
                }
                break;
            case Skill.MAKANKOSAPPO: //makankosappo
                if (laze) {
                    laze = false;
                    if (plTarget != null) {
                        try {
                            boolean phanSatThuong = false;
                            if (phanSatThuong) {
                                msg = new Message(56);
                                msg.writer().writeInt((int) player.id);
                                int hpNguocLai = -player.injured(null, 100, true);
                                msg.writer().writeInt(player.point.getHP());
                                msg.writer().writeInt(hpNguocLai);
                                msg.writer().writeBoolean(false); //crit
                                msg.writer().writeByte(36); //hiệu ứng pst
                                Service.getInstance().sendMessAllPlayerInMap(plTarget.map, msg);
                                msg.cleanup();
                            }

                            msg = new Message(-60);
                            msg.writer().writeInt((int) player.id); //id pem
                            msg.writer().writeByte(skillSelect.skillId); //skill pem
                            msg.writer().writeByte(1); //số người pem
                            msg.writer().writeInt((int) plTarget.id); //id ăn pem

                            msg.writer().writeByte(1); //read continue
                            msg.writer().writeByte(0); //type skill

                            int dameHit = plTarget.injured(player, player.point.getDameAttack(), false);
                            msg.writer().writeInt(dameHit); //dame ăn
                            msg.writer().writeBoolean(false); //is die
                            msg.writer().writeBoolean(player.point.isCrit); //crit
                            Service.getInstance().sendMessAllPlayerInMap(plTarget.map, msg);
                            msg.cleanup();
                            plTarget.sendInfo();

                        } catch (Exception e) {
                        }
                    }
                    if (mobTarget != null) {
                        mobTarget.attackMob(player, false);
                    }

                } else {
                    try {
                        laze = true;
                        msg = new Message(-45);
                        msg.writer().writeByte(4);
                        msg.writer().writeInt((int) player.id);
                        msg.writer().writeShort(skillSelect.skillId);
                        msg.writer().writeShort(3000);
                        Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
                        msg.cleanup();
                    } catch (Exception e) {

                    }
                }
                break;
            case Skill.DE_TRUNG: //đẻ trứng

                try {
                    sendEffectUseSkill();
                    if (player.mobMe != null) {
                        player.mobMe.mobMeDie();
                    }
                    player.mobMe = new MobMe(player);
                } catch (Exception e) {
                }

                break;
            case Skill.BIEN_KHI: //biến khỉ
                try {
                    System.out.println(player.name + " biến khỉ=============================");
                    Thread.sleep(2000);
                    isMonkey = true;
                    timeMonkey = SkillUtil.getTimeMonkey(skillSelect.point);
                    if (player.point.isSKH(134)) {
                        timeMonkey *= 5;
                    }
                    lastTimeUpMonkey = System.currentTimeMillis();
                    levelMonkey = (byte) skillSelect.point;
                    player.point.setHP(player.point.getHP() * 2);

                    msg = new Message(-45);
                    msg.writer().writeByte(6);
                    msg.writer().writeInt((int) player.id);
                    msg.writer().writeShort(skillSelect.skillId);
                    Service.getInstance().sendMessAllPlayerInMap(this.player.map, msg);
                    msg.cleanup();

                    Service.getInstance().sendSpeedPlayer(player, 0);
                    Service.getInstance().Send_Caitrang(player);

                    Service.getInstance().sendSpeedPlayer(player, -1);

                    if (!player.isPet) {
                        msg = Service.getInstance().messageSubCommand((byte) 5);
                        msg.writer().writeInt(player.point.getHPFull());
                        player.sendMessage(msg);
                        msg.cleanup();
                    }

                    Service.getInstance().point(player);
                    Service.getInstance().Send_Info_NV(player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Skill.TU_SAT: //tự sát
                if (tuSat) {
                    tuSat = false;
                    players = new ArrayList<>();
                    mobs = new ArrayList<>();
                    for (Mob mob : player.map.mobs) {
                        if (Util.getDistance(player, mob) <= SkillUtil.getRangeBom(skillSelect.point) && mob.tempId != 0) {
                            mobs.add(mob);
                        }
                    }

                    for (Player pl : player.map.getPlayers()) {
                        PVP pvp = PVP.gI().findPvp(pl);
                        if (pvp != null && (pvp.player1.equals(player) || pvp.player2.equals(player))) {
                            players.add(pl);
                            continue;
                        }
                        if (pl != player && (player.typePk == 5 || pl.typePk == 5 || (player.cFlag != 0 && pl.cFlag != 0 && (pl.cFlag != player.cFlag || player.cFlag == 8 || pl.cFlag == 8)))) {
                            if (Util.getDistance(player, pl) <= SkillUtil.getRangeBom(skillSelect.point)) {
                                players.add(pl);
                            }
                        }
                        if (pl.isBoss) {
                            players.add(pl);
                        }
                    }

                    for (Player pl : players) {
                        pl.injured(player, player.point.getHPFull(), false);
                        pl.sendInfoHPMP();
                    }
                    for (Mob mob : mobs) {
                        mob.injured(player, player.point.getHPFull());
                    }
                    player.injured(null, 2147483647, true);
                    System.out.println("so nguoi chet: " + players.size());
                } else {
                    try {
                        tuSat = true;
                        msg = new Message(-45);
                        msg.writer().writeByte(7);
                        msg.writer().writeInt((int) player.id);
                        msg.writer().writeShort(skillSelect.skillId);
                        msg.writer().writeShort(2000);
                        Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
                        msg.cleanup();
                    } catch (Exception e) {
                    }
                }
                break;
            case Skill.SOCOLA: //socola
                try {
                    sendEffectUseSkill();
                    int timeSocola = 5000;
                    if (plTarget != null) {
                        plTarget.playerSkill.setSocola(System.currentTimeMillis(), timeSocola);
                        Service.getInstance().sendItemTime(plTarget, 3780, timeSocola / 1000);
                        Service.getInstance().Send_Caitrang(plTarget);
                    } else {
                        msg = new Message(-112);
                        msg.writer().writeByte(1);
                        msg.writer().writeByte(mobTarget.id); //b4
                        msg.writer().writeShort(4133);//b5
                        Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
                        msg.cleanup();
                        mobTarget.setSocola(System.currentTimeMillis(), timeSocola);
                    }
                } catch (Exception e) {
                }

                break;
            case Skill.KHIEN_NANG_LUONG: //khiên năng lượng
                isShielding = true;
                lastTimeShieldUp = System.currentTimeMillis();
                timeShield = SkillUtil.getTimeShield(skillSelect.point);
                try {
                    msg = new Message(-124);
                    msg.writer().writeByte(1); //b5
                    msg.writer().writeByte(0); //b6
                    msg.writer().writeByte(33); //num3
                    msg.writer().writeInt((int) player.id); //num4
                    Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
                    msg.cleanup();

                    Service.getInstance().sendItemTime(player, 3784, timeShield / 10000 );
                } catch (Exception e) {
                }
                break;
            case Skill.DICH_CHUYEN_TUC_THOI: //dịch chuyển tức thời
                int timeDCTT = SkillUtil.getTimeDCTT(skillSelect.point) * 5;
                if (mobTarget != null) {
                    try {
                        mobTarget.setStartBlindDCTT(System.currentTimeMillis(), timeDCTT);
                        mobTarget.attackMob(player, false);
                        Service.getInstance().setPos(player, mobTarget.pointX, mobTarget.pointY);

                        msg = new Message(-124);
                        msg.writer().writeByte(1);
                        msg.writer().writeByte(1);
                        msg.writer().writeByte(40);
                        msg.writer().writeByte(mobTarget.id);
                        Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
                        msg.cleanup();

                    } catch (Exception e) {

                    }
                }
                if (plTarget != null) {
                    try {
                        plTarget.playerSkill.setBlindDCTT(System.currentTimeMillis(), timeDCTT);
                        Service.getInstance().setPos(player, plTarget.x, plTarget.y);

                        msg = new Message(-124);
                        msg.writer().writeByte(1);
                        msg.writer().writeByte(0);
                        msg.writer().writeByte(40);
                        msg.writer().writeInt((int) plTarget.id);
                        msg.writer().writeByte(0);
                        msg.writer().writeByte(32);
                        Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
                        msg.cleanup();

                        msg = new Message(-60);
                        msg.writer().writeInt((int) player.id); //id pem
                        msg.writer().writeByte(skillSelect.skillId); //skill pem
                        msg.writer().writeByte(1); //số người pem
                        msg.writer().writeInt((int) plTarget.id); //id ăn pem

                        msg.writer().writeByte(1); //read continue
                        msg.writer().writeByte(0); //type skill

                        int dameHit = plTarget.injured(player, player.point.getDameAttack(), false);
                        msg.writer().writeInt(dameHit); //dame ăn
                        msg.writer().writeBoolean(false); //is die
                        msg.writer().writeBoolean(player.point.isCrit); //crit
                        Service.getInstance().sendMessAllPlayerInMap(plTarget.map, msg);
                        msg.cleanup();
                        plTarget.sendInfo();

                        Service.getInstance().point(plTarget);
                        Service.getInstance().sendItemTime(plTarget, 3779, timeDCTT / 1000);

                    } catch (Exception e) {

                    }
                }

                player.point.setCrit100();
                Service.getInstance().lastTimeUseThisSkill(player);

                break;
            case Skill.HUYT_SAO: //huýt sáo
                for (Player pl : player.map.getPlayers()) {
                    if (pl.gender != 1) {
                        pl.playerSkill.setHuytSao(SkillUtil.getPercentHPHuytSao(skillSelect.point));
                        try {
                            msg = new Message(-124);
                            msg.writer().writeByte(1); //b5
                            msg.writer().writeByte(0); //b6
                            msg.writer().writeByte(39); //num3
                            msg.writer().writeInt((int) pl.id); //num4
                            Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
                            msg.cleanup();

                            Service.getInstance().point(pl);
                            Service.getInstance().Send_Info_NV(pl);
                            Service.getInstance().sendItemTime(pl, 3781, 30);

                            msg = Service.getInstance().messageSubCommand((byte) 5);
                            msg.writer().writeInt((pl.point.getHP()));
                            pl.sendMessage(msg);
                            msg.cleanup();

                        } catch (Exception e) {
                        }
                    }
                }
                break;
            case Skill.THOI_MIEN: //thôi miên
                int timeThoiMien = SkillUtil.getTimeThoiMien(skillSelect.point);
                if (plTarget != null) {
                    try {
                        msg = new Message(-124);
                        msg.writer().writeByte(1); //b5
                        msg.writer().writeByte(0); //b6
                        msg.writer().writeByte(41); //num3
                        msg.writer().writeInt((int) plTarget.id); //num4
                        Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
                        msg.cleanup();
                        Player plAnThoiMien = player.map.getPlayerInMap((int) plTarget.id);
                        plAnThoiMien.playerSkill.setThoiMien(System.currentTimeMillis(), timeThoiMien);
                        Service.getInstance().sendItemTime(plAnThoiMien, 3782, timeThoiMien / 1000);
                    } catch (Exception e) {
                    }
                }
                if (mobTarget != null) {
                    try {
                        msg = new Message(-124);
                        msg.writer().writeByte(1); //b5
                        msg.writer().writeByte(1); //b6
                        msg.writer().writeByte(41); //num6
                        msg.writer().writeByte(mobTarget.id); //b7
                        Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
                        msg.cleanup();
                        mobTarget.setThoiMien(System.currentTimeMillis(), timeThoiMien);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Skill.TROI: //trói
                int timeTroiz = SkillUtil.getTimeTroi(skillSelect.point);
                setUseTroi(System.currentTimeMillis(), timeTroiz);
                sendEffectUseSkill();
                if (plTarget != null) {
                    try {
                        plAnTroi = plTarget;
                        msg = new Message(-124);
                        msg.writer().writeByte(1); //b5
                        msg.writer().writeByte(0);//b6
                        msg.writer().writeByte(32);//num3
                        msg.writer().writeInt((int) plAnTroi.id);//num4
                        msg.writer().writeInt((int) player.id);//num9
                        Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
                        msg.cleanup();
                        plAnTroi.playerSkill.setAnTroi(player, System.currentTimeMillis(), timeTroiz);
                    } catch (Exception e) {
                    }
                }

                if (mobTarget != null) {
                    try {
                        mobAnTroi = mobTarget;
                        msg = new Message(-124);
                        msg.writer().writeByte(1); //b4
                        msg.writer().writeByte(1);//b5
                        msg.writer().writeByte(32);//num8
                        msg.writer().writeByte(mobTarget.id);//b6
                        msg.writer().writeInt((int) player.id);//num9
                        Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
                        msg.cleanup();
                        mobAnTroi.setTroi(System.currentTimeMillis(), timeTroiz);
                    } catch (Exception e) {

                    }
                }
                break;
        }
//        Service.getInstance().lastTimeUseThisSkill(player);
    }

    public boolean isStun = false;
    public long lastTimeStartStun;
    public int timeStun;

    public void startStun(long lastTimeStartBlind, int timeBlind) {
        this.lastTimeStartStun = lastTimeStartBlind;
        this.timeStun = timeBlind;
        isStun = true;
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(1); //b5
            msg.writer().writeByte(0); //b6
            msg.writer().writeByte(40); //num3
            msg.writer().writeInt((int) player.id); //num4
            Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private void removeStun() {
        isStun = false;
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b4
            msg.writer().writeByte(0); //b5
            msg.writer().writeByte(40); //num3
            msg.writer().writeInt((int) player.id); //num4
            Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public boolean isShielding;
    private long lastTimeShieldUp;
    public int timeShield;

    public void shieldDown() {
        isShielding = false;
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b5
            msg.writer().writeByte(0); //b6
            msg.writer().writeByte(33); //num3
            msg.writer().writeInt((int) player.id); //num4
            Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public boolean isMonkey;
    private byte levelMonkey;
    public long lastTimeUpMonkey;
    public int timeMonkey;

    public byte getLevelMonkey() {
        return levelMonkey;
    }

    private void monkeyDown() {
        isMonkey = false;
        levelMonkey = 0;
        Message msg;
        try {
            Skill monkeySkill = null;
            for (Skill skill : skills) {
                if (skill.template.id == 13) {
                    monkeySkill = skill;
                    break;
                }
            }
            msg = new Message(-45);
            msg.writer().writeByte(5);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(monkeySkill.skillId);
            Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();

            msg = new Message(-45);
            msg.writer().writeByte(6);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(monkeySkill.skillId);
            Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();

            msg = new Message(-90);
            msg.writer().writeByte(-1);
            msg.writer().writeInt((int) player.id);
            Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();

            Service.getInstance().Send_Caitrang(player);
            Service.getInstance().sendSpeedPlayer(player, 0);

            Service.getInstance().point(player);

            msg = Service.getInstance().messageSubCommand((byte) 5);
            msg.writer().writeInt(player.point.getHP());
            player.sendMessage(msg);
            msg.cleanup();

        } catch (Exception e) {
        }
    }
    public boolean isCharging;

    private void startCharge() {
        Skill ttnl = getSkillbyId(8);
        Message msg = new Message(-45);
        try {
            msg.writer().writeByte(1);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(ttnl.skillId);
            Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void charge() {
        if (isCharging) {
            return;
        }
        isCharging = true;
        new Thread(() -> {
            Skill ttnl = getSkillbyId(8);
            if (ttnl != null) {
                startCharge();
                for (int i = 0; i < 10; i++) {
                    if (isCharging && (player.point.getHP() < player.point.getHPFull() || player.point.getMP() < player.point.getMPFull())) {
                        player.hoiphuc(Math.round(player.point.getHPFull() / 100) * (3 + ttnl.point), Math.round(player.point.getMPFull() / 100) * (3 + ttnl.point));
                        if (i % 10 == 0) {
                            Service.getInstance().chat(player, "Phục hồi năng lượng " + ((int) ((float) player.point.getHP() / player.point.getHPFull() * 100)) + "%");
                        }
                        Service.getInstance().point(player);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                        }
                    } else {
                        break;
                    }
                }
                stopCharge();
            }
        }).start();
    }

    public void stopCharge() {
        if (!isCharging) {
            return;
        }
        try {
            Message msg = new Message(-45);
            msg.writer().writeByte(3);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(-1);
            Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isCharging = false;
    }

    public int tiLeHPHuytSao;
    public long lastTimeHuytSao;

    public void setHuytSao(int tiLeHP) {
        this.tiLeHPHuytSao = tiLeHP;
        player.point.setHP(player.point.getHP() + (player.point.getHP() * tiLeHP / 100));
        lastTimeHuytSao = System.currentTimeMillis();
    }

    private void removeHuytSao() {
        this.tiLeHPHuytSao = 0;
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b5
            msg.writer().writeByte(0); //b6
            msg.writer().writeByte(39); //num3
            msg.writer().writeInt((int) player.id); //num4
            Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();
            Service.getInstance().point(player);
            Service.getInstance().Send_Info_NV(player);
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
    }

    public void removeThoiMien() {
        this.isThoiMien = false;
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b5
            msg.writer().writeByte(0); //b6
            msg.writer().writeByte(41); //num3
            msg.writer().writeInt((int) player.id); //num4
            Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public boolean useTroi;
    public boolean anTroi;
    public long lastTimeTroi;
    public long lastTimeAnTroi;
    public int timeTroi;
    public int timeAnTroi;
    public Player plTroi;
    public Player plAnTroi;
    public Mob mobAnTroi;

    public void removeUseTroi() {
        if (mobAnTroi != null) {
            mobAnTroi.removeAnTroi();
        }
        if (plAnTroi != null) {
            plAnTroi.playerSkill.removeAnTroi();
        }

        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b4
            msg.writer().writeByte(0); //b5
            msg.writer().writeByte(32);
            msg.writer().writeInt((int) player.id);//b5
            Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();

        } catch (Exception e) {

        }
        this.useTroi = false;
        this.mobAnTroi = null;
        this.plAnTroi = null;
    }

    public void removeAnTroi() {
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0); //b4
            msg.writer().writeByte(0); //b5
            msg.writer().writeByte(32);
            msg.writer().writeInt((int) player.id);//b5
            Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();

        } catch (Exception e) {

        }
        this.anTroi = false;
        this.plTroi = null;
    }

    public void setAnTroi(Player plTroi, long lastTimeAnTroi, int timeAnTroi) {
        this.anTroi = true;
        this.lastTimeAnTroi = lastTimeAnTroi;
        this.timeAnTroi = timeAnTroi;
        this.plTroi = plTroi;
    }

    public void setUseTroi(long lastTimeTroi, int timeTroi) {
        this.useTroi = true;
        this.lastTimeTroi = lastTimeTroi;
        this.timeTroi = timeTroi;
    }

    public boolean isBlindDCTT;
    public long lastTimeBlindDCTT;
    public int timeBlindDCTT;

    public void setBlindDCTT(long lastTimeDCTT, int timeBlindDCTT) {
        this.isBlindDCTT = true;
        this.lastTimeBlindDCTT = lastTimeDCTT;
        this.timeBlindDCTT = timeBlindDCTT;
    }

    public void removeBlindDCTT() {
        this.isBlindDCTT = false;
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            msg.writer().writeByte(40);
            msg.writer().writeInt((int) player.id);
            Service.getInstance().sendMessAllPlayerInMap(player.map, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private boolean nemQCKK;
    private boolean tuSat;
    private boolean laze;

    public boolean isSocola;
    private long lastTimeSocola;
    private int timeSocola;
    public int countPem1hp;

    private void setSocola(long lastTimeSocola, int timeSocola) {
        this.lastTimeSocola = lastTimeSocola;
        this.timeSocola = timeSocola;
        this.isSocola = true;
        countPem1hp = 0;
    }

    private void removeSocola() {
        this.isSocola = false;
        Service.getInstance().Send_Caitrang(player);
    }

    private void sendEffectUseSkill() {
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(8);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(skillSelect.skillId);
            Service.getInstance().sendMessAnotherNotMeInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public boolean isHaveEffectSkill() {
        return isStun || isBlindDCTT || anTroi || isThoiMien;
    }

    public byte getIndexSkillSelect() {
        switch (skillSelect.template.id) {
            case Skill.DRAGON:
            case Skill.DEMON:
            case Skill.GALIK:
            case Skill.KAIOKEN:
            case Skill.LIEN_HOAN:
                return 1;
            case Skill.KAMEJOKO:
            case Skill.ANTOMIC:
            case Skill.MASENKO:
                return 2;
            default:
                return 3;
        }
    }
}
