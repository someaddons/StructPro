package com.ternsip.structpro.universe.items;

import com.ternsip.structpro.logic.Configurator;
import com.ternsip.structpro.universe.utils.Report;
import com.ternsip.structpro.universe.utils.Selector;
import net.minecraft.init.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Items wrapper
 * @author  Ternsip
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class UItems {

    public static final UItem AIR = new UItem(Items.AIR);
    public static final UItem IRON_SHOVEL = new UItem(Items.IRON_SHOVEL);
    public static final UItem IRON_PICKAXE = new UItem(Items.IRON_PICKAXE);
    public static final UItem IRON_AXE = new UItem(Items.IRON_AXE);
    public static final UItem FLINT_AND_STEEL = new UItem(Items.FLINT_AND_STEEL);
    public static final UItem APPLE = new UItem(Items.APPLE);
    public static final UItem BOW = new UItem(Items.BOW);
    public static final UItem ARROW = new UItem(Items.ARROW);
    public static final UItem SPECTRAL_ARROW = new UItem(Items.SPECTRAL_ARROW);
    public static final UItem TIPPED_ARROW = new UItem(Items.TIPPED_ARROW);
    public static final UItem COAL = new UItem(Items.COAL);
    public static final UItem DIAMOND = new UItem(Items.DIAMOND);
    public static final UItem IRON_INGOT = new UItem(Items.IRON_INGOT);
    public static final UItem GOLD_INGOT = new UItem(Items.GOLD_INGOT);
    public static final UItem IRON_SWORD = new UItem(Items.IRON_SWORD);
    public static final UItem WOODEN_SWORD = new UItem(Items.WOODEN_SWORD);
    public static final UItem WOODEN_SHOVEL = new UItem(Items.WOODEN_SHOVEL);
    public static final UItem WOODEN_PICKAXE = new UItem(Items.WOODEN_PICKAXE);
    public static final UItem WOODEN_AXE = new UItem(Items.WOODEN_AXE);
    public static final UItem STONE_SWORD = new UItem(Items.STONE_SWORD);
    public static final UItem STONE_SHOVEL = new UItem(Items.STONE_SHOVEL);
    public static final UItem STONE_PICKAXE = new UItem(Items.STONE_PICKAXE);
    public static final UItem STONE_AXE = new UItem(Items.STONE_AXE);
    public static final UItem DIAMOND_SWORD = new UItem(Items.DIAMOND_SWORD);
    public static final UItem DIAMOND_SHOVEL = new UItem(Items.DIAMOND_SHOVEL);
    public static final UItem DIAMOND_PICKAXE = new UItem(Items.DIAMOND_PICKAXE);
    public static final UItem DIAMOND_AXE = new UItem(Items.DIAMOND_AXE);
    public static final UItem STICK = new UItem(Items.STICK);
    public static final UItem BOWL = new UItem(Items.BOWL);
    public static final UItem MUSHROOM_STEW = new UItem(Items.MUSHROOM_STEW);
    public static final UItem GOLDEN_SWORD = new UItem(Items.GOLDEN_SWORD);
    public static final UItem GOLDEN_SHOVEL = new UItem(Items.GOLDEN_SHOVEL);
    public static final UItem GOLDEN_PICKAXE = new UItem(Items.GOLDEN_PICKAXE);
    public static final UItem GOLDEN_AXE = new UItem(Items.GOLDEN_AXE);
    public static final UItem STRING = new UItem(Items.STRING);
    public static final UItem FEATHER = new UItem(Items.FEATHER);
    public static final UItem GUNPOWDER = new UItem(Items.GUNPOWDER);
    public static final UItem WOODEN_HOE = new UItem(Items.WOODEN_HOE);
    public static final UItem STONE_HOE = new UItem(Items.STONE_HOE);
    public static final UItem IRON_HOE = new UItem(Items.IRON_HOE);
    public static final UItem DIAMOND_HOE = new UItem(Items.DIAMOND_HOE);
    public static final UItem GOLDEN_HOE = new UItem(Items.GOLDEN_HOE);
    public static final UItem WHEAT_SEEDS = new UItem(Items.WHEAT_SEEDS);
    public static final UItem WHEAT = new UItem(Items.WHEAT);
    public static final UItem BREAD = new UItem(Items.BREAD);
    public static final UItem LEATHER_HELMET = new UItem(Items.LEATHER_HELMET);
    public static final UItem LEATHER_CHESTPLATE = new UItem(Items.LEATHER_CHESTPLATE);
    public static final UItem LEATHER_LEGGINGS = new UItem(Items.LEATHER_LEGGINGS);
    public static final UItem LEATHER_BOOTS = new UItem(Items.LEATHER_BOOTS);
    public static final UItem CHAINMAIL_HELMET = new UItem(Items.CHAINMAIL_HELMET);
    public static final UItem CHAINMAIL_CHESTPLATE = new UItem(Items.CHAINMAIL_CHESTPLATE);
    public static final UItem CHAINMAIL_LEGGINGS = new UItem(Items.CHAINMAIL_LEGGINGS);
    public static final UItem CHAINMAIL_BOOTS = new UItem(Items.CHAINMAIL_BOOTS);
    public static final UItem IRON_HELMET = new UItem(Items.IRON_HELMET);
    public static final UItem IRON_CHESTPLATE = new UItem(Items.IRON_CHESTPLATE);
    public static final UItem IRON_LEGGINGS = new UItem(Items.IRON_LEGGINGS);
    public static final UItem IRON_BOOTS = new UItem(Items.IRON_BOOTS);
    public static final UItem DIAMOND_HELMET = new UItem(Items.DIAMOND_HELMET);
    public static final UItem DIAMOND_CHESTPLATE = new UItem(Items.DIAMOND_CHESTPLATE);
    public static final UItem DIAMOND_LEGGINGS = new UItem(Items.DIAMOND_LEGGINGS);
    public static final UItem DIAMOND_BOOTS = new UItem(Items.DIAMOND_BOOTS);
    public static final UItem GOLDEN_HELMET = new UItem(Items.GOLDEN_HELMET);
    public static final UItem GOLDEN_CHESTPLATE = new UItem(Items.GOLDEN_CHESTPLATE);
    public static final UItem GOLDEN_LEGGINGS = new UItem(Items.GOLDEN_LEGGINGS);
    public static final UItem GOLDEN_BOOTS = new UItem(Items.GOLDEN_BOOTS);
    public static final UItem FLINT = new UItem(Items.FLINT);
    public static final UItem PORKCHOP = new UItem(Items.PORKCHOP);
    public static final UItem COOKED_PORKCHOP = new UItem(Items.COOKED_PORKCHOP);
    public static final UItem PAINTING = new UItem(Items.PAINTING);
    public static final UItem GOLDEN_APPLE = new UItem(Items.GOLDEN_APPLE);
    public static final UItem SIGN = new UItem(Items.SIGN);
    public static final UItem OAK_DOOR = new UItem(Items.OAK_DOOR);
    public static final UItem SPRUCE_DOOR = new UItem(Items.SPRUCE_DOOR);
    public static final UItem BIRCH_DOOR = new UItem(Items.BIRCH_DOOR);
    public static final UItem JUNGLE_DOOR = new UItem(Items.JUNGLE_DOOR);
    public static final UItem ACACIA_DOOR = new UItem(Items.ACACIA_DOOR);
    public static final UItem DARK_OAK_DOOR = new UItem(Items.DARK_OAK_DOOR);
    public static final UItem BUCKET = new UItem(Items.BUCKET);
    public static final UItem WATER_BUCKET = new UItem(Items.WATER_BUCKET);
    public static final UItem LAVA_BUCKET = new UItem(Items.LAVA_BUCKET);
    public static final UItem MINECART = new UItem(Items.MINECART);
    public static final UItem SADDLE = new UItem(Items.SADDLE);
    public static final UItem IRON_DOOR = new UItem(Items.IRON_DOOR);
    public static final UItem REDSTONE = new UItem(Items.REDSTONE);
    public static final UItem SNOWBALL = new UItem(Items.SNOWBALL);
    public static final UItem BOAT = new UItem(Items.BOAT);
    public static final UItem SPRUCE_BOAT = new UItem(Items.SPRUCE_BOAT);
    public static final UItem BIRCH_BOAT = new UItem(Items.BIRCH_BOAT);
    public static final UItem JUNGLE_BOAT = new UItem(Items.JUNGLE_BOAT);
    public static final UItem ACACIA_BOAT = new UItem(Items.ACACIA_BOAT);
    public static final UItem DARK_OAK_BOAT = new UItem(Items.DARK_OAK_BOAT);
    public static final UItem LEATHER = new UItem(Items.LEATHER);
    public static final UItem MILK_BUCKET = new UItem(Items.MILK_BUCKET);
    public static final UItem BRICK = new UItem(Items.BRICK);
    public static final UItem CLAY_BALL = new UItem(Items.CLAY_BALL);
    public static final UItem REEDS = new UItem(Items.REEDS);
    public static final UItem PAPER = new UItem(Items.PAPER);
    public static final UItem BOOK = new UItem(Items.BOOK);
    public static final UItem SLIME_BALL = new UItem(Items.SLIME_BALL);
    public static final UItem CHEST_MINECART = new UItem(Items.CHEST_MINECART);
    public static final UItem FURNACE_MINECART = new UItem(Items.FURNACE_MINECART);
    public static final UItem EGG = new UItem(Items.EGG);
    public static final UItem COMPASS = new UItem(Items.COMPASS);
    public static final UItem FISHING_ROD = new UItem(Items.FISHING_ROD);
    public static final UItem CLOCK = new UItem(Items.CLOCK);
    public static final UItem GLOWSTONE_DUST = new UItem(Items.GLOWSTONE_DUST);
    public static final UItem FISH = new UItem(Items.FISH);
    public static final UItem COOKED_FISH = new UItem(Items.COOKED_FISH);
    public static final UItem DYE = new UItem(Items.DYE);
    public static final UItem BONE = new UItem(Items.BONE);
    public static final UItem SUGAR = new UItem(Items.SUGAR);
    public static final UItem CAKE = new UItem(Items.CAKE);
    public static final UItem BED = new UItem(Items.BED);
    public static final UItem REPEATER = new UItem(Items.REPEATER);
    public static final UItem COOKIE = new UItem(Items.COOKIE);
    public static final UItem FILLED_MAP = new UItem(Items.FILLED_MAP);
    public static final UItem SHEARS = new UItem(Items.SHEARS);
    public static final UItem MELON = new UItem(Items.MELON);
    public static final UItem PUMPKIN_SEEDS = new UItem(Items.PUMPKIN_SEEDS);
    public static final UItem MELON_SEEDS = new UItem(Items.MELON_SEEDS);
    public static final UItem BEEF = new UItem(Items.BEEF);
    public static final UItem COOKED_BEEF = new UItem(Items.COOKED_BEEF);
    public static final UItem CHICKEN = new UItem(Items.CHICKEN);
    public static final UItem COOKED_CHICKEN = new UItem(Items.COOKED_CHICKEN);
    public static final UItem MUTTON = new UItem(Items.MUTTON);
    public static final UItem COOKED_MUTTON = new UItem(Items.COOKED_MUTTON);
    public static final UItem RABBIT = new UItem(Items.RABBIT);
    public static final UItem COOKED_RABBIT = new UItem(Items.COOKED_RABBIT);
    public static final UItem RABBIT_STEW = new UItem(Items.RABBIT_STEW);
    public static final UItem RABBIT_FOOT = new UItem(Items.RABBIT_FOOT);
    public static final UItem RABBIT_HIDE = new UItem(Items.RABBIT_HIDE);
    public static final UItem ROTTEN_FLESH = new UItem(Items.ROTTEN_FLESH);
    public static final UItem ENDER_PEARL = new UItem(Items.ENDER_PEARL);
    public static final UItem BLAZE_ROD = new UItem(Items.BLAZE_ROD);
    public static final UItem GHAST_TEAR = new UItem(Items.GHAST_TEAR);
    public static final UItem GOLD_NUGGET = new UItem(Items.GOLD_NUGGET);
    public static final UItem NETHER_WART = new UItem(Items.NETHER_WART);
    public static final UItem POTIONITEM = new UItem(Items.POTIONITEM);
    public static final UItem SPLASH_POTION = new UItem(Items.SPLASH_POTION);
    public static final UItem LINGERING_POTION = new UItem(Items.LINGERING_POTION);
    public static final UItem GLASS_BOTTLE = new UItem(Items.GLASS_BOTTLE);
    public static final UItem DRAGON_BREATH = new UItem(Items.DRAGON_BREATH);
    public static final UItem SPIDER_EYE = new UItem(Items.SPIDER_EYE);
    public static final UItem FERMENTED_SPIDER_EYE = new UItem(Items.FERMENTED_SPIDER_EYE);
    public static final UItem BLAZE_POWDER = new UItem(Items.BLAZE_POWDER);
    public static final UItem MAGMA_CREAM = new UItem(Items.MAGMA_CREAM);
    public static final UItem BREWING_STAND = new UItem(Items.BREWING_STAND);
    public static final UItem CAULDRON = new UItem(Items.CAULDRON);
    public static final UItem ENDER_EYE = new UItem(Items.ENDER_EYE);
    public static final UItem SPECKLED_MELON = new UItem(Items.SPECKLED_MELON);
    public static final UItem SPAWN_EGG = new UItem(Items.SPAWN_EGG);
    public static final UItem EXPERIENCE_BOTTLE = new UItem(Items.EXPERIENCE_BOTTLE);
    public static final UItem FIRE_CHARGE = new UItem(Items.FIRE_CHARGE);
    public static final UItem WRITABLE_BOOK = new UItem(Items.WRITABLE_BOOK);
    public static final UItem WRITTEN_BOOK = new UItem(Items.WRITTEN_BOOK);
    public static final UItem EMERALD = new UItem(Items.EMERALD);
    public static final UItem ITEM_FRAME = new UItem(Items.ITEM_FRAME);
    public static final UItem FLOWER_POT = new UItem(Items.FLOWER_POT);
    public static final UItem CARROT = new UItem(Items.CARROT);
    public static final UItem POTATO = new UItem(Items.POTATO);
    public static final UItem BAKED_POTATO = new UItem(Items.BAKED_POTATO);
    public static final UItem POISONOUS_POTATO = new UItem(Items.POISONOUS_POTATO);
    public static final UItem MAP = new UItem(Items.MAP);
    public static final UItem GOLDEN_CARROT = new UItem(Items.GOLDEN_CARROT);
    public static final UItem SKULL = new UItem(Items.SKULL);
    public static final UItem CARROT_ON_A_STICK = new UItem(Items.CARROT_ON_A_STICK);
    public static final UItem NETHER_STAR = new UItem(Items.NETHER_STAR);
    public static final UItem PUMPKIN_PIE = new UItem(Items.PUMPKIN_PIE);
    public static final UItem FIREWORKS = new UItem(Items.FIREWORKS);
    public static final UItem FIREWORK_CHARGE = new UItem(Items.FIREWORK_CHARGE);
    public static final UItem ENCHANTED_BOOK = new UItem(Items.ENCHANTED_BOOK);
    public static final UItem COMPARATOR = new UItem(Items.COMPARATOR);
    public static final UItem NETHERBRICK = new UItem(Items.NETHERBRICK);
    public static final UItem QUARTZ = new UItem(Items.QUARTZ);
    public static final UItem TNT_MINECART = new UItem(Items.TNT_MINECART);
    public static final UItem HOPPER_MINECART = new UItem(Items.HOPPER_MINECART);
    public static final UItem ARMOR_STAND = new UItem(Items.ARMOR_STAND);
    public static final UItem IRON_HORSE_ARMOR = new UItem(Items.IRON_HORSE_ARMOR);
    public static final UItem GOLDEN_HORSE_ARMOR = new UItem(Items.GOLDEN_HORSE_ARMOR);
    public static final UItem DIAMOND_HORSE_ARMOR = new UItem(Items.DIAMOND_HORSE_ARMOR);
    public static final UItem LEAD = new UItem(Items.LEAD);
    public static final UItem NAME_TAG = new UItem(Items.NAME_TAG);
    public static final UItem COMMAND_BLOCK_MINECART = new UItem(Items.COMMAND_BLOCK_MINECART);
    public static final UItem RECORD_13 = new UItem(Items.RECORD_13);
    public static final UItem RECORD_CAT = new UItem(Items.RECORD_CAT);
    public static final UItem RECORD_BLOCKS = new UItem(Items.RECORD_BLOCKS);
    public static final UItem RECORD_CHIRP = new UItem(Items.RECORD_CHIRP);
    public static final UItem RECORD_FAR = new UItem(Items.RECORD_FAR);
    public static final UItem RECORD_MALL = new UItem(Items.RECORD_MALL);
    public static final UItem RECORD_MELLOHI = new UItem(Items.RECORD_MELLOHI);
    public static final UItem RECORD_STAL = new UItem(Items.RECORD_STAL);
    public static final UItem RECORD_STRAD = new UItem(Items.RECORD_STRAD);
    public static final UItem RECORD_WARD = new UItem(Items.RECORD_WARD);
    public static final UItem RECORD_11 = new UItem(Items.RECORD_11);
    public static final UItem RECORD_WAIT = new UItem(Items.RECORD_WAIT);
    public static final UItem PRISMARINE_SHARD = new UItem(Items.PRISMARINE_SHARD);
    public static final UItem PRISMARINE_CRYSTALS = new UItem(Items.PRISMARINE_CRYSTALS);
    public static final UItem BANNER = new UItem(Items.BANNER);
    public static final UItem END_CRYSTAL = new UItem(Items.END_CRYSTAL);
    public static final UItem SHIELD = new UItem(Items.SHIELD);
    public static final UItem ELYTRA = new UItem(Items.ELYTRA);
    public static final UItem CHORUS_FRUIT = new UItem(Items.CHORUS_FRUIT);
    public static final UItem CHORUS_FRUIT_POPPED = new UItem(Items.CHORUS_FRUIT_POPPED);
    public static final UItem BEETROOT_SEEDS = new UItem(Items.BEETROOT_SEEDS);
    public static final UItem BEETROOT = new UItem(Items.BEETROOT);
    public static final UItem BEETROOT_SOUP = new UItem(Items.BEETROOT_SOUP);
    public static final UItem SHULKER_SHELL = new UItem(Items.SHULKER_SHELL);

    /** Cache mapping for all possible metadata values for each item */
    private static final HashMap<UItem, ArrayList<Integer>> possibleMeta = new HashMap<>();

    /** All possible items */
    public static final Selector<UItem> items = new Selector<>();

    /**
     * Get item possible states
     * @param item Target item
     * @return Possible item metadata array
     */
    public static ArrayList<Integer> getPossibleMeta(UItem item) {
        if (!possibleMeta.containsKey(item)) {
            ArrayList<Integer> states = new ArrayList<>();
            for (int meta = 0; meta <= item.getMaxDamage(); ++meta) {
                if (item.isValidItemState(meta)) {
                    states.add(meta);
                }
            }
            possibleMeta.put(item, states);
        }
        return possibleMeta.get(item);
    }

    static {

        /* Construct acceptable item list */
        final Selector<UItem> names = new Selector<>();
        for (UItem item : UItem.getItems()) {
            if (item.isValid() && !getPossibleMeta(item).isEmpty()) {
                names.add(item.getPath(), item);
            }
        }
        ArrayList<UItem> acceptable = new ArrayList<>();
        acceptable.addAll(names.select());
        for (String exclusion : Configurator.EXCLUDE_ITEMS) {
            try {
                Pattern ePattern = Pattern.compile(exclusion, Pattern.CASE_INSENSITIVE);
                acceptable.removeAll(names.select(ePattern));
            } catch (PatternSyntaxException pse) {
                new Report().post("BAD PATTERN", pse.getMessage()).print();
            }
        }

        /* Add acceptable items */
        for (UItem item: acceptable) {
            items.add(item.getPath(), item);
            items.add(String.valueOf(item.getId()), item);
        }

    }

}
