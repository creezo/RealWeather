/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;


/**
 *
 * @author creezo
 */
public class PlayerCheck {
        private static Configuration configuration = new Configuration();
        
        private static boolean CheckToTop(Block PlayerBlock, int MaxMapHeigh) 
        {
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
                if(Inside = false) break;
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
            
        if(configuration.DebugMode()) player.chat("Check end");
        return Inside;
    }
    public static Biome checkPlayerBiome(Player player) {
        Biome BiomeType = player.getLocation().getBlock().getBiome();
        return BiomeType;
    }

    public static int checkPlayerClothes(Player player) {
        int clothesNumber = 0;
        if(player.getInventory().getBoots().getTypeId() != 0) {
            clothesNumber++;
        }
        if(player.getInventory().getChestplate().getTypeId() != 0) {
            clothesNumber++;
        }
        if(player.getInventory().getHelmet().getTypeId() != 0) {
            clothesNumber++;
        }
        if(player.getInventory().getLeggings().getTypeId() != 0) {
            clothesNumber++;
        }
        return clothesNumber;
    }

    private static String ConvertIntToString(int heigh) {
        return "" + heigh;
    }
    
    private static String ConvertDoubleToString(double number) {
        return "" + number;
    }
}
