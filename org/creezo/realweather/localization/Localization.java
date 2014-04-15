package org.creezo.realweather.localization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.creezo.realweather.RealWeather;
import org.creezo.realweather.util.Utils;

/**
 *
 * @author Dodec
 */
public class Localization {
    private final String MissingEntry = "Missing field in localization";
    private File locFile;
    private final RealWeather plugin;
    private HashMap<String, String> data = new HashMap<String, String>();
    public String lang;
    
    public Localization(RealWeather plugin) {
        this.plugin = plugin;
    }
    
    public boolean firstLoadLanguage(String lang) {
        this.lang = lang;
        locFile = new File(plugin.getDataFolder(), "lang/"+lang+".lang");
        initLocalFile();
        return load();
    }
    
    private boolean load() {
        boolean returnval = true;
        try {
            if (locFile.exists() & locFile.canRead()) {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(locFile), Charset.forName("UTF-8")));
                    String line;
                    data.clear();
                    data = new HashMap<String, String>();
                    while ((line = br.readLine()) != null) {
                        String[] kv = line.split(":", 2);
                        while(kv[1].startsWith(" ")) {
                            kv[1] = kv[1].substring(1);
                        }
                        data.put(kv[0], kv[1].replaceAll("(&([a-f0-9]))", "\u00A7$2"));
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Localization.class.getName()).log(Level.SEVERE, null, ex);
                    returnval = false;
                } catch (IOException ex) {
                    Logger.getLogger(Localization.class.getName()).log(Level.SEVERE, null, ex);
                    returnval = false;
                } finally {
                    try {
                        br.close();
                    } catch (Exception e) {
                    }
                }
            } else {
                RealWeather.log("Locale file " + lang + ".lang is not accessible!");
                returnval = false;
            }
        } catch (Exception e) {
            RealWeather.log.log(Level.SEVERE, null, e);
            RealWeather.sendStackReport(e);
            returnval = false;
        }
        return returnval;
    }

    public String getValue(String key) {
        if (data.containsKey(key)) {
            return data.get(key);
        } else {
            return "Locale ERROR. Key not found";
        }
    }
    
    private void initLocalFile() {
        if(!locFile.exists()) {
            locFile.getParentFile().mkdirs();
            InputStream stream = plugin.getResource(lang+".lang");
            if(stream == null) {
                stream = plugin.getResource("empty.lang");
            }
            Utils.copy(stream, locFile);
            RealWeather.log(lang+".lang file copied.");
        }
    }
    
    public boolean langExists(String _lang) {
        File _locFile = new File(plugin.getDataFolder(), "lang/"+_lang+".lang");
        if(_locFile.exists()) {
            return true;
        } else {
            return plugin.getResource(_lang+".lang") != null;
        }
    }
    
    public boolean setLanguage(String _lang) {
        return firstLoadLanguage(_lang);
    }
}
