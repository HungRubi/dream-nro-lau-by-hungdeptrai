package com.girlkun.tool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import real.item.Item;
import real.item.ItemOption;
import server.DBService;

public class ShopData {

    private static DataOutputStream dos;

    public static void writeShop(String path, int npc, int tab, String tabName, List<Item> itemShops, List<Integer> golds, List<Integer> gems) {
        try {
            dos = new DataOutputStream(new FileOutputStream(new File(path)));
            dos.writeByte(npc);
            dos.writeByte(tab);
            dos.writeUTF(tabName);
            dos.writeInt(itemShops.size());
            int slot = 0;
            for (int i = 0; i < itemShops.size(); i++) {
                dos.writeShort(((Item) itemShops.get(i)).template.id);
                dos.writeByte(slot++);
                dos.writeInt((golds.get(i)));
                dos.writeInt((gems.get(i)));
                dos.writeByte(((Item) itemShops.get(i)).itemOptions.size());
                for (ItemOption io : ((Item) itemShops.get(i)).itemOptions) {
                    dos.writeByte(io.optionTemplate.id);
                    dos.writeShort(io.param);
                }
            }
            dos.flush();
            dos.close();
        } catch (Exception exception) {
        }
    }

    public static void readShopFromFile(String path) {
        try {
            Connection con = DBService.gI().getConnection();
            con.setAutoCommit(false);
            PreparedStatement ps1 = con.prepareStatement("insert into item_shop values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement ps2 = con.prepareStatement("insert into item_option_shop values(?,?,?,?,?)");
            DataInputStream dis = new DataInputStream(new FileInputStream(new File(path)));
            int npc = dis.readByte();
            System.out.println("npc: " + npc);
            int tab = dis.readByte();
            System.out.println("tab: " + tab);
            String tabName = dis.readUTF().replaceAll("\\.", "\n");
            int itemCount = dis.readInt();

            System.out.println("npc: " + npc);
            System.out.println("tab: " + tab);
            System.out.println("tabName: " + tabName);
            System.out.println("itemCount: " + itemCount);
            for (int i = 0; i < itemCount; i++) {
                int tempId = dis.readShort();
                int slot = dis.readByte();
                int gold = dis.readInt();
                int gem = dis.readInt();
                int countOption = dis.readByte();
                System.out.println("tempID: " + tempId);
                System.out.println("slot: " + slot);
                System.out.println("gold: " + gold);
                System.out.println("gem: " + gem);
                System.out.println("countOption: " + countOption);

                PreparedStatement psc = con.prepareStatement("select * from item_shop where npc_id = ? and item_template_id = ? and tab = ?");
                psc.setInt(1, npc);
                psc.setInt(2, tempId);
                psc.setInt(3, tab);
                if (psc.executeQuery().next()) {
                    for (int j = 0; j < countOption; j++) {
                        dis.readByte();
                        dis.readShort();
                    }
                    continue;
                }

                ps1.setInt(1, npc);
                ps1.setInt(2, tempId);
                ps1.setInt(3, 0); //type
                ps1.setInt(4, tab);
                ps1.setString(5, tabName);
                ps1.setInt(6, 1); //item new
                ps1.setInt(7, 1); //sell
                ps1.setInt(8, gold);
                ps1.setInt(9, gem);
                ps1.setInt(10, 1); // quantity
                ps1.setString(11, "");
                ps1.setLong(12, 0);
                ps1.setObject(13, null);
                ps1.setInt(14, 0);
                ps1.setInt(15, 0);
                ps1.setInt(16, -1);
                ps1.setInt(17, -1);
                ps1.setTimestamp(18, new Timestamp(System.currentTimeMillis()));
                ps1.addBatch();

                for (int j = 0; j < countOption; j++) {
                    int optionId = dis.readByte();
                    int param = dis.readShort();
                    System.out.println("optionId: " + optionId);
                    System.out.println("param: " + param);
                    ps2.setInt(1, npc);
                    ps2.setInt(2, tempId);
                    ps2.setInt(3, optionId);
                    ps2.setInt(4, param);
                    ps2.setInt(5, tab);
                    ps2.addBatch();
                }
            }
            dis.close();
            ps1.executeBatch();
            ps2.executeBatch();
            con.commit();
            con.close();
            System.out.println("done");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void readShopFromDataTeam(String path) {
        try {
            Connection con = DBService.gI().getConnection();
            con.setAutoCommit(false);
            PreparedStatement ps1 = con.prepareStatement("insert into item_shop values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement ps2 = con.prepareStatement("insert into item_option_shop values(?,?,?,?,?)");
            File file = new File(path);
            DataInputStream dis = new DataInputStream(new FileInputStream(file));

            int npc = Integer.parseInt(file.getName().split("_")[0]);
            System.out.println("npc: " + npc);
            byte type = dis.readByte(); //type
            int countTab = dis.readByte();
            for (int tab = 0; tab < countTab; tab++) {
                String tabName = dis.readUTF().replaceAll("\\.", "\n");
                int itemCount = dis.readByte();

                for (int i = 0; i < itemCount; i++) {
                    int tempId = dis.readShort();
                    if (tempId == -1) {
                        continue;
                    }

                    int slot = i;
                    int gold = dis.readInt();
                    int gem = dis.readInt();
//                    System.out.println("gold: " + gold + " gem: " + gem);

                    int countOption = dis.readByte();

                    PreparedStatement psc = con.prepareStatement("select * from item_shop where npc_id = ? and item_template_id = ? and tab = ?");
                    psc.setInt(1, npc);
                    psc.setInt(2, tempId);
                    psc.setInt(3, tab);
                    if (psc.executeQuery().next() || tempId == 297|| tempId == 299) {
                        for (int j = 0; j < countOption; j++) {
                            dis.readByte();
                            dis.readShort();

                        }
                        dis.readByte();
                        byte isCT = dis.readByte();
                        if (isCT == 1) {
                            dis.readShort();
                            dis.readShort();
                            dis.readShort();
                            dis.readShort();
                        }
                        continue;
                    }

                    ps1.setInt(1, npc);
                    ps1.setInt(2, tempId);
                    ps1.setInt(3, type); //type
                    ps1.setInt(4, tab);
                    ps1.setString(5, tabName);
                    ps1.setInt(6, 1); //item new
                    ps1.setInt(7, 1); //sell
                    ps1.setInt(8, gold);
                    ps1.setInt(9, gem);
                    ps1.setInt(10, 1); // quantity
                    ps1.setString(11, "");
                    ps1.setLong(12, 0);
                    ps1.setObject(13, null);
                    ps1.setInt(14, 0);
                    ps1.setInt(15, 0);
                    ps1.setInt(16, -1);
                    ps1.setInt(17, -1);
                    ps1.setTimestamp(18, new Timestamp(System.currentTimeMillis() - i * 1000));
                    ps1.addBatch();
                    for (int j = 0; j < countOption; j++) {
                        int optionId = dis.readByte();
                        if (optionId < 0) {
                            optionId += 256;
                        }
                        int param = dis.readShort();

                        ps2.setInt(1, npc);
                        ps2.setInt(2, tempId);
                        ps2.setInt(3, optionId);
                        ps2.setInt(4, param);
                        ps2.setInt(5, tab);
                        ps2.addBatch();
                    }

                    byte itemNew = dis.readByte();
                    byte isCT = dis.readByte();
                    if (isCT == 1) {
                        dis.readShort();
                        dis.readShort();
                        dis.readShort();
                        dis.readShort();
                    }
                }
            }
            dis.close();
            ps1.executeBatch();
            ps2.executeBatch();
            con.commit();
            con.close();
            System.out.println("done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        readShopFromFile("C:\\Users\\adm\\Desktop\\yardrat");
        
//        readShopFromDataTeam("C:\\Users\\adm\\Desktop\\data nro\\shopteam\\7_0");
//        readShopFromDataTeam("C:\\Users\\adm\\Desktop\\data nro\\shopteam\\8_1");
//        readShopFromDataTeam("C:\\Users\\adm\\Desktop\\data nro\\shopteam\\9_2");
//        readShopFromDataTeam("C:\\Users\\adm\\Desktop\\data nro\\shopteam\\16_0");
//        readShopFromDataTeam("C:\\Users\\adm\\Desktop\\data nro\\shopteam\\16_1");
//        readShopFromDataTeam("C:\\Users\\adm\\Desktop\\data nro\\shopteam\\16_2");
//        readShopFromDataTeam("C:\\Users\\adm\\Desktop\\data nro\\shopteam\\39_0");
//        readShopFromDataTeam("C:\\Users\\adm\\Desktop\\data nro\\shopteam\\39_1");
//        readShopFromDataTeam("C:\\Users\\adm\\Desktop\\data nro\\shopteam\\39_2");
    }

}
