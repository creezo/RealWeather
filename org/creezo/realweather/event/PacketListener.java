package org.creezo.realweather.event;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.creezo.realweather.RealWeather;

/**
 *
 * @author Dodec
 */
public class PacketListener implements PluginMessageListener{
    private final RealWeather plugin;
    
    PacketListener(RealWeather plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        RealWeather.log("Got mod message..." + new String(message));
        if(channel.equals("realweather") && player != null) {
            RealWeather.log("Player "+player.getPlayerListName()+" connected with RW client mod.");
            plugin.playerClientMod.put(player.getEntityId(), true);
        }
    }
}
