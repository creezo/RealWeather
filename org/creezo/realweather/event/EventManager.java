package org.creezo.realweather.event;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.creezo.realweather.RealWeather;

/**
 *
 * @author Dodec
 */
public class EventManager {
    private final PlayerListener playerListener;
    private final PacketListener packetListener;
    
    private final RealWeather plugin;
    
    public EventManager(RealWeather plugin) {
        this.plugin = plugin;
        packetListener = new PacketListener(plugin);
        playerListener = new PlayerListener(plugin);
    }
    
    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(playerListener, plugin);
    }
    
    public void registerPluginChannel(String name) {
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, name);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, name, packetListener);
    }
}
