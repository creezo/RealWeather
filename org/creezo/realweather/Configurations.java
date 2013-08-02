package org.creezo.realweather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author creezo
 */
public class Configurations {
    private FileConfiguration FreezingConf;
    private FileConfiguration ExhaustingConf;
    private FileConfiguration JungleConf;
    private FileConfiguration GlobalConf;
    private FileConfiguration ArmourConf;
    private RealWeather plugin;
    public Configurations(RealWeather plugin, FileConfiguration FreezingConf, FileConfiguration ExhaustingConf, FileConfiguration JungleConf, FileConfiguration GlobalConf, FileConfiguration ArmourConf) {
        this.FreezingConf = FreezingConf;
        this.ExhaustingConf = ExhaustingConf;
        this.JungleConf = JungleConf;
        this.GlobalConf = GlobalConf;
        this.ArmourConf = ArmourConf;
        this.plugin = plugin;
    }
    private Armours Armours = new Armours();
    public Armours getArmours() {
        return Armours;
    }

    public class Armours {
        
        public double[] getResistance(int itemID, String resistanceType) {
            if(plugin.Config.getVariables().isDebugMode()) plugin.log("Getting resistance.");
            double[] factor = {1,0};
            HashMap<Integer, String> type = new HashMap<Integer, String>();
            type.put(298, "Leather");
            type.put(302, "Chain");
            type.put(306, "Iron");
            type.put(310, "Diamond");
            type.put(314, "Gold");
            type.put(86, "Pumpkin");
            if(itemID==86) {
                return new double[] { ArmourConf.getDouble(type.get(itemID)+"."+resistanceType+"ResistanceFactor"), 1};
            }
            if(type.containsKey(itemID)) {
                if(plugin.Config.getVariables().isDebugMode()) plugin.log("Helmet resistance: "+ArmourConf.getDouble(type.get(itemID)+".Helmet."+resistanceType+"ResistanceFactor")+" "+type.get(itemID)+".Helmet."+resistanceType+"ResistanceFactor");
                return new double[] { ArmourConf.getDouble(type.get(itemID)+".Helmet."+resistanceType+"ResistanceFactor"), 1};
            } else if(type.containsKey(itemID-1)) {
                if(plugin.Config.getVariables().isDebugMode()) plugin.log("Chestplate resistance: "+ArmourConf.getDouble(type.get(itemID-1)+".Chestplate."+resistanceType+"ResistanceFactor")+" "+type.get(itemID-1)+".Chestplate."+resistanceType+"ResistanceFactor");
                return new double[] { ArmourConf.getDouble(type.get(itemID-1)+".Chestplate."+resistanceType+"ResistanceFactor"), 1};
            } else if(type.containsKey(itemID-2)) {
                if(plugin.Config.getVariables().isDebugMode()) plugin.log("Leggings resistance: "+ArmourConf.getDouble(type.get(itemID-2)+".Leggings."+resistanceType+"ResistanceFactor")+" "+type.get(itemID-2)+".Leggings."+resistanceType+"ResistanceFactor");
                return new double[] { ArmourConf.getDouble(type.get(itemID-2)+".Leggings."+resistanceType+"ResistanceFactor"), 1};
            } else if(type.containsKey(itemID-3)) {
                if(plugin.Config.getVariables().isDebugMode()) plugin.log("Boots resistance: "+ArmourConf.getDouble(type.get(itemID-3)+".Boots."+resistanceType+"ResistanceFactor")+" "+type.get(itemID-3)+".Boots."+resistanceType+"ResistanceFactor");
                return new double[] { ArmourConf.getDouble(type.get(itemID-3)+".Boots."+resistanceType+"ResistanceFactor"), 1};
            }
            return factor;
        }
    }
    private Biomes Biomes = new Biomes();
    public Biomes getBiomes() {
        return Biomes;
    }
    public class Biomes {
        private Freezing Freezing = new Freezing();
        public Freezing getFreezing() {
            return Freezing;
        }
        public class Freezing {
            public void setFreezingKill(boolean FreezingKill) {
                FreezingConf.set("CanKillPlayer", FreezingKill);
            }
            public void setEnabled(boolean FreezingEnabled) {
                FreezingConf.set("enable", FreezingEnabled);
            }
            public void setMissingArmorDamage(int Damage) {
                FreezingConf.set("PlayerDamage", Damage);
            }
            public void setIceBlock(boolean PlayerIceBlock) {
                FreezingConf.set("PlayerIceBlock", PlayerIceBlock);
            }
            public void setHouseRecoFreezing(String HouseRecoFreezing) {
                FreezingConf.set("HouseRecognizer", HouseRecoFreezing);
            }
            public void setCheckRadius(int CheckRadius) {
                FreezingConf.set("CheckRadius", CheckRadius);
            }
            public boolean isFreezingKilliing() {
                return FreezingConf.getBoolean("CanKillPlayer");
            }
            public boolean isEnabled() {
                return FreezingConf.getBoolean("enable");
            }
            public int getDamage() {
                return FreezingConf.getInt("PlayerDamage");
            }
            public boolean getPlayerIceBlock() {
                return FreezingConf.getBoolean("PlayerIceBlock", true);
            }
            public String getHouseRecoFreezing() {
                return FreezingConf.getString("HouseRecognizer");
            }
            public int getCheckRadius() {
                return FreezingConf.getInt("CheckRadius");
            }
        }
        private Exhausting Exhausting = new Exhausting();
        public Exhausting getExhausting() {
            return Exhausting;
        }
        public class Exhausting {
            public int getChecksPerFoodDecrease() {
                return ExhaustingConf.getInt("NumberOfCheckPerFoodLost");
            }
            public float getStaminaLost() {
                return ExhaustingConf.getFloatList("StaminaLost").get(0);
            }
            public String getHouseRecognizer() {
                return ExhaustingConf.getString("HouseRecognizer");
            }
            public boolean isEnabled() {
                return ExhaustingConf.getBoolean("enable");
            }
            public void setChecksPerFoodDecrease(int ChecksPerFoodDecrease) {
                ExhaustingConf.set("NumberOfCheckPerFoodLost", ChecksPerFoodDecrease);
            }
            public void setEnabled(boolean ExhaustingEnabled) {
                ExhaustingConf.set("enable", ExhaustingEnabled);
            }
            public void setStaminaLost(float ExhaustingStaminaLostHelmet) {
                List<Float> list = new ArrayList<Float>();
                list.add(ExhaustingStaminaLostHelmet);
                ExhaustingConf.set("StaminaLost", list);
            }
            public void setHouseRecognizer(String HouseRecoExhausting) {
                ExhaustingConf.set("HouseRecognizer", HouseRecoExhausting);
            }
        }
        private Jungle Jungle = new Jungle();
        public Jungle getJungle() {
            return Jungle;
        }
        public class Jungle {
            public boolean isEnabled() {
                return JungleConf.getBoolean("enable");
            }
            public void setEnabled(boolean JungleEnabled) {
                JungleConf.set("enable", JungleEnabled);
            }
            public int getInsectJumpRange() {
                return JungleConf.getInt("InsectJumpRange");
            }
            public void setInsectJumpRange(int InsectJumpRange) {
                JungleConf.set("InsectJumpRange", InsectJumpRange);
            }
            public int getChanceMultiplier() {
                return JungleConf.getInt("ChanceMultiplier");
            }
            public void setChanceMultiplier(int ChanceMultiplier) {
                JungleConf.set("ChanceMultiplier", ChanceMultiplier);
            }
            public int getInsectBiteDuration() {
                return JungleConf.getInt("DefaultNegativeEffectDuration");
            }
            public void setInsectBiteDuration(int InsectBiteDuration) {
                JungleConf.set("DefaultNegativeEffectDuration", InsectBiteDuration);
            }
            public int getInsectPoisonDuration() {
                return JungleConf.getInt("PoisonDuration");
            }
            public void setInsectPoisonDuration(int PoisonDuration) {
                JungleConf.set("PoisonDuration", PoisonDuration);
            }
            public int getSilverFishChance() {
                return JungleConf.getInt("SilverFishChance");
            }
            public void setSilverFishChance(int SilverFishChance) {
                JungleConf.set("SilverFishChance", SilverFishChance);
            }
            public int getSilverFishPoisonChance() {
                return JungleConf.getInt("SilverFishPoisonChance");
            }
            public void setSilverFishPoisonChance(int SilverFishPoisonChance) {
                JungleConf.set("SilverFishPoisonChance", SilverFishPoisonChance);
            }
        }
        private Global Global = new Global();
        public Global getGlobal() {
            return Global;
        }
        public class Global {
            public List<String> getThirstAllowedWorlds() {
                return GlobalConf.getStringList("thirst.AffectedWorlds");
            }
            public float getThirstStaminaLost() {
                return GlobalConf.getFloatList("thirst.StaminaLost").get(0);
            }
            public boolean isThirstEnabled() {
                return GlobalConf.getBoolean("thirst.enable");
            }
            public boolean isReplenishEnabled() {
                return GlobalConf.getBoolean("staminareplenish.enable");
            }
            public float getStaminaReplenishAmount() {
                return GlobalConf.getFloatList("staminareplenish.StaminaReplenishWaterBottle").get(0);
            }
            public int getBiomeAverageTemp(String biome) {
                if(GlobalConf.contains("BiomesAverageTemp."+biome.toUpperCase())) {
                    return GlobalConf.getInt("BiomesAverageTemp."+biome.toUpperCase());
                } else {
                    plugin.log.log(Level.WARNING, "Biome "+biome.toUpperCase()+" not found. Returning 0 as average biome temperature.");
                    return 0;
                }
            }
            public int getBiomesWeatherTempModifier(String biome) {
                if(GlobalConf.contains("BiomesWeatherTempModifier."+biome.toUpperCase())) {
                    return GlobalConf.getInt("BiomesWeatherTempModifier."+biome.toUpperCase());
                } else {
                    plugin.log.log(Level.WARNING, "Biome "+biome.toUpperCase()+" not found. Returning 0 as rain/storm temperature modifier.");
                    return 0;
                }
            }
            public int getBiomeDayNightTempModifier(String time, String biome) {
                if(GlobalConf.contains("BiomeDayNightTempModifier."+biome.toUpperCase()+"."+time)) {
                    return GlobalConf.getInt("BiomeDayNightTempModifier."+biome.toUpperCase()+"."+time);
                } else {
                    plugin.log.log(Level.WARNING, "Biome "+biome.toUpperCase()+" not found. Returning 0 as "+time+" modifier temperature.");
                    return 0;
                }
            }
            public int getHeatCheckRadius() {
                return GlobalConf.getInt("HeatCheckRadius");
            }
            public int getPlayerHeat() {
                return GlobalConf.getInt("PlayerHeat");
            }
            public int getFreezeUnder() {
                return GlobalConf.getInt("FreezeUnder");
            }
            public int getOverheatOver() {
                return GlobalConf.getInt("OverheatOver");
            }
            public int getTopTemp() {
                return GlobalConf.getInt("MaxMapHeightTemperatureModifier");
            }
            public int getSeaLevel() {
                return GlobalConf.getInt("SeaLevel");
            }
            public int getShowersRainChance() {
                return GlobalConf.getInt("ShowersRainChance");
            }
            public void setThirstEnabled(boolean ThirstEnable) {
                GlobalConf.set("thirst.enable", ThirstEnable);
            }
            public void setReplenishEnabled(boolean ReplenishEnabled) {
                GlobalConf.set("staminareplenish.enable", ReplenishEnabled);
            }
            public void setThirstAllowedWorlds(List<String> ThirstAllowedWorlds) {
                GlobalConf.set("thirst.AffectedWorlds", ThirstAllowedWorlds);
            }
            public void setThirstStaminaLost(float ThirstStaminaLost) {
                List<Float> list = new ArrayList<Float>();
                list.add(ThirstStaminaLost);
                GlobalConf.set("thirst.StaminaLost", list);
            }
            public void setStaminaReplenishAmount(float Amount) {
                List<Float> list = new ArrayList<Float>();
                list.add(Amount);
                GlobalConf.set("staminareplenish.StaminaReplenishWaterBottle", list);
            }
            public void setBiomeAverageTemp(String biome, int num) {
                GlobalConf.set("BiomesAverageTemp."+biome.toUpperCase(), num);
            }
            public void setBiomesWeatherTempModifier(String biome, int num) {
                GlobalConf.set("BiomesWeatherTempModifier."+biome.toUpperCase(), num);
            }
            public void setBiomeDayNightTempModifier(String time, String biome, int num) {
                GlobalConf.set("BiomeDayNightTempModifier."+biome.toUpperCase()+"."+time, num);
            }
            public void setHeatCheckRadius(int HeatCheckRadius) {
                GlobalConf.set("HeatCheckRadius", HeatCheckRadius);
            }
            public void setPlayerHeat(int heat) {
                GlobalConf.set("PlayerHeat", heat);
            }
            public void setFreezeUnder(int temp) {
                GlobalConf.set("FreezeUnder", temp);
            }
            public void setOverheatOver(int temp) {
                GlobalConf.set("OverheatOver", temp);
            }
            public void setTopTemp(int temp) {
                GlobalConf.set("MaxMapHeightTemperatureModifier", temp);
            }
            public void setSeaLevel(int level) {
                GlobalConf.set("SeaLevel", level);
            }
            public void setShowersRainChance(int percentage) {
                GlobalConf.set("ShowersRainChance", percentage);
            }
            public void setHeatSource(String source, double power) {
                GlobalConf.set("HeatSources."+source.toUpperCase(), power);
            }
            public void setHeatInHand(String source, double power) {
                GlobalConf.set("HeatInHand."+source.toUpperCase(), power);
            }
            public boolean isTorchesFading() {
                return GlobalConf.getBoolean("TorchesFading", true);
            }
            public void setTorchesFading(boolean state) {
                GlobalConf.set("TorchesFading", state);
            }
        }
    }
    private String GameDifficulty = "peaceful";
    private int MaxPlayers;
    /*private List<String> AllowedWorlds = new ArrayList();*/
    public int getCheckDelay(String GameDifficulty) {
        return plugin.getConfig().getInt(GameDifficulty + ".CheckDelay");
    }
    public boolean isGlobalyEnable() {
        return plugin.getConfig().getBoolean("GlobalyEnable");
    }
    public String getGameDifficulty() {
        return GameDifficulty;
    }
    public int getMaxPlayers() {
        return MaxPlayers;
    }
    public int getMessageDelay() {
        return plugin.getConfig().getInt("NumberOfChecksPerWarningMessage");
    }
    public int getStartDelay(String GameDifficulty) {
        return plugin.getConfig().getInt(GameDifficulty + ".StartDelay");
    }
    public List<String> getAllowedWorlds() {
        return plugin.getConfig().getStringList("AffectedWorlds");
    }
    public boolean isDebugGlassBlocks() {
        return plugin.getConfig().getBoolean("DebugGlassBlocks");
    }
    public boolean isDebugMode() {
        return plugin.getConfig().getBoolean("DebugMode");
    }
    public void setGlobalyEnabled(boolean state) {
        plugin.getConfig().set("GlobalyEnable", state);
    }
    public void setCheckDelay(int CheckDelay, String GameDifficulty) {
        plugin.getConfig().set(GameDifficulty + ".CheckDelay", CheckDelay);
    }
    public void setDebugGlassBlocks(boolean DebugGlassBlocks) {
        plugin.getConfig().set("DebugGlassBlocks", DebugGlassBlocks);
    }
    public void setDebugMode(boolean DebugMode) {
        plugin.getConfig().set("DebugMode", DebugMode);
    }
    public void setGameDifficulty(String GameDifficulty) {
        this.GameDifficulty = GameDifficulty;
    }
    public void setMaxPlayers(int MaxPlayers) {
        this.MaxPlayers = MaxPlayers;
    }
    public void setMessageDelay(int MessageDelay) {
        plugin.getConfig().set("NumberOfChecksPerWarningMessage", MessageDelay);
    }
    public void setStartDelay(int StartDelay, String GameDifficulty) {
        plugin.getConfig().set(GameDifficulty + ".StartDelay", StartDelay);
    }
    public void setAllowedWorlds(List<String> AllowedWorlds) {
        plugin.getConfig().set("AffectedWorlds", AllowedWorlds);
    }
    public boolean isWeatherChangeEnable() {
        return plugin.getConfig().getBoolean("CanChangeWeather", true);
    }
    public boolean isWeatherBroadcastEnable() {
        return plugin.getConfig().getBoolean("BroadcastForecast", true);
    }
}