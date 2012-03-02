/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creezo.realwinter;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


/**
 *
 * @author creezo
 */
public class PlayerCheck {
        private static Configuration configuration = new Configuration();
        
    public static boolean checkPlayerInside(Player player) {
        boolean Inside = false;
        Location playerPosition = player.getLocation();
        int heigh = playerPosition.getBlockY();
        
        if(configuration.DebugMode()) player.chat(simpleConvert(heigh));
        
        Block block = playerPosition.getBlock();
        Block newBlock = null;
        newBlock = block.getRelative(0, 0, 0);
        while(heigh < 126) {
            newBlock = newBlock.getRelative(0, 1, 0);
            if(newBlock.getTypeId() != 0) {
                Inside = true;
                break;
            }
            if(configuration.DebugMode()) player.chat("Outside");
            heigh = newBlock.getY();
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

    private static String simpleConvert(int heigh) {
        return "" + heigh;
    }
}
