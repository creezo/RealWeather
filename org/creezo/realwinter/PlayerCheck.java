/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

import java.util.List;
import java.util.logging.Level;
import org.bukkit.Location;
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
    private static Configuration Config = RealWinter.Config;
    private static List<ItemStack> AllowedBoots;
    private static List<ItemStack> AllowedChestplate;
    private static List<ItemStack> AllowedHelmet;
    private static List<ItemStack> AllowedLeggings;
    
    public void PCheckInit() {
        AllowedBoots = Config.AllowedBoots;
        AllowedChestplate = Config.AllowedChestplate;
        AllowedHelmet = Config.AllowedHelmet;
        AllowedLeggings = Config.AllowedLeggings;
    }
        
    private static boolean CheckToTop(Block PlayerBlock, int MaxMapHeigh, Player player) {
        boolean IsUnderRoof = false;
        int heigh = PlayerBlock.getY();
        while(heigh < MaxMapHeigh) {
            PlayerBlock = PlayerBlock.getRelative(BlockFace.UP);
            if(PlayerBlock.getTypeId() != 0) {
                //PlayerBlock.setTypeId(20);
                if(Config.DebugGlassBlocks) player.sendBlockChange(PlayerBlock.getLocation(), 20, (byte)0);
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
            if(Config.DebugMode) RealWinter.log.log(Level.INFO, "[RealWinter] simple selected");
            Inside = CheckToTop(player.getLocation().getBlock().getRelative(BlockFace.UP), player.getLocation().getWorld().getMaxHeight() - 1, player);
        } else if("default".equals(Recognizer)) {
            if(Config.DebugMode) RealWinter.log.log(Level.INFO, "[RealWinter] default selected");
            Location playerPosition = player.getLocation();
            int heigh = playerPosition.getBlockY();
            int MaxHeigh = player.getLocation().getWorld().getMaxHeight() - 1;

            if(Config.DebugMode) RealWinter.log.log(Level.INFO, "[RealWinter] Heigh: " + ConvertIntToString(heigh));

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
            if(Config.DebugMode) RealWinter.log.log(Level.INFO, "[RealWinter] cross selected");
            Block RangeCheckBlock;
            Location playerPosition = player.getLocation();
            int heigh = playerPosition.getBlockY();
            Block playerPositionBlock = playerPosition.getBlock().getRelative(BlockFace.UP);
            int MaxHeigh = player.getLocation().getWorld().getMaxHeight() - 1;

            if(Config.DebugMode) RealWinter.log.log(Level.INFO, "[RealWinter] Heigh: " + ConvertIntToString(heigh));
            
            for(int once = 1; once == 1; once++) {
                Inside = CheckToTop(playerPositionBlock, MaxHeigh, player);
                if(Inside == false) break;

                int RangeToNorthSide = 0;
                RangeCheckBlock = playerPositionBlock;
                for(int range = 1; range <= CheckRadius; range++) {
                    if(RangeCheckBlock.getRelative(BlockFace.NORTH, range).getTypeId() == 0) {
                        RangeToNorthSide++;
                    } else {
                        //RealWinter.log.log(Level.INFO, "[RealWinter] North side: " + ConvertIntToString(RangeToNorthSide));
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

    public int checkPlayerClothes(Player player, RealWinter plugin) {
        int clothesNumber = 0;
        ItemStack WearBoots = player.getInventory().getBoots();
        ItemStack WearChestplate = player.getInventory().getChestplate();
        ItemStack WearHelmet = player.getInventory().getHelmet();
        ItemStack WearLeggings = player.getInventory().getLeggings();
        if(Config.DebugMode) {
            try {
                plugin.log.log(Level.INFO, "[RealWinter] Boots ID: " + ConvertIntToString(WearBoots.getTypeId()));
            } catch(Exception ex) { 
                //plugin.log.log(Level.INFO, ex.getMessage()) ;
            }
            try {
                plugin.log.log(Level.INFO, "[RealWinter] Chestplate ID: " + ConvertIntToString(WearChestplate.getTypeId()));
            } catch(Exception ex) { 
                //plugin.log.log(Level.INFO, ex.getMessage()) ;
            }
            try {
                plugin.log.log(Level.INFO, "[RealWinter] Helmet ID: " + ConvertIntToString(WearHelmet.getTypeId()));
            } catch(Exception ex) { 
                //plugin.log.log(Level.INFO, ex.getMessage()) ;
            }
            try {
                plugin.log.log(Level.INFO, "[RealWinter] Leggings ID: " + ConvertIntToString(WearLeggings.getTypeId()));
            } catch(Exception ex) { 
                //plugin.log.log(Level.INFO, ex.getMessage()) ;
            }
        }
        try {
            for(int num = 0; num < AllowedBoots.size(); num++) {
                if(AllowedBoots.get(num).getTypeId() == WearBoots.getTypeId()) clothesNumber++;
            }
        } catch(Exception ex) { 
            if(Config.DebugMode) plugin.log.log(Level.INFO, "[RealWinter] No Boots. " + ex.getMessage()) ;
        }
        try {
            for(int num = 0; num < AllowedChestplate.size(); num++) {
                if(AllowedChestplate.get(num).getTypeId() == WearChestplate.getTypeId()) clothesNumber++;
            }
        } catch(Exception ex) { 
            if(Config.DebugMode) plugin.log.log(Level.INFO, "[RealWinter] No Chestplate. " + ex.getMessage()) ;
        }
        try {
            for(int num = 0; num < AllowedHelmet.size(); num++) {
                if(AllowedHelmet.get(num).getTypeId() == WearHelmet.getTypeId()) clothesNumber++;
            }
        } catch(Exception ex) { 
            if(Config.DebugMode) plugin.log.log(Level.INFO, "[RealWinter] No Helmet. " + ex.getMessage()) ;
        }
        try {
            for(int num = 0; num < AllowedLeggings.size(); num++) {
                if(AllowedLeggings.get(num).getTypeId() == WearLeggings.getTypeId()) clothesNumber++;
            }
        } catch(Exception ex) { 
            if(Config.DebugMode) plugin.log.log(Level.INFO, "[RealWinter] No Leggings. " + ex.getMessage()) ;
        }
        if(Config.DebugMode) {
            plugin.log.log(Level.INFO, "[RealWinter] Armors: " + ConvertIntToString(clothesNumber));
        }
        return clothesNumber;
    }
    
    public boolean GetPlayerHelmet(Player player, RealWinter plugin) {
        boolean HasHelmet = false;
        ItemStack WearHelmet = player.getInventory().getHelmet();
        if(Config.DebugMode) {
            try {
                plugin.log.log(Level.INFO, "[RealWinter] Helmet ID: " + ConvertIntToString(WearHelmet.getTypeId()));
            } catch(Exception ex) {
                //plugin.log.log(Level.INFO, ex.getMessage());
            }
        }
        try {
            for(int num = 0; num < AllowedHelmet.size(); num++) {
                if(AllowedHelmet.get(num).getTypeId() == WearHelmet.getTypeId()) HasHelmet = true;
            }
        } catch(Exception ex) {
            //plugin.log.log(Level.INFO, ex.getMessage());
        }
        return HasHelmet;
    }

    public static int checkHeatAround(Player player) {
        int heat = 49;
        //player.getLocation().getBlock().getLightFromBlocks();
        return heat;
    }
    
    private static String ConvertIntToString(int number) {
        return "" + number;
    }
    
    private static String ConvertDoubleToString(double number) {
        return "" + number;
    }
    
    private static String ConvertbyteToString(byte number) {
        return "" + number;
    }
}
