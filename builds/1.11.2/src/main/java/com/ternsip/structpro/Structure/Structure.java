package com.ternsip.structpro.Structure;

import com.ternsip.structpro.Logic.Configurator;
import com.ternsip.structpro.Universe.Cache.Universe;
import com.ternsip.structpro.Universe.Entities.Mobs;
import com.ternsip.structpro.Universe.Items.Items;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import com.ternsip.structpro.Universe.Blocks.Blocks;
import com.ternsip.structpro.Universe.Blocks.Classifier;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Pattern;

import static com.ternsip.structpro.Universe.Blocks.Classifier.*;

/* Holds schematic-extended information and can load/spawn/calibrate it */
public class Structure extends Blueprint {

    /* Structure version */
    private static final int VERSION = 105;


    /* Directory for dump files */
    private static final File DUMP_DIR = new File("sprodump", "structures");

    private File fileStructure;
    private File fileFlag;
    private File fileData;
    private long schemaLen;
    private Method method;
    private Biome biome;
    private int lift;
    private BitSet skin;

    /* Construct structure based on structure file */
    public Structure(File file) throws IOException {
        this(  file,
                new File(DUMP_DIR.getPath(), file.getPath().hashCode() + ".dmp"),
                new File(DUMP_DIR.getPath(), file.getPath().hashCode() + ".flg"));
    }

    /* Construct structure based on blueprint file, data and flags */
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

    @Override
    void loadSchematic(File file) throws IOException {
        super.loadSchematic(file);
        schemaLen = fileStructure.length();
        method = Method.valueOf(fileStructure);
        biome = Biome.valueOf(fileStructure, blocks);
        lift = calcLift();
        skin = getSkin();
        saveFlags();
        saveData();
        free();
    }

    /* Load flags from flags-file */
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

    /* Load data from data-file and schematic-file */
    private void loadData() throws IOException {
        super.loadSchematic(fileStructure);
        NBTTagCompound tag = Utils.readTags(fileData);
        skin = Utils.toBitSet(tag.getByteArray("Skin"));
    }

    /* Save flags to file */
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

    /* Save file to file */
    private void saveData() throws IOException {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByteArray("Skin", Utils.toByteArray(skin));
        Utils.writeTags(fileData, tag);
    }

    /* Free memory */
    private void free() {
        blocks = null;
        meta = null;
        skin = null;
        tiles = null;
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

    /* Generate schematic skin as BitSet of possible(0) and restricted(1) to spawn blocks */
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

    /* Generate structure report */
    public Report report() {
        return new Report()
                .post("SCHEMATIC", getFile().getName())
                .post("METHOD", method.name)
                .post("BIOME", biome.name)
                .post("LIFT", String.valueOf(lift))
                .post("SIZE", "[W=" + width + ";H=" + height + ";L=" + length + "]");
    }

    /* Project structure to the world */
    void project(World world, Posture posture, boolean liana, long seed) throws IOException {

        /* Prepare tiles */
        Random random = new Random(seed);

        /* Load whole data */
        loadData();

        liana = liana && method == Method.BASIC;

        /* Iterate over volume and manage blocks */
        for (int index = 0; index < width * height * length; ++index) {
            if (!skin.get(index)) {
                pasteBlock(world, index, posture, random);
            }
        }

        /* Iterate over volume and process pasted blocks */
        for (int index = 0; index < width * height * length; ++index) {
            if (!skin.get(index)) {
                processBlock(world, index, posture, random);
            }
        }

        /* Do liana fix*/
        if (liana && method == Method.BASIC) {
            for (int x = 0; x < width; ++x) {
                for (int z = 0; z < length; ++z) {
                    int index = getIndex(x, 0, z);
                    if (!skin.get(index)) {
                        lianaFix(world, posture.getWorldPos(x, 0, z));
                    }
                }
            }
        }

        /* Populate generated structure */
        if (Configurator.SPAWN_MOBS) {
            populate(world, posture, seed);
        }

        Universe.update();
        free();
    }

    /* Populate structure with entities */
    private void populate(World world, Posture posture, long seed) {
        boolean village = isVillage();
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
            if (!world.isAirBlock(blockPosFloor) || !world.isAirBlock(blockPosTop) || skin.get(index)) {
                continue;
            }
            if (!mobs.isEmpty()) {
                Class<? extends Entity> mob = Utils.select(mobs, random.nextLong());
                Universe.spawnEntity(world, mob, blockPosFloor);
            }
            --count;
        }
    }

    /* Paste structure block in the world */
    private void pasteBlock(World world, int index, Posture posture, Random random) {
        int x = getX(index), y = getY(index), z = getZ(index);
        BlockPos worldPos = posture.getWorldPos(x, y, z);
        Block block = Blocks.idToBlock(blocks[index]);
        if (block == null) {
            return;
        }
        int metaData = posture.getWorldMeta(block, meta[index]);
        Universe.setBlockState(world, worldPos, Blocks.state(block, metaData));
        processTile(Universe.getTileEntity(world, worldPos), index, random);
    }

    /* Process block after spawning */
    private void processBlock(World world, int index, Posture posture, Random random) {
        int x = getX(index), y = getY(index), z = getZ(index);
        if (!Blocks.isVanillaID(blocks[index]) || !Classifier.isBlock(CARDINAL, blocks[index]) || y < lift) {
            return;
        }
        /* Clear soil and overlook around cardinal blocks */
        double radius = 3;
        int shift = (int) radius;
        for (int dx = -shift; dx <= shift; ++dx) {
            for (int dz = -shift; dz <= shift; ++dz) {
                for (int dy = 0; dy <= shift * 2; ++dy) {
                    if (Math.sqrt(dx * dx + dy * dy / 4.0 + dz * dz) <= radius) {
                        int nx = x + dx, ny = y + dy, nz = z + dz;
                        BlockPos worldPos = posture.getWorldPos(nx, ny, nz);
                        int blockID = Blocks.blockID(Universe.getBlockState(world, worldPos));
                        if (Classifier.isBlock(SOIL, blockID) || Classifier.isBlock(OVERLOOK, blockID)) {
                            if (nx < 0 || nx >= width || ny < 0 || ny >= height || nz < 0 || nz >= length) {
                                Universe.setBlockState(world, worldPos, Blocks.state(Blocks.AIR));
                            } else {
                                int nIndex = getIndex(nx, ny, nz);
                                if (skin.get(nIndex)) {
                                    pasteBlock(world, nIndex, posture, random);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /* Do block-liana fix for specific position */
    private void lianaFix(World world, BlockPos pos) {
        IBlockState state = Universe.getBlockState(world, pos);
        if (Classifier.isBlock(SOIL, state)) {
            for (int y = pos.getY() - 1; y >= 0; --y) {
                BlockPos nPos = new BlockPos(pos.getX(), y, pos.getZ());
                if (Classifier.isBlock(OVERLOOK, Universe.getBlockState(world, nPos))) {
                    Universe.setBlockState(world, nPos, state);
                } else {
                    break;
                }
            }
        }
    }

    /* Process tile entity */
    private void processTile(TileEntity tile, int index, Random random) {
        if (tile == null) {
            return;
        }
        NBTTagCompound tileTag = tiles[index];
        if (tile instanceof TileEntityChest && Configurator.LOOT_CHANCE >= random.nextDouble()) {
            ArrayList<ItemStack> forceItems = new ArrayList<ItemStack>();
            if (Configurator.NATIVE_LOOT && tileTag != null) {
                NBTTagList items = tileTag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < items.tagCount(); ++i) {
                    NBTTagCompound stackTag = items.getCompoundTagAt(i);
                    Item item = Items.ItemByName(stackTag.getString("id"));
                    byte cnt = items.getCompoundTagAt(i).getByte("Count");
                    int dmg = items.getCompoundTagAt(i).getShort("Damage");
                    if (item != null && cnt > 0 && cnt <= Items.itemMaxStack(item) && dmg >= 0 && dmg <= Items.itemMaxMeta(item)) {
                        forceItems.add(new ItemStack(item, cnt, dmg));
                    }
                }
            }
            TileEntityChest chest = (TileEntityChest) tile;
            int minItems = Math.max(0, Math.min(27, Configurator.MIN_CHEST_ITEMS));
            int maxItems = Math.max(minItems, Math.min(27, Configurator.MAX_CHEST_ITEMS));
            int itemsCount = minItems + random.nextInt(maxItems - minItems + 1);
            ArrayList<Integer> permutation = new ArrayList<Integer>();
            for (int idx = 0; idx < 27; ++idx) {
                permutation.add(idx);
            }
            Collections.shuffle(forceItems, random);
            Collections.shuffle(permutation, random);
            for (int idx = 0; idx < itemsCount; ++idx) {
                Item item = Utils.select(Items.select(), random.nextLong());
                if (item == null) {
                    continue;
                }
                int stackMeta = random.nextInt(Math.abs(Items.itemMaxMeta(item)) + 1);
                if (idx < forceItems.size()) {
                    item = forceItems.get(idx).getItem();
                    stackMeta = forceItems.get(idx).getItemDamage();
                }
                int maxStackSize = Math.max(1, Math.min(Configurator.MAX_CHEST_STACK_SIZE, Items.itemMaxStack(item)));
                int stackSize = 1 + random.nextInt(maxStackSize);
                chest.setInventorySlotContents(permutation.get(idx), new ItemStack(item, stackSize, stackMeta));
            }
            return;
        }
        if (tile instanceof TileEntitySign && tileTag != null) {
            TileEntitySign sign = (TileEntitySign) tile;
            for (int i = 0; i < 4; ++i) {
                try {
                    ITextComponent tc = ITextComponent.Serializer.fromJsonLenient(tileTag.getString("Text" + (i + 1)));
                    sign.signText[i] = new TextComponentString(tc.getUnformattedComponentText());
                } catch (Throwable ignored){}
            }
            return;
        }
        if (tile instanceof TileEntityDispenser && tileTag != null) {
            TileEntityDispenser dispenser = (TileEntityDispenser) tile;
            NBTTagList items = tileTag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < items.tagCount() && i < 9; ++i) {
                NBTTagCompound stackTag = items.getCompoundTagAt(i);
                Item item = Item.REGISTRY.getObject(new ResourceLocation(stackTag.getString("id")));
                byte count = items.getCompoundTagAt(i).getByte("Count");
                int damage = items.getCompoundTagAt(i).getShort("Damage");
                if (item != null && count > 0) {
                    dispenser.setInventorySlotContents(i, new ItemStack(item, count, damage));
                }
            }
            return;
        }
        if (tile instanceof TileEntityCommandBlock && tileTag != null) {
            TileEntityCommandBlock commandBlock = (TileEntityCommandBlock) tile;
            CommandBlockBaseLogic logic = commandBlock.getCommandBlockLogic();
            logic.setCommand(tileTag.getString("Command"));
            if (tileTag.hasKey("CustomName")) {
                logic.setName(tileTag.getString("CustomName"));
            }
            if (tileTag.hasKey("TrackOutput")) {
                logic.setTrackOutput(tileTag.getBoolean("TrackOutput"));
            }
            return;
        }
        if (tile instanceof TileEntityMobSpawner) {
            TileEntityMobSpawner spawner = (TileEntityMobSpawner) tile;
            MobSpawnerBaseLogic logic = spawner.getSpawnerBaseLogic();
            Class<? extends Entity> mob = Utils.select(Mobs.hostile.select(biome), random.nextLong());
            if (mob != null) {
                logic.setEntityId(Mobs.classToName(mob));
            }
            if (tileTag != null && tileTag.hasKey("EntityId")) {
                String mobName = Pattern.quote(tileTag.getString("EntityId"));
                Pattern mPattern = Pattern.compile(".*" + Pattern.quote(mobName) + ".*", Pattern.CASE_INSENSITIVE);
                mob = Utils.select(Configurator.MOB_SPAWNERS_EGGS_ONLY ? Mobs.eggs.select(mPattern) : Mobs.mobs.select(mPattern));
                if (mob != null) {
                    logic.setEntityId(Mobs.classToName(mob));
                }
            }
        }
    }

    /* Match biome acceptability */
    public void matchBiome(Biome bio) throws IOException {
        if (bio != Biome.SNOW && biome == Biome.SNOW) {
            throw new IOException("Can't spawn Snow objects not in Snow biome");
        }
        if (bio != Biome.NETHER && biome == Biome.NETHER) {
            throw new IOException("Can't spawn Nether objects not in Nether");
        }
        if (bio != Biome.END && biome == Biome.END) {
            throw new IOException("Can't spawn The End objects not in The End");
        }
        if (bio == Biome.SNOW && biome == Biome.SAND) {
            throw new IOException("Can't spawn Desert objects in Snow biome");
        }
        if (bio == Biome.NETHER && biome == Biome.SAND) {
            throw new IOException("Can't spawn Desert objects in Nether");
        }
        if (bio == Biome.END && biome == Biome.SAND) {
            throw new IOException("Can't spawn Desert objects in The End");
        }
    }

    /* Match roughness acceptability */
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

    /* Calibrates posture in the world */
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
        return Math.max(0, Math.min(posY, 255));
    }

    /* Check if the structure is village structure */
    private boolean isVillage() {
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
