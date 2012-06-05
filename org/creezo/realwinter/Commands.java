package org.creezo.realwinter;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 *
 * @author creezo
 */
public class Commands {
    private final RealWinter plugin;
    private final Configuration config;
    private final Localization localization;

    public Commands(RealWinter plugin, Configuration config, Localization Loc) {
        this.plugin = plugin;
        this.config = config;
        this.localization = Loc;
    }

    public void Disable() {
        plugin.getConfig().set("GlobalEnable", false);
        try {
            plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
    }
    
    public void Disable(String part) {
        if("all".equals(part)) {
            config.getVariables().getBiomes().getWinter().setEnabled(false);
            config.getVariables().getBiomes().getDesert().setEnabled(false);
            config.getVariables().getBiomes().getJungle().setEnabled(false);
            config.getVariables().getBiomes().getGlobal().setReplenishEnabled(false);
            config.getVariables().getBiomes().getGlobal().setThirstEnabled(false);
        } else if("desert".equals(part)) {
            config.getVariables().getBiomes().getDesert().setEnabled(false);
        } else if("jungle".equals(part)) {
            config.getVariables().getBiomes().getJungle().setEnabled(false);
        } else if("winter".equals(part)) {
            config.getVariables().getBiomes().getWinter().setEnabled(false);
        } else if("thirst".equals(part)) {
            config.getVariables().getBiomes().getGlobal().setThirstEnabled(false);
        } else if("waterbottle".equals(part)) {
            config.getVariables().getBiomes().getGlobal().setReplenishEnabled(false);
        }
    }
    
    public void Enable() {
        plugin.getConfig().set("GlobalEnable", true);
        try {
            plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
    }
    
    public void Enable(String part) {
        if("all".equals(part)) {
            config.getVariables().getBiomes().getWinter().setEnabled(true);
            config.getVariables().getBiomes().getDesert().setEnabled(true);
            config.getVariables().getBiomes().getJungle().setEnabled(true);
            config.getVariables().getBiomes().getGlobal().setReplenishEnabled(true);
        } else if("desert".equals(part)) {
            config.getVariables().getBiomes().getDesert().setEnabled(true);
        } else if("jungle".equals(part)) {
            config.getVariables().getBiomes().getJungle().setEnabled(true);
        } else if("winter".equals(part)) {
            config.getVariables().getBiomes().getWinter().setEnabled(true);
        } else if("thirst".equals(part)) {
            config.getVariables().getBiomes().getGlobal().setThirstEnabled(true);
        } else if("waterbottle".equals(part)) {
            config.getVariables().getBiomes().getGlobal().setReplenishEnabled(true);
        }
    }
    
    public boolean Language(String lang) {
        if(!localization.LangExists(lang)) {
            plugin.log.log(Level.INFO, "Language doesn't exists!");
            return false;
        }
        if(!localization.SetLanguage(lang)) {
            return false;
        }
        return true;
    }
}
