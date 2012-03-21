package org.creezo.realwinter;

import java.util.HashMap;
import java.util.logging.Level;
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
public class PlayerListener implements Listener {
    private final RealWinter plugin;
    private static Configuration Config = RealWinter.Config;
    private static PlayerCheck playerCheck = RealWinter.playerCheck;
    private boolean DebugMode = Config.DebugMode;
    private HashMap<Integer, Integer> PlayerHashMap = RealWinter.PlayerHashMap;
    private int RepeatingMessage = 1;
    private int MessageDelay = Config.MessageDelay;
    private int RepeatingFoodDecrease = 1;
    private int RepeatingFoodDecreaseDelay = Config.ChecksPerFoodDecrease;
    private Localization Loc = RealWinter.Localization;

    public PlayerListener(RealWinter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        
        //plugin.log.log(Level.INFO, ConvertFloatToString(PlayerEnergy));
        int PlayerID = player.getEntityId();
        final int[] MissingArmorDamage = Config.MissingArmorDamage;
        RealWinter.actualWeather = event.getPlayer().getLocation().getBlock().getWorld().hasStorm();
        if(RealWinter.actualWeather == true) player.sendMessage(Loc.WinterLoginMessage);
            PlayerHashMap.put(PlayerID, new Integer(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() { 

            @Override
            public void run() {
                    if(Config.GlobalEnable) {
                    try {
                        if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Check");
                        if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Difficulty: " + player.getWorld().getDifficulty().name());
                        boolean isInside;
                        Biome PlayerBiome;
                        int NumOfClothes;
                        int heat;
                        RealWinter.actualWeather = player.getLocation().getWorld().hasStorm();
                        if(player.getGameMode().equals(GameMode.SURVIVAL) && RealWinter.actualWeather == true) {
                            if(Config.WinterEnabled) {
                                PlayerBiome = PlayerCheck.checkPlayerBiome(player);
                                if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Biome: " + PlayerBiome.name());
                                if(PlayerBiome == Biome.FROZEN_OCEAN || PlayerBiome == Biome.FROZEN_RIVER || PlayerBiome == Biome.ICE_DESERT || PlayerBiome == Biome.ICE_MOUNTAINS || PlayerBiome == Biome.ICE_PLAINS || PlayerBiome == Biome.TUNDRA || PlayerBiome == Biome.TAIGA || PlayerBiome == Biome.TAIGA_HILLS) {
                                    NumOfClothes = playerCheck.checkPlayerClothes(player, plugin);
                                    if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Clothes check done");
                                    if(MissingArmorDamage[4] == 0 && NumOfClothes == 4) {
                                    if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] FullArmor damage set to 0. All armor pieces worn.");
                                    } else {
                                        heat = PlayerCheck.checkHeatAround(player);
                                        if(heat < 50) {
                                            isInside = PlayerCheck.checkPlayerInside(player, Config.CheckRadius, Config.HouseRecoWinter);
                                            if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Is Inside done");
                                            if(isInside == false) {
                                                if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Is Inside = false");
                                                if(RepeatingMessage == 1) {
                                                    player.sendMessage(Loc.WinterWarnMessage);
                                                    RepeatingMessage = MessageDelay;
                                                } else { RepeatingMessage--; }
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
                        } else if(player.getGameMode().equals(GameMode.SURVIVAL) && RealWinter.actualWeather == false) {
                            if(Config.DesertEnabled) {
                                PlayerBiome = PlayerCheck.checkPlayerBiome(player);
                                if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Biome: " + PlayerBiome.name());
                                if(PlayerBiome == Biome.DESERT || PlayerBiome == Biome.DESERT_HILLS) {
                                    if(player.getWorld().getTime() >= 2500 && player.getWorld().getTime() < 10000) {
                                        isInside = PlayerCheck.checkPlayerInside(player, Config.CheckRadius, Config.HouseRecoDesert);
                                        if(isInside == false) {
                                            if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Player is outside.");
                                            boolean HasHelmet = playerCheck.GetPlayerHelmet(player, plugin);
                                            if(HasHelmet == true) {
                                                if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Player has helmet.");
                                                if(player.getSaturation() > 0.1F) {
                                                    player.setSaturation(player.getSaturation() - 0.1F);
                                                    if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Stamina: " + ConvertFloatToString(player.getSaturation()));
                                                } else { player.setSaturation(0.0F); }
                                            } else if(HasHelmet == false) {
                                                if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Player has not helmet.");
                                                if(RepeatingMessage == 1) {
                                                    player.sendMessage(Loc.DesertWarnMessage);
                                                    RepeatingMessage = MessageDelay;
                                                } else { RepeatingMessage--; }
                                                if(player.getSaturation() > 0.3F) {
                                                    player.setSaturation(player.getSaturation() - 0.3F);
                                                    if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Stamina: " + ConvertFloatToString(player.getSaturation()));
                                                } else { player.setSaturation(0.0F);
                                                    if(player.getFoodLevel() > 1) {
                                                        if(RepeatingFoodDecrease == 1) {
                                                            player.setFoodLevel(player.getFoodLevel() - 1);
                                                            RepeatingFoodDecrease = RepeatingFoodDecreaseDelay;
                                                        } else { RepeatingFoodDecrease--; }

                                                        if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Food level(1-20): " + ConvertIntToString(player.getFoodLevel()));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Check end");
                    } catch(Exception e) {
                        plugin.getServer().broadcastMessage(e.getMessage());
                    }
                }
            }
        }, Config.StartDelay * 20, Config.CheckDelay * 20)));
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        int PlayerID = event.getPlayer().getEntityId();
        Integer TaskID = PlayerHashMap.get(PlayerID);
        plugin.getServer().getScheduler().cancelTask(TaskID.intValue());
        PlayerHashMap.remove(PlayerID);
    }
    
    private static String ConvertIntToString(int number) {
        return "" + number;
    }
    
    private static String ConvertFloatToString(float number) {
        return "" + number;
    }
}