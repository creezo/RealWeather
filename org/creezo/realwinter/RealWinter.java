package org.creezo.realwinter;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author creezo
 */
public class RealWinter extends JavaPlugin {
    public static RealWinter TentoPlugin;
    private Configuration configuration;
    public void Initialize(RealWinter instance) {
        TentoPlugin = instance;
    }
    
    public RealWinterPlayerListener playerlistener;
    public RealWinterWeatherListener weatherlistener;
    public static final Logger log = Logger.getLogger("Minecraft");
    public static int[] tid = new int[1000];
    public static boolean actualWeather = false;
    
    @Override
    public void onEnable() {
        Initialize(this);
        configuration = new Configuration();
        configuration.LoadConfig();
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
    
//    private String convertSimple(int i) {
//        return "" + i;
//    }
}