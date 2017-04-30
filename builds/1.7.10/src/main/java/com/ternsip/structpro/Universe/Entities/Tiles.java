package com.ternsip.structpro.Universe.Entities;

import com.ternsip.structpro.Logic.Configurator;
import com.ternsip.structpro.Structure.Biome;
import com.ternsip.structpro.Universe.Blocks.Blocks;
import com.ternsip.structpro.Universe.Universe;
import com.ternsip.structpro.Universe.Items.Items;
import com.ternsip.structpro.Utils.BlockPos;
import com.ternsip.structpro.Utils.Utils;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.*;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Tile entities controller
 * @author  Ternsip
 * @since JDK 1.6
 */
@SuppressWarnings("unused")
public class Tiles {

    /**
     * Load tile entity data from NBT tag
     * @param tile Target tile
     * @param tag Tag to load
     * @param seed Loading seed
     */
    public static void load(TileEntity tile, NBTTagCompound tag, long seed) {
        if (tile == null) {
            return;
        }
        if (tile instanceof TileEntityChest) {
            load((TileEntityChest)tile, tag, seed);
            return;
        }
        if (tile instanceof TileEntityMobSpawner) {
            load((TileEntityMobSpawner)tile, tag, seed);
        }
        if (tile instanceof TileEntitySign) {
            load((TileEntitySign)tile, tag, seed);
            return;
        }
        if (tile instanceof TileEntityCommandBlock) {
            load((TileEntityCommandBlock)tile, tag, seed);
            return;
        }
        if (tile instanceof IInventory) {
            load((IInventory)tile, tag, seed);
            return;
        }
        if (tile instanceof TileEntityComparator) {
            load((TileEntityComparator)tile, tag, seed);
            return;
        }
        if (tile instanceof TileEntityFlowerPot) {
            load((TileEntityFlowerPot)tile, tag, seed);
            return;
        }
        if (tile instanceof TileEntityNote) {
            load((TileEntityNote)tile, tag, seed);
        }
    }

    /**
     * Load chest data from NBT tag
     * @param chest Target chest
     * @param tag tag to load
     * @param seed Loading seed
     */
    private static void load(TileEntityChest chest, NBTTagCompound tag, long seed) {
        Random random = new Random(seed);
        if (Configurator.LOOT_CHANCE < random.nextDouble()) {
            return;
        }
        ArrayList<ItemStack> forceItems = new ArrayList<ItemStack>();
        if (Configurator.NATIVE_LOOT && tag != null) {
            NBTTagList items = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < items.tagCount(); ++i) {
                NBTTagCompound stackTag = items.getCompoundTagAt(i);
                String itemName = stackTag.getString("id").replaceAll(".*:", "");
                itemName = itemName.isEmpty() ? String.valueOf(stackTag.getShort("id")) : itemName;
                Pattern iPattern = Pattern.compile(Pattern.quote(itemName), Pattern.CASE_INSENSITIVE);
                Item item = Utils.select(Items.items.select(iPattern), random.nextLong());
                byte cnt = items.getCompoundTagAt(i).getByte("Count");
                int dmg = items.getCompoundTagAt(i).getShort("Damage");
                if (item != null && cnt > 0 && cnt <= Items.itemMaxStack(item) && Items.getPossibleMeta(item).contains(dmg)) {
                    forceItems.add(new ItemStack(item, cnt, dmg));
                }
            }
        }
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
            Item item = Utils.select(Items.items.select(), random.nextLong());
            if (item == null) {
                continue;
            }
            Integer stackMeta = Utils.select(Items.getPossibleMeta(item), random.nextLong());
            if (stackMeta == null) {
                continue;
            }
            if (idx < forceItems.size()) {
                item = forceItems.get(idx).getItem();
                stackMeta = forceItems.get(idx).getItemDamage();
            }
            int maxStackSize = Math.max(1, Math.min(Configurator.MAX_CHEST_STACK_SIZE, Items.itemMaxStack(item)));
            int stackSize = 1 + random.nextInt(maxStackSize);
            chest.setInventorySlotContents(permutation.get(idx), new ItemStack(item, stackSize, stackMeta));
        }
    }

    /**
     * Load mob-spawner data from NBT tag
     * @param spawner Target spawner
     * @param tag tag to load
     * @param seed Loading seed
     */
    private static void load(TileEntityMobSpawner spawner, NBTTagCompound tag, long seed) {
        Random random = new Random(seed);
        MobSpawnerBaseLogic logic = spawner.func_145881_a();
        Biome biome = Biome.valueOf(Universe.getBiome(spawner.getWorldObj(), new BlockPos(spawner.xCoord, spawner.yCoord, spawner.zCoord)));
        Class<? extends Entity> mob = Utils.select(Mobs.hostile.select(biome), random.nextLong());
        if (mob != null) {
            logic.setEntityName(Mobs.classToName(mob));
        }
        if (tag != null && tag.hasKey("EntityId")) {
            Pattern mPattern = Pattern.compile(".*" + Pattern.quote(tag.getString("EntityId")) + ".*", Pattern.CASE_INSENSITIVE);
            mob = Utils.select(Configurator.MOB_SPAWNERS_EGGS_ONLY ? Mobs.eggs.select(mPattern) : Mobs.mobs.select(mPattern));
            if (mob != null) {
                logic.setEntityName(Mobs.classToName(mob));
            }
        }
    }

    /**
     * Load sign data from NBT tag
     * @param sign Target sign
     * 0@param tag tag to load
     * @param seed Loading seed
     */
    private static void load(TileEntitySign sign, NBTTagCompound tag, long seed) {
        if (tag == null) {
            return;
        }
        Random random = new Random(seed);
        for (int i = 0; i < 4; ++i) {
            sign.signText[i] = tag.getString("Text" + (i + 1));
            if ( sign.signText[i].length() > 15) {
                sign.signText[i] = sign.signText[i].substring(0, 15);
            }
        }
    }

    /**
     * Load inventory data from NBT tag
     * @param inventory Target inventory
     * @param tag tag to load
     * @param seed Loading seed
     */
    private static void load(IInventory inventory, NBTTagCompound tag, long seed) {
        if (tag == null || !Configurator.NATIVE_LOOT) {
            return;
        }
        Random random = new Random(seed);
        NBTTagList items = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount() && i < inventory.getSizeInventory(); ++i) {
            NBTTagCompound stackTag = items.getCompoundTagAt(i);
            String itemName = stackTag.getString("id").replaceAll(".*:", "");
            itemName = itemName.isEmpty() ? String.valueOf(stackTag.getShort("id")) : itemName;
            Pattern iPattern = Pattern.compile(Pattern.quote(itemName), Pattern.CASE_INSENSITIVE);
            Item item = Utils.select(Items.items.select(iPattern), random.nextLong());
            byte count = items.getCompoundTagAt(i).getByte("Count");
            int damage = items.getCompoundTagAt(i).getShort("Damage");
            int slot = stackTag.hasKey("Slot", Constants.NBT.TAG_BYTE) ? stackTag.getByte("Slot") : i;
            slot = (slot < 0 || slot >= inventory.getSizeInventory()) ? i : slot;
            if (item != null && count > 0 && Items.getPossibleMeta(item).contains(damage)) {
                inventory.setInventorySlotContents(slot, new ItemStack(item, count, damage));
            }
        }
    }

    /**
     * Load command data from NBT tag
     * @param commandBlock Target command block
     * @param tag tag to load
     * @param seed Loading seed
     */
    private static void load(TileEntityCommandBlock commandBlock, NBTTagCompound tag, long seed) {
        if (tag == null) {
            return;
        }
        Random random = new Random(seed);
        CommandBlockLogic logic = commandBlock.func_145993_a();
        logic.func_145759_b(tag);
    }

    /**
     * Load comparator data from NBT tag
     * @param comparator Target comparator
     * @param tag tag to load
     * @param seed Loading seed
     */
    private static void load(TileEntityComparator comparator, NBTTagCompound tag, long seed) {
        if (tag == null) {
            return;
        }
        Random random = new Random(seed);
        comparator.setOutputSignal(tag.getInteger("OutputSignal"));
    }

    /**
     * Load pot data from NBT tag
     * @param pot Target pot
     * @param tag tag to load
     * @param seed Loading seed
     */
    private static void load(TileEntityFlowerPot pot, NBTTagCompound tag, long seed) {
        if (tag == null) {
            return;
        }
        Random random = new Random(seed);
        if (tag.hasKey("Item", Constants.NBT.TAG_STRING)) {
            String itemName = tag.getString("Item").replaceAll(".*:", "");
            itemName = itemName.isEmpty() ? String.valueOf(tag.getInteger("Item")) : itemName;
            Pattern iPattern = Pattern.compile(Pattern.quote(itemName), Pattern.CASE_INSENSITIVE);
            Item item = Utils.select(Items.items.select(iPattern), random.nextLong());
            if (item != null) {
                pot.func_145964_a(item, tag.getInteger("Data"));
            }
        }
    }

    /**
     * Load note data from NBT tag
     * @param note Target note
     * @param tag tag to load
     * @param seed Loading seed
     */
    private static void load(TileEntityNote note, NBTTagCompound tag, long seed) {
        if (tag == null) {
            return;
        }
        Random random = new Random(seed);
        note.note = (byte) MathHelper.clamp_int(tag.getByte("note"), 0, 24);
        note.previousRedstoneState = tag.getBoolean("powered");
    }

}
