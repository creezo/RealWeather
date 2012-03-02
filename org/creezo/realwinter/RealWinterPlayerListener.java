package org.creezo.realwinter;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author creezo
 */
public class RealWinterPlayerListener implements Listener {
    private RealWinter plugin;
    private Configuration configuration = new Configuration();
    private int startDelay = configuration.StartDelay();
    private int checkDelay = configuration.CheckDelay();
    public void Initialize(RealWinter instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Initialize(RealWinter.TentoPlugin);
        final Player player = event.getPlayer();
        int PlayerID = player.getEntityId();
        RealWinter.actualWeather = event.getPlayer().getLocation().getBlock().getWorld().hasStorm();
        if(RealWinter.actualWeather == true) player.sendMessage("Bevare of frozen areas without clothes!");
            RealWinter.tid[PlayerID] = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() { 
            
                @Override
            public void run() {
                try {
                    if(configuration.DebugMode()) Bukkit.broadcastMessage("Check");
                    boolean isInside;
                    Biome PlayerBiome;
                    int wearingClothes;
                    RealWinter.actualWeather = player.getLocation().getWorld().hasStorm();
                    if(player.getGameMode().equals(GameMode.SURVIVAL) && RealWinter.actualWeather == true) {
                        PlayerBiome = PlayerCheck.checkPlayerBiome(player);
                        if(configuration.DebugMode()) Bukkit.broadcastMessage(PlayerBiome.name());
                        if(PlayerBiome == Biome.FROZEN_OCEAN || PlayerBiome == Biome.FROZEN_RIVER || PlayerBiome == Biome.ICE_DESERT || PlayerBiome == Biome.ICE_MOUNTAINS || PlayerBiome == Biome.ICE_PLAINS || PlayerBiome == Biome.TUNDRA || PlayerBiome == Biome.TAIGA || PlayerBiome == Biome.TAIGA_HILLS) {
                            wearingClothes = PlayerCheck.checkPlayerClothes(player);
                            if(wearingClothes != 4) {
                                isInside = PlayerCheck.checkPlayerInside(player);
                                if(isInside == false) {
                                    if(wearingClothes <= 3 && wearingClothes > 0) {
                                        player.damage(1);
                                    }
                                    if(wearingClothes == 0) {
                                        player.damage(2);
                                    }
                                }
                            }
                        }
                    }
                    
                } catch(Exception e) {
                    
                } 
            }

        }, startDelay * 20, checkDelay * 20);   
        
    }
    
    
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        int PlayerID = event.getPlayer().getEntityId();
        plugin.getServer().getScheduler().cancelTask(RealWinter.tid[PlayerID]);
    }
        

}