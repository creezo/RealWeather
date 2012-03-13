/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 *
 * @author creezo
 */
public class Configuration {
    public boolean enabled;
    public int StartDelay;
    public int CheckDelay;
    public boolean DebugMode;
    public int CheckRadius;
    public String HouseRecognizer;
    public String GameDifficulty = "peaceful";

    public boolean setEnabled(boolean state) {
        enabled = state;
        return enabled;
    }
    
    public void InitConfig(RealWinter plugin) {
        RealWinter.log.log(Level.INFO, "[RealWinter] Loading Configuration.");
        try {
            try {
                try {
                    plugin.getConfig().load("plugins/Realwinter/config.yml");
                } catch(InvalidConfigurationException e) { plugin.getServer().broadcastMessage(e.getMessage()); }
            } catch (FileNotFoundException e) { plugin.getServer().broadcastMessage(e.getMessage()); }
        } catch (IOException e) { plugin.getServer().broadcastMessage(e.getMessage()); }
        GameDifficulty = plugin.getServer().getWorlds().get(0).getDifficulty().name().toLowerCase();
        String StartDelayDiff = GameDifficulty + ".StartDelay";
        StartDelay = plugin.getConfig().getInt(StartDelayDiff, 20);
        String CheckDelayDiff = GameDifficulty + ".CheckDelay";
        CheckDelay = plugin.getConfig().getInt(CheckDelayDiff, 10);
        DebugMode = plugin.getConfig().getBoolean("debug-mode");
        CheckRadius = plugin.getConfig().getInt("CheckRadius");
        HouseRecognizer = plugin.getConfig().getString("HouseRecognizer", "cross");
        //RealWinter.log.log(Level.INFO, StartDelay + " " + CheckDelay + " " + CheckRadius + " " + HouseRecognizer + " " + GameDifficulty);
    }
    
    private static String ConvertIntToString(int number) {
        return "" + number;
    }
}
