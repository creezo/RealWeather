package org.creezo.realweather.command;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.creezo.realweather.RealWeather;

/**
 *
 * @author creezo
 */
public class Commands {
    private final RealWeather plugin;

    public Commands(RealWeather plugin) {
        this.plugin = plugin;
    }

    /*public boolean Set(String[] args) {
        if(args[1].equalsIgnoreCase("exhausting")) {
            if(args[2].equalsIgnoreCase("NumberOfCheckPerFoodLost")) {
                plugin.config.getVariables().getBiomes().getExhausting().setChecksPerFoodDecrease(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("StaminaLost")) {
                plugin.config.getVariables().getBiomes().getExhausting().setStaminaLost(Float.parseFloat(args[3]));
                return true;
            }
        } else if(args[1].equalsIgnoreCase("global")) {
            if(args[2].equalsIgnoreCase("thirst.StaminaLost")) {
                plugin.config.getVariables().getBiomes().getGlobal().setThirstStaminaLost(Float.parseFloat(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("staminareplenish.StaminaReplenishWaterBottle")) {
                plugin.config.getVariables().getBiomes().getGlobal().setStaminaReplenishAmount(Float.parseFloat(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("PlayerHeat")) {
                plugin.config.getVariables().getBiomes().getGlobal().setPlayerHeat(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("FreezeUnder")) {
                plugin.config.getVariables().getBiomes().getGlobal().setFreezeUnder(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("OverheatOver")) {
                plugin.config.getVariables().getBiomes().getGlobal().setOverheatOver(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("MaxMapHeightTemperatureModifier")) {
                plugin.config.getVariables().getBiomes().getGlobal().setTopTemp(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("TorchesFading")) {
                plugin.config.getVariables().getBiomes().getGlobal().setTorchesFading(Boolean.parseBoolean(args[3]));
                return true;
            } else {
                return setEntry(args);
            }
        } else if(args[1].equalsIgnoreCase("jungle")) {
            if(args[2].equalsIgnoreCase("InsectJumpRange")) {
                plugin.config.getVariables().getBiomes().getJungle().setInsectJumpRange(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("ChanceMultiplier")) {
                plugin.config.getVariables().getBiomes().getJungle().setChanceMultiplier(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("DefaultNegativeEffectDuration")) {
                plugin.config.getVariables().getBiomes().getJungle().setInsectBiteDuration(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("PoisonDuration")) {
                plugin.config.getVariables().getBiomes().getJungle().setInsectPoisonDuration(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("SilverFishChance")) {
                plugin.config.getVariables().getBiomes().getJungle().setSilverFishChance(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("SilverFishPoisonChance")) {
                plugin.config.getVariables().getBiomes().getJungle().setSilverFishPoisonChance(Integer.parseInt(args[3]));
                return true;
            }
        } else if(args[1].equalsIgnoreCase("freezing")) {
            if(args[2].equalsIgnoreCase("CanKillPlayer")) {
                plugin.config.getVariables().getBiomes().getFreezing().setFreezingKill(Boolean.parseBoolean(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("PlayerDamage")) {
                plugin.config.getVariables().getBiomes().getFreezing().setMissingArmorDamage(Integer.parseInt(args[3]));
                return true;
            } else if(args[2].equalsIgnoreCase("HouseRecognizer")) {
                plugin.config.getVariables().getBiomes().getFreezing().setHouseRecoFreezing(args[3]);
                return true;
            } else if(args[2].equalsIgnoreCase("CheckRadius")) {
                plugin.config.getVariables().getBiomes().getFreezing().setCheckRadius(Integer.parseInt(args[3]));
                return true;
            }
        }
        return false;
    }*/
    
    /*private boolean setEntry(String[] args) {
        if(args[2].startsWith("BiomesAverageTemp.")) {
            plugin.config.getVariables().getBiomes().getGlobal().setBiomeAverageTemp(args[2].split("\\.")[1], Integer.parseInt(args[3]));
            return true;
        } else if(args[2].startsWith("BiomesWeatherTempModifier.")) {
            plugin.config.getVariables().getBiomes().getGlobal().setBiomesWeatherTempModifier(args[2].split("\\.")[1], Integer.parseInt(args[3]));
            return true;
        } else if(args[2].startsWith("BiomeDayNightTempModifier.")) {
            plugin.config.getVariables().getBiomes().getGlobal().setBiomeDayNightTempModifier(args[2].split("\\.")[2], args[2].split("\\.")[1], Integer.parseInt(args[3]));
            return true;
        } else if(args[2].startsWith("HeatSources.")) {
            plugin.config.getVariables().getBiomes().getGlobal().setHeatSource(args[2].split("\\.")[1], Double.parseDouble(args[3]));
            plugin.config.loadHeatSources();
            return true;
        } else if(args[2].startsWith("HeatInHand.")) {
            plugin.config.getVariables().getBiomes().getGlobal().setHeatInHand(args[2].split("\\.")[1], Double.parseDouble(args[3]));
            plugin.config.loadHeatSources();
            return true;
        } else {
            return false;
        }
    }*/
    
    public void Disable() {
        plugin.getConfig().set("GlobalyEnable", false);
        try {
            plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
    }
    
    /*public void Disable(String part) {
        if("all".equals(part)) {
            plugin.config.getVariables().getBiomes().getFreezing().setEnabled(false);
            plugin.config.getVariables().getBiomes().getExhausting().setEnabled(false);
            plugin.config.getVariables().getBiomes().getJungle().setEnabled(false);
            plugin.config.getVariables().getBiomes().getGlobal().setReplenishEnabled(false);
            plugin.config.getVariables().getBiomes().getGlobal().setThirstEnabled(false);
        } else if("exhausting".equals(part)) {
            plugin.config.getVariables().getBiomes().getExhausting().setEnabled(false);
        } else if("jungle".equals(part)) {
            plugin.config.getVariables().getBiomes().getJungle().setEnabled(false);
        } else if("freezing".equals(part)) {
            plugin.config.getVariables().getBiomes().getFreezing().setEnabled(false);
        } else if("thirst".equals(part)) {
            plugin.config.getVariables().getBiomes().getGlobal().setThirstEnabled(false);
        } else if("waterbottle".equals(part)) {
            plugin.config.getVariables().getBiomes().getGlobal().setReplenishEnabled(false);
        }
    }*/
    
    public void Enable() {
        plugin.getConfig().set("GlobalyEnable", true);
        try {
            plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
    }
    
    /*public void Enable(String part) {
        if("all".equals(part)) {
            plugin.config.getVariables().getBiomes().getFreezing().setEnabled(true);
            plugin.config.getVariables().getBiomes().getExhausting().setEnabled(true);
            plugin.config.getVariables().getBiomes().getJungle().setEnabled(true);
            plugin.config.getVariables().getBiomes().getGlobal().setReplenishEnabled(true);
            plugin.config.getVariables().getBiomes().getGlobal().setThirstEnabled(true);
        } else if("exhausting".equals(part)) {
            plugin.config.getVariables().getBiomes().getExhausting().setEnabled(true);
        } else if("jungle".equals(part)) {
            plugin.config.getVariables().getBiomes().getJungle().setEnabled(true);
        } else if("freezing".equals(part)) {
            plugin.config.getVariables().getBiomes().getFreezing().setEnabled(true);
        } else if("thirst".equals(part)) {
            plugin.config.getVariables().getBiomes().getGlobal().setThirstEnabled(true);
        } else if("waterbottle".equals(part)) {
            plugin.config.getVariables().getBiomes().getGlobal().setReplenishEnabled(true);
        }
    }*/
    
    public boolean Language(String lang) {
        if(!plugin.localization.LangExists(lang)) {
            plugin.log.log(Level.INFO, "Language doesn't exists!");
            return false;
        }
        if(!plugin.localization.SetLanguage(lang)) {
            return false;
        }
        return true;
    }
}
