package org.creezo.realweather;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.logging.Level;
import net.minecraft.server.v1_6_R2.BiomeBase;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 *
 * @author creezo
 */
class TempThread implements Runnable {
    private final RealWeather plugin;
    private final Player player;
    private int RepeatingMessage = 1;
    //private int MessageDelay = plugin.Config.getVariables().getMessageDelay();
    //private int RepeatingFoodDecreaseDelay = plugin.Config.getVariables().getBiomes().getExhausting().getChecksPerFoodDecrease();
    private int RepeatingFoodDecrease = 1;
    private DecimalFormat df = new DecimalFormat("##.#");
    private DecimalFormat df2 = new DecimalFormat("##.###");
    private float walkSpeed = 1.0f;
    
    private int ErrNum = 1;

    public TempThread(RealWeather plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        ErrNum = 2;
    }
    
    @Override
    public void run() {
        try {
            if(plugin.Config.getVariables().isGlobalyEnable() && !player.getGameMode().equals(GameMode.CREATIVE)) {
                double[] frostResist = {1,1};
                double Temperature = plugin.checkCenter.getTemperature(player.getLocation(), player);
                if(plugin.PlayerHeatShow.get(player.getEntityId()).equals(Boolean.TRUE)) {
                    plugin.Utils.SendMessage(player, plugin.Localization.CurrentTemperature+df.format(Temperature));
                }
                try{
                    if(plugin.PlayerClientMod.get(player.getEntityId())) {
                        byte[] bytes = ("TM:"+df.format(Temperature)).getBytes();
                        player.sendPluginMessage(plugin, "realweather", bytes);
                    }
                } catch(Exception e) {
                    if(plugin.Config.getVariables().isDebugMode()) plugin.log.log(Level.SEVERE, "Error in Cliend-mod handling" ,e);
                }
                double TempFinal = Temperature;
                plugin.PlayerTemperature.put(player, Temperature);
                if(Temperature < -60) Temperature = -60;
                if(Temperature > 80) Temperature = 80;
                
                if(player.hasPermission("realweather.immune.all")) return;
                
                walkSpeed = (float)Math.pow(walkSpeed, 0.75f);
                if(!plugin.PlayerRefreshing.isEmpty() && plugin.PlayerRefreshing.containsKey(player)) {
                    walkSpeed = (float)Math.pow(walkSpeed, 0.66f);
                    if(plugin.Config.getVariables().isDebugMode()) plugin.log("Exhaustion decreasing because of refreshing.");
                }
                player.setWalkSpeed((float)Math.pow(0.2f, 1 + (walkSpeed - 1)/ 10));
                if(plugin.Config.getVariables().isDebugMode()) plugin.log("Player walk speed: " + df2.format(player.getWalkSpeed()));
                
                if(Temperature < plugin.Config.getVariables().getBiomes().getGlobal().getFreezeUnder()) {
                    ErrNum = 18;
                    if(plugin.Config.getVariables().getBiomes().getFreezing().isEnabled() && plugin.Config.getVariables().getAllowedWorlds().contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realweather.immune.winter")) {
                        int TempRange;
                        ErrNum = 19;
                        if(plugin.Config.getVariables().getBiomes().getGlobal().getFreezeUnder() >= 0) {
                            TempRange = 60 + plugin.Config.getVariables().getBiomes().getGlobal().getFreezeUnder();
                        } else {
                            TempRange = 60 + plugin.Config.getVariables().getBiomes().getGlobal().getFreezeUnder();
                        }
                        ErrNum = 20;
                        double percent = ((((Temperature + 60)/TempRange)-1)*-1);
                        if(plugin.Config.getVariables().isDebugMode()) plugin.log("Freezing: "+df2.format(percent));
                        ErrNum = 21;
                        double damage = plugin.Config.getVariables().getBiomes().getFreezing().getDamage();
                        if(plugin.Config.getVariables().isDebugMode()) plugin.log("Damage: "+damage);
                        double finalDamage = damage * percent;
                        if(plugin.Config.getVariables().isDebugMode()) plugin.log("Final damage: "+df2.format(finalDamage));
                        ErrNum = 22;
                        if(plugin.checkCenter.checkPlayerInside(player.getLocation(), plugin.Config.getVariables().getBiomes().getFreezing().getCheckRadius(), plugin.Config.getVariables().getBiomes().getFreezing().getHouseRecoFreezing())) finalDamage = 0;
                        if(finalDamage!=0) {
                            ErrNum = 23;
                            frostResist = plugin.checkCenter.getPlrResist(player, "Frost");
                            if(plugin.Config.getVariables().isDebugMode()) plugin.log("Resist: "+df2.format(frostResist[0]));
                            finalDamage /= frostResist[0];
                            ErrNum = 24;
                            if(frostResist[1]==4 && finalDamage >= 0.5d) finalDamage -= 0.5d;
                        } else {
                            if(plugin.Config.getVariables().isDebugMode()) plugin.log("Player is inside.");
                        }
                        ErrNum = 25;
                        if(plugin.Config.getVariables().isDebugMode()) plugin.log("Final damage + resist: "+df2.format(finalDamage));
                        if(finalDamage >= 0.5d) {
                            if(RepeatingMessage == 1) {
                                player.sendMessage(ChatColor.GOLD + plugin.Localization.FreezingWarnMessage);
                                RepeatingMessage = plugin.Config.getVariables().getMessageDelay();
                            } else { RepeatingMessage--; }
                        }
                        ErrNum = 26;
                        int IntDamage = (int)Math.round(finalDamage);
                        if(plugin.Config.getVariables().isDebugMode()) plugin.log("Rounded damage: "+IntDamage);
                        ErrNum = 27;
                        if(plugin.PlayerDamagerMap.containsKey(player)) {
                            plugin.PlayerDamage.put(player, IntDamage);
                            synchronized (plugin.PlayerDamagerMap.get(player)) {
                                plugin.PlayerDamagerMap.get(player).notify();
                            }
                            ErrNum = 28;
                        } else {
                            ErrNum = 29;
                            PlayerDamageThread pdmgTH = new PlayerDamageThread(player, plugin);
                            //RealWeather.PlayerDamagerMap.put(player, new Thread(pdmgTH));
                            //RealWeather.PlayerDamagerMap.get(player).setDaemon(true);
                            plugin.PlayerDamage.put(player, IntDamage);
                            //RealWeather.PlayerDamagerMap.get(player).start();
                            Thread dmgTH = new Thread(pdmgTH);
                            dmgTH.setDaemon(true);
                            dmgTH.start();
                            plugin.PlayerDamagerMap.put(player, dmgTH);
                            ErrNum = 30;
                        }
                        ErrNum = 31;
                    }
                    ErrNum = 32;
                } else if(Temperature > plugin.Config.getVariables().getBiomes().getGlobal().getOverheatOver()) {
                    ErrNum = 33;
                    if(plugin.Config.getVariables().getBiomes().getExhausting().isEnabled() && plugin.Config.getVariables().getAllowedWorlds().contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realweather.immune.desert")) {
                        ErrNum = 34;
                        int TempRange;
                        if(plugin.Config.getVariables().getBiomes().getGlobal().getOverheatOver() >= 0) {
                            TempRange = 80 - plugin.Config.getVariables().getBiomes().getGlobal().getOverheatOver();
                        } else {
                            TempRange = 80 + plugin.Config.getVariables().getBiomes().getGlobal().getOverheatOver();
                        }
                        ErrNum = 35;
                        double percent = (((Temperature - 80)/(double)TempRange)+1);
                        double damage = plugin.Config.getVariables().getBiomes().getExhausting().getStaminaLost();
                        double finalDamage = damage * percent;
                        ErrNum = 36;
                        if(plugin.checkCenter.checkPlayerInside(player.getLocation(), 1, plugin.Config.getVariables().getBiomes().getExhausting().getHouseRecognizer())) {
                            finalDamage *= 0.5D;
                        }
                        ErrNum = 37;
                        if(finalDamage!=0) {
                            double[] resist = plugin.checkCenter.getPlrResist(player, "Heat");
                            finalDamage /= resist[0];
                        }
                        ErrNum = 38;
                        if(finalDamage > 0.5d) {
                            if(RepeatingMessage == 1) {
                                player.sendMessage(ChatColor.GOLD + plugin.Localization.ExhaustingWarnMessage);
                                RepeatingMessage = plugin.Config.getVariables().getMessageDelay();
                            } else { RepeatingMessage--; }
                        }
                        walkSpeed += percent;
                        //player.setWalkSpeed((float)Math.pow(0.2f, 1 + (walkSpeed - 1)/ 10));
                        if(plugin.Config.getVariables().isDebugMode()) System.out.println("Recalculated walk speed for next check: " + df2.format(player.getWalkSpeed()));
                        //plugin.log("Factor: " + (float)((percent * 0.9F) - 1) * -1);
                        ErrNum = 39;
                        if(player.getSaturation() > (float)finalDamage) {
                            player.setSaturation(player.getSaturation() - (float)finalDamage);
                            if(plugin.Config.getVariables().isDebugMode()) plugin.log("Stamina: " + Utils.ConvertFloatToString(player.getSaturation()));
                            ErrNum = 40;
                        } else {
                            ErrNum = 41;
                            player.setSaturation(0.0F);
                            if(player.getFoodLevel() > 1) {
                                if(RepeatingFoodDecrease == 1) {
                                    player.setFoodLevel(player.getFoodLevel() - 1);
                                    RepeatingFoodDecrease = plugin.Config.getVariables().getBiomes().getExhausting().getChecksPerFoodDecrease();
                                } else { RepeatingFoodDecrease--; }
                                if(plugin.Config.getVariables().isDebugMode()) plugin.log("Food level(1-20): " + Utils.ConvertIntToString(player.getFoodLevel()));
                            }
                            ErrNum = 42;
                        }
                        ErrNum = 43;
                    }
                    ErrNum = 44;
                }
                /*if(plugin.Config.getVariables().getBiomes().getFreezing().getPlayerIceBlock()) {
                    ErrNum = 45;
                    double TempIB = (frostResist[0])*(-60);
                    if(TempFinal <= TempIB) {
                        ErrNum = 46;
                        if(!plugin.PlayerIceHashMap.get(player.getEntityId())) {
                            DamageEvent DamageEvent = new DamageEvent(player, 1, (int) player.getHealth());
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
                }*/
                ErrNum = 51;
                if(plugin.Config.getVariables().getBiomes().getGlobal().isThirstEnabled() && plugin.Config.getVariables().getBiomes().getGlobal().getThirstAllowedWorlds().contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realweather.immune.thirst")) {
                    ErrNum = 52;
                    if(player.getSaturation() > plugin.Config.getVariables().getBiomes().getGlobal().getThirstStaminaLost()) {
                        player.setSaturation(player.getSaturation() - plugin.Config.getVariables().getBiomes().getGlobal().getThirstStaminaLost());
                        if(plugin.Config.getVariables().isDebugMode()) plugin.log("Stamina: " + Utils.ConvertFloatToString(player.getSaturation()));
                    } else { player.setSaturation(0.0F); }
                    ErrNum = 53;
                }
                ErrNum = 54;
                if(plugin.Config.getVariables().getBiomes().getJungle().isEnabled() && plugin.Config.getVariables().getAllowedWorlds().contains(player.getLocation().getWorld().getName()) && !player.hasPermission("realweather.immune.jungle")) {
                    ErrNum = 55;
                    BiomeBase PlayerBiome = CheckCenter.checkPlayerBiome(player);
                    if(PlayerBiome.equals(BiomeBase.JUNGLE) || PlayerBiome.equals(BiomeBase.JUNGLE_HILLS)) {
                        ErrNum = 56;
                        if(player.getLocation().getY() >= 60) {
                            if(plugin.Config.getVariables().isDebugMode()) plugin.log("Looking for tall grass...");
                            boolean IsGrass = CheckCenter.checkRandomGrass(player, plugin.Config.getVariables().getBiomes().getJungle().getInsectJumpRange(), plugin.Config.getVariables().getBiomes().getJungle().getChanceMultiplier());
                            ErrNum = 57;
                            if(IsGrass) {
                                if(plugin.Config.getVariables().isDebugMode()) plugin.log("Found.");
                                plugin.Utils.PlayerPoisoner(player, 100, IsGrass);
                            }
                        }
                        ErrNum = 58;
                    }
                }
                ErrNum = 59;
            }
        } catch(Exception e) {
            plugin.log.log(Level.SEVERE, "Error in temperature thread: "+ErrNum);
            plugin.log.log(Level.SEVERE, null, e);
        }
        ErrNum = 0;
    }
    
    public static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }
}
