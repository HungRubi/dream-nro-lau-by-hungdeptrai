package real.item;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import server.DBService;
import server.Util;

public class ItemTemplateDAO {

    public static List<ItemTemplate> getAll() {
        List<ItemTemplate> list = new ArrayList<>();
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from item_template order by id");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ItemTemplate(rs.getShort(1), rs.getByte(2),
                        rs.getByte(3), rs.getString(4), rs.getString(5), rs.getShort(6), rs.getShort(7), rs.getInt(8) == 1, rs.getInt(9)));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            Util.log("Tải item template thất bại!");
            e.printStackTrace();
            System.exit(0);
        }
        return list;
    }

    public static void insertList(ArrayList<ItemTemplate> list) {
        System.out.println(list.size());
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("insert into item_template values (?,?,?,?,?,?,?,?,?)");
            for (ItemTemplate it : list) {
                ps.setInt(1, it.id);
                ps.setInt(2, it.type);
                ps.setInt(3, it.gender);
                ps.setString(4, it.name);
                ps.setString(5, it.description);
                ps.setInt(6, it.iconID);
                ps.setInt(7, it.part);
                ps.setInt(8, it.isUpToUp ? 1 : 0);
                ps.setInt(9, it.strRequire);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("done");
        } catch (Exception e) {
        }
    }

    public static void readFile() throws FileNotFoundException {
        ArrayList<ItemTemplate> list = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\adm\\Desktop\\nro qltk java by girlkun\\data x1\\items.txt"));
        try {
            while (true) {
                ItemTemplate it = new ItemTemplate();
                for (int i = 0; i < 12; i++) {
                    switch (i) {
                        case 0:
                            it.id = (short) Integer.parseInt(br.readLine().replaceAll("id: ", ""));
                            break;
                        case 1:
                            it.type = (byte) Integer.parseInt(br.readLine().replaceAll("type: ", ""));
                            break;
                        case 2:
                            it.gender = (byte) Integer.parseInt(br.readLine().replaceAll("gender: ", ""));
                            break;
                        case 3:
                            it.name = br.readLine().replaceAll("name: ", "");
                            break;
                        case 4:
                            it.description = br.readLine().replaceAll("description: ", "");
                            break;
                        case 5:
                            it.iconID = (short) Integer.parseInt(br.readLine().replaceAll("iconID: ", ""));
                            break;
                        case 6:
                            it.part = (short) Integer.parseInt(br.readLine().replaceAll("part: ", ""));
                            break;
                        case 7:
                            it.isUpToUp = Boolean.parseBoolean(br.readLine().replaceAll("isuptoup: ", ""));
                            break;
                        case 8:
                            it.strRequire = Integer.parseInt(br.readLine().replaceAll("level: ", ""));
                            break;
                        case 9:
//                            it.gold = 75000;
                            break;
                        case 10:
//                            it.gem = 0;
                            break;
                        default:
                            br.readLine();
                            list.add(it);
                    }
                }
            }
        } catch (Exception e) {
        }
        insertList(list);
    }

    public static void main(String[] args) throws FileNotFoundException {
        readFile();
    }
}
