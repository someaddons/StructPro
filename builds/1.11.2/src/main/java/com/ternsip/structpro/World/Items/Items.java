package com.ternsip.structpro.World.Items;

import com.ternsip.structpro.Logic.Selector;
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

    /* Items names selector */
    private static Selector<Item> names = new Selector<Item>() {{
        for (Item item : items) {
            if (item.getRegistryName() != null) {
                add(item.getRegistryName().getResourcePath().toLowerCase(), item);
            }
        }
    }};

    /* Items domain selector */
    private static Selector<ArrayList<Item>> domains = new Selector<ArrayList<Item>>() {{
        for (final Item item : items) {
            if (item.getRegistryName() != null) {
                String domain = item.getRegistryName().getResourceDomain();
                ArrayList<ArrayList<Item>> domainItems = select(domain, false);
                if (domainItems.isEmpty()) {
                    add(domain, new ArrayList<Item>(){{add(item);}});
                } else {
                    domainItems.get(0).add(item);
                }
            }
        }
    }};

    /* Remove items given by names from storage */
    public static void remove(final Iterable<String> badItems) {
        items.removeAll(new HashSet<Item>() {{
            for (String itemName : badItems) {
                addAll(names.select(itemName, false));
            }
        }});
    }

    /* Remove items that are not vanilla */
    public static void removeNotVanilla() {
        items.removeAll(new HashSet<Item>() {{
            addAll(items);
            ArrayList<ArrayList<Item>> res = domains.select("minecraft", false);
            if (!res.isEmpty()) {
                removeAll(res.get(0));
            }
        }});
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
