package org.creezo.realweather.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.creezo.realweather.RealWeather;
import org.creezo.realweather.configuration.Configuration;
import org.creezo.realweather.feature.Feature;

/**
 *
 * @author Dodec
 */
public class ThreadManager {
    private final BukkitScheduler scheduler;
    private final RealWeather plugin;
    private final Configuration config;
    
    private final HashMap<Player, BukkitTask> playerTemperatureThreads;
    private final HashMap<Integer, Feature> independentThreads;
    private final ArrayList<Feature> dependentThreads;
    
    public ThreadManager(BukkitScheduler scheduler, RealWeather plugin, Configuration config) {
        this.scheduler = scheduler;
        playerTemperatureThreads = new HashMap<Player, BukkitTask>();
        independentThreads = new HashMap<Integer, Feature>();
        dependentThreads = new ArrayList<Feature>();
        this.plugin = plugin;
        this.config = config;
    }
    
    public int scheduleIndependentThread(Feature feature, int start, int repeat) {
        if(RealWeather.isDebug()) RealWeather.log("Sheduling check independent thread. " + feature.getName());
        int Id = scheduler.scheduleSyncRepeatingTask(plugin, feature, start, repeat);
        independentThreads.put(Id, feature);
        return Id;
    }
    
    public boolean scheduleDependentThread(Feature feature) {
        if(RealWeather.isDebug()) RealWeather.log("Sheduling check dependent thread. " + feature.getName());
        if(dependentThreads.contains(feature)) {
            if(RealWeather.isDebug()) RealWeather.log("Already scheduled!");
            return false;
        } else {
            dependentThreads.add(feature);
            return true;
        }
    }
    
    public void stopThread(int id) {
        scheduler.cancelTask(id);
        independentThreads.remove(id);
    }
    
    public void stopThread(Feature feature) {
        dependentThreads.remove(feature);
    }
    
    public void stopAllThreadsForFeature(Feature feature) {
        dependentThreads.remove(feature);
        for (Integer _feature : independentThreads.keySet()) {
            if(independentThreads.get(_feature) == feature) plugin.getServer().getScheduler().cancelTask(_feature);
        }
    }
    
    /**
     * Starts temperature thread for player.
     * <p>
     * @param player
     */
    public void startTempThread(Player player) {
        if(RealWeather.isDebug()) RealWeather.log("Starting base temperature thread for player. Start: " + config.getVariables().getStartDelay(config.getVariables().getGameDifficulty()) * 20 + ", Delay: " + config.getVariables().getCheckDelay(config.getVariables().getGameDifficulty()) * 20);
        playerTemperatureThreads.put(player, scheduler.runTaskTimer(plugin, new TempThread(plugin, player), config.getVariables().getStartDelay(config.getVariables().getGameDifficulty()) * 20, config.getVariables().getCheckDelay(config.getVariables().getGameDifficulty()) * 20));
    }
    
    /**
     * Stops temperature thread for player.
     * <p>
     * @param player
     */
    public void stopTempThread(Player player) {
       if(RealWeather.isDebug()) RealWeather.log("Stopping base temperature thread for player.");
       scheduler.cancelTask(playerTemperatureThreads.get(player).getTaskId());
       playerTemperatureThreads.remove(player);
    }

    public void runDependent(Player player, double temp) {
        if(RealWeather.isDebug()) RealWeather.log("Running all dependent features.");
        for (Feature feature : dependentThreads) {
            try {
                if (RealWeather.isDebug()) {
                    RealWeather.log("RUN: " + feature.getName());
                }
                feature.run(player, temp);
            } catch (Exception e) {
                if (RealWeather.isDebug()) {
                    RealWeather.log("RUN: " + feature.getName() + " FAILED");
                    RealWeather.log.log(Level.SEVERE, null, e);
                    RealWeather.sendStackReport(e);
                }
            }
        }
    }
}
