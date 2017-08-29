package com.ternsip.structpro.universe.biomes;

import com.ternsip.structpro.universe.blocks.UBlock;
import com.ternsip.structpro.universe.blocks.UBlocks;

import java.io.File;
import java.util.HashMap;

/**
 * Possible biome styles
 * Have to completely cover all known biomes
 * @author  Ternsip
 */
public enum Biomus {

    COMMON (0x00, "COMMON"),
    SNOW (0x01, "SNOW"),
    NETHER (0x02, "NETHER"),
    SAND (0x03, "SAND"),
    MUSHROOM (0x04, "MUSHROOM"),
    MESA (0x05, "MESA"),
    END (0x07, "END"),
    WATER (0x08, "WATER");

    private final int value;
    private final String name;

    /**
     * Default biomus constructor
     * @param value Biomus index
     * @param name Biomus name
     */
    Biomus(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * Biome names map
     * Reflect biome to potential biome sub-name array
     * If sub-name contains in game-biome full-name then it depends to this biome enum
     * In case game-biome depends to multiple biome enums random is selected
     */
    private static final HashMap<Biomus, String[]> bioNames = new HashMap<Biomus, String[]>(){{
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
     * Reflect biomus to potenial biome block array
     * Used for counting blocks of each biome
     */
    private static final HashMap<Biomus, UBlock[]> bioBlocks = new HashMap<Biomus, UBlock[]>(){{
        put(COMMON, new UBlock[]{});
        put(SNOW, new UBlock[]{UBlocks.SNOW_LAYER, UBlocks.SNOW, UBlocks.ICE});
        put(SAND, new UBlock[]{UBlocks.SAND, UBlocks.SANDSTONE, UBlocks.SANDSTONE_STAIRS});
        put(MESA, new UBlock[]{UBlocks.STAINED_HARDENED_CLAY, UBlocks.HARDENED_CLAY, UBlocks.CLAY});
        put(MUSHROOM,  new UBlock[]{UBlocks.RED_MUSHROOM_BLOCK, UBlocks.BROWN_MUSHROOM_BLOCK});
        put(WATER, new UBlock[]{UBlocks.WATER, UBlocks.FLOWING_WATER});
        put(NETHER, new UBlock[]{UBlocks.NETHERRACK, UBlocks.SOUL_SAND, UBlocks.NETHER_BRICK, UBlocks.NETHER_BRICK_FENCE, UBlocks.NETHER_BRICK_STAIRS, UBlocks.OBSIDIAN});
        put(END, new UBlock[]{UBlocks.END_STONE});
    }};

    /**
     * Get biomus by it internal value
     * @param value Biome internal number
     * @return The biome
     */
    public static Biomus valueOf(int value) {
        for (Biomus biomus : Biomus.values()) {
            if (biomus.value == value) {
                return biomus;
            }
        }
        return COMMON;
    }

    /**
     * Get biomus by given set of blocks
     * @param blocks array of blocks
     * @return The biome
     */
    public static Biomus valueOf(short[] blocks) {

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

        HashMap<Biomus, Double> bioCounts = new HashMap<>();
        HashMap<Biomus, Double> bioFrequencies = new HashMap<>();

        for (Biomus biomus : Biomus.values()) {
            double count = 0, frequency = 0;
            for (UBlock block : bioBlocks.get(biomus)) {
                count += counts[block.getID()];
                frequency += frequencies[block.getID()];
            }
            bioCounts.put(biomus, count);
            bioFrequencies.put(biomus, frequency);
        }

        if (bioCounts.get(SNOW) > 8.5) return Biomus.SNOW;
        if (bioFrequencies.get(END) > 0.25) return  Biomus.END;
        if (bioFrequencies.get(NETHER) > 0.25) return  Biomus.NETHER;
        if (bioFrequencies.get(SAND) > 0.25) return  Biomus.SAND;
        if (bioFrequencies.get(MESA) > 0.25) return  Biomus.MESA;
        if (bioFrequencies.get(MUSHROOM) > 0.1) return  Biomus.MUSHROOM;
        return Biomus.COMMON;

    }

    /**
     * Determine Biomus by given Biome
     * @param biome Minecraft native biome
     * @return The biome
     */
    public static Biomus valueOf(UBiome biome) {
        if (!biome.isValid()) {
            return Biomus.COMMON;
        }
        String biomeName = biome.getPath().toLowerCase().replace(" ", "");
        for (HashMap.Entry<Biomus, String[]> entry : bioNames.entrySet()) {
            for (int i = 0; i < entry.getValue().length; ++i) {
                if (biomeName.contains(entry.getValue()[i].toLowerCase())) {
                    return entry.getKey();
                }
            }
        }
        return Biomus.COMMON;
    }

    /**
     * Get biomus by given file, works only with path
     * @param file target file
     * @return The biome
     */
    public static Biomus valueOf(File file) {
        String path = file.getPath().toLowerCase().replace("\\", "/").replace("//", "/");
        if (path.contains("/sand/"))        return Biomus.SAND;
        if (path.contains("/desert/"))      return Biomus.SAND;
        if (path.contains("/snow/"))        return Biomus.SNOW;
        if (path.contains("/ice/"))         return Biomus.SNOW;
        if (path.contains("/cold/"))        return Biomus.SNOW;
        if (path.contains("/water/"))       return Biomus.WATER;
        if (path.contains("/end/"))         return Biomus.END;
        if (path.contains("/mesa/"))        return Biomus.MESA;
        if (path.contains("/mushroom/"))    return Biomus.MUSHROOM;
        if (path.contains("/nether/"))      return Biomus.NETHER;
        if (path.contains("/hell/"))        return Biomus.NETHER;
        return null;
    }

    /**
     * Get most appropriate biomus by file and blocks
     * @param file target file
     * @param blocks array of blocks
     * @return The biome
     */
    public static Biomus valueOf(File file, short[] blocks) {
        Biomus biomus = valueOf(file);
        return biomus != null ? biomus : valueOf(blocks);
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}