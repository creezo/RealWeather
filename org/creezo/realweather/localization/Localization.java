package org.creezo.realweather.localization;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.creezo.realweather.RealWeather;
import org.creezo.realweather.util.Utils;

/**
 *
 * @author Dodec
 */
public class Localization {
    public String Language;
    public String FreezingWarnMessage;
    public String FreezingLoginMessage;
    public String FreezingInIceBlock;
    public String ExhaustingWarnMessage;
    public String Refreshed;
    public String LanguageDescription;
    public String Temperature;
    public String TemperatureShow;
    public String TemperatureHide;
    public String YourStamina;
    public String CurrentTemperature;
    
    private String MissingEntry = "Missing field in localization";
    private FileConfiguration Localization;
    private File LocFile;
    private final RealWeather plugin;
    
    public Localization(RealWeather plugin) {
        this.plugin = plugin;
    }
    
    public void firstLoadLanguage() {
        LocFile = new File(plugin.getDataFolder(), "localization.yml");
        Localization = new YamlConfiguration();
        InitLocalFile();
        Language = Localization.getString("UseLanguage", "english");
        LoadSpecificLang(Language);
    }
    
    private void LoadSpecificLang(String language) {
        FreezingLoginMessage = Localization.getString(language + ".effect.freezing.PlayerWarnOnLogin", MissingEntry).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        FreezingWarnMessage = Localization.getString(language + ".effect.freezing.WarningMessage", MissingEntry).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        FreezingInIceBlock = Localization.getString(language + ".effect.freezing.FrozenInIce", MissingEntry).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        ExhaustingWarnMessage = Localization.getString(language + ".effect.exhausting.WarningMessage", MissingEntry).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        Refreshed = Localization.getString(language + ".effect.exhausting.Refreshed", MissingEntry).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        LanguageDescription = Localization.getString(language + ".description", MissingEntry).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        Temperature = Localization.getString(language + ".command.Temperature", MissingEntry).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        TemperatureShow = Localization.getString(language + ".command.TemperatureShow", MissingEntry).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        TemperatureHide = Localization.getString(language + ".command.TemperatureHide", MissingEntry).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        YourStamina = Localization.getString(language + ".command.YourStamina", MissingEntry).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        CurrentTemperature = Localization.getString(language + ".command.CurrentTemperature", MissingEntry).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        if(RealWeather.isDebug()) RealWeather.log("Localization loaded: " + language);
    }
    
    private void InitLocalFile() {
        PluginDescriptionFile pdfFile = plugin.getDescription();
        if(!LocFile.exists()) {
            LocFile.getParentFile().mkdirs();
            Utils.copy(plugin.getResource("localization.yml"), LocFile);
            RealWeather.log("Localization file copied.");
        }
        LoadLocalizationFields();
        File oldLocalFile = new File(plugin.getDataFolder(), "localization_" + Localization.getString("version", "old") + ".yml");
        if(!pdfFile.getVersion().equals(Localization.getString("version"))) {
            RealWeather.log("Version of localization file doesn't match with current plugin version.");
            try {
                Localization.save(oldLocalFile);
                RealWeather.log("Localization version: " + Localization.getString("version"));
                RealWeather.log("Plugin version: " + pdfFile.getVersion());
                RealWeather.log("Old localization.yml saved.");
            } catch(IOException ex) {
                RealWeather.log("Saving of old config file failed. " + ex.getMessage());
            }
            LocFile.delete();
            Utils.copy(plugin.getResource("localization.yml"), LocFile);
            LoadLocalizationFields();
        } else {
            RealWeather.log("Localization file             OK.");
        }
    }
    
    public boolean LangExists(String lang) {
        if(Localization.isConfigurationSection(lang)) {
            return true;
        } else {
            return false;
        }
    }
    
    public HashMap<String, String> GetLangList() {
        HashMap<String, String> langs = new HashMap<String, String>();
        for (String lang : Localization.getKeys(false)) {
            if(lang.equals("version") || lang.equals("UseLanguage")) continue;
            if(Localization.contains(lang+".description")) langs.put(lang, Localization.getString(lang+".description"));
        }
        return langs;
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

    public void LoadLocalizationFields() {
        try {
            Localization.load(LocFile);
        } catch (Exception e) {
            RealWeather.log.log(Level.WARNING, null, e);
        }
    }
    
    public void SaveLocalizationFields() {
        try {
            Localization.save(LocFile);
        } catch (Exception e) {
            RealWeather.log.log(Level.WARNING, null, e);
        }
    }
}
