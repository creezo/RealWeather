/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realweather;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author creezo
 */
public class PlayerCheck {
    private static RealWeather plugin;
    private static Configuration Config = RealWeather.Config;
    
    public PlayerCheck(RealWeather plugin) {
        this.plugin = plugin;
    }
    
    static boolean checkRandomGrass(Player player, int range, int tries) {
        Random random = new Random();
        for(int i = 0; i < tries;i++) {
            Block thisBlock = player.getLocation().getBlock().getRelative(random.nextInt((range*2)+1)-range, random.nextInt((range*2)+1)-range, random.nextInt((range*2)+1)-range);
            if(thisBlock.getTypeId() == 31) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean CheckToTop(Block PlayerBlock, int MaxMapHeigh, Player player) {
        boolean IsUnderRoof = false;
        int heigh = PlayerBlock.getY();
        while(heigh < MaxMapHeigh) {
            PlayerBlock = PlayerBlock.getRelative(BlockFace.UP);
            if(PlayerBlock.getTypeId() != 0) {
                if(Config.getVariables().isDebugGlassBlocks()) player.sendBlockChange(PlayerBlock.getLocation(), 20, (byte)0);
                IsUnderRoof = true;
                break;
            }
            heigh = PlayerBlock.getY();
            IsUnderRoof = false;
        }
        return IsUnderRoof;
    }
        
    public static boolean checkPlayerInside(Player player, int CheckRadius, String Recognizer) {
        boolean Inside = false;
        boolean CheckOnce = true;
        if("simple".equals(Recognizer)) {
            if(Config.getVariables().isDebugMode()) RealWeather.log("simple selected");
            Inside = CheckToTop(player.getLocation().getBlock().getRelative(BlockFace.UP), player.getLocation().getWorld().getMaxHeight() - 1, player);
        } else if("default".equals(Recognizer)) {
            if(Config.getVariables().isDebugMode()) RealWeather.log("default selected");
            Location playerPosition = player.getLocation();
            int heigh = playerPosition.getBlockY();
            int MaxHeigh = player.getLocation().getWorld().getMaxHeight() - 1;

            if(Config.getVariables().isDebugMode()) RealWeather.log("Heigh: " + ConvertIntToString(heigh));

            Block NowCheckingBlock = playerPosition.getBlock();
            Block StartBlock = playerPosition.getBlock();
            for(int radius = 1; radius <= CheckRadius; radius++) {
                if(CheckOnce == true) {
                    CheckOnce = false;
                    Inside = CheckToTop(NowCheckingBlock, MaxHeigh, player);
                    if(Inside == false) break;
                }

                StartBlock = StartBlock.getRelative(BlockFace.NORTH_WEST);
                NowCheckingBlock = StartBlock;
                int BlockNumInSide = (radius*2);
                for(int side = 1; side <= 4; side ++)
                {
                    switch(side) {
                        case 1:
                            for(int blocks = 1; blocks <= BlockNumInSide; blocks++) {
                                Inside = CheckToTop(NowCheckingBlock, MaxHeigh, player);
                                if(Inside == false) break;
                                NowCheckingBlock = NowCheckingBlock.getRelative(BlockFace.EAST);
                            }
                            break;
                        case 2:
                            for(int blocks = 1; blocks <= BlockNumInSide; blocks++) {
                                Inside = CheckToTop(NowCheckingBlock, MaxHeigh, player);
                                if(Inside == false) break;
                                NowCheckingBlock = NowCheckingBlock.getRelative(BlockFace.SOUTH);
                            }
                            break;
                        case 3:
                            for(int blocks = 1; blocks <= BlockNumInSide; blocks++) {
                                Inside = CheckToTop(NowCheckingBlock, MaxHeigh, player);
                                if(Inside == false) break;
                                NowCheckingBlock = NowCheckingBlock.getRelative(BlockFace.WEST);
                            }
                            break;
                        case 4:
                            for(int blocks = 1; blocks <= BlockNumInSide; blocks++) {
                                Inside = CheckToTop(NowCheckingBlock, MaxHeigh, player);
                                if(Inside == false) break;
                                NowCheckingBlock = NowCheckingBlock.getRelative(BlockFace.NORTH);
                            }
                            break;
                    }
                    if(Inside == false) break;
                }
                if(Inside == false) break;
            }
        } else if("cross".equals(Recognizer)) {
            if(Config.getVariables().isDebugMode()) RealWeather.log("cross selected");
            Block RangeCheckBlock;
            Location playerPosition = player.getLocation();
            int heigh = playerPosition.getBlockY();
            Block playerPositionBlock = playerPosition.getBlock().getRelative(BlockFace.UP);
            int MaxHeigh = player.getLocation().getWorld().getMaxHeight() - 1;

            if(Config.getVariables().isDebugMode()) RealWeather.log("Heigh: " + ConvertIntToString(heigh));
            
            for(int once = 1; once == 1; once++) {
                Inside = CheckToTop(playerPositionBlock, MaxHeigh, player);
                if(Inside == false) break;

                int RangeToNorthSide = 0;
                RangeCheckBlock = playerPositionBlock;
                for(int range = 1; range <= CheckRadius; range++) {
                    if(RangeCheckBlock.getRelative(BlockFace.NORTH, range).getTypeId() == 0) {
                        RangeToNorthSide++;
                    } else {
                        break;
                    }
                }

                int RangeToEastSide = 0;
                RangeCheckBlock = playerPositionBlock;
                for(int range = 1; range <= CheckRadius; range++) {
                    if(RangeCheckBlock.getRelative(BlockFace.EAST, range).getTypeId() == 0) {
                        RangeToEastSide++;
                    } else {
                        break;
                    }
                }

                int RangeToSouthSide = 0;
                RangeCheckBlock = playerPositionBlock;
                for(int range = 1; range <= CheckRadius; range++) {
                    if(RangeCheckBlock.getRelative(BlockFace.SOUTH, range).getTypeId() == 0) {
                        RangeToSouthSide++;
                    } else {
                        break;
                    }
                }

                int RangeToWestSide = 0;
                RangeCheckBlock = playerPositionBlock;
                for(int range = 1; range <= CheckRadius; range++) {
                    if(RangeCheckBlock.getRelative(BlockFace.WEST, range).getTypeId() == 0) {
                        RangeToWestSide++;
                    } else {
                        break;
                    }
                }

                Block StartBlock = playerPositionBlock.getRelative(BlockFace.NORTH, RangeToNorthSide);
                StartBlock = StartBlock.getRelative(BlockFace.EAST, RangeToEastSide);
                for(int EastWestSize = 0 ; EastWestSize <= RangeToWestSide + RangeToEastSide ; EastWestSize++) {
                    for(int NorthSouthSize = 0 ; NorthSouthSize <= RangeToNorthSide + RangeToSouthSide ; NorthSouthSize++) {
                        Inside = CheckToTop(StartBlock.getRelative(NorthSouthSize, 0, EastWestSize), MaxHeigh, player);
                        if(Inside == false) break;
                    }
                    if(Inside == false) break;
                }  
            }
        }
        return Inside;
    }
    
    public static Biome checkPlayerBiome(Player player) {
        Biome BiomeType = player.getLocation().getBlock().getBiome();
        return BiomeType;
    }
    
    public double[] getPlrResist(Player player, String resistType) {
        double[] resist = {1,0};
        ItemStack WearBoots = player.getInventory().getBoots();
        ItemStack WearChestplate = player.getInventory().getChestplate();
        ItemStack WearHelmet = player.getInventory().getHelmet();
        ItemStack WearLeggings = player.getInventory().getLeggings();
        int BootsID = 0, ChestplateID = 0, HelmetID = 0, LeggingsID = 0;
        try {
            BootsID = WearBoots.getTypeId();
            if(Config.getVariables().isDebugMode()) plugin.log("BootsID: "+BootsID);
        } catch(Exception ex) { if(Config.getVariables().isDebugMode()) plugin.log("No Boots."); }
        try {
            ChestplateID = WearChestplate.getTypeId();
            if(Config.getVariables().isDebugMode()) plugin.log("ChestplateID: "+ChestplateID);
        } catch(Exception ex) { if(Config.getVariables().isDebugMode()) plugin.log("No Chestplate."); }
        try {
            HelmetID = WearHelmet.getTypeId();
            if(Config.getVariables().isDebugMode()) plugin.log("HelmetID: "+HelmetID);
        } catch(Exception ex) { if(Config.getVariables().isDebugMode()) plugin.log("No Helmet."); }
        try {
            LeggingsID = WearLeggings.getTypeId();
            if(Config.getVariables().isDebugMode()) plugin.log("LeggingsID: "+LeggingsID);
        } catch(Exception ex) { if(Config.getVariables().isDebugMode()) plugin.log("No Leggings."); }
        if(Config.getVariables().isDebugMode()) plugin.log("BootsID: "+BootsID);
        if(Config.getVariables().isDebugMode()) plugin.log("ChestplateID: "+ChestplateID);
        if(Config.getVariables().isDebugMode()) plugin.log("HelmetID: "+HelmetID);
        if(Config.getVariables().isDebugMode()) plugin.log("LeggingsID: "+LeggingsID);
        if(Config.getVariables().isDebugMode()) plugin.log("Resist2(0): "+resist[0]);
        if(BootsID!=0) {
            double[] vars = Config.getVariables().getArmours().getResistance(BootsID, resistType);
            resist[0] *= vars[0];
            resist[1] += vars[1];
        }
        if(Config.getVariables().isDebugMode()) plugin.log("Resist2(1): "+resist[0]);
        if(ChestplateID!=0) {
            double[] vars = Config.getVariables().getArmours().getResistance(ChestplateID, resistType);
            resist[0] *= vars[0];
            resist[1] += vars[1];
        }
        if(Config.getVariables().isDebugMode()) plugin.log("Resist2(2): "+resist[0]);
        if(HelmetID!=0) {
            double[] vars = Config.getVariables().getArmours().getResistance(HelmetID, resistType);
            resist[0] *= vars[0];
            resist[1] += vars[1];
        }
        if(Config.getVariables().isDebugMode()) plugin.log("Resist2(3): "+resist[0]);
        if(LeggingsID!=0) {
            double[] vars = Config.getVariables().getArmours().getResistance(LeggingsID, resistType);
            resist[0] *= vars[0];
            resist[1] += vars[1];
        }
        if(Config.getVariables().isDebugMode()) plugin.log("Resist2(4): "+resist[0]);
        return resist;
    }
    public static double checkHeatAround(Player player, int HeatCheckRadius) {
        if(Config.getVariables().isDebugMode()) RealWeather.log("Checking heat...");
        double Temperature = 0;
        double BlockPower = 0;
        double rangeDouble = 0;
        double varOne = 0;
        boolean cooler;
        Block playerBlock = player.getLocation().getBlock();
        Block startBlock = playerBlock.getRelative(HeatCheckRadius*(-1)-1, (HeatCheckRadius*(-1)), HeatCheckRadius*(-1)-1);
        for(int x = 1 ; x <= (HeatCheckRadius*2)+1 ; x++) {
            for(int z = 1 ; z <= (HeatCheckRadius*2)+1 ; z++) {
                for(int y = 1 ; y <= (HeatCheckRadius*2) ; y++) {
                    if(plugin.HeatSources.containsKey(startBlock.getRelative(x, y, z).getType())) {
                        BlockPower = plugin.HeatSources.get(startBlock.getRelative(x, y, z).getType());
                        if(BlockPower >= 0) {
                            cooler = false;
                        } else {
                            cooler = true;
                        }
                    } else {
                        BlockPower = 0;
                        cooler = false;
                    }
                    if(BlockPower != 0) {
                        rangeDouble = startBlock.getRelative(x, y, z).getLocation().distance(playerBlock.getLocation());
                        varOne = BlockPower*(1-(rangeDouble/(HeatCheckRadius*2)));
                        if(varOne >= 0.0d && cooler == false) {
                            Temperature += varOne;
                        } else if(varOne <= 0.0d && cooler == true) {
                            Temperature += varOne;
                        }
                    }
                }
            }
        }
        if(plugin.HeatInHand.containsKey(player.getItemInHand().getType())) {
            BlockPower = plugin.HeatInHand.get(player.getItemInHand().getType());
        } else {
            BlockPower = 0;
        }
        if(Config.getVariables().isDebugMode()) RealWeather.log("From item in hand: " + ConvertIntToString((int)BlockPower));
        Temperature += BlockPower;
        if(Config.getVariables().isDebugMode()) RealWeather.log("Total heat from blocks and items: " + Temperature);
        return Temperature;
    }
    
    public boolean isInIce(Player player) {
        if(player.getLocation().getBlock().getType().equals(Material.ICE)) {
            return true;
        }
        return false;
    }
    
    private static String ConvertIntToString(int number) {
        return "" + number;
    }
}