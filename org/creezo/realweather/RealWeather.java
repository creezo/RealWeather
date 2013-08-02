package org.creezo.realweather;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.creezo.realweather.weather.MakeWeather;
import org.creezo.realweather.weather.Weather;

/**
 *
 * @author creezo
 */
public class RealWeather extends JavaPlugin {
    public boolean Running = true;
    public PlayerListener playerlistener;
    public WeatherListener weatherlistener;
    public PlayerInteract playerinteract;
    public PlayerDamage playerdamage;
    public PlayerMove playermove;
    public static final Logger log = Logger.getLogger("Minecraft");
    public HashMap<Integer, Integer> PlayerTemperatureThreads;
    public HashMap<Player, Thread> PlayerDamagerMap = new HashMap<Player, Thread>();
    public HashMap<Player, Integer> PlayerDamage = new HashMap<Player, Integer>();
    public HashMap<Integer, Boolean> PlayerHeatShow;
    public HashMap<Integer, Boolean> PlayerClientMod;
    //public HashMap<Integer, Boolean> PlayerIceHashMap;
    //public HashMap<Integer, Block> IceBlock;
    public HashMap<Material, Double> HeatSources;
    public HashMap<Material, Double> HeatInHand;
    public HashMap<Player, Integer> PlayerRefreshing = new HashMap<Player, Integer>();
    public HashMap<Player, Double> PlayerTemperature = new HashMap<Player, Double>();
    public static List<Material> Mats = new ArrayList<Material>();
    public boolean actualWeather = false;
    public Configuration Config;
    public CheckCenter checkCenter;
    public Localization Localization;
    public Utils Utils;
    public Commands Command;
    public PacketListener PListener = new PacketListener(this);
    public int ForecastTemp = 0;
    private DecimalFormat df = new DecimalFormat("##.#");
    private WeatherAPI WeatherAPI;
    private File persistenceWeatherFile = new File("plugins/RealWeather/storage/weather.yml");
    private FileConfiguration persWeather;
    public Weather[] weather = new Weather[5];
    public MakeWeather mWeather;
    
    private FileConfiguration getPersistanceWeather() {
        FileConfiguration _persWeather = new YamlConfiguration();
        if(!persistenceWeatherFile.exists()) {
            this.log("Creating weather storage file.");
            persistenceWeatherFile.getParentFile().mkdirs();
            Utils.copy(this.getResource("storage/weather.yml"), persistenceWeatherFile);
        }
        try {
            _persWeather.load(persistenceWeatherFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RealWeather.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RealWeather.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(RealWeather.class.getName()).log(Level.SEVERE, null, ex);
        }
        return _persWeather;
    }
    
    private void addWeathers() {
        weather[0] = Weather.CLEAR;
        weather[1] = Weather.CLEAR;
        weather[2] = Weather.valueOf(persWeather.getString("W2", "CLEAR"));
        weather[3] = Weather.valueOf(persWeather.getString("W3", "CLEAR"));
        weather[4] = Weather.valueOf(persWeather.getString("W4", "CLEAR"));
    }
    
    @Override
    public void onEnable() {
        try {
            HeatSources = new HashMap<Material, Double>();
            HeatInHand = new HashMap<Material, Double>();
            PlayerTemperatureThreads = new HashMap<Integer, Integer>();
            PlayerHeatShow = new HashMap<Integer, Boolean>();
            PlayerClientMod = new HashMap<Integer, Boolean>();
            //PlayerIceHashMap = new HashMap<Integer, Boolean>();
            //IceBlock = new HashMap<Integer, Block>();
            Config = new Configuration(this);
            LoadConfig();
            Config.InitConfig();
            Config.SaveAll();
            Utils = new Utils(this);
            Utils.addMats();
            persWeather = getPersistanceWeather();
            addWeathers();
            mWeather = new MakeWeather(this);
            Localization = new Localization(this);
            Localization.FirstLoadLanguage();
            log("Language: " + Localization.LanguageDescription);
            checkCenter = new CheckCenter(this);
            /*for(int i=0;i<getServer().getOnlinePlayers().length;i++) {
                Player player = getServer().getOnlinePlayers()[i];
                PlayerIceHashMap.put(player.getEntityId(), checkCenter.isInIce(player));
                if(checkCenter.isInIce(player)) {
                    IceBlock.put(player.getEntityId(), player.getLocation().getBlock());
                }
            }*/
            PluginManager pm = getServer().getPluginManager();
            playerlistener = new PlayerListener(this);
            weatherlistener = new WeatherListener(this);
            playerinteract = new PlayerInteract(this);
            playerdamage = new PlayerDamage(this);
            playermove = new PlayerMove(this);
            pm.registerEvents(playerlistener, this);
            pm.registerEvents(weatherlistener, this);
            pm.registerEvents(playerinteract, this);
            pm.registerEvents(playerdamage, this);
            pm.registerEvents(playermove, this);
            for(int i=0;i<getServer().getOnlinePlayers().length;i++) {
                Player player = getServer().getOnlinePlayers()[i];
                PlayerTemperatureThreads.put(player.getEntityId(), new Integer(this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TempThread(this, player), Config.getVariables().getStartDelay(Config.getVariables().getGameDifficulty()) * 20, Config.getVariables().getCheckDelay(Config.getVariables().getGameDifficulty()) * 20)));
                PlayerHeatShow.put(player.getEntityId(), false);
                PlayerClientMod.put(player.getEntityId(), false);
            }
            Command = new Commands(this);
            WeatherAPI = new WeatherAPI(this);
            this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ForecastThread(this), 10*20, 50*20);
            this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new RefresherThread(this), 2*20, 30);
            log.log(Level.INFO, "[RealWeather] RealWeather enabled.");
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, "realweather");
            Bukkit.getMessenger().registerIncomingPluginChannel(this, "realweather", PListener);
            getServer().getServicesManager().register(WeatherAPI.class, WeatherAPI, this, ServicePriority.Low);
        } catch(NullPointerException e) {
            log.log(Level.WARNING, null, e);
            this.setEnabled(false);
            this.Running = false;
        }
    }
    
    @Override
    public void onDisable() {
        Config.SaveAll();
        Running = false;
        this.getServer().getScheduler().cancelAllTasks();
        PlayerHeatShow.clear();
        PlayerDamage.clear();
        for (Player player : PlayerDamagerMap.keySet()) {
            synchronized (PlayerDamagerMap.get(player)) {
                PlayerDamagerMap.get(player).notify();
            }
        }
        if(!persistenceWeatherFile.exists()) {
            persistenceWeatherFile.getParentFile().mkdirs();
            Utils.copy(this.getResource("storage/weather.yml"), persistenceWeatherFile);
        }
        try {
            persWeather.load(persistenceWeatherFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RealWeather.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RealWeather.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(RealWeather.class.getName()).log(Level.SEVERE, null, ex);
        }
        persWeather.set("W2", weather[2].name());
        persWeather.set("W3", weather[3].name());
        persWeather.set("W4", weather[4].name());
        try {
            persWeather.save(persistenceWeatherFile);
        } catch (IOException ex) {
            Logger.getLogger(RealWeather.class.getName()).log(Level.SEVERE, null, ex);
        }
        PlayerDamagerMap.clear();
        PlayerClientMod.clear();
        PlayerRefreshing.clear();
        PlayerTemperature.clear();
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
        log.log(Level.INFO, "[RealWeather] RealWeather Disabled!");
    }
    
    public void log(String mssg) {
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
                if(sender instanceof Player) {
                    if(PlayerTemperature.containsKey(player))
                        Utils.SendMessage(player, Localization.Temperature + df.format(PlayerTemperature.get(player)));
                    Utils.SendMessage(player, Localization.YourStamina + df.format(player.getSaturation()));
                    Utils.SendMessage(player, Localization.FCToday + ForecastThread.getForecastMessage(weather[2], this.Localization));
                }
            } else {
                if("help".equalsIgnoreCase(args[0])) {
                    Utils.SendHelp(player);
                } else if("version".equalsIgnoreCase(args[0])) {
                    Utils.SendMessage(player, "Version: " + getDescription().getVersion());
                } else if("forecast".equalsIgnoreCase(args[0])) {
                    Utils.SendMessage(player, Localization.FCToday + ForecastThread.getForecastMessage(weather[2], this.Localization));
                    Utils.SendMessage(player, Localization.FCTomorrow + ForecastThread.getForecastMessage(weather[3], this.Localization));
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
                } else if("set".equals(args[0])) {
                    if(args.length != 4) {
                        Utils.SendMessage(player, "You must set: <file>, <config key>, <value>. Ex: /rwadmin set global PlayerHeat 1");
                    } else {
                        if(Command.Set(args)) {
                            Config.SaveAll();
                            Utils.SendMessage(player, "Value set.");
                        } else {
                            Utils.SendMessage(player, "Failed to set value.");
                        }
                    }
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
                        } else if("freezing".equalsIgnoreCase(args[1])) {
                            Command.Disable(args[1]);
                            Utils.SendMessage(player, "Part \"" + args[1] + "\" disabled!");
                        } else if("exhausting".equalsIgnoreCase(args[1])) {
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
                            Utils.SendMessage(player, "Available parts: freezing, exhausting, jungle, thirst, waterbottle");
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
                        } else if("freezing".equalsIgnoreCase(args[1])) {
                            Command.Enable(args[1]);
                            Utils.SendMessage(player, "Part \"" + args[1] + "\" enabled!");
                        } else if("exhausting".equalsIgnoreCase(args[1])) {
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
                            Utils.SendMessage(player, "Available parts: freezing, exhausting, jungle, thirst, waterbottle");
                        }
                    }
                } else if("lang".equalsIgnoreCase(args[0])) {
                    if(args.length == 1) {
                        Utils.SendMessage(player, "Language: " + Localization.Language + ".");
                        Utils.SendMessage(player, "Available languages:");
                        HashMap<String, String> langs = Localization.GetLangList();
                        for (String lang : langs.keySet()) {
                            Utils.SendMessage(player, lang+" - "+langs.get(lang));
                        }
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