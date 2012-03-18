/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author creezo
 */
public class Configuration {
    public boolean WinterEnabled;
    public boolean DesertEnabled;
    public int StartDelay;
    public int CheckDelay;
    public boolean DebugMode;
    public int CheckRadius;
    public String HouseRecoWinter;
    public String HouseRecoDesert;
    public String GameDifficulty = "peaceful";
    public int MaxPlayers;
    public List<ItemStack> AllowedBoots = new ArrayList();
    public List<ItemStack> AllowedChestplate = new ArrayList();
    public List<ItemStack> AllowedHelmet = new ArrayList();
    public List<ItemStack> AllowedLeggings = new ArrayList();
    public int[] MissingArmorDamage = new int[5];
    public float DesertStaminaLostHelmet;
    public float DesertStaminaLostNoHelmet;
    public int ChecksPerFoodDecrease;
    public int MessageDelay;
    public float StaminaReplenish;

    public void InitConfig(RealWinter plugin) {
        RealWinter.log.log(Level.INFO, "[RealWinter] Loading Configuration.");
        try {
            try {
                try {
                    plugin.getConfig().load("plugins/RealWinter/config.yml");
                } catch(InvalidConfigurationException e) { plugin.getServer().broadcastMessage(e.getMessage()); }
            } catch (FileNotFoundException e) { plugin.getServer().broadcastMessage(e.getMessage()); }
        } catch (IOException e) { plugin.getServer().broadcastMessage(e.getMessage()); }
        GameDifficulty = plugin.getServer().getWorlds().get(0).getDifficulty().name().toLowerCase();
        WinterEnabled = plugin.getConfig().getBoolean("winter.enable", false);
        DesertEnabled = plugin.getConfig().getBoolean("desert.enable", false);
        String StartDelayDiff = GameDifficulty + ".StartDelay";
        StartDelay = plugin.getConfig().getInt(StartDelayDiff, 20);
        String CheckDelayDiff = GameDifficulty + ".CheckDelay";
        CheckDelay = plugin.getConfig().getInt(CheckDelayDiff, 10);
        DebugMode = plugin.getConfig().getBoolean("debug-mode");
        CheckRadius = plugin.getConfig().getInt("CheckRadius");
        HouseRecoWinter = plugin.getConfig().getString("winter.HouseRecognizer", "cross");
        HouseRecoDesert = plugin.getConfig().getString("desert.HouseRecognizer", "simple");
        DesertStaminaLostHelmet = plugin.getConfig().getFloatList("desert.StaminaLost.WithHelmet").get(0);
        DesertStaminaLostNoHelmet = plugin.getConfig().getFloatList("desert.StaminaLost.WithoutHelmet").get(0);
        ChecksPerFoodDecrease = plugin.getConfig().getInt("desert.NumberOfCheckPerFoodLost", 5); 
        MessageDelay = plugin.getConfig().getInt("NumberOfChecksPerWarningMessage", 5);
        StaminaReplenish = plugin.getConfig().getFloatList("desert.StaminaReplenishWaterBottle").get(0);
        MaxPlayers = plugin.getServer().getMaxPlayers();
        MissingArmorDamage[0] = plugin.getConfig().getInt("winter.PlayerDamage.NoArmor", 3);
        MissingArmorDamage[1] = plugin.getConfig().getInt("winter.PlayerDamage.OnePiece", 2);
        MissingArmorDamage[2] = plugin.getConfig().getInt("winter.PlayerDamage.TwoPieces", 2);
        MissingArmorDamage[3] = plugin.getConfig().getInt("winter.PlayerDamage.ThreePieces", 1);
        MissingArmorDamage[4] = plugin.getConfig().getInt("winter.PlayerDamage.FullArmor", 0);
        //RealWinter.log.log(Level.INFO, StartDelay + " " + CheckDelay + " " + CheckRadius + " " + HouseRecoWinter + " " + GameDifficulty);
    }
    
    public void InitEquip(RealWinter plugin) {
        RealWinter.log.log(Level.INFO, "[RealWinter] Loading Armors.");
        List<Integer> ListOfBoots = plugin.getConfig().getIntegerList("Armor.Boots");
        List<Integer> ListOfChestplate = plugin.getConfig().getIntegerList("Armor.Chestplate");
        List<Integer> ListOfHelmet = plugin.getConfig().getIntegerList("Armor.Helmet");
        List<Integer> ListOfLeggings = plugin.getConfig().getIntegerList("Armor.Leggings");
        int[] numOfArmors = new int[4];
        for(int index = 0 ; index < ListOfBoots.size() ; index++ ) {
            ItemStack IStack = new ItemStack(ListOfBoots.get(index), 1);
            AllowedBoots.add(IStack);
            numOfArmors[0]++;
        }
        for(int index = 0 ; index < ListOfChestplate.size() ; index++ ) {
            ItemStack IStack = new ItemStack(ListOfChestplate.get(index), 1);
            AllowedChestplate.add(IStack);
            numOfArmors[1]++;
        }
        for(int index = 0 ; index < ListOfHelmet.size() ; index++ ) {
            ItemStack IStack = new ItemStack(ListOfHelmet.get(index), 1);
            AllowedHelmet.add(IStack);
            numOfArmors[2]++;
        }
        for(int index = 0 ; index < ListOfLeggings.size() ; index++ ) {
            ItemStack IStack = new ItemStack(ListOfLeggings.get(index), 1);
            AllowedLeggings.add(IStack);
            numOfArmors[3]++;
        }
        RealWinter.log.log(Level.INFO, "[RealWinter] Loaded " + ConvertIntToString(numOfArmors[0]) + " Boots, " + ConvertIntToString(numOfArmors[1]) + " Chestplates, " + ConvertIntToString(numOfArmors[2]) + " Helmets, " + ConvertIntToString(numOfArmors[3]) + " Leggings.");
    }
    
    private static String ConvertIntToString(int number) {
        return "" + number;
    }
}
