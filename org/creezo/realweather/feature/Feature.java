package org.creezo.realweather.feature;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.creezo.realweather.thread.ThreadManager;

/**
 *
 * @author Dodec
 */
public abstract class Feature implements Runnable{
    private final String name;
    private FeatureConfiguration config = null;
    private final JavaPlugin plugin;
    
    public Feature(String name, JavaPlugin plugin) {
        this.name = name;
        this.plugin = plugin;
    }
    public abstract void onEnable();
    public abstract void onDisable();
    public abstract void registerEvents(PluginManager pm);
    public abstract void initThreads(ThreadManager tm);
    public abstract void run(Player player, double temp);
    public String getName() {
        return name;
    }
    public FeatureConfiguration getConfig() {
        if(config == null) {
            config = FeatureManager.getConfig(name);
        }
        return config;
    }
    public JavaPlugin getPlugin() {
        return plugin;
    }
}
