package org.creezo.realweather;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 *
 * @author Dodec
 */
public class PlayerDamage implements Listener {
    private final RealWeather plugin;
    private static Configuration Config = RealWeather.Config;
    private HashMap<Integer, Boolean> PlayerIceHashMap = RealWeather.PlayerIceHashMap;
    private HashMap<Integer, Block> IceBlock = RealWeather.IceBlock;
    private boolean PlayerIceBlock = Config.getVariables().getBiomes().getWinter().getPlayerIceBlock();
    private List<Material> Mats = RealWeather.Mats;
    private Localization Loc = RealWeather.Localization;
    
    public PlayerDamage(RealWeather plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerDamageFromRW(DamageEvent event) {
        if(Config.getVariables().isDebugMode()) plugin.log(event.getPlayer().getPlayerListName() + ", Damage: " + event.getDamage() + " LeftHP: " + event.getHealth());
        Player player = event.getPlayer();
        if(event.getHealth() > 0 && event.getHealth() <= 2 && PlayerIceBlock) {
            PlayerIceHashMap.put(player.getEntityId(), true);
            IceBlock.put(player.getEntityId(), player.getLocation().getBlock());
            player.sendMessage(ChatColor.GOLD + Loc.WinterInIceBlock);
            if(Mats.contains(player.getLocation().getBlock().getType())) {
                player.getLocation().getBlock().setType(Material.ICE);
            }
            if(Mats.contains(player.getLocation().getBlock().getRelative(BlockFace.UP).getType())) {
                player.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.ICE);
            }
        }
    }
    
    @EventHandler
    public void onPlayerDamageFromIceBlock(EntityDamageEvent event) {
        Entity[] ents = plugin.getServer().getOnlinePlayers();
        List<Entity> Lents = new ArrayList();
        Lents.addAll(Arrays.asList(ents));
        if(Lents.contains(event.getEntity())) {
            if(event.getCause().equals(DamageCause.SUFFOCATION) || event.getCause().equals(DamageCause.FALL) || event.getCause().equals(DamageCause.ENTITY_ATTACK) || event.getCause().equals(DamageCause.PROJECTILE)) {
                Player player = (Player) event.getEntity();
                try {
                    if(PlayerIceHashMap.get(player.getEntityId())) {
                        event.setCancelled(true);
                    }
                } catch (Exception e) {
                }
            }
            if(event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
                Player player = (Player) event.getEntity();
                if(player.getLocation().getBlock().getBiome().equals(Biome.JUNGLE) || player.getLocation().getBlock().getBiome().equals(Biome.JUNGLE_HILLS)) {
                    if (event instanceof EntityDamageByEntityEvent) {
                        EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent)event;
                        Entity damager = edbeEvent.getDamager();
                        if(damager.getType().equals(EntityType.SILVERFISH)) {
                            Utils.PlayerPoisoner(player, Config.getVariables().getBiomes().getJungle().getSilverFishPoisonChance(), true);
                        }
                    }
                }
            }
        }
    }
}
