package real.map;

import real.npc.Npc;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import server.DBService;

public class MapData {

    public static ArrayList<Map> loadMap() {
        ArrayList<Map> maps = new ArrayList<>();
        Connection conn = DBService.gI().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM map");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int so_luong_khu = rs.getInt(8);
                int id = rs.getInt("id");
                int[][] tileMap = readTileMap(id);
//                for (int i = 0; i <= so_luong_khu; i++) {
                for (int i = 0; i < so_luong_khu; i++) {
                    Map map = new Map(id, i, tileMap);
                    map.name = rs.getString("name");
                    map.type = rs.getByte("type");
                    map.setPlanetId(rs.getByte("planet_id"));
                    map.tileId = rs.getByte("tile_id");
                    map.bgId = rs.getByte("bg_id");
                    map.bgType = rs.getByte("bg_type");
//                    map.maxPlayer = rs.getInt(9);
                    map.maxPlayer = 10;
                    map.npcs = Npc.getByMap(map);
                    map.mobs = loadListMob(map);
                    map.wayPoints = loadListWayPoint(map.id);
                    maps.add(map);
                }
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return maps;
    }

    public static ArrayList<Mob> loadListMob(Map map) {
        ArrayList<Mob> mobs = new ArrayList<>();
        Connection conn = DBService.gI().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM map_mob WHERE map_id=?");
            ps.setInt(1, map.id);
            ResultSet rs = ps.executeQuery();
            int id = 0;
            while (rs.next()) {
                Mob mob = new Mob();
                mob.id = id++;
                mob.map = map;
                mob.tempId = rs.getInt("temp_id");
                mob.level = rs.getByte("level");
                mob.setHpFull(rs.getInt("max_hp"));
                mob.pointX = rs.getShort("point_x");
                mob.pointY = rs.getShort("point_y");
                mob.mindame = rs.getInt("mindame");
                mob.maxdame = rs.getInt("maxdame");
                mob.sethp(mob.getHpFull());
                mob.status = 5;
                mobs.add(mob);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mobs;
    }

    public static ArrayList<WayPoint> loadListWayPoint(int mapId) {
        ArrayList<WayPoint> wayPoints = new ArrayList<>();
        Connection conn = DBService.gI().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM map_waypoint WHERE map_id=?");
            ps.setInt(1, mapId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                WayPoint wp = new WayPoint();
                wp.minX = rs.getShort("min_x");
                wp.minY = rs.getShort("min_y");
                wp.maxX = rs.getShort("max_x");
                wp.maxY = rs.getShort("max_y");
                wp.name = rs.getString("name");
                wp.isEnter = rs.getBoolean("is_enter");
                wp.isOffline = rs.getBoolean("is_offline");
                wp.goMap = rs.getInt("go_map");
                wp.goX = rs.getShort("go_x");
                wp.goY = rs.getShort("go_y");
                wayPoints.add(wp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wayPoints;
    }

    private static int[][] readTileMap(int mapId) {
        int[][] tileMap = null;
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream("data/map/temp/" + mapId));
            dis.readByte();
            int w = dis.readByte();
            int h = dis.readByte();
            tileMap = new int[h][w];
            for (int i = 0; i < tileMap.length; i++) {
                for (int j = 0; j < tileMap[i].length; j++) {
                    tileMap[i][j] = dis.readByte();
                }
            }
            dis.close();
        } catch (Exception e) {
        }
        return tileMap;
    }
}
