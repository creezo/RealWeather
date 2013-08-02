package org.creezo.realweather;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 *
 * @author Dodec
 */
public class PacketListener implements PluginMessageListener{
    private final RealWeather plugin;
    
    public PacketListener(RealWeather plugin) {
        this.plugin = plugin;
    }
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if(channel.equals("realweather") && player != null) {
            plugin.log("Player "+player.getPlayerListName()+" connected with RW client mod.");
            plugin.PlayerClientMod.put(player.getEntityId(), true);
        }
    }
    
}
