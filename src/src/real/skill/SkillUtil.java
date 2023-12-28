package real.skill;

import java.util.ArrayList;
import java.util.List;
import real.pet.Pet;
import real.player.Player;
import real.player.PlayerSkill;

public class SkillUtil {

    private final static NClass nClassTD;
    private final static NClass nClassNM;
    private final static NClass nClassXD;

    static {
        nClassTD = SkillData.nClasss[0];
        nClassNM = SkillData.nClasss[1];
        nClassXD = SkillData.nClasss[2];
    }

    public static Skill createSkill(int tempId, int level) {
        Skill skill = null;
        try {
            skill = nClassTD.getSkillTemplate(tempId).skills[level - 1];
        } catch (Exception e) {
            try {
                skill = nClassNM.getSkillTemplate(tempId).skills[level - 1];
            } catch (Exception ex) {
                skill = nClassXD.getSkillTemplate(tempId).skills[level - 1];
            }
        }
        return new Skill(skill);
    }

    public static int getTimeMonkey(int level) { //thời gian tồn tại khỉ
        return  (40+((level-1)*10))*10000;
    }

    public static int getPercentHpMonkey(int level) { //tỉ lệ máu khỉ cộng thêm
        return (level + 3) * 10;
    }

    public static int getTimeStun(int level) { //thời gian choáng thái dương hạ san
        return 45*10000 - level * 10000;
    }

    public static int getTimeShield(int level) { //thời gian tồn tại khiên
        return (15+((level-1)*5))*10000;
    }
    public static int getRangeStun(int level) { //phạm vi thái dương hạ san
        return 120 + level * 30;
    }

    public static int getRangeBom(int level) { //phạm vi tự sát
        if(level == 7){
            return 9999;
        }
        return 350 + level * 30;
    }

    public static int getTimeTroi(int level) { //thời gian trói
        return (level*5) * 100000;
    }

    public static int getTimeDCTT(int level) { //thời gian choáng dịch chuyển tức thời
        return level * 10000;
    }

    public static int getTimeThoiMien(int level) { //thời gian thôi miên
        return (level * 10000);
    }

    public static int getPercentHPHuytSao(int level) { //tỉ lệ máu huýt sáo cộng thêm
        return (13 - (10 - level)) * 10;
    }

    public static int getPercentTriThuong(int level) { //tỉ lệ máu hồi phục trị thương
        return level * 5 + 45;
    }

    public static int getTempMobMe(int level) { //template đẻ trứng
        int[] temp = {8, 11, 32, 25, 43, 49, 50};
        return temp[level - 1];
    }

    public static int getTimeSurviveMobMe(int level) { //thời gian trứng tồn tại
        return level * 30000 + 30000;
    }

    public static int getHPMobMe(int hpMaxPlayer, int level) { //lấy hp max của đệ trứng theo hp max player
        int[] perHPs = {30, 40, 50, 60, 70, 80, 90};
        return hpMaxPlayer * perHPs[level - 1] / 100;
    }

    public static Skill getSkillbyId(Player player, int id) {
        for (Skill skill : player.playerSkill.skills) {
            if (skill.template.id == id) {
                return skill;
            }
        }
        return null;
    }

    public static boolean upSkill(List<Skill> skills, int index) {
        int tempId = skills.get(index).template.id;
        int level = skills.get(index).point + 1;
        if (level > 7) {
            return false;
        }
        Skill skill = null;
        try {
            skill = nClassTD.getSkillTemplate(tempId).skills[level - 1];
        } catch (Exception e) {
            try {
                skill = nClassNM.getSkillTemplate(tempId).skills[level - 1];
            } catch (Exception ex) {
                skill = nClassXD.getSkillTemplate(tempId).skills[level - 1];
            }
        }
        skills.set(index, skill);
        return true;
    }

    public static void sortSkillsPet(Pet pet) {
        List<Skill> list = new ArrayList<Skill>();
        for (int i = 0; i < 4; i++) {
            Skill skill = new Skill();
            skill.skillId = -1;
            list.add(skill);
        }
        for (Skill skill : pet.playerSkill.skills) {
            int temp = skill.template.id;
            if (temp == Skill.DRAGON || temp == Skill.GALIK || temp == Skill.DEMON) {
                list.set(0, skill);
            } else if (temp == Skill.KAMEJOKO || temp == Skill.ANTOMIC || temp == Skill.MASENKO) {
                list.set(1, skill);
            } else if (temp == Skill.KAIOKEN || temp == Skill.THAI_DUONG_HA_SAN || temp == Skill.TAI_TAO_NANG_LUONG) {
                list.set(2, skill);
            } else if (temp == Skill.DE_TRUNG || temp == Skill.BIEN_KHI || temp == Skill.KHIEN_NANG_LUONG) {
                list.set(3, skill);
            }
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).skillId == -1) {
                list.remove(i);
            }
        }
        pet.playerSkill.skills = list;
    }

    public static byte getTempSkillSkillByItemID(int id) {
        if (id >= 66 && id <= 72) {
            return Skill.DRAGON;
        } else if (id >= 79 && id <= 84 || id == 86) {
            return Skill.DEMON;
        } else if (id >= 87 && id <= 93) {
            return Skill.GALIK;
        } else if (id >= 94 && id <= 100) {
            return Skill.KAMEJOKO;
        } else if (id >= 101 && id <= 107) {
            return Skill.MASENKO;
        } else if (id >= 108 && id <= 114) {
            return Skill.ANTOMIC;
        } else if (id >= 115 && id <= 121) {
            return Skill.THAI_DUONG_HA_SAN;
        } else if (id >= 122 && id <= 128) {
            return Skill.TRI_THUONG;
        } else if (id >= 129 && id <= 135) {
            return Skill.TAI_TAO_NANG_LUONG;
        } else if (id >= 300 && id <= 306) {
            return Skill.KAIOKEN;
        } else if (id >= 307 && id <= 313) {
            return Skill.QUA_CAU_KENH_KHI;
        } else if (id >= 314 && id <= 320) {
            return Skill.BIEN_KHI;
        } else if (id >= 321 && id <= 327) {
            return Skill.TU_SAT;
        } else if (id >= 328 && id <= 334) {
            return Skill.MAKANKOSAPPO;
        } else if (id >= 335 && id <= 341) {
            return Skill.DE_TRUNG;
        } else if (id >= 434 && id <= 440) {
            return Skill.KHIEN_NANG_LUONG;
        } else if (id >= 474 && id <= 480) {
            return Skill.SOCOLA;
        } else if (id >= 481 && id <= 487) {
            return Skill.LIEN_HOAN;
        } else if (id >= 488 && id <= 494) {
            return Skill.DICH_CHUYEN_TUC_THOI;
        } else if (id >= 495 && id <= 501) {
            return Skill.THOI_MIEN;
        } else if (id >= 502 && id <= 508) {
            return Skill.TROI;
        } else if (id >= 509 && id <= 515) {
            return Skill.HUYT_SAO;
        } else {
            return -1;
        }
    }

    public static Skill getSkillByItemID(Player pl, int tempId) {
        if (tempId >= 66 && tempId <= 72) {
            return getSkillbyId(pl, Skill.DRAGON);
        } else if (tempId >= 79 && tempId <= 84 || tempId == 86) {
            return getSkillbyId(pl, Skill.DEMON);
        } else if (tempId >= 87 && tempId <= 93) {
            return getSkillbyId(pl, Skill.GALIK);
        } else if (tempId >= 94 && tempId <= 100) {
            return getSkillbyId(pl, Skill.KAMEJOKO);
        } else if (tempId >= 101 && tempId <= 107) {
            return getSkillbyId(pl, Skill.MASENKO);
        } else if (tempId >= 108 && tempId <= 114) {
            return getSkillbyId(pl, Skill.ANTOMIC);
        } else if (tempId >= 115 && tempId <= 121) {
            return getSkillbyId(pl, Skill.THAI_DUONG_HA_SAN);
        } else if (tempId >= 122 && tempId <= 128) {
            return getSkillbyId(pl, Skill.TRI_THUONG);
        } else if (tempId >= 129 && tempId <= 135) {
            return getSkillbyId(pl, Skill.TAI_TAO_NANG_LUONG);
        } else if (tempId >= 300 && tempId <= 306) {
            return getSkillbyId(pl, Skill.KAIOKEN);
        } else if (tempId >= 307 && tempId <= 313) {
            return getSkillbyId(pl, Skill.QUA_CAU_KENH_KHI);
        } else if (tempId >= 314 && tempId <= 320) {
            return getSkillbyId(pl, Skill.BIEN_KHI);
        } else if (tempId >= 321 && tempId <= 327) {
            return getSkillbyId(pl, Skill.TU_SAT);
        } else if (tempId >= 328 && tempId <= 334) {
            return getSkillbyId(pl, Skill.MAKANKOSAPPO);
        } else if (tempId >= 335 && tempId <= 341) {
            return getSkillbyId(pl, Skill.DE_TRUNG);
        } else if (tempId >= 434 && tempId <= 440) {
            return getSkillbyId(pl, Skill.KHIEN_NANG_LUONG);
        } else if (tempId >= 474 && tempId <= 480) {
            return getSkillbyId(pl, Skill.SOCOLA);
        } else if (tempId >= 481 && tempId <= 487) {
            return getSkillbyId(pl, Skill.LIEN_HOAN);
        } else if (tempId >= 488 && tempId <= 494) {
            return getSkillbyId(pl, Skill.DICH_CHUYEN_TUC_THOI);
        } else if (tempId >= 495 && tempId <= 501) {
            return getSkillbyId(pl, Skill.THOI_MIEN);
        } else if (tempId >= 502 && tempId <= 508) {
            return getSkillbyId(pl, Skill.TROI);
        } else if (tempId >= 509 && tempId <= 515) {
            return getSkillbyId(pl, Skill.HUYT_SAO);
        } else {
            return null;
        }
    }

    public static void setSkill(Player pl, Skill skill) {
        for (int i = 0; i < pl.playerSkill.skills.size(); i++) {
            if (pl.playerSkill.skills.get(i).template.id == skill.template.id) {
                pl.playerSkill.skills.set(i, skill);
                break;
            }
        }
    }
}
