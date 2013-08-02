package org.creezo.realweather;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
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
            plugin.log("" + message);
            return false;
        }
    }
    
    public boolean SendHelp(Player player) {
        try {
            player.sendMessage(ChatColor.GOLD + "Commands: /rw stamina, /rw temp, /rw forecast, /rw version");
            return true;
        } catch (Exception e) {
            plugin.log("Help message --- see help in game console");
            return false;
        }
    }

    public boolean SendAdminHelp(Player player) {
        try {
            player.sendMessage(ChatColor.GOLD + "Commands: /rwadmin enable [plugin-part], /rwadmin disable [plugin-part], /rwadmin save, /rwadmin load /rwadmin version, /rwadmin lang [language], /rwadmin debug");
            return true;
        } catch (Exception e) {
            plugin.log("Commands: /rwadmin enable [plugin-part], /rwadmin disable [plugin-part], /rwadmin save, /rwadmin load /rwadmin version, /rwadmin lang [language], /rwadmin debug");
            return false;
        }
    }
    
    public void PlayerPoisoner(Player player, int probablity, boolean IsGrass) {
        Random random = new Random();
        if(random.nextInt(100) < probablity) {
            int Type = random.nextInt(100)+1;
            int Duration = plugin.Config.getVariables().getBiomes().getJungle().getInsectBiteDuration() - ((int)player.getFoodLevel() + (int)player.getSaturation());
            int PoisonDuration = plugin.Config.getVariables().getBiomes().getJungle().getInsectPoisonDuration();
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
    
    public void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            plugin.log.log(Level.WARNING, null, e);
        }
    }
}
