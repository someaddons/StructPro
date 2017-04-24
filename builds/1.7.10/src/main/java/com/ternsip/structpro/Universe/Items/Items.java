package com.ternsip.structpro.Universe.Items;

import com.google.common.collect.ImmutableList;
import com.ternsip.structpro.Logic.Configurator;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Selector;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Items storage
 * @author  Ternsip
 * @since JDK 1.6
 */
@SuppressWarnings({"WeakerAccess", "deprecation"})
public class Items extends net.minecraft.init.Items {

    /** Cache mapping for all possible metadata values for each item */
    private static final HashMap<Item, ArrayList<Integer>> possibleMeta = new HashMap<Item, ArrayList<Integer>>();

    /** All possible items */
    @SuppressWarnings({"unchecked"})
    public static final Selector<Item> items = new Selector<Item>() {{
        final Selector<Item> names = new Selector<Item>() {{
            for (Item item : (List<Item>)(ImmutableList.copyOf(Item.itemRegistry))) {
                if (isValidItem(item) && !getPossibleMeta(item).isEmpty()) {
                    add(item.getUnlocalizedName().replace("item.", ""), item);
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
        for (Item item: acceptable) {
            add(item.getUnlocalizedName().replace("item.", ""), item);
            add(String.valueOf(Item.getIdFromItem(item)), item);
        }
    }};

    /**
     * Resolve item validity
     * @param item Target item
     * @return Is item valid
     */
    public static boolean isValidItem(Item item) {
        return item != null;
    }

    /**
     * Resolve item state validity
     * @param item Target item
     * @param meta Item metadata
     * @return Is item state valid
     */
    @SuppressWarnings({"ConstantConditions"})
    public static boolean isValidItemState(Item item, int meta) {
        if (!isValidItem(item)) {
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
     * Get item possible states
     * @param item Target item
     * @return Possible item metadata array
     */
    public static ArrayList<Integer> getPossibleMeta(Item item) {
        if (!possibleMeta.containsKey(item)) {
            ArrayList<Integer> states = new ArrayList<Integer>();
            for (int meta = 0; meta <= item.getMaxDamage(); ++meta) {
                if (isValidItemState(item, meta)) {
                    states.add(meta);
                }
            }
            possibleMeta.put(item, states);
        }
        return possibleMeta.get(item);
    }


    /**
     * Get item max stack size
     * @param item Target item
     * @return Item max stack size
     */
    public static int itemMaxStack(Item item) {
        return item.getItemStackLimit();
    }

}
