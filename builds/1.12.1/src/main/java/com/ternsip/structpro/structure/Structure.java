package com.ternsip.structpro.structure;

import com.ternsip.structpro.logic.Configurator;
import com.ternsip.structpro.universe.biomes.Biomus;
import com.ternsip.structpro.universe.blocks.Classifier;
import com.ternsip.structpro.universe.blocks.UBlockPos;
import com.ternsip.structpro.universe.blocks.UBlockState;
import com.ternsip.structpro.universe.blocks.UBlocks;
import com.ternsip.structpro.universe.entities.Mobs;
import com.ternsip.structpro.universe.entities.UEntityClass;
import com.ternsip.structpro.universe.utils.Report;
import com.ternsip.structpro.universe.utils.Utils;
import com.ternsip.structpro.universe.world.UWorld;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

import static com.ternsip.structpro.universe.blocks.Classifier.*;

/**
 * Holds schematic-extended information and can load, calibrate, project it
 *
 * @author Ternsip
 */
@SuppressWarnings({"WeakerAccess"})
public class Structure extends Blueprint {

    /**
     * Structure version
     */
    private static final int VERSION = 110;

    /**
     * Directory for dump files
     */
    private static final File DUMP_DIR = new File("sprodump", "structures");

    /**
     * Melting distance measured in blocks
     */
    public static final int MELT = 5;

    /**
     * Structure file
     */
    private File fileStructure;

    /**
     * Flag file
     */
    private File fileFlag;

    /**
     * Data file
     */
    private File fileData;

    /**
     * Structure file size in bytes
     */
    private long schemaLen;

    /**
     * Spawning method
     */
    private Method method;

    /**
     * Biome belonging
     */
    private Biomus biomus;

    /**
     * Structure foundation level
     * Soil immersing level
     */
    private int lift;

    /**
     * Skin mask over the volume
     * Characterize Structure interior
     * 0 - deny to spawn cell, 1 - allow to spawn
     */
    private BitSet skin;

    /**
     * Melting skin has an extended volume than Structure
     * Determines what have to be replaced/erased or not around structure
     * Also useful for light near structure
     * 0 - do nothing, 1 - remove block
     */
    private BitSet melt;

    /**
     * Construct Structure based on Structure file
     *
     * @param file Target file
     * @throws IOException If Structure failed to construct
     */
    public Structure(File file) throws IOException {
        this(file,
                new File(DUMP_DIR.getPath(), file.getPath().hashCode() + ".dmp"),
                new File(DUMP_DIR.getPath(), file.getPath().hashCode() + ".flg"));
    }

    /**
     * Construct Structure based on blueprint file, data and flags
     *
     * @param file  Target file
     * @param data  Data file
     * @param flags Flag file
     * @throws IOException If Structure failed to construct
     */
    public Structure(File file, File data, File flags) throws IOException {
        setFileStructure(file);
        setFileData(data);
        setFileFlag(flags);
        try {
            if (!getFileData().exists() || !getFileFlag().exists()) {
                throw new IOException("Dump files not exists");
            }
            loadFlags();
        } catch (IOException ioe) {
            loadSchematic(getFileStructure());
            setSchemaLen(getFileStructure().length());
            setMethod(Method.valueOf(getFileStructure()));
            setBiomus(Biomus.valueOf(getFileStructure(), getBlocks()));
            setLift(calcLift());
            setSkin(calcSkin());
            setMelt(calcMelt());
            saveFlags();
            saveData();
            free();
        }
    }

    /**
     * Load flags from flags-file
     *
     * @throws IOException If flags failed to read
     */
    private void loadFlags() throws IOException {
        NBTTagCompound tag = Utils.readTags(getFileFlag());
        int version = tag.getInteger("Version");
        if (version != VERSION) {
            throw new IOException("Incomplete version: " + version + " requires " + VERSION);
        }
        setWidth(tag.getShort("Width"));
        setHeight(tag.getShort("Height"));
        setLength(tag.getShort("Length"));
        setSchemaLen(tag.getLong("SchemaLen"));
        setLift(tag.getInteger("Lift"));
        setMethod(Method.valueOf(tag.getInteger("Method")));
        setBiomus(Biomus.valueOf(tag.getInteger("Biome")));
        if (getSchemaLen() != getFileStructure().length()) {
            throw new IOException("Mismatched schematic length: " + getFileStructure().length() + "/" + getSchemaLen());
        }
    }

    /**
     * Load data from data-file and schematic-file
     *
     * @throws IOException If data failed to load
     */
    private void loadData() throws IOException {
        super.loadSchematic(getFileStructure());
        NBTTagCompound tag = Utils.readTags(getFileData());
        setSkin(Utils.toBitSet(tag.getByteArray("Skin")));
        setMelt(Utils.toBitSet(tag.getByteArray("Melt")));
    }

    /**
     * Save flags to file
     *
     * @throws IOException If flags failed to save
     */
    private void saveFlags() throws IOException {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Version", VERSION);
        tag.setShort("Width", (short) getWidth());
        tag.setShort("Height", (short) getHeight());
        tag.setShort("Length", (short) getLength());
        tag.setLong("SchemaLen", getSchemaLen());
        tag.setInteger("Lift", getLift());
        tag.setInteger("Method", getMethod().getValue());
        tag.setInteger("Biome", getBiomus().getValue());
        Utils.writeTags(getFileFlag(), tag);
    }

    /**
     * Save file to file
     *
     * @throws IOException If data failed to save
     */
    private void saveData() throws IOException {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByteArray("Skin", Utils.toByteArray(getSkin()));
        tag.setByteArray("Melt", Utils.toByteArray(getMelt()));
        Utils.writeTags(getFileData(), tag);
    }

    /**
     * Free internal memory
     */
    private void free() {
        setBlocks(null);
        setMetas(null);
        setTiles(null);
        setSkin(null);
        setMelt(null);
    }

    /**
     * Get Structure ground level to dig it down
     *
     * @return Lift level
     */
    private int calcLift() {
        int[][] level = new int[getWidth()][getLength()];
        int[][] levelMax = new int[getWidth()][getLength()];
        boolean dry = getMethod() != Method.UNDERWATER;
        for (int index = 0; index < getBlocks().length; ++index) {
            if (Classifier.isBlock(SOIL, getBlock(index)) || (dry && Classifier.isBlock(LIQUID, getBlock(index)))) {
                level[getX(index)][getZ(index)] += 1;
                levelMax[getX(index)][getZ(index)] = getY(index) + 1;
            }
        }
        long borders = 0, totals = 0;
        for (int x = 0; x < getWidth(); ++x) {
            for (int z = 0; z < getLength(); ++z) {
                totals += level[x][z];
                if (x == 0 || z == 0 || x == getWidth() - 1 || z == getLength() - 1) {
                    borders += levelMax[x][z];
                }
            }
        }
        int borderLevel = (int) Math.round(borders / ((getWidth() + getLength()) * 2.0));
        int wholeLevel = Math.round(totals / (getWidth() * getLength()));
        return Math.max(borderLevel, wholeLevel);
    }

    /**
     * Generate schematic skin as BitSet of possible(0) and restricted(1) to calibrate blocks
     *
     * @return Skin mask
     */
    private BitSet calcSkin() {

        Classifier skips = (getMethod() == Method.AFLOAT || getMethod() == Method.UNDERWATER) ? SOP : GAS;

        /* Height Map [SIDE][DIR][SIZE_A][SIZE_B], SIDE = YX, YZ, XZ, DIR = MAX, MIN */
        int[][][][] heightMap = {
                {new int[getHeight()][getWidth()], new int[getHeight()][getWidth()]},
                {new int[getHeight()][getLength()], new int[getHeight()][getLength()]},
                {new int[getWidth()][getLength()], new int[getWidth()][getLength()]},
        };
        int iMax[] = {getHeight(), getHeight(), getWidth()};
        int jMax[] = {getWidth(), getLength(), getLength()};
        int kStart[][] = {{getLength() - 1, 0}, {getWidth() - 1, 0}, {getHeight() - 1, 0}};
        int kEnd[][] = {{-1, getLength()}, {-1, getWidth()}, {-1, getHeight()}};
        int dk[] = {-1, 1};
        for (int side = 0; side < 3; ++side) {
            for (int dir = 0; dir < 2; ++dir) {
                for (int i = 0; i < iMax[side]; ++i) {
                    for (int j = 0; j < jMax[side]; ++j) {
                        int k = kStart[side][dir];
                        for (; k != kEnd[side][dir]; k += dk[dir]) {
                            int index = side == 0 ? getIndex(j, i, k) : (side == 1 ? getIndex(k, i, j) : getIndex(i, k, j));
                            if (!Classifier.isBlock(skips, getBlock(index))) {
                                break;
                            }
                        }
                        heightMap[side][dir][i][j] = k - dk[dir];
                    }
                }
            }
        }

        /* Create skin using height-maps */
        BitSet skin = new BitSet(getWidth() * getHeight() * getLength());
        for (int x = 0; x < getWidth(); ++x) {
            for (int y = 0; y < getHeight(); ++y) {
                for (int z = 0; z < getLength(); ++z) {
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
        for (int index = 0; index < getWidth() * getHeight() * getLength(); ++index) {
            if (skin.get(index)) {
                int x = getX(index), y = getY(index), z = getZ(index);
                while (y-- > 0) {
                    int next = getIndex(x, y, z);
                    if (!skin.get(next) && Classifier.isBlock(skips, getBlock(next))) {
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
     * Returns Structure melts
     * Requires calculated sky
     *
     * @return Melt mask
     */
    private BitSet calcMelt() {

        /* Create melt using cardinal blocks */
        Volume meltVolume = extend(MELT, MELT, MELT);
        BitSet melt = new BitSet(meltVolume.getSize());

        /* Skip underwater melting*/
        if (getMethod() == Method.UNDERWATER || getMethod() == Method.AFLOAT) {
            return melt;
        }

        for (int x = 0; x < getWidth(); ++x) {
            for (int y = lift; y < getHeight(); ++y) {
                for (int z = 0; z < getLength(); ++z) {
                    int idx = getIndex(x, y, z);
                    if (!Classifier.isBlock(CARDINAL, getBlock(idx))) {
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
                                    if (!isInside(nx, ny, nz) || getSkin().get(getIndex(nx, ny, nz))) {
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
     * Combine Structure report
     *
     * @return Generated report
     */
    @Override
    public Report report() {
        return new Report()
                .post("SCHEMATIC", getFile().getName())
                .post("METHOD", getMethod().getName())
                .post("BIOME", getBiomus().getName())
                .post("LIFT", String.valueOf(getLift()))
                .post(super.report());
    }

    @Override
    public void project(UWorld world, Posture posture, long seed, boolean isInsecure) throws IOException {

        /* Prepare tiles */
        Random random = new Random(seed);

        /* Load whole data */
        loadData();

        /* Iterate over volume and paste blocks */
        for (int index = 0; index < posture.getSize(); ++index) {
            UBlockPos worldPos = posture.getWorldPos(index);
            UBlockState state = world.getBlockState(worldPos);
            if (!getSkin().get(index) && !Classifier.isBlock(PROTECTED, state)) {
                project(world, posture, index, isInsecure, random);
            }
        }

        /* Iterate over volume and melt area */
        Posture meltPosture = posture.extend(MELT, MELT, MELT);
        for (int index = 0; index < meltPosture.getSize(); ++index) {
            if (!melt.get(index)) {
                continue;
            }
            UBlockPos worldPos = meltPosture.getWorldPos(index);
            UBlockState state = world.getBlockState(worldPos);
            if ((Classifier.isBlock(SOIL, state) || Classifier.isBlock(OVERLOOK, state)) && !Classifier.isBlock(PROTECTED, state)) {
                world.setBlockState(worldPos, UBlocks.AIR.getState());
            }
        }

        /* Do liana fix, works only for basic structures */
        if (getMethod() == Method.BASIC) {
            for (int x = 0; x < getWidth(); ++x) {
                for (int z = 0; z < getLength(); ++z) {
                    int index = getIndex(x, 0, z);
                    if (!getSkin().get(index)) {
                        UBlockPos pos = posture.getWorldPos(x, 0, z);
                        UBlockState state = world.getBlockState(pos);
                        if (Classifier.isBlock(SOIL, state)) {
                            for (int y = pos.getY() - 1, count = 0; y >= 0 && count < MELT; --y, ++count) {
                                UBlockPos nPos = new UBlockPos(pos.getX(), y, pos.getZ());
                                if (Classifier.isBlock(HEAT_RAY, world.getBlockState(nPos))) {
                                    world.setBlockState(nPos, state);
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        /* Populate generated Structure */
        if (Configurator.SPAWN_MOBS) {
            populate(world, posture, seed);
        }

        /* Free Structure memory */
        free();

        /* Check light for generated Structure */
        world.grassFix(meltPosture);
        world.notifyPosture(meltPosture);

    }

    /**
     * Populate Structure with entities
     *
     * @param world   Target world
     * @param posture Traformation posture
     * @param seed    population seed
     */
    private void populate(UWorld world, Posture posture, long seed) {
        boolean village = isHostile();
        ArrayList<UEntityClass> mobs = village ? Mobs.village.select() : Mobs.hostile.select(biomus);
        Random random = new Random(seed);
        int size = getWidth() * getLength();
        int count = 1 + (size >= 256 ? 2 : 0) + (size >= 1024 ? 2 : 0) + (size >= 8192 ? 2 : 0);
        count = village ? count : count * 2;
        int maxTries = 16 + count * count;
        for (int i = 0; i < maxTries && count > 0; ++i) {
            int dx = random.nextInt(getWidth()), dy = random.nextInt(getHeight()), dz = random.nextInt(getLength());
            int index = getIndex(dx, dy, dz);
            UBlockPos blockPosFloor = posture.getWorldPos(dx, dy, dz);
            UBlockPos blockPosTop = new UBlockPos(blockPosFloor.getX(), blockPosFloor.getY() + 1, blockPosFloor.getZ());
            if (!world.isAirBlock(blockPosFloor) || !world.isAirBlock(blockPosTop) || getSkin().get(index)) {
                continue;
            }
            if (!mobs.isEmpty()) {
                UEntityClass mob = Utils.select(mobs, random.nextLong());
                world.spawnEntity(mob, blockPosFloor);
            }
            --count;
        }
    }

    /**
     * Match biome acceptability
     *
     * @param biomus Biome to match
     * @throws IOException If biome not acceptable
     */
    public void matchBiome(Biomus biomus) throws IOException {
        if (biomus != Biomus.SNOW && getBiomus() == Biomus.SNOW && getMethod() != Method.UNDERGROUND) {
            throw new IOException("Can't calibrate Snow objects not in Snow biome");
        }
        if (biomus != Biomus.NETHER && getBiomus() == Biomus.NETHER && getMethod() != Method.SKY && getMethod() != Method.UNDERGROUND) {
            throw new IOException("Can't calibrate Nether objects not in Nether");
        }
        if (biomus == Biomus.NETHER && getBiomus() != Biomus.NETHER && getMethod() != Method.UNDERGROUND && getMethod() != Method.UNDERWATER) {
            throw new IOException("Can't calibrate not Nether objects in Nether");
        }
        if (biomus == Biomus.SNOW && getBiomus() == Biomus.SAND && getMethod() != Method.UNDERGROUND) {
            throw new IOException("Can't calibrate Desert objects in Snow biome");
        }
        if (biomus == Biomus.END && getBiomus() != Biomus.END && getMethod() != Method.SKY) {
            throw new IOException("Can't calibrate not End objects in The End");
        }
    }

    /**
     * Match roughness acceptability
     *
     * @param surface Region characterizes surface
     * @param bottom  Region characterizes bottom
     * @throws IOException If regions are not acceptable
     */
    public void matchAccuracy(Region surface, Region bottom) throws IOException {
        DecimalFormat decimal = new DecimalFormat("######0.00");
        double liquidHeight = surface.getAverage() - bottom.getAverage();
        if (getMethod() == Method.AFLOAT) {
            if (surface.getRoughness() > (getHeight() / 3.0 + 2) * Configurator.ACCURACY) {
                throw new IOException("Rough water: " + decimal.format(surface.getRoughness()));
            }
            if (liquidHeight < 6.0) {
                throw new IOException("Too shallow: " + decimal.format(liquidHeight));
            }
        }
        if (getMethod() == Method.UNDERWATER) {
            if (bottom.getRoughness() > (getHeight() / 3.0 + 2) * Configurator.ACCURACY) {
                throw new IOException("Rough bottom: " + decimal.format(bottom.getRoughness()));
            }
            if (liquidHeight < getHeight() * 0.35 && liquidHeight + lift < getHeight()) {
                throw new IOException("Too shallow: " + decimal.format(liquidHeight));
            }
        }
        if (getMethod() == Method.BASIC) {
            if (bottom.getRoughness() > (getHeight() / 8.0 + 2) * Configurator.ACCURACY) {
                throw new IOException("Rough area: " + decimal.format(bottom.getRoughness()));
            }
            if (liquidHeight > 1.5) {
                throw new IOException("Too deep: " + decimal.format(liquidHeight));
            }
        }
    }

    /**
     * Returns best paste height in region
     *
     * @param surface Region characterizes surface
     * @param bottom  Region characterizes bottom
     * @param seed    Calibrating seed
     * @return Best height
     */
    public int getBestY(Region surface, Region bottom, long seed) {
        Random random = new Random(seed);
        int posY;
        if (getMethod() == Method.AFLOAT) {
            posY = (int) (surface.getAverage() - surface.getRoughness());
        } else {
            posY = (int) (bottom.getAverage() - bottom.getRoughness());
        }
        if (getMethod() == Method.SKY) {
            posY += 8 + getHeight() + random.nextInt() % (getHeight() / 2);
        } else if (getMethod() == Method.UNDERGROUND) {
            posY = Math.min(posY - getHeight(), 30 + random.nextInt() % 25);
        } else {
            posY -= getLift();
            posY += Configurator.FORCE_LIFT;
        }
        return Math.max(4, Math.min(posY, 255));
    }

    /**
     * Check Structure hostile for players
     *
     * @return If Structure is hostile for players
     */
    private boolean isHostile() {
        return getFileStructure().getPath().toLowerCase().contains("village");
    }

    public Method getMethod() {
        return method;
    }

    public Biomus getBiomus() {
        return biomus;
    }

    public int getLift() {
        return lift;
    }

    public File getFile() {
        return fileStructure;
    }

    private File getFileStructure() {
        return fileStructure;
    }

    private File getFileFlag() {
        return fileFlag;
    }

    private File getFileData() {
        return fileData;
    }

    private long getSchemaLen() {
        return schemaLen;
    }

    private void setFileStructure(File fileStructure) {
        this.fileStructure = fileStructure;
    }

    private void setFileFlag(File fileFlag) {
        this.fileFlag = fileFlag;
    }

    private void setFileData(File fileData) {
        this.fileData = fileData;
    }

    private void setSchemaLen(long schemaLen) {
        this.schemaLen = schemaLen;
    }

    private void setMethod(Method method) {
        this.method = method;
    }

    private void setBiomus(Biomus biomus) {
        this.biomus = biomus;
    }

    private BitSet getSkin() {
        return skin;
    }

    private BitSet getMelt() {
        return melt;
    }

    private void setLift(int lift) {
        this.lift = lift;
    }

    private void setSkin(BitSet skin) {
        this.skin = skin;
    }

    private void setMelt(BitSet melt) {
        this.melt = melt;
    }

}
