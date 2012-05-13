/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

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
    
    private static Configuration Config = RealWinter.Config;
    private String MissingEntry = "Missing field in localization";
    private FileConfiguration Localization;
    private File LocFile;
    private final RealWinter plugin;
    
    public Localization(RealWinter plugin) {
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
        if(Config.DebugMode) plugin.log.log(Level.INFO, "[RealWinter] Localization loaded: " + language);
    }
    
    private void InitLocalFile() {
        PluginDescriptionFile pdfFile = plugin.getDescription();
        if(!LocFile.exists()) {
            LocFile.getParentFile().mkdirs();
            copy(plugin.getResource("localization.yml"), LocFile);
            plugin.log.log(Level.INFO, "[RealWinter] Localization file copied.");
        }
        LoadLocalizationFields();
        File oldLocalFile = new File("plugins/RealWinter/localization_" + Localization.getString("version", "old") + ".yml");
        if(!pdfFile.getVersion().equals(Localization.getString("version"))) {
            plugin.log.log(Level.INFO, "[RealWinter] Version of localization file doesn't match with current plugin version.");
            try {
                Localization.save(oldLocalFile);
                plugin.log.log(Level.INFO, "[RealWinter] Localization version: " + Localization.getString("version"));
                plugin.log.log(Level.INFO, "[RealWinter] Plugin version: " + pdfFile.getVersion());
                plugin.log.log(Level.INFO, "[RealWinter] Old localization.yml saved.");
            } catch(IOException ex) {
                plugin.log.log(Level.INFO, "[RealWinter] Saving of old config file failed. " + ex.getMessage());
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
            RealWinter.log.log(Level.INFO, e.getMessage());
        }
    }

    public void LoadLocalizationFields() {
        try {
            Localization.load(LocFile);
        } catch (Exception e) {
            RealWinter.log.log(Level.INFO, e.getMessage());
        }
    }
    
    public void SaveLocalizationFields() {
        try {
            Localization.save(LocFile);
        } catch (Exception e) {
            RealWinter.log.log(Level.INFO, e.getMessage());
        }
    }
}
