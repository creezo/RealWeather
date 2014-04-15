package org.creezo.realweather.event;

import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.creezo.realweather.RealWeather;

/**
 *
 * @author creezo
 */
class PlayerListener implements Listener {

    private final RealWeather plugin;
    private final HashMap<Integer, Boolean> PlayerHeatShow;

    PlayerListener(RealWeather plugin) {
        this.plugin = plugin;
        PlayerHeatShow = plugin.playerHeatShow;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            final Player player = event.getPlayer();
            final int PlayerID = player.getEntityId();
            plugin.getThreadManager().startTempThread(player);
            PlayerHeatShow.put(PlayerID, Boolean.FALSE);
            plugin.playerClientMod.put(PlayerID, Boolean.FALSE);
            byte[] bytes = ("RW:Login").getBytes();
            player.sendPluginMessage(plugin, "realweather", bytes);
        } catch (Exception e) {
            RealWeather.log.log(Level.SEVERE, null, e);
            RealWeather.sendStackReport(e);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        int PlayerID = event.getPlayer().getEntityId();
        try {
            plugin.getThreadManager().stopTempThread(event.getPlayer());
            plugin.getPlayerTemperature().remove(event.getPlayer());
            PlayerHeatShow.remove(PlayerID);
            plugin.playerClientMod.remove(PlayerID);
        } catch (Exception e) {
            RealWeather.log.log(Level.SEVERE, null, e);
            RealWeather.sendStackReport(e);
        }
    }
}