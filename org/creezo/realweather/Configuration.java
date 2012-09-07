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
    private final File WinterConfigFile;
    private final File DesertConfigFile;
    private final File JungleConfigFile;
    private final File ArmourConfigFile;
    private FileConfiguration WinterConf;
    private FileConfiguration DesertConf;
    private FileConfiguration JungleConf;
    private FileConfiguration GlobalConf;
    private FileConfiguration ArmourConf;
    
    private Configurations variables;
    public List<String> ArmourTypes = new ArrayList<String>();
    private List<String> ArmourPieces = new ArrayList<String>();
    
    public Configurations getVariables() {
        return variables;
    }
    private RealWeather plugin;

    public Configuration(RealWeather plugin) {
        this.plugin = plugin;
        this.WinterConf = new YamlConfiguration();
        this.DesertConf = new YamlConfiguration();
        this.JungleConf = new YamlConfiguration();
        this.GlobalConf = new YamlConfiguration();
        this.ArmourConf = new YamlConfiguration();
        this.GlobalConfigFile = new File(plugin.getDataFolder(), "biomes/Global.yml");
        this.WinterConfigFile = new File(plugin.getDataFolder(), "biomes/Winter.yml");
        this.DesertConfigFile = new File(plugin.getDataFolder(), "biomes/Desert.yml");
        this.JungleConfigFile = new File(plugin.getDataFolder(), "biomes/Jungle.yml");
        this.ArmourConfigFile = new File(plugin.getDataFolder(), "armour.yml");
        
    }
    public void InitConfig() {
        plugin.log("Loading Configuration.");
        LoadAll();
        variables = new Configurations(plugin, WinterConf, DesertConf, JungleConf, GlobalConf, ArmourConf);
        try {
            plugin.getConfig().load("plugins/RealWeather/config.yml");
        } catch (FileNotFoundException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
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
        if(!plugin.getConfig().contains("Statistics.Enable")) plugin.getConfig().set("Statistics.Enable", true);
        if(!plugin.getConfig().contains("Statistics.ShowMyServerOnList")) plugin.getConfig().set("Statistics.ShowMyServerOnList", false);
        if(!plugin.getConfig().contains("Statistics.ServerName")) plugin.getConfig().set("Statistics.ServerName", "Unknown");
        if(!plugin.getConfig().contains("Statistics.Comment")) plugin.getConfig().set("Statistics.Comment", "none");
        if(!plugin.getConfig().contains("Statistics.ServerAddress")) plugin.getConfig().set("Statistics.ServerAddress", "0.0.0.0:25565");
        if(!plugin.getConfig().contains("GlobalEnable")) plugin.getConfig().set("GlobalEnable", true);
        String StartDelayDiff = variables.getGameDifficulty() + ".StartDelay";
        if(!plugin.getConfig().contains(StartDelayDiff)) plugin.getConfig().set(StartDelayDiff, (int) 20);
        String CheckDelayDiff = variables.getGameDifficulty() + ".CheckDelay";
        if(!plugin.getConfig().contains(CheckDelayDiff)) plugin.getConfig().set(CheckDelayDiff, (int) 20);
        if(!plugin.getConfig().contains("NumberOfChecksPerWarningMessage")) plugin.getConfig().set("NumberOfChecksPerWarningMessage", (int) 5);
        variables.setMaxPlayers(plugin.getServer().getMaxPlayers());
        if(!plugin.getConfig().contains("AffectedWorlds")) {
            List<String> list = new ArrayList();
            list.add("world"); list.add("world_nether"); list.add("world_the_end");
            plugin.getConfig().set("AffectedWorlds", true);
        }
        //RealWinter.log.log(Level.INFO, StartDelay + " " + CheckDelay + " " + CheckRadius + " " + HouseRecoWinter + " " + GameDifficulty);
    }
        
    public boolean SaveAll() {
        try {
            WinterConf.save(WinterConfigFile);
            DesertConf.save(DesertConfigFile);
            JungleConf.save(JungleConfigFile);
            GlobalConf.save(GlobalConfigFile);
            ArmourConf.save(ArmourConfigFile);
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
        if(WinterConfigFile.exists()) {
            LoadWinter(WinterConfigFile);
        } else {
            copy(plugin.getResource("biomes/Winter.yml"), WinterConfigFile);
            plugin.log("Winter configuration file copied.");
            LoadWinter(WinterConfigFile);
        }
        if(DesertConfigFile.exists()) {
            LoadDesert(DesertConfigFile);
        } else {
            copy(plugin.getResource("biomes/Desert.yml"), DesertConfigFile);
            plugin.log("Desert configuration file copied.");
            LoadDesert(DesertConfigFile);
        }
        if(JungleConfigFile.exists()) {
            LoadJungle(JungleConfigFile);
        } else {
            copy(plugin.getResource("biomes/Jungle.yml"), JungleConfigFile);
            plugin.log("Jungle configuration file copied.");
            LoadJungle(JungleConfigFile);
        }
        if(GlobalConfigFile.exists()) {
            LoadGlobal(GlobalConfigFile);
        } else {
            copy(plugin.getResource("biomes/Global.yml"), GlobalConfigFile);
            plugin.log("Global configuration file copied.");
            LoadGlobal(GlobalConfigFile);
        }
        if(ArmourConfigFile.exists()) {
            LoadArmour(ArmourConfigFile);
        } else {
            copy(plugin.getResource("armour.yml"), ArmourConfigFile);
            plugin.log("Armour configuration file copied.");
            LoadArmour(ArmourConfigFile);
        }
    }
    
    private void LoadWinter(File CFile) {
        try {
            WinterConf.load(CFile);
        } catch (FileNotFoundException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
        if(!WinterConf.contains("enable")) WinterConf.set("enable", false);
        if(!WinterConf.contains("CheckRadius")) WinterConf.set("CheckRadius", (int) 2);
        if(!WinterConf.contains("TempPeak")) WinterConf.set("TempPeak", (int) 50);
        if(!WinterConf.contains("CanKillPlayer")) WinterConf.set("CanKillPlayer", false);
        if(!WinterConf.contains("PlayerIceBlock")) WinterConf.set("PlayerIceBlock", true);
        if(!WinterConf.contains("InitialTemperature")) WinterConf.set("InitialTemperature", (int) 0);
        if(!WinterConf.contains("HouseRecognizer")) WinterConf.set("HouseRecognizer", "cross");
        if(!WinterConf.contains("PlayerDamage")) WinterConf.set("PlayerDamage", (int) 4);
    }
    private void LoadDesert(File CFile) {
        try {
            DesertConf.load(CFile);
        } catch (FileNotFoundException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
        if(!DesertConf.contains("enable")) DesertConf.set("enable", true);
        if(!DesertConf.contains("HouseRecognizer")) DesertConf.set("HouseRecognizer", "simple");
        if(!DesertConf.contains("StaminaLost")) {
            List<Float> list = new ArrayList();
            list.add(0.05F);
            DesertConf.set("StaminaLost", list);
        }
        if(!DesertConf.contains("NumberOfCheckPerFoodLost")) DesertConf.set("enaNumberOfCheckPerFoodLostle", (int) 5);
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
            List<Float> list = new ArrayList();
            list.add(0.03F);
            GlobalConf.set("thirst.StaminaLost", list);
        }
        if(!GlobalConf.contains("thirst.AffectedWorlds")) {
            List<String> list = new ArrayList();
            list.add("world"); list.add("world_nether"); list.add("world_the_end");
            GlobalConf.set("thirst.AffectedWorlds", list);
        }
        if(!GlobalConf.contains("staminareplenish.enable")) GlobalConf.set("staminareplenish.enable", true);
        if(!GlobalConf.contains("BiomesWeatherTempModifier.Light")) GlobalConf.set("BiomesWeatherTempModifier.Light", 5);
        if(!GlobalConf.contains("BiomesWeatherTempModifier.Medium")) GlobalConf.set("BiomesWeatherTempModifier.Medium", 8);
        if(!GlobalConf.contains("BiomesWeatherTempModifier.Hard")) GlobalConf.set("BiomesWeatherTempModifier.Hard", 12);
        if(!GlobalConf.contains("HeatCheckRadius")) GlobalConf.set("HeatCheckRadius", (int) 3);
        if(!GlobalConf.contains("PlayerHeat")) GlobalConf.set("PlayerHeat", (int) 1);
        if(!GlobalConf.contains("FreezeUnder")) GlobalConf.set("FreezeUnder", (int) 7);
        if(!GlobalConf.contains("OverheatOver")) GlobalConf.set("OverheatOver", (int) 30);
        if(!GlobalConf.contains("SeaLevel")) GlobalConf.set("SeaLevel", (int) 62);
        if(!GlobalConf.contains("MaxMapHeightTemperatureModifier")) GlobalConf.set("MaxMapHeightTemperatureModifier", (int) -10);
        if(!GlobalConf.contains("staminareplenish.StaminaReplenishWaterBottle")) {
            List<Float> list = new ArrayList();
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
    
    private void LoadArmour(File CFile) {
        try {
            ArmourConf.load(CFile);
        } catch (FileNotFoundException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
        ArmourTypes.add("Leather");
        ArmourTypes.add("Iron");
        ArmourTypes.add("Gold");
        ArmourTypes.add("Diamond");
        ArmourTypes.add("Chain");
        ArmourTypes.add("Other");
        ArmourPieces.add("Boots");
        ArmourPieces.add("Chestplate");
        ArmourPieces.add("Helmet");
        ArmourPieces.add("Leggings");
        for (String type : ArmourTypes) {
            for (String piece : ArmourPieces) {
                if(!ArmourConf.contains(type+"."+piece+".FrostResistanceFactor")) ArmourConf.set(type+"."+piece+".FrostResistanceFactor", (double)1d);
                if(!ArmourConf.contains(type+"."+piece+".HeatResistanceFactor")) ArmourConf.set(type+"."+piece+".HeatResistanceFactor", (double)1d);
            }
        }
        if(!ArmourConf.contains("Pumpkin.FrostResistanceFactor")) ArmourConf.set("Pumpkin.FrostResistanceFactor", (double)1.1d);
        if(!ArmourConf.contains("Pumpkin.HeatResistanceFactor")) ArmourConf.set("Pumpkin.HeatResistanceFactor", (double)1.1d);
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
            RealWeather.log.log(Level.INFO, e.getMessage());
        }
    }
}
