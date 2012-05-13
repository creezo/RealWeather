/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author Dodec
 */
public class Utils {
    private List<Material> Mats = RealWinter.Mats;
    
    public void addMats() {
        Mats.add(Material.AIR);
        Mats.add(Material.CROPS);
        Mats.add(Material.SAPLING);
        Mats.add(Material.SNOW);
        Mats.add(Material.STATIONARY_WATER);
        Mats.add(Material.SUGAR_CANE_BLOCK);
        Mats.add(Material.TORCH);
        Mats.add(Material.VINE);
        Mats.add(Material.WATER);
        Mats.add(Material.WATER_LILY);
        Mats.add(Material.WEB);
        Mats.add(Material.RED_ROSE);
        Mats.add(Material.YELLOW_FLOWER);
    }
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
        try {
            player.sendMessage(ChatColor.GOLD + "Commands: /rw stamina, /rw heat, /rw version");
            return true;
        } catch (Exception e) {
            RealWinter.log.log(Level.INFO, "Help message");
            return false;
        }
    }

    public boolean SendAdminHelp(Player player) {
        try {
            player.sendMessage(ChatColor.GOLD + "Commands: /rwadmin enable [plugin-part], /rwadmin disable [plugin-part], /rwadmin version, /rwadmin lang [language]");
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
