package org.creezo.realwinter;

import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author Dodec
 */
public class PlayerMove implements Listener {
    private HashMap<Integer, Boolean> PlayerIceHashMap = RealWinter.PlayerIceHashMap;
    private HashMap<Integer, Block> IceBlock = RealWinter.IceBlock;
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        try {
            if(PlayerIceHashMap.get(player.getEntityId())) {
                if(player.getLocation().distanceSquared(IceBlock.get(player.getEntityId()).getLocation()) <= 4) {
                    event.setTo(event.getFrom());
                } else {
                    PlayerIceHashMap.remove(player.getEntityId());
                    Block Block = IceBlock.get(player.getEntityId());
                    if(Block.getLocation().getBlock().getType().equals(Material.ICE)) Block.getLocation().getBlock().setType(Material.AIR);
                    if(Block.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.ICE)) Block.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
                    IceBlock.remove(player.getEntityId());
                }
            }
        } catch(NullPointerException ex) { }
    }
}
