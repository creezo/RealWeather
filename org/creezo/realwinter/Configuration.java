/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 *
 * @author creezo
 */
public class Configuration {
    private boolean enabled;

    static String gameDifficulty = "peaceful";

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
    
    public int CheckDelay(RealWinter pluginn) {
        int CheckDelay = pluginn.getConfig().getConfigurationSection(gameDifficulty).getInt("CheckDelay");
        return CheckDelay;
    }
    
    public boolean DebugMode() {
        boolean DebugMode = plugin.getConfig().getBoolean("debug-mode");
        return DebugMode;
    }
    
    public int CheckRadius(RealWinter pluginn) {
        int radius = pluginn.getConfig().getInt("CheckRadius");
        return radius;
    }
    
    public String HouseRecognizer() {
        String HouseRecognizer = plugin.getConfig().getString("HouseRecognizer", "cross");
        return HouseRecognizer;
    }
}
