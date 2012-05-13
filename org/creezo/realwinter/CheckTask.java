/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

/**
 *
 * @author Dodec
 */
public class CheckTask implements Runnable {
    private final RealWinter plugin;
    private static Configuration Config = RealWinter.Config;
    private static PlayerCheck playerCheck = RealWinter.playerCheck;
    private boolean DebugMode = Config.DebugMode;
    private int RepeatingMessage = 1;
    private int MessageDelay = Config.MessageDelay;
    private int RepeatingFoodDecrease = 1;
    private int RepeatingFoodDecreaseDelay = Config.ChecksPerFoodDecrease;
    private Localization Loc = RealWinter.Localization;
    private int[] MissingArmorDamage = Config.MissingArmorDamage;
    private final Player player;

    public CheckTask(RealWinter plugin, Player Player) {
        this.plugin = plugin;
        this.player = Player;
    }

    @Override
    public void run() {
            if(Config.GlobalEnable && !player.hasPermission("realwinter.immune")) {
            try {
                if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Check");
                if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Difficulty: " + player.getWorld().getDifficulty().name());
                if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Player stamina: " + Utils.ConvertFloatToString(player.getSaturation()));
                boolean isInside;
                Biome PlayerBiome;
                int NumOfClothes;
                int heat;
                RealWinter.actualWeather = player.getLocation().getWorld().hasStorm();
                if(player.getGameMode().equals(GameMode.SURVIVAL) && RealWinter.actualWeather == true) {
                    if(Config.WinterEnabled && Config.AllowedWorlds.contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realwinter.immune.winter")) {
                        PlayerBiome = PlayerCheck.checkPlayerBiome(player);
                        if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Biome: " + PlayerBiome.name());
                        if(PlayerBiome == Biome.FROZEN_OCEAN || PlayerBiome == Biome.FROZEN_RIVER || PlayerBiome == Biome.ICE_DESERT || PlayerBiome == Biome.ICE_MOUNTAINS || PlayerBiome == Biome.ICE_PLAINS || PlayerBiome == Biome.TUNDRA || PlayerBiome == Biome.TAIGA || PlayerBiome == Biome.TAIGA_HILLS) {
                            NumOfClothes = playerCheck.checkPlayerClothes(player);
                            if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Clothes check done");
                            if(MissingArmorDamage[4] == 0 && NumOfClothes == 4) {
                                if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] FullArmor damage set to 0. All armor pieces worn.");
                            } else {
                                heat = PlayerCheck.checkHeatAround(player, Config.HeatCheckRadius);
                                if(heat < Config.TempPeak) {
                                    isInside = PlayerCheck.checkPlayerInside(player, Config.CheckRadius, Config.HouseRecoWinter);
                                    if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Is Inside done");
                                    if(isInside == false && !RealWinter.PlayerIceHashMap.get(player.getEntityId())) {
                                        if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Is Inside = false");
                                        if(RepeatingMessage == 1) {
                                            player.sendMessage(ChatColor.GOLD + Loc.WinterWarnMessage);
                                            RepeatingMessage = MessageDelay;
                                        } else { RepeatingMessage--; }
                                        switch(NumOfClothes) {
                                            case 0:
                                                if((player.getHealth() - MissingArmorDamage[NumOfClothes]) >= 1) {
                                                    player.damage(MissingArmorDamage[NumOfClothes]);
                                                } else if((player.getHealth() - MissingArmorDamage[NumOfClothes]) <= 0 && Config.WinterKill) {
                                                    player.damage(MissingArmorDamage[NumOfClothes]);
                                                }
                                                break;
                                            case 1:
                                                if((player.getHealth() - MissingArmorDamage[NumOfClothes]) >= 1) {
                                                    player.damage(MissingArmorDamage[NumOfClothes]);
                                                } else if((player.getHealth() - MissingArmorDamage[NumOfClothes]) <= 0 && Config.WinterKill) {
                                                    player.damage(MissingArmorDamage[NumOfClothes]);
                                                }
                                                break;
                                            case 2:
                                                if((player.getHealth() - MissingArmorDamage[NumOfClothes]) >= 1) {
                                                    player.damage(MissingArmorDamage[NumOfClothes]);
                                                } else if((player.getHealth() - MissingArmorDamage[NumOfClothes]) <= 0 && Config.WinterKill) {
                                                    player.damage(MissingArmorDamage[NumOfClothes]);
                                                }
                                                break;
                                            case 3:
                                                if((player.getHealth() - MissingArmorDamage[NumOfClothes]) >= 1) {
                                                    player.damage(MissingArmorDamage[NumOfClothes]);
                                                } else if((player.getHealth() - MissingArmorDamage[NumOfClothes]) <= 0 && Config.WinterKill) {
                                                    player.damage(MissingArmorDamage[NumOfClothes]);
                                                }
                                                break;
                                            case 4:
                                                if((player.getHealth() - MissingArmorDamage[NumOfClothes]) >= 1) {
                                                    player.damage(MissingArmorDamage[NumOfClothes]);
                                                } else if((player.getHealth() - MissingArmorDamage[NumOfClothes]) <= 0 && Config.WinterKill) {
                                                    player.damage(MissingArmorDamage[NumOfClothes]);
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                        DamageEvent DamageEvent = new DamageEvent(player, MissingArmorDamage[NumOfClothes], player.getHealth());
                                        plugin.getServer().getPluginManager().callEvent(DamageEvent);
                                    }
                                }
                            }
                        }
                    }
                } else if(player.getGameMode().equals(GameMode.SURVIVAL) && RealWinter.actualWeather == false) {
                    if(Config.DesertEnabled && Config.AllowedWorlds.contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realwinter.immune.desert")) {
                        PlayerBiome = PlayerCheck.checkPlayerBiome(player);
                        if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Biome: " + PlayerBiome.name());
                        if(PlayerBiome == Biome.DESERT || PlayerBiome == Biome.DESERT_HILLS) {
                            if(player.getWorld().getTime() >= 2500 && player.getWorld().getTime() < 10000) {
                                isInside = PlayerCheck.checkPlayerInside(player, Config.CheckRadius, Config.HouseRecoDesert);
                                if(isInside == false) {
                                    if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Player is outside.");
                                    boolean HasHelmet = playerCheck.GetPlayerHelmet(player);
                                    if(HasHelmet == true) {
                                        if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Player has helmet.");
                                        if(player.getSaturation() > Config.DesertStaminaLostHelmet) {
                                            player.setSaturation(player.getSaturation() - Config.DesertStaminaLostHelmet);
                                            if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Stamina: " + Utils.ConvertFloatToString(player.getSaturation()));
                                        } else { player.setSaturation(0.0F); }
                                    } else if(HasHelmet == false) {
                                        if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Player has not helmet.");
                                        if(RepeatingMessage == 1) {
                                            player.sendMessage(ChatColor.GOLD + Loc.DesertWarnMessage);
                                            RepeatingMessage = MessageDelay;
                                        } else { RepeatingMessage--; }
                                        if(player.getSaturation() > Config.DesertStaminaLostNoHelmet) {
                                            player.setSaturation(player.getSaturation() - Config.DesertStaminaLostNoHelmet);
                                            if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Stamina: " + Utils.ConvertFloatToString(player.getSaturation()));
                                        } else { player.setSaturation(0.0F);
                                            if(player.getFoodLevel() > 1) {
                                                if(RepeatingFoodDecrease == 1) {
                                                    player.setFoodLevel(player.getFoodLevel() - 1);
                                                    RepeatingFoodDecrease = RepeatingFoodDecreaseDelay;
                                                } else { RepeatingFoodDecrease--; }

                                                if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Food level(1-20): " + Utils.ConvertIntToString(player.getFoodLevel()));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if(player.getGameMode().equals(GameMode.SURVIVAL)) {
                    if(Config.GlobalThirstEnabled && Config.ThirstAllowedWorlds.contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realwinter.immune.thirst")) {
                        if(player.getSaturation() > Config.ThirstStaminaLost) {
                            player.setSaturation(player.getSaturation() - Config.ThirstStaminaLost);
                            if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Stamina: " + Utils.ConvertFloatToString(player.getSaturation()));
                        } else { player.setSaturation(0.0F); }
                    }
                }
                if(DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Check end");
            } catch(Exception e) {
                plugin.getServer().broadcastMessage(e.getMessage());
            }
        }
    }
}
