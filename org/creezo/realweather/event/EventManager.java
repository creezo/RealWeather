package org.creezo.realweather.event;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.creezo.realweather.RealWeather;

/**
 *
 * @author Dodec
 */
public class EventManager {
    private PlayerListener playerListener;
    //private WeatherListener weatherListener;
    //private PlayerInteractListener playerInteractListener;
    //private PlayerDamageListener playerDamageListener;
    //private PlayerMoveListener playerMoveListener;
    private PacketListener packetListener;
    
    private final RealWeather plugin;
    
    public EventManager(RealWeather plugin) {
        this.plugin = plugin;
        
        packetListener = new PacketListener(plugin);
        playerListener = new PlayerListener(plugin);
        //weatherListener = new WeatherListener(plugin);
        //playerInteractListener = new PlayerInteractListener(plugin);
        //playerDamageListener = new PlayerDamageListener(plugin);
        //playerMoveListener = new PlayerMoveListener(plugin);
    }
    
    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(playerListener, plugin);
        //pm.registerEvents(weatherListener, plugin);
        //pm.registerEvents(playerInteractListener, plugin);
        //pm.registerEvents(playerDamageListener, plugin);
        //pm.registerEvents(playerMoveListener, plugin);
    }
    
    public void registerPluginChannel(String name) {
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, name);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, name, packetListener);
    }
}
