package org.creezo.realweather;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 *
 * @author creezo
 */
class TempThread implements Runnable {
    private final RealWeather plugin;
    private final Player player;
    private int RepeatingMessage = 1;
    private int MessageDelay = Config.getVariables().getMessageDelay();
    private int RepeatingFoodDecreaseDelay = Config.getVariables().getBiomes().getDesert().getChecksPerFoodDecrease();
    private int RepeatingFoodDecrease = 1;
    private DecimalFormat df = new DecimalFormat("##.#");
    
    private static Configuration Config = RealWeather.Config;
    private static PlayerCheck playerCheck = RealWeather.playerCheck;
    private Localization Loc = RealWeather.Localization;
    private HashMap<Integer, Boolean> PlayerHeatShow = RealWeather.PlayerHeatShow;
    private Utils utils = RealWeather.Utils;
    private int ErrNum = 1;

    public TempThread(RealWeather plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        ErrNum = 2;
    }
    
    @Override
    public synchronized void run() {
        try {
            if(Config.getVariables().isGlobalEnable() && !player.getGameMode().equals(GameMode.CREATIVE) && !player.hasPermission("realweather.immune")) {
                if(Config.getVariables().isDebugMode()) plugin.log("Starting temp calculation.");
                ErrNum = 3;
                RealWeather.actualWeather = player.getLocation().getWorld().hasStorm();
                Biome PBiome = player.getLocation().getBlock().getBiome();
                int StartTemp = Config.getVariables().getBiomes().getGlobal().getBiomeAverageTemp(player.getLocation().getBlock().getBiome().toString());
                int WeatherModifier = 0;
                double[] frostResist = {1,1};
                double Temperature = 0, TempFinal = 0;
                ErrNum = 4;
                double TimeMultiplier = Math.sin(Math.toRadians(0.015D * player.getWorld().getTime()));
                ErrNum = 5;
                if(RealWeather.actualWeather) {
                    WeatherModifier = Config.getVariables().getBiomes().getGlobal().getBiomesWeatherTempModifier(PBiome.name());
                }
                if(TimeMultiplier > 0) {
                    Temperature = TimeMultiplier * (double) Config.getVariables().getBiomes().getGlobal().getBiomeDayNightTempModifier("Day", PBiome.name());
                } else {
                    Temperature = Math.abs(TimeMultiplier) * (double) Config.getVariables().getBiomes().getGlobal().getBiomeDayNightTempModifier("Night", PBiome.name());
                }
                ErrNum = 6;
                Temperature += (player.getLocation().getY()-Config.getVariables().getBiomes().getGlobal().getSeaLevel())/(player.getWorld().getMaxHeight()-Config.getVariables().getBiomes().getGlobal().getSeaLevel())*Config.getVariables().getBiomes().getGlobal().getTopTemp();
                ErrNum = 7;
                Temperature += RealWeather.ForecastTemp;
                Temperature += StartTemp;
                Temperature += WeatherModifier;
                ErrNum = 8;
                if(player.getLocation().getBlock().getLightFromSky() < (byte)4 && player.getLocation().getY() < Config.getVariables().getBiomes().getGlobal().getSeaLevel()) {
                    ErrNum = 9;
                    double DeepModifier = 1;
                    if((double)player.getLocation().getY() >= (double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.8d) {
                        DeepModifier = (((double)player.getLocation().getY()-((double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.8d))/((double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()-(double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.8d))+((((double)player.getLocation().getY()-(double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.8d)/((double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()-((double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.8d))-1)*(-0.15d));
                    } else if((double)player.getLocation().getY() <= (double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.2d) {
                        if(Temperature < 0) Temperature = (Temperature *-1)/2;
                        DeepModifier = (((double)player.getLocation().getY()-((double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.2d))/(0-(double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.2d))+((((double)player.getLocation().getY()-(double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.2d)/(0-((double)Config.getVariables().getBiomes().getGlobal().getSeaLevel()*0.2d))-1)*(-0.15d));
                    } else {
                        DeepModifier = 0.15d;
                    }
                    ErrNum = 10;
                    if(Config.getVariables().isDebugMode()) plugin.log("DeepModifier (Number between 1 and 0.15):"+DeepModifier);
                    Temperature = ((Temperature-10)*DeepModifier)+10;
                    ErrNum = 11;
                }
                ErrNum = 12;
                Temperature += PlayerCheck.checkHeatAround(player, Config.getVariables().getBiomes().getGlobal().getHeatCheckRadius());
                ErrNum = 13;
                List<Entity> Entities = player.getNearbyEntities(Config.getVariables().getBiomes().getGlobal().getHeatCheckRadius(), Config.getVariables().getBiomes().getGlobal().getHeatCheckRadius(), Config.getVariables().getBiomes().getGlobal().getHeatCheckRadius());
                ErrNum = 14;
                for (Entity entity : Entities) {
                    if(entity.getType().isAlive() && Temperature <= 25) {
                        Temperature += Config.getVariables().getBiomes().getGlobal().getPlayerHeat();
                    }
                }
                ErrNum = 15;
                if(PlayerHeatShow.get(player.getEntityId()).equals(Boolean.TRUE)) {
                    utils.SendMessage(player, Loc.CurrentTemperature+df.format(Temperature));
                }
                ErrNum = 16;
                try{
                    if(RealWeather.PlayerClientMod.get(player.getEntityId())) {
                        byte[] bytes = ("TM:"+df.format(Temperature)).getBytes();
                        player.sendPluginMessage(plugin, "realweather", bytes);
                    }
                } catch(Exception e) {
                    plugin.log("Error in Cliend-mod handling"+e.getMessage());
                }
                ErrNum = 17;
                TempFinal = Temperature;
                plugin.PlayerTemperature.put(player, Temperature);
                if(Temperature < -60) Temperature = -60;
                if(Temperature > 80) Temperature = 80;
                if(Temperature < Config.getVariables().getBiomes().getGlobal().getFreezeUnder()) {
                    ErrNum = 18;
                    if(Config.getVariables().getBiomes().getWinter().isEnabled() && Config.getVariables().getAllowedWorlds().contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realweather.immune.winter")) {
                        int TempRange;
                        ErrNum = 19;
                        if(Config.getVariables().getBiomes().getGlobal().getFreezeUnder() >= 0) {
                            TempRange = 60 + Config.getVariables().getBiomes().getGlobal().getFreezeUnder();
                        } else {
                            TempRange = 60 + Config.getVariables().getBiomes().getGlobal().getFreezeUnder();
                        }
                        ErrNum = 20;
                        double percent = ((((Temperature + 60)/TempRange)-1)*-1);
                        if(Config.getVariables().isDebugMode()) plugin.log("Freezing: "+df.format(percent));
                        ErrNum = 21;
                        double damage = Config.getVariables().getBiomes().getWinter().getDamage();
                        if(Config.getVariables().isDebugMode()) plugin.log("Damage: "+damage);
                        double finalDamage = damage * percent;
                        if(Config.getVariables().isDebugMode()) plugin.log("Final damage: "+df.format(finalDamage));
                        ErrNum = 22;
                        if(PlayerCheck.checkPlayerInside(player, Config.getVariables().getBiomes().getWinter().getCheckRadius(), Config.getVariables().getBiomes().getWinter().getHouseRecoWinter())) finalDamage = 0;
                        if(finalDamage!=0) {
                            ErrNum = 23;
                            frostResist = playerCheck.getPlrResist(player, "Frost");
                            if(Config.getVariables().isDebugMode()) plugin.log("Resist: "+df.format(frostResist[0]));
                            finalDamage /= frostResist[0];
                            ErrNum = 24;
                            if(frostResist[1]==4 && finalDamage >= 0.5d) finalDamage -= 0.5d;
                        } else {
                            if(Config.getVariables().isDebugMode()) plugin.log("Player is inside.");
                        }
                        ErrNum = 25;
                        if(Config.getVariables().isDebugMode()) plugin.log("Final damage + resist: "+df.format(finalDamage));
                        if(finalDamage >= 0.5d) {
                            if(RepeatingMessage == 1) {
                                player.sendMessage(ChatColor.GOLD + Loc.WinterWarnMessage);
                                RepeatingMessage = MessageDelay;
                            } else { RepeatingMessage--; }
                        }
                        ErrNum = 26;
                        int IntDamage = (int)Math.round(finalDamage);
                        if(Config.getVariables().isDebugMode()) plugin.log("Rounded damage: "+IntDamage);
                        ErrNum = 27;
                        if(RealWeather.PlayerDamagerMap.containsKey(player)) {
                            RealWeather.PlayerDamage.put(player, IntDamage);
                            synchronized (RealWeather.PlayerDamagerMap.get(player)) {
                                RealWeather.PlayerDamagerMap.get(player).notify();
                            }
                            ErrNum = 28;
                        } else {
                            ErrNum = 29;
                            PlayerDamageThread pdmgTH = new PlayerDamageThread(player, plugin);
                            //RealWeather.PlayerDamagerMap.put(player, new Thread(pdmgTH));
                            //RealWeather.PlayerDamagerMap.get(player).setDaemon(true);
                            RealWeather.PlayerDamage.put(player, IntDamage);
                            //RealWeather.PlayerDamagerMap.get(player).start();
                            Thread dmgTH = new Thread(pdmgTH);
                            dmgTH.setDaemon(true);
                            dmgTH.start();
                            RealWeather.PlayerDamagerMap.put(player, dmgTH);
                            ErrNum = 30;
                        }
                        ErrNum = 31;
                    }
                    ErrNum = 32;
                } else if(Temperature > Config.getVariables().getBiomes().getGlobal().getOverheatOver()) {
                    ErrNum = 33;
                    if(Config.getVariables().getBiomes().getDesert().isEnabled() && Config.getVariables().getAllowedWorlds().contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realweather.immune.desert")) {
                        ErrNum = 34;
                        int TempRange;
                        if(Config.getVariables().getBiomes().getGlobal().getOverheatOver() >= 0) {
                            TempRange = 80 - Config.getVariables().getBiomes().getGlobal().getOverheatOver();
                        } else {
                            TempRange = 80 + Config.getVariables().getBiomes().getGlobal().getOverheatOver();
                        }
                        ErrNum = 35;
                        double percent = (((Temperature - 80)/(double)TempRange)+1);
                        double damage = Config.getVariables().getBiomes().getDesert().getStaminaLost();
                        double finalDamage = damage * percent;
                        ErrNum = 36;
                        if(PlayerCheck.checkPlayerInside(player, 1, Config.getVariables().getBiomes().getDesert().getHouseRecognizer())) {
                            finalDamage *= 0.5D;
                        }
                        ErrNum = 37;
                        if(finalDamage!=0) {
                            double[] resist = playerCheck.getPlrResist(player, "Heat");
                            finalDamage /= resist[0];
                        }
                        ErrNum = 38;
                        if(finalDamage > 0.5d) {
                            if(RepeatingMessage == 1) {
                                player.sendMessage(ChatColor.GOLD + Loc.DesertWarnMessage);
                                RepeatingMessage = MessageDelay;
                            } else { RepeatingMessage--; }
                        }
                        ErrNum = 39;
                        if(player.getSaturation() > (float)finalDamage) {
                            player.setSaturation(player.getSaturation() - (float)finalDamage);
                            if(Config.getVariables().isDebugMode()) plugin.log("Stamina: " + Utils.ConvertFloatToString(player.getSaturation()));
                            ErrNum = 40;
                        } else {
                            ErrNum = 41;
                            player.setSaturation(0.0F);
                            if(player.getFoodLevel() > 1) {
                                if(RepeatingFoodDecrease == 1) {
                                    player.setFoodLevel(player.getFoodLevel() - 1);
                                    RepeatingFoodDecrease = RepeatingFoodDecreaseDelay;
                                } else { RepeatingFoodDecrease--; }
                                if(Config.getVariables().isDebugMode()) plugin.log("Food level(1-20): " + Utils.ConvertIntToString(player.getFoodLevel()));
                            }
                            ErrNum = 42;
                        }
                        ErrNum = 43;
                    }
                    ErrNum = 44;
                }
                if(Config.getVariables().getBiomes().getWinter().getPlayerIceBlock()) {
                    ErrNum = 45;
                    double TempIB = (frostResist[0])*(-60);
                    if(TempFinal <= TempIB) {
                        ErrNum = 46;
                        if(!plugin.PlayerIceHashMap.get(player.getEntityId())) {
                            DamageEvent DamageEvent = new DamageEvent(player, 1, player.getHealth());
                            plugin.getServer().getPluginManager().callEvent(DamageEvent);
                        }
                        ErrNum = 47;
                    } else if(TempFinal > TempIB){
                        ErrNum = 48;
                        if(plugin.PlayerIceHashMap.get(player.getEntityId())) {
                            plugin.IceBlock.remove(player.getEntityId());
                            if(player.getLocation().getBlock().getType().equals(Material.ICE)) player.getLocation().getBlock().setType(Material.AIR);
                            if(player.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.ICE)) player.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
                            plugin.PlayerIceHashMap.put(player.getEntityId(), false);
                        }
                        ErrNum = 49;
                    }
                    ErrNum = 50;
                }
                ErrNum = 51;
                if(Config.getVariables().getBiomes().getGlobal().isThirstEnabled() && Config.getVariables().getBiomes().getGlobal().getThirstAllowedWorlds().contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realweather.immune.thirst")) {
                    ErrNum = 52;
                    if(player.getSaturation() > Config.getVariables().getBiomes().getGlobal().getThirstStaminaLost()) {
                        player.setSaturation(player.getSaturation() - Config.getVariables().getBiomes().getGlobal().getThirstStaminaLost());
                        if(Config.getVariables().isDebugMode()) plugin.log("Stamina: " + Utils.ConvertFloatToString(player.getSaturation()));
                    } else { player.setSaturation(0.0F); }
                    ErrNum = 53;
                }
                ErrNum = 54;
                if(Config.getVariables().getBiomes().getJungle().isEnabled() && Config.getVariables().getAllowedWorlds().contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realweather.immune.jungle")) {
                    ErrNum = 55;
                    Biome PlayerBiome = PlayerCheck.checkPlayerBiome(player);
                    if(PlayerBiome.equals(Biome.JUNGLE) || PlayerBiome.equals(Biome.JUNGLE_HILLS)) {
                        ErrNum = 56;
                        if(player.getLocation().getY() >= 60) {
                            if(Config.getVariables().isDebugMode()) plugin.log("Looking for tall grass...");
                            boolean IsGrass = PlayerCheck.checkRandomGrass(player, Config.getVariables().getBiomes().getJungle().getInsectJumpRange(), Config.getVariables().getBiomes().getJungle().getChanceMultiplier());
                            ErrNum = 57;
                            if(IsGrass) {
                                if(Config.getVariables().isDebugMode()) plugin.log("Found.");
                                Utils.PlayerPoisoner(player, 100, IsGrass);
                            }
                        }
                        ErrNum = 58;
                    }
                }
                ErrNum = 59;
            }
        } catch(Exception e) {
            plugin.log.log(Level.SEVERE, "Error in temperature thread: "+ErrNum);
            plugin.log.log(Level.SEVERE, e.getMessage());
        }
        ErrNum = 0;
    }
}
