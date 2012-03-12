package org.creezo.realwinter;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author creezo
 */
public class RealWinter extends JavaPlugin {
    
    public static RealWinter TentoPlugin;
    public static File ConfigFile;
    public static FileConfiguration Config;
    public RealWinterPlayerListener playerlistener;
    public RealWinterWeatherListener weatherlistener;
    public static final Logger log = Logger.getLogger("Minecraft");
    public static int[] tid = new int[1000];
    public static boolean actualWeather = false;
    public static String gameDifficulty = "peaceful";
    
    public void Initialize() {
        TentoPlugin = this;
    }

    @Override
    public void onEnable() {
        Initialize();
        
        //configuration = new Configuration();
        //configuration.LoadConfig();
        
        
        PluginManager pm = getServer().getPluginManager();
        playerlistener = new RealWinterPlayerListener();
        weatherlistener = new RealWinterWeatherListener();
        pm.registerEvents(playerlistener, this);
        pm.registerEvents(weatherlistener, this);
        log.log(Level.INFO, "RealWinter enabled.");
        }
    
    @Override
    public void onDisable() {
        getServer().getScheduler().cancelAllTasks();
        log.log(Level.INFO, "RealWinter Disabled!");
    }
    
    public void LoadConfig() {
        PluginDescriptionFile pdfFile = this.getDescription();
        File oldConfigFile = new File("plugins/Realwinter/config_" + this.getConfig().getString("version", "old") + ".yml");
        File configFile = new File("plugins/Realwinter/config.yml");
        gameDifficulty = this.getServer().getWorld("world").getDifficulty().name().toLowerCase();
        
        if(!configFile.exists()) {
            this.saveDefaultConfig();
            this.log.log(Level.INFO, "[RealWinter] Default Config.yml copied.");
        }
        if(!pdfFile.getVersion().equals(this.getConfig().getString("version"))) {
            RealWinter.log.log(Level.INFO, "[RealWinter] Version of config file doesn't match with current plugin version.");
            this.getConfig().getDefaults();
            try {
                this.getConfig().save(oldConfigFile);
                RealWinter.log.log(Level.INFO, "[RealWinter] Config version: " + this.getConfig().getString("version"));
                RealWinter.log.log(Level.INFO, "[RealWinter] Plugin version: " + pdfFile.getVersion());
                this.log.log(Level.INFO, "[RealWinter] Old Config.yml saved.");
            } catch(IOException ex) {
                RealWinter.log.log(Level.INFO, "[RealWinter] Saving of old config file failed. " + ex.getMessage());
            }
            configFile.delete();
            this.saveDefaultConfig();
        }
    }
}