package com.ternsip.structpro.Structure;

import com.ternsip.structpro.Logic.Configurator;
import com.ternsip.structpro.Universe.Blocks.Blocks;
import com.ternsip.structpro.Universe.Blocks.Classifier;
import com.ternsip.structpro.Universe.Universe;
import com.ternsip.structpro.Universe.Entities.Mobs;
import com.ternsip.structpro.Universe.Entities.Tiles;
import com.ternsip.structpro.Utils.BlockPos;
import com.ternsip.structpro.Utils.IBlockState;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

import static com.ternsip.structpro.Universe.Blocks.Classifier.*;

/**
 * Holds schematic-extended information and can load, calibrate, project it
 * @author Ternsip
 * @since JDK 1.6
 */
public class Structure extends Blueprint {

    /** Structure version */
    private static final int VERSION = 110;

    /** Directory for dump files */
    private static final File DUMP_DIR = new File("sprodump", "structures");

    /** Melting distance measured in blocks */
    public static final int MELT = 5;

    /** Structure file */
    private File fileStructure;

    /** Flag file */
    private File fileFlag;

    /** Data file */
    private File fileData;

    /** Structure file size in bytes */
    private long schemaLen;

    /** Spawning method */
    private Method method;

    /** Biome belonging */
    private Biome biome;

    /**
     * Structure foundation level
     * Soil immersing level
     */
    private int lift;

    /**
     * Skin mask over the volume
     * Characterize structure interior
     * 0 - deny to spawn cell, 1 - allow to spawn
     */
    private BitSet skin;

    /**
     * Melting skin has an extended volume than structure
     * 0 - do nothing, 1 - remove block
     */
    private BitSet melt;

    /**
     * Construct structure based on structure file
     * @param file Target file
     * @throws IOException If structure failed to construct
     */
    public Structure(File file) throws IOException {
        this(  file,
                new File(DUMP_DIR.getPath(), file.getPath().hashCode() + ".dmp"),
                new File(DUMP_DIR.getPath(), file.getPath().hashCode() + ".flg"));
    }

    /**
     * Construct structure based on blueprint file, data and flags
     * @param file Target file
     * @param data Data file
     * @param flags Flag file
     * @throws IOException If structure failed to construct
     */
    public Structure(File file, File data, File flags) throws IOException {
        fileStructure = file;
        fileData = data;
        fileFlag = flags;
        try {
            if (!fileData.exists() || !fileFlag.exists()) {
                throw new IOException("Dump files not exists");
            }
            loadFlags();
        } catch (IOException ioe) {
            loadSchematic(fileStructure);
        }
    }

    /**
     * Load schematic from file
     * @param file File to load
     * @throws IOException If schematic failed to load
     */
    @Override
    void loadSchematic(File file) throws IOException {
        super.loadSchematic(file);
        schemaLen = fileStructure.length();
        method = Method.valueOf(fileStructure);
        biome = Biome.valueOf(fileStructure, blocks);
        lift = calcLift();
        skin = getSkin();
        melt = getMelt();
        saveFlags();
        saveData();
        free();
    }

    /**
     * Load flags from flags-file
     * @throws IOException If flags failed to read
     */
    private void loadFlags() throws IOException {
        NBTTagCompound tag = Utils.readTags(fileFlag);
        int version = tag.getInteger("Version");
        if (version != VERSION) {
            throw new IOException("Incomplete version: " + version + " requires " + VERSION);
        }
        width = tag.getShort("Width");
        height = tag.getShort("Height");
        length = tag.getShort("Length");
        schemaLen = tag.getLong("SchemaLen");
        lift = tag.getInteger("Lift");
        method = Method.valueOf(tag.getInteger("Method"));
        biome = Biome.valueOf(tag.getInteger("Biome"));
        if (schemaLen != fileStructure.length()) {
            throw new IOException("Mismatched schematic length: " + fileStructure.length() + "/" + schemaLen);
        }
    }

    /**
     * Load data from data-file and schematic-file
     * @throws IOException If data failed to load
     */
    private void loadData() throws IOException {
        super.loadSchematic(fileStructure);
        NBTTagCompound tag = Utils.readTags(fileData);
        skin = Utils.toBitSet(tag.getByteArray("Skin"));
        melt = Utils.toBitSet(tag.getByteArray("Melt"));
    }

    /**
     * Save flags to file
     * @throws IOException If flags failed to save
     */
    private void saveFlags() throws IOException {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Version", VERSION);
        tag.setShort("Width", (short)width);
        tag.setShort("Height", (short)height);
        tag.setShort("Length", (short)length);
        tag.setLong("SchemaLen", schemaLen);
        tag.setInteger("Lift", lift);
        tag.setInteger("Method", method.value);
        tag.setInteger("Biome", biome.value);
        Utils.writeTags(fileFlag, tag);
    }

    /**
     * Save file to file
     * @throws IOException If data failed to save
     * */
    private void saveData() throws IOException {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByteArray("Skin", Utils.toByteArray(skin));
        tag.setByteArray("Melt", Utils.toByteArray(melt));
        Utils.writeTags(fileData, tag);
    }

    /** Free internal memory */
    private void free() {
        blocks = null;
        meta = null;
        skin = null;
        melt = null;
        tiles = null;
    }

    /**
     * Get structure ground level to dig it down
     * @return Lift level
     */
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

    /**
     * Generate schematic skin as BitSet of possible(0) and restricted(1) to calibrate blocks
     * @return Skin mask
     */
    @SuppressWarnings({"ConstantConditions"})
    private BitSet getSkin() {

        Classifier skips = (method == Method.AFLOAT || method == Method.UNDERWATER) ? SOP : GAS;

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
                            if (!Classifier.isBlock(skips, blocks[index])) {
                                break;
                            }
                        }
                        heightMap[side][dir][i][j] = k - dk[dir];
                    }
                }
            }
        }

        /* Create skin using height-maps */
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

        /* Shed under skin positions */
        for (int index = 0; index < width * height * length; ++index) {
            if (skin.get(index)) {
                int x = getX(index), y = getY(index), z = getZ(index);
                while (y-- > 0) {
                    int next = getIndex(x, y, z);
                    if (!skin.get(next) && Classifier.isBlock(skips, blocks[next])) {
                        skin.set(next);
                    } else {
                        break;
                    }
                }
            }
        }

        return skin;
    }

    /**
     * Returns structure melts, requires calculated ski
     * @return Melt mask
     */
    private BitSet getMelt() {

        /* Create melt using cardinal blocks */
        Volume meltVolume = extend(MELT, MELT, MELT);
        BitSet melt = new BitSet(meltVolume.getSize());

        /* Skip underwater melting*/
        if (method == Method.UNDERWATER || method == Method.AFLOAT) {
            return melt;
        }

        for (int x = 0; x < width; ++x) {
            for (int y = lift; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int idx = getIndex(x, y, z);
                    if (!Classifier.isBlock(CARDINAL, blocks[idx])) {
                        continue;
                    }
                    double radius = 3;
                    int shift = (int) radius;
                    for (int dx = -shift; dx <= shift; ++dx) {
                        for (int dz = -shift; dz <= shift; ++dz) {
                            for (int dy = 0; dy <= shift * 2; ++dy) {
                                if (Math.sqrt(dx * dx + dy * dy / 4.0 + dz * dz) <= radius) {
                                    int nx = x + dx, ny = y + dy, nz = z + dz;
                                    if (!meltVolume.isInside(nx, ny, nz)) {
                                        continue;
                                    }
                                    if (!isInside(nx, ny, nz) || skin.get(getIndex(nx, ny, nz))) {
                                        melt.set(meltVolume.getIndex(nx + MELT, ny + MELT, nz + MELT), true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return melt;
    }

    /**
     * Combine structure report
     * @return Generated report
     */
    public Report report() {
        return new Report()
                .post("SCHEMATIC", getFile().getName())
                .post("METHOD", method.name)
                .post("BIOME", biome.name)
                .post("LIFT", String.valueOf(lift))
                .post(super.report());
    }

    /**
     * Inject structure into specific position in the world
     * It's not a transaction
     * @param world World instance
     * @param posture Transformation state
     * @param seed Projection seed
     * @throws IOException If blueprint failed to project
     */
    @Override
    void project(World world, Posture posture, long seed) throws IOException {

        /* Prepare tiles */
        Random random = new Random(seed);

        /* Load whole data */
        loadData();

        /* Iterate over volume and paste blocks */
        for (int index = 0; index < posture.getSize(); ++index) {
            if (!skin.get(index)) {
                BlockPos worldPos = posture.getWorldPos(index);
                Block block = Blocks.getBlockVanilla(blocks[index]);
                if (block == null) {
                    continue;
                }
                int metaData = posture.getWorldMeta(block, meta[index]);
                Universe.setBlockState(world, worldPos, Blocks.getState(block, metaData));
                Tiles.load(Universe.getTileEntity(world, worldPos), tiles[index], random.nextLong());
            }
        }

        /* Iterate over volume and melt area */
        Posture meltPosture = posture.extend(MELT, MELT, MELT);
        for (int index = 0; index < meltPosture.getSize(); ++index) {
            if (!melt.get(index)) {
                continue;
            }
            BlockPos worldPos = meltPosture.getWorldPos(index);
            IBlockState state = Universe.getBlockState(world, worldPos);
            if (Classifier.isBlock(SOIL, state) || Classifier.isBlock(OVERLOOK, state)) {
                Universe.setBlockState(world, worldPos, Blocks.getState(Blocks.air));
            }
        }

        /* Do liana fix, works only for basic structures */
        if (method == Method.BASIC) {
            for (int x = 0; x < width; ++x) {
                for (int z = 0; z < length; ++z) {
                    int index = getIndex(x, 0, z);
                    if (!skin.get(index)) {
                        BlockPos pos = posture.getWorldPos(x, 0, z);
                        IBlockState state = Universe.getBlockState(world, pos);
                        if (Classifier.isBlock(SOIL, state)) {
                            for (int y = pos.getY() - 1, count = 0; y >= 0 && count < MELT; --y, ++count) {
                                BlockPos nPos = new BlockPos(pos.getX(), y, pos.getZ());
                                if (Classifier.isBlock(HEAT_RAY, Universe.getBlockState(world, nPos))) {
                                    Universe.setBlockState(world, nPos, state);
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        /* Populate generated structure */
        if (Configurator.SPAWN_MOBS) {
            populate(world, posture, seed);
        }

        /* Free structure memory */
        free();

        /* Check light for generated structure */
        Universe.grassFix(world, meltPosture);
        Universe.notifyPosture(world, meltPosture);

    }

    /**
     * Populate structure with entities
     * @param world Target world
     * @param posture Traformation posture
     * @param seed population seed
     */
    private void populate(World world, Posture posture, long seed) {
        boolean village = isHostile();
        ArrayList<Class<? extends Entity>> mobs = village ? Mobs.village.select() : Mobs.hostile.select(biome);
        Random random = new Random(seed);
        int size = width * length;
        int count = 1 + (size >= 256 ? 2 : 0) + (size >= 1024 ? 2 : 0) + (size >= 8192 ? 2 : 0);
        count = village ? count : count * 2;
        int maxTries = 16 + count * count;
        for (int i = 0; i < maxTries && count > 0; ++i) {
            int dx = random.nextInt(width), dy = random.nextInt(height), dz = random.nextInt(length);
            int index = getIndex(dx, dy, dz);
            BlockPos blockPosFloor = posture.getWorldPos(dx, dy, dz);
            BlockPos blockPosTop = new BlockPos(blockPosFloor.getX(), blockPosFloor.getY() + 1, blockPosFloor.getZ());
            if (!world.isAirBlock(blockPosFloor.getX(), blockPosFloor.getY(), blockPosFloor.getZ()) || !world.isAirBlock(blockPosTop.getX(), blockPosTop.getY(), blockPosTop.getZ()) || skin.get(index)) {
                continue;
            }
            if (!mobs.isEmpty()) {
                Class<? extends Entity> mob = Utils.select(mobs, random.nextLong());
                Universe.spawnEntity(world, mob, blockPosFloor);
            }
            --count;
        }
    }

    /**
     * Match biome acceptability
     * @param bio Minecraft native biome to match
     * @throws IOException If biome not acceptable
     */
    public void matchBiome(Biome bio) throws IOException {
        if (bio != Biome.SNOW && biome == Biome.SNOW && method != Method.UNDERGROUND) {
            throw new IOException("Can't calibrate Snow objects not in Snow biome");
        }
        if (bio != Biome.NETHER && biome == Biome.NETHER && method != Method.SKY && method != Method.UNDERGROUND) {
            throw new IOException("Can't calibrate Nether objects not in Nether");
        }
        if (bio == Biome.NETHER && biome != Biome.NETHER && method != Method.UNDERGROUND && method != Method.UNDERWATER) {
            throw new IOException("Can't calibrate not Nether objects in Nether");
        }
        if (bio == Biome.SNOW && biome == Biome.SAND && method != Method.UNDERGROUND) {
            throw new IOException("Can't calibrate Desert objects in Snow biome");
        }
        if (bio == Biome.END && biome != Biome.END && method != Method.SKY) {
            throw new IOException("Can't calibrate not End objects in The End");
        }
    }

    /**
     * Match roughness acceptability
     * @param surface Region characterizes surface
     * @param bottom Region characterizes bottom
     * @throws IOException If regions are not acceptable
     */
    public void matchAccuracy(Region surface, Region bottom) throws IOException {
        DecimalFormat decimal = new DecimalFormat("######0.00");
        double liquidHeight = surface.getAverage() - bottom.getAverage();
        if (method == Method.AFLOAT) {
            if (surface.getRoughness() > (height / 3.0 + 2) * Configurator.ACCURACY) {
                throw new IOException("Rough water: " + decimal.format(surface.getRoughness()));
            }
            if (liquidHeight < 6.0) {
                throw new IOException("Too shallow: " + decimal.format(liquidHeight));
            }
        }
        if (method == Method.UNDERWATER) {
            if (bottom.getRoughness() > (height / 3.0 + 2) * Configurator.ACCURACY) {
                throw new IOException("Rough bottom: " + decimal.format(bottom.getRoughness()));
            }
            if (liquidHeight < height * 0.35 && liquidHeight + lift < height) {
                throw new IOException("Too shallow: " + decimal.format(liquidHeight));
            }
        }
        if (method == Method.BASIC) {
            if (bottom.getRoughness() > (height / 8.0 + 2) * Configurator.ACCURACY) {
                throw new IOException("Rough area: " + decimal.format(bottom.getRoughness()));
            }
            if (liquidHeight > 1.5) {
                throw new IOException("Too deep: " + decimal.format(liquidHeight));
            }
        }
    }

    /**
     * Returns best paste height in region
     * @param surface Region characterizes surface
     * @param bottom Region characterizes bottom
     * @param seed Calibrating seed
     * @return Best height
     */
    public int getBestY(Region surface, Region bottom, long seed) {
        Random random = new Random(seed);
        int posY;
        if (method == Method.AFLOAT) {
            posY = (int) (surface.getAverage() - surface.getRoughness());
        } else {
            posY = (int) (bottom.getAverage() - bottom.getRoughness());
        }
        if (method == Method.SKY) {
            posY += 8 + height + random.nextInt() % (height / 2);
        } else if (method == Method.UNDERGROUND) {
            posY = Math.min(posY - height, 30 + random.nextInt() % 25);
        } else {
            posY -= lift;
            posY += Configurator.FORCE_LIFT;
        }
        return Math.max(4, Math.min(posY, 255));
    }

    /**
     * Check structure hostile for players
     * @return If structure is hostile for players
     */
    private boolean isHostile() {
        return fileStructure.getPath().toLowerCase().contains("village");
    }

    public Method getMethod() {
        return method;
    }

    public Biome getBiome() {
        return biome;
    }

    public int getLift() {
        return lift;
    }

    public File getFile() {
        return fileStructure;
    }
}
