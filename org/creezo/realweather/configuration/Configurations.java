package org.creezo.realweather.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.creezo.realweather.RealWeather;

/**
 *
 * @author creezo
 */
public class Configurations {
    private FileConfiguration GlobalConf;
    private FileConfiguration ArmourConf;
    private RealWeather plugin;
    Configurations(RealWeather plugin, FileConfiguration GlobalConf, FileConfiguration ArmourConf) {
        this.GlobalConf = GlobalConf;
        this.ArmourConf = ArmourConf;
        this.plugin = plugin;
    }
    private Armors armors = new Armors();
    public Armors getArmors() {
        return armors;
    }

    public class Armors {
        
        public double[] getResistance(int itemID, String resistanceType) {
            if(RealWeather.isDebug()) RealWeather.log("Getting resistance.");
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
                if(RealWeather.isDebug()) RealWeather.log("Helmet resistance: "+ArmourConf.getDouble(type.get(itemID)+".Helmet."+resistanceType+"ResistanceFactor")+" "+type.get(itemID)+".Helmet."+resistanceType+"ResistanceFactor");
                return new double[] { ArmourConf.getDouble(type.get(itemID)+".Helmet."+resistanceType+"ResistanceFactor"), 1};
            } else if(type.containsKey(itemID-1)) {
                if(RealWeather.isDebug()) RealWeather.log("Chestplate resistance: "+ArmourConf.getDouble(type.get(itemID-1)+".Chestplate."+resistanceType+"ResistanceFactor")+" "+type.get(itemID-1)+".Chestplate."+resistanceType+"ResistanceFactor");
                return new double[] { ArmourConf.getDouble(type.get(itemID-1)+".Chestplate."+resistanceType+"ResistanceFactor"), 1};
            } else if(type.containsKey(itemID-2)) {
                if(RealWeather.isDebug()) RealWeather.log("Leggings resistance: "+ArmourConf.getDouble(type.get(itemID-2)+".Leggings."+resistanceType+"ResistanceFactor")+" "+type.get(itemID-2)+".Leggings."+resistanceType+"ResistanceFactor");
                return new double[] { ArmourConf.getDouble(type.get(itemID-2)+".Leggings."+resistanceType+"ResistanceFactor"), 1};
            } else if(type.containsKey(itemID-3)) {
                if(RealWeather.isDebug()) RealWeather.log("Boots resistance: "+ArmourConf.getDouble(type.get(itemID-3)+".Boots."+resistanceType+"ResistanceFactor")+" "+type.get(itemID-3)+".Boots."+resistanceType+"ResistanceFactor");
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
        private Global Global = new Global();
        public Global getGlobal() {
            return Global;
        }
        public class Global {
            public int getBiomeAverageTemp(String biome) {
                if(GlobalConf.contains("BiomesAverageTemp."+biome.toUpperCase())) {
                    return GlobalConf.getInt("BiomesAverageTemp."+biome.toUpperCase());
                } else {
                    RealWeather.log.log(Level.WARNING, "Biome "+biome.toUpperCase()+" not found. Returning 0 as average biome temperature.");
                    return 0;
                }
            }
            public int getBiomesWeatherTempModifier(String biome) {
                if(GlobalConf.contains("BiomesWeatherTempModifier."+biome.toUpperCase())) {
                    return GlobalConf.getInt("BiomesWeatherTempModifier."+biome.toUpperCase());
                } else {
                    RealWeather.log.log(Level.WARNING, "Biome "+biome.toUpperCase()+" not found. Returning 0 as rain/storm temperature modifier.");
                    return 0;
                }
            }
            public int getBiomeDayNightTempModifier(String time, String biome) {
                if(GlobalConf.contains("BiomeDayNightTempModifier."+biome.toUpperCase()+"."+time)) {
                    return GlobalConf.getInt("BiomeDayNightTempModifier."+biome.toUpperCase()+"."+time);
                } else {
                    RealWeather.log.log(Level.WARNING, "Biome "+biome.toUpperCase()+" not found. Returning 0 as "+time+" modifier temperature.");
                    return 0;
                }
            }
            public int getHeatCheckRadius() {
                return GlobalConf.getInt("HeatCheckRadius");
            }
            public int getPlayerHeat() {
                return GlobalConf.getInt("PlayerHeat");
            }
            public int getTopTemp() {
                return GlobalConf.getInt("MaxMapHeightTemperatureModifier");
            }
            public int getSeaLevel() {
                return GlobalConf.getInt("SeaLevel");
            }
            public int getBedTemperatureBonus() {
                return GlobalConf.getInt("BedTemperatureBonus");
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
    private static int checkDelay = 10;
    private int MaxPlayers;
    public int getCheckDelay(String GameDifficulty) {
        checkDelay = plugin.getConfig().getInt(GameDifficulty + ".CheckDelay");
        return plugin.getConfig().getInt(GameDifficulty + ".CheckDelay");
    }
    public static int getCheckDelay() {
        return checkDelay;
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
    public boolean isReportingEnabled() {
        return plugin.getConfig().getBoolean("ErrorReporting", true);
    }
    public String getReportName() {
        return plugin.getConfig().getString("ErrorReportingName", "Unknown");
    }
    public String getLanguage() {
        return plugin.getConfig().getString("Language", "eng");
    }
}