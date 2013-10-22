package org.creezo.realweather.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.creezo.realweather.RealWeather;

/**
 *
 * @author Dodec
 */
public class Utils {
    private final RealWeather plugin;
    public Utils(RealWeather plugin) {
        this.plugin = plugin;
    }
    public boolean sendMessage(Player player, String message) {
        try {
            player.sendMessage(ChatColor.GOLD + "RealWeather: " + message);
            return true;
        } catch (Exception e) {
            RealWeather.log("" + message);
            return false;
        }
    }
    
    public boolean sendHelp(Player player) {
        try {
            player.sendMessage(ChatColor.GOLD + "Commands: /rw stamina, /rw temp, /rw forecast, /rw version");
            return true;
        } catch (Exception e) {
            RealWeather.log("Help message --- see help in game console");
            return false;
        }
    }

    public boolean sendAdminHelp(Player player) {
        try {
            player.sendMessage(ChatColor.GOLD + "Commands: /rwadmin enable [plugin-part], /rwadmin disable [plugin-part], /rwadmin save, /rwadmin load /rwadmin version, /rwadmin lang [language], /rwadmin debug");
            return true;
        } catch (Exception e) {
            RealWeather.log("Commands: /rwadmin enable [plugin-part], /rwadmin disable [plugin-part], /rwadmin save, /rwadmin load /rwadmin version, /rwadmin lang [language], /rwadmin debug");
            return false;
        }
    }
    
    public static String convertIntToString(int number) {
        return "" + number;
    }
    
    public static String convertFloatToString(float number) {
        return "" + number;
    }
    
    public static void copy(InputStream in, File file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
        } catch (Exception e) {
            RealWeather.log.log(Level.WARNING, null, e);
        } finally {
            try {
                out.close();
            } catch (Exception ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                in.close();
            } catch (Exception ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    }
}
