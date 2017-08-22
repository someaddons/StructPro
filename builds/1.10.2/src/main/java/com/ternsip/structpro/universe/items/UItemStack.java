package com.ternsip.structpro.universe.items;

import com.ternsip.structpro.universe.blocks.UBlock;
import net.minecraft.item.ItemStack;

/**
 * Item stack wrapper
 * @author  Ternsip
 */
public class UItemStack {

    /** Minecraft native item stack */
    private ItemStack itemStack;

    /** Construct item stack from item, count and metadata */
    public UItemStack(UItem item, int count, int meta) {
        itemStack = new ItemStack(item.getItem(), count, meta);
    }

    /** Construct item stack from block, count and metadata */
    public UItemStack(UBlock block, int count, int meta) {
        itemStack = new ItemStack(block.getBlock(), count, meta);
    }

    /** Returns minecraft native item stack */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Get item from stack
     * @return Stack item
     * */
    public UItem getItem() {
        return new UItem(itemStack.getItem());
    }

    /**
     * Get item damage
     * @return Item damage
     * */
    public int getItemDamage() {
        return itemStack.getItemDamage();
    }

}
