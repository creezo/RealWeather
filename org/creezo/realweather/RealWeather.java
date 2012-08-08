package org.creezo.realweather;

import java.io.File;
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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author creezo
 */
public class RealWeather extends JavaPlugin {
    public static boolean Running = true;
    public PlayerListener playerlistener;
    public WeatherListener weatherlistener;
    public PlayerInteract playerinteract;
    public PlayerDamage playerdamage;
    public PlayerMove playermove;
    public static final Logger log = Logger.getLogger("Minecraft");
    public static HashMap<Integer, Integer> PlayerTemperatureThreads;
    public static HashMap<Player, Thread> PlayerDamagerMap = new HashMap<Player, Thread>();
    public static HashMap<Player, Integer> PlayerDamage = new HashMap<Player, Integer>();
    public static HashMap<Integer, Boolean> PlayerHeatShow;
    //public static HashMap<Integer, Boolean> PlayerClientMod;
    public static HashMap<Integer, Boolean> PlayerIceHashMap;
    public static HashMap<Integer, Block> IceBlock;
    public static List<Material> Mats = new ArrayList();
    public static boolean actualWeather = false;
    public static Configuration Config;
    public static PlayerCheck playerCheck;
    public static Localization Localization;
    public static Utils Utils;
    public static Commands Command;
    public PacketListener PListener = new PacketListener();
    public static int ForecastTemp = 0;
    
    public int StatsTask;

    @Override
    public void onEnable() {
        Config = new Configuration(this);
        LoadConfig();
        Config.InitConfig();
        Config.SaveAll();
        Localization = new Localization(this);
        Localization.FirstLoadLanguage();
        log("Language: " + Localization.LanguageDescription);
        playerCheck = new PlayerCheck(this);
        PlayerTemperatureThreads = new HashMap<Integer, Integer>(getServer().getMaxPlayers()+5);
        PlayerHeatShow = new HashMap<Integer, Boolean>(getServer().getMaxPlayers()+5);
        //PlayerClientMod = new HashMap<Integer, Boolean>(getServer().getMaxPlayers()+5);
        PlayerIceHashMap = new HashMap<Integer, Boolean>(getServer().getMaxPlayers()+5);
        IceBlock = new HashMap<Integer, Block>(getServer().getMaxPlayers()+5);
        Utils = new Utils(this);
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
            PlayerTemperatureThreads.put(player.getEntityId(), new Integer(this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TempThread(this, player), Config.getVariables().getStartDelay(Config.getVariables().getGameDifficulty()) * 20, Config.getVariables().getCheckDelay(Config.getVariables().getGameDifficulty()) * 20)));
            PlayerHeatShow.put(player.getEntityId(), false);
        }
        Command = new Commands(this, Config, Localization);
        this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new ForecastThread(this), 10*20, 50*20);
        log.log(Level.INFO, "[RealWeather] RealWeather enabled.");
        StatsTask = this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new StatsSender(this), 10 * 20, 9 * 60 * 20);
        //Bukkit.getMessenger().registerOutgoingPluginChannel(this, "realweather");
        //Bukkit.getMessenger().registerIncomingPluginChannel(this, "realweather", PListener);
    }
    
    @Override
    public void onDisable() {
        Running = false;
        this.getServer().getScheduler().cancelAllTasks();
        PlayerHeatShow.clear();
        PlayerDamage.clear();
        for (Player player : PlayerDamagerMap.keySet()) {
            synchronized (PlayerDamagerMap.get(player)) {
                PlayerDamagerMap.get(player).notify();
            }
            /*try {
                PlayerDamagerMap.get(player).interrupt();
            } catch (SecurityException ex) {
                log(ex.getMessage());
            }*/
        }
        PlayerDamagerMap.clear();
        //PlayerClientMod.clear();
        log.log(Level.INFO, "[RealWeather] RealWeather Disabled!");
    }
    
    public static void log(String mssg) {
        log.log(Level.INFO, (new StringBuilder()).append("[RealWeather] ").append(mssg).toString());
    }
    
    private void LoadConfig() {
        File configFile = new File("plugins/RealWeather/config.yml");
        if(!configFile.exists()) {
            saveDefaultConfig();
            log.log(Level.INFO, "[RealWeather] Default config.yml copied.");
        }
        getConfig().getDefaults();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        String comm = command.getName();
        if(sender instanceof Player) {
            player = (Player) sender;
        }
        if("rw".equalsIgnoreCase(comm)) {
           if(!sender.hasPermission("realweather.player")) {
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
                //} else if("pack".equalsIgnoreCase(args[0])) {
                //    byte[] bytes = ("10.5").getBytes();
                //    player.sendPluginMessage(this, "realweather", bytes);
                } else if("forecast".equalsIgnoreCase(args[0])) {
                    Utils.SendMessage(player, Utils.DoForecast(ForecastTemp));
                } else if("temp".equalsIgnoreCase(args[0])) {
                    if(sender instanceof Player) {
                        if(PlayerHeatShow.get(player.getEntityId()).equals(Boolean.FALSE)) {
                            PlayerHeatShow.put(player.getEntityId(), Boolean.TRUE);
                            Utils.SendMessage(player, Localization.TemperatureShow);
                        } else {
                            PlayerHeatShow.put(player.getEntityId(), Boolean.FALSE);
                            Utils.SendMessage(player, Localization.TemperatureHide);
                        }
                    } else {
                        Utils.SendMessage(player, "Can not be executed from console.");
                    }
                } else if("stamina".equalsIgnoreCase(args[0])) {
                    if(sender instanceof Player) {
                        float stamina = player.getSaturation();
                        Utils.SendMessage(player, Localization.YourStamina + Utils.ConvertFloatToString(stamina));
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
            if(!sender.isOp() && !sender.hasPermission("realweather.admin")) {
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
                } else if("debug".equals(args[0])) {
                    if(Config.getVariables().isDebugMode()) {
                        Config.getVariables().setDebugMode(false);
                        Utils.SendMessage(player, "Debug disabled.");
                    } else {
                        Config.getVariables().setDebugMode(true);
                        Utils.SendMessage(player, "Debug enabled.");
                    }
                } else if("save".equals(args[0])) {
                    if(Config.SaveAll()) {
                        Utils.SendMessage(player, "Configuration saved.");
                    } else {
                        Utils.SendMessage(player, "Error in saving. See console for stack trace.");
                    }
                } else if("load".equals(args[0])) {
                    Config.LoadAll();
                    Utils.SendMessage(player, "Tried to load configuration");
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
                        } else if("jungle".equalsIgnoreCase(args[1])) {
                            Command.Disable(args[1]);
                            Utils.SendMessage(player, "Part \"" + args[1] + "\" disabled!");
                        } else if("thirst".equalsIgnoreCase(args[1])) {
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
                        } else if("jungle".equalsIgnoreCase(args[1])) {
                            Command.Enable(args[1]);
                            Utils.SendMessage(player, "Part \"" + args[1] + "\" enabled!");
                        } else if("thirst".equalsIgnoreCase(args[1])) {
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