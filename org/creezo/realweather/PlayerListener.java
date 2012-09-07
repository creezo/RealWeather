package org.creezo.realweather;

import java.util.HashMap;
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
    private static Configuration Config = RealWeather.Config;
    private static PlayerCheck playerCheck = RealWeather.playerCheck;
    private HashMap<Integer, Integer> PlayerTemperatureThreads = RealWeather.PlayerTemperatureThreads;
    private HashMap<Integer, Boolean> PlayerHeatShow = RealWeather.PlayerHeatShow;
    private HashMap<Integer, Boolean> PlayerIceHashMap = RealWeather.PlayerIceHashMap;
    private HashMap<Integer, Block> IceBlock = RealWeather.IceBlock;
    private Localization Loc = RealWeather.Localization;

    public PlayerListener(RealWeather plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final int PlayerID = player.getEntityId();
        RealWeather.actualWeather = event.getPlayer().getLocation().getBlock().getWorld().hasStorm();
        if(RealWeather.actualWeather == true) {
            player.sendMessage(Loc.WinterLoginMessage);
            PlayerIceHashMap.put(PlayerID, playerCheck.isInIce(player));
        } else {
            PlayerIceHashMap.put(PlayerID, false);
        }
        PlayerTemperatureThreads.put(PlayerID, new Integer(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new TempThread(plugin, player), Config.getVariables().getStartDelay(Config.getVariables().getGameDifficulty()) * 20, Config.getVariables().getCheckDelay(Config.getVariables().getGameDifficulty()) * 20)));
        PlayerHeatShow.put(PlayerID, Boolean.FALSE);
        RealWeather.PlayerClientMod.put(PlayerID, Boolean.FALSE);
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
            RealWeather.PlayerClientMod.remove(PlayerID);
            PlayerTemperatureThreads.remove(PlayerID);
            if(PlayerIceHashMap.get(PlayerID)) {
                Player player = event.getPlayer();
                IceBlock.remove(player.getEntityId());
                if(player.getLocation().getBlock().getType().equals(Material.ICE)) player.getLocation().getBlock().setType(Material.AIR);
                if(player.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.ICE)) player.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
            }
            PlayerIceHashMap.remove(PlayerID);
            RealWeather.PlayerDamage.remove(event.getPlayer());
            synchronized (RealWeather.PlayerDamagerMap.get(event.getPlayer())) {
                RealWeather.PlayerDamagerMap.get(event.getPlayer()).notify();
            }
        } catch (NullPointerException ex) {}
    }
}