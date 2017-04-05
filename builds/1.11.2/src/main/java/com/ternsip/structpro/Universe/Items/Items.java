package com.ternsip.structpro.Universe.Items;

import com.ternsip.structpro.Logic.Configurator;
import com.ternsip.structpro.Utils.Selector;
import com.ternsip.structpro.Utils.Report;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@SuppressWarnings({"WeakerAccess", "deprecation"})
public class Items extends net.minecraft.init.Items {

    /* All possible items */
    private static final ArrayList<Item> items = new ArrayList<Item>() {{
        final Selector<Item> names = new Selector<Item>() {{
            for (Item item : GameData.getItemRegistry().getValues()) {
                if (item != null && item.getRegistryName() != null) {
                    String path = item.getRegistryName().getResourcePath();
                    String domain = item.getRegistryName().getResourceDomain();
                    if (domain.equalsIgnoreCase("minecraft") || !Configurator.ONLY_VANILLA_LOOT) {
                        add(path, item);
                    }
                }
            }
        }};
        ArrayList<Item> acceptable = new ArrayList<Item>() {{
            addAll(names.select());
            for (String exclusion : Configurator.EXCLUDE_ITEMS) {
                try {
                    Pattern ePattern = Pattern.compile(exclusion, Pattern.CASE_INSENSITIVE);
                    removeAll(names.select(ePattern));
                } catch (PatternSyntaxException pse) {
                    new Report().post("BAD PATTERN", pse.getMessage()).print();
                }
            }
        }};
        addAll(acceptable);
    }};

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
