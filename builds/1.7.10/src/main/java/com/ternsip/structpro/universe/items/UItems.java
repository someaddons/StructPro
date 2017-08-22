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

    public static final UItem AIR = null;
    public static final UItem IRON_SHOVEL = new UItem(Items.iron_shovel);
    public static final UItem IRON_PICKAXE = new UItem(Items.iron_pickaxe);
    public static final UItem IRON_AXE = new UItem(Items.iron_axe);
    public static final UItem FLINT_AND_STEEL = new UItem(Items.flint_and_steel);
    public static final UItem APPLE = new UItem(Items.apple);
    public static final UItem BOW = new UItem(Items.bow);
    public static final UItem ARROW = new UItem(Items.arrow);
    public static final UItem COAL = new UItem(Items.coal);
    public static final UItem DIAMOND = new UItem(Items.diamond);
    public static final UItem IRON_INGOT = new UItem(Items.iron_ingot);
    public static final UItem GOLD_INGOT = new UItem(Items.gold_ingot);
    public static final UItem IRON_SWORD = new UItem(Items.iron_sword);
    public static final UItem WOODEN_SWORD = new UItem(Items.wooden_sword);
    public static final UItem WOODEN_SHOVEL = new UItem(Items.wooden_shovel);
    public static final UItem WOODEN_PICKAXE = new UItem(Items.wooden_pickaxe);
    public static final UItem WOODEN_AXE = new UItem(Items.wooden_axe);
    public static final UItem STONE_SWORD = new UItem(Items.stone_sword);
    public static final UItem STONE_SHOVEL = new UItem(Items.stone_shovel);
    public static final UItem STONE_PICKAXE = new UItem(Items.stone_pickaxe);
    public static final UItem STONE_AXE = new UItem(Items.stone_axe);
    public static final UItem DIAMOND_SWORD = new UItem(Items.diamond_sword);
    public static final UItem DIAMOND_SHOVEL = new UItem(Items.diamond_shovel);
    public static final UItem DIAMOND_PICKAXE = new UItem(Items.diamond_pickaxe);
    public static final UItem DIAMOND_AXE = new UItem(Items.diamond_axe);
    public static final UItem STICK = new UItem(Items.stick);
    public static final UItem BOWL = new UItem(Items.bowl);
    public static final UItem MUSHROOM_STEW = new UItem(Items.mushroom_stew);
    public static final UItem GOLDEN_SWORD = new UItem(Items.golden_sword);
    public static final UItem GOLDEN_SHOVEL = new UItem(Items.golden_shovel);
    public static final UItem GOLDEN_PICKAXE = new UItem(Items.golden_pickaxe);
    public static final UItem GOLDEN_AXE = new UItem(Items.golden_axe);
    public static final UItem STRING = new UItem(Items.string);
    public static final UItem FEATHER = new UItem(Items.feather);
    public static final UItem GUNPOWDER = new UItem(Items.gunpowder);
    public static final UItem WOODEN_HOE = new UItem(Items.wooden_hoe);
    public static final UItem STONE_HOE = new UItem(Items.stone_hoe);
    public static final UItem IRON_HOE = new UItem(Items.iron_hoe);
    public static final UItem DIAMOND_HOE = new UItem(Items.diamond_hoe);
    public static final UItem GOLDEN_HOE = new UItem(Items.golden_hoe);
    public static final UItem WHEAT_SEEDS = new UItem(Items.wheat_seeds);
    public static final UItem WHEAT = new UItem(Items.wheat);
    public static final UItem BREAD = new UItem(Items.bread);
    public static final UItem LEATHER_HELMET = new UItem(Items.leather_helmet);
    public static final UItem LEATHER_CHESTPLATE = new UItem(Items.leather_chestplate);
    public static final UItem LEATHER_LEGGINGS = new UItem(Items.leather_leggings);
    public static final UItem LEATHER_BOOTS = new UItem(Items.leather_boots);
    public static final UItem CHAINMAIL_HELMET = new UItem(Items.chainmail_helmet);
    public static final UItem CHAINMAIL_CHESTPLATE = new UItem(Items.chainmail_chestplate);
    public static final UItem CHAINMAIL_LEGGINGS = new UItem(Items.chainmail_leggings);
    public static final UItem CHAINMAIL_BOOTS = new UItem(Items.chainmail_boots);
    public static final UItem IRON_HELMET = new UItem(Items.iron_helmet);
    public static final UItem IRON_CHESTPLATE = new UItem(Items.iron_chestplate);
    public static final UItem IRON_LEGGINGS = new UItem(Items.iron_leggings);
    public static final UItem IRON_BOOTS = new UItem(Items.iron_boots);
    public static final UItem DIAMOND_HELMET = new UItem(Items.diamond_helmet);
    public static final UItem DIAMOND_CHESTPLATE = new UItem(Items.diamond_chestplate);
    public static final UItem DIAMOND_LEGGINGS = new UItem(Items.diamond_leggings);
    public static final UItem DIAMOND_BOOTS = new UItem(Items.diamond_boots);
    public static final UItem GOLDEN_HELMET = new UItem(Items.golden_helmet);
    public static final UItem GOLDEN_CHESTPLATE = new UItem(Items.golden_chestplate);
    public static final UItem GOLDEN_LEGGINGS = new UItem(Items.golden_leggings);
    public static final UItem GOLDEN_BOOTS = new UItem(Items.golden_boots);
    public static final UItem FLINT = new UItem(Items.flint);
    public static final UItem PORKCHOP = new UItem(Items.porkchop);
    public static final UItem COOKED_PORKCHOP = new UItem(Items.cooked_porkchop);
    public static final UItem PAINTING = new UItem(Items.painting);
    public static final UItem GOLDEN_APPLE = new UItem(Items.golden_apple);
    public static final UItem SIGN = new UItem(Items.sign);
    public static final UItem BUCKET = new UItem(Items.bucket);
    public static final UItem WATER_BUCKET = new UItem(Items.water_bucket);
    public static final UItem LAVA_BUCKET = new UItem(Items.lava_bucket);
    public static final UItem MINECART = new UItem(Items.minecart);
    public static final UItem SADDLE = new UItem(Items.saddle);
    public static final UItem IRON_DOOR = new UItem(Items.iron_door);
    public static final UItem REDSTONE = new UItem(Items.redstone);
    public static final UItem SNOWBALL = new UItem(Items.snowball);
    public static final UItem BOAT = new UItem(Items.boat);
    public static final UItem LEATHER = new UItem(Items.leather);
    public static final UItem MILK_BUCKET = new UItem(Items.milk_bucket);
    public static final UItem BRICK = new UItem(Items.brick);
    public static final UItem CLAY_BALL = new UItem(Items.clay_ball);
    public static final UItem REEDS = new UItem(Items.reeds);
    public static final UItem PAPER = new UItem(Items.paper);
    public static final UItem BOOK = new UItem(Items.book);
    public static final UItem SLIME_BALL = new UItem(Items.slime_ball);
    public static final UItem CHEST_MINECART = new UItem(Items.chest_minecart);
    public static final UItem FURNACE_MINECART = new UItem(Items.furnace_minecart);
    public static final UItem EGG = new UItem(Items.egg);
    public static final UItem COMPASS = new UItem(Items.compass);
    public static final UItem FISHING_ROD = new UItem(Items.fishing_rod);
    public static final UItem CLOCK = new UItem(Items.clock);
    public static final UItem GLOWSTONE_DUST = new UItem(Items.glowstone_dust);
    public static final UItem FISH = new UItem(Items.fish);
    public static final UItem DYE = new UItem(Items.dye);
    public static final UItem BONE = new UItem(Items.bone);
    public static final UItem SUGAR = new UItem(Items.sugar);
    public static final UItem CAKE = new UItem(Items.cake);
    public static final UItem BED = new UItem(Items.bed);
    public static final UItem REPEATER = new UItem(Items.repeater);
    public static final UItem COOKIE = new UItem(Items.cookie);
    public static final UItem FILLED_MAP = new UItem(Items.filled_map);
    public static final UItem SHEARS = new UItem(Items.shears);
    public static final UItem MELON = new UItem(Items.melon);
    public static final UItem PUMPKIN_SEEDS = new UItem(Items.pumpkin_seeds);
    public static final UItem MELON_SEEDS = new UItem(Items.melon_seeds);
    public static final UItem BEEF = new UItem(Items.beef);
    public static final UItem COOKED_BEEF = new UItem(Items.cooked_beef);
    public static final UItem CHICKEN = new UItem(Items.chicken);
    public static final UItem COOKED_CHICKEN = new UItem(Items.cooked_chicken);
    public static final UItem ROTTEN_FLESH = new UItem(Items.rotten_flesh);
    public static final UItem ENDER_PEARL = new UItem(Items.ender_pearl);
    public static final UItem BLAZE_ROD = new UItem(Items.blaze_rod);
    public static final UItem GHAST_TEAR = new UItem(Items.ghast_tear);
    public static final UItem GOLD_NUGGET = new UItem(Items.gold_nugget);
    public static final UItem NETHER_WART = new UItem(Items.nether_wart);
    public static final UItem POTIONITEM = new UItem(Items.potionitem);
    public static final UItem GLASS_BOTTLE = new UItem(Items.glass_bottle);
    public static final UItem SPIDER_EYE = new UItem(Items.spider_eye);
    public static final UItem FERMENTED_SPIDER_EYE = new UItem(Items.fermented_spider_eye);
    public static final UItem BLAZE_POWDER = new UItem(Items.blaze_powder);
    public static final UItem MAGMA_CREAM = new UItem(Items.magma_cream);
    public static final UItem BREWING_STAND = new UItem(Items.brewing_stand);
    public static final UItem CAULDRON = new UItem(Items.cauldron);
    public static final UItem ENDER_EYE = new UItem(Items.ender_eye);
    public static final UItem SPECKLED_MELON = new UItem(Items.speckled_melon);
    public static final UItem SPAWN_EGG = new UItem(Items.spawn_egg);
    public static final UItem EXPERIENCE_BOTTLE = new UItem(Items.experience_bottle);
    public static final UItem FIRE_CHARGE = new UItem(Items.fire_charge);
    public static final UItem WRITABLE_BOOK = new UItem(Items.writable_book);
    public static final UItem WRITTEN_BOOK = new UItem(Items.written_book);
    public static final UItem EMERALD = new UItem(Items.emerald);
    public static final UItem ITEM_FRAME = new UItem(Items.item_frame);
    public static final UItem FLOWER_POT = new UItem(Items.flower_pot);
    public static final UItem CARROT = new UItem(Items.carrot);
    public static final UItem POTATO = new UItem(Items.potato);
    public static final UItem BAKED_POTATO = new UItem(Items.baked_potato);
    public static final UItem POISONOUS_POTATO = new UItem(Items.poisonous_potato);
    public static final UItem MAP = new UItem(Items.map);
    public static final UItem GOLDEN_CARROT = new UItem(Items.golden_carrot);
    public static final UItem SKULL = new UItem(Items.skull);
    public static final UItem CARROT_ON_A_STICK = new UItem(Items.carrot_on_a_stick);
    public static final UItem NETHER_STAR = new UItem(Items.nether_star);
    public static final UItem PUMPKIN_PIE = new UItem(Items.pumpkin_pie);
    public static final UItem FIREWORKS = new UItem(Items.fireworks);
    public static final UItem FIREWORK_CHARGE = new UItem(Items.firework_charge);
    public static final UItem ENCHANTED_BOOK = new UItem(Items.enchanted_book);
    public static final UItem COMPARATOR = new UItem(Items.comparator);
    public static final UItem NETHERBRICK = new UItem(Items.netherbrick);
    public static final UItem QUARTZ = new UItem(Items.quartz);
    public static final UItem TNT_MINECART = new UItem(Items.tnt_minecart);
    public static final UItem HOPPER_MINECART = new UItem(Items.hopper_minecart);
    public static final UItem IRON_HORSE_ARMOR = new UItem(Items.iron_horse_armor);
    public static final UItem GOLDEN_HORSE_ARMOR = new UItem(Items.golden_horse_armor);
    public static final UItem DIAMOND_HORSE_ARMOR = new UItem(Items.diamond_horse_armor);
    public static final UItem LEAD = new UItem(Items.lead);
    public static final UItem NAME_TAG = new UItem(Items.name_tag);
    public static final UItem COMMAND_BLOCK_MINECART = new UItem(Items.command_block_minecart);
    public static final UItem RECORD_13 = new UItem(Items.record_13);
    public static final UItem RECORD_CAT = new UItem(Items.record_cat);
    public static final UItem RECORD_BLOCKS = new UItem(Items.record_blocks);
    public static final UItem RECORD_CHIRP = new UItem(Items.record_chirp);
    public static final UItem RECORD_FAR = new UItem(Items.record_far);
    public static final UItem RECORD_MALL = new UItem(Items.record_mall);
    public static final UItem RECORD_MELLOHI = new UItem(Items.record_mellohi);
    public static final UItem RECORD_STAL = new UItem(Items.record_stal);
    public static final UItem RECORD_STRAD = new UItem(Items.record_strad);
    public static final UItem RECORD_WARD = new UItem(Items.record_ward);
    public static final UItem RECORD_11 = new UItem(Items.record_11);
    public static final UItem RECORD_WAIT = new UItem(Items.record_wait);

    /** Cache mapping for all possible metadata values for each item */
    private static final HashMap<UItem, ArrayList<Integer>> possibleMeta = new HashMap<>();

    /** All possible items */
    public static final Selector<UItem> items = new Selector<>();

    /**
     * Get item possible states
     * @param uItem Target item
     * @return Possible item metadata array
     */
    public static ArrayList<Integer> getPossibleMeta(UItem uItem) {
        if (!possibleMeta.containsKey(uItem)) {
            ArrayList<Integer> states = new ArrayList<>();
            for (int meta = 0; meta <= uItem.getMaxDamage(); ++meta) {
                if (uItem.isValidItemState(meta)) {
                    states.add(meta);
                }
            }
            possibleMeta.put(uItem, states);
        }
        return possibleMeta.get(uItem);
    }

    static {

        /* Construct acceptable item list */
        final Selector<UItem> names = new Selector<>();
        for (UItem uItem : UItem.getItems()) {
            if (uItem.isValid() && !getPossibleMeta(uItem).isEmpty()) {
                names.add(uItem.getPath(), uItem);
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
        for (UItem uItem: acceptable) {
            items.add(uItem.getPath(), uItem);
            items.add(String.valueOf(uItem.getId()), uItem);
        }

    }

}
