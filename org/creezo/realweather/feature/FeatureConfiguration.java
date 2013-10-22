package org.creezo.realweather.feature;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Dodec
 */
public class FeatureConfiguration {
    private final File dataFolder;
    private final YamlConfiguration config;
    private final File confFile;
    private final FeatureLocalization locale;
    
    FeatureConfiguration(File dataFolder) throws IOException, FileNotFoundException, InvalidConfigurationException {
        this.dataFolder = dataFolder;
        config = new YamlConfiguration();
        confFile = new File(dataFolder, "config.yml");
        String lang;
        if(!confFile.exists()) {
            confFile.createNewFile();
            lang = "eng";
        } else {
            config.load(confFile);
            if(!config.contains("language")) config.set("language", "eng");
            lang = config.getString("language");
        }
        locale = new FeatureLocalization(dataFolder, lang);
    }
    
    public File getDataFolder() {
        return dataFolder;
    }

    public void save() throws IOException {
        config.save(confFile);
    }
    
    public YamlConfiguration getYaml() {
        return config;
    }
    public FeatureLocalization getLocale() {
        return locale;
    }
}
