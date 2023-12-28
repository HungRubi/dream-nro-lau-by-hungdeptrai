/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import real.pet.Pet;
import real.player.Player;
import server.DBService;

/**
 *
 * @author phong
 */
public class ServiceDAO {

    public static boolean logGlobalChat(Player pl, String chat) {
        Connection conn = DBService.gI().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `global_chat` (`id`, `owner`, `content`, `time_created`, `server`) "
                    + "VALUES (NULL, ?, ?, ?, '1');");
            ps.setLong(1, pl.id);
            ps.setString(2, chat);
            ps.setLong(3, System.currentTimeMillis());
            if (ps.executeUpdate() == 1) {
                conn.close();
                ps.close();
                return true;
            } else {
                conn.close();
                ps.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
