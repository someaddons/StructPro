package com.ternsip.structpro.Universe.Entities;

import com.ternsip.structpro.Logic.Configurator;
import com.ternsip.structpro.Structure.Biome;
import com.ternsip.structpro.Universe.Cache.Universe;
import com.ternsip.structpro.Universe.Items.Items;
import com.ternsip.structpro.Utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Pattern;

/* Tile entities controller */
public class Tiles {

    /* Load tile entity data from NBT tag */
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
        }
    }

    /* Load chest data from NBT tag */
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
                Pattern iPattern = Pattern.compile(Pattern.quote(itemName), Pattern.CASE_INSENSITIVE);
                Item item = Utils.select(Items.items.select(iPattern), random.nextLong());
                byte cnt = items.getCompoundTagAt(i).getByte("Count");
                int dmg = items.getCompoundTagAt(i).getShort("Damage");
                if (item != null && cnt > 0 && cnt <= Items.itemMaxStack(item) && dmg >= 0 && dmg <= Items.itemMaxMeta(item)) {
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
            int stackMeta = random.nextInt(Math.abs(Items.itemMaxMeta(item)) + 1);
            if (idx < forceItems.size()) {
                item = forceItems.get(idx).getItem();
                stackMeta = forceItems.get(idx).getItemDamage();
            }
            int maxStackSize = Math.max(1, Math.min(Configurator.MAX_CHEST_STACK_SIZE, Items.itemMaxStack(item)));
            int stackSize = 1 + random.nextInt(maxStackSize);
            chest.setInventorySlotContents(permutation.get(idx), new ItemStack(item, stackSize, stackMeta));
        }
    }

    /* Load mob-spawner data from NBT tag */
    private static void load(TileEntityMobSpawner spawner, NBTTagCompound tag, long seed) {
        Random random = new Random(seed);
        MobSpawnerBaseLogic logic = spawner.getSpawnerBaseLogic();
        Biome biome = Biome.valueOf(Universe.getBiome(spawner.getWorld(), spawner.getPos()));
        Class<? extends Entity> mob = Utils.select(Mobs.hostile.select(biome), random.nextLong());
        if (mob != null) {
            logic.setEntityId(Mobs.classToName(mob));
        }
        if (tag != null && tag.hasKey("EntityId")) {
            String mobName = Pattern.quote(tag.getString("EntityId"));
            Pattern mPattern = Pattern.compile(".*" + Pattern.quote(mobName) + ".*", Pattern.CASE_INSENSITIVE);
            mob = Utils.select(Configurator.MOB_SPAWNERS_EGGS_ONLY ? Mobs.eggs.select(mPattern) : Mobs.mobs.select(mPattern));
            if (mob != null) {
                logic.setEntityId(Mobs.classToName(mob));
            }
        }
    }

    /* Load sign data from NBT tag */
    private static void load(TileEntitySign sign, NBTTagCompound tag, long seed) {
        if (tag == null) {
            return;
        }
        Random random = new Random(seed);
        for (int i = 0; i < 4; ++i) {
            try {
                ITextComponent tc = ITextComponent.Serializer.fromJsonLenient(tag.getString("Text" + (i + 1)));
                sign.signText[i] = new TextComponentString(tc.getUnformattedComponentText());
            } catch (Throwable ignored) {
            }
        }
    }

    /* Load inventory data from NBT tag */
    private static void load(IInventory inventory, NBTTagCompound tag, long seed) {
        if (tag == null || !Configurator.NATIVE_LOOT) {
            return;
        }
        Random random = new Random(seed);
        NBTTagList items = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount() && i < inventory.getSizeInventory(); ++i) {
            NBTTagCompound stackTag = items.getCompoundTagAt(i);
            String itemName = stackTag.getString("id").replaceAll(".*:", "");
            Pattern iPattern = Pattern.compile(Pattern.quote(itemName), Pattern.CASE_INSENSITIVE);
            Item item = Utils.select(Items.items.select(iPattern), random.nextLong());
            byte count = items.getCompoundTagAt(i).getByte("Count");
            int damage = items.getCompoundTagAt(i).getShort("Damage");
            if (item != null && count > 0) {
                inventory.setInventorySlotContents(i, new ItemStack(item, count, damage));
            }
        }
    }

    /* Load command data from NBT tag */
    private static void load(TileEntityCommandBlock commandBlock, NBTTagCompound tag, long seed) {
        if (tag == null) {
            return;
        }
        Random random = new Random(seed);
        CommandBlockBaseLogic logic = commandBlock.getCommandBlockLogic();
        logic.setCommand(tag.getString("Command"));
        if (tag.hasKey("CustomName")) {
            logic.setName(tag.getString("CustomName"));
        }
        if (tag.hasKey("TrackOutput")) {
            logic.setTrackOutput(tag.getBoolean("TrackOutput"));
        }
    }

}
