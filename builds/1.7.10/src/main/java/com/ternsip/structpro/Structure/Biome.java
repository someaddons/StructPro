package com.ternsip.structpro.Structure;

import com.ternsip.structpro.Universe.Blocks.Blocks;
import net.minecraft.block.Block;

import java.io.File;
import java.util.HashMap;

/**
 * Possible structure biome styles
 * Have to completely cover all known biomes
 * @author  Ternsip
 * @since JDK 1.6
 */
public enum Biome {

    COMMON (0x00, "COMMON"),
    SNOW (0x01, "SNOW"),
    NETHER (0x02, "NETHER"),
    SAND (0x03, "SAND"),
    MUSHROOM (0x04, "MUSHROOM"),
    MESA (0x05, "MESA"),
    END (0x07, "END"),
    WATER (0x08, "WATER");

    public final int value;
    public final String name;

    /**
     * Default biome constructor
     * @param value Biome index
     * @param name Biome name
     */
    Biome(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * Biome names map
     * Reflect biome to potential biome sub-name array
     * If sub-name contains in game-biome full-name then it depends to this biome enum
     * In case game-biome depends to multiple biome enums random is selected
     */
    static final HashMap<Biome, String[]> bioNames = new HashMap<Biome, String[]>(){{
        put(COMMON, new String[]{});
        put(SNOW, new String[]{"Frozen", "Ice", "Cold", "Alps", "Arctic", "Frost", "Icy", "Snow", "Coniferous", "Tundra", "Glacier"});
        put(SAND, new String[]{"Desert", "Canyon", "Dune", "Beach", "Mangrove", "Oasis", "Xeric"});
        put(MESA, new String[]{"Mesa", "Badlands", "LushDesert"});
        put(MUSHROOM, new String[]{"Roofed", "Mushroom", "Fungi"});
        put(WATER, new String[]{"Ocean", "Coral", "Pond", "Kelp", "River"});
        put(NETHER, new String[]{"Hell", "Bloody", "Boneyard", "Corrupted", "Inferno", "Chasm", "Undergarden", "Nether"});
        put(END, new String[]{"TheEnd"});
    }};

    /**
     * Biome blocks map
     * Reflect biome to potenial biome block array
     * Used for counting blocks of each biome
     */
    static final HashMap<Biome, Block[]> bioBlocks = new HashMap<Biome, Block[]>(){{
        put(COMMON, new Block[]{});
        put(SNOW, new Block[]{Blocks.snow_layer, Blocks.snow, Blocks.ice});
        put(SAND, new Block[]{Blocks.sand, Blocks.sandstone, Blocks.sandstone_stairs});
        put(MESA, new Block[]{Blocks.stained_hardened_clay, Blocks.hardened_clay, Blocks.clay});
        put(MUSHROOM,  new Block[]{Blocks.red_mushroom_block, Blocks.brown_mushroom_block});
        put(WATER, new Block[]{Blocks.water, Blocks.flowing_water});
        put(NETHER, new Block[]{Blocks.netherrack, Blocks.soul_sand, Blocks.nether_brick, Blocks.nether_brick_fence, Blocks.nether_brick_stairs, Blocks.obsidian});
        put(END, new Block[]{Blocks.end_stone});
    }};

    /**
     * Get biome by it internal value
     * @param value Biome internal number
     * @return The biome
     */
    public static Biome valueOf(int value) {
        for (Biome sample : Biome.values()) {
            if (sample.value == value) {
                return sample;
            }
        }
        return COMMON;
    }

    /**
     * Get biome by given set of blocks
     * @param blocks array of blocks
     * @return The biome
     */
    static Biome valueOf(short[] blocks) {

        /* Counts [0..SIZE] of each vanilla blocks */
        double[] counts = new double[256];
        for (short blockID : blocks) {
            if (blockID >= 0 && blockID < 256) {
                counts[blockID] += 1.0;
            }
        }

        /* Frequency [0..1] of each vanilla block, exclusive air */
        double[] frequencies = new double[256];
        double notAir = 1 + blocks.length - counts[0];
        frequencies[0] /= counts[0] / blocks.length;
        for (int i = 1; i < 256; ++i) {
            frequencies[i] = counts[i] / notAir;
        }

        HashMap<Biome, Double> bioCounts = new HashMap<Biome, Double>();
        HashMap<Biome, Double> bioFrequencies = new HashMap<Biome, Double>();

        for (Biome biome : Biome.values()) {
            double count = 0, frequency = 0;
            for (Block block : bioBlocks.get(biome)) {
                count += counts[Blocks.getID(block)];
                frequency += frequencies[Blocks.getID(block)];
            }
            bioCounts.put(biome, count);
            bioFrequencies.put(biome, frequency);
        }

        if (bioCounts.get(SNOW) > 8.5) return Biome.SNOW;
        if (bioFrequencies.get(END) > 0.25) return  Biome.END;
        if (bioFrequencies.get(NETHER) > 0.25) return  Biome.NETHER;
        if (bioFrequencies.get(SAND) > 0.25) return  Biome.SAND;
        if (bioFrequencies.get(MESA) > 0.25) return  Biome.MESA;
        if (bioFrequencies.get(MUSHROOM) > 0.1) return  Biome.MUSHROOM;
        return  Biome.COMMON;

    }

    /**
     * Determine Biome by given Minecraft Biome
     * @param biome Minecraft native biome
     * @return The biome
     */
    public static Biome valueOf(net.minecraft.world.biome.BiomeGenBase biome) {
        String biomeName = biome.biomeName.toLowerCase().replace(" ", "");
        for (HashMap.Entry<Biome, String[]> entry : bioNames.entrySet()) {
            for (int i = 0; i < entry.getValue().length; ++i) {
                if (biomeName.contains(entry.getValue()[i].toLowerCase())) {
                    return entry.getKey();
                }
            }
        }
        return Biome.COMMON;
    }

    /**
     * Get biome by given file, works only with path
     * @param file target file
     * @return The biome
     */
    static Biome valueOf(File file) {
        String path = file.getPath().toLowerCase().replace("\\", "/").replace("//", "/");
        if (path.contains("/sand/"))        return Biome.SAND;
        if (path.contains("/desert/"))      return Biome.SAND;
        if (path.contains("/snow/"))        return Biome.SNOW;
        if (path.contains("/ice/"))         return Biome.SNOW;
        if (path.contains("/cold/"))        return Biome.SNOW;
        if (path.contains("/water/"))       return Biome.WATER;
        if (path.contains("/end/"))         return Biome.END;
        if (path.contains("/mesa/"))        return Biome.MESA;
        if (path.contains("/mushroom/"))    return Biome.MUSHROOM;
        if (path.contains("/nether/"))      return Biome.NETHER;
        if (path.contains("/hell/"))        return Biome.NETHER;
        return null;
    }

    /**
     * Get most appropriate biome by file and blocks
     * @param file target file
     * @param blocks array of blocks
     * @return The biome
     */
    static Biome valueOf(File file, short[] blocks) {
        Biome biome = valueOf(file);
        return biome != null ? biome : valueOf(blocks);
    }

}