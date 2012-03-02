/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 *
 * @author creezo
 */
public class Configuration {
    private boolean enabled;
    private RealWinter plugin;
    public void Initialize(RealWinter instance) {
        plugin = instance;
    }
    private String gameDifficulty = "peaceful";

    public void LoadConfig() {
        Initialize(RealWinter.TentoPlugin);
        PluginDescriptionFile pdfFile = plugin.getDescription();
        File oldConfigFile = new File("plugins/Realwinter/config_" + plugin.getConfig().getString("version", "old") + ".yml");
        File configFile = new File("plugins/Realwinter/config.yml");
        gameDifficulty = plugin.getServer().getWorld("world").getDifficulty().name().toLowerCase();
        
        if(!configFile.exists()) {
            plugin.saveDefaultConfig();
            plugin.log.log(Level.INFO, "[RealWinter] Default Config.yml copied.");
        }
        if(!pdfFile.getVersion().equals(plugin.getConfig().getString("version"))) {
            RealWinter.log.log(Level.INFO, "[RealWinter] Version of config file doesn't match with current plugin version.");
            plugin.getConfig().getDefaults();
            try {
                plugin.getConfig().save(oldConfigFile);
                RealWinter.log.log(Level.INFO, "[RealWinter] Config version: " + plugin.getConfig().getString("version"));
                RealWinter.log.log(Level.INFO, "[RealWinter] Plugin version: " + pdfFile.getVersion());
                plugin.log.log(Level.INFO, "[RealWinter] Old Config.yml saved.");
            } catch(IOException ex) {
                RealWinter.log.log(Level.INFO, "[RealWinter] Saving of old config file failed. " + ex.getMessage());
            }
            configFile.delete();
            plugin.saveDefaultConfig();
        }
    }

    public boolean getEnabled() {
        enabled = plugin.getConfig().getBoolean("enable");
        return enabled;
    }
    
    public boolean setEnabled(boolean state) {
        enabled = state;
        return enabled;
    }
    
    public int StartDelay() {
        int StartDelay = plugin.getConfig().getConfigurationSection(gameDifficulty).getInt("StartDelay");
        return StartDelay;
    }
    
    public int CheckDelay() {
        int CheckDelay = plugin.getConfig().getConfigurationSection(gameDifficulty).getInt("CheckDelay");
        return CheckDelay;
    }
    
    public boolean DebugMode() {
        boolean DebugMode = plugin.getConfig().getBoolean("debug-mode");
        return DebugMode;
    }
    
//    private String convertSimple(int i) {
//        return "" + i;
//    }
}
