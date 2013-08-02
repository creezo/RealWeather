package org.creezo.realweather;

import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author creezo
 */
public class PlayerMove implements Listener {
    private final RealWeather plugin;
    //private HashMap<Integer, Boolean> PlayerIceHashMap = RealWeather.PlayerIceHashMap;
    //private HashMap<Integer, Block> IceBlock = RealWeather.IceBlock;
    
    public PlayerMove(RealWeather plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        /*try {
            if(plugin.PlayerIceHashMap.get(player.getEntityId())) {
                if(player.getLocation().distanceSquared(plugin.IceBlock.get(player.getEntityId()).getLocation()) <= 4) {
                    event.setTo(event.getFrom());
                } else {
                    plugin.PlayerIceHashMap.remove(player.getEntityId());
                    Block Block = plugin.IceBlock.get(player.getEntityId());
                    if(Block.getLocation().getBlock().getType().equals(Material.ICE)) Block.getLocation().getBlock().setType(Material.AIR);
                    if(Block.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.ICE)) Block.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
                    plugin.IceBlock.remove(player.getEntityId());
                }
            }
        } catch(NullPointerException ex) { 
            if(plugin.Config.getVariables().isDebugMode()) plugin.log.log(Level.WARNING, null, ex);
        }*/
    }
}
