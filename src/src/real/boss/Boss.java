package real.boss;

import real.player.Player;
import server.Util;

public abstract class Boss extends Player {

    protected static final byte BOSS_BROLY = 1;
    
    protected static final byte JUST_JOIN_MAP = 0;
    protected static final byte TALK_BEFORE = 1;
    protected static final byte ATTACK = 2;
    protected static final byte IDLE = 3;
    protected static final byte DIE = 4;
    protected static final byte TALK_AFTER = 5;
    protected static final byte LEAVE_MAP = 6;

    protected String[] textTalkBefore;
    protected String[] textTalkMidle;
    protected String[] textTalkAfter;
    protected long lastTimeTalk;
    protected int timeTalk;
    protected byte indexTalk;

    protected byte status;

    protected void setStatus(byte status) {
        this.status = status;
        if (status == TALK_BEFORE || status == TALK_AFTER) {
            indexTalk = 0;
        }
    }

    public Boss() {
        super();
        this.init();
        this.initSkill();
        this.initTalk();
    }

    protected abstract void init();

    protected abstract void initSkill();

    protected abstract void initTalk();

    @Override
    public abstract short getHead();

    @Override
    public abstract short getBody();

    @Override
    public abstract short getLeg();

    @Override
    public abstract void update();

    protected Player getPlayerAttack() {
//        Player pl = this.map.players.get(Util.nextInt(0, this.map.players.size() - 1));
//        if(pl.isDie()){
//            pl = getPlayerAttack();
//        }
//        return pl;
        for(Player pl : this.map.players){
            if(!pl.isPet&&!pl.isDie() &&pl != this && !pl.isBoss){
                return pl;
            }
        }
        return null;
    }

    public void dispose() {
        this.inventory = null;
        this.playerSkill = null;
        this.point = null;
        this.combine = null;
        this.map = null;
    }
}
