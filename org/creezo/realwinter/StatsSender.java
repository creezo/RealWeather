/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.URL;
import java.util.logging.Level;

/**
 *
 * @author Dodec
 */
public class StatsSender implements Runnable{
    private static RealWinter plugin;
    private static Configuration Config = RealWinter.Config;
    
    public StatsSender(RealWinter plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        if(Config.DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Updating statistics.");
        int NumPlayers = plugin.getServer().getOnlinePlayers().length;
        URLConnection connection;
        try {
            connection = new URL("http://www.dodex-mc.bluefile.cz/RealWinter/stats/echo.php?plrs="+NumPlayers).openConnection();
            connection.setConnectTimeout(5000);
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input = reader.readLine();
            if ((input = reader.readLine()) != null) {
                if("ok".equals(input)) {
                    if(Config.DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Web statistics updated.");
                } else {
                    if(Config.DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Failed to update web statistics.");
                }
            }
            reader.close();
        } catch (MalformedURLException ex) {
            if(Config.DebugMode) plugin.log.log(Level.INFO, ex.getMessage());
        } catch (IOException ex) {
            if(Config.DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Connection failed: " + ex.getMessage());
        } 
    }
}
