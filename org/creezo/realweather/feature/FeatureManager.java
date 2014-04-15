package org.creezo.realweather.feature;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.creezo.realweather.RealWeather;
import org.creezo.realweather.configuration.Configurations;

/**
 *
 * @author Dodec
 */
public class FeatureManager {
    private final RealWeather plugin;
    private static HashMap<String, Feature> features;
    private static HashMap<String, FeatureConfiguration> configs;
    private ArrayList<String> brokenModules;
    private static SharedData sharedData;
    private final File featuresFolder;
    
    public FeatureManager(RealWeather plugin) {
        this.plugin = plugin;
        featuresFolder = new File(plugin.getDataFolder(), "features");
    }
    
    public void init() {
        if(RealWeather.isDebug()) RealWeather.log("Initializing features.");
        features = new HashMap<String, Feature>();
        brokenModules = new ArrayList<String>();
        configs = new HashMap<String, FeatureConfiguration>();
        sharedData = new SharedData();
        
        if(!featuresFolder.exists()) {
            featuresFolder.mkdir();
        } else {
            for (String featJar : featuresFolder.list(new FilenameFilter() {

                @Override
                public boolean accept(File featuresFolder, String featJar) {
                    return featJar.endsWith(".jar");
                }
            })) {
                try {
                    URL[] urls = new URL[1];
                    urls[0] = (new File(featuresFolder, featJar)).toURI().toURL();
                    
                    URLClassLoader child = new URLClassLoader(urls, this.getClass().getClassLoader());
                    InputStream in;
                    if((in = child.getResourceAsStream("module.yml")) != null) {
                        YamlConfiguration base = new YamlConfiguration();
                        base.load(in);
                        if(base.contains("main") & base.contains("author")) {
                            Class classToLoad = Class.forName(base.getString("main"), true, child);
                            Constructor cons = classToLoad.getConstructor(JavaPlugin.class);
                            Object instance = cons.newInstance(plugin);
                            Feature newFeature = (Feature)instance;
                            File featureDataFolder = new File(plugin.getDataFolder(), "features/" + newFeature.getName());
                            featureDataFolder.mkdirs();
                            FeatureConfiguration newConfig = new FeatureConfiguration(featureDataFolder);
                            features.put(newFeature.getName(), newFeature);
                            configs.put(newFeature.getName(), newConfig);
                            if(base.getString("main").endsWith("FWeather")) {
                                plugin.setWeatherLoaded(true);
                            }
                            if(RealWeather.isDebug()) RealWeather.log("Succesfuly loaded: " + newFeature.getName() + ", by " + base.getString("author"));
                        } else {
                            if(RealWeather.isDebug()) RealWeather.log("module.yml of " + featJar + " is not valid! Skip.");
                        }
                        in.close();
                    } else {
                        if(RealWeather.isDebug()) RealWeather.log("module.yml of " + featJar + " is missing! Skip.");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(FeatureManager.class.getName()).log(Level.SEVERE, null, ex);
                    RealWeather.sendStackReport(ex);
                }
            }
        }
        if(RealWeather.isDebug()) RealWeather.log("Features initialization complete");
    }
    
    public void enable() {
        if(RealWeather.isDebug()) RealWeather.log("Enabling features.");
        for (Feature feature : features.values()) {
            try {
                if(RealWeather.isDebug()) RealWeather.log("ENA: " + feature.getName());
                feature.onEnable();
            } catch (Exception ex) {
                if(RealWeather.isDebug()) RealWeather.log("Error occured while enabling module.");
                Logger.getLogger(FeatureManager.class.getName()).log(Level.SEVERE, null, ex);
                brokenModules.add(feature.getName());
            }
        }
        for (String feature : brokenModules) {
            try {
                features.get(feature).onDisable();
            } catch (Exception ex) {
                Logger.getLogger(FeatureManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                features.remove(feature);
            } catch (Exception ex) {
                Logger.getLogger(FeatureManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                configs.remove(feature);
            } catch (Exception ex) {
                Logger.getLogger(FeatureManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(RealWeather.isDebug()) RealWeather.log("Starting features threads.");
        for (Feature feature : features.values()) {
            try {
                if(RealWeather.isDebug()) RealWeather.log("THR: " + feature.getName());
                feature.initThreads(plugin.getThreadManager());
            } catch (Exception ex) {
                if(RealWeather.isDebug()) RealWeather.log("Error occured while module threads were starting.");
                Logger.getLogger(FeatureManager.class.getName()).log(Level.SEVERE, null, ex);
                brokenModules.add(feature.getName());
            }
        }
        for (String feature : brokenModules) {
            try {
                features.get(feature).onDisable();
            } catch (Exception ex) {
                Logger.getLogger(FeatureManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                features.remove(feature);
            } catch (Exception ex) {
                Logger.getLogger(FeatureManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                configs.remove(feature);
            } catch (Exception ex) {
                Logger.getLogger(FeatureManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(RealWeather.isDebug()) RealWeather.log("All features enabled.");
    }
    
    public void registerEvents(PluginManager pm) {
        if(RealWeather.isDebug()) RealWeather.log("Registering features events.");
        for (Feature feature : features.values()) {
            try {
                if(RealWeather.isDebug()) RealWeather.log("EVT: " + feature.getName());
                feature.registerEvents(pm);
            } catch (Exception ex) {
                if(RealWeather.isDebug()) RealWeather.log("Error occured while registering events for module.");
                Logger.getLogger(FeatureManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(RealWeather.isDebug()) RealWeather.log("Registering events complete.");
    }
    
    public void disable() {
        if(RealWeather.isDebug()) RealWeather.log("Disabling features.");
        for (Feature feature : features.values()) {
            try {
                if(RealWeather.isDebug()) RealWeather.log("DIS: " + feature.getName());
                try {
                    configs.get(feature.getName()).save();
                } catch (IOException ex) {
                    if(RealWeather.isDebug()) RealWeather.log("ERROR while saving config.");
                    Logger.getLogger(FeatureManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                feature.onDisable();
                plugin.getThreadManager().stopAllThreadsForFeature(feature);
            } catch (Exception ex) {
                if(RealWeather.isDebug()) RealWeather.log("Error occured while disabling module.");
                Logger.getLogger(FeatureManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(RealWeather.isDebug()) RealWeather.log("All features disabled.");
    }
    
    public void tick(Player player, double temp) {
        if(RealWeather.isDebug()) RealWeather.log("Feature manager tick starting.");
        plugin.getThreadManager().runDependent(player, temp);
        if(RealWeather.isDebug()) RealWeather.log("FM Tick finished.");
    }

    public Feature getModule(String module) {
        return features.get(module);
    }
    
    public static SharedData getSharedData() {
        return sharedData;
    }
    
    static FeatureConfiguration getConfig(String name) {
        return configs.get(name);
    }
    
    public static Float getPlayerTemperature(Player player) {
        return RealWeather.getPlayerTemperature(player);
    }
    
    public static int getCheckDelay() {
        return Configurations.getCheckDelay();
    }
}
