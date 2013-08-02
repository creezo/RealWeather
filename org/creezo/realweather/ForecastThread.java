package org.creezo.realweather;

import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.creezo.realweather.weather.Weather;

/**
 *
 * @author Dodec
 */
class ForecastThread implements Runnable{
    private final RealWeather plugin;
    private World world;
    private Random rnd = new Random();
    
    ForecastThread(RealWeather plugin) {
        this.plugin = plugin;
        if(plugin.getConfig().isString("WorldName")) {
            world = plugin.getServer().getWorld(plugin.getConfig().getString("WorldName"));
        } else {
            world = plugin.getServer().getWorlds().get(0);
        }
    }
    
    @Override
    public void run() {
        if(world.getTime() <= 1000) {
            if(plugin.Config.getVariables().isWeatherChangeEnable()) {
                if(world.hasStorm()) world.setStorm(false);
                if(world.isThundering()) world.setThundering(false);
            }
            plugin.mWeather.stormComing = false;
            plugin.mWeather.rainComing = false;
            plugin.mWeather.showersComing = false;
            plugin.mWeather.weatherStartTime = 0;
            
            plugin.weather = plugin.mWeather.addDay(plugin.weather);
            if(plugin.weather[2].equals(Weather.BLIZZARD)) {
                plugin.mWeather.stormComing = true;
            } else if(plugin.weather[2].equals(Weather.RAINSNOW)) {//
                plugin.mWeather.rainComing = true;
            } else if(plugin.weather[2].equals(Weather.SHOWERS)) {//
                plugin.mWeather.showersComing = true;
            } else if(plugin.weather[2].equals(Weather.STORM)) {
                plugin.mWeather.weatherStartTime = rnd.nextInt(12000)+4000;
                plugin.mWeather.weatherDuration = rnd.nextInt(4000)+4000;
                plugin.mWeather.rainComing = true;
                plugin.mWeather.stormComing = true;
            } else if(plugin.weather[2].equals(Weather.SUMMERSTORM)) {
                plugin.mWeather.weatherStartTime = rnd.nextInt(12000)+3000;
                plugin.mWeather.weatherDuration = rnd.nextInt(2000)+4000;
                plugin.mWeather.stormComing = true;
            }
            plugin.ForecastTemp = plugin.mWeather.temperaturesList.get(plugin.weather[2]);
            if(plugin.Config.getVariables().isWeatherBroadcastEnable()) {
                plugin.getServer().broadcastMessage(ChatColor.GOLD + plugin.Localization.FCToday + getForecastMessage(plugin.weather[2], plugin.Localization));
            }
        }
        
        if(plugin.Config.getVariables().isWeatherChangeEnable()) {
            if(plugin.mWeather.rainComing && !plugin.mWeather.stormComing && !world.hasStorm()) {
                world.setStorm(true);
            }

            if(plugin.mWeather.showersComing) {
                if(rnd.nextInt(100) < plugin.Config.getVariables().getBiomes().getGlobal().getShowersRainChance()) {
                    if(!world.hasStorm())  world.setStorm(true);
                } else {
                    if(world.hasStorm()) world.setStorm(false);
                }
            }

            if(plugin.mWeather.weatherStartTime-2000 <= world.getTime() && plugin.mWeather.rainComing && plugin.mWeather.stormComing && !world.hasStorm()) {
                world.setStorm(true);
            }

            if(plugin.mWeather.weatherStartTime <= world.getTime() && plugin.mWeather.weatherStartTime+plugin.mWeather.weatherDuration >= world.getTime() && plugin.mWeather.stormComing && !world.isThundering()) {
                world.setThundering(true);
            } else if(plugin.mWeather.weatherStartTime+plugin.mWeather.weatherDuration <= world.getTime() && world.isThundering() && world.hasStorm()) {
                world.setThundering(false);
                world.setStorm(false);
            }
        }
    }
    
    public static String getForecastMessage(Weather weather, Localization loc) {
        String Message;
        switch (weather) {
            case BLIZZARD:
                Message = loc.FCBlizzard;
                break;
            case STORM:
                Message = loc.FCStorm;
                break;
            case FREEZE:
                Message = loc.FCFreeze;
                break;
            case RAINSNOW:
                Message = loc.FCRainSnow;
                break;
            case COLD:
                Message = loc.FCCold;
                break;
            case SHOWERS:
                Message = loc.FCShowers;
                break;
            case CLEAR:
                Message = loc.FCClear;
                break;
            case WARM:
                Message = loc.FCWarm;
                break;
            case SUMMERSTORM:
                Message = loc.FCSummerStorm;
                break;
            case HOT:
                Message = loc.FCHot;
                break;
            case TROPIC:
                Message = loc.FCTropic;
                break;
            default:
                Message = "RealWeather: Forecast error! Non-existing weather provided.";
        }
        return Message;
    }
}
