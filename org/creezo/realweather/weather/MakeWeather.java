package org.creezo.realweather.weather;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.creezo.realweather.RealWeather;


/**
 *
 * @author creezo
 */
public class MakeWeather {
    private final RealWeather plugin;
    private Random rnd = new Random();
    private FileConfiguration WeatherConf;
    private File WeatherFile;
    private HashMap<Weather, List<Weather>> chancesList = new HashMap<Weather, List<Weather>>();
    public HashMap<Weather, Integer> temperaturesList = new HashMap<Weather, Integer>();
    public int weatherStartTime = 0;
    public int weatherDuration = 4000;
    public boolean stormComing = false;
    public boolean rainComing = false;
    public boolean showersComing = false;
    private int WeatherFileVersion = 1;
    
    public MakeWeather(RealWeather plugin) {
        this.plugin = plugin;
        WeatherFile = new File(plugin.getDataFolder(), "weather.yml");
        WeatherConf = new YamlConfiguration();
        loadWeather();
        loadChances();
    }
    
    public Weather[] addDay(Weather[] yweather) {
        Weather[] weather = yweather;
        for (int i = 0; i < 4; i++) {
            weather[i] = yweather[i+1];
        }
        weather[4] = getNextDay(weather[3]);
        return weather;
    }
    
    private Weather getNextDay(Weather weather) {
        int n = chancesList.get(weather).size();
        weather = chancesList.get(weather).get(rnd.nextInt(n));
        return weather;
    }

    private void loadWeather() {
        if(!WeatherFile.exists()) {
            WeatherFile.getParentFile().mkdirs();
            plugin.Utils.copy(plugin.getResource("weather.yml"), WeatherFile);
            plugin.log.log(Level.INFO, "[RealWeather] Weather file copied.");
        }
        try {
            WeatherConf.load(WeatherFile);
        } catch (Exception e) {
            plugin.log.log(Level.WARNING, null, e);
        }
        if(WeatherConf.getInt("fileVersion", 0) != WeatherFileVersion) {
            File oldFile = new File("plugins/RealWeather/weather_" + WeatherConf.getInt("fileVersion", 0) + ".yml");
            plugin.log.log(Level.INFO, "[RealWeather] File version of weather.yml is not supported by this version.");
            try {
                WeatherConf.save(oldFile);
                plugin.log.log(Level.INFO, "[RealWeather] weather.yml version: " + WeatherConf.getInt("fileVersion", 0));
                plugin.log.log(Level.INFO, "[RealWeather] Required version: " + WeatherFileVersion);
                plugin.log.log(Level.INFO, "[RealWeather] Old weather.yml saved.");
            } catch(IOException ex) {
                plugin.log.log(Level.INFO, "[RealWeather] Saving of old weather.yml file failed. " + ex.getMessage());
            }
            WeatherFile.delete();
            plugin.Utils.copy(plugin.getResource("weather.yml"), WeatherFile);
            plugin.log("Weather configuration file copied.");
        } else {
            plugin.log("Weather configuration file    OK.");
        }
    }

    private void loadChances() {
        try {
            WeatherConf.load(WeatherFile);
        } catch (Exception e) {
            plugin.log.log(Level.WARNING, null, e);
        }
        
        int i = 0;
        try {
            for (String fromWeather : WeatherConf.getKeys(false)) {
                if(plugin.Config.getVariables().isDebugMode()) plugin.log("Parsing " + fromWeather);
                if(fromWeather.equals("fileVersion") || fromWeather.equals("changeWeather")) continue;
                if(Weather.valueOf(fromWeather) != null) {
                    List<Weather> list = new ArrayList<Weather>();
                    if(plugin.Config.getVariables().isDebugMode()) plugin.log("This is weather.");
                    i = 0;
                    for (String toWeather : WeatherConf.getConfigurationSection(fromWeather+".ChangeChance").getKeys(false)) {
                        if(Weather.valueOf(toWeather) != null) {
                            for (int j = 0; j < WeatherConf.getInt(fromWeather+".ChangeChance."+toWeather); j++) {
                                list.add(i, Weather.valueOf(toWeather));
                                i++;
                            }
                        }
                    }
                    chancesList.put(Weather.valueOf(fromWeather), list);
                    temperaturesList.put(Weather.valueOf(fromWeather), WeatherConf.getInt(fromWeather+".Temperature"));
                }
            }
        } catch(NullPointerException ex) {
            plugin.log.log(Level.SEVERE, "NPE Error in loading 'Weather'. Make sure it is configured in weather.yml!");
        }
    }
}
