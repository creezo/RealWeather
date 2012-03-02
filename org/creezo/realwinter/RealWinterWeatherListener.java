package org.creezo.realwinter;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 *
 * @author creezo
 */
public class RealWinterWeatherListener implements Listener {

    public void onWeatherChange(WeatherChangeEvent event) {
        Bukkit.broadcastMessage("Weather Changed.");
        RealWinter.actualWeather = event.toWeatherState();
    }
}
