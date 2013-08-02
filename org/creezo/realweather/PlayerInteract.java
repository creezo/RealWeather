package org.creezo.realweather;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author creezo
 */
public class PlayerInteract implements Listener{
    //private Configuration Config = RealWeather.Config;
    private ItemStack ItemInHand;
    //private HashMap<Integer, Boolean> PlayerIceHashMap = RealWeather.PlayerIceHashMap;
    //private HashMap<Integer, Block> IceBlock = RealWeather.IceBlock;
    private final RealWeather plugin;
    
    public PlayerInteract(RealWeather plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public synchronized void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if(plugin.Config.getVariables().getBiomes().getGlobal().isReplenishEnabled() == true) {
            try {
                ItemInHand = event.getItem();
                int itemID = ItemInHand.getTypeId();
            } catch(Exception ex) { 
                ItemInHand = new ItemStack(35, 1);
                ItemInHand.setDurability((short)1);
            }
        }
        if(ItemInHand.getTypeId() == 373 && ItemInHand.getDurability() == 0 && plugin.Config.getVariables().getBiomes().getGlobal().isReplenishEnabled() == true) {
            Thread WaterWait = new Thread(new Runnable() {

                @Override
                public void run() {
                    for(int i = 1; i == 1; i++) {
                        try {
                            Thread.sleep(1400);
                        } catch (InterruptedException ex) {
                            plugin.log.log(Level.WARNING, null, ex);
                        }
                        if(player.getItemInHand().getTypeId() != 373) break;
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ex) {
                            plugin.log.log(Level.WARNING, null, ex);
                        }
                        if(player.getItemInHand().getTypeId() == 374)
                            player.setSaturation(player.getSaturation() + plugin.Config.getVariables().getBiomes().getGlobal().getStaminaReplenishAmount());
                        if(plugin.Config.getVariables().isDebugMode()) plugin.log("Stamina Replenished to level: " + player.getSaturation());
                    }
                }
            });
            WaterWait.setDaemon(true);
            WaterWait.setName(player.getName());
            WaterWait.start();
        }
    }
    
    @EventHandler
    public void onPlayerDestroyBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        /*if(plugin.PlayerIceHashMap.get(player.getEntityId())) {
            event.setCancelled(true);
        } else {
            if(plugin.IceBlock.containsValue(block) && block.getType().equals(Material.ICE)) {
                event.setCancelled(true);
                block.setType(Material.AIR);
                block.getRelative(BlockFace.UP).setType(Material.AIR);
                for(int i=0;i<plugin.getServer().getOnlinePlayers().length;i++) {
                    if(block.equals(plugin.getServer().getOnlinePlayers()[i].getLocation().getBlock()) && plugin.PlayerIceHashMap.get(plugin.getServer().getOnlinePlayers()[i].getEntityId())) {
                        plugin.PlayerIceHashMap.put(plugin.getServer().getOnlinePlayers()[i].getEntityId(), Boolean.FALSE);
                        plugin.IceBlock.remove(plugin.getServer().getOnlinePlayers()[i].getEntityId());
                    }
                }
            } else if(plugin.IceBlock.containsValue(block.getRelative(BlockFace.DOWN)) && block.getType().equals(Material.ICE)) {
                event.setCancelled(true);
                block.setType(Material.AIR);
                if(block.getRelative(BlockFace.DOWN).getType().equals(Material.ICE)) {
                    block.getRelative(BlockFace.DOWN).setType(Material.AIR);
                }
                for(int i=0;i<plugin.getServer().getOnlinePlayers().length;i++) {
                    if(block.getRelative(BlockFace.DOWN).equals(plugin.getServer().getOnlinePlayers()[i].getLocation().getBlock()) && plugin.PlayerIceHashMap.get(plugin.getServer().getOnlinePlayers()[i].getEntityId())) {
                        plugin.PlayerIceHashMap.put(plugin.getServer().getOnlinePlayers()[i].getEntityId(), Boolean.FALSE);
                        plugin.IceBlock.remove(plugin.getServer().getOnlinePlayers()[i].getEntityId());
                    }
                }
            }
        }*/
        if(player.getLocation().getBlock().getBiome()==Biome.JUNGLE | player.getLocation().getBlock().getBiome()==Biome.JUNGLE_HILLS) {
            if(block.getTypeId()==2 || (block.getTypeId()==31 && block.getData()==2)) {
                Random random = new Random();
                if(random.nextInt(100) < plugin.Config.getVariables().getBiomes().getJungle().getSilverFishChance()) {
                    Entity SFish = block.getWorld().spawnEntity(block.getLocation(), EntityType.SILVERFISH);
                    SFish.playEffect(EntityEffect.HURT);
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockMelt(BlockPhysicsEvent event) {
        /*if(plugin.Config.getVariables().getBiomes().getFreezing().getPlayerIceBlock()) {
            Block block = event.getBlock();
            if(plugin.IceBlock.containsValue(block) || plugin.IceBlock.containsValue(block.getRelative(BlockFace.DOWN))) {
                event.setCancelled(true);
            }
        }*/
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(event.getPlayer().getLocation().getBlock().getType().equals(Material.STATIONARY_WATER)) {
            if(plugin.PlayerRefreshing.isEmpty()) {
                plugin.PlayerRefreshing.put(event.getPlayer(), 1);
            } else {
                if(!plugin.PlayerRefreshing.containsKey(event.getPlayer())) {
                    plugin.PlayerRefreshing.put(event.getPlayer(), 1);
                }
            }
        }
    }
}
