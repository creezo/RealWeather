package org.creezo.realwinter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author creezo
 */
public class RealWinter extends JavaPlugin {
    public PlayerListener playerlistener;
    public WeatherListener weatherlistener;
    public PlayerInteract playerinteract;
    public static final Logger log = Logger.getLogger("Minecraft");
    public static HashMap<Integer, Integer> PlayerHashMap;
    public static boolean actualWeather = false;
    public static Configuration Config;
    public static PlayerCheck playerCheck;
    public static Localization Localization;
    public static Utils Util;
    public static Commands Command;

    @Override
    public void onEnable() {
        Config = new Configuration(this);
        LoadConfig();
        Config.InitConfig();
        Config.InitEquip(this);
        Localization = new Localization();
        Localization.LoadLanguage(this);
        log.log(Level.INFO, (new StringBuilder()).append("[RealWinter] Language: ").append(Localization.LanguageDescription).toString());
        playerCheck = new PlayerCheck();
        playerCheck.PCheckInit();
        PlayerHashMap = new HashMap<Integer, Integer>(getServer().getMaxPlayers()+1);
        Util = new Utils();
        PluginManager pm = getServer().getPluginManager();
        playerlistener = new PlayerListener(this);
        weatherlistener = new WeatherListener();
        playerinteract = new PlayerInteract();
        pm.registerEvents(playerlistener, this);
        pm.registerEvents(weatherlistener, this);
        pm.registerEvents(playerinteract, this);
        Command = new Commands(this, Config, playerlistener);
        log.log(Level.INFO, "[RealWinter] RealWinter enabled.");
    }
    
    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelAllTasks();
        log.log(Level.INFO, "[RealWinter] RealWinter Disabled!");
    }
    
    private void LoadConfig() {
        PluginDescriptionFile pdfFile = this.getDescription();
        File oldConfigFile = new File("plugins/RealWinter/config_" + getConfig().getString("version", "old") + ".yml");
        File configFile = new File("plugins/RealWinter/config.yml");
        if(!configFile.exists()) {
            saveDefaultConfig();
            log.log(Level.INFO, "[RealWinter] Default Config.yml copied.");
        }
        if(!pdfFile.getVersion().equals(getConfig().getString("version"))) {
            log.log(Level.INFO, "[RealWinter] Version of config file doesn't match with current plugin version.");
            getConfig().getDefaults();
            try {
                getConfig().save(oldConfigFile);
                log.log(Level.INFO, "[RealWinter] Config version: " + this.getConfig().getString("version"));
                log.log(Level.INFO, "[RealWinter] Plugin version: " + pdfFile.getVersion());
                log.log(Level.INFO, "[RealWinter] Old Config.yml saved.");
            } catch(IOException ex) {
                log.log(Level.INFO, "[RealWinter] Saving of old config file failed. " + ex.getMessage());
            }
            configFile.delete();
            saveDefaultConfig();
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        log.log(Level.INFO, "Command");
        Player player = null;
        String comm = command.getName();
        if(sender instanceof Player) {
            log.log(Level.INFO, "Instance of player");
            player = (Player) sender;
            if(!player.isOp()) {
                Util.SendMessage(player, "You must be OP to perform this command!");
                return true;
            }
        }
        if("realwinter".equals(comm)) {
            if(args.length == 0) {
                Util.SendMessage(player, "No arguments set. Try '/rw help'.");
            } else {
                if("help".equals(args[0])) {
                    Util.SendHelp(player);
                } else if("version".equals(args[0])) {
                    Util.SendMessage(player, "RealWinter: version: " + getDescription().getVersion());
                } else if("disable".equals(args[0])) {
                    if(args.length == 1) {
                        Command.Disable();
                        Util.SendMessage(player, "RealWinter: globaly disabled!");
                    } else if(args.length == 2) {
                        if("all".equals(args[1])) {
                            Command.Disable("all");
                            Util.SendMessage(player, "RealWinter: all parts disabled!");
                        } else if("winter".equals(args[1])) {
                            Command.Disable(args[1]);
                            Util.SendMessage(player, "RealWinter: Part \"" + args[1] + "\" disabled!");
                        } else if("desert".equals(args[1])) {
                            Command.Disable(args[1]);
                            Util.SendMessage(player, "RealWinter: Part \"" + args[1] + "\" disabled!");
                        } else if("waterbottle".equals(args[1])) {
                            Command.Disable(args[1]);
                            Util.SendMessage(player, "RealWinter: Part \"" + args[1] + "\" disabled!");
                        } else {
                            Util.SendMessage(player, "Can't disable non-existing part.");
                        }
                    }
                } else if("enable".equals(args[0])) {
                    if(args.length == 1) {
                        Command.Enable();
                        Util.SendMessage(player, "RealWinter: globaly enabled!");
                    } else if(args.length == 2) {
                        if("all".equals(args[1])) {
                            Command.Enable("all");
                            Util.SendMessage(player, "RealWinter: all parts enabled!");
                        } else if("winter".equals(args[1])) {
                            Command.Enable(args[1]);
                            Util.SendMessage(player, "RealWinter: Part \"" + args[1] + "\" enabled!");
                        } else if("desert".equals(args[1])) {
                            Command.Enable(args[1]);
                            Util.SendMessage(player, "RealWinter: Part \"" + args[1] + "\" enabled!");
                        } else if("waterbottle".equals(args[1])) {
                            Command.Enable(args[1]);
                            Util.SendMessage(player, "RealWinter: Part \"" + args[1] + "\" enabled!");
                        } else {
                            Util.SendMessage(player, "Can't enable non-existing part.");
                        }
                    }
                }
            }
        }
        return true;
    }
}