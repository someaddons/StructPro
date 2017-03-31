package com.ternsip.structpro.Structure;

import com.ternsip.structpro.World.Blocks.Blocks;
import com.ternsip.structpro.World.Blocks.Classifier;
import com.ternsip.structpro.World.Items.Items;
import com.ternsip.structpro.Logic.Configurator;
import com.ternsip.structpro.World.Entities.Mobs;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import com.ternsip.structpro.World.Cache.WorldCache;
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
import java.util.Collections;
import java.util.Random;
import java.util.regex.Pattern;

import static com.ternsip.structpro.World.Blocks.Classifier.CARDINAL;
import static com.ternsip.structpro.World.Blocks.Classifier.OVERLOOK;
import static com.ternsip.structpro.World.Blocks.Classifier.SOIL;

/*
* Projector is wrapper for structure for better storing and pasting
* It provides file-cache on structures for minimizing RAM and maximizing performance
*/
public class Projector extends Structure {

    /* Directory for dump files */
    private static final File DUMP_DIR = new File("sprodump");

    /* Construct projector based on structure file */
    public Projector(File file) throws IOException {
        super(  file,
                new File(DUMP_DIR.getPath(), file.getPath().hashCode() + ".dmp"),
                new File(DUMP_DIR.getPath(), file.getPath().hashCode() + ".flg"));
    }

    /* Paste structure into the world using given arguments */
    public Report paste(World world, int posX, int posY, int posZ,
                        int rotateX, int rotateY, int rotateZ,
                        boolean flipX, boolean flipY, boolean flipZ,
                        long seed) {
        long startTime = System.currentTimeMillis();
        Report report = new Report();
        try {
            boolean freely = posY > 0;
            Posture posture = calibrate(world, posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ, freely, seed);
            project(world, posture, freely, seed);
            report.add("PASTED ", "[X=" + posture.getPosX() + ";Y=" + posture.getPosY() + ";Z=" + posture.getPosZ() + "]");
        } catch (IOException ioe) {
            report.setSuccess(false);
            report.add("NOT PASTED", ioe.getMessage()).add("AT", "[X=" + posX + ";Z=" + posZ + "]");
        }
        long spentTime = (System.currentTimeMillis() - startTime);
        report
                .add("SCHEMATIC", getFile().getName())
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
    private void project(World world, Posture posture, boolean freely, long seed) throws IOException {

        /* Prepare tiles */
        Random random = new Random(seed);

        /* Load whole data */
        loadData();

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
        if (!freely && method == Method.BASIC) {
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

        WorldCache.update();
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
            Class<? extends Entity> mob = Utils.select(mobs, random.nextLong());
            if (mob != null) {
                WorldCache.spawnEntity(world, mob, blockPosFloor);
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
        WorldCache.setBlockState(world, worldPos, Blocks.state(block, metaData));
        processTile(WorldCache.getTileEntity(world, worldPos), index, random);
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
                        int blockID = Blocks.blockID(WorldCache.getBlockState(world, worldPos));
                        if (Classifier.isBlock(SOIL, blockID) || Classifier.isBlock(OVERLOOK, blockID)) {
                            if (nx < 0 || nx >= width || ny < 0 || ny >= height || nz < 0 || nz >= length) {
                                WorldCache.setBlockState(world, worldPos, Blocks.state(Blocks.AIR));
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
        IBlockState state = WorldCache.getBlockState(world, pos);
        if (Classifier.isBlock(SOIL, state)) {
            for (int y = pos.getY() - 1; y >= 0; --y) {
                BlockPos nPos = new BlockPos(pos.getX(), y, pos.getZ());
                if (Classifier.isBlock(OVERLOOK, WorldCache.getBlockState(world, nPos))) {
                    WorldCache.setBlockState(world, nPos, state);
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

    /* Calibrates posture in the world */
    private Posture calibrate(World world,
                              int posX, int posY, int posZ,
                              int rotateX, int rotateY, int rotateZ,
                              boolean flipX, boolean flipY, boolean flipZ,
                              boolean freely, long seed) throws IOException {

        Posture result = new Posture(posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ, width, height, length);

        /* Positive Y-position not calibrates */
        if (freely) {
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
                int level = WorldCache.getHeight(world, new BlockPos(wx, 0, wz));
                maxHeight = Math.max(maxHeight, level);
                minHeight = Math.min(minHeight, level);
                totalHeight += level;
                squareHeightSum += level * level;
                level = WorldCache.getBottomHeight(world, new BlockPos(wx, 0, wz));
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
        boolean afloat = method == Method.AFLOAT;
        boolean underwater = method == Method.UNDERWATER;
        boolean sky = method == Method.SKY;
        boolean underground = method == Method.UNDERGROUND;
        boolean hill = method == Method.HILL;
        double roughness = Math.sqrt(variance);
        double underLiquidRoughness = Math.sqrt(varianceUnderLiquid);
        double roughnessFactor = Configurator.ACCURACY;
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
            posY += Configurator.FORCE_LIFT;
        }
        posY = Math.max(4, Math.min(posY, 250));
        return new Posture(posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ, width, height, length);
    }

}
