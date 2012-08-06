package org.creezo.realweather;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 *
 * @author Dodec
 */
public class PacketListener implements PluginMessageListener{

    //private HashMap<Integer, Boolean> PlayerClientMod = RealWeather.PlayerClientMod;
    
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        Bukkit.broadcastMessage(channel+" "+player.getDisplayName()+" "+new String(message));
        if(channel.equals("realweather") && player != null) {
            RealWeather.log("Player "+player.getPlayerListName()+" connected with RW client mod.");
            //PlayerClientMod.put(player.getEntityId(), Boolean.TRUE);
        }
    }
    
}
