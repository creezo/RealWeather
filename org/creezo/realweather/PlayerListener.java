package org.creezo.realweather;

import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author creezo
 */
public class PlayerListener implements Listener {
    private final RealWeather plugin;
    //private static Configuration Config = RealWeather.Config;
    //private static CheckCenter playerCheck = RealWeather.checkCenter;
    private HashMap<Integer, Integer> PlayerTemperatureThreads;
    private HashMap<Integer, Boolean> PlayerHeatShow;
    //private HashMap<Integer, Boolean> PlayerIceHashMap;
    //private HashMap<Integer, Block> IceBlock;
    //private Localization Loc = RealWeather.Localization;

    public PlayerListener(RealWeather plugin) {
        this.plugin = plugin;
        PlayerTemperatureThreads = plugin.PlayerTemperatureThreads;
        PlayerHeatShow = plugin.PlayerHeatShow;
        //PlayerIceHashMap = plugin.PlayerIceHashMap;
        //IceBlock = plugin.IceBlock;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final int PlayerID = player.getEntityId();
        plugin.actualWeather = event.getPlayer().getLocation().getBlock().getWorld().hasStorm();
        /*if(plugin.actualWeather == true) {
            player.sendMessage(plugin.Localization.FreezingLoginMessage);
            PlayerIceHashMap.put(PlayerID, plugin.checkCenter.isInIce(player));
        } else {
            PlayerIceHashMap.put(PlayerID, false);
        }*/
        PlayerTemperatureThreads.put(PlayerID, new Integer(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new TempThread(plugin, player), plugin.Config.getVariables().getStartDelay(plugin.Config.getVariables().getGameDifficulty()) * 20, plugin.Config.getVariables().getCheckDelay(plugin.Config.getVariables().getGameDifficulty()) * 20)));
        PlayerHeatShow.put(PlayerID, Boolean.FALSE);
        plugin.PlayerClientMod.put(PlayerID, Boolean.FALSE);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        int PlayerID = event.getPlayer().getEntityId();
        try {
            Integer TaskID = PlayerTemperatureThreads.get(PlayerID);
            plugin.getServer().getScheduler().cancelTask(TaskID.intValue());
            plugin.PlayerRefreshing.remove(event.getPlayer());
            plugin.PlayerTemperature.remove(event.getPlayer());
            PlayerHeatShow.remove(PlayerID);
            plugin.PlayerClientMod.remove(PlayerID);
            PlayerTemperatureThreads.remove(PlayerID);
            /*if(PlayerIceHashMap.get(PlayerID)) {
                Player player = event.getPlayer();
                IceBlock.remove(player.getEntityId());
                if(player.getLocation().getBlock().getType().equals(Material.ICE)) player.getLocation().getBlock().setType(Material.AIR);
                if(player.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.ICE)) player.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
            }
            PlayerIceHashMap.remove(PlayerID);*/
            plugin.PlayerDamage.remove(event.getPlayer());
            if(plugin.PlayerDamagerMap.containsKey(event.getPlayer())) {
                synchronized (plugin.PlayerDamagerMap.get(event.getPlayer())) {
                    plugin.PlayerDamagerMap.get(event.getPlayer()).notify();
                }
            }
        } catch (NullPointerException ex) {
            if(plugin.Config.getVariables().isDebugMode()) plugin.log.log(Level.WARNING, null, ex);
        }
    }
}