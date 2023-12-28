package real.map;

import java.util.ArrayList;
import java.util.List;
import real.player.Player;
import server.Util;
import server.io.*;

public class MapManager implements Runnable {

//    public Session _session;
    private static MapManager instance;

    private ArrayList<Map> maps;
    private ArrayList<Map> mapBoss;
    
    public MapManager() {
        this.maps = new ArrayList<>();
        this.mapBoss = new ArrayList<>();
        new Thread(this).start();
    }

    public static MapManager gI() {
        if (instance == null) {
            instance = new MapManager();
        }
        return instance;
    }

    public void init() {
        Util.log("load map");
        maps = MapData.loadMap();
        Util.log("finish load map: " + maps.size());
    }

    public List<Map> getMapBroly() {
            return mapBoss;
     }
    
    public void  addMapBroly(Map m) {
            mapBoss.add(m);
     }
    
    
    public List<Map> getMapById(int id) {
        List<Map> list = new ArrayList<>();
        for (Map map : maps) {
            if (map.id == id) {
                list.add(map);
            }
        }
        return list;
    }

    public Map getMap(int mapId, int zoneId) {
        for (Map map : maps) {
            if (map.id == mapId && map.zoneId == zoneId) {
                return map;
            }
        }
        return null;
    }

    public Map getMapWithRandZone(int mapId) {
        List<Map> list = new ArrayList<>();

        for (Map map : maps) {
            if (map.id == mapId) {
                list.add(map);
            }
        }
        return list.get(Util.nextInt(0, list.size() - 1));
    }

    public String getPlanetName(byte planetId) {
        switch (planetId) {
            case 0:
                return "Trái đất";
            case 1:
                return "Namếc";
            case 2:
                return "Xayda";
            default:
                return "";
        }
    }

    public List<Map> getMapCapsule(Player pl) {
        List<Map> list = new ArrayList<>();
        if (pl.mapBeforeCapsuleId != -1 && pl.zoneBeforeCapsuleId != -1
                && pl.mapBeforeCapsuleId != 21
                && pl.mapBeforeCapsuleId != 22
                && pl.mapBeforeCapsuleId != 23) {
            addListMapCapsule(pl, list, getMap(pl.mapBeforeCapsuleId, 0));
        }
        addListMapCapsule(pl, list, getMap(21 + pl.gender, 0));
        addListMapCapsule(pl, list, getMap(0, 0));
        addListMapCapsule(pl, list, getMap(7, 0));
        addListMapCapsule(pl, list, getMap(14, 0));
        addListMapCapsule(pl, list, getMap(5, 0));
        addListMapCapsule(pl, list, getMap(13, 0));
        addListMapCapsule(pl, list, getMap(20, 0));
        addListMapCapsule(pl, list, getMap(24 + pl.gender, 0));
        addListMapCapsule(pl, list, getMap(27, 0));
        addListMapCapsule(pl, list, getMap(19, 0));
        addListMapCapsule(pl, list, getMap(79, 0));
        addListMapCapsule(pl, list, getMap(84, 0));
        return list;
    }

    private void addListMapCapsule(Player pl, List<Map> list, Map map) {
        for (Map m : list) {
            if (m.id == map.id) {
                return;
            }
        }
        if (pl.map.id != map.id) {
            list.add(map);
        }
    }

    @Override
    public void run() {
        while (true) {
            long l1 = System.currentTimeMillis();
            for (Map map : maps) {
                map.update();
                try {
//                    MapService.gI().loadPlayers(_session, map);
                } catch (Exception e) {

                }
            }

            long l2 = System.currentTimeMillis() - l1;
            if (l2 < 1000) {
                try {
                    Thread.sleep(1000 - l2);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
