package org.creezo.realweather;

import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

/**
 *
 * @author Dodec
 */
public class CheckTask implements Runnable {
    private final RealWeather plugin;
    private static Configuration Config = RealWeather.Config;
    private static PlayerCheck playerCheck = RealWeather.playerCheck;
    private boolean DebugMode = Config.getVariables().isDebugMode();
    private static Utils Utils = RealWeather.Utils;
    private int RepeatingMessage = 1;
    private int MessageDelay = Config.getVariables().getMessageDelay();
    private int RepeatingFoodDecrease = 1;
    private int RepeatingFoodDecreaseDelay = Config.getVariables().getBiomes().getDesert().getChecksPerFoodDecrease();
    private Localization Loc = RealWeather.Localization;
    private int[] MissingArmorDamage = Config.getVariables().getBiomes().getWinter().getMissingArmorDamage();
    private final Player player;
    private HashMap<Integer, Integer> PlayerHealthBuffer = RealWeather.PlayerHealthBuffer;

    public CheckTask(RealWeather plugin, Player Player) {
        this.plugin = plugin;
        this.player = Player;
    }

    @Override
    public synchronized void run() {
            if(Config.getVariables().isGlobalEnable() && !player.hasPermission("realwinter.immune")) {
            try {
                if(DebugMode) plugin.log("Check");
                if(DebugMode) plugin.log("Difficulty: " + player.getWorld().getDifficulty().name());
                if(DebugMode) plugin.log("Player stamina: " + Utils.ConvertFloatToString(player.getSaturation()));
                boolean isInside;
                Biome PlayerBiome;
                int NumOfClothes;
                int heat;
                RealWeather.actualWeather = player.getLocation().getWorld().hasStorm();
                if(player.getGameMode().equals(GameMode.SURVIVAL) && RealWeather.actualWeather == true) {
                    if(Config.getVariables().getBiomes().getWinter().isEnabled() && Config.getVariables().getAllowedWorlds().contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realwinter.immune.winter")) {
                        PlayerBiome = PlayerCheck.checkPlayerBiome(player);
                        if(DebugMode) plugin.log("Biome: " + PlayerBiome.name());
                        if(PlayerBiome == Biome.FROZEN_OCEAN || PlayerBiome == Biome.FROZEN_RIVER || PlayerBiome == Biome.ICE_DESERT || PlayerBiome == Biome.ICE_MOUNTAINS || PlayerBiome == Biome.ICE_PLAINS || PlayerBiome == Biome.TUNDRA || PlayerBiome == Biome.TAIGA || PlayerBiome == Biome.TAIGA_HILLS) {
                            NumOfClothes = playerCheck.checkPlayerClothes(player);
                            if(DebugMode) plugin.log("Clothes check done");
                            if(MissingArmorDamage[4] == 0 && NumOfClothes == 4) {
                                if(DebugMode) plugin.log("FullArmor damage set to 0. All armor pieces worn.");
                            } else {
                                heat = PlayerCheck.checkHeatAround(player, Config.getVariables().getBiomes().getWinter().getHeatCheckRadius());
                                if(heat < Config.getVariables().getBiomes().getWinter().getTempPeak()) {
                                    isInside = PlayerCheck.checkPlayerInside(player, Config.getVariables().getBiomes().getWinter().getCheckRadius(), Config.getVariables().getBiomes().getWinter().getHouseRecoWinter());
                                    if(DebugMode) plugin.log("Is Inside done");
                                    if(isInside == false && !RealWeather.PlayerIceHashMap.get(player.getEntityId())) {
                                        if(DebugMode) plugin.log("Is Inside = false");
                                        if(RepeatingMessage == 1) {
                                            player.sendMessage(ChatColor.GOLD + Loc.WinterWarnMessage);
                                            RepeatingMessage = MessageDelay;
                                        } else { RepeatingMessage--; }
                                        switch(NumOfClothes) {
                                            case 0:
                                                if((player.getHealth() - MissingArmorDamage[NumOfClothes]) >= 1) {
                                                    //player.damage(MissingArmorDamage[NumOfClothes]);
                                                    int DMGBuffer = PlayerHealthBuffer.get(player.getEntityId());
                                                    DMGBuffer += MissingArmorDamage[NumOfClothes];
                                                    PlayerHealthBuffer.put(player.getEntityId(), DMGBuffer);
                                                } else if((player.getHealth() - MissingArmorDamage[NumOfClothes]) <= 0 && Config.getVariables().getBiomes().getWinter().isWinterKilliing()) {
                                                    //player.damage(MissingArmorDamage[NumOfClothes]);
                                                }
                                                break;
                                            case 1:
                                                if((player.getHealth() - MissingArmorDamage[NumOfClothes]) >= 1) {
                                                    //player.damage(MissingArmorDamage[NumOfClothes]);
                                                    int DMGBuffer = PlayerHealthBuffer.get(player.getEntityId());
                                                    DMGBuffer += MissingArmorDamage[NumOfClothes];
                                                    PlayerHealthBuffer.put(player.getEntityId(), DMGBuffer);
                                                } else if((player.getHealth() - MissingArmorDamage[NumOfClothes]) <= 0 && Config.getVariables().getBiomes().getWinter().isWinterKilliing()) {
                                                    //player.damage(MissingArmorDamage[NumOfClothes]);
                                                }
                                                break;
                                            case 2:
                                                if((player.getHealth() - MissingArmorDamage[NumOfClothes]) >= 1) {
                                                    //player.damage(MissingArmorDamage[NumOfClothes]);
                                                    int DMGBuffer = PlayerHealthBuffer.get(player.getEntityId());
                                                    DMGBuffer += MissingArmorDamage[NumOfClothes];
                                                    PlayerHealthBuffer.put(player.getEntityId(), DMGBuffer);
                                                } else if((player.getHealth() - MissingArmorDamage[NumOfClothes]) <= 0 && Config.getVariables().getBiomes().getWinter().isWinterKilliing()) {
                                                    //player.damage(MissingArmorDamage[NumOfClothes]);
                                                }
                                                break;
                                            case 3:
                                                if((player.getHealth() - MissingArmorDamage[NumOfClothes]) >= 1) {
                                                    //player.damage(MissingArmorDamage[NumOfClothes]);
                                                    int DMGBuffer = PlayerHealthBuffer.get(player.getEntityId());
                                                    DMGBuffer += MissingArmorDamage[NumOfClothes];
                                                    PlayerHealthBuffer.put(player.getEntityId(), DMGBuffer);
                                                } else if((player.getHealth() - MissingArmorDamage[NumOfClothes]) <= 0 && Config.getVariables().getBiomes().getWinter().isWinterKilliing()) {
                                                    player.damage(MissingArmorDamage[NumOfClothes]);
                                                }
                                                break;
                                            case 4:
                                                if((player.getHealth() - MissingArmorDamage[NumOfClothes]) >= 1) {
                                                    //player.damage(MissingArmorDamage[NumOfClothes]);
                                                    int DMGBuffer = PlayerHealthBuffer.get(player.getEntityId());
                                                    DMGBuffer += MissingArmorDamage[NumOfClothes];
                                                    PlayerHealthBuffer.put(player.getEntityId(), DMGBuffer);
                                                } else if((player.getHealth() - MissingArmorDamage[NumOfClothes]) <= 0 && Config.getVariables().getBiomes().getWinter().isWinterKilliing()) {
                                                    //player.damage(MissingArmorDamage[NumOfClothes]);
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                        //DamageEvent DamageEvent = new DamageEvent(player, MissingArmorDamage[NumOfClothes], player.getHealth());
                                        //plugin.getServer().getPluginManager().callEvent(DamageEvent);
                                    }
                                }
                            }
                        }
                    }
                } else if(player.getGameMode().equals(GameMode.SURVIVAL) && RealWeather.actualWeather == false) {
                    if(Config.getVariables().getBiomes().getDesert().isEnabled() && Config.getVariables().getAllowedWorlds().contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realwinter.immune.desert")) {
                        PlayerBiome = PlayerCheck.checkPlayerBiome(player);
                        if(DebugMode) plugin.log("Biome: " + PlayerBiome.name());
                        if(PlayerBiome == Biome.DESERT || PlayerBiome == Biome.DESERT_HILLS) {
                            if(player.getWorld().getTime() >= 2500 && player.getWorld().getTime() < 10000) {
                                isInside = PlayerCheck.checkPlayerInside(player, 1, Config.getVariables().getBiomes().getDesert().getHouseRecognizer());
                                if(isInside == false) {
                                    if(DebugMode) plugin.log("Player is outside.");
                                    boolean HasHelmet = playerCheck.GetPlayerHelmet(player);
                                    if(HasHelmet == true) {
                                        if(DebugMode) plugin.log("Player has helmet.");
                                        if(player.getSaturation() > Config.getVariables().getBiomes().getDesert().getStaminaLostHelmet()) {
                                            player.setSaturation(player.getSaturation() - Config.getVariables().getBiomes().getDesert().getStaminaLostHelmet());
                                            if(DebugMode) plugin.log("Stamina: " + Utils.ConvertFloatToString(player.getSaturation()));
                                        } else { player.setSaturation(0.0F); }
                                    } else if(HasHelmet == false) {
                                        if(DebugMode) plugin.log("Player has not helmet.");
                                        if(RepeatingMessage == 1) {
                                            player.sendMessage(ChatColor.GOLD + Loc.DesertWarnMessage);
                                            RepeatingMessage = MessageDelay;
                                        } else { RepeatingMessage--; }
                                        if(player.getSaturation() > Config.getVariables().getBiomes().getDesert().getStaminaLostNoHelmet()) {
                                            player.setSaturation(player.getSaturation() - Config.getVariables().getBiomes().getDesert().getStaminaLostNoHelmet());
                                            if(DebugMode) plugin.log("Stamina: " + Utils.ConvertFloatToString(player.getSaturation()));
                                        } else { player.setSaturation(0.0F);
                                            if(player.getFoodLevel() > 1) {
                                                if(RepeatingFoodDecrease == 1) {
                                                    player.setFoodLevel(player.getFoodLevel() - 1);
                                                    RepeatingFoodDecrease = RepeatingFoodDecreaseDelay;
                                                } else { RepeatingFoodDecrease--; }

                                                if(DebugMode) plugin.log("Food level(1-20): " + Utils.ConvertIntToString(player.getFoodLevel()));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if(player.getGameMode().equals(GameMode.SURVIVAL)) {
                    if(Config.getVariables().getBiomes().getGlobal().isThirstEnabled() && Config.getVariables().getBiomes().getGlobal().getThirstAllowedWorlds().contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realwinter.immune.thirst")) {
                        if(player.getSaturation() > Config.getVariables().getBiomes().getGlobal().getThirstStaminaLost()) {
                            player.setSaturation(player.getSaturation() - Config.getVariables().getBiomes().getGlobal().getThirstStaminaLost());
                            if(DebugMode) plugin.log("Stamina: " + Utils.ConvertFloatToString(player.getSaturation()));
                        } else { player.setSaturation(0.0F); }
                    }
                    if(Config.getVariables().getBiomes().getJungle().isEnabled() && Config.getVariables().getAllowedWorlds().contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realwinter.immune.jungle")) {
                        PlayerBiome = PlayerCheck.checkPlayerBiome(player);
                        if(PlayerBiome.equals(Biome.JUNGLE) || PlayerBiome.equals(Biome.JUNGLE_HILLS)) {
                            //PlayerIsInJungle.contains(player);
                            if(player.getLocation().getY() >= 60) {
                                if(DebugMode) plugin.log("Looking for tall grass...");
                                boolean IsGrass = PlayerCheck.checkRandomGrass(player, Config.getVariables().getBiomes().getJungle().getInsectJumpRange(), Config.getVariables().getBiomes().getJungle().getChanceMultiplier());
                                if(IsGrass) {
                                    if(DebugMode) plugin.log("Found.");
                                    Utils.PlayerPoisoner(player, 100, IsGrass);
                                }
                            }
                        }
                    }
                }
                if(DebugMode) plugin.log("Check end");
            } catch(Exception e) {
                plugin.getServer().broadcastMessage(e.getMessage());
            }
        }
    }
}
