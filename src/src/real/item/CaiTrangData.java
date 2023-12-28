package real.item;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import server.DBService;
import server.Util;

public class CaiTrangData {

    private static List<CaiTrang> caiTrangs;
    public static List<Avatar> avatars;

    public static CaiTrang getCaiTrangByTempID(int tempId) {
        for (CaiTrang ct : caiTrangs) {
            if (ct.tempId == tempId) {
                return ct;
            }
        }
        return null;
    }

    public static void loadCaiTrang() {
        caiTrangs = new ArrayList<>();
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from cai_trang");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                caiTrangs.add(new CaiTrang(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5)));
            }
            ps.close();
            con.close();
            Util.log("Load cải trang thành công! (" + caiTrangs.size() + ")");
        } catch (Exception e) {
            Util.log("Lỗi load cải trang");
        }
    }

    public static class CaiTrang {

        private int tempId;
        private int[] id;

        private CaiTrang(int tempId, int... id) {
            this.tempId = tempId;
            this.id = id;
        }

        public int[] getID() {
            return id;
        }
    }

    public static class Avatar {

        private short idHead;
        private short idAvatar;

        private Avatar(short idHead, short idAvatar) {
            this.idHead = idHead;
            this.idAvatar = idAvatar;
        }

        public short getidHead() {
            return idHead;
        }

        public short getidAvatar() {
            return idAvatar;
        }

    }

    public static void loadAvatar() {
        avatars = new ArrayList<>();
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("select * from nr_head");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                avatars.add(new Avatar(rs.getShort(2), rs.getShort(3)));
            }
            ps.close();
            con.close();
            Util.log("Load Avatar thành công! (" + avatars.size() + ")");
        } catch (Exception e) {
            Util.log("Lỗi load Avatar");
        }
    }

    public static void main(String[] args) {
        try {
            Connection con = DBService.gI().getConnection();
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement("insert into cai_trang values(?,?,?,?,?)");
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\adm\\Desktop\\data nro\\caitrang.txt"));
            String line = "";
            while ((line = br.readLine()) != null) {
                Util.log(line);
                String[] data = line.split(" ");
                ps.setInt(1, Integer.parseInt(data[0]));
                ps.setInt(2, Integer.parseInt(data[1]));
                ps.setInt(3, Integer.parseInt(data[2]));
                ps.setInt(4, Integer.parseInt(data[3]));
                ps.setInt(5, -1);
                ps.addBatch();
            }
            ps.executeBatch();
            con.commit();
            con.close();
            System.out.println("done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
