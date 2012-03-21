/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

/**
 *
 * @author creezo
 */
public class Commands {
    private final RealWinter plugin;
    private final Configuration config;
    private final PlayerListener plistener;

    public Commands(RealWinter plugin, Configuration config, PlayerListener playerlistener) {
        this.plugin = plugin;
        this.config = config;
        this.plistener = playerlistener;
    }

    public void Disable() {
        config.GlobalEnable = false;
    }
    
    public void Disable(String part) {
        if("all".equals(part)) {
            config.WinterEnabled = false;
            config.DesertEnabled = false;
            config.WaterBottleEnabled = false;
        } else if("desert".equals(part)) {
            config.DesertEnabled = false;
        } else if("winter".equals(part)) {
            config.WinterEnabled = false;
        } else if("waterbottle".equals(part)) {
            config.WaterBottleEnabled = false;
        }
    }
    
    public void Enable() {
        config.GlobalEnable = true;
    }
    
    public void Enable(String part) {
        if("all".equals(part)) {
            config.WinterEnabled = true;
            config.DesertEnabled = true;
            config.WaterBottleEnabled = true;
        } else if("desert".equals(part)) {
            config.DesertEnabled = true;
        } else if("winter".equals(part)) {
            config.WinterEnabled = true;
        } else if("waterbottle".equals(part)) {
            config.WaterBottleEnabled = true;
        }
    }
    
}
