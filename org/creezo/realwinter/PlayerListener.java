package org.creezo.realwinter;

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
    private final RealWinter plugin;
    private static Configuration Config = RealWinter.Config;
    private static PlayerCheck playerCheck = RealWinter.playerCheck;
    private HashMap<Integer, Integer> PlayerHashMap = RealWinter.PlayerHashMap;
    private HashMap<Integer, Boolean> PlayerIceHashMap = RealWinter.PlayerIceHashMap;
    private HashMap<Integer, Block> IceBlock = RealWinter.IceBlock;
    private Localization Loc = RealWinter.Localization;

    public PlayerListener(RealWinter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final int PlayerID = player.getEntityId();
        RealWinter.actualWeather = event.getPlayer().getLocation().getBlock().getWorld().hasStorm();
        if(RealWinter.actualWeather == true) {
            player.sendMessage(Loc.WinterLoginMessage);
            PlayerIceHashMap.put(PlayerID, playerCheck.isInIce(player));
        } else {
            PlayerIceHashMap.put(PlayerID, false);
        }
        PlayerHashMap.put(PlayerID, new Integer(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new CheckTask(plugin, player), Config.StartDelay * 20, Config.CheckDelay * 20)));
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        int PlayerID = event.getPlayer().getEntityId();
        try {
            Integer TaskID = PlayerHashMap.get(PlayerID);
            plugin.getServer().getScheduler().cancelTask(TaskID.intValue());
            PlayerHashMap.remove(PlayerID);
            if(PlayerIceHashMap.get(PlayerID)) {
                Player player = event.getPlayer();
                IceBlock.remove(player.getEntityId());
                if(player.getLocation().getBlock().getType().equals(Material.ICE)) player.getLocation().getBlock().setType(Material.AIR);
                if(player.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.ICE)) player.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
            }
            PlayerIceHashMap.remove(PlayerID);
        } catch (NullPointerException ex) {
            
        }
    }
}