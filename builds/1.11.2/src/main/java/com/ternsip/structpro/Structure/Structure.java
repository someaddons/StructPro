package com.ternsip.structpro.Structure;

import com.ternsip.structpro.Utils.Utils;
import com.ternsip.structpro.World.Blocks.Blocks;
import com.ternsip.structpro.World.Blocks.Classifier;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

import static com.ternsip.structpro.World.Blocks.Classifier.LIQUID;
import static com.ternsip.structpro.World.Blocks.Classifier.SOIL;

/* Holds schematic-extended information and can load/spawn/calibrate it */
public class Structure extends Blueprint {

    /* Structure version */
    private static final int VERSION = 105;

    public Method getMethod() {
        return method;
    }

    public Biome getBiome() {
        return biome;
    }

    public int getLift() {
        return lift;
    }

    private long schemaLen;
    Method method;
    Biome biome;
    int lift;
    BitSet skin;

    @Override
    void loadSchematic(File file) throws IOException {
        super.loadSchematic(file);
        schemaLen = file.length();
        method = Method.valueOf(file);
        biome = Biome.valueOf(file, blocks);
        lift = calcLift();
        skin = getSkin();
    }

    /* Load flags from flags-file and match it with schematic-file */
    void loadFlags(File schematic, File flags) throws IOException {
        readFlags(readTags(flags));
        if (schemaLen != schematic.length()) {
            throw new IOException("Mismatched schematic length: " + schematic.length() + "/" + schemaLen);
        }
    }

    /* Load data from data-file and match it with schematic-file */
    void loadData(File schematic, File data) throws IOException {
        super.loadSchematic(schematic);
        readData(readTags(data));
    }

    /* Save flags to file */
    void saveFlags(File file) throws IOException {
        writeTags(file, getFlags());
    }

    /* Save data to file */
    void saveData(File file) throws IOException {
        writeTags(file, getData());
    }

    /* Free memory */
    void free() {
        blocks = null;
        meta = null;
        skin = null;
        tiles = null;
    }

    /* Get data as NBT tag */
    private NBTTagCompound getData() {
        NBTTagCompound result = new NBTTagCompound();
        result.setByteArray("Skin", Utils.toByteArray(skin));
        return result;
    }

    /* Get flags as NBT tag */
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

    /* Read data from NBT tag */
    private void readData(NBTTagCompound data) throws IOException {
        skin = Utils.toBitSet(data.getByteArray("Skin"));
    }

    /* read flags from NBT tag */
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
            if (Classifier.isBlock(SOIL, blocks[index]) || (dry && Classifier.isBlock(LIQUID, blocks[index]))) {
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
        /* Height Map [SIDE][DIR][SIZE_A][SIZE_B], SIDE = YX, YZ, XZ, DIR = MAX, MIN */
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
