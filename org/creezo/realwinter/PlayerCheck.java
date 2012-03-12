/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

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
    private static Configuration configuration = new Configuration();
        
    private static boolean CheckToTop(Block PlayerBlock, int MaxMapHeigh) {
        boolean IsUnderRoof = false;
        int heigh = PlayerBlock.getY();
        while(heigh < MaxMapHeigh) {
            PlayerBlock = PlayerBlock.getRelative(BlockFace.UP);
            if(PlayerBlock.getTypeId() != 0) {
                //PlayerBlock.setTypeId(20);
                IsUnderRoof = true;
                break;
            }
            heigh = PlayerBlock.getY();
            IsUnderRoof = false;
        }
        return IsUnderRoof;
    }
        
    public static boolean checkPlayerInside(Player player, int CheckRadius) {
        boolean Inside = false;
        boolean CheckOnce = true;
        if(configuration.DebugMode()) player.chat(configuration.HouseRecognizer());
        if("default".equals(configuration.HouseRecognizer())) {
            if(configuration.DebugMode()) player.chat("default selected");
            Location playerPosition = player.getLocation();
            int heigh = playerPosition.getBlockY();
            int MaxHeigh = player.getLocation().getWorld().getMaxHeight() - 1;

            if(configuration.DebugMode()) player.chat("Heigh: " + ConvertIntToString(heigh));

            Block NowCheckingBlock = playerPosition.getBlock();
            Block StartBlock = playerPosition.getBlock();
            for(int radius = 1; radius <= CheckRadius; radius++) {
                if(CheckOnce == true) {
                    CheckOnce = false;
                    Inside = CheckToTop(NowCheckingBlock, MaxHeigh);
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
                                Inside = CheckToTop(NowCheckingBlock, MaxHeigh);
                                if(Inside == false) break;
                                NowCheckingBlock = NowCheckingBlock.getRelative(BlockFace.EAST);
                            }
                            break;
                        case 2:
                            for(int blocks = 1; blocks <= BlockNumInSide; blocks++) {
                                Inside = CheckToTop(NowCheckingBlock, MaxHeigh);
                                if(Inside == false) break;
                                NowCheckingBlock = NowCheckingBlock.getRelative(BlockFace.SOUTH);
                            }
                            break;
                        case 3:
                            for(int blocks = 1; blocks <= BlockNumInSide; blocks++) {
                                Inside = CheckToTop(NowCheckingBlock, MaxHeigh);
                                if(Inside == false) break;
                                NowCheckingBlock = NowCheckingBlock.getRelative(BlockFace.WEST);
                            }
                            break;
                        case 4:
                            for(int blocks = 1; blocks <= BlockNumInSide; blocks++) {
                                Inside = CheckToTop(NowCheckingBlock, MaxHeigh);
                                if(Inside == false) break;
                                NowCheckingBlock = NowCheckingBlock.getRelative(BlockFace.NORTH);
                            }
                            break;
                    }
                    if(Inside == false) break;
                }
                if(Inside == false) break;
            }
        }
        
        if("cross".equals(configuration.HouseRecognizer())) {
            if(configuration.DebugMode()) player.chat("cross selected");
            Block RangeCheckBlock;
            Location playerPosition = player.getLocation();
            int heigh = playerPosition.getBlockY();
            Block playerPositionBlock = playerPosition.getBlock().getRelative(BlockFace.UP);
            int MaxHeigh = player.getLocation().getWorld().getMaxHeight() - 1;

            if(configuration.DebugMode()) player.chat("Heigh: " + ConvertIntToString(heigh));
            
            for(int once = 1; once == 1; once++) {
                if(CheckOnce == true) {
                    CheckOnce = false;
                    Inside = CheckToTop(playerPositionBlock, MaxHeigh);
                    if(Inside == false) break;
                }

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
                        Inside = CheckToTop(StartBlock.getRelative(NorthSouthSize, 0, EastWestSize), MaxHeigh);
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

    public static int checkPlayerClothes(Player player) {
        int clothesNumber = 0;
        ItemStack nullStack = player.getInventory().getBoots();
        player.chat(nullStack.toString());
        player.chat("Boty");
        if(player.getInventory().getBoots() == nullStack) {
            player.chat("Boty chybi");
            clothesNumber++;
        }
        player.chat("Chest");
        if(player.getInventory().getChestplate().getTypeId() != 0) {
            clothesNumber++;
        }
        player.chat("Helma");
        if(player.getInventory().getHelmet().getTypeId() != 0) {
            clothesNumber++;
        }
        player.chat("Kalhoty");
        if(player.getInventory().getLeggings().getTypeId() != 0) {
            clothesNumber++;
        }
        return clothesNumber;
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
