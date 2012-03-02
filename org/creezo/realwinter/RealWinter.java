package org.creezo.realwinter;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author creezo
 */
public class RealWinter extends JavaPlugin implements Listener {
    public static RealWinter TentoPlugin;
    public void Initialize(RealWinter instance) {
        TentoPlugin = instance;
    }
    public final RealWinterPlayerListener playerlistener = new RealWinterPlayerListener();
    public final RealWinterWeatherListener weatherlistener = new RealWinterWeatherListener();
    public static final Logger log = Logger.getLogger("Minecraft");
    public static int[] tid = new int[1000];
    public static boolean actualWeather = false;
    
    @Override
    public void onEnable() {
        Initialize(this);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(playerlistener, this);
        pm.registerEvents(weatherlistener, this);
        log.log(Level.INFO, "RealWinter enabled.");
        }
    
    @Override
    public void onDisable() {
        getServer().getScheduler().cancelAllTasks();
        log.log(Level.INFO, "RealWinter Disabled!");
    }
    
//    private String convertSimple(int i) {
//        return "" + i;
//    }
}