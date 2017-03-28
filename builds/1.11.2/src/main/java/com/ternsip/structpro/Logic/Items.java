package com.ternsip.structpro.Logic;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

import java.util.ArrayList;
import java.util.HashSet;

@SuppressWarnings({"WeakerAccess", "deprecation"})
public class Items extends net.minecraft.init.Items {

    /* All possible items */
    private static final ArrayList<Item> items = new ArrayList<Item>() {{
        for (Item item : GameData.getItemRegistry().getValues()) {
            if (item != null) {
                add(item);
            }
        }
    }};

    /* Returns most suitable item by name */
    public static ArrayList<Item> select(String name) {
        ArrayList<Item> result = new ArrayList<Item>();
        for (Item item : items) {
            if (    item.getRegistryName() != null &&
                    item.getRegistryName().getResourcePath().toLowerCase().contains(name.toLowerCase())) {
                result.add(item);
            }
        }
        return result;
    }

    /* Remove items given by names from storage */
    public static void remove(final Iterable<String> bannedItemNames) {
        HashSet<Item> bannedItems = new HashSet<Item>() {{
            for (String itemName : bannedItemNames) {
                addAll(select(itemName));
            }
        }};
        items.removeAll(bannedItems);
    }

    /* Remove items that are not vanilla */
    public static void removeNotVanilla() {
        final HashSet<Item> bannedItems = new HashSet<Item>() {{
            for (Item item : items) {
                if (    item.getRegistryName() != null &&
                        !item.getRegistryName().getResourceDomain().equalsIgnoreCase("minecraft")) {
                    add(item);
                }
            }
        }};
        items.removeAll(bannedItems);
    }

    /* Get item by name */
    public static Item ItemByName(String name) {
        return Item.REGISTRY.getObject(new ResourceLocation(name));
    }

    /* Get item max damage */
    public static int itemMaxMeta(Item item) {
        return item.getMaxDamage();
    }

    /* Get item max stack size */
    public static int itemMaxStack(Item item) {
        return item.getItemStackLimit();
    }

    /* Select all items */
    public static ArrayList<Item> select() {
        return items;
    }

}
