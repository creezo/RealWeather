package org.creezo.realweather.feature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.creezo.realweather.RealWeather;

/**
 *
 * @author Dodec
 */
public class FeatureLocalization {

    private final File dataFolder;
    private final String lang;
    private HashMap<String, String> data = new HashMap<String, String>();

    FeatureLocalization(File dataFolder, String lang) {
        this.dataFolder = dataFolder;
        this.lang = lang;
        load();
    }

    private void load() {
        try {
            File file = new File(dataFolder, lang + ".lang");
            if (file.exists() & file.canRead()) {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] kv = line.split(":", 2);
                        data.put(kv[0], kv[1].replaceAll("(&([a-f0-9]))", "\u00A7$2"));
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FeatureLocalization.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(FeatureLocalization.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        br.close();
                    } catch (Exception e) {
                    }
                }
            } else {
                RealWeather.log("Locale file " + lang + ".lang is not accessible!");
            }
        } catch (Exception e) {
            RealWeather.log.log(Level.SEVERE, null, e);
            RealWeather.sendStackReport(e);
        }
    }

    public String getValue(String key) {
        if (data.containsKey(key)) {
            return data.get(key);
        } else {
            return "Locale ERROR. Key not found";
        }
    }
}
