/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realweather;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 *
 * @author Dodec
 */
class TempThread implements Runnable {
    private final RealWeather plugin;
    private final Player player;
    private boolean DebugMode = Config.getVariables().isDebugMode();
    List<Biome> BioLight = new ArrayList();
    List<Biome> BioMedium = new ArrayList();
    List<Biome> BioHard = new ArrayList();
    private int RepeatingMessage = 1;
    private int ForecastTemp = RealWeather.ForecastTemp;
    private int MessageDelay = Config.getVariables().getMessageDelay();
    private int RepeatingFoodDecreaseDelay = Config.getVariables().getBiomes().getDesert().getChecksPerFoodDecrease();
    private int RepeatingFoodDecrease = 1;
    private DecimalFormat df = new DecimalFormat("##.#");
    
    private static Configuration Config = RealWeather.Config;
    private static PlayerCheck playerCheck = RealWeather.playerCheck;
    private Localization Loc = RealWeather.Localization;
    private HashMap<Integer, Integer> PlayerHealthBuffer = RealWeather.PlayerHealthBuffer;
    private HashMap<Integer, Boolean> PlayerHeatShow = RealWeather.PlayerHeatShow;
    private Utils utils = RealWeather.Utils;

    public TempThread(RealWeather plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    private void addBiomes() {
        BioLight.add(Biome.BEACH);
        BioLight.add(Biome.FOREST);
        BioLight.add(Biome.MUSHROOM_ISLAND);
        BioLight.add(Biome.MUSHROOM_SHORE);
        BioLight.add(Biome.OCEAN);
        BioLight.add(Biome.PLAINS);
        BioLight.add(Biome.RIVER);
        BioLight.add(Biome.SAVANNA);
        BioLight.add(Biome.SEASONAL_FOREST);
        BioLight.add(Biome.SHRUBLAND);
        BioLight.add(Biome.SWAMPLAND);
        BioLight.add(Biome.HELL);
        BioMedium.add(Biome.DESERT);
        BioMedium.add(Biome.FROZEN_OCEAN);
        BioMedium.add(Biome.FROZEN_RIVER);
        BioMedium.add(Biome.ICE_DESERT);
        BioMedium.add(Biome.ICE_PLAINS);
        BioMedium.add(Biome.JUNGLE);
        BioMedium.add(Biome.RAINFOREST);
        BioMedium.add(Biome.SKY);
        BioMedium.add(Biome.SMALL_MOUNTAINS);
        BioMedium.add(Biome.TAIGA);
        BioMedium.add(Biome.TUNDRA);
        BioHard.add(Biome.DESERT_HILLS);
        BioHard.add(Biome.EXTREME_HILLS);
        BioHard.add(Biome.FOREST_HILLS);
        BioHard.add(Biome.ICE_MOUNTAINS);
        BioHard.add(Biome.JUNGLE_HILLS);
        BioHard.add(Biome.TAIGA_HILLS);
    }
    
    @Override
    public synchronized void run() {
        try {
            if(Config.getVariables().isGlobalEnable() && !player.getGameMode().equals(GameMode.CREATIVE) && !player.hasPermission("realweather.immune")) {
                if(DebugMode) plugin.log("Starting temp calculation.");
                RealWeather.actualWeather = player.getLocation().getWorld().hasStorm();
                Biome PBiome = player.getLocation().getBlock().getBiome();
                int StartTemp = Config.getVariables().getBiomes().getGlobal().getBiomeAverageTemp(player.getLocation().getBlock().getBiome().toString());
                int WeatherModifier = 0;
                double Temperature = 0;
                double TimeMultiplier = Math.sin(Math.toRadians(0.015D * player.getWorld().getTime()));
                if(BioLight.contains(PBiome)) {
                    if(RealWeather.actualWeather) {
                        WeatherModifier = -1 * Config.getVariables().getBiomes().getGlobal().getBiomesWeatherTempModifier("Light");
                    } else {
                        WeatherModifier = Config.getVariables().getBiomes().getGlobal().getBiomesWeatherTempModifier("Light");
                    }
                    Temperature = TimeMultiplier * (double) Config.getVariables().getBiomes().getGlobal().getBiomesWeatherTempModifier("Light");
                }
                else if(BioMedium.contains(PBiome)) {
                    if(RealWeather.actualWeather) {
                        WeatherModifier = -1 * Config.getVariables().getBiomes().getGlobal().getBiomesWeatherTempModifier("Medium");
                    } else {
                        WeatherModifier = Config.getVariables().getBiomes().getGlobal().getBiomesWeatherTempModifier("Medium");
                    }
                    Temperature = TimeMultiplier * (double) Config.getVariables().getBiomes().getGlobal().getBiomesWeatherTempModifier("Medium");
                }
                else if(BioHard.contains(PBiome)) {
                    if(RealWeather.actualWeather) {
                        WeatherModifier = -1 * Config.getVariables().getBiomes().getGlobal().getBiomesWeatherTempModifier("Hard");
                    } else {
                        WeatherModifier = Config.getVariables().getBiomes().getGlobal().getBiomesWeatherTempModifier("Hard");
                    }
                    Temperature = TimeMultiplier * (double) Config.getVariables().getBiomes().getGlobal().getBiomesWeatherTempModifier("Hard");
                    if((PBiome.equals(Biome.ICE_MOUNTAINS) || PBiome.equals(Biome.TAIGA_HILLS) || PBiome.equals(Biome.JUNGLE_HILLS)) && Temperature < 0) Temperature /= 3;
                } else {
                    if(RealWeather.actualWeather) {
                        WeatherModifier = -1 * Config.getVariables().getBiomes().getGlobal().getBiomesWeatherTempModifier("Medium");
                    } else {
                        WeatherModifier = Config.getVariables().getBiomes().getGlobal().getBiomesWeatherTempModifier("Medium");
                    }
                    Temperature = TimeMultiplier * (double) Config.getVariables().getBiomes().getGlobal().getBiomesWeatherTempModifier("Medium");
                }
                Temperature += (player.getLocation().getY()-Config.getVariables().getBiomes().getGlobal().getSeaLevel())/(player.getWorld().getMaxHeight()-Config.getVariables().getBiomes().getGlobal().getSeaLevel())*Config.getVariables().getBiomes().getGlobal().getTopTemp();
                Temperature += ForecastTemp;
                Temperature += StartTemp;
                Temperature += WeatherModifier;
                if(player.getLocation().getBlock().getLightFromSky() < (byte)4 && player.getLocation().getY() < Config.getVariables().getBiomes().getGlobal().getSeaLevel()) {
                    double DeepModifier = 1;
                    if((double)player.getLocation().getY() >= (double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.8d) {
                        DeepModifier = (((double)player.getLocation().getY()-((double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.8d))/((double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()-(double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.8d))+((((double)player.getLocation().getY()-(double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.8d)/((double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()-((double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.8d))-1)*(-0.15d));
                    } else if((double)player.getLocation().getY() <= (double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.2d) {
                        if(Temperature < 0) Temperature = (Temperature *-1)/2;
                        DeepModifier = (((double)player.getLocation().getY()-((double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.2d))/(0-(double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.2d))+((((double)player.getLocation().getY()-(double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.2d)/(0-((double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.2d))-1)*(-0.15d));
                    } else {
                        DeepModifier = 0.15d;
                    }
                    if(DebugMode) plugin.log("DeepModifier (Number between 1 and 0.15):"+DeepModifier);
                    Temperature = ((Temperature-10)*DeepModifier)+10;
                }
                Temperature += PlayerCheck.checkHeatAround(player, Config.getVariables().getBiomes().getGlobal().getHeatCheckRadius());
                List<Entity> Entities = player.getNearbyEntities(Config.getVariables().getBiomes().getGlobal().getHeatCheckRadius(), Config.getVariables().getBiomes().getGlobal().getHeatCheckRadius(), Config.getVariables().getBiomes().getGlobal().getHeatCheckRadius());
                for (Entity entity : Entities) {
                    if(entity.getType().isAlive() && Temperature <= 25) {
                        Temperature += Config.getVariables().getBiomes().getGlobal().getPlayerHeat();
                    }
                }
                if(PlayerHeatShow.get(player.getEntityId()).equals(Boolean.TRUE)) {
                    utils.SendMessage(player, "Temperature in your area: "+df.format(Temperature));
                }
                if(Temperature < -60) Temperature = -60;
                if(Temperature > 80) Temperature = 80;
                if(Temperature < Config.getVariables().getBiomes().getGlobal().getFreezeUnder()) {
                    if(Config.getVariables().getBiomes().getWinter().isEnabled() && Config.getVariables().getAllowedWorlds().contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realweather.immune.winter")) {
                        int TempRange;
                        if(Config.getVariables().getBiomes().getGlobal().getFreezeUnder() >= 0) {
                            TempRange = 60 + Config.getVariables().getBiomes().getGlobal().getFreezeUnder();
                        } else {
                            TempRange = 60 + Config.getVariables().getBiomes().getGlobal().getFreezeUnder();
                        }
                        double percent = ((((Temperature + 60)/TempRange)-1)*-1);
                        double damage = Config.getVariables().getBiomes().getWinter().getMissingArmorDamage()[playerCheck.checkPlayerClothes(player)];
                        double finalDamage = damage * percent;
                        if(PlayerCheck.checkPlayerInside(player, Config.getVariables().getBiomes().getWinter().getCheckRadius(), Config.getVariables().getBiomes().getWinter().getHouseRecoWinter())) finalDamage = 0;
                        if(finalDamage >= 0.5d) {
                            if(RepeatingMessage == 1) {
                                player.sendMessage(ChatColor.GOLD + Loc.WinterWarnMessage);
                                RepeatingMessage = MessageDelay;
                            } else { RepeatingMessage--; }
                        }
                        int IntDamage = (int)finalDamage;
                        double zbytekDouble = finalDamage - IntDamage;
                        if(zbytekDouble >= 0.5d) {
                            IntDamage++;
                        }
                        if((player.getHealth() - IntDamage) >= 1) {
                            int DMGBuffer = PlayerHealthBuffer.get(player.getEntityId());
                            DMGBuffer += IntDamage;
                            PlayerHealthBuffer.put(player.getEntityId(), DMGBuffer);
                        }
                    }
                } else if(Temperature > Config.getVariables().getBiomes().getGlobal().getOverheatOver()) {
                    if(Config.getVariables().getBiomes().getDesert().isEnabled() && Config.getVariables().getAllowedWorlds().contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realweather.immune.desert")) {
                        int TempRange;
                        if(Config.getVariables().getBiomes().getGlobal().getOverheatOver() >= 0) {
                            TempRange = 80 - Config.getVariables().getBiomes().getGlobal().getOverheatOver();
                        } else {
                            TempRange = 80 + Config.getVariables().getBiomes().getGlobal().getOverheatOver();
                        }
                        double percent = (((Temperature - 80)/(double)TempRange)+1);
                        double damage;
                        if(playerCheck.GetPlayerHelmet(player)) {
                            damage = (double) Config.getVariables().getBiomes().getDesert().getStaminaLostHelmet();
                        } else {
                            damage = (double) Config.getVariables().getBiomes().getDesert().getStaminaLostNoHelmet();
                            if(RepeatingMessage == 1) {
                                player.sendMessage(ChatColor.GOLD + Loc.DesertWarnMessage);
                                RepeatingMessage = MessageDelay;
                            } else { RepeatingMessage--; }
                        }
                        double finalDamage = damage * percent;
                        if(PlayerCheck.checkPlayerInside(player, 1, Config.getVariables().getBiomes().getDesert().getHouseRecognizer())) {
                            finalDamage *= 0.5D;
                        }
                        if(player.getSaturation() > (float)finalDamage) {
                            player.setSaturation(player.getSaturation() - (float)finalDamage);
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
                if(Config.getVariables().getBiomes().getGlobal().isThirstEnabled() && Config.getVariables().getBiomes().getGlobal().getThirstAllowedWorlds().contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realweather.immune.thirst")) {
                    if(player.getSaturation() > Config.getVariables().getBiomes().getGlobal().getThirstStaminaLost()) {
                        player.setSaturation(player.getSaturation() - Config.getVariables().getBiomes().getGlobal().getThirstStaminaLost());
                        if(DebugMode) plugin.log("Stamina: " + Utils.ConvertFloatToString(player.getSaturation()));
                    } else { player.setSaturation(0.0F); }
                }
                if(Config.getVariables().getBiomes().getJungle().isEnabled() && Config.getVariables().getAllowedWorlds().contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realweather.immune.jungle")) {
                    Biome PlayerBiome = PlayerCheck.checkPlayerBiome(player);
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
        } catch(Exception e) {
            plugin.getServer().broadcastMessage(e.getMessage());
        }
    }
}
