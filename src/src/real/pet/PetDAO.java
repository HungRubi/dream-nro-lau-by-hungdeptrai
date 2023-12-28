package real.pet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import real.func.Shop;
import real.item.Item;
import real.item.ItemDAO;
import real.player.Player;
import real.player.PlayerSkill;
import real.skill.Skill;
import real.skill.SkillData;
import real.skill.SkillUtil;
import server.DBService;
import server.Service;
import server.Util;

public class PetDAO {

    public static void loadForPlayer(Player pl) {
        Pet pet = null;
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from pet where id = ?");
            ps.setLong(1, -pl.id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                pet = new Pet(pl);
                pet.id = rs.getLong(1);
                pet.name = rs.getString(2);
                pet.point.power = rs.getLong(3);
                pet.gender = (byte) rs.getInt(4);
                pet.map = null;
                byte status = rs.getByte(6);
                if (status == 3) {
                    pet.status = status;
                } else {
                    pet.changeStatus((byte) rs.getInt(6));
                }
                pl.typeFusion = (byte) rs.getInt(7);
                pl.lastTimeFusion = System.currentTimeMillis() - (Player.timeFusion - rs.getInt(8));
                pet.x = pl.x;
                pet.y = pl.y;
                ps = con.prepareStatement("select * from pet_point where pet_id = ?");
                ps.setLong(1, pet.id);
                rs = ps.executeQuery();
                if (rs.next()) {
                    pet.point.hpGoc = rs.getInt(2);
                    pet.point.mpGoc = rs.getInt(3);
                    pet.point.dameGoc = rs.getInt(4);
                    pet.point.defGoc = (short) rs.getInt(5);
                    pet.point.critGoc = (byte) rs.getInt(6);
                    pet.point.tiemNang = rs.getLong(7);
                    pet.point.limitPower = (byte) rs.getInt(8);

                    pet.point.hp = pet.point.hpGoc;
                    pet.point.mp = pet.point.mpGoc;
                }
                ps = con.prepareStatement("select * from pet_skill where pet_id = ?");
                ps.setLong(1, pet.id);
                rs = ps.executeQuery();
                while (rs.next()) {
                    int tempId = rs.getInt(2);
                    int level = rs.getInt(3);
                    Skill skill = SkillUtil.createSkill(tempId, level);
                    int temp = skill.template.id;
                    if (temp == Skill.DRAGON || temp == Skill.GALIK || temp == Skill.DEMON || temp == Skill.KAMEJOKO || temp == Skill.ANTOMIC || temp == Skill.MASENKO) {
                        skill.coolDown = 1000;
                    } else if (temp == Skill.THAI_DUONG_HA_SAN || temp == Skill.TAI_TAO_NANG_LUONG || temp == Skill.DE_TRUNG || temp == Skill.BIEN_KHI || temp == Skill.KHIEN_NANG_LUONG) {
                        skill.coolDown /= 2;
                    } else if (temp == Skill.KAIOKEN) {
                        skill.coolDown = 2500;
                    }
                    pet.playerSkill.skills.add(skill);
                }
                rs.close();
                ps.close();
                ItemDAO.loadPetItems(pet);
                SkillUtil.sortSkillsPet(pet);
                pl.pet = pet;
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void create(Player pl, int gender) {
        try {
            long petID = (pl.id * (-1));
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("insert into pet(id, pet_name, power, gender) values (?,?,?,?)");
            con.setAutoCommit(false);
            ps.setLong(1, petID);
            ps.setString(2, "$Đệ tử");
            ps.setLong(3, 1200);
            ps.setInt(4, gender);
            ps.executeUpdate();

            ps = con.prepareStatement("insert into pet_point values(?,?,?,?,?,?,?,?)");
            ps.setLong(1, petID);
            ps.setInt(2, Util.nextInt(600, 2000));
            ps.setInt(3, Util.nextInt(600, 2000));
            ps.setInt(4, Util.nextInt(20, 120));
            ps.setInt(5, Util.nextInt(70, 100));
            ps.setInt(6, Util.nextInt(0, 2));
            ps.setInt(7, 0);
            ps.setInt(8, 0);
            ps.executeUpdate();

            ps = con.prepareStatement("insert into pet_body values(?,?,?)");
            for (int i = 0; i < 7; i++) {
                ps.setLong(1, petID);
                ps.setObject(2, null);
                ps.setInt(3, i);
                ps.addBatch();
            }
            ps.executeBatch();

            ps = con.prepareStatement("insert into pet_skill values(?,?,?)");
            ps.setLong(1, petID);
            ps.setInt(2, 4);
            ps.setInt(3, 1);
            ps.executeUpdate();
            ps.close();
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean renamePet(Player pl, String name) {
        try {
            int idRenameCard = 400;
            if (!pl.inventory.existItemBag(idRenameCard)) {
                return false;
            }
            pl.inventory.subQuantityItemsBag(pl.inventory.findItemBagByTemp(idRenameCard), 1);
            Connection con = DBService.gI().getConnection();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement("update pet set pet_name = ? where id = ?");
            ps.setString(1, name);
            ps.setLong(2, -pl.id);
            ps.executeUpdate();
            ps.executeBatch();
            con.commit();
            PetDAO.loadForPlayer(pl);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void updateBD(Pet pet) {
        try {
            Connection con = DBService.gI().getConnection();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement("update pet set pet_name = ?, power = ?, gender = ?, status = ?, type_fusion = ?, left_time_fusion = ? where id = ?");
            ps.setString(1, pet.name);
            ps.setLong(2, pet.point.getPower());
            ps.setInt(3, pet.gender);
            ps.setInt(4, pet.getStatus());
            ps.setInt(5, pet.master.typeFusion);
            ps.setInt(6, (int) (Player.timeFusion - (System.currentTimeMillis() - pet.master.lastTimeFusion)));
            ps.setLong(7, pet.id);
            ps.executeUpdate();

            ps = con.prepareStatement("update pet_point set hp_goc = ?, mp_goc = ?, dam_goc = ?, def_goc = ?, crit_goc = ?, tiem_nang = ?, limit_power = ? where pet_id = ?");
            ps.setInt(1, pet.point.hpGoc);
            ps.setInt(2, pet.point.mpGoc);
            ps.setInt(3, pet.point.dameGoc);
            ps.setInt(4, pet.point.defGoc);
            ps.setInt(5, pet.point.critGoc);
            ps.setLong(6, pet.point.tiemNang);
            ps.setInt(7, pet.point.limitPower);
            ps.setLong(8, pet.id);
            ps.executeUpdate();

            ps = con.prepareStatement("update pet_body set item_id = ? where pet_id = ? and slot = ?");
            for (int i = 0; i < pet.inventory.itemsBody.size(); i++) {
                Item item = pet.inventory.itemsBody.get(i);
                if (item.id != -1) {
                    ps.setInt(1, item.id);
                } else {
                    ps.setObject(1, null);
                }
                ps.setLong(2, pet.id);
                ps.setInt(3, i);
                ps.addBatch();
            }
            ps.executeBatch();

            ps = con.prepareStatement("delete from pet_skill where pet_id = ?");
            ps.setInt(1, (int) pet.id);
            ps.executeUpdate();

            ps = con.prepareStatement("insert into pet_skill values(?,?,?)");
            for (Skill skill : pet.playerSkill.skills) {
                ps.setInt(1, (int) pet.id);
                ps.setInt(2, skill.template.id);
                ps.setInt(3, skill.point);
                ps.addBatch();
            }
            ps.executeBatch();
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
