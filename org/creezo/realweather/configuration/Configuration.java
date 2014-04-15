package org.creezo.realweather.configuration;

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
import org.creezo.realweather.RealWeather;

/**
 *
 * @author creezo
 */
public class Configuration {

    private final File GlobalConfigFile;
    private final File ArmorConfigFile;
    private final FileConfiguration GlobalConf;
    private final FileConfiguration ArmorConf;
    private final int GlobalConfigFileVersion = 5;
    private final int ArmorConfigFileVersion = 1;
    private final int ConfigFileVersion = 3;
    private Configurations variables;
    public List<String> ArmorTypes = new ArrayList<String>();
    private List<String> ArmorPieces = new ArrayList<String>();

    public Configurations getVariables() {
        return variables;
    }
    private RealWeather plugin;

    public Configuration(RealWeather plugin) {
        this.plugin = plugin;
        this.GlobalConf = new YamlConfiguration();
        this.ArmorConf = new YamlConfiguration();
        this.GlobalConfigFile = new File(plugin.getDataFolder(), "Global.yml");
        this.ArmorConfigFile = new File(plugin.getDataFolder(), "armor.yml");

    }

    public void initConfig() {
        RealWeather.log("Loading Configuration.");
        LoadAll();
        variables = new Configurations(plugin, GlobalConf, ArmorConf);
        try {
            plugin.getConfig().load(new File(plugin.getDataFolder(), "config.yml"));
        } catch (FileNotFoundException ex) {
            RealWeather.log.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            RealWeather.log.log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            RealWeather.log.log(Level.SEVERE, null, ex);
        }
        if (plugin.getConfig().getInt("fileVersion", 0) != ConfigFileVersion) {
            File oldFile = new File(plugin.getDataFolder(), "config_" + plugin.getConfig().getInt("fileVersion", 0) + ".yml");
            RealWeather.log.log(Level.INFO, "[RealWeather] File version of config.yml is not supported by this version.");
            try {
                plugin.getConfig().save(oldFile);
                RealWeather.log("[RealWeather] config.yml version: " + plugin.getConfig().getInt("fileVersion", 0));
                RealWeather.log("[RealWeather] Required version: " + ConfigFileVersion);
                RealWeather.log("[RealWeather] Old config.yml saved.");
            } catch (IOException ex) {
                RealWeather.log("[RealWeather] Saving of old config.yml file failed. " + ex.getMessage());
            }
            File confFile = new File(plugin.getDataFolder(), "config.yml");
            confFile.delete();
            copy(plugin.getResource("config.yml"), confFile);
            RealWeather.log("Configuration file copied.");
            try {
                plugin.getConfig().load(new File(plugin.getDataFolder(), "config.yml"));
            } catch (FileNotFoundException ex) {
                RealWeather.log.log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                RealWeather.log.log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                RealWeather.log.log(Level.SEVERE, null, ex);
            }
        }
        if (!plugin.getConfig().contains("DebugMode")) {
            plugin.getConfig().set("DebugMode", false);
        }
        if (!plugin.getConfig().contains("ErrorReporting")) {
            plugin.getConfig().set("ErrorReporting", true);
        }
        if (!plugin.getConfig().contains("ErrorReportingName")) {
            plugin.getConfig().set("ErrorReportingName", "Unknown");
        }
        if (!plugin.getConfig().contains("DebugGlassBlocks")) {
            plugin.getConfig().set("DebugGlassBlocks", false);
        }
        if (!plugin.getConfig().contains("WorldName")) {
            plugin.getConfig().createSection("WorldName");
        }
        if (plugin.getConfig().isString("WorldName")) {
            variables.setGameDifficulty(plugin.getServer().getWorld(plugin.getConfig().getString("WorldName")).getDifficulty().name().toLowerCase());
            RealWeather.log((new StringBuilder()).append("Loaded difficulty ").append(variables.getGameDifficulty()).append(" from world ").append(plugin.getConfig().getString("WorldName")).toString());
        } else {
            variables.setGameDifficulty(plugin.getServer().getWorlds().get(0).getDifficulty().name().toLowerCase());
        }
        if (!plugin.getConfig().contains("GlobalyEnable")) {
            plugin.getConfig().set("GlobalyEnable", true);
        }
        String StartDelayDiff = variables.getGameDifficulty() + ".StartDelay";
        if (!plugin.getConfig().contains(StartDelayDiff)) {
            plugin.getConfig().set(StartDelayDiff, (int) 20);
        }
        String CheckDelayDiff = variables.getGameDifficulty() + ".CheckDelay";
        if (!plugin.getConfig().contains(CheckDelayDiff)) {
            plugin.getConfig().set(CheckDelayDiff, (int) 20);
        }
        if (!plugin.getConfig().contains("NumberOfChecksPerWarningMessage")) {
            plugin.getConfig().set("NumberOfChecksPerWarningMessage", (int) 5);
        }
        variables.setMaxPlayers(plugin.getServer().getMaxPlayers());
        if (!plugin.getConfig().contains("AffectedWorlds")) {
            List<String> list = new ArrayList<String>();
            list.add("world");
            list.add("world_nether");
            list.add("world_the_end");
            plugin.getConfig().set("AffectedWorlds", list);
        }
        if (!plugin.getConfig().contains("Language")) {
            plugin.getConfig().set("Language", "eng");
        }
    }

    public boolean saveAll() {
        try {
            GlobalConf.save(GlobalConfigFile);
            ArmorConf.save(ArmorConfigFile);
            plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            RealWeather.log.log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public void LoadAll() {
        if (!new File(plugin.getDataFolder(), "biomes").exists()) {
            GlobalConfigFile.getParentFile().mkdirs();
        }
        if (GlobalConfigFile.exists()) {
            try {
                GlobalConf.load(GlobalConfigFile);
            } catch (FileNotFoundException ex) {
                RealWeather.log.log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                RealWeather.log.log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                RealWeather.log.log(Level.SEVERE, null, ex);
            }
            if (GlobalConf.getInt("fileVersion", 0) != GlobalConfigFileVersion) {
                File oldFile = new File(plugin.getDataFolder(), "Global_" + GlobalConf.getInt("fileVersion", 0) + ".yml");
                RealWeather.log("[RealWeather] File version of Global.yml is not supported by this version.");
                try {
                    GlobalConf.save(oldFile);
                    RealWeather.log("[RealWeather] Global.yml version: " + GlobalConf.getInt("fileVersion", 0));
                    RealWeather.log("[RealWeather] Required version: " + GlobalConfigFileVersion);
                    RealWeather.log("[RealWeather] Old Global.yml saved.");
                } catch (IOException ex) {
                    RealWeather.log("[RealWeather] Saving of old Global.yml file failed. " + ex.getMessage());
                }
                GlobalConfigFile.delete();
                copy(plugin.getResource("Global.yml"), GlobalConfigFile);
                RealWeather.log("Global configuration file copied.");
            } else {
                RealWeather.log("Global configuration file     OK.");
            }
            LoadGlobal(GlobalConfigFile);
        } else {
            copy(plugin.getResource("Global.yml"), GlobalConfigFile);
            RealWeather.log("Global configuration file copied.");
            LoadGlobal(GlobalConfigFile);
        }
        if (ArmorConfigFile.exists()) {
            try {
                ArmorConf.load(ArmorConfigFile);
            } catch (FileNotFoundException ex) {
                RealWeather.log.log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                RealWeather.log.log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                RealWeather.log.log(Level.SEVERE, null, ex);
            }
            if (ArmorConf.getInt("fileVersion", 0) != ArmorConfigFileVersion) {
                File oldFile = new File(plugin.getDataFolder(), "armor_" + ArmorConf.getInt("fileVersion", 0) + ".yml");
                RealWeather.log("[RealWeather] File version of armor.yml is not supported by this version.");
                try {
                    ArmorConf.save(oldFile);
                    RealWeather.log("[RealWeather] armor.yml version: " + ArmorConf.getInt("fileVersion", 0));
                    RealWeather.log("[RealWeather] Required version: " + ArmorConfigFileVersion);
                    RealWeather.log("[RealWeather] Old armor.yml saved.");
                } catch (IOException ex) {
                    RealWeather.log("[RealWeather] Saving of old armor.yml file failed. " + ex.getMessage());
                }
                ArmorConfigFile.delete();
                copy(plugin.getResource("armor.yml"), ArmorConfigFile);
                RealWeather.log("Armor configuration file copied.");
            } else {
                RealWeather.log("Armor configuration file      OK.");
            }
            LoadArmor(ArmorConfigFile);
        } else {
            copy(plugin.getResource("armor.yml"), ArmorConfigFile);
            RealWeather.log("Armor configuration file copied.");
            LoadArmor(ArmorConfigFile);
        }
    }

    private void LoadGlobal(File CFile) {
        try {
            GlobalConf.load(CFile);
        } catch (FileNotFoundException ex) {
            RealWeather.log.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            RealWeather.log.log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            RealWeather.log.log(Level.SEVERE, null, ex);
        }
        if (!GlobalConf.contains("HeatCheckRadius")) {
            GlobalConf.set("HeatCheckRadius", (int) 3);
        }
        if (!GlobalConf.contains("PlayerHeat")) {
            GlobalConf.set("PlayerHeat", (int) 1);
        }
        if (!GlobalConf.contains("SeaLevel")) {
            GlobalConf.set("SeaLevel", (int) 62);
        }
        if (!GlobalConf.contains("ShowersRainChance")) {
            GlobalConf.set("ShowersRainChance", (int) 25);
        }
        if (!GlobalConf.contains("BedTemperatureBonus")) {
            GlobalConf.set("BedTemperatureBonus", (int) 10);
        }
        if (!GlobalConf.contains("MaxMapHeightTemperatureModifier")) {
            GlobalConf.set("MaxMapHeightTemperatureModifier", (int) -10);
        }
        if (!GlobalConf.contains("BiomesAverageTemp")) {
            GlobalConf.createSection("BiomesAverageTemp");
            RealWeather.log.log(Level.SEVERE, "Biomes Average Temperatures are missing in Global.yml");
        }
        loadHeatSources();
    }

    public void loadHeatSources() {
        try {
            plugin.heatSources.clear();
            try {
                for (String tempSource : GlobalConf.getConfigurationSection("HeatSources").getKeys(false)) {
                    if (Material.getMaterial(tempSource) != null) {
                        if (!GlobalConf.isSet("HeatSources." + tempSource)) {
                            GlobalConf.set("HeatSources." + tempSource, (double) 0);
                        }
                        plugin.heatSources.put(Material.getMaterial(tempSource), GlobalConf.getDouble("HeatSources." + tempSource));
                    }
                }
            } catch (NullPointerException ex) {
                RealWeather.log.log(Level.SEVERE, "NPE Error in loading 'HeatSources'. Make sure it is configured in Global.yml!");
            }
            plugin.heatInHand.clear();
            try {
                for (String tempSource : GlobalConf.getConfigurationSection("HeatInHand").getKeys(false)) {
                    if (Material.getMaterial(tempSource) != null) {
                        if (!GlobalConf.isSet("HeatInHand." + tempSource)) {
                            GlobalConf.set("HeatInHand." + tempSource, (double) 0);
                        }
                        plugin.heatInHand.put(Material.getMaterial(tempSource), GlobalConf.getDouble("HeatSources." + tempSource));
                    }
                }
            } catch (NullPointerException ex) {
                RealWeather.log.log(Level.SEVERE, "NPE Error in loading 'HeatInHand'. Make sure it is configured in Global.yml!");
            }
        } catch (Exception e) {
            RealWeather.log.log(Level.SEVERE, null, e);
            plugin.sendStackReport(e);
        }

    }

    private void LoadArmor(File CFile) {
        try {
            try {
                ArmorConf.load(CFile);
            } catch (FileNotFoundException ex) {
                RealWeather.log.log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                RealWeather.log.log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                RealWeather.log.log(Level.SEVERE, null, ex);
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
                    if (!ArmorConf.contains(type + "." + piece + ".FrostResistanceFactor")) {
                        ArmorConf.set(type + "." + piece + ".FrostResistanceFactor", (double) 1d);
                    }
                    if (!ArmorConf.contains(type + "." + piece + ".HeatResistanceFactor")) {
                        ArmorConf.set(type + "." + piece + ".HeatResistanceFactor", (double) 1d);
                    }
                }
            }
            if (!ArmorConf.contains("Pumpkin.FrostResistanceFactor")) {
                ArmorConf.set("Pumpkin.FrostResistanceFactor", (double) 1.1d);
            }
            if (!ArmorConf.contains("Pumpkin.HeatResistanceFactor")) {
                ArmorConf.set("Pumpkin.HeatResistanceFactor", (double) 1.1d);
            }
        } catch (Exception e) {
            RealWeather.log.log(Level.SEVERE, null, e);
            plugin.sendStackReport(e);
        }
    }

    private void copy(InputStream input, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = input.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            input.close();
        } catch (IOException e) {
            RealWeather.log.log(Level.SEVERE, null, e);
            RealWeather.sendStackReport(e);
        }
    }
}
