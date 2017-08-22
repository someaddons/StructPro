package com.ternsip.structpro.universe.items;

import com.google.common.collect.ImmutableList;
import com.ternsip.structpro.logic.Configurator;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Item wrapper
 * @author  Ternsip
 */
@SuppressWarnings({"WeakerAccess", "deprecation"})
public class UItem {

    /** Minecraft native item */
    private Item item;

    /** Construct from minecraft native item */
    public UItem(Item item) {
        this.item = item;
    }

    /**
     * Resolve item state validity
     * @param meta Item metadata
     * @return Is item state valid
     */
    @SuppressWarnings({"ConstantConditions"})
    public boolean isValidItemState(int meta) {
        if (!isValid()) {
            return false;
        }
        try {
            IIcon sprite = item.getIconFromDamage(meta);
            return sprite != null &&
                    sprite.getIconName() != null &&
                    !sprite.getIconName().equalsIgnoreCase("") &&
                    !sprite.getIconName().equalsIgnoreCase("missingno");
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * Get all possible items
     * @return An array with all possible items
     */
    @SuppressWarnings({"unchecked"})
    public static Collection<UItem> getItems() {
        ArrayList<UItem> uItems = new ArrayList<>();
        for (Item item : (List<Item>)(ImmutableList.copyOf(Item.itemRegistry))) {
            uItems.add(new UItem(item));
        }
        return uItems;
    }

    /**
     * Resolve item validity
     * @return Is item valid
     */
    public boolean isValid() {
        return item != null && (item.delegate.name().contains("minecraft:") || !Configurator.ONLY_VANILLA_LOOT);
    }

    /**
     * Get item path
     * @return Item resource path
     * */
    public String getPath() {
        return item.getUnlocalizedName().replace("item.", "");
    }

    /**
     * Get item id
     * @return Item global index
     */
    public int getId() {
        return Item.getIdFromItem(item);
    }

    /**
     * Get Item max metadata/stack value
     * @return Max damage
     * */
    public int getMaxDamage() {
        return item.getMaxDamage();
    }

    /**
     * Get item max stack size
     * @return Item max stack size
     */
    public int getMaxStack() {
        return item.getItemStackLimit();
    }

    /** Return minecraft native item */
    public Item getItem() {
        return item;
    }

}
