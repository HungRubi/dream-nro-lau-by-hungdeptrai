/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package real.map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import real.item.ItemOptionShop;
import server.DBService;

/**
 *
 * @author phong
 */
public class Mob_ItemDAO {

    public static int[][] itemList_RoiDo_idItem = new int[3][88];
    public static int[][] itemList_RoiDo_Optione = new int[3][150];
    public static int[][] itemList_RoiDo_maxValue = new int[3][160];

    public static int[][] itemList_SKH_idItem = new int[3][5];
    public static int[][] itemList_SKH_Optione = new int[3][5];
    public static int[][] itemList_SKH_maxValue = new int[3][5];

    public static int[][] itemList_SKH_Optione_Name = new int[3][3];

    public static int[][] itemList_SKH_Optione_Value = new int[3][3];

    static {
        itemList_SKH_idItem[0] = new int[]{0, 6, 12, 21, 27};
        itemList_SKH_Optione[0] = new int[] {47, 6, 14, 0, 7};
        itemList_SKH_maxValue[0] = new int[] {2, 30, 1, 4, 10};

        itemList_SKH_idItem[1] = new int[]{1, 7, 12, 22, 28};
        itemList_SKH_Optione[1] = new int[] {47, 6, 14, 0, 7};
        itemList_SKH_maxValue[1] = new int[] {2, 20, 1, 3, 15};

        itemList_SKH_idItem[2] = new int[]{2, 8, 12, 23, 29};
        itemList_SKH_Optione[2] = new int[] {47, 6, 14, 0, 7};
        itemList_SKH_maxValue[2] = new int[] {3, 20, 1, 5, 10};

        itemList_SKH_Optione_Name[0] = new int[] {127, 128, 129};
        itemList_SKH_Optione_Value[0] = new int[] {139, 140, 141};

        itemList_SKH_Optione_Name[1] = new int[] {130, 131, 132};
        itemList_SKH_Optione_Value[1] = new int[] {142, 143, 144};

        itemList_SKH_Optione_Name[2] = new int[] {133, 134, 135};
        itemList_SKH_Optione_Value[2] = new int[] {136, 137, 138};
    }
    public static void getItemRoi() {
        getItemDB(0);
        getItemDB(1);
        getItemDB(2);
        System.out.println("Load itemRoi done");

    }
    public static void getItemDB(int gender) {
        try {
            Connection con = DBService.gI().getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT `item_option_shop`.* FROM `item_option_shop` WHERE (`item_option_shop`.`npc_id` = ?) AND (`tab` = 0 OR `tab` = 1);");
            ps.setInt(1, (gender+7));
            ResultSet rs = ps.executeQuery();
            int loop = 0;
            while (rs.next()) {
                int idItem = rs.getInt(2);
                int Options = rs.getInt(3);
                int maxValue = rs.getInt(4);
                itemList_RoiDo_idItem[gender][loop] = idItem;
                itemList_RoiDo_Optione[gender][loop] = Options;
                itemList_RoiDo_maxValue[gender][loop] = maxValue;
//                System.out.println("itemList_RoiDo_idItem["+gender+"]["+loop+"] = "+ itemList_RoiDo_idItem[gender][loop]);
                loop++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
