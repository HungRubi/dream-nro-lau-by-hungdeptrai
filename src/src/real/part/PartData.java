package real.part;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import server.DBService;
import server.FileIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PartData {
    public static Part[] parts;

    public static void loadPart() {
        Connection conn = DBService.gI().getConnection();
        try {
            System.out.println("Load Part");
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM nr_part");
            ResultSet rs = ps.executeQuery();
            if (rs.last()) {
                parts = new Part[rs.getRow()];
                rs.beforeFirst();
            }
            int i = 0;
            while (rs.next()) {
                byte type = rs.getByte("type");
                JSONArray jA = (JSONArray) JSONValue.parse(rs.getString("part"));
                Part part = new Part(type);
                for (int k = 0; k < part.pi.length; k++) {
                    JSONObject o = (JSONObject) jA.get(k);
                    part.pi[k] = new PartImage();
                    part.pi[k].id = ((Long) o.get("id")).shortValue();
                    part.pi[k].dx = ((Long) o.get("dx")).byteValue();
                    part.pi[k].dy = ((Long) o.get("dy")).byteValue();
                }
                parts[i] = part;
                ++i;
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        createCachePart();
    }
    public static void createCachePart() {
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bas);
            dos.writeShort(parts.length);
            for (short i = 0; i < parts.length; ++i) {
                dos.writeByte(parts[i].type);
                for (short j = 0; j < parts[i].pi.length; ++j) {
                    dos.writeShort(parts[i].pi[j].id);
                    dos.writeByte(parts[i].pi[j].dx);
                    dos.writeByte(parts[i].pi[j].dy);
                }
            }

            byte[] ab = bas.toByteArray();
            FileIO.writeFile("data/NR_part", ab);
            System.out.println("------ Tạo Cache Part ------");
            dos.close();
            bas.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
