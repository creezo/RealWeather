package org.creezo.realweather;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.creezo.realweather.weather.Weather;

/**
 *
 * @author creezo
 */
public class WeatherListener implements Listener {
    private final RealWeather plugin;
    //private Configuration Config = RealWeather.Config;
    //private HashMap<Integer, Boolean> PlayerIceHashMap = RealWeather.PlayerIceHashMap;
    //private HashMap<Integer, Block> IceBlock = RealWeather.IceBlock;
    
    public WeatherListener(RealWeather plugin) {
        this.plugin = plugin;
    }
        
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if(plugin.Config.getVariables().isDebugMode()) Bukkit.broadcastMessage("Weather Changed.");
        plugin.actualWeather = event.toWeatherState();
        if((plugin.weather[2].equals(Weather.CLEAR) || plugin.weather[2].equals(Weather.COLD) || plugin.weather[2].equals(Weather.FREEZE) || plugin.weather[2].equals(Weather.HOT) || plugin.weather[2].equals(Weather.TROPIC) || plugin.weather[2].equals(Weather.WARM)) && event.toWeatherState()) {
            if(plugin.Config.getVariables().isWeatherChangeEnable()) event.setCancelled(true);
        }
        /*if(!event.toWeatherState() && !event.isCancelled()) {
            for(int i=0;i<plugin.getServer().getOnlinePlayers().length;i++) {
                if(plugin.PlayerIceHashMap.get(plugin.getServer().getOnlinePlayers()[i].getEntityId())) {
                    Player player = plugin.getServer().getOnlinePlayers()[i];
                    Block Block = plugin.IceBlock.get(player.getEntityId());
                    if(Block.getLocation().getBlock().getType().equals(Material.ICE)) Block.getLocation().getBlock().setType(Material.AIR);
                    if(Block.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.ICE)) Block.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
                    plugin.IceBlock.remove(player.getEntityId());
                }
                plugin.PlayerIceHashMap.put(plugin.getServer().getOnlinePlayers()[i].getEntityId(), Boolean.FALSE);
            }
        }*/
    }
}
