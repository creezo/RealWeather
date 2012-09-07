package org.creezo.realweather;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Dodec
 */
public class Utils {
    private List<Material> Mats = RealWeather.Mats;
    private static Configuration Config = RealWeather.Config;
    private final RealWeather plugin;
    Utils(RealWeather plugin) {
        this.plugin = plugin;
    }
    public void addMats() {
        Mats.add(Material.AIR);
        Mats.add(Material.CROPS);
        Mats.add(Material.SAPLING);
        Mats.add(Material.SNOW);
        Mats.add(Material.STATIONARY_WATER);
        Mats.add(Material.SUGAR_CANE_BLOCK);
        Mats.add(Material.TORCH);
        Mats.add(Material.VINE);
        Mats.add(Material.WATER);
        Mats.add(Material.WATER_LILY);
        Mats.add(Material.WEB);
        Mats.add(Material.RED_ROSE);
        Mats.add(Material.YELLOW_FLOWER);
    }
    public boolean SendMessage(Player player, String message) {
        try {
            player.sendMessage(ChatColor.GOLD + "RealWeather: " + message);
            return true;
        } catch (Exception e) {
            RealWeather.log("" + message);
            return false;
        }
    }
    
    public boolean SendHelp(Player player) {
        try {
            player.sendMessage(ChatColor.GOLD + "Commands: /rw stamina, /rw temp, /rw forecast, /rw version");
            return true;
        } catch (Exception e) {
            RealWeather.log("Help message --- see help in game console");
            return false;
        }
    }

    public boolean SendAdminHelp(Player player) {
        try {
            player.sendMessage(ChatColor.GOLD + "Commands: /rwadmin enable [plugin-part], /rwadmin disable [plugin-part], /rwadmin save, /rwadmin load /rwadmin version, /rwadmin lang [language], /rwadmin debug");
            return true;
        } catch (Exception e) {
            RealWeather.log("Commands: /rwadmin enable [plugin-part], /rwadmin disable [plugin-part], /rwadmin save, /rwadmin load /rwadmin version, /rwadmin lang [language], /rwadmin debug");
            return false;
        }
    }
    
    public static void PlayerPoisoner(Player player, int probablity, boolean IsGrass) {
        Random random = new Random();
        if(random.nextInt(100) < probablity) {
            int Type = random.nextInt(100)+1;
            int Duration = Config.getVariables().getBiomes().getJungle().getInsectBiteDuration() - ((int)player.getFoodLevel() + (int)player.getSaturation());
            int PoisonDuration = Config.getVariables().getBiomes().getJungle().getInsectPoisonDuration();
            PotionEffectType PEffect = PotionEffectType.SLOW;
            if(Type >= 1 && Type <= 6) {
                PEffect = PotionEffectType.BLINDNESS;
                player.addPotionEffect(PEffect.createEffect(80 * Duration, 1), IsGrass);
            } else if(Type >= 7 && Type <= 12) {
                PEffect = PotionEffectType.CONFUSION;
                player.addPotionEffect(PEffect.createEffect(80 * Duration, 1), IsGrass);
            } else if(Type >= 13 && Type <= 45) {
                PEffect = PotionEffectType.WEAKNESS;
                player.addPotionEffect(PEffect.createEffect(40 * Duration, 1), IsGrass);
            } else if(Type >= 46 && Type <= 100) {
                PEffect = PotionEffectType.SLOW;
                player.addPotionEffect(PEffect.createEffect(40 * Duration, 1), IsGrass);
            }
            if(probablity == 100) {
                player.sendMessage(ChatColor.YELLOW + "You have been"+ChatColor.GREEN+" poisoned"+ChatColor.YELLOW+" by insect which causes "+ChatColor.GREEN+PEffect.getName());
            } else {
                player.sendMessage(ChatColor.YELLOW + "You have been"+ChatColor.GREEN+" poisoned"+ChatColor.YELLOW+" by silverfish which causes "+ChatColor.GREEN+PEffect.getName());
            }
            if(random.nextInt(100) <= 4) {
                PEffect = PotionEffectType.POISON;
                player.addPotionEffect(PEffect.createEffect(40 * PoisonDuration, 1), IsGrass);
            }
        }
    }
    
    public static String ConvertIntToString(int number) {
        return "" + number;
    }
    
    public static String ConvertFloatToString(float number) {
        return "" + number;
    }
    public String DoForecast(int Temp) {
        String Message;
        switch (Temp) {
            case -7:
            case -6:
                Message = RealWeather.Localization.StormTemp;
                plugin.getServer().getWorlds();
                break;
            case -5:
            case -4:
                Message = RealWeather.Localization.VLowTemp;
                break;
            case -3:
            case -2:
                Message = RealWeather.Localization.LowTemp;
                break;
            case -1:
            case 0:
            case 1:
                Message = RealWeather.Localization.MedTemp;
                break;
            case 2:
            case 3:
                Message = RealWeather.Localization.HighTemp;
                break;
            case 4:
            case 5:
                Message = RealWeather.Localization.VHighTemp;
                break;
            default:
                Message = "RealWeather: Bad number in forecast!";
        }
        return Message;
    }
}
