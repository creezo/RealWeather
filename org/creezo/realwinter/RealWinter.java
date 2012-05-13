package org.creezo.realwinter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
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
    public PlayerDamage playerdamage;
    public PlayerMove playermove;
    public static final Logger log = Logger.getLogger("Minecraft");
    public static HashMap<Integer, Integer> PlayerHashMap;
    public static HashMap<Integer, Boolean> PlayerIceHashMap;
    public static HashMap<Integer, Block> IceBlock;
    public static List<Material> Mats = new ArrayList();
    public static boolean actualWeather = false;
    public static Configuration Config;
    public static PlayerCheck playerCheck;
    public static Localization Localization;
    public static Utils Utils;
    public static Commands Command;
    
    public int StatsTask;

    @Override
    public void onEnable() {
        Config = new Configuration(this);
        LoadConfig();
        Config.InitConfig();
        Config.InitEquip();
        Localization = new Localization(this);
        Localization.FirstLoadLanguage();
        log.log(Level.INFO, (new StringBuilder()).append("[RealWinter] Language: ").append(Localization.LanguageDescription).toString());
        playerCheck = new PlayerCheck(this);
        playerCheck.PCheckInit();
        PlayerHashMap = new HashMap<Integer, Integer>(getServer().getMaxPlayers()+5);
        PlayerIceHashMap = new HashMap<Integer, Boolean>(getServer().getMaxPlayers()+5);
        IceBlock = new HashMap<Integer, Block>(getServer().getMaxPlayers()+5);
        Utils = new Utils();
        Utils.addMats();
        for(int i=0;i<getServer().getOnlinePlayers().length;i++) {
            Player player = getServer().getOnlinePlayers()[i];
            PlayerIceHashMap.put(player.getEntityId(), playerCheck.isInIce(player));
            if(playerCheck.isInIce(player)) {
                IceBlock.put(player.getEntityId(), player.getLocation().getBlock());
            }
        }
        PluginManager pm = getServer().getPluginManager();
        playerlistener = new PlayerListener(this);
        weatherlistener = new WeatherListener(this);
        playerinteract = new PlayerInteract(this);
        playerdamage = new PlayerDamage(this);
        playermove = new PlayerMove();
        pm.registerEvents(playerlistener, this);
        pm.registerEvents(weatherlistener, this);
        pm.registerEvents(playerinteract, this);
        pm.registerEvents(playerdamage, this);
        pm.registerEvents(playermove, this);
        for(int i=0;i<getServer().getOnlinePlayers().length;i++) {
            Player player = getServer().getOnlinePlayers()[i];
            PlayerHashMap.put(player.getEntityId(), new Integer(this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new CheckTask(this, player), Config.StartDelay * 20, Config.CheckDelay * 20)));
        }
        Command = new Commands(this, Config, Localization);
        log.log(Level.INFO, "[RealWinter] RealWinter enabled.");
        StatsTask = this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new StatsSender(this), 30 * 20, 4 * 60 * 20);
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
        Player player = null;
        String comm = command.getName();
        if(sender instanceof Player) {
            player = (Player) sender;
        }
        if("rw".equalsIgnoreCase(comm)) {
           if(!sender.hasPermission("realwinter.player")) {
                Utils.SendMessage(player, "You must have permissions to perform this command!");
                return true;
            }
            if(args.length == 0) {
                Utils.SendMessage(player, "No arguments set. Try '/rw help'.");
            } else {
                if("help".equalsIgnoreCase(args[0])) {
                    Utils.SendHelp(player);
                } else if("version".equalsIgnoreCase(args[0])) {
                    Utils.SendMessage(player, "Version: " + getDescription().getVersion());
                } else if("heat".equalsIgnoreCase(args[0])) {
                    if(sender instanceof Player) {
                        int heat = playerCheck.checkHeatAround(player, Config.HeatCheckRadius);
                        Utils.SendMessage(player, "Temperature in your position: " + Utils.ConvertIntToString(heat));
                    } else {
                        Utils.SendMessage(player, "Can not be executed from console.");
                    }
                } else if("stamina".equalsIgnoreCase(args[0])) {
                    if(sender instanceof Player) {
                        float stamina = player.getSaturation();
                        Utils.SendMessage(player, "Your stamina: " + Utils.ConvertFloatToString(stamina));
                    } else {
                        try {
                            if(!args[1].isEmpty()) {
                                Player plr = getServer().getPlayerExact(args[1]);
                                Utils.SendMessage(player, "Stamina: " + Utils.ConvertFloatToString(plr.getSaturation()));
                            }
                        } catch(ArrayIndexOutOfBoundsException e) {
                            Utils.SendMessage(player, "Yout must set player name in console to view his stamina!");
                        } catch(Exception e) {
                            Utils.SendMessage(player, "Player name not valid.");
                        }
                    }
                } else {
                    Utils.SendMessage(player, "Invalid command!");
                }
            }
        }
        
        if("rwadmin".equalsIgnoreCase(comm)) {
            if(!sender.isOp() && !sender.hasPermission("realwinter.admin")) {
                Utils.SendMessage(player, "You must be OP to perform this command!");
                return true;
            }
            if(args.length == 0) {
                Utils.SendMessage(player, "No arguments set. Try '/rwadmin help'.");
            } else {
                if("help".equalsIgnoreCase(args[0])) {
                    Utils.SendAdminHelp(player);
                } else if("version".equals(args[0])) {
                    Utils.SendMessage(player, "Version: " + getDescription().getVersion());
                } else if("disable".equals(args[0])) {
                    if(args.length == 1) {
                        Command.Disable();
                        Utils.SendMessage(player, "Globaly disabled!");
                    } else if(args.length == 2) {
                        if("all".equalsIgnoreCase(args[1])) {
                            Command.Disable("all");
                            Utils.SendMessage(player, "All parts disabled!");
                        } else if("winter".equalsIgnoreCase(args[1])) {
                            Command.Disable(args[1]);
                            Utils.SendMessage(player, "Part \"" + args[1] + "\" disabled!");
                        } else if("desert".equalsIgnoreCase(args[1])) {
                            Command.Disable(args[1]);
                            Utils.SendMessage(player, "Part \"" + args[1] + "\" disabled!");
                        } else if("waterbottle".equalsIgnoreCase(args[1])) {
                            Command.Disable(args[1]);
                            Utils.SendMessage(player, "Part \"" + args[1] + "\" disabled!");
                        } else {
                            Utils.SendMessage(player, "Can't disable non-existing part.");
                        }
                    }
                } else if("enable".equalsIgnoreCase(args[0])) {
                    if(args.length == 1) {
                        Command.Enable();
                        Utils.SendMessage(player, "Globaly enabled!");
                    } else if(args.length == 2) {
                        if("all".equalsIgnoreCase(args[1])) {
                            Command.Enable("all");
                            Utils.SendMessage(player, "All parts enabled!");
                        } else if("winter".equalsIgnoreCase(args[1])) {
                            Command.Enable(args[1]);
                            Utils.SendMessage(player, "Part \"" + args[1] + "\" enabled!");
                        } else if("desert".equalsIgnoreCase(args[1])) {
                            Command.Enable(args[1]);
                            Utils.SendMessage(player, "Part \"" + args[1] + "\" enabled!");
                        } else if("waterbottle".equalsIgnoreCase(args[1])) {
                            Command.Enable(args[1]);
                            Utils.SendMessage(player, "Part \"" + args[1] + "\" enabled!");
                        } else {
                            Utils.SendMessage(player, "Can't enable non-existing part.");
                        }
                    }
                } else if("lang".equalsIgnoreCase(args[0])) {
                    if(args.length == 1) {
                        Utils.SendMessage(player, "Language: " + Localization.Language + ".");
                    } else {
                        if(args.length == 2) {
                            boolean result = Command.Language(args[1]);
                            if(result == true) {
                                Utils.SendMessage(player, "Language changed to: " + Localization.LanguageDescription + ".");
                            } else {
                                Utils.SendMessage(player, "Language load error!");
                            }
                        }
                    }
                } else {
                    Utils.SendMessage(player, "Invalid command!");
                }
            }
        }
        return true;
    }
}