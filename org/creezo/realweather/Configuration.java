package org.creezo.realweather;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author creezo
 */
public class Configuration {
    private final File GlobalConfigFile;
    private final File FreezingConfigFile;
    private final File ExhaustingConfigFile;
    private final File JungleConfigFile;
    private final File ArmorConfigFile;
    private FileConfiguration FreezingConf;
    private FileConfiguration ExhaustingConf;
    private FileConfiguration JungleConf;
    private FileConfiguration GlobalConf;
    private FileConfiguration ArmorConf;
    private int GlobalConfigFileVersion = 3;
    private int FreezingConfigFileVersion = 1;
    private int ExhaustingConfigFileVersion = 1;
    private int JungleConfigFileVersion = 1;
    private int ArmorConfigFileVersion = 1;
    private int ConfigFileVersion = 3;
    
    private Configurations variables;
    public List<String> ArmorTypes = new ArrayList<String>();
    private List<String> ArmorPieces = new ArrayList<String>();
    
    public Configurations getVariables() {
        return variables;
    }
    private RealWeather plugin;

    public Configuration(RealWeather plugin) {
        this.plugin = plugin;
        this.FreezingConf = new YamlConfiguration();
        this.ExhaustingConf = new YamlConfiguration();
        this.JungleConf = new YamlConfiguration();
        this.GlobalConf = new YamlConfiguration();
        this.ArmorConf = new YamlConfiguration();
        this.GlobalConfigFile = new File(plugin.getDataFolder(), "biomes/Global.yml");
        this.FreezingConfigFile = new File(plugin.getDataFolder(), "biomes/Freezing.yml");
        this.ExhaustingConfigFile = new File(plugin.getDataFolder(), "biomes/Exhausting.yml");
        this.JungleConfigFile = new File(plugin.getDataFolder(), "biomes/Jungle.yml");
        this.ArmorConfigFile = new File(plugin.getDataFolder(), "armor.yml");
        
    }
    public void InitConfig() {
        plugin.log("Loading Configuration.");
        LoadAll();
        variables = new Configurations(plugin, FreezingConf, ExhaustingConf, JungleConf, GlobalConf, ArmorConf);
        try {
            plugin.getConfig().load("plugins/RealWeather/config.yml");
        } catch (FileNotFoundException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
        if(plugin.getConfig().getInt("fileVersion", 0) != ConfigFileVersion) {
            File oldFile = new File("plugins/RealWeather/config_" + plugin.getConfig().getInt("fileVersion", 0) + ".yml");
            plugin.log.log(Level.INFO, "[RealWeather] File version of config.yml is not supported by this version.");
            try {
                plugin.getConfig().save(oldFile);
                plugin.log.log(Level.INFO, "[RealWeather] config.yml version: " + plugin.getConfig().getInt("fileVersion", 0));
                plugin.log.log(Level.INFO, "[RealWeather] Required version: " + ConfigFileVersion);
                plugin.log.log(Level.INFO, "[RealWeather] Old config.yml saved.");
            } catch(IOException ex) {
                plugin.log.log(Level.INFO, "[RealWeather] Saving of old config.yml file failed. " + ex.getMessage());
            }
            File confFile = new File("plugins/RealWeather/config.yml");
            confFile.delete();
            copy(plugin.getResource("config.yml"), confFile);
            plugin.log("Configuration file copied.");
            try {
                plugin.getConfig().load("plugins/RealWeather/config.yml");
            } catch (FileNotFoundException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
        }
        if(!plugin.getConfig().contains("DebugMode")) plugin.getConfig().set("DebugMode", false);
        if(!plugin.getConfig().contains("DebugGlassBlocks")) plugin.getConfig().set("DebugGlassBlocks", false);
        if(!plugin.getConfig().contains("WorldName")) plugin.getConfig().createSection("WorldName");
        if(plugin.getConfig().isString("WorldName")) {
            variables.setGameDifficulty(plugin.getServer().getWorld(plugin.getConfig().getString("WorldName")).getDifficulty().name().toLowerCase());
            plugin.log((new StringBuilder()).append("Loaded difficulty ").append(variables.getGameDifficulty()).append(" from world ").append(plugin.getConfig().getString("WorldName")).toString());
        } else {
            variables.setGameDifficulty(plugin.getServer().getWorlds().get(0).getDifficulty().name().toLowerCase());
        }
        if(!plugin.getConfig().contains("GlobalyEnable")) plugin.getConfig().set("GlobalyEnable", true);
        String StartDelayDiff = variables.getGameDifficulty() + ".StartDelay";
        if(!plugin.getConfig().contains(StartDelayDiff)) plugin.getConfig().set(StartDelayDiff, (int) 20);
        String CheckDelayDiff = variables.getGameDifficulty() + ".CheckDelay";
        if(!plugin.getConfig().contains(CheckDelayDiff)) plugin.getConfig().set(CheckDelayDiff, (int) 20);
        if(!plugin.getConfig().contains("NumberOfChecksPerWarningMessage")) plugin.getConfig().set("NumberOfChecksPerWarningMessage", (int) 5);
        variables.setMaxPlayers(plugin.getServer().getMaxPlayers());
        if(!plugin.getConfig().contains("AffectedWorlds")) {
            List<String> list = new ArrayList<String>();
            list.add("world"); list.add("world_nether"); list.add("world_the_end");
            plugin.getConfig().set("AffectedWorlds", true);
        }
        if(!plugin.getConfig().contains("BroadcastForecast")) plugin.getConfig().set("BroadcastForecast", true);
        if(!plugin.getConfig().contains("CanChangeWeather")) plugin.getConfig().set("CanChangeWeather", true);
    }
        
    public boolean SaveAll() {
        try {
            FreezingConf.save(FreezingConfigFile);
            ExhaustingConf.save(ExhaustingConfigFile);
            JungleConf.save(JungleConfigFile);
            GlobalConf.save(GlobalConfigFile);
            ArmorConf.save(ArmorConfigFile);
            plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public void LoadAll() {
        if(!new File(plugin.getDataFolder(), "biomes").exists()) {
            GlobalConfigFile.getParentFile().mkdirs();
        }
        if(FreezingConfigFile.exists()) {
            try {
                FreezingConf.load(FreezingConfigFile);
            } catch (FileNotFoundException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
            if(FreezingConf.getInt("fileVersion", 0) != FreezingConfigFileVersion) {
                File oldFile = new File("plugins/RealWeather/biomes/Freezing_" + FreezingConf.getInt("fileVersion", 0) + ".yml");
                plugin.log.log(Level.INFO, "[RealWeather] File version of Freezing.yml is not supported by this version.");
                try {
                    FreezingConf.save(oldFile);
                    plugin.log.log(Level.INFO, "[RealWeather] Freezing.yml version: " + FreezingConf.getInt("fileVersion", 0));
                    plugin.log.log(Level.INFO, "[RealWeather] Required version: " + FreezingConfigFileVersion);
                    plugin.log.log(Level.INFO, "[RealWeather] Old Freezing.yml saved.");
                } catch(IOException ex) {
                    plugin.log.log(Level.INFO, "[RealWeather] Saving of old Freezing.yml file failed. " + ex.getMessage());
                }
                FreezingConfigFile.delete();
                copy(plugin.getResource("biomes/Freezing.yml"), FreezingConfigFile);
                plugin.log("Freezing configuration file copied.");
            } else {
                plugin.log("Freezing configuration file   OK.");
            }
            LoadFreezing(FreezingConfigFile);
        } else {
            copy(plugin.getResource("biomes/Freezing.yml"), FreezingConfigFile);
            plugin.log("Freezing configuration file copied.");
            LoadFreezing(FreezingConfigFile);
        }
        if(ExhaustingConfigFile.exists()) {
            try {
                ExhaustingConf.load(ExhaustingConfigFile);
            } catch (FileNotFoundException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
            if(ExhaustingConf.getInt("fileVersion", 0) != ExhaustingConfigFileVersion) {
                File oldFile = new File("plugins/RealWeather/biomes/Exhausting_" + ExhaustingConf.getInt("fileVersion", 0) + ".yml");
                plugin.log.log(Level.INFO, "[RealWeather] File version of Exhausting.yml is not supported by this version.");
                try {
                    ExhaustingConf.save(oldFile);
                    plugin.log.log(Level.INFO, "[RealWeather] Exhausting.yml version: " + ExhaustingConf.getInt("fileVersion", 0));
                    plugin.log.log(Level.INFO, "[RealWeather] Required version: " + ExhaustingConfigFileVersion);
                    plugin.log.log(Level.INFO, "[RealWeather] Old Exhausting.yml saved.");
                } catch(IOException ex) {
                    plugin.log.log(Level.INFO, "[RealWeather] Saving of old Exhausting.yml file failed. " + ex.getMessage());
                }
                ExhaustingConfigFile.delete();
                copy(plugin.getResource("biomes/Exhausting.yml"), ExhaustingConfigFile);
                plugin.log("Exhausting configuration file copied.");
            } else {
                plugin.log("Exhausting configuration file OK.");
            }
            LoadExhausting(ExhaustingConfigFile);
        } else {
            copy(plugin.getResource("biomes/Exhausting.yml"), ExhaustingConfigFile);
            plugin.log("Exhausting configuration file copied.");
            LoadExhausting(ExhaustingConfigFile);
        }
        if(JungleConfigFile.exists()) {
            try {
                JungleConf.load(JungleConfigFile);
            } catch (FileNotFoundException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
            if(JungleConf.getInt("fileVersion", 0) != JungleConfigFileVersion) {
                File oldFile = new File("plugins/RealWeather/biomes/Jungle_" + JungleConf.getInt("fileVersion", 0) + ".yml");
                plugin.log.log(Level.INFO, "[RealWeather] File version of Jungle.yml is not supported by this version.");
                try {
                    JungleConf.save(oldFile);
                    plugin.log.log(Level.INFO, "[RealWeather] Jungle.yml version: " + JungleConf.getInt("fileVersion", 0));
                    plugin.log.log(Level.INFO, "[RealWeather] Required version: " + JungleConfigFileVersion);
                    plugin.log.log(Level.INFO, "[RealWeather] Old Jungle.yml saved.");
                } catch(IOException ex) {
                    plugin.log.log(Level.INFO, "[RealWeather] Saving of old Jungle.yml file failed. " + ex.getMessage());
                }
                JungleConfigFile.delete();
                copy(plugin.getResource("biomes/Jungle.yml"), JungleConfigFile);
                plugin.log("Jungle configuration file copied.");
            } else {
                plugin.log("Jungle configuration file     OK.");
            }
            LoadJungle(JungleConfigFile);
        } else {
            copy(plugin.getResource("biomes/Jungle.yml"), JungleConfigFile);
            plugin.log("Jungle configuration file copied.");
            LoadJungle(JungleConfigFile);
        }
        if(GlobalConfigFile.exists()) {
            try {
                GlobalConf.load(GlobalConfigFile);
            } catch (FileNotFoundException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
            if(GlobalConf.getInt("fileVersion", 0) != GlobalConfigFileVersion) {
                File oldFile = new File("plugins/RealWeather/biomes/Global_" + GlobalConf.getInt("fileVersion", 0) + ".yml");
                plugin.log.log(Level.INFO, "[RealWeather] File version of Global.yml is not supported by this version.");
                try {
                    GlobalConf.save(oldFile);
                    plugin.log.log(Level.INFO, "[RealWeather] Global.yml version: " + GlobalConf.getInt("fileVersion", 0));
                    plugin.log.log(Level.INFO, "[RealWeather] Required version: " + GlobalConfigFileVersion);
                    plugin.log.log(Level.INFO, "[RealWeather] Old Global.yml saved.");
                } catch(IOException ex) {
                    plugin.log.log(Level.INFO, "[RealWeather] Saving of old Global.yml file failed. " + ex.getMessage());
                }
                GlobalConfigFile.delete();
                copy(plugin.getResource("biomes/Global.yml"), GlobalConfigFile);
                plugin.log("Global configuration file copied.");
            } else {
                plugin.log("Global configuration file     OK.");
            }
            LoadGlobal(GlobalConfigFile);
        } else {
            copy(plugin.getResource("biomes/Global.yml"), GlobalConfigFile);
            plugin.log("Global configuration file copied.");
            LoadGlobal(GlobalConfigFile);
        }
        if(ArmorConfigFile.exists()) {
            try {
                ArmorConf.load(ArmorConfigFile);
            } catch (FileNotFoundException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
            if(ArmorConf.getInt("fileVersion", 0) != ArmorConfigFileVersion) {
                File oldFile = new File("plugins/RealWeather/biomes/Armor_" + ArmorConf.getInt("fileVersion", 0) + ".yml");
                plugin.log.log(Level.INFO, "[RealWeather] File version of Armor.yml is not supported by this version.");
                try {
                    ArmorConf.save(oldFile);
                    plugin.log.log(Level.INFO, "[RealWeather] Armor.yml version: " + ArmorConf.getInt("fileVersion", 0));
                    plugin.log.log(Level.INFO, "[RealWeather] Required version: " + ArmorConfigFileVersion);
                    plugin.log.log(Level.INFO, "[RealWeather] Old Armor.yml saved.");
                } catch(IOException ex) {
                    plugin.log.log(Level.INFO, "[RealWeather] Saving of old Armor.yml file failed. " + ex.getMessage());
                }
                ArmorConfigFile.delete();
                copy(plugin.getResource("biomes/Armor.yml"), ArmorConfigFile);
                plugin.log("Armor configuration file copied.");
            } else {
                plugin.log("Armor configuration file      OK.");
            }
            LoadArmor(ArmorConfigFile);
        } else {
            copy(plugin.getResource("armor.yml"), ArmorConfigFile);
            plugin.log("Armor configuration file copied.");
            LoadArmor(ArmorConfigFile);
        }
    }
    
    private void LoadFreezing(File CFile) {
        try {
            FreezingConf.load(CFile);
        } catch (FileNotFoundException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
        if(!FreezingConf.contains("enable")) FreezingConf.set("enable", false);
        if(!FreezingConf.contains("CheckRadius")) FreezingConf.set("CheckRadius", (int) 2);
        if(!FreezingConf.contains("TempPeak")) FreezingConf.set("TempPeak", (int) 50);
        if(!FreezingConf.contains("CanKillPlayer")) FreezingConf.set("CanKillPlayer", false);
        if(!FreezingConf.contains("PlayerIceBlock")) FreezingConf.set("PlayerIceBlock", true);
        if(!FreezingConf.contains("InitialTemperature")) FreezingConf.set("InitialTemperature", (int) 0);
        if(!FreezingConf.contains("HouseRecognizer")) FreezingConf.set("HouseRecognizer", "cross");
        if(!FreezingConf.contains("PlayerDamage")) FreezingConf.set("PlayerDamage", (int) 4);
    }
    private void LoadExhausting(File CFile) {
        try {
            ExhaustingConf.load(CFile);
        } catch (FileNotFoundException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
        if(!ExhaustingConf.contains("enable")) ExhaustingConf.set("enable", true);
        if(!ExhaustingConf.contains("HouseRecognizer")) ExhaustingConf.set("HouseRecognizer", "simple");
        if(!ExhaustingConf.contains("StaminaLost")) {
            List<Float> list = new ArrayList<Float>();
            list.add(0.05F);
            ExhaustingConf.set("StaminaLost", list);
        }
        if(!ExhaustingConf.contains("NumberOfCheckPerFoodLost")) ExhaustingConf.set("enaNumberOfCheckPerFoodLostle", (int) 5);
    }
    private void LoadJungle(File CFile) {
        try {
            JungleConf.load(CFile);
        } catch (FileNotFoundException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
        if(!JungleConf.contains("enable")) JungleConf.set("enable", true);
        if(!JungleConf.contains("InsectJumpRange")) JungleConf.set("InsectJumpRange", 3);
        if(!JungleConf.contains("ChanceMultiplier")) JungleConf.set("ChanceMultiplier", 1);
        if(!JungleConf.contains("DefaultNegativeEffectDuration")) JungleConf.set("DefaultNegativeEffectDuration", 60);
        if(!JungleConf.contains("PoisonDuration")) JungleConf.set("PoisonDuration", 8);
        if(!JungleConf.contains("SilverFishChance")) JungleConf.set("SilverFishChance", 5);
        if(!JungleConf.contains("SilverFishPoisonChance")) JungleConf.set("SilverFishPoisonChance", 5);
    }
    private void LoadGlobal(File CFile) {
        try {
            GlobalConf.load(CFile);
        } catch (FileNotFoundException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
        if(!GlobalConf.contains("thirst.enable")) GlobalConf.set("thirst.enable", true);
        if(!GlobalConf.contains("thirst.StaminaLost")) {
            List<Float> list = new ArrayList<Float>();
            list.add(0.03F);
            GlobalConf.set("thirst.StaminaLost", list);
        }
        if(!GlobalConf.contains("thirst.AffectedWorlds")) {
            List<String> list = new ArrayList<String>();
            list.add("world"); list.add("world_nether"); list.add("world_the_end");
            GlobalConf.set("thirst.AffectedWorlds", list);
        }
        if(!GlobalConf.contains("staminareplenish.enable")) GlobalConf.set("staminareplenish.enable", true);
        if(!GlobalConf.contains("HeatCheckRadius")) GlobalConf.set("HeatCheckRadius", (int) 3);
        if(!GlobalConf.contains("PlayerHeat")) GlobalConf.set("PlayerHeat", (int) 1);
        if(!GlobalConf.contains("FreezeUnder")) GlobalConf.set("FreezeUnder", (int) 7);
        if(!GlobalConf.contains("OverheatOver")) GlobalConf.set("OverheatOver", (int) 30);
        if(!GlobalConf.contains("SeaLevel")) GlobalConf.set("SeaLevel", (int) 62);
        if(!GlobalConf.contains("ShowersRainChance")) GlobalConf.set("ShowersRainChance", (int) 25);
        if(!GlobalConf.contains("MaxMapHeightTemperatureModifier")) GlobalConf.set("MaxMapHeightTemperatureModifier", (int) -10);
        if(!GlobalConf.contains("staminareplenish.StaminaReplenishWaterBottle")) {
            List<Float> list = new ArrayList<Float>();
            list.add(1.00F);
            GlobalConf.set("staminareplenish.StaminaReplenishWaterBottle", list);
        }
        if(!GlobalConf.contains("BiomesAverageTemp")) {
            GlobalConf.createSection("BiomesAverageTemp");
            plugin.log.log(Level.SEVERE, "Biomes Average Temperatures are missing in Global.yml");
        }
        loadHeatSources();
    }
    
    public void loadHeatSources() {
        plugin.HeatSources.clear();
        try {
            for (String tempSource : GlobalConf.getConfigurationSection("HeatSources").getKeys(false)) {
                if(Material.getMaterial(tempSource) != null) {
                    if(!GlobalConf.isSet("HeatSources."+tempSource)) GlobalConf.set("HeatSources."+tempSource, (double) 0);
                    plugin.HeatSources.put(Material.getMaterial(tempSource), GlobalConf.getDouble("HeatSources."+tempSource));
                }
            }
        } catch(NullPointerException ex) {
            plugin.log.log(Level.SEVERE, "NPE Error in loading 'HeatSources'. Make sure it is configured in Global.yml!");
        }
        plugin.HeatInHand.clear();
        try {
            for (String tempSource : GlobalConf.getConfigurationSection("HeatInHand").getKeys(false)) {
                if(Material.getMaterial(tempSource) != null) {
                    if(!GlobalConf.isSet("HeatInHand."+tempSource)) GlobalConf.set("HeatInHand."+tempSource, (double) 0);
                    plugin.HeatInHand.put(Material.getMaterial(tempSource), GlobalConf.getDouble("HeatSources."+tempSource));
                }
            }
        } catch(NullPointerException ex) {
            plugin.log.log(Level.SEVERE, "NPE Error in loading 'HeatInHand'. Make sure it is configured in Global.yml!");
        }
    }
    
    private void LoadArmor(File CFile) {
        try {
            ArmorConf.load(CFile);
        } catch (FileNotFoundException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
        ArmorTypes.add("Leather");
        ArmorTypes.add("Iron");
        ArmorTypes.add("Gold");
        ArmorTypes.add("Diamond");
        ArmorTypes.add("Chain");
        ArmorTypes.add("Other");
        ArmorPieces.add("Boots");
        ArmorPieces.add("Chestplate");
        ArmorPieces.add("Helmet");
        ArmorPieces.add("Leggings");
        for (String type : ArmorTypes) {
            for (String piece : ArmorPieces) {
                if(!ArmorConf.contains(type+"."+piece+".FrostResistanceFactor")) ArmorConf.set(type+"."+piece+".FrostResistanceFactor", (double)1d);
                if(!ArmorConf.contains(type+"."+piece+".HeatResistanceFactor")) ArmorConf.set(type+"."+piece+".HeatResistanceFactor", (double)1d);
            }
        }
        if(!ArmorConf.contains("Pumpkin.FrostResistanceFactor")) ArmorConf.set("Pumpkin.FrostResistanceFactor", (double)1.1d);
        if(!ArmorConf.contains("Pumpkin.HeatResistanceFactor")) ArmorConf.set("Pumpkin.HeatResistanceFactor", (double)1.1d);
    }

    private void copy(InputStream input, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=input.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            input.close();
        } catch (Exception e) {
            plugin.log.log(Level.WARNING, null, e);
        }
    }
}
