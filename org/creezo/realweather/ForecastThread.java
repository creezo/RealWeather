/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realweather;

import java.util.Random;
import org.bukkit.World;

/**
 *
 * @author Dodec
 */
class ForecastThread implements Runnable{
    private final RealWeather plugin;
    private World world;
    private Utils utils = RealWeather.Utils;
    
    ForecastThread(RealWeather plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if(plugin.getConfig().isString("WorldName")) {
            world = plugin.getServer().getWorld(plugin.getConfig().getString("WorldName"));
        } else {
            world = plugin.getServer().getWorlds().get(0);
        }
        if(world.getTime() <= 1000) {
            Random rnd = new Random();
            int Temp = rnd.nextInt(11)-5;
            RealWeather.ForecastTemp = Temp;
            String Message = utils.DoForecast(Temp);
            plugin.getServer().broadcastMessage(Message);
        }
    }

}
