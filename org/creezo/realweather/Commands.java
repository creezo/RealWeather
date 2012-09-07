package org.creezo.realweather;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 *
 * @author creezo
 */
public class Commands {
    private final RealWeather plugin;

    public Commands(RealWeather plugin) {
        this.plugin = plugin;
    }

    public boolean Set(String[] args) {
        if(args[1].equalsIgnoreCase("desert")) {
            if(args[2].equalsIgnoreCase("NumberOfCheckPerFoodLost")) {
                plugin.Config.getVariables().getBiomes().getDesert().setChecksPerFoodDecrease(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("StaminaLost")) {
                plugin.Config.getVariables().getBiomes().getDesert().setStaminaLost(Float.parseFloat(args[3]));
                return true;
            }
        } else if(args[1].equalsIgnoreCase("global")) {
            if(args[2].equalsIgnoreCase("thirst.StaminaLost")) {
                plugin.Config.getVariables().getBiomes().getGlobal().setThirstStaminaLost(Float.parseFloat(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("staminareplenish.StaminaReplenishWaterBottle")) {
                plugin.Config.getVariables().getBiomes().getGlobal().setStaminaReplenishAmount(Float.parseFloat(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("PlayerHeat")) {
                plugin.Config.getVariables().getBiomes().getGlobal().setPlayerHeat(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("FreezeUnder")) {
                plugin.Config.getVariables().getBiomes().getGlobal().setFreezeUnder(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("OverheatOver")) {
                plugin.Config.getVariables().getBiomes().getGlobal().setOverheatOver(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("MaxMapHeightTemperatureModifier")) {
                plugin.Config.getVariables().getBiomes().getGlobal().setTopTemp(Integer.parseInt(args[3]));
                return true;
            } else {
                return setEntry(args);
            }
        } else if(args[1].equalsIgnoreCase("jungle")) {
            if(args[2].equalsIgnoreCase("InsectJumpRange")) {
                plugin.Config.getVariables().getBiomes().getJungle().setInsectJumpRange(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("ChanceMultiplier")) {
                plugin.Config.getVariables().getBiomes().getJungle().setChanceMultiplier(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("DefaultNegativeEffectDuration")) {
                plugin.Config.getVariables().getBiomes().getJungle().setInsectBiteDuration(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("PoisonDuration")) {
                plugin.Config.getVariables().getBiomes().getJungle().setInsectPoisonDuration(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("SilverFishChance")) {
                plugin.Config.getVariables().getBiomes().getJungle().setSilverFishChance(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("SilverFishPoisonChance")) {
                plugin.Config.getVariables().getBiomes().getJungle().setSilverFishPoisonChance(Integer.parseInt(args[3]));
                return true;
            }
        } else if(args[1].equalsIgnoreCase("winter")) {
            if(args[2].equalsIgnoreCase("CanKillPlayer")) {
                plugin.Config.getVariables().getBiomes().getWinter().setWinterKill(Boolean.parseBoolean(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("PlayerDamage")) {
                plugin.Config.getVariables().getBiomes().getWinter().setMissingArmorDamage(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("PlayerIceBlock")) {
                plugin.Config.getVariables().getBiomes().getWinter().setIceBlock(Boolean.parseBoolean(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("HouseRecognizer")) {
                plugin.Config.getVariables().getBiomes().getWinter().setHouseRecoWinter(args[3]);
                return true;
            } else if(args[2].equalsIgnoreCase("CheckRadius")) {
                plugin.Config.getVariables().getBiomes().getWinter().setCheckRadius(Integer.parseInt(args[3]));
                return true;
            }
        }
        return false;
    }
    
    private boolean setEntry(String[] args) {
        if(args[2].startsWith("BiomesAverageTemp.")) {
            plugin.Config.getVariables().getBiomes().getGlobal().setBiomeAverageTemp(args[2].split("\\.")[1], Integer.parseInt(args[3]));
            return true;
        } else if(args[2].startsWith("BiomesWeatherTempModifier.")) {
            plugin.Config.getVariables().getBiomes().getGlobal().setBiomesWeatherTempModifier(args[2].split("\\.")[1], Integer.parseInt(args[3]));
            return true;
        } else if(args[2].startsWith("BiomeDayNightTempModifier.")) {
            plugin.Config.getVariables().getBiomes().getGlobal().setBiomeDayNightTempModifier(args[2].split("\\.")[2], args[2].split("\\.")[1], Integer.parseInt(args[3]));
            return true;
        } else if(args[2].startsWith("HeatSources.")) {
            plugin.Config.getVariables().getBiomes().getGlobal().setHeatSource(args[2].split("\\.")[1], Double.parseDouble(args[3]));
            plugin.Config.loadHeatSources();
            return true;
        } else if(args[2].startsWith("HeatInHand.")) {
            plugin.Config.getVariables().getBiomes().getGlobal().setHeatInHand(args[2].split("\\.")[1], Double.parseDouble(args[3]));
            plugin.Config.loadHeatSources();
            return true;
        } else {
            return false;
        }
    }
    
    public void Disable() {
        plugin.getConfig().set("GlobalEnable", false);
        try {
            plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
    }
    
    public void Disable(String part) {
        if("all".equals(part)) {
            plugin.Config.getVariables().getBiomes().getWinter().setEnabled(false);
            plugin.Config.getVariables().getBiomes().getDesert().setEnabled(false);
            plugin.Config.getVariables().getBiomes().getJungle().setEnabled(false);
            plugin.Config.getVariables().getBiomes().getGlobal().setReplenishEnabled(false);
            plugin.Config.getVariables().getBiomes().getGlobal().setThirstEnabled(false);
        } else if("desert".equals(part)) {
            plugin.Config.getVariables().getBiomes().getDesert().setEnabled(false);
        } else if("jungle".equals(part)) {
            plugin.Config.getVariables().getBiomes().getJungle().setEnabled(false);
        } else if("winter".equals(part)) {
            plugin.Config.getVariables().getBiomes().getWinter().setEnabled(false);
        } else if("thirst".equals(part)) {
            plugin.Config.getVariables().getBiomes().getGlobal().setThirstEnabled(false);
        } else if("waterbottle".equals(part)) {
            plugin.Config.getVariables().getBiomes().getGlobal().setReplenishEnabled(false);
        }
    }
    
    public void Enable() {
        plugin.getConfig().set("GlobalEnable", true);
        try {
            plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
    }
    
    public void Enable(String part) {
        if("all".equals(part)) {
            plugin.Config.getVariables().getBiomes().getWinter().setEnabled(true);
            plugin.Config.getVariables().getBiomes().getDesert().setEnabled(true);
            plugin.Config.getVariables().getBiomes().getJungle().setEnabled(true);
            plugin.Config.getVariables().getBiomes().getGlobal().setReplenishEnabled(true);
            plugin.Config.getVariables().getBiomes().getGlobal().setThirstEnabled(true);
        } else if("desert".equals(part)) {
            plugin.Config.getVariables().getBiomes().getDesert().setEnabled(true);
        } else if("jungle".equals(part)) {
            plugin.Config.getVariables().getBiomes().getJungle().setEnabled(true);
        } else if("winter".equals(part)) {
            plugin.Config.getVariables().getBiomes().getWinter().setEnabled(true);
        } else if("thirst".equals(part)) {
            plugin.Config.getVariables().getBiomes().getGlobal().setThirstEnabled(true);
        } else if("waterbottle".equals(part)) {
            plugin.Config.getVariables().getBiomes().getGlobal().setReplenishEnabled(true);
        }
    }
    
    public boolean Language(String lang) {
        if(!plugin.Localization.LangExists(lang)) {
            plugin.log.log(Level.INFO, "Language doesn't exists!");
            return false;
        }
        if(!plugin.Localization.SetLanguage(lang)) {
            return false;
        }
        return true;
    }
}
