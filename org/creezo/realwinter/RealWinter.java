package org.creezo.realwinter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author creezo
 */
public class RealWinter extends JavaPlugin {
    public static RealWinter TentoPlugin;
    public RealWinterPlayerListener playerlistener;
    public RealWinterWeatherListener weatherlistener;
    public static final Logger log = Logger.getLogger("Minecraft");
    public static HashMap<Integer, Integer> PlayerHashMap;
    public static boolean actualWeather = false;
    public static Configuration Config;
    public static PlayerCheck playerCheck;
    
    public void Initialize() {
        TentoPlugin = this;
    }

    @Override
    public void onEnable() {
        Initialize();

        Config = new Configuration();
        LoadConfig();
        Config.InitConfig(this);
        Config.InitEquip(this);
        playerCheck = new PlayerCheck();
        playerCheck.PCheckInit();
        PlayerHashMap = new HashMap<Integer, Integer>(getServer().getMaxPlayers()+1);
        PluginManager pm = getServer().getPluginManager();
        playerlistener = new RealWinterPlayerListener();
        weatherlistener = new RealWinterWeatherListener();
        pm.registerEvents(playerlistener, this);
        pm.registerEvents(weatherlistener, this);
        log.log(Level.INFO, "RealWinter enabled.");
    }
    
    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelAllTasks();
        log.log(Level.INFO, "RealWinter Disabled!");
    }
    
    private void LoadConfig() {
        PluginDescriptionFile pdfFile = this.getDescription();
        File oldConfigFile = new File("plugins/RealWinter/config_" + getConfig().getString("version", "old") + ".yml");
        File configFile = new File("plugins/RealWinter/config.yml");
        
        if(!configFile.exists()) {
            saveDefaultConfig();
            log.log(Level.INFO, "[RealWinter] Default Config.yml copied.");
        }
        if(!pdfFile.getVersion().equals(getConfig().getString("version"))) {
            log.log(Level.INFO, "[RealWinter] Version of config file doesn't match with current plugin version.");
            getConfig().getDefaults();
            try {
                getConfig().save(oldConfigFile);
                log.log(Level.INFO, "[RealWinter] Config version: " + this.getConfig().getString("version"));
                log.log(Level.INFO, "[RealWinter] Plugin version: " + pdfFile.getVersion());
                log.log(Level.INFO, "[RealWinter] Old Config.yml saved.");
            } catch(IOException ex) {
                log.log(Level.INFO, "[RealWinter] Saving of old config file failed. " + ex.getMessage());
            }
            configFile.delete();
            saveDefaultConfig();
        }
    }
}