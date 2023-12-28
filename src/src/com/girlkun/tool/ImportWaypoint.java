package com.girlkun.tool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import server.DBService;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN  💖
 *
 */
public class ImportWaypoint {
    public static void main(String[] args) throws Exception{
        Connection con = DBService.gI().getConnection();
        PreparedStatement ps = con.prepareStatement("delete from map_waypoint");
        ps.executeUpdate();
        ps = con.prepareStatement("insert into map_waypoint values(?,?,?,?,?,?,?,?,?,?,?)");
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\adm\\Desktop\\nro qltk java by girlkun\\data_log\\waypoint.txt"));
        String line;
        while((line = br.readLine()) != null){
            String[] data = line.split("\\|");
            ps.setInt(1, Integer.parseInt(data[0]));
            ps.setString(2, data[1]);
            ps.setInt(3, Integer.parseInt(data[2]));
            ps.setInt(4, Integer.parseInt(data[3]));
            ps.setInt(5, Integer.parseInt(data[4]));
            ps.setInt(6, Integer.parseInt(data[5]));
            ps.setInt(7, Integer.parseInt(data[6]));
            ps.setInt(8, Integer.parseInt(data[7]));
            ps.setInt(9, Integer.parseInt(data[8]));
            ps.setInt(10, Integer.parseInt(data[9]));
            ps.setInt(11, Integer.parseInt(data[10]));
            ps.addBatch();
        }
        ps.executeBatch();
        con.close();
    }
}
