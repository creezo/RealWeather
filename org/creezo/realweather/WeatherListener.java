package org.creezo.realweather;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 *
 * @author creezo
 */
public class WeatherListener implements Listener {
    private final RealWeather plugin;
    private Configuration Config = RealWeather.Config;
    private HashMap<Integer, Boolean> PlayerIceHashMap = RealWeather.PlayerIceHashMap;
    private HashMap<Integer, Block> IceBlock = RealWeather.IceBlock;
    
    public WeatherListener(RealWeather plugin) {
        this.plugin = plugin;
    }
        
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if(Config.getVariables().isDebugMode()) Bukkit.broadcastMessage("Weather Changed.");
        RealWeather.actualWeather = event.toWeatherState();
        if(!event.toWeatherState()) {
            for(int i=0;i<plugin.getServer().getOnlinePlayers().length;i++) {
                if(PlayerIceHashMap.get(plugin.getServer().getOnlinePlayers()[i].getEntityId())) {
                    Player player = plugin.getServer().getOnlinePlayers()[i];
                    Block Block = IceBlock.get(player.getEntityId());
                    if(Block.getLocation().getBlock().getType().equals(Material.ICE)) Block.getLocation().getBlock().setType(Material.AIR);
                    if(Block.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.ICE)) Block.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
                    IceBlock.remove(player.getEntityId());
                }
                PlayerIceHashMap.put(plugin.getServer().getOnlinePlayers()[i].getEntityId(), Boolean.FALSE);
            }
        }
    }
}
