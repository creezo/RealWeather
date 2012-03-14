package org.creezo.realwinter;

import java.util.HashMap;
import org.bukkit.GameMode;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author creezo
 */
public class RealWinterPlayerListener implements Listener {
    private RealWinter plugin;
    private static Configuration Config = RealWinter.Config;
    private static PlayerCheck playerCheck = RealWinter.playerCheck;
    private boolean DebugMode = Config.DebugMode;
    private HashMap<Integer, Integer> PlayerHashMap = RealWinter.PlayerHashMap;
    public void Initialize(RealWinter instance) {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Initialize(RealWinter.TentoPlugin);
        int StartDelay = Config.StartDelay;
        int CheckDelay = Config.CheckDelay;
        final int CheckRadius = Config.CheckRadius;
        final Player player = event.getPlayer();
        int PlayerID = player.getEntityId();
        final int[] MissingArmorDamage = Config.MissingArmorDamage;
        RealWinter.actualWeather = event.getPlayer().getLocation().getBlock().getWorld().hasStorm();
        if(RealWinter.actualWeather == true) player.sendMessage("Bevare of frozen areas without clothes!");
            PlayerHashMap.put(PlayerID, new Integer(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() { 
            
            @Override
            public void run() {
                try {
                    if(DebugMode) player.chat("Check");
                    if(DebugMode) player.chat("Difficulty: " + player.getWorld().getDifficulty().name());
                    boolean isInside;
                    Biome PlayerBiome;
                    int NumOfClothes;
                    int heat;
                    RealWinter.actualWeather = player.getLocation().getWorld().hasStorm();
                    if(player.getGameMode().equals(GameMode.SURVIVAL) && RealWinter.actualWeather == true) {
                        PlayerBiome = PlayerCheck.checkPlayerBiome(player);
                        if(DebugMode) player.chat("Biome: " + PlayerBiome.name());
                        if(PlayerBiome == Biome.FROZEN_OCEAN || PlayerBiome == Biome.FROZEN_RIVER || PlayerBiome == Biome.ICE_DESERT || PlayerBiome == Biome.ICE_MOUNTAINS || PlayerBiome == Biome.ICE_PLAINS || PlayerBiome == Biome.TUNDRA || PlayerBiome == Biome.TAIGA || PlayerBiome == Biome.TAIGA_HILLS) {
                            NumOfClothes = playerCheck.checkPlayerClothes(player, plugin);
                            if(DebugMode) player.chat("Clothes check done");
                            if(MissingArmorDamage[4] != 0) {
                                heat = PlayerCheck.checkHeatAround(player);
                                if(heat < 50) {
                                    isInside = PlayerCheck.checkPlayerInside(player, CheckRadius);
                                    if(DebugMode) player.chat("Is Inside done");
                                    if(isInside == false) {
                                        switch(NumOfClothes) {
                                            case 0:
                                                player.damage(MissingArmorDamage[NumOfClothes]);
                                                break;
                                            case 1:
                                                player.damage(MissingArmorDamage[NumOfClothes]);
                                                break;
                                            case 2:
                                                player.damage(MissingArmorDamage[NumOfClothes]);
                                                break;
                                            case 3:
                                                player.damage(MissingArmorDamage[NumOfClothes]);
                                                break;
                                            case 4:
                                                player.damage(MissingArmorDamage[NumOfClothes]);
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(DebugMode) player.chat("Check end");
                } catch(Exception e) {
                    plugin.getServer().broadcastMessage(e.getMessage());
                } 
            }

        }, StartDelay * 20, CheckDelay * 20)));
        
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        int PlayerID = event.getPlayer().getEntityId();
        Integer TaskID = PlayerHashMap.get(PlayerID);
        plugin.getServer().getScheduler().cancelTask(TaskID.intValue());
        PlayerHashMap.remove(PlayerID);
    }
}