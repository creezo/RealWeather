package org.creezo.realweather;

import java.util.ConcurrentModificationException;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author creezo
 */
public class RefresherThread implements Runnable {
    private RealWeather plugin;

    public RefresherThread(RealWeather plugin) {
        this.plugin = plugin;
    }
    @Override
    public void run() {
        if(!plugin.PlayerRefreshing.isEmpty()) {
            if(plugin.Config.getVariables().isDebugMode()) plugin.log("Someone is refreshing");
            try {
                for (Player player : plugin.PlayerRefreshing.keySet()) {
                    //if(RealWeather.Config.getVariables().isDebugMode()) RealWeather.log("This one: "+player.getName());
                    if(!plugin.PlayerTemperature.isEmpty() && plugin.PlayerTemperature.containsKey(player)) {
                        //if(RealWeather.Config.getVariables().isDebugMode()) RealWeather.log("Player temp");
                        if(plugin.PlayerTemperature.get(player) >= plugin.Config.getVariables().getBiomes().getGlobal().getOverheatOver()) {
                            int phase = plugin.PlayerRefreshing.get(player);
                            if(phase >= 11) {
                                plugin.getServer().sendPluginMessage(plugin, "realweather", "RF:11".getBytes());
                                player.setSaturation((float)phase);
                                plugin.PlayerRefreshing.remove(player);
                                player.sendMessage(plugin.Localization.Refreshed);
                                //if(RealWeather.Config.getVariables().isDebugMode()) RealWeather.log("Finished " + phase);
                                continue;
                            } else {
                                if(player.getSaturation() < phase && (player.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER) | player.getLocation().getBlock().getType().equals(Material.WATER))) {
                                    player.setSaturation((float)phase);
                                    plugin.getServer().sendPluginMessage(plugin, "realweather", ("RF:"+phase).getBytes());
                                    //if(RealWeather.Config.getVariables().isDebugMode()) RealWeather.log("Refreshing " + phase);
                                } else if(player.getSaturation() > phase && player.getSaturation() <= 10 && player.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER)) {
                                    phase = Math.round(player.getSaturation());
                                    plugin.getServer().sendPluginMessage(plugin, "realweather", ("RF:"+phase).getBytes());
                                    //if(RealWeather.Config.getVariables().isDebugMode()) RealWeather.log("Setting phase" + phase);
                                } else {
                                    phase = 11;
                                    plugin.getServer().sendPluginMessage(plugin, "realweather", ("RF:11").getBytes());
                                    plugin.PlayerRefreshing.remove(player);
                                    //if(RealWeather.Config.getVariables().isDebugMode()) RealWeather.log("Too much stamina or out of water " + phase);
                                    continue;
                                }
                                phase++;
                            }
                            plugin.PlayerRefreshing.put(player, phase);
                        } else {
                            plugin.getServer().sendPluginMessage(plugin, "realweather", ("RF:11").getBytes());
                            plugin.PlayerRefreshing.remove(player);
                        }
                    } else {
                        plugin.PlayerRefreshing.remove(player);
                    }
                }
            } catch(ConcurrentModificationException ex) {
                if(plugin.Config.getVariables().isDebugMode()) plugin.log("Too fast modiffication in players list. Ignore this if someone left server recently.");
            }
        }
    }
}
