package org.creezo.realweather;

import java.util.HashMap;
import java.util.List;
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
    private HashMap<Integer, Integer> PlayerHashMap = RealWeather.PlayerHashMap;
    private List<Player> PlayerHealthControler = RealWeather.PlayerHealthControler;
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
        PlayerHashMap.put(PlayerID, new Integer(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new CheckTask(plugin, player), Config.getVariables().getStartDelay(Config.getVariables().getGameDifficulty()) * 20, Config.getVariables().getCheckDelay(Config.getVariables().getGameDifficulty()) * 20)));
        PlayerHealthControler PHControl = new PlayerHealthControler(player, plugin);
        Thread PlayerThread = new Thread(PHControl);
        PlayerThread.setDaemon(true);
        PlayerThread.start();
        PlayerHealthControler.add(player);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        int PlayerID = event.getPlayer().getEntityId();
        try {
            Integer TaskID = PlayerHashMap.get(PlayerID);
            plugin.getServer().getScheduler().cancelTask(TaskID.intValue());
            PlayerHealthControler.remove(event.getPlayer());
            PlayerHashMap.remove(PlayerID);
            if(PlayerIceHashMap.get(PlayerID)) {
                Player player = event.getPlayer();
                IceBlock.remove(player.getEntityId());
                if(player.getLocation().getBlock().getType().equals(Material.ICE)) player.getLocation().getBlock().setType(Material.AIR);
                if(player.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.ICE)) player.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
            }
            PlayerIceHashMap.remove(PlayerID);
        } catch (NullPointerException ex) {}
    }
}