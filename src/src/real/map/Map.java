package real.map;

import real.npc.Npc;
import java.util.ArrayList;
import java.util.List;
import real.player.Player;

public class Map implements Runnable {

    public int id;

    public int zoneId;

    public int maxPlayer;

    public String planetName;

    public byte planetId;

    public byte tileId;

    public byte bgId;

    public byte bgType;

    public byte type;

    public String name;

    public ArrayList<Player> players;

    public ArrayList<WayPoint> wayPoints;

    public List<Npc> npcs;

    public ArrayList<Mob> mobs;

    public long timeLoadMob;

    public ArrayList<ItemMap> items;

    private int[][] tileMap;
    public int mapWidth;
    public int mapHeight;
    private static final int SIZE = 24;

    public Map(int _id, int zoneId, int[][] tileMap) {
        this.id = _id;
        this.zoneId = zoneId;
        this.players = new ArrayList<>();
        this.wayPoints = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.mobs = new ArrayList<>();
        this.items = new ArrayList<>();
        this.tileMap = tileMap;
        try {
            this.mapHeight = tileMap.length * SIZE;
            this.mapWidth = tileMap[0].length * SIZE;
        } catch (Exception e) {
        }
        new Thread(this).start();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getPlanetId() {
        return planetId;
    }

    public void setPlanetId(byte planetId) {
        planetName = MapManager.gI().getPlanetName(planetId);
        this.planetId = planetId;
    }

    public byte getBgId() {
        return bgId;
    }

    public void setBgId(byte bgId) {
        this.bgId = bgId;
    }

    public byte getBgType() {
        return bgType;
    }

    public void setBgType(byte bgType) {
        this.bgType = bgType;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getNumPlayerInMap() {
        System.out.println(players.size());
        int count = 0;
        for (Player pl : getPlayers()) {
            if (!pl.isPet && !pl.isBoss) {
                count++;
            }
        }
        return count;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public ArrayList<WayPoint> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(ArrayList<WayPoint> wayPoints) {
        this.wayPoints = wayPoints;
    }

    public List<Npc> getNpcs() {
        return npcs;
    }

    public void setNpcs(ArrayList<Npc> npcs) {
        this.npcs = npcs;
    }

    public ArrayList<Mob> getMobs() {
        return mobs;
    }

    public void setMobs(ArrayList<Mob> mobs) {
        this.mobs = mobs;
    }

    public int getNumItem() {
        return items.size();
    }

    public void addItem(ItemMap itemMap) {
        if (items.size() > 15) {
            ItemMap.removeItemMap(items.get(0));
            items.remove(0);
        }
        items.add(itemMap);
    }

    public void setItems(ArrayList<ItemMap> items) {
        this.items = items;
    }

    public Player getPlayerInMap(int idPlayer) {
        for (Player pl : players) {
            if (pl.id == idPlayer) {
                return pl;
            }
        }
        return null;
    }

    public void update() {
    }

    @Override
    public void run() {
        while (true) {
            long l1 = System.currentTimeMillis();
            try {
                for (Mob mob : mobs) {
                    mob.active();
                }
                for (ItemMap itemMap : items) {
                    itemMap.update();
                }
            } catch (Exception e) {
            }

            long l2 = System.currentTimeMillis() - l1;
            if (l2 < 200) {
                try {
                    Thread.sleep(200 - l2);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public int yPhysic(int x, int y) {
        int rX = (int) x / SIZE;
        int rY = 0;
        if(tileMap[y/SIZE][rX] != 0){
            return y;
        }
        for (int i = y / SIZE; i < tileMap.length; i++) {
            if (tileMap[i][rX] != 0) {
                rY = i * SIZE + SIZE;
                break;
            }
        }
        return rY;
    }

    public boolean isMapSKH() {
        return (this.id == 1 || this.id == 2 || this.id == 3 || this.id == 8 || this.id == 9 || this.id == 10 || this.id == 15 || this.id == 16 || this.id == 17);
    }
}
