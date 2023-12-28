package real.boss;

import java.util.ArrayList;
import real.func.ChangeMap;
import real.map.Map;
import real.map.MapManager;
import real.pet.PetDAO;
import real.player.Player;
import real.player.PlayerManger;
import real.skill.Skill;
import real.skill.SkillUtil;
import server.Service;
import server.Util;

public class Broly extends Boss {

    public static final int[] MAP_JOIN = {19,20,38,37,36,13,33,32,12,10,5,29,6,28,30,27,34};
   
    public static int idb = -1;
     public static int brolyid = 1;
    
    public Broly() {
        super();
        this.active(1000, 1);
    }

    @Override
    public short getHead() {
        return 294;
    }

    @Override
    public short getBody() {
        return 295;
    }

    @Override
    public short getLeg() {
        return 296;
    }

    @Override
    public void update() {
        try {
            if (this.isDie() && this.status < DIE) {
                setStatus(DIE);
            }
            switch (status) {
                case JUST_JOIN_MAP:
                    joinMap();
                    setStatus(TALK_BEFORE);
                    break;
                case TALK_BEFORE:
                    if (indexTalk > textTalkBefore.length - 1) {
                        Service.getInstance().changeTypePK(this, Player.PK_ALL);
                        setStatus(ATTACK);
                    } else {
                        if (Util.canDoWithTime(lastTimeTalk, timeTalk)) {
                            Service.getInstance().chat(this, textTalkBefore[indexTalk]);
                            timeTalk = textTalkBefore[indexTalk].length() * 100;
                            lastTimeTalk = System.currentTimeMillis();
                            indexTalk++;
                        }
                    }
                    break;
                case ATTACK:
                    if(this.point.getHP() > 0){
                    Player pl = getPlayerAttack();
                    if (pl != null && pl != this && !pl.isBoss) {
                        int dis = Util.getDistance(this, pl);
                        if (dis > 100) {
                            byte dir = (byte) (this.x - pl.x < 0 ? 1 : -1);
                            byte move = (byte) Util.nextInt(50, 100);
                            move(this.x + (dir == 1 ? move : -move), pl.y);
                        } else {
                            this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                            this.playerSkill.useSkill(pl, null);
                        }

                    }
                    }
                    break;
                case IDLE:

                    break;
                case DIE:
                    setStatus(TALK_AFTER);
                    if(this.endHit.pet == null){
                        createPET(this.endHit);
                        Service.getInstance().sendThongBao(this.endHit, "Chúc mừng cưng đã nhận được đệ tử");
                    }
                    break;
                case TALK_AFTER:
                    if (indexTalk > textTalkAfter.length - 1) {
                        setStatus(LEAVE_MAP);
                    } else {
                        if (Util.canDoWithTime(lastTimeTalk, timeTalk)) {
                            Service.getInstance().chat(this, textTalkAfter[indexTalk]);
                            timeTalk = textTalkAfter[indexTalk].length() * 100;
                            lastTimeTalk = System.currentTimeMillis();
                            indexTalk++;
                        }
                    }
                    break;
                case LEAVE_MAP:
                    ChangeMap.gI().spaceShipArrive(this, ChangeMap.TENNIS_SPACE_SHIP);
                    //MAP_DA_JOIN_BROLY.remove(this.map);
                    
                    this.exitMap();
                    this.timer.cancel();
                    this.dispose();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void createPET(Player pl) {
        if (pl.pet != null) {
        PetDAO.create(pl, Util.nextInt(0, 2));
        PetDAO.loadForPlayer(pl);
        sendMeHavePet();
            PlayerManger.gI().getPlayers().add(pl.pet);
            pl.pet.point.updateall();
            pl.pet.active(100, 100);
        }
    }
    public void joinMap() {
        ChangeMap.gI().changeMapBySpaceShip(this, this.map, ChangeMap.TENNIS_SPACE_SHIP);
        Util.log("Broly đã xuất hiện tại" +this.map.name+"' khu "+this.map.zoneId);
    }

    public boolean getBossInMap(int idmap){
        try{
            for(Map m : MapManager.gI().getMapBroly()){
                if(m.id == idmap){
                    return true;
                }
            }
        }catch(Exception e){
            
        }
        return false;
    }
    
    @Override
    protected void initSkill() {
//        byte[][] skillTemp = {{3, 1}, {3, 5}, {3, 7}, {1, 7}, {4, 6}, {5, 3}};
        byte[][] skillTemp = {{1, 7}};
        for (int i = 0; i < skillTemp.length; i++) {
            Skill skill = SkillUtil.createSkill(skillTemp[i][0], skillTemp[i][1]);
            skill.coolDown = 10;
            this.playerSkill.skills.add(skill);
        }
    }
    
    
    
    @Override
    protected void init() {
        int mapJoin = 0;
        for(int idmap : MAP_JOIN){
            if(getBossInMap(idmap) == false){
                    mapJoin = idmap;
                break;
            }
        }
         if(getBossInMap(mapJoin) == false  && mapJoin != 0){
                this.isBoss = true;
                this.typeBoss = 1;
                this.status = JUST_JOIN_MAP;
                this.id = idb;
                idb--;
                this.name = "Super Broly "+brolyid;
                brolyid++;
                this.gender = 2;
                this.point.power = 1000000L;
                this.point.dameGoc = 10000;
                this.point.hpGoc = 60000000;
                this.point.mpGoc = 60000000;
                this.point.hp = this.point.hpGoc;
                this.point.mp =  this.point.mpGoc;
                int khu = (Util.nextInt(0, MapManager.gI().getMapById(mapJoin).size() -1));
                this.map = MapManager.gI().getMap(mapJoin, khu);
                MapManager.gI().addMapBroly(this.map);
                Service.getInstance().sendThongBaoBenDuoi("Super Broly vừa xuất hiện tại "+this.map.name+" khu "+khu );
                }
          }

    @Override
    protected void initTalk() {
        this.textTalkMidle = new String[] {"Haha bọn mì yếu vậy haha","ui ui đau ghê","úi úi các ngươi đước lắm"};
        this.textTalkBefore = new String[]{"Hello mấy cưng!",
            "Hôm nay ta tới đây để hủy diệt cái hành tinh này!...",
            "Ý kiến mấy cưng thế nào?"};
        this.textTalkAfter = new String[]{"Được lắm các ngươi!", "Hãy nhớ mặt ta đấy...", "... ta sẽ quay lại vào thời gian tới!","Bọn nhóc con","Những tên cư dân NRO Hồi Ức :))"};
    }

    @Override
    public void dispose() {
        super.dispose();
    }

}
