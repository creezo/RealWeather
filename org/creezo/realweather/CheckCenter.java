package org.creezo.realweather;

import java.util.List;
import java.util.Random;
import net.minecraft.server.v1_6_R2.BiomeBase;
import net.minecraft.server.v1_6_R2.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
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
        if (plugin.Config.getVariables().isDebugMode()) {
            plugin.log("Starting temp calculation.");
        }
        World world = ((CraftWorld) location.getWorld()).getHandle();
        BiomeBase biome = world.getBiome(location.getBlockX(), location.getBlockZ());
        String biomeName = biome.y;
        plugin.actualWeather = location.getWorld().hasStorm();
        if (plugin.Config.getVariables().isDebugMode()) {
            plugin.log("Biome: " + biomeName.toUpperCase());
        }
        int StartTemp = plugin.Config.getVariables().getBiomes().getGlobal().getBiomeAverageTemp(biomeName);
        int WeatherModifier = 0;
        double Temperature;
        double TimeMultiplier = Math.sin(Math.toRadians(0.015D * location.getWorld().getTime()));
        if (plugin.actualWeather) {
            WeatherModifier = plugin.Config.getVariables().getBiomes().getGlobal().getBiomesWeatherTempModifier(biomeName);
        }
        if (TimeMultiplier > 0) {
            Temperature = TimeMultiplier * (double) plugin.Config.getVariables().getBiomes().getGlobal().getBiomeDayNightTempModifier("Day", biomeName);
        } else {
            Temperature = Math.abs(TimeMultiplier) * (double) plugin.Config.getVariables().getBiomes().getGlobal().getBiomeDayNightTempModifier("Night", biomeName);
        }
        Temperature += (location.getY() - plugin.Config.getVariables().getBiomes().getGlobal().getSeaLevel()) / (location.getWorld().getMaxHeight() - plugin.Config.getVariables().getBiomes().getGlobal().getSeaLevel()) * plugin.Config.getVariables().getBiomes().getGlobal().getTopTemp();
        Temperature += plugin.ForecastTemp;
        Temperature += StartTemp;
        Temperature += WeatherModifier;
        if (location.getBlock().getLightFromSky() < (byte) 4 && location.getY() < plugin.Config.getVariables().getBiomes().getGlobal().getSeaLevel()) {
            double DeepModifier;
            if ((double) location.getY() >= (double) plugin.Config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.8d) {
                DeepModifier = (((double) location.getY() - ((double) plugin.Config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.8d)) / ((double) plugin.Config.getVariables().getBiomes().getGlobal().getSeaLevel() - (double) plugin.Config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.8d)) + ((((double) location.getY() - (double) plugin.Config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.8d) / ((double) plugin.Config.getVariables().getBiomes().getGlobal().getSeaLevel() - ((double) plugin.Config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.8d)) - 1) * (-0.15d));
            } else if ((double) location.getY() <= (double) plugin.Config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.2d) {
                if (Temperature < 0) {
                    Temperature = (Temperature * -1) / 2;
                }
                DeepModifier = (((double) location.getY() - ((double) plugin.Config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.2d)) / (0 - (double) plugin.Config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.2d)) + ((((double) location.getY() - (double) plugin.Config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.2d) / (0 - ((double) plugin.Config.getVariables().getBiomes().getGlobal().getSeaLevel() * 0.2d)) - 1) * (-0.15d));
            } else {
                DeepModifier = 0.15d;
            }
            if (plugin.Config.getVariables().isDebugMode()) {
                plugin.log("DeepModifier (Number between 1 and 0.15):" + DeepModifier);
            }
            Temperature = ((Temperature - 10) * DeepModifier) + 10;
        }
        Temperature += checkHeatAround(player, location, plugin.Config.getVariables().getBiomes().getGlobal().getHeatCheckRadius());
        if (player != null) {
            List<Entity> Entities = player.getNearbyEntities(plugin.Config.getVariables().getBiomes().getGlobal().getHeatCheckRadius(), plugin.Config.getVariables().getBiomes().getGlobal().getHeatCheckRadius(), plugin.Config.getVariables().getBiomes().getGlobal().getHeatCheckRadius());
            for (Entity entity : Entities) {
                if (entity.getType().isAlive() && Temperature <= 25) {
                    Temperature += plugin.Config.getVariables().getBiomes().getGlobal().getPlayerHeat();
                }
            }
        }
        return Temperature;
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

    public boolean checkPlayerInBed(Player player) {
        if (player.isSleeping()) {
            return true;
        }
        return false;
    }

    public boolean checkPlayerInside(Location location, int checkRadius, String recognizer) {
        boolean inside = false;
        boolean checkOnce = true;
        if (recognizer.equals("simple")) {
            if (plugin.Config.getVariables().isDebugMode()) {
                plugin.log("simple selected");
            }
            inside = CheckToTop(location.getBlock().getRelative(BlockFace.UP), location.getWorld().getMaxHeight() - 1);
        } else if (recognizer.equals("default")) {
            if (plugin.Config.getVariables().isDebugMode()) {
                plugin.log("default selected");
            }
            int heigh = location.getBlockY();
            int MaxHeigh = location.getWorld().getMaxHeight() - 1;
            if (plugin.Config.getVariables().isDebugMode()) {
                plugin.log("Heigh: " + ConvertIntToString(heigh));
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
            if (plugin.Config.getVariables().isDebugMode()) {
                plugin.log("cross selected");
            }
            Block RangeCheckBlock;
            int heigh = location.getBlockY();
            Block playerPositionBlock = location.getBlock().getRelative(BlockFace.UP);
            int MaxHeigh = location.getWorld().getMaxHeight() - 1;
            if (plugin.Config.getVariables().isDebugMode()) {
                plugin.log("Heigh: " + ConvertIntToString(heigh));
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

    public double[] getPlrResist(Player player, String resistType) {
        double[] resist = {1, 0};
        ItemStack WearBoots = player.getInventory().getBoots();
        ItemStack WearChestplate = player.getInventory().getChestplate();
        ItemStack WearHelmet = player.getInventory().getHelmet();
        ItemStack WearLeggings = player.getInventory().getLeggings();
        int BootsID = 0, ChestplateID = 0, HelmetID = 0, LeggingsID = 0;
        try {
            BootsID = WearBoots.getTypeId();
            if (plugin.Config.getVariables().isDebugMode()) {
                plugin.log("BootsID: " + BootsID);
            }
        } catch (Exception ex) {
            if (plugin.Config.getVariables().isDebugMode()) {
                plugin.log("No Boots.");
            }
        }
        try {
            ChestplateID = WearChestplate.getTypeId();
            if (plugin.Config.getVariables().isDebugMode()) {
                plugin.log("ChestplateID: " + ChestplateID);
            }
        } catch (Exception ex) {
            if (plugin.Config.getVariables().isDebugMode()) {
                plugin.log("No Chestplate.");
            }
        }
        try {
            HelmetID = WearHelmet.getTypeId();
            if (plugin.Config.getVariables().isDebugMode()) {
                plugin.log("HelmetID: " + HelmetID);
            }
        } catch (Exception ex) {
            if (plugin.Config.getVariables().isDebugMode()) {
                plugin.log("No Helmet.");
            }
        }
        try {
            LeggingsID = WearLeggings.getTypeId();
            if (plugin.Config.getVariables().isDebugMode()) {
                plugin.log("LeggingsID: " + LeggingsID);
            }
        } catch (Exception ex) {
            if (plugin.Config.getVariables().isDebugMode()) {
                plugin.log("No Leggings.");
            }
        }
        if (plugin.Config.getVariables().isDebugMode()) {
            plugin.log("BootsID: " + BootsID);
        }
        if (plugin.Config.getVariables().isDebugMode()) {
            plugin.log("ChestplateID: " + ChestplateID);
        }
        if (plugin.Config.getVariables().isDebugMode()) {
            plugin.log("HelmetID: " + HelmetID);
        }
        if (plugin.Config.getVariables().isDebugMode()) {
            plugin.log("LeggingsID: " + LeggingsID);
        }
        if (plugin.Config.getVariables().isDebugMode()) {
            plugin.log("Resist2(0): " + resist[0]);
        }
        if (BootsID != 0) {
            double[] vars = plugin.Config.getVariables().getArmours().getResistance(BootsID, resistType);
            resist[0] *= vars[0];
            resist[1] += vars[1];
        }
        if (plugin.Config.getVariables().isDebugMode()) {
            plugin.log("Resist2(1): " + resist[0]);
        }
        if (ChestplateID != 0) {
            double[] vars = plugin.Config.getVariables().getArmours().getResistance(ChestplateID, resistType);
            resist[0] *= vars[0];
            resist[1] += vars[1];
        }
        if (plugin.Config.getVariables().isDebugMode()) {
            plugin.log("Resist2(2): " + resist[0]);
        }
        if (HelmetID != 0) {
            double[] vars = plugin.Config.getVariables().getArmours().getResistance(HelmetID, resistType);
            resist[0] *= vars[0];
            resist[1] += vars[1];
        }
        if (plugin.Config.getVariables().isDebugMode()) {
            plugin.log("Resist2(3): " + resist[0]);
        }
        if (LeggingsID != 0) {
            double[] vars = plugin.Config.getVariables().getArmours().getResistance(LeggingsID, resistType);
            resist[0] *= vars[0];
            resist[1] += vars[1];
        }
        if (plugin.Config.getVariables().isDebugMode()) {
            plugin.log("Resist2(4): " + resist[0]);
        }
        return resist;
    }

    public double checkHeatAround(Player player, Location location, int HeatCheckRadius) {
        if (plugin.Config.getVariables().isDebugMode()) {
            plugin.log("Checking heat...");
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
                    if (plugin.HeatSources.containsKey(startBlock.getRelative(x, y, z).getType())) {
                        BlockPower = plugin.HeatSources.get(startBlock.getRelative(x, y, z).getType());
                        if (plugin.Config.getVariables().getBiomes().getGlobal().isTorchesFading() && startBlock.getRelative(x, y, z).getType().equals(Material.TORCH)) {
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
            if (plugin.HeatInHand.containsKey(player.getItemInHand().getType())) {
                BlockPower = plugin.HeatInHand.get(player.getItemInHand().getType());
            } else {
                BlockPower = 0;
            }
        } else {
            BlockPower = 0;
        }
        if (plugin.Config.getVariables().isDebugMode()) {
            plugin.log("From item in hand: " + ConvertIntToString((int) BlockPower));
        }
        Temperature += BlockPower;
        if (plugin.Config.getVariables().isDebugMode()) {
            plugin.log("Total heat from blocks and items: " + Temperature);
        }
        return Temperature;
    }

    public boolean isInIce(Player player) {
        if (player.getLocation().getBlock().getType().equals(Material.ICE)) {
            return true;
        }
        return false;
    }

    private static String ConvertIntToString(int number) {
        return "" + number;
    }
}