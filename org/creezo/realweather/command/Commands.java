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
    
    public void Disable() {
        plugin.getConfig().set("GlobalyEnable", false);
        try {
            plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            RealWeather.log.log(Level.SEVERE, null, ex);
        }
    }
    
    public void Enable() {
        plugin.getConfig().set("GlobalyEnable", true);
        try {
            plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            RealWeather.log.log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean Language(String lang) {
        if(!plugin.localization.langExists(lang)) {
            RealWeather.log.log(Level.INFO, "Language doesn't exists!");
            return false;
        }
        return plugin.localization.setLanguage(lang);
    }
}
