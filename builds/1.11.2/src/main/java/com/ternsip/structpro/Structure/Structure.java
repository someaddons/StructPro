package com.ternsip.structpro.Structure;

import com.ternsip.structpro.Logic.Blocks;
import com.ternsip.structpro.Utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

/* Holds schematic-extended information and can load/spawn/calibrate it */
public class Structure extends Blueprint {

    /* Structure version */
    private static final int VERSION = 103;

    public Method getMethod() {
        return method;
    }

    public Biome getBiome() {
        return biome;
    }

    public int getLift() {
        return lift;
    }

    /*
    * Spawn methods
    * Basic - spawn on ground and pass water
    * Underground - spawn under soil stratum
    * Underwater - spawn under water on bottom
    * Sky - spawn floating in the sky
    * Hill - spawn on ground without roughness bias
    */
    public enum Method {
        BASIC (0x00, "BASIC"),
        UNDERGROUND (0x01, "UNDERGROUND"),
        UNDERWATER(0x02, "UNDERWATER"),
        AFLOAT (0x03, "AFLOAT"),
        SKY (0x04, "SKY"),
        HILL (0x05, "HILL");

        public final int value;
        public final String name;

        Method(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public static Method valueOf(int value) {
            for (Method sample : Method.values()) {
                if (sample.value == value) {
                    return sample;
                }
            }
            return BASIC;
        }

        private static Method valueOf(File file) {
            String path = file.getPath().toLowerCase().replace("\\", "/").replace("//", "/");
            if (path.contains("/underground/"))     return Method.UNDERGROUND;
            if (path.contains("/sky/"))             return Method.SKY;
            if (path.contains("/floating/"))        return Method.SKY;
            if (path.contains("/afloat/"))          return Method.AFLOAT;
            if (path.contains("/water/"))           return Method.AFLOAT;
            if (path.contains("/underwater/"))      return Method.UNDERWATER;
            if (path.contains("/hill/"))            return Method.HILL;
            if (path.contains("/mountain/"))        return Method.HILL;
            return Method.BASIC;
        }

    }

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

        /* Biome ID to Biome.Style */
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

        static Biome valueOf(File file, short[] blocks) {
            Biome biome = valueOf(file);
            return biome != null ? biome : valueOf(blocks);
        }

    }

    private long schemaLen;
    Method method;
    Biome biome;
    int lift;
    BitSet skin;

    Structure() {}

    @Override
    void loadSchematic(File file) throws IOException {
        super.loadSchematic(file);
        schemaLen = file.length();
        method = Method.valueOf(file);
        biome = Biome.valueOf(file, blocks);
        lift = calcLift();
        skin = getSkin();
    }

    void loadFlags(File schematic, File flags) throws IOException {
        readFlags(readTags(flags));
        if (schemaLen != schematic.length()) {
            throw new IOException("Mismatched schematic length: " + schematic.length() + "/" + schemaLen);
        }
    }

    void loadData(File schematic, File data) throws IOException {
        super.loadSchematic(schematic);
        readData(readTags(data));
    }

    void saveFlags(File file) throws IOException {
        writeTags(file, getFlags());
    }

    void saveData(File file) throws IOException {
        writeTags(file, getData());
    }

    void free() {
        blocks = null;
        meta = null;
        skin = null;
        tiles = null;
    }

    private NBTTagCompound getData() {
        NBTTagCompound result = new NBTTagCompound();
        result.setByteArray("Skin", Utils.toByteArray(skin));
        return result;
    }

    private NBTTagCompound getFlags() {
        NBTTagCompound result = new NBTTagCompound();
        result.setInteger("Version", VERSION);
        result.setShort("Width", (short)width);
        result.setShort("Height", (short)height);
        result.setShort("Length", (short)length);
        result.setLong("SchemaLen", schemaLen);
        result.setInteger("Lift", lift);
        result.setInteger("Method", method.value);
        result.setInteger("Biome", biome.value);
        return result;
    }

    private void readData(NBTTagCompound data) throws IOException {
        skin = Utils.toBitSet(data.getByteArray("Skin"));
    }

    private void readFlags(NBTTagCompound flags) throws IOException {
        int version = flags.getInteger("Version");
        if (version != VERSION) {
            throw new IOException("Incomplete version: " + version + " requires " + VERSION);
        }
        width = flags.getShort("Width");
        height = flags.getShort("Height");
        length = flags.getShort("Length");
        schemaLen = flags.getLong("SchemaLen");
        lift = flags.getInteger("Lift");
        method = Method.valueOf(flags.getInteger("Method"));
        biome = Biome.valueOf(flags.getInteger("Biome"));
    }

    /* Get structure ground level (lift) to dig it down */
    private int calcLift() {
        int[][] level = new int[width][length];
        int[][] levelMax = new int[width][length];
        boolean dry = method != Method.UNDERWATER;
        for (int index = 0; index < blocks.length; ++index) {
            if (Blocks.isCardinal(blocks[index]) || (dry && Blocks.isLiquid(blocks[index]))) {
                level[getX(index)][getZ(index)] += 1;
                levelMax[getX(index)][getZ(index)] = getY(index) + 1;
            }
        }
        long borders = 0, totals = 0;
        for (int x = 0; x < width; ++x) {
            for (int z = 0; z < length; ++z) {
                totals += level[x][z];
                if (x == 0 || z == 0 || x == width - 1 || z == length - 1) {
                    borders += levelMax[x][z];
                }
            }
        }
        int borderLevel = (int) Math.round(borders / ((width + length) * 2.0));
        int wholeLevel = Math.round(totals / (width * length));
        return  Math.max(borderLevel, wholeLevel);
    }

    /* Generate schematic skin as bitset of possible(0) and restricted(1) to spawn blocks */
    private BitSet getSkin() {

        boolean[] skip = new boolean[256];
        ArrayList<Block> skipBlocks = new ArrayList<Block>(){{
            add(Blocks.AIR);
            if (method == Method.AFLOAT || method == Method.UNDERWATER) {
                add(Blocks.WATER);
                add(Blocks.FLOWING_WATER);
                add(Blocks.LAVA);
                add(Blocks.FLOWING_LAVA);
            }
        }};
        for (Block block : skipBlocks) {
            int blockID = Blocks.blockID(block);
            if (blockID >= 0 && blockID < 256) {
                skip[blockID] = true;
            }
        }
        /*
        * Height Map [SIDE][DIR][SIZE_A][SIZE_B]
        * SIDE = YX, YZ, XZ
        * DIR = MAX, MIN
        */
        int[][][][] heightMap = {
                {new int[height][width], new int[height][width]},
                {new int[height][length], new int[height][length]},
                {new int[width][length], new int[width][length]},
        };
        int iMax[] = {height, height, width};
        int jMax[] = {width, length, length};
        int kStart[][] = {{length - 1, 0}, {width - 1, 0}, {height - 1, 0}};
        int kEnd[][] = {{-1, length}, {-1, width}, {-1, height}};
        int dk[] = {-1, 1};
        for (int side = 0; side < 3; ++side) {
            for (int dir = 0; dir < 2; ++dir) {
                for (int i = 0; i < iMax[side]; ++i) {
                    for (int j = 0; j < jMax[side]; ++j) {
                        int k = kStart[side][dir];
                        for (; k != kEnd[side][dir]; k += dk[dir]) {
                            int index = side == 0 ? getIndex(j, i, k) : (side == 1 ? getIndex(k, i, j) : getIndex(i, k, j));
                            int blockID = blocks[index];
                            if (blockID >= 0 && blockID < 256 && !skip[blockID]) {
                                break;
                            }
                        }
                        heightMap[side][dir][i][j] = k - dk[dir];
                    }
                }
            }
        }

        BitSet skin = new BitSet(width * height * length);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    boolean reachable = heightMap[0][0][y][x] <= z ||
                            heightMap[0][1][y][x] >= z ||
                            heightMap[1][0][y][z] <= x ||
                            heightMap[1][1][y][z] >= x ||
                            heightMap[2][0][x][z] <= y;
                    skin.set(getIndex(x, y, z), reachable);
                }
            }
        }

        for (int index = 0; index < width * height * length; ++index) {
            if (skin.get(index)) {
                int x = getX(index), y = getY(index), z = getZ(index);
                while (y-- > 0) {
                    int next = getIndex(x, y, z);
                    int blockID = blocks[next];
                    if (!skin.get(next) && blockID >= 0 && blockID < 256 && skip[blockID]) {
                        skin.set(next);
                    } else {
                        break;
                    }
                }
            }
        }

        return skin;
    }

}
