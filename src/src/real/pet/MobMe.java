package real.pet;

import real.map.Map;
import real.map.Mob;
import real.player.Player;
import real.skill.SkillUtil;
import server.Service;
import server.Util;
import server.io.Message;

public final class MobMe extends Mob {

    private final Player player;
    private final long lastTimeSpawn;
    private final int timeSurvive;
    

    public MobMe(Player player) {
        this.player = player;
        this.id = (int) player.id;
        int level = player.playerSkill.getSkillbyId(12).point;
        this.tempId = SkillUtil.getTempMobMe(level);
        this.maxHp = SkillUtil.getHPMobMe(player.point.getHPFull(), level);
        this.dame = SkillUtil.getHPMobMe(player.point.getDameAttack(), level);
        this.hp = this.maxHp;
        this.map = player.map;
        this.lastTimeSpawn = System.currentTimeMillis();
        this.timeSurvive = SkillUtil.getTimeSurviveMobMe(level);
        spawn();
        this.active();
    }

    @Override
    public void update() {
        if(Util.canDoWithTime(lastTimeSpawn, timeSurvive)){
            this.mobMeDie();
        }
    }

    public void attack(Player pl, Mob mob) {
        Message msg;
        try {
            if (pl != null) {
                int dameHit = pl.injured(null,this.dame,true);
                System.out.println("dame hit mob me: " + dameHit);
                if (pl.point.getHP() > dameHit) {
                    msg = new Message(-95);
                    msg.writer().writeByte(2);
                    
                    msg.writer().writeInt(this.id);
                    msg.writer().writeInt((int) pl.id);
                    msg.writer().writeInt(dameHit);
                    msg.writer().writeInt(pl.point.getHP());
                    
                    Service.getInstance().sendMessAllPlayerInMap(map, msg);
                    msg.cleanup();
                }
            }

            if (mob != null) {
                if (mob.gethp() > this.dame) {
                    msg = new Message(-95);
                    msg.writer().writeByte(3);
                    msg.writer().writeInt(this.id);
                    msg.writer().writeInt((int) mob.id);
                    mob.sethp(mob.gethp() - this.dame);
                    msg.writer().writeInt(mob.gethp());
                    msg.writer().writeInt(this.dame);
                    Service.getInstance().sendMessAllPlayerInMap(map, msg);
                    msg.cleanup();
                    Service.getInstance().congTiemNang(player, (byte)2, 2000);
                }
            }
        } catch (Exception e) {
        }
    }

    public void spawn() {
        Message msg;
        try {
            System.out.println("38");
            msg = new Message(-95);
            msg.writer().writeByte(0);//type
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(this.tempId);
            msg.writer().writeInt(this.hp);// hp mob
            Service.getInstance().sendMessAllPlayerInMap(map, msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void goToMap(Map map) {
        if (map != null) {
            this.removeMobInMap();
            this.map = map;
        }
    }

    public void removeMobInMap() {
        Message msg;
        try {
            msg = new Message(-95);
            msg.writer().writeByte(7);//type
            msg.writer().writeInt((int) player.id);
            Service.getInstance().sendMessAllPlayerInMap(this.map, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void mobMeDie(){
        Message msg;
        try {
            msg = new Message(-95);
            msg.writer().writeByte(6);//type
            msg.writer().writeInt((int) player.id);
            Service.getInstance().sendMessAllPlayerInMap(this.map, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
        this.timer.cancel();
        player.mobMe = null;
    }
}
