package com.ternsip.structpro.Structure;

import com.ternsip.structpro.World.Blocks.Blocks;
import net.minecraft.block.Block;

import java.io.File;
import java.util.HashMap;

/* Possible structure biome styles. Have to completely cover all known biomes */
public enum Biome {

    COMMON (0x00, "COMMON"),
    SNOW (0x01, "SNOW"),
    NETHER (0x02, "NETHER"),
    SAND (0x03, "SAND"),
    MUSHROOM (0x04, "MUSHROOM"),
    MESA (0x05, "MESA"),
    END (0x07, "END"),
    WATER (0x08, "WATER");

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

    static final HashMap<Biome, Block[]> bioBlocks = new HashMap<Biome, Block[]>(){{
        put(COMMON, new Block[]{});
        put(SNOW, new Block[]{Blocks.SNOW_LAYER, Blocks.SNOW, Blocks.ICE});
        put(SAND, new Block[]{Blocks.SAND, Blocks.SANDSTONE, Blocks.SANDSTONE_STAIRS});
        put(MESA, new Block[]{Blocks.STAINED_HARDENED_CLAY, Blocks.HARDENED_CLAY, Blocks.CLAY});
        put(MUSHROOM,  new Block[]{Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK});
        put(WATER, new Block[]{Blocks.WATER, Blocks.FLOWING_WATER});
        put(NETHER, new Block[]{Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.NETHER_BRICK, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.OBSIDIAN});
        put(END, new Block[]{Blocks.END_STONE});
    }};

    public final int value;
    public final String name;

    Biome(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /* Get biome by it internal value */
    public static Biome valueOf(int value) {
        for (Biome sample : Biome.values()) {
            if (sample.value == value) {
                return sample;
            }
        }
        return COMMON;
    }

    /* Detect Biome.Style by given set of blocks */
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
                count += counts[Blocks.blockID(block)];
                frequency += frequencies[Blocks.blockID(block)];
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

    /* Determine Biome by given Minecraft Biome */
    static Biome valueOf(net.minecraft.world.biome.Biome biome) {
        String biomeName = biome.getBiomeName().toLowerCase().replace(" ", "");
        for (HashMap.Entry<Biome, String[]> entry : bioNames.entrySet()) {
            for (int i = 0; i < entry.getValue().length; ++i) {
                if (biomeName.contains(entry.getValue()[i].toLowerCase())) {
                    return entry.getKey();
                }
            }
        }
        return Biome.COMMON;
    }

    /* Get biome by given file path */
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

    /* Get biome by file or blocks */
    static Biome valueOf(File file, short[] blocks) {
        Biome biome = valueOf(file);
        return biome != null ? biome : valueOf(blocks);
    }

}