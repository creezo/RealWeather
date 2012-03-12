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
    private int checkDelay;
    private int CheckRadius;
    public void Initialize(RealWinter instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Initialize(RealWinter.TentoPlugin);
        checkDelay = configuration.CheckDelay(RealWinter.TentoPlugin);
        CheckRadius = configuration.CheckRadius(RealWinter.TentoPlugin);
        final Player player = event.getPlayer();
        int PlayerID = player.getEntityId();
        RealWinter.actualWeather = event.getPlayer().getLocation().getBlock().getWorld().hasStorm();
        if(RealWinter.actualWeather == true) player.sendMessage("Bevare of frozen areas without clothes!");
            RealWinter.tid[PlayerID] = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() { 
            
                @Override
            public void run() {
                try {
                    if(configuration.DebugMode()) player.chat("Check");
                    if(configuration.DebugMode()) player.chat("Difficulty: " + player.getWorld().getDifficulty().name());
                    boolean isInside;
                    Biome PlayerBiome;
                    int wearingClothes;
                    int heat;
                    RealWinter.actualWeather = player.getLocation().getWorld().hasStorm();
                    if(player.getGameMode().equals(GameMode.SURVIVAL) && RealWinter.actualWeather == true) {
                        PlayerBiome = PlayerCheck.checkPlayerBiome(player);
                        if(configuration.DebugMode()) player.chat("Biome: " + PlayerBiome.name());
                        if(PlayerBiome == Biome.FROZEN_OCEAN || PlayerBiome == Biome.FROZEN_RIVER || PlayerBiome == Biome.ICE_DESERT || PlayerBiome == Biome.ICE_MOUNTAINS || PlayerBiome == Biome.ICE_PLAINS || PlayerBiome == Biome.TUNDRA || PlayerBiome == Biome.TAIGA || PlayerBiome == Biome.TAIGA_HILLS) {
                            wearingClothes = PlayerCheck.checkPlayerClothes(player);
                            if(configuration.DebugMode()) player.chat("Clothes check done");
                            if(wearingClothes != 4) {
                                heat = PlayerCheck.checkHeatAround(player);
                                if(heat < 50) {
                                    isInside = PlayerCheck.checkPlayerInside(player, CheckRadius);
                                    if(configuration.DebugMode()) player.chat("Is Inside done");
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
                    }
                    if(configuration.DebugMode()) player.chat("Check end");
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
    private String ConvertDoubleToString(double number) {
        String returningString = Double.toString(number);
        return returningString;
    }
}