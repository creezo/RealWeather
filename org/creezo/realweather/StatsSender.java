package org.creezo.realweather;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author creezo
 */
public class StatsSender implements Runnable{
    private static RealWeather plugin;
    private static Configuration Config = RealWeather.Config;
    
    public StatsSender(RealWeather plugin) {
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
            String ServerAddress = Config.getVariables().getStatistics().getServerAddress();
            try {
                String urlParameters = "public=1&plrs="+NumPlayers+"&port="+ServerPort+"&ver="+Version+"&maxplrs="+MaxPlayers+"&srvaddr="+ServerAddress+"&srvname="+ServerName+"&comm="+Comment;
                String request = "http://www.dodex-mc.bluefile.cz/RealWeather/stats/echo.php";
                URL url = new URL(request);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false); 
                connection.setRequestMethod("POST"); 
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                connection.setUseCaches (false);
                connection.connect();
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
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
                connection.disconnect();
            } catch (MalformedURLException ex) {
                if(Config.getVariables().isDebugMode()) plugin.log(ex.getMessage());
            } catch (IOException ex) {
                if(Config.getVariables().isDebugMode()) plugin.log("Connection failed: " + ex.getMessage());
            }
        } else {
            int NumPlayers = plugin.getServer().getOnlinePlayers().length;
            int ServerPort = plugin.getServer().getPort();
            String Version = plugin.getDescription().getVersion();
            try {
                String urlParameters = "public=0&plrs="+NumPlayers+"&port="+ServerPort+"&ver="+Version;
                String request = "http://www.dodex-mc.bluefile.cz/RealWeather/stats/echo.php";
                URL url = new URL(request);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false); 
                connection.setRequestMethod("POST"); 
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                connection.setUseCaches (false);
                connection.connect();
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
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
