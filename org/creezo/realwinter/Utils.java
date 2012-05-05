/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Dodec
 */
public class Utils {
    public boolean SendMessage(Player player, String message) {
        try {
            player.sendMessage(ChatColor.GOLD + "RealWinter: " + message);
            return true;
        } catch (Exception e) {
            RealWinter.log.log(Level.INFO, "[RealWinter] " + message);
            return false;
        }
    }
    
    public boolean SendHelp(Player player) {
        String helpMssg = "";
        try {
            player.sendMessage("Help message");
            return true;
        } catch (Exception e) {
            RealWinter.log.log(Level.INFO, "Help message");
            return false;
        }
    }

    public boolean SendAdminHelp(Player player) {
        String helpMssg = "";
        try {
            player.sendMessage("Admin help message");
            return true;
        } catch (Exception e) {
            RealWinter.log.log(Level.INFO, "Admin help message");
            return false;
        }
    }
    
    public static String ConvertIntToString(int number) {
        return "" + number;
    }
    
    public static String ConvertFloatToString(float number) {
        return "" + number;
    }
}
