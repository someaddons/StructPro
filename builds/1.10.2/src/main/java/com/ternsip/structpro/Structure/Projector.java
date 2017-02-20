package com.ternsip.structpro.Structure;

import com.ternsip.structpro.Logic.Loader;
import com.ternsip.structpro.WorldCache.Mobs;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import com.ternsip.structpro.WorldCache.WorldCache;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
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
import java.util.Collections;
import java.util.Random;

/*
* Projector is wrapper for blueprint for better storing and pasting
* It provides file-cache on structures for minimizing RAM and maximizing performance
*
* */
public class Projector extends Structure {

    /* Directory for dump files */
    private static final File DUMP_DIR = new File("sprodump");

    /* Flags file for blueprint flags */
    private File originFile = null;

    public Projector(File file) throws IOException {
        originFile = file;
        try {
            if (!getDataFile().exists() || !getFlagFile().exists()) {
                throw new IOException("Dump files not exists");
            }
            loadFlags(originFile, getFlagFile());
        } catch (IOException ioe) {
            loadSchematic(file);
        }
    }

    private File getDataFile() {
        return new File(DUMP_DIR.getPath(), originFile.getPath().hashCode() + ".dmp");
    }

    private File getFlagFile() {
        return new File(DUMP_DIR.getPath(), originFile.getPath().hashCode() + ".flg");
    }

    @Override
    protected void loadSchematic(File file) throws IOException {
        super.loadSchematic(file);
        saveFlags(getFlagFile());
        saveData(getDataFile());
        free();
    }

    /* Paste structure into the world using given arguments */
    public Report paste(World world, int posX, int posY, int posZ,
                        int rotateX, int rotateY, int rotateZ,
                        boolean flipX, boolean flipY, boolean flipZ,
                        long seed) {
        long startTime = System.currentTimeMillis();
        Report report = new Report();
        try {
            Posture posture = calibrate(world, posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ, seed);
            project(world, posture, seed);
            report.add("PASTED ", "[X=" + posture.getPosX() + ";Y=" + posture.getPosY() + ";Z=" + posture.getPosZ() + "]");
        } catch (IOException ioe) {
            report.setSuccess(false);
            report.add("NOT PASTED", ioe.getMessage()).add("AT", "[X=" + posX + ";Z=" + posZ + "]");
        }
        long spentTime = (System.currentTimeMillis() - startTime);
        report
                .add("SCHEMATIC", originFile.getName())
                .add("SPENT TIME", new DecimalFormat("###0.00").format(spentTime / 1000.0) + "s")
                .add("WORLD", world.getWorldInfo().getWorldName())
                .add("DIMENSION", String.valueOf(world.provider.getDimension()))
                .add("METHOD", method.name)
                .add("BIOME", biome.name)
                .add("LIFT", String.valueOf(lift))
                .add("SIZE", "[W=" + width + ";H=" + height + ";L=" + length + "]")
                .add("ROTATE", "[X=" + rotateX + ";Y=" + rotateY + ";Z=" + rotateZ + "]")
                .add("FLIP", "[X=" + flipX + ";Y=" + flipY + ";Z=" + flipZ + "]");
        return report;
    }
    /* Project structure to the world */
    private void project(World world, Posture posture, long seed) throws IOException {

        /* Prepare tiles */
        Random random = new Random(seed);

        /* Load whole data */
        loadData(originFile, getDataFile());

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

        if (Loader.spawnMobs) {
            populate(world, posture, seed);
        }

        WorldCache.update();
        free();
    }

    /* Populate structure with entities */
    private void populate(World world, Posture posture, long seed) {
        boolean village = getOriginFile().getPath().toLowerCase().contains("village");
        ArrayList<Class<? extends Entity>> mobs = village ? Mobs.selectVillage() : Mobs.select(biome);
        Random random = new Random(seed);
        int size = width * length;
        int count = 1 + (size >= 256 ? 2 : 0) + (size >= 1024 ? 2 : 0) + (size >= 8192 ? 2 : 0);
        count = village ? count : count * 2;
        int maxTries = 16 + count * count;
        for (int i = 0; i < maxTries && count > 0; ++i) {
            int dx = random.nextInt(width);
            int dy = random.nextInt(height);
            int dz = random.nextInt(length);
            int index = getIndex(dx, dy, dz);
            BlockPos blockPos1 = posture.getWorldPos(dx, dy, dz);
            BlockPos blockPos2 = new BlockPos(blockPos1.getX(), blockPos1.getY() + 1, blockPos1.getZ());
            if (!world.isAirBlock(blockPos1) || !world.isAirBlock(blockPos2) || skin.get(index)) {
                continue;
            }
            Class<? extends Entity> mob = Utils.select(mobs, random.nextLong());
            if (mob != null) {
                WorldCache.spawnEntity(world, mob, blockPos1);
            }
            --count;
        }
    }

    /* Paste structure block in the world */
    private void pasteBlock(World world, int index, Posture posture, Random random) {
        int x = getX(index), y = getY(index), z = getZ(index);
        BlockPos worldPos = posture.getWorldPos(x, y, z);
        if (worldPos.getY() > 255 || worldPos.getY() < 0) {
            return;
        }
        int blockID = blocks[index];
        Block block = null;
        int metaData = 0;
        if (blockID >= 0 && blockID < 256) {
            blockID = Loader.blockReplaces[blockID];
            block = Loader.vanillaBlocks[blockID];
        }
        if (block == null) {
            if (Loader.allowOnlyVanillaBlocks) {
                return;
            }
            block = Block.getBlockById(blockID);
        } else {
            metaData = posture.getWorldMeta(block, meta[index]);
        }
        WorldCache.setBlockState(world, worldPos, WorldCache.blockState(block, metaData));
        processTile(WorldCache.getTileEntity(world, worldPos), index, random);
    }

    /* Process block after spawning */
    private void processBlock(World world, int index, Posture posture, Random random) {
        int x = getX(index), y = getY(index), z = getZ(index);
        if (blocks[index] < 0 || blocks[index] >= 256 || !Loader.cardinalBlocks[blocks[index]] || y < lift) {
            return;
        }
        double radius = Loader.cardinalBlocksRadius;
        int shift = (int) radius;
        for (int dx = -shift; dx <= shift; ++dx) {
            for (int dz = -shift; dz <= shift; ++dz) {
                for (int dy = 0; dy <= shift * 2; ++dy) {
                    if (Math.sqrt(dx * dx + dy * dy / 4.0 + dz * dz) <= radius) {
                        int nx = x + dx, ny = y + dy, nz = z + dz;
                        BlockPos worldPos = posture.getWorldPos(nx, ny, nz);
                        if (worldPos.getY() > 255 || worldPos.getY() < 0) {
                            break;
                        }
                        int blockID = WorldCache.blockID(WorldCache.getBlockState(world, worldPos).getBlock());
                        if (Loader.soil[blockID] || Loader.overlook[blockID]) {
                            if (nx < 0 || nx >= width || ny < 0 || ny >= height || nz < 0 || nz >= length) {
                                WorldCache.setBlockState(world, worldPos, Blocks.AIR.getDefaultState());
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

    /* Process tile entity */
    private void processTile(TileEntity tile, int index, Random random) {
        if (tile == null) {
            return;
        }
        NBTTagCompound tileTag = tiles[index];
        if (tile instanceof TileEntityChest && Loader.lootChance >= random.nextDouble()) {
            ArrayList<ItemStack> forceItems = new ArrayList<ItemStack>();
            if (Loader.nativeLoot && tileTag != null) {
                NBTTagList items = tileTag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < items.tagCount(); ++i) {
                    NBTTagCompound stackTag = items.getCompoundTagAt(i);
                    Item item = WorldCache.ItemByName(stackTag.getString("id"));
                    byte cnt = items.getCompoundTagAt(i).getByte("Count");
                    int dmg = items.getCompoundTagAt(i).getShort("Damage");
                    if (item != null && cnt > 0 && cnt <= WorldCache.itemMaxStack(item) && dmg >= 0 && dmg <= WorldCache.itemMaxMeta(item)) {
                        forceItems.add(new ItemStack(item, cnt, dmg));
                    }
                }
            }
            TileEntityChest chest = (TileEntityChest) tile;
            int minItems = Math.max(0, Math.min(27, Loader.minChestItems));
            int maxItems = Math.max(minItems, Math.min(27, Loader.maxChestItems));
            int itemsCount = minItems + random.nextInt(maxItems - minItems + 1);
            ArrayList<Integer> permutation = new ArrayList<Integer>();
            for (int idx = 0; idx < 27; ++idx) {
                permutation.add(idx);
            }
            Collections.shuffle(forceItems, random);
            Collections.shuffle(permutation, random);
            for (int idx = 0; idx < itemsCount; ++idx) {
                Item item = Loader.items.get(random.nextInt(Loader.items.size()));
                int stackMeta = random.nextInt(Math.abs(WorldCache.itemMaxMeta(item)) + 1);
                if (idx < forceItems.size()) {
                    item = forceItems.get(idx).getItem();
                    stackMeta = forceItems.get(idx).getItemDamage();
                }
                int maxStackSize = Math.max(1, Math.min(Loader.maxChestStackSize, WorldCache.itemMaxStack(item)));
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
            Class<? extends Entity> mob = Utils.select(Mobs.select(biome), random.nextLong());
            if (mob != null) {
                logic.setEntityName(Mobs.classToName(mob));
            }
            if (tileTag != null && tileTag.hasKey("EntityId")) {
                mob = Mobs.selectByName(tileTag.getString("EntityId"));
                if (mob != null) {
                    logic.setEntityName(Mobs.classToName(mob));
                }
            }
        }
    }

    /* Calibrates posture in the world */
    private Posture calibrate(World world,
                              int posX, int posY, int posZ,
                              int rotateX, int rotateY, int rotateZ,
                              boolean flipX, boolean flipY, boolean flipZ,
                              long seed) throws IOException {

        Posture result = new Posture(posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ, width, height, length);

        /* Positive Y-position not calibrates */
        if (posY > 0) {
            return result;
        }

        /* Check biome spawn allowance */
        Biome blockBiome = Biome.valueOf(world.getBiome(new BlockPos(posX, 64, posZ)));
        if (blockBiome != Biome.SNOW && biome == Biome.SNOW) {
            throw new IOException("Can't spawn Snow objects not in Snow biome");
        }
        if (blockBiome != Biome.NETHER && biome == Biome.NETHER) {
            throw new IOException("Can't spawn Nether objects not in Nether");
        }
        if (blockBiome != Biome.END && biome == Biome.END) {
            throw new IOException("Can't spawn The End objects not in The End");
        }
        if (blockBiome == Biome.SNOW && biome == Biome.SAND) {
            throw new IOException("Can't spawn Desert objects in Snow biome");
        }
        if (blockBiome == Biome.NETHER && biome == Biome.SAND) {
            throw new IOException("Can't spawn Desert objects in Nether");
        }
        if (blockBiome == Biome.END && biome == Biome.SAND) {
            throw new IOException("Can't spawn Desert objects in The End");
        }

        /* Max block height in region */
        int maxHeight = 0;

        /* Max block height under liquids */
        int maxHeightUnderLiquid = 0;

        /* Min block height in region */
        int minHeight = 255;

        /* Min block height under liquids */
        int minHeightUnderLiquid = 255;

        double totalHeight = 0;
        double squareHeightSum = 0;
        double totalHeightUnderLiquid = 0;
        double squareHeightSumUnderLiquid = 0;
        double area = width * length;
        for (int wx = result.getPosX(); wx <= result.getEndX(); ++wx) {
            for (int wz = result.getPosZ(); wz <= result.getEndZ(); ++wz) {
                int level = WorldCache.getHeight(world, wx, wz);
                maxHeight = Math.max(maxHeight, level);
                minHeight = Math.min(minHeight, level);
                totalHeight += level;
                squareHeightSum += level * level;
                level = WorldCache.getBottomHeight(world, wx, wz);
                maxHeightUnderLiquid = Math.max(maxHeightUnderLiquid, level);
                minHeightUnderLiquid = Math.min(minHeightUnderLiquid, level);
                totalHeightUnderLiquid += level;
                squareHeightSumUnderLiquid += level * level;
            }
        }

        /* Characterizes roughness */
        double variance = Math.abs((squareHeightSum - (totalHeight * totalHeight) / area) / (area - 1));

        /* Characterizes roughness under liquids */
        double varianceUnderLiquid = Math.abs((squareHeightSumUnderLiquid - (totalHeightUnderLiquid * totalHeightUnderLiquid) / area) / (area - 1));

        /* Average blocks height */
        double averageHeight = totalHeight / area;

        /* Average blocks height under liquids */
        double averageHeightUnderLiquid = totalHeightUnderLiquid / area;

        /* Calibrates structure height. Throw exceptions if it impossible. */
        Random random = new Random(seed);
        double liquidHeight = averageHeight - averageHeightUnderLiquid;
        DecimalFormat decimal = new DecimalFormat("######0.00");
        boolean afloat = method == Projector.Method.AFLOAT;
        boolean underwater = method == Projector.Method.UNDERWATER;
        boolean sky = method == Projector.Method.SKY;
        boolean underground = method == Projector.Method.UNDERGROUND;
        boolean hill = method == Projector.Method.HILL;
        double roughness = Math.sqrt(variance);
        double underLiquidRoughness = Math.sqrt(varianceUnderLiquid);
        double roughnessFactor = Loader.accuracy;
        if (afloat) {
            if (roughness > 3.0 * roughnessFactor) {
                throw new IOException("Rough water: " + decimal.format(roughness));
            }
            if (liquidHeight < 6.0) {
                throw new IOException("Too shallow: " + decimal.format(liquidHeight));
            }
            posY = (int) (averageHeight - roughness);
        } else {
            if (underwater) {
                if (underLiquidRoughness > (height / 3.0 + 2) * roughnessFactor) {
                    throw new IOException("Rough bottom: " + decimal.format(underLiquidRoughness));
                }
                if (liquidHeight < height * 0.35 && liquidHeight + lift < height) {
                    throw new IOException("Too shallow: " + decimal.format(liquidHeight));
                }
            } else if (!sky && !underground && !hill) {
                if (underLiquidRoughness > (height / 8.0 + 2) * roughnessFactor) {
                    throw new IOException("Rough area: " + decimal.format(underLiquidRoughness));
                }
                if (liquidHeight > 1.5) {
                    throw new IOException("Too deep: " + decimal.format(liquidHeight));
                }
            }
            posY = (int) (averageHeightUnderLiquid - underLiquidRoughness);
        }
        if (sky) {
            posY += 8 + height + random.nextInt() % (height / 2);
        } else if (underground) {
            posY = Math.min(posY - height, 30 + random.nextInt() % 25);
        } else {
            posY -= lift;
            posY += Loader.forceLift;
        }
        posY = Math.max(4, Math.min(posY, 250));
        return new Posture(posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ, width, height, length);
    }

    public File getOriginFile() {
        return originFile;
    }


}
