package org.creezo.realwinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.URL;

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
        if(Config.getVariables().isDebugMode()) plugin.log("Updating statistics.");
        if(Config.getVariables().getStatistics().getPublic()) {
            int NumPlayers = plugin.getServer().getOnlinePlayers().length;
            int MaxPlayers = plugin.getServer().getMaxPlayers();
            int ServerPort = plugin.getServer().getPort();
            String Version = plugin.getDescription().getVersion();
            String ServerName = Config.getVariables().getStatistics().getServerName();
            String Comment = Config.getVariables().getStatistics().getComment();
            URLConnection connection;
            try {
                connection = new URL("http://www.dodex-mc.bluefile.cz/RealWinter/stats/echo.php?plrs="+NumPlayers+"&public=true&port="+ServerPort+"&ver="+Version+"&maxplrs="+MaxPlayers+"&srvname="+ServerName+"&comm="+Comment).openConnection();
                connection.setConnectTimeout(5000);
                connection.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String input = reader.readLine();
                if ((input = reader.readLine()) != null) {
                    if("ok".equals(input)) {
                        if(Config.getVariables().isDebugMode()) plugin.log("Web statistics updated.");
                    } else {
                        if(Config.getVariables().isDebugMode()) plugin.log("Failed to update web statistics.");
                    }
                }
                reader.close();
            } catch (MalformedURLException ex) {
                if(Config.getVariables().isDebugMode()) plugin.log(ex.getMessage());
            } catch (IOException ex) {
                if(Config.getVariables().isDebugMode()) plugin.log("Connection failed: " + ex.getMessage());
            }
        } else {
            int NumPlayers = plugin.getServer().getOnlinePlayers().length;
            int ServerPort = plugin.getServer().getPort();
            String Version = plugin.getDescription().getVersion();
            URLConnection connection;
            try {
                connection = new URL("http://www.dodex-mc.bluefile.cz/RealWinter/stats/echo.php?plrs="+NumPlayers+"&public=false&port="+ServerPort+"&ver="+Version).openConnection();
                connection.setConnectTimeout(5000);
                connection.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String input = reader.readLine();
                if ((input = reader.readLine()) != null) {
                    if("ok".equals(input)) {
                        if(Config.getVariables().isDebugMode()) plugin.log("Web statistics updated.");
                    } else {
                        if(Config.getVariables().isDebugMode()) plugin.log("Failed to update web statistics.");
                    }
                }
                reader.close();
            } catch (MalformedURLException ex) {
                if(Config.getVariables().isDebugMode()) plugin.log(ex.getMessage());
            } catch (IOException ex) {
                if(Config.getVariables().isDebugMode()) plugin.log("Connection failed: " + ex.getMessage());
            }
        }
    }
}
