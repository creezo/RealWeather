package org.creezo.realweather;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 *
 * @author Dodec
 */
public class PacketListener implements PluginMessageListener{
    
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if(channel.equals("realweather") && player != null) {
            RealWeather.log("Player "+player.getPlayerListName()+" connected with RW client mod.");
            RealWeather.PlayerClientMod.put(player.getEntityId(), true);
        }
    }
    
}
