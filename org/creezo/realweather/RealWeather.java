package org.creezo.realweather;

/*import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;*/
import org.creezo.realweather.localization.Localization;
import org.creezo.realweather.util.Utils;
import org.creezo.realweather.configuration.Configuration;
import org.creezo.realweather.command.Commands;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.creezo.realweather.configuration.Configurations.Armors;
import org.creezo.realweather.event.EventManager;
import org.creezo.realweather.feature.FeatureLocalization;
import org.creezo.realweather.feature.FeatureManager;
import org.creezo.realweather.thread.ThreadManager;

/**
 *
 * @author creezo
 */
public class RealWeather extends JavaPlugin {

    public static final Logger log = Logger.getLogger("Minecraft");
    private FeatureManager featureManager;
    private EventManager eventManager;
    private ThreadManager threadManager;
    static Configuration config;
    public CheckCenter checkCenter;
    public Localization localization;
    public Utils utils;
    public Commands command;
    private WeatherAPI weatherAPI;
    private boolean isWeatherModuleLoaded = false;
    private WeatherCompat weatherCompat = null;
    private static ArrayList<Thread> reports = new ArrayList<Thread>();
    private static long lastReport = 0;
    private static String version = "0";
    //public HashMap<Player, Thread> playerDamagerMap = new HashMap<Player, Thread>();
    //public HashMap<Player, Integer> playerDamage = new HashMap<Player, Integer>();
    public HashMap<Integer, Boolean> playerHeatShow;
    public HashMap<Integer, Boolean> playerClientMod;
    public HashMap<Material, Double> heatSources;
    public HashMap<Material, Double> heatInHand;
    //public HashMap<Player, Integer> PlayerRefreshing = new HashMap<Player, Integer>();
    static HashMap<Player, Double> playerTemperature = new HashMap<Player, Double>();
    private DecimalFormat df = new DecimalFormat("##.#");
    //private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        try {
            //Init classes
            version = this.getDescription().getVersion();
            heatSources = new HashMap<Material, Double>();
            heatInHand = new HashMap<Material, Double>();
            playerHeatShow = new HashMap<Integer, Boolean>();
            playerClientMod = new HashMap<Integer, Boolean>();
            config = new Configuration(this);
            utils = new Utils(this);
            localization = new Localization(this);
            checkCenter = new CheckCenter(this);
            command = new Commands(this);
            weatherAPI = new WeatherAPI(this);
            featureManager = new FeatureManager(this);
            eventManager = new EventManager(this); //REQ: playerHeatShow, 
            threadManager = new ThreadManager(getServer().getScheduler(), this, config); //REQ: config
            //protocolManager = ProtocolLibrary.getProtocolManager();

            //Load plugin
            loadConfig();
            config.initConfig();
            config.saveAll();
            //weathermanager.enable();
            localization.firstLoadLanguage(config.getVariables().getLanguage());
            log("Language: " + localization.getValue("Description"));
            featureManager.init();
            featureManager.enable();
            featureManager.registerEvents(this.getServer().getPluginManager());
            eventManager.registerEvents();
            eventManager.registerPluginChannel("realweather");

            //Start threads
            //threadManager.startThread(2);
            for (int i = 0; i < getServer().getOnlinePlayers().length; i++) {
                Player player = getServer().getOnlinePlayers()[i];
                threadManager.startTempThread(player);
                playerHeatShow.put(player.getEntityId(), false);
                playerClientMod.put(player.getEntityId(), false);
            }

            log.log(Level.INFO, "[RealWeather] RealWeather enabled.");
            getServer().getServicesManager().register(WeatherAPI.class, weatherAPI, this, ServicePriority.Low);

            /*protocolManager.addPacketListener(
                    new PacketAdapter(this, ConnectionSide.SERVER_SIDE,
                    ListenerPriority.NORMAL, Packets.Server.CUSTOM_PAYLOAD) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    // Item packets
                    switch (event.getPacketID()) {
                        case Packets.Server.CUSTOM_PAYLOAD:
                            System.out.println("Packet recieved");
                            String data = new String(event.getPacket().getByteArrays().read(0));
                            System.out.println(data);
                            if(data.startsWith("RW:")) {
                                RealWeather.log("Player "+event.getPlayer().getPlayerListName()+" connected with RW client mod.");
                                playerClientMod.put(event.getPlayer().getEntityId(), true);
                            }
                            break;
                    }
                }
            });*/
        } catch (NullPointerException e) {
            log.log(Level.WARNING, null, e);
            sendStackReport(e);
            this.setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        try {
            config.saveAll();
            this.getServer().getScheduler().cancelAllTasks();
            playerHeatShow.clear();
            /*playerDamage.clear();
             for (Player player : playerDamagerMap.keySet()) {
             synchronized (playerDamagerMap.get(player)) {
             playerDamagerMap.get(player).notify();
             }
             }*/
            featureManager.disable();
            //playerDamagerMap.clear();
            playerClientMod.clear();
            //PlayerRefreshing.clear();
            playerTemperature.clear();
            Bukkit.getMessenger().unregisterIncomingPluginChannel(this);
            Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
            log("[RealWeather] RealWeather Disabled!");
        } catch (Exception e) {
            sendStackReport(e);
        }
    }

    public static void log(String mssg) {
        log.log(Level.INFO, (new StringBuilder()).append("[RealWeather] ").append(mssg).toString());
    }

    private void loadConfig() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
            log("[RealWeather] Default config.yml copied.");
        }
        getConfig().getDefaults();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            Player player = null;
            String comm = command.getName();
            if (sender instanceof Player) {
                player = (Player) sender;
            }
            if ("rw".equalsIgnoreCase(comm)) {
                if (!sender.hasPermission("realweather.player")) {
                    utils.sendMessage(player, "You must have permissions to perform this command!");
                    return true;
                }
                if (args.length == 0) {
                    utils.sendMessage(player, "No arguments set. Try '/rw help'.");
                    if (sender instanceof Player) {
                        if (playerTemperature.containsKey(player)) {
                            utils.sendMessage(player, localization.getValue("Temperature") + df.format(playerTemperature.get(player)));
                        }
                        utils.sendMessage(player, localization.getValue("YourStamina") + df.format(player.getSaturation()));
                        if (isWeatherModuleLoaded) {
                            utils.sendMessage(player, featureManager.getModule("weather").getConfig().getLocale().getValue("Today") + getWeather().getForecastMessage(getWeather().getCurrentWeather(2)));
                        }
                    }
                } else {
                    if ("help".equalsIgnoreCase(args[0])) {
                        utils.sendHelp(player);
                    } else if ("version".equalsIgnoreCase(args[0])) {
                        utils.sendMessage(player, "Version: " + getDescription().getVersion());
                    } else if ("forecast".equalsIgnoreCase(args[0])) {
                        if (isWeatherModuleLoaded) {
                            utils.sendMessage(player, featureManager.getModule("weather").getConfig().getLocale().getValue("Today") + getWeather().getForecastMessage(getWeather().getCurrentWeather(2)));
                            utils.sendMessage(player, featureManager.getModule("weather").getConfig().getLocale().getValue("Tomorrow") + getWeather().getForecastMessage(getWeather().getCurrentWeather(3)));
                        }
                    } else if ("temp".equalsIgnoreCase(args[0])) {
                        if (sender instanceof Player) {
                            if (playerHeatShow.get(player.getEntityId()).equals(Boolean.FALSE)) {
                                playerHeatShow.put(player.getEntityId(), Boolean.TRUE);
                                utils.sendMessage(player, localization.getValue("TemperatureShow"));
                            } else {
                                playerHeatShow.put(player.getEntityId(), Boolean.FALSE);
                                utils.sendMessage(player, localization.getValue("TemperatureHide"));
                            }
                        } else {
                            utils.sendMessage(player, "Can not be executed from console.");
                        }
                    } else if ("stamina".equalsIgnoreCase(args[0])) {
                        if (sender instanceof Player) {
                            float stamina = player.getSaturation();
                            utils.sendMessage(player, localization.getValue("YourStamina") + Utils.convertFloatToString(stamina));
                        } else {

                            try {
                                if (!args[1].isEmpty()) {
                                    Player plr = getServer().getPlayerExact(args[1]);
                                    utils.sendMessage(player, "Stamina: " + Utils.convertFloatToString(plr.getSaturation()));
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                utils.sendMessage(player, "Yout must set player name in console to view his stamina!");
                            } catch (Exception e) {
                                utils.sendMessage(player, "Player name not valid.");
                            }
                        }
                    } else {
                        utils.sendMessage(player, "Invalid command!");
                    }
                }
            }

            if ("rwadmin".equalsIgnoreCase(comm)) {
                if (!sender.isOp() && !sender.hasPermission("realweather.admin")) {
                    utils.sendMessage(player, "You must be OP to perform this command!");
                    return true;
                }
                if (args.length == 0) {
                    utils.sendMessage(player, "No arguments set. Try '/rwadmin help'.");
                } else {
                    if ("help".equalsIgnoreCase(args[0])) {
                        utils.sendAdminHelp(player);
                    } else if ("version".equals(args[0])) {
                        utils.sendMessage(player, "Version: " + getDescription().getVersion());
                    }/* else if("set".equals(args[0])) {
                     if(args.length != 4) {
                     utils.sendMessage(player, "You must set: <file>, <config key>, <value>. Ex: /rwadmin set global PlayerHeat 1");
                     } else {
                     if(this.command.Set(args)) {
                     config.saveAll();
                     utils.sendMessage(player, "Value set.");
                     } else {
                     utils.sendMessage(player, "Failed to set value.");
                     }
                     }
                     }*/ else if ("debug".equals(args[0])) {
                        if (config.getVariables().isDebugMode()) {
                            config.getVariables().setDebugMode(false);
                            utils.sendMessage(player, "Debug disabled.");
                        } else {
                            config.getVariables().setDebugMode(true);
                            utils.sendMessage(player, "Debug enabled.");
                        }
                    } else if ("save".equals(args[0])) {
                        if (config.saveAll()) {
                            utils.sendMessage(player, "Configuration saved.");
                        } else {
                            utils.sendMessage(player, "Error in saving. See console for stack trace.");
                        }
                    } else if ("load".equals(args[0])) {
                        config.LoadAll();
                        utils.sendMessage(player, "Tried to load configuration");
                    } else if ("walk".equals(args[0])) {
                        try {
                            getServer().getPlayer(args[1]).setWalkSpeed(0.2F);
                            utils.sendMessage(player, "Player speed set to default (0.2). SUCCESS");
                        } catch (NullPointerException e) {
                            utils.sendMessage(player, "Player speed set to default (0.2). FAILED (Missing name?)");
                        }
                    } else if ("disable".equals(args[0])) {
                        if (args.length == 1) {
                            this.command.Disable();
                            utils.sendMessage(player, "Globaly disabled!");
                        }/* else if(args.length == 2) {
                         if("all".equalsIgnoreCase(args[1])) {
                         this.command.Disable("all");
                         utils.sendMessage(player, "All parts disabled!");
                         } else if("freezing".equalsIgnoreCase(args[1])) {
                         this.command.Disable(args[1]);
                         utils.sendMessage(player, "Part \"" + args[1] + "\" disabled!");
                         } else if("exhausting".equalsIgnoreCase(args[1])) {
                         this.command.Disable(args[1]);
                         utils.sendMessage(player, "Part \"" + args[1] + "\" disabled!");
                         } else if("jungle".equalsIgnoreCase(args[1])) {
                         this.command.Disable(args[1]);
                         utils.sendMessage(player, "Part \"" + args[1] + "\" disabled!");
                         } else if("thirst".equalsIgnoreCase(args[1])) {
                         this.command.Disable(args[1]);
                         utils.sendMessage(player, "Part \"" + args[1] + "\" disabled!");
                         } else if("waterbottle".equalsIgnoreCase(args[1])) {
                         this.command.Disable(args[1]);
                         utils.sendMessage(player, "Part \"" + args[1] + "\" disabled!");
                         } else {
                         utils.sendMessage(player, "Can't disable non-existing part.");
                         utils.sendMessage(player, "Available parts: freezing, exhausting, jungle, thirst, waterbottle");
                         }
                         }*/
                    } else if ("enable".equalsIgnoreCase(args[0])) {
                        if (args.length == 1) {
                            this.command.Enable();
                            utils.sendMessage(player, "Globaly enabled!");
                        }/* else if(args.length == 2) {
                         if("all".equalsIgnoreCase(args[1])) {
                         this.command.Enable("all");
                         utils.sendMessage(player, "All parts enabled!");
                         } else if("freezing".equalsIgnoreCase(args[1])) {
                         this.command.Enable(args[1]);
                         utils.sendMessage(player, "Part \"" + args[1] + "\" enabled!");
                         } else if("exhausting".equalsIgnoreCase(args[1])) {
                         this.command.Enable(args[1]);
                         utils.sendMessage(player, "Part \"" + args[1] + "\" enabled!");
                         } else if("jungle".equalsIgnoreCase(args[1])) {
                         this.command.Enable(args[1]);
                         utils.sendMessage(player, "Part \"" + args[1] + "\" enabled!");
                         } else if("thirst".equalsIgnoreCase(args[1])) {
                         this.command.Enable(args[1]);
                         utils.sendMessage(player, "Part \"" + args[1] + "\" enabled!");
                         } else if("waterbottle".equalsIgnoreCase(args[1])) {
                         this.command.Enable(args[1]);
                         utils.sendMessage(player, "Part \"" + args[1] + "\" enabled!");
                         } else {
                         utils.sendMessage(player, "Can't enable non-existing part.");
                         utils.sendMessage(player, "Available parts: freezing, exhausting, jungle, thirst, waterbottle");
                         }
                         }*/
                    } else if ("lang".equalsIgnoreCase(args[0])) {
                        if (args.length == 1) {
                            utils.sendMessage(player, "Language: " + localization.lang + ".");
                            utils.sendMessage(player, "List of available languages can be found on bukkitdev plugin page.");
                            /*HashMap<String, String> langs = localization.GetLangList();
                            for (String lang : langs.keySet()) {
                                utils.sendMessage(player, lang + " - " + langs.get(lang));
                            }*/
                        } else {
                            if (args.length == 2) {
                                boolean result = this.command.Language(args[1]);
                                if (result == true) {
                                    utils.sendMessage(player, "Language changed to: " + localization.getValue("Description") + ".");
                                } else {
                                    utils.sendMessage(player, "Language load error!");
                                }
                            }
                        }
                    } else {
                        utils.sendMessage(player, "Invalid command!");
                    }
                }
            }
        } catch (Exception e) {
            RealWeather.log("Command error");
            sendStackReport(e);
        }
        return true;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public ThreadManager getThreadManager() {
        return threadManager;
    }

    public FeatureManager getFeatureManager() {
        return featureManager;
    }

    public static boolean isDebug() {
        return config.getVariables().isDebugMode();
    }

    static Armors getArmors() {
        return config.getVariables().getArmors();
    }

    public static boolean isGlobalyEnable() {
        return config.getVariables().isGlobalyEnable();
    }

    public boolean isWeatherModuleLoaded() {
        return isWeatherModuleLoaded;
    }

    public void setWeatherLoaded(boolean state) {
        this.isWeatherModuleLoaded = state;
    }

    public WeatherCompat getWeather() {
        if (weatherCompat != null) {
            return weatherCompat;
        } else {
            return (weatherCompat = new WeatherCompat());
        }
    }

    public static float getPlayerTemperature(Player player) {
        float f = 15;
        try {
            if (playerTemperature.containsKey(player)) {
                f = playerTemperature.get(player).floatValue();
            }
        } catch (Exception e) {
            RealWeather.log.log(Level.SEVERE, null, e);
            RealWeather.sendStackReport(e);
        }
        return f;
    }

    public HashMap<Player, Double> getPlayerTemperature() {
        return playerTemperature;
    }

    /*public void setWeatherModule(FWeather module) {
     weatherModule = module;
     }*/
    class WeatherCompat {

        public static final int BLIZZARD = 0;
        public static final int STORM = 1;
        public static final int FREEZE = 2;
        public static final int RAINSNOW = 3;
        public static final int COLD = 4;
        public static final int SHOWERS = 5;
        public static final int CLEAR = 6;
        public static final int WARM = 7;
        public static final int SUMMERSTORM = 8;
        public static final int HOT = 9;
        public static final int TROPIC = 10;

        WeatherCompat() {
        }

        public int getCurrentWeather(int day) {
            return ((Integer) FeatureManager.getSharedData().getValue("FW-WEATHER" + day)).intValue();
        }

        public int getWeatherTemp() {
            return ((Integer) FeatureManager.getSharedData().getValue("FW-WEATHER-TEMP")).intValue();
        }

        public String getForecastMessage(int weather) {
            FeatureLocalization loc = featureManager.getModule("weather").getConfig().getLocale();
            String Message;
            switch (weather) {
                case BLIZZARD:
                    Message = loc.getValue("BLIZZARD");
                    break;
                case STORM:
                    Message = loc.getValue("STORM");
                    break;
                case FREEZE:
                    Message = loc.getValue("FREEZE");
                    break;
                case RAINSNOW:
                    Message = loc.getValue("RAINSNOW");
                    break;
                case COLD:
                    Message = loc.getValue("COLD");
                    break;
                case SHOWERS:
                    Message = loc.getValue("SHOWERS");
                    break;
                case CLEAR:
                    Message = loc.getValue("CLEAR");
                    break;
                case WARM:
                    Message = loc.getValue("WARM");
                    break;
                case SUMMERSTORM:
                    Message = loc.getValue("SUMMERSTORM");
                    break;
                case HOT:
                    Message = loc.getValue("HOT");
                    break;
                case TROPIC:
                    Message = loc.getValue("TROPIC");
                    break;
                default:
                    Message = "RealWeather: Forecast error! Non-existing weather provided.";
            }
            return Message;
        }
    }

    /*public void sendStackReport(Exception ex) {
     RealWeather.log("Sending error...");
     sendStackReport(getDescription().getVersion(), ex);
     }*/
    public static void sendStackReport(Exception e) {
        if (RealWeather.config.getVariables().isReportingEnabled() & ((lastReport + 300000) < System.currentTimeMillis())) {
            RealWeather.log("Sending error...");
            String stack = Utils.joinStackTrace(e);
            String _plugins = "";
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                _plugins = _plugins.concat("___" + plugin.getName());
            }
            ReportSender newReport = new ReportSender(version, Bukkit.getBukkitVersion(), stack, _plugins);
            Thread newThread = new Thread(newReport);
            newThread.setDaemon(true);
            newThread.start();
            lastReport = System.currentTimeMillis();
        } else {
            RealWeather.log("Error sending is disabled or 5 min timeout not passed. Aborted.");
        }
    }
}