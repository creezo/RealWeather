package org.creezo.realwinter;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 *
 * @author creezo
 */
public class RealWinterWeatherListener implements Listener {
    private Configuration Config = RealWinter.Config;
    
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if(Config.DebugMode) Bukkit.broadcastMessage("Weather Changed.");
        RealWinter.actualWeather = event.toWeatherState();
    }
}
