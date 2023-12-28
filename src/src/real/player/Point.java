package real.player;

import java.io.IOException;
import real.item.Item;
import real.item.ItemOption;
import real.skill.Skill;
import real.skill.SkillUtil;
import server.Service;
import server.Util;
import server.io.Message;

public class Point {

    private Player player;

    public byte limitPower;
    public long power;
    public long tiemNang;

    public int hp;
    public int hpGoc;
    public int mp;
    public int mpGoc;
    public int dameGoc;
    public short defGoc;
    public byte critGoc;

    public boolean isCrit;
    private boolean crit100;

    private void setCrit() {
        if (crit100) {
            crit100 = false;
            isCrit = true;
        } else {
            isCrit = Util.isTrue(getCritFull());
        }
    }

    public Point(Player player) {
        this.player = player;
    }

    public void setPower(long power) {
        this.power = power;
    }

    public long getPower() {
        return this.power;
    }

    public long getTiemNang() {
        return this.tiemNang;
    }

    public void setHP(int hp) {
        this.hp = hp <= getHPFull() ? hp : getHPFull();
    }

    public int getHP() {
        return this.hp <= getHPFull() ? this.hp : getHPFull();
    }
    
    public void setCrit100(){
        this.crit100 = true;
    }

    public int getBaseHPFull() {
        int hpBaseFull = this.hpGoc;
        int skh = 0;
        for (Item it : player.inventory.itemsBody) {
            if (it.id != -1) {
                for (ItemOption iop : it.itemOptions) {
                    switch (iop.optionTemplate.id) {
                        case 2:
                        case 6:
                        case 48:
                            hpBaseFull += iop.param;
                            break;
                        case 22:
                            hpBaseFull += iop.param * 1000;
                            break;
                        case 77:
                            hpBaseFull += hpBaseFull * iop.param / 100;
                            break;
                        case 135:
                            skh++;
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        if (this.player.inventory.getItemTimeId(382) != null) {
            hpBaseFull *= 2;
        }
        if (skh >= 5) {
            hpBaseFull += hpBaseFull * 8 / 10;
        }
        return hpBaseFull;
    }

    public int getHPFull() {
        int hpFull = getBaseHPFull();
        if (player.typeFusion != 0) {
            hpFull += player.pet.point.getBaseHPFull();
        }
        if (player.playerSkill.isMonkey) {
            int percent = SkillUtil.getPercentHpMonkey(player.playerSkill.getLevelMonkey());
            hpFull = hpFull + (hpFull * percent) / 100;
        }
        hpFull += (hpFull * player.playerSkill.tiLeHPHuytSao) / 100;

        if(this.player.isPet != true){
            if(this.player.map.id >= 105 && this.player.map.id <= 110){
                hpFull = (int)(hpFull/2);
            }
        }else{
            if(this.player.map != null){
                if(this.player.map.id >= 105 && this.player.map.id <= 110){
                    hpFull = (int)(hpFull/2);
                }
            }
        }
        return hpFull;
    }

    public void setMP(int mp) {
        this.mp = mp <= getMPFull() ? mp : getMPFull();
    }

    public int getMP() {
        return this.mp <= getMPFull() ? this.mp : getMPFull();
    }

    public int getBaseMPFull() {
        int mpBaseFull = this.mpGoc;
        for (Item it : player.inventory.itemsBody) {
            if (it.id != -1) {
                for (ItemOption iop : it.itemOptions) {
                    switch (iop.optionTemplate.id) {
                        case 2:
                        case 7:
                        case 48:
                            mpBaseFull += iop.param;
                            break;
                        case 23:
                            mpBaseFull += iop.param * 1000;
                            break;
                        case 103:
                            mpBaseFull += mpBaseFull * iop.param / 100;
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        if (this.player.inventory.getItemTimeId(383) != null) {
            mpBaseFull *= 2;
        }
        return mpBaseFull;
    }

    public int getMPFull() {
        int mpFull = getBaseMPFull();
        if (player.typeFusion != 0) {
            mpFull += player.pet.point.getBaseMPFull();
        }
        return mpFull;
    }

    public void powerUp(int power) {
        this.power += power;
    }

    public void tiemNangUp(int tiemNang) {
        this.tiemNang += tiemNang;
    }

    public int getDefFull() {
        int defFull = this.defGoc * 4;
        for (Item it : player.inventory.itemsBody) {
            if (it.id != -1) {
                for (ItemOption iop : it.itemOptions) {
                    if (iop.optionTemplate.id == 47) {
                        defFull += iop.param;
                    }
                }
            }
        };
        return defFull;
    }

    public byte getCritFull() {
        int critFull = this.critGoc;

        for (Item it : player.inventory.itemsBody) {
            if (it.id != -1) {
                for (ItemOption iop : it.itemOptions) {
                    if (iop.optionTemplate.id == 14) {
                        critFull += iop.param;
                    }
                }
            }
        };

        if (player.playerSkill.isMonkey) {
            critFull = 110;
        }
        if (isSKH(127)) {
            critFull += critFull * 8 / 10;
        }
        return (byte) critFull;
    }

    public int getBaseDame() {
        int dameBase = this.dameGoc;
        if (this.player.inventory.getItemTimeId(381) != null) {
            dameBase *= 2;
        }
        for (Item it : player.inventory.itemsBody) {
            if (it.id != -1) {
                for (ItemOption iop : it.itemOptions) {
                    if (iop.optionTemplate.id == 0) {
                        dameBase += iop.param;
                    }
                }
            }
        };
        for (Item it : player.inventory.itemsBody) {
            if (it.id != -1) {
                for (ItemOption iop : it.itemOptions) {
                    if (iop.optionTemplate.id == 49 || iop.optionTemplate.id == 50) {
                        dameBase += (dameBase * iop.param / 100);
                    }
                }
            }
        }
        if (player.typeFusion != 0) {
            dameBase += player.pet.point.getBaseDame();
        }
        
        if(this.player.isPet != true){
            if(this.player.map.id >= 105 && this.player.map.id <= 110){
                dameBase = (int)(dameBase/2);
            }
        }else{
            if(this.player.map != null){
                if(this.player.map.id >= 105 && this.player.map.id <= 110){
                    dameBase = (int)(dameBase/2);
                }
            }
        }
        if(this.player.isBoss == false){
                        if(this.player.inventory.getGiapLuyenTap() != null && this.player.inventory.getGiapLuyenTap().itemOptions.get(0).param > 0){
                            switch ((this.player.inventory.getGiapLuyenTap().template.id)) {
                                case 529:// Giáp luyện tập cấp 1
                                case 534:
                                    dameBase += (int)(dameBase/10);
                                    break;

                                case 530:// Giáp luyện tập cấp 2
                                case 535:
                                    dameBase += (int)(dameBase/20);
                                    break;
                                 case 531:// Giáp luyện tập cấp 3
                                case 536:
                                    dameBase += (int)(dameBase/30);
                                    break;
                            }
                        }
                        }
        return dameBase;
    }
    
    
    public boolean isSKH (int skillId) {
        int dem = 0;
        for (Item it : player.inventory.itemsBody) {
            if (it.id != -1) {
                for (ItemOption iop : it.itemOptions) {
                    if (iop.optionTemplate.id == skillId) {
                        dem++;
                    }
                }
            }
        }
        return dem >= 5;
    }
    public int getDameAttack() {
        if (player.playerSkill.isSocola && player.playerSkill.countPem1hp < 3) {
            player.playerSkill.countPem1hp++;
            return 1;
        }
        setCrit();
        Skill skillSelect = player.playerSkill.skillSelect;
        int percentDameSkill = 0;
        switch (skillSelect.template.id) {
            case Skill.DRAGON:
            case Skill.KAMEJOKO:
            case Skill.GALIK:
            case Skill.ANTOMIC:
            case Skill.DEMON:
            case Skill.MASENKO:
            case Skill.KAIOKEN:
            case Skill.LIEN_HOAN:
                percentDameSkill = skillSelect.damage;
                if (isSKH(129)) {
                    percentDameSkill *= 2;
                }
                else if (isSKH(133)) {
                    percentDameSkill *= 2;
                }
                else if (isSKH(132)) {
                    percentDameSkill *= 2;
                }
                break;
            case Skill.DICH_CHUYEN_TUC_THOI:
                percentDameSkill = 200;
                break;
            case Skill.QUA_CAU_KENH_KHI:
                percentDameSkill = skillSelect.damage;
                if (isSKH(128)) {
                    percentDameSkill *= 2;
                }
                System.err.println("QCKK");
                break;
            case Skill.MAKANKOSAPPO:
                if (isSKH(130)) {
                    return getMP() + (getMP() * 5 / 10);
                }
                return getMP();

        }

        int dameAttack = getBaseDame();
        if (isCrit) {
            dameAttack *= 2;
        }
        if (percentDameSkill != 0) {
            dameAttack = dameAttack * percentDameSkill / 100;
        }
        dameAttack = Util.nextInt(dameAttack - (dameAttack * 10 / 100), dameAttack + (dameAttack * 10 / 100));
        System.err.println("dame: " + dameAttack);

        return dameAttack;
    }

    public byte getSpeed() {
        return 5;
    }

    public void increasePoint(byte type, short point) {
        if (point <= 0) {
            return;
        }
        long tiemNangUse = 0;
        if (type == 0) {
            int pointHp = point * 20;
            tiemNangUse = point * (2 * (this.hpGoc + 1000) + pointHp - 20) / 2;
            if ((this.hpGoc + pointHp) <= getHpMpLimit()) {
                if (useTiemNang(tiemNangUse)) {
                    hpGoc += pointHp;
                }
            } else {
                Service.getInstance().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 1) {
            int pointMp = point * 20;
            tiemNangUse = point * (2 * (this.mpGoc + 1000) + pointMp - 20) / 2;
            if ((this.mpGoc + pointMp) <= getHpMpLimit()) {
                if (useTiemNang(tiemNangUse)) {
                    mpGoc += pointMp;
                }
            } else {
                Service.getInstance().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 2) {
            tiemNangUse = point * (2 * this.dameGoc + point - 1) / 2 * 100;
            if ((this.dameGoc + point) <= getDameLimit()) {
                if (useTiemNang(tiemNangUse)) {
                    dameGoc += point;
                }
            } else {
                Service.getInstance().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 3) {
            tiemNangUse = 2 * (this.defGoc + 5) / 2 * 100000;
            if ((this.defGoc + point) <= getDefLimit()) {
                if (useTiemNang(tiemNangUse)) {
                    defGoc += point;
                }
            } else {
                Service.getInstance().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 4) {
            tiemNangUse = 50000000L;
            for (int i = 0; i < this.critGoc; i++) {
                tiemNangUse *= 5L;
            }
            if ((this.critGoc + point) <= player.point.getCritLimit()) {
                if (useTiemNang(tiemNangUse)) {
                    critGoc += point;
                }
            } else {
                Service.getInstance().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        Service.getInstance().point(player);
    }

    public boolean useTiemNang(long tiemNang) {
        if (this.tiemNang < tiemNang) {
            Service.getInstance().sendThongBaoOK(player, "Bạn không đủ tiềm năng");
            return false;
        }
        if (this.tiemNang >= tiemNang) {
            this.tiemNang -= tiemNang;
            return true;
        }
        return false;
    }
    
    public void setHpMp(int hp, int mp){
        setHP(hp);
        setMP(mp);
    }

    public void updateall() {
        Service.getInstance().point(player);
    }

    //--------------------------------------------------------------------------
    public int getHpMpLimit() {
        if (limitPower == 0) {
            return 220000;
        }
        if (limitPower == 1) {
            return 240000;
        }
        if (limitPower == 2) {
            return 300000;
        }
        if (limitPower == 3) {
            return 350000;
        }
        if (limitPower == 4) {
            return 400000;
        }
        if (limitPower == 5) {
            return 450000;
        }
        return 0;
    }

    public int getDameLimit() {
        if (limitPower == 0) {
            return 11000;
        }
        if (limitPower == 1) {
            return 12000;
        }
        if (limitPower == 2) {
            return 15000;
        }
        if (limitPower == 3) {
            return 18000;
        }
        if (limitPower == 4) {
            return 20000;
        }
        if (limitPower == 5) {
            return 22000;
        }
        return 0;
    }

    public short getDefLimit() {
        if (limitPower == 0) {
            return 550;
        }
        if (limitPower == 1) {
            return 600;
        }
        if (limitPower == 2) {
            return 700;
        }
        if (limitPower == 3) {
            return 800;
        }
        if (limitPower == 4) {
            return 100;
        }
        if (limitPower == 5) {
            return 22000;
        }
        return 0;
    }

    public byte getCritLimit() {
        if (limitPower == 0) {
            return 5 * 20;
        }
        if (limitPower == 1) {
            return 6;
        }
        if (limitPower == 2) {
            return 7;
        }
        if (limitPower == 3) {
            return 8;
        }
        if (limitPower == 4) {
            return 9;
        }
        if (limitPower == 5) {
            return 10;
        }
        return 0;
    }
}
