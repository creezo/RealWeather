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
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author creezo
 */
public class Configuration {
    private final File GlobalConfigFile;
    private final File WinterConfigFile;
    private final File DesertConfigFile;
    private final File JungleConfigFile;
    private FileConfiguration WinterConf;
    private FileConfiguration DesertConf;
    private FileConfiguration JungleConf;
    private FileConfiguration GlobalConf;
    
    private Configurations variables;
    
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
        this.GlobalConfigFile = new File(plugin.getDataFolder(), "biomes/Global.yml");
        this.WinterConfigFile = new File(plugin.getDataFolder(), "biomes/Winter.yml");
        this.DesertConfigFile = new File(plugin.getDataFolder(), "biomes/Desert.yml");
        this.JungleConfigFile = new File(plugin.getDataFolder(), "biomes/Jungle.yml");
        
    }
    public void InitConfig() {
        plugin.log("Loading Configuration.");
        LoadAll();
        variables = new Configurations(plugin, WinterConf, DesertConf, JungleConf, GlobalConf);
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
    
    public void InitEquip() {
        if(variables.isDebugMode()) plugin.log("Loading Armors.");
        List<Integer> ListOfBoots = plugin.getConfig().getIntegerList("Armor.Boots");
        List<Integer> ListOfChestplate = plugin.getConfig().getIntegerList("Armor.Chestplate");
        List<Integer> ListOfHelmet = plugin.getConfig().getIntegerList("Armor.Helmet");
        List<Integer> ListOfLeggings = plugin.getConfig().getIntegerList("Armor.Leggings");
        int[] numOfArmors = new int[4];
        for(int index = 0 ; index < ListOfBoots.size() ; index++ ) {
            ItemStack IStack = new ItemStack(ListOfBoots.get(index), 1);
            List<ItemStack> ItemList = variables.getAllowedBoots();
            ItemList.add(IStack);
            variables.setAllowedBoots(ItemList);
            numOfArmors[0]++;
        }
        for(int index = 0 ; index < ListOfChestplate.size() ; index++ ) {
            ItemStack IStack = new ItemStack(ListOfChestplate.get(index), 1);
            List<ItemStack> ItemList = variables.getAllowedChestplate();
            ItemList.add(IStack);
            variables.setAllowedChestplate(ItemList);
            numOfArmors[1]++;
        }
        for(int index = 0 ; index < ListOfHelmet.size() ; index++ ) {
            ItemStack IStack = new ItemStack(ListOfHelmet.get(index), 1);
            List<ItemStack> ItemList = variables.getAllowedHelmet();
            ItemList.add(IStack);
            variables.setAllowedHelmet(ItemList);
            numOfArmors[2]++;
        }
        for(int index = 0 ; index < ListOfLeggings.size() ; index++ ) {
            ItemStack IStack = new ItemStack(ListOfLeggings.get(index), 1);
            List<ItemStack> ItemList = variables.getAllowedLeggings();
            ItemList.add(IStack);
            variables.setAllowedLeggings(ItemList);
            numOfArmors[3]++;
        }
        plugin.log("Loaded " + ConvertIntToString(numOfArmors[0]) + " Boots, " + ConvertIntToString(numOfArmors[1]) + " Chestplates, " + ConvertIntToString(numOfArmors[2]) + " Helmets, " + ConvertIntToString(numOfArmors[3]) + " Leggings.");
    }
    
    public boolean SaveAll() {
        try {
            WinterConf.save(WinterConfigFile);
            DesertConf.save(DesertConfigFile);
            JungleConf.save(JungleConfigFile);
            GlobalConf.save(GlobalConfigFile);
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
        if(!WinterConf.contains("PlayerDamage.NoArmor")) WinterConf.set("PlayerDamage.NoArmor", (int) 3);
        if(!WinterConf.contains("PlayerDamage.OnePiece")) WinterConf.set("PlayerDamage.OnePiece", (int) 2);
        if(!WinterConf.contains("PlayerDamage.TwoPieces")) WinterConf.set("PlayerDamage.TwoPieces", (int) 2);
        if(!WinterConf.contains("PlayerDamage.ThreePieces")) WinterConf.set("PlayerDamage.ThreePieces", (int) 1);
        if(!WinterConf.contains("PlayerDamage.FullArmor")) WinterConf.set("PlayerDamage.FullArmor", (int) 0);
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
        if(!DesertConf.contains("StaminaLost.WithHelmet")) {
            List<Float> list = new ArrayList();
            list.add(0.05F);
            DesertConf.set("StaminaLost.WithHelmet", list);
        }
        if(!DesertConf.contains("StaminaLost.WithoutHelmet")) {
            List<Float> list = new ArrayList();
            list.add(0.15F);
            DesertConf.set("StaminaLost.WithoutHelmet", list);
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
    }
    
    private static String ConvertIntToString(int number) {
        return "" + number;
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
