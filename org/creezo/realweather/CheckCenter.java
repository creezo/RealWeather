package org.creezo.realweather;

import java.util.List;
import java.util.Random;
import net.minecraft.server.v1_6_R3.BiomeBase;
import net.minecraft.server.v1_6_R3.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author creezo
 */
public class CheckCenter {

    private RealWeather plugin;

    public CheckCenter(RealWeather plugin) {
        this.plugin = plugin;
    }

    public double getTemperature(Location location, Player player) {
        if (location != null) {
        } else {
            location = player.getLocation();
        }
        if (RealWeather.isDebug()) {
            RealWeather.log("Starting temp calculation.");
        }
        World world = ((CraftWorld) location.getWorld()).getHandle();
        BiomeBase biome = world.getBiome(location.getBlockX(), location.getBlockZ());
        String biomeName = biome.y;
        if (RealWeather.isDebug()) {
            RealWeather.log("Biome: " + biomeName.toUpperCase());
        }
        int startTemp = plugin.config.getVariables().getBiomes().getGlobal().getBiomeAverageTemp(biomeName);
        if (RealWeather.isDebug()) {
            RealWeather.log("Biome average temp: " + startTemp);
        }
        double temperature;
        double timeMultiplier = Math.sin(Math.toRadians(0.015D * location.getWorld().getTime()));
        
        if (timeMultiplier > 0) {
            temperature = timeMultiplier * (double) plugin.config.getVariables().getBiomes().getGlobal().getBiomeDayNightTempModifier("Day", biomeName);
        } else {
            temperature = Math.abs(timeMultiplier) * (double) plugin.config.getVariables().getBiomes().getGlobal().getBiomeDayNightTempModifier("Night", biomeName);
        }
        try {
        if (plugin.isWeatherModuleLoaded()) {
            if (location.getWorld().hasStorm()) {
                temperature += plugin.config.getVariables().getBiomes().getGlobal().getBiomesWeatherTempModifier(biomeName);
            }
            temperature += plugin.getWeather().getWeatherTemp();
        }
        } catch (NullPointerException e) { if (RealWeather.isDebug()) RealWeather.log("Weather module is errorneous. Skipping weather temp.");}
        temperature += (location.getY() - plugin.config.getVariables().getBiomes().getGlobal().getSeaLevel()) / (location.getWorld().getMaxHeight() - plugin.config.getVariables().getBiomes().getGlobal().getSeaLevel()) * plugin.config.getVariables().getBiomes().getGlobal().getTopTemp();
        temperature += startTemp;
        if (location.getBlock().getLightFromSky() < (byte) 4 && location.getY() < plugin.config.getVariables().getBiomes().getGlobal().getSeaLevel()) {
            double deepModifier;
            if ((double) location.getY() >= (double) plugin.config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.8d) {
                deepModifier = (((double) location.getY() - ((double) plugin.config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.8d)) / ((double) plugin.config.getVariables().getBiomes().getGlobal().getSeaLevel() - (double) plugin.config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.8d)) + ((((double) location.getY() - (double) plugin.config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.8d) / ((double) plugin.config.getVariables().getBiomes().getGlobal().getSeaLevel() - ((double) plugin.config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.8d)) - 1) * (-0.15d));
            } else if ((double) location.getY() <= (double) plugin.config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.2d) {
                if (temperature < 0) {
                    temperature = (temperature * -1) / 2;
                }
                deepModifier = (((double) location.getY() - ((double) plugin.config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.2d)) / (0 - (double) plugin.config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.2d)) + ((((double) location.getY() - (double) plugin.config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.2d) / (0 - ((double) plugin.config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.2d)) - 1) * (-0.15d));
            } else {
                deepModifier = 0.15d;
            }
            if (RealWeather.isDebug()) {
                RealWeather.log("DeepModifier (Number between 1 and 0.15):" + deepModifier);
            }
            temperature = ((temperature - 10) * deepModifier) + 10;
        }
        temperature += checkHeatAround(player, location, plugin.config.getVariables().getBiomes().getGlobal().getHeatCheckRadius());
        if (player != null) {
            List<Entity> Entities = player.getNearbyEntities(plugin.config.getVariables().getBiomes().getGlobal().getHeatCheckRadius(), plugin.config.getVariables().getBiomes().getGlobal().getHeatCheckRadius(), plugin.config.getVariables().getBiomes().getGlobal().getHeatCheckRadius());
            for (Entity entity : Entities) {
                if (entity.getType().isAlive() && temperature <= 25) {
                    temperature += plugin.config.getVariables().getBiomes().getGlobal().getPlayerHeat();
                }
            }
        }
        if (RealWeather.isDebug()) {
            RealWeather.log("Returning temperature: " + temperature);
        }
        return temperature;
    }

    public static boolean checkRandomGrass(Player player, int range, int tries) {
        Random random = new Random();
        for (int i = 0; i < tries; i++) {
            Block thisBlock = player.getLocation().getBlock().getRelative(random.nextInt((range * 2) + 1) - range, random.nextInt((range * 2) + 1) - range, random.nextInt((range * 2) + 1) - range);
            if (thisBlock.getTypeId() == 31) {
                return true;
            }
        }
        return false;
    }

    private static boolean CheckToTop(Block block, int MaxMapHeigh) {
        boolean IsUnderRoof = false;
        int heigh = block.getY();
        while (heigh < MaxMapHeigh) {
            block = block.getRelative(BlockFace.UP);
            if (block.getTypeId() != 0) {
                IsUnderRoof = true;
                break;
            }
            heigh = block.getY();
            IsUnderRoof = false;
        }
        return IsUnderRoof;
    }

    public static boolean checkPlayerInBed(Player player) {
        if (player.isSleeping()) {
            return true;
        }
        return false;
    }

    public static boolean checkPlayerInside(Location location, int checkRadius, String recognizer) {
        boolean inside = false;
        boolean checkOnce = true;
        if (recognizer.equals("simple")) {
            if (RealWeather.isDebug()) {
                RealWeather.log("simple selected");
            }
            inside = CheckToTop(location.getBlock().getRelative(BlockFace.UP), location.getWorld().getMaxHeight() - 1);
        } else if (recognizer.equals("default")) {
            if (RealWeather.isDebug()) {
                RealWeather.log("default selected");
            }
            int heigh = location.getBlockY();
            int MaxHeigh = location.getWorld().getMaxHeight() - 1;
            if (RealWeather.isDebug()) {
                RealWeather.log("Heigh: " + ConvertIntToString(heigh));
            }
            Block NowCheckingBlock = location.getBlock();
            Block StartBlock = location.getBlock();
            for (int radius = 1; radius <= checkRadius; radius++) {
                if (checkOnce == true) {
                    checkOnce = false;
                    inside = CheckToTop(NowCheckingBlock, MaxHeigh);
                    if (inside == false) {
                        break;
                    }
                }

                StartBlock = StartBlock.getRelative(BlockFace.NORTH_WEST);
                NowCheckingBlock = StartBlock;
                int BlockNumInSide = (radius * 2);
                for (int side = 1; side <= 4; side++) {
                    switch (side) {
                        case 1:
                            for (int blocks = 1; blocks <= BlockNumInSide; blocks++) {
                                inside = CheckToTop(NowCheckingBlock, MaxHeigh);
                                if (inside == false) {
                                    break;
                                }
                                NowCheckingBlock = NowCheckingBlock.getRelative(BlockFace.EAST);
                            }
                            break;
                        case 2:
                            for (int blocks = 1; blocks <= BlockNumInSide; blocks++) {
                                inside = CheckToTop(NowCheckingBlock, MaxHeigh);
                                if (inside == false) {
                                    break;
                                }
                                NowCheckingBlock = NowCheckingBlock.getRelative(BlockFace.SOUTH);
                            }
                            break;
                        case 3:
                            for (int blocks = 1; blocks <= BlockNumInSide; blocks++) {
                                inside = CheckToTop(NowCheckingBlock, MaxHeigh);
                                if (inside == false) {
                                    break;
                                }
                                NowCheckingBlock = NowCheckingBlock.getRelative(BlockFace.WEST);
                            }
                            break;
                        case 4:
                            for (int blocks = 1; blocks <= BlockNumInSide; blocks++) {
                                inside = CheckToTop(NowCheckingBlock, MaxHeigh);
                                if (inside == false) {
                                    break;
                                }
                                NowCheckingBlock = NowCheckingBlock.getRelative(BlockFace.NORTH);
                            }
                            break;
                    }
                    if (inside == false) {
                        break;
                    }
                }
                if (inside == false) {
                    break;
                }
            }
        } else if (recognizer.equals("cross")) {
            if (RealWeather.isDebug()) {
                RealWeather.log("cross selected");
            }
            Block RangeCheckBlock;
            int heigh = location.getBlockY();
            Block playerPositionBlock = location.getBlock().getRelative(BlockFace.UP);
            int MaxHeigh = location.getWorld().getMaxHeight() - 1;
            if (RealWeather.isDebug()) {
                RealWeather.log("Heigh: " + ConvertIntToString(heigh));
            }
            for (int once = 1; once == 1; once++) {
                inside = CheckToTop(playerPositionBlock, MaxHeigh);
                if (inside == false) {
                    break;
                }

                int RangeToNorthSide = 0;
                RangeCheckBlock = playerPositionBlock;
                for (int range = 1; range <= checkRadius; range++) {
                    if (RangeCheckBlock.getRelative(BlockFace.NORTH, range).getTypeId() == 0) {
                        RangeToNorthSide++;
                    } else {
                        break;
                    }
                }

                int RangeToEastSide = 0;
                RangeCheckBlock = playerPositionBlock;
                for (int range = 1; range <= checkRadius; range++) {
                    if (RangeCheckBlock.getRelative(BlockFace.EAST, range).getTypeId() == 0) {
                        RangeToEastSide++;
                    } else {
                        break;
                    }
                }

                int RangeToSouthSide = 0;
                RangeCheckBlock = playerPositionBlock;
                for (int range = 1; range <= checkRadius; range++) {
                    if (RangeCheckBlock.getRelative(BlockFace.SOUTH, range).getTypeId() == 0) {
                        RangeToSouthSide++;
                    } else {
                        break;
                    }
                }

                int RangeToWestSide = 0;
                RangeCheckBlock = playerPositionBlock;
                for (int range = 1; range <= checkRadius; range++) {
                    if (RangeCheckBlock.getRelative(BlockFace.WEST, range).getTypeId() == 0) {
                        RangeToWestSide++;
                    } else {
                        break;
                    }
                }

                Block StartBlock = playerPositionBlock.getRelative(BlockFace.NORTH, RangeToNorthSide);
                StartBlock = StartBlock.getRelative(BlockFace.EAST, RangeToEastSide);
                for (int EastWestSize = 0; EastWestSize <= RangeToWestSide + RangeToEastSide; EastWestSize++) {
                    for (int NorthSouthSize = 0; NorthSouthSize <= RangeToNorthSide + RangeToSouthSide; NorthSouthSize++) {
                        inside = CheckToTop(StartBlock.getRelative(NorthSouthSize, 0, EastWestSize), MaxHeigh);
                        if (inside == false) {
                            break;
                        }
                    }
                    if (inside == false) {
                        break;
                    }
                }
            }
        }
        return inside;
    }

    public static BiomeBase checkPlayerBiome(Player player) {
        World world = ((CraftWorld) player.getLocation().getWorld()).getHandle();
        BiomeBase BiomeType = world.getBiome(player.getLocation().getBlockX(), player.getLocation().getBlockZ());
        return BiomeType;
    }

    public static BiomeBase checkPlayerBiome(Location loc) {
        World world = ((CraftWorld) loc.getWorld()).getHandle();
        BiomeBase BiomeType = world.getBiome(loc.getBlockX(), loc.getBlockZ());
        return BiomeType;
    }

    public static double[] getPlrResist(Player player, String resistType) {
        double[] resist = {1, 0};
        ItemStack WearBoots = player.getInventory().getBoots();
        ItemStack WearChestplate = player.getInventory().getChestplate();
        ItemStack WearHelmet = player.getInventory().getHelmet();
        ItemStack WearLeggings = player.getInventory().getLeggings();
        int BootsID = 0, ChestplateID = 0, HelmetID = 0, LeggingsID = 0;
        try {
            BootsID = WearBoots.getTypeId();
            if (RealWeather.isDebug()) {
                RealWeather.log("BootsID: " + BootsID);
            }
        } catch (Exception ex) {
            if (RealWeather.isDebug()) {
                RealWeather.log("No Boots.");
            }
        }
        try {
            ChestplateID = WearChestplate.getTypeId();
            if (RealWeather.isDebug()) {
                RealWeather.log("ChestplateID: " + ChestplateID);
            }
        } catch (Exception ex) {
            if (RealWeather.isDebug()) {
                RealWeather.log("No Chestplate.");
            }
        }
        try {
            HelmetID = WearHelmet.getTypeId();
            if (RealWeather.isDebug()) {
                RealWeather.log("HelmetID: " + HelmetID);
            }
        } catch (Exception ex) {
            if (RealWeather.isDebug()) {
                RealWeather.log("No Helmet.");
            }
        }
        try {
            LeggingsID = WearLeggings.getTypeId();
            if (RealWeather.isDebug()) {
                RealWeather.log("LeggingsID: " + LeggingsID);
            }
        } catch (Exception ex) {
            if (RealWeather.isDebug()) {
                RealWeather.log("No Leggings.");
            }
        }
        if (RealWeather.isDebug()) {
            RealWeather.log("BootsID: " + BootsID);
        }
        if (RealWeather.isDebug()) {
            RealWeather.log("ChestplateID: " + ChestplateID);
        }
        if (RealWeather.isDebug()) {
            RealWeather.log("HelmetID: " + HelmetID);
        }
        if (RealWeather.isDebug()) {
            RealWeather.log("LeggingsID: " + LeggingsID);
        }
        if (RealWeather.isDebug()) {
            RealWeather.log("Resist2(0): " + resist[0]);
        }
        if (BootsID != 0) {
            double[] vars = RealWeather.getArmors().getResistance(BootsID, resistType);
            resist[0] *= vars[0];
            resist[1] += vars[1];
        }
        if (RealWeather.isDebug()) {
            RealWeather.log("Resist2(1): " + resist[0]);
        }
        if (ChestplateID != 0) {
            double[] vars = RealWeather.getArmors().getResistance(ChestplateID, resistType);
            resist[0] *= vars[0];
            resist[1] += vars[1];
        }
        if (RealWeather.isDebug()) {
            RealWeather.log("Resist2(2): " + resist[0]);
        }
        if (HelmetID != 0) {
            double[] vars = RealWeather.getArmors().getResistance(HelmetID, resistType);
            resist[0] *= vars[0];
            resist[1] += vars[1];
        }
        if (RealWeather.isDebug()) {
            RealWeather.log("Resist2(3): " + resist[0]);
        }
        if (LeggingsID != 0) {
            double[] vars = RealWeather.getArmors().getResistance(LeggingsID, resistType);
            resist[0] *= vars[0];
            resist[1] += vars[1];
        }
        if (RealWeather.isDebug()) {
            RealWeather.log("Resist2(4): " + resist[0]);
        }
        return resist;
    }

    public double checkHeatAround(Player player, Location location, int HeatCheckRadius) {
        if (RealWeather.isDebug()) {
            RealWeather.log("Checking heat...");
        }
        double NumOfTorches = 1;
        double Temperature = 0;
        double BlockPower;
        double rangeDouble;
        double varOne;
        boolean cooler;
        Block playerBlock = location.getBlock();
        Block startBlock = playerBlock.getRelative(HeatCheckRadius * (-1) - 1, (HeatCheckRadius * (-1)), HeatCheckRadius * (-1) - 1);
        for (int x = 1; x <= (HeatCheckRadius * 2) + 1; x++) {
            for (int z = 1; z <= (HeatCheckRadius * 2) + 1; z++) {
                for (int y = 1; y <= (HeatCheckRadius * 2); y++) {
                    if (plugin.heatSources.containsKey(startBlock.getRelative(x, y, z).getType())) {
                        BlockPower = plugin.heatSources.get(startBlock.getRelative(x, y, z).getType());
                        if (plugin.config.getVariables().getBiomes().getGlobal().isTorchesFading() && startBlock.getRelative(x, y, z).getType().equals(Material.TORCH)) {
                            BlockPower /= NumOfTorches;
                            NumOfTorches++;
                        }
                        if (BlockPower >= 0) {
                            cooler = false;
                        } else {
                            cooler = true;
                        }
                    } else {
                        BlockPower = 0;
                        cooler = false;
                    }
                    if (BlockPower != 0) {
                        rangeDouble = startBlock.getRelative(x, y, z).getLocation().distance(playerBlock.getLocation());
                        varOne = BlockPower * (1 - (rangeDouble / (HeatCheckRadius * 2)));
                        if (varOne >= 0.0d && cooler == false) {
                            Temperature += varOne;
                        } else if (varOne <= 0.0d && cooler == true) {
                            Temperature += varOne;
                        }
                    }
                }
            }
        }
        if (player != null) {
            if (plugin.heatInHand.containsKey(player.getItemInHand().getType())) {
                BlockPower = plugin.heatInHand.get(player.getItemInHand().getType());
            } else {
                BlockPower = 0;
            }
        } else {
            BlockPower = 0;
        }
        if (RealWeather.isDebug()) {
            RealWeather.log("From item in hand: " + ConvertIntToString((int) BlockPower));
        }
        Temperature += BlockPower;
        if (RealWeather.isDebug()) {
            RealWeather.log("Total heat from blocks and items: " + Temperature);
        }
        return Temperature;
    }

    private static String ConvertIntToString(int number) {
        return "" + number;
    }
}