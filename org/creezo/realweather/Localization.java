package org.creezo.realweather;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 *
 * @author Dodec
 */
public class Localization {
    public String Language;
    public String WinterWarnMessage;
    public String WinterLoginMessage;
    public String WinterInIceBlock;
    public String DesertWarnMessage;
    public String LanguageDescription;
    public String TemperatureShow;
    public String TemperatureHide;
    public String YourStamina;
    public String VLowTemp;
    public String LowTemp;
    public String MedTemp;
    public String HighTemp;
    public String VHighTemp;
    public String CurrentTemperature;
    
    private static Configuration Config = RealWeather.Config;
    private String MissingEntry = "Missing field in localization";
    private FileConfiguration Localization;
    private File LocFile;
    private final RealWeather plugin;
    
    public Localization(RealWeather plugin) {
        this.plugin = plugin;
    }
    
    public void FirstLoadLanguage() {
        LocFile = new File(plugin.getDataFolder(), "localization.yml");
        Localization = new YamlConfiguration();
        InitLocalFile();
        Language = Localization.getString("UseLanguage", "english");
        LoadSpecificLang(Language);
    }
    
    private void LoadSpecificLang(String language) {
        WinterLoginMessage = Localization.getString(language + ".biome.winter.PlayerWarnOnLogin", MissingEntry);
        WinterWarnMessage = Localization.getString(language + ".biome.winter.WarningMessage", MissingEntry);
        WinterInIceBlock = Localization.getString(language + ".biome.winter.FrozenInIce", MissingEntry);
        DesertWarnMessage = Localization.getString(language + ".biome.desert.WarningMessage", MissingEntry);
        LanguageDescription = Localization.getString(language + ".description", MissingEntry);
        TemperatureShow = Localization.getString(language + ".command.TemperatureShow", MissingEntry);
        TemperatureHide = Localization.getString(language + ".command.TemperatureHide", MissingEntry);
        YourStamina = Localization.getString(language + ".command.YourStamina", MissingEntry);
        VLowTemp = Localization.getString(language + ".forecast.VLowTemp", MissingEntry);
        LowTemp = Localization.getString(language + ".forecast.LowTemp", MissingEntry);
        MedTemp = Localization.getString(language + ".forecast.MedTemp", MissingEntry);
        HighTemp = Localization.getString(language + ".forecast.HighTemp", MissingEntry);
        VHighTemp = Localization.getString(language + ".forecast.VHighTemp", MissingEntry);
        CurrentTemperature = Localization.getString(language + ".command.CurrentTemperature", MissingEntry);
        if(Config.getVariables().isDebugMode()) plugin.log.log(Level.INFO, "[RealWeather] Localization loaded: " + language);
    }
    
    private void InitLocalFile() {
        PluginDescriptionFile pdfFile = plugin.getDescription();
        if(!LocFile.exists()) {
            LocFile.getParentFile().mkdirs();
            copy(plugin.getResource("localization.yml"), LocFile);
            plugin.log.log(Level.INFO, "[RealWeather] Localization file copied.");
        }
        LoadLocalizationFields();
        File oldLocalFile = new File("plugins/RealWeather/localization_" + Localization.getString("version", "old") + ".yml");
        if(!pdfFile.getVersion().equals(Localization.getString("version"))) {
            plugin.log.log(Level.INFO, "[RealWeather] Version of localization file doesn't match with current plugin version.");
            try {
                Localization.save(oldLocalFile);
                plugin.log.log(Level.INFO, "[RealWeather] Localization version: " + Localization.getString("version"));
                plugin.log.log(Level.INFO, "[RealWeather] Plugin version: " + pdfFile.getVersion());
                plugin.log.log(Level.INFO, "[RealWeather] Old localization.yml saved.");
            } catch(IOException ex) {
                plugin.log.log(Level.INFO, "[RealWeather] Saving of old config file failed. " + ex.getMessage());
            }
            LocFile.delete();
            copy(plugin.getResource("localization.yml"), LocFile);
            LoadLocalizationFields();
        }
    }
    
    public boolean LangExists(String lang) {
        if(Localization.isConfigurationSection(lang)) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean SetLanguage(String lang) {
        try {
            Localization.set("UseLanguage", (String)lang);
            SaveLocalizationFields();
            LoadSpecificLang(Localization.getString("UseLanguage"));
            Language = Localization.getString("UseLanguage");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    
    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            RealWeather.log.log(Level.INFO, e.getMessage());
        }
    }

    public void LoadLocalizationFields() {
        try {
            Localization.load(LocFile);
        } catch (Exception e) {
            RealWeather.log.log(Level.INFO, e.getMessage());
        }
    }
    
    public void SaveLocalizationFields() {
        try {
            Localization.save(LocFile);
        } catch (Exception e) {
            RealWeather.log.log(Level.INFO, e.getMessage());
        }
    }
}
