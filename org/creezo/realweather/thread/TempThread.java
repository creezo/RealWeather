package org.creezo.realweather.thread;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.logging.Level;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.creezo.realweather.RealWeather;

/**
 *
 * @author creezo
 */
class TempThread implements Runnable {
    private final RealWeather plugin;
    private final Player player;
    private final DecimalFormat df = new DecimalFormat("##.#");

    TempThread(RealWeather plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }
    
    @Override
    public void run() {
        try {
            if(RealWeather.isGlobalyEnable() & !player.getGameMode().equals(GameMode.CREATIVE)) {
                double temperature = plugin.checkCenter.getTemperature(player.getLocation(), player);
                if(plugin.playerHeatShow.get(player.getEntityId()).equals(Boolean.TRUE)) {
                    plugin.utils.sendMessage(player, plugin.localization.getValue("CurrentTemperature")+df.format(temperature));
                }
                try{
                    if(plugin.playerClientMod.get(player.getEntityId())) {
                        byte[] bytes = ("TM:"+df.format(temperature)).getBytes();
                        player.sendPluginMessage(plugin, "realweather", bytes);
                    }
                } catch(Exception e) {
                    if(RealWeather.isDebug()) RealWeather.log.log(Level.SEVERE, "Error in Cliend-mod handling" ,e);
                }
                plugin.getPlayerTemperature().put(player, temperature);
                /*if(temperature < -60) temperature = -60;
                if(temperature > 80) temperature = 80;*/
                
                if(player.hasPermission("realweather.immune.all")) return;
                
                plugin.getFeatureManager().tick(player, temperature);
            }
        } catch(Exception e) {
            RealWeather.log("Error in temperature thread!");
            RealWeather.log.log(Level.SEVERE, null, e);
            RealWeather.sendStackReport(e);
        }
    }
    
    public static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }
}
