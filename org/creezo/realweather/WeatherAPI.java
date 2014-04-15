package org.creezo.realweather;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author creezo
 */
public class WeatherAPI {
    private final RealWeather plugin;
    
    /**
     * 
     * @param plugin
     */
    public WeatherAPI(RealWeather plugin) {
        this.plugin = plugin;
    }
    /**
     * Aquire current temperature on specified location OR player.
     * <p>
     * If there is not player present you must specify location.
     * Set location or player <code>null</code> if you don't want to set them.
     * <p>
     * If you set both values temperature is taken from location
     * and temperature from other entities from player's location.
     * @param location Location of the calculated position.
     * @param player Player to calculate heat from other entities.
     * @return Temperature. Returns NaN if both are <code>null</code>.
     */
    public double getTemperature(Location location, Player player) {
        if(location == null && player == null) return Double.NaN;
        return plugin.checkCenter.getTemperature(location, player);
    }
    /**
     * Checks if location is inside building/under cover.
     * <p>
     * Area of checking is square, not circle.
     * @param location Location of the center.
     * @param CheckRadius Maximum range from center.
     * @param recognizer Type of checking system. Available: simple, default, cross
     * 
     * @return true if there is cover, false otherwise.
     */
    public boolean isInside(Location location, int CheckRadius, String recognizer) {
        return CheckCenter.checkPlayerInside(location, CheckRadius, recognizer);
    }
    /**
     * Gets heat around player/location.
     * <p>
     * Area of checking is half square, half circle.
     * 
     * @param player OPTIONAL. <code>null</code> if you don't have player.
     * @param location REQUIRED. Location of center
     * @param HeatCheckRadius REQUIRED. Maximal radius of checking.
     * 
     * @return Heat from block around. If player is specified, also from his hand.
     * NaN if player and location are both <code>null</code>.
     */
    public double checkHeatAround(Player player, Location location, int HeatCheckRadius) {
        if(player == null && location == null) return Double.NaN;
        return plugin.checkCenter.checkHeatAround(player, location, HeatCheckRadius);
    }
}
