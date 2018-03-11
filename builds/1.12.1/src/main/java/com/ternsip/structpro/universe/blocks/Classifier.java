package com.ternsip.structpro.universe.blocks;

import com.ternsip.structpro.logic.Configurator;
import com.ternsip.structpro.universe.utils.Report;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Block type classifier enumeration
 * SOIL - ground soil blocks
 * OVERLOOK - plants, stuff, web, fire, decorations, etc.
 * CARDINAL - blocks with cardinal influence
 * LIQUID - liquid blocks
 * LIGHT - blocks that emits light
 * HEAT_RAY - OVERLOOKS + LIQUID
 * GAS - Air, gases, etc.
 * SOP - GAS + LIQUID
 * TRANSPARENT - non solid blocks
 *
 * @author Ternsip
 */
@SuppressWarnings({"unused"})
public enum Classifier {

    SOIL(0x00),
    OVERLOOK(0x01),
    CARDINAL(0x02),
    LIQUID(0x03),
    LIGHT(0x04),
    HEAT_RAY(0x05),
    GAS(0x06),
    SOP(0x07),
    TRANSPARENT(0x8),
    PROTECTED(0x9);

    private final int value;

    Classifier(int value) {
        this.value = value;
    }

    /**
     * Ground soil blocks
     */
    private static final boolean[][] blocks = new boolean[Classifier.values().length][256];

    /**
     * Check if block of specific class by ID
     *
     * @param type    Classifier type
     * @param blockID Target block ID
     * @return If class block of type
     */
    public static boolean isBlock(Classifier type, int blockID) {
        return UBlocks.isVanilla(blockID) && blocks[type.value][blockID];
    }

    /**
     * Check if block of specific class by ID
     *
     * @param type  Classifier type
     * @param block Target block
     * @return If class block of type
     */
    public static boolean isBlock(Classifier type, UBlock block) {
        return isBlock(type, block.getID());
    }

    /**
     * Check if block of specific class by ID
     *
     * @param type  Classifier type
     * @param state Target block state
     * @return If class block of type
     */
    public static boolean isBlock(Classifier type, UBlockState state) {
        return isBlock(type, state.getBlock());
    }

    /**
     * Set block class by block state
     *
     * @param type  Classifier type
     * @param state Target block state
     */
    private static void setBlock(Classifier type, UBlockState state) {
        setBlock(type, state.getID());
    }

    /**
     * Set block class by block
     *
     * @param type  Classifier type
     * @param block Target block
     */
    private static void setBlock(Classifier type, UBlock block) {
        setBlock(type, block.getID());
    }

    /**
     * Set block class by block ID
     *
     * @param type    Classifier type
     * @param blockID Target block ID
     */
    private static void setBlock(Classifier type, int blockID) {
        if (UBlocks.isVanilla(blockID)) {
            blocks[type.value][blockID] = true;
        }
    }

    static {

        /* Set soil blocks */
        setBlock(SOIL, UBlocks.GRASS);
        setBlock(SOIL, UBlocks.DIRT);
        setBlock(SOIL, UBlocks.STONE);
        setBlock(SOIL, UBlocks.COBBLESTONE);
        setBlock(SOIL, UBlocks.SANDSTONE);
        setBlock(SOIL, UBlocks.NETHERRACK);
        setBlock(SOIL, UBlocks.GRAVEL);
        setBlock(SOIL, UBlocks.SAND);
        setBlock(SOIL, UBlocks.BEDROCK);
        setBlock(SOIL, UBlocks.COAL_ORE);
        setBlock(SOIL, UBlocks.IRON_ORE);
        setBlock(SOIL, UBlocks.GOLD_ORE);
        setBlock(SOIL, UBlocks.DIAMOND_ORE);
        setBlock(SOIL, UBlocks.REDSTONE_ORE);
        setBlock(SOIL, UBlocks.LAPIS_ORE);
        setBlock(SOIL, UBlocks.EMERALD_ORE);
        setBlock(SOIL, UBlocks.LIT_REDSTONE_ORE);
        setBlock(SOIL, UBlocks.QUARTZ_ORE);
        setBlock(SOIL, UBlocks.CLAY);
        setBlock(SOIL, UBlocks.OBSIDIAN);

        /* Set overlook blocks */
        setBlock(OVERLOOK, UBlocks.AIR);
        setBlock(OVERLOOK, UBlocks.LOG);
        setBlock(OVERLOOK, UBlocks.LOG2);
        setBlock(OVERLOOK, UBlocks.LEAVES);
        setBlock(OVERLOOK, UBlocks.LEAVES2);
        setBlock(OVERLOOK, UBlocks.SAPLING);
        setBlock(OVERLOOK, UBlocks.WEB);
        setBlock(OVERLOOK, UBlocks.TALLGRASS);
        setBlock(OVERLOOK, UBlocks.DEADBUSH);
        setBlock(OVERLOOK, UBlocks.YELLOW_FLOWER);
        setBlock(OVERLOOK, UBlocks.RED_FLOWER);
        setBlock(OVERLOOK, UBlocks.RED_MUSHROOM_BLOCK);
        setBlock(OVERLOOK, UBlocks.BROWN_MUSHROOM_BLOCK);
        setBlock(OVERLOOK, UBlocks.BROWN_MUSHROOM);
        setBlock(OVERLOOK, UBlocks.FIRE);
        setBlock(OVERLOOK, UBlocks.WHEAT);
        setBlock(OVERLOOK, UBlocks.SNOW_LAYER);
        setBlock(OVERLOOK, UBlocks.SNOW);
        setBlock(OVERLOOK, UBlocks.CACTUS);
        setBlock(OVERLOOK, UBlocks.PUMPKIN);
        setBlock(OVERLOOK, UBlocks.VINE);
        setBlock(OVERLOOK, UBlocks.COCOA);
        setBlock(OVERLOOK, UBlocks.WATERLILY);
        setBlock(OVERLOOK, UBlocks.DOUBLE_PLANT);

        /* Set liquids */
        setBlock(LIQUID, UBlocks.WATER);
        setBlock(LIQUID, UBlocks.FLOWING_WATER);
        setBlock(LIQUID, UBlocks.ICE);
        setBlock(LIQUID, UBlocks.LAVA);
        setBlock(LIQUID, UBlocks.FLOWING_LAVA);

        /* Cardinal blocks - doors */
        setBlock(CARDINAL, UBlocks.TRAPDOOR);
        setBlock(CARDINAL, UBlocks.IRON_TRAPDOOR);
        setBlock(CARDINAL, UBlocks.SPRUCE_DOOR);
        setBlock(CARDINAL, UBlocks.OAK_DOOR);
        setBlock(CARDINAL, UBlocks.DARK_OAK_DOOR);
        setBlock(CARDINAL, UBlocks.ACACIA_DOOR);
        setBlock(CARDINAL, UBlocks.BIRCH_DOOR);
        setBlock(CARDINAL, UBlocks.IRON_DOOR);
        setBlock(CARDINAL, UBlocks.JUNGLE_DOOR);

        /* Cardinal blocks - glass */
        setBlock(CARDINAL, UBlocks.GLASS);
        setBlock(CARDINAL, UBlocks.GLASS_PANE);
        setBlock(CARDINAL, UBlocks.STAINED_GLASS_PANE);
        setBlock(CARDINAL, UBlocks.STAINED_GLASS);

        /* Cardinal blocks - interior */
        setBlock(CARDINAL, UBlocks.CHEST);
        setBlock(CARDINAL, UBlocks.ENDER_CHEST);
        setBlock(CARDINAL, UBlocks.TRAPPED_CHEST);
        setBlock(CARDINAL, UBlocks.FLOWER_POT);
        setBlock(CARDINAL, UBlocks.CHORUS_FLOWER);
        setBlock(CARDINAL, UBlocks.RED_FLOWER);
        setBlock(CARDINAL, UBlocks.YELLOW_FLOWER);
        setBlock(CARDINAL, UBlocks.RAIL);
        setBlock(CARDINAL, UBlocks.ACTIVATOR_RAIL);
        setBlock(CARDINAL, UBlocks.DETECTOR_RAIL);
        setBlock(CARDINAL, UBlocks.GOLDEN_RAIL);
        setBlock(CARDINAL, UBlocks.REDSTONE_WIRE);
        setBlock(CARDINAL, UBlocks.TRIPWIRE);
        setBlock(CARDINAL, UBlocks.TRIPWIRE_HOOK);
        setBlock(CARDINAL, UBlocks.PORTAL);
        setBlock(CARDINAL, UBlocks.COMMAND_BLOCK);
        setBlock(CARDINAL, UBlocks.POWERED_COMPARATOR);
        setBlock(CARDINAL, UBlocks.UNPOWERED_COMPARATOR);
        setBlock(CARDINAL, UBlocks.DRAGON_EGG);
        setBlock(CARDINAL, UBlocks.HAY_BLOCK);
        setBlock(CARDINAL, UBlocks.NETHER_WART);
        setBlock(CARDINAL, UBlocks.ANVIL);
        setBlock(CARDINAL, UBlocks.CRAFTING_TABLE);
        setBlock(CARDINAL, UBlocks.CACTUS);
        setBlock(CARDINAL, UBlocks.JUKEBOX);
        setBlock(CARDINAL, UBlocks.COCOA);
        setBlock(CARDINAL, UBlocks.DEADBUSH);
        setBlock(CARDINAL, UBlocks.BEACON);
        setBlock(CARDINAL, UBlocks.PISTON);
        setBlock(CARDINAL, UBlocks.PISTON_EXTENSION);
        setBlock(CARDINAL, UBlocks.PISTON_HEAD);
        setBlock(CARDINAL, UBlocks.DISPENSER);
        setBlock(CARDINAL, UBlocks.STANDING_BANNER);
        setBlock(CARDINAL, UBlocks.WALL_BANNER);
        setBlock(CARDINAL, UBlocks.STANDING_SIGN);
        setBlock(CARDINAL, UBlocks.WALL_SIGN);
        setBlock(CARDINAL, UBlocks.BED);
        setBlock(CARDINAL, UBlocks.DOUBLE_PLANT);
        setBlock(CARDINAL, UBlocks.WHEAT);
        setBlock(CARDINAL, UBlocks.MELON_BLOCK);
        setBlock(CARDINAL, UBlocks.TORCH);
        setBlock(CARDINAL, UBlocks.REDSTONE_TORCH);
        setBlock(CARDINAL, UBlocks.UNLIT_REDSTONE_TORCH);
        setBlock(CARDINAL, UBlocks.TNT);
        setBlock(CARDINAL, UBlocks.SAPLING);
        setBlock(CARDINAL, UBlocks.REDSTONE_LAMP);
        setBlock(CARDINAL, UBlocks.DAYLIGHT_DETECTOR);
        setBlock(CARDINAL, UBlocks.DAYLIGHT_DETECTOR_INVERTED);
        setBlock(CARDINAL, UBlocks.PUMPKIN);
        setBlock(CARDINAL, UBlocks.POTATOES);
        setBlock(CARDINAL, UBlocks.FLOWER_POT);
        setBlock(CARDINAL, UBlocks.LIT_PUMPKIN);
        setBlock(CARDINAL, UBlocks.FURNACE);
        setBlock(CARDINAL, UBlocks.LIT_FURNACE);
        setBlock(CARDINAL, UBlocks.IRON_TRAPDOOR);
        setBlock(CARDINAL, UBlocks.IRON_BARS);
        setBlock(CARDINAL, UBlocks.BOOKSHELF);
        setBlock(CARDINAL, UBlocks.ENCHANTING_TABLE);
        setBlock(CARDINAL, UBlocks.DROPPER);

        /* Cardinal blocks - stairs */
        setBlock(CARDINAL, UBlocks.ACACIA_STAIRS);
        setBlock(CARDINAL, UBlocks.SANDSTONE_STAIRS);
        setBlock(CARDINAL, UBlocks.BIRCH_STAIRS);
        setBlock(CARDINAL, UBlocks.SPRUCE_STAIRS);
        setBlock(CARDINAL, UBlocks.STONE_BRICK_STAIRS);
        setBlock(CARDINAL, UBlocks.STONE_STAIRS);
        setBlock(CARDINAL, UBlocks.BRICK_STAIRS);
        setBlock(CARDINAL, UBlocks.DARK_OAK_STAIRS);
        setBlock(CARDINAL, UBlocks.JUNGLE_STAIRS);
        setBlock(CARDINAL, UBlocks.NETHER_BRICK_STAIRS);
        setBlock(CARDINAL, UBlocks.OAK_STAIRS);
        setBlock(CARDINAL, UBlocks.DARK_OAK_STAIRS);
        setBlock(CARDINAL, UBlocks.PURPUR_STAIRS);
        setBlock(CARDINAL, UBlocks.RED_SANDSTONE_STAIRS);
        setBlock(CARDINAL, UBlocks.QUARTZ_STAIRS);

        /* Cardinal blocks - fence */
        setBlock(CARDINAL, UBlocks.ACACIA_FENCE);
        setBlock(CARDINAL, UBlocks.BIRCH_FENCE);
        setBlock(CARDINAL, UBlocks.SPRUCE_FENCE);
        setBlock(CARDINAL, UBlocks.DARK_OAK_FENCE);
        setBlock(CARDINAL, UBlocks.JUNGLE_FENCE);
        setBlock(CARDINAL, UBlocks.NETHER_BRICK_FENCE);
        setBlock(CARDINAL, UBlocks.OAK_FENCE);
        setBlock(CARDINAL, UBlocks.DARK_OAK_FENCE);
        setBlock(CARDINAL, UBlocks.ACACIA_FENCE_GATE);
        setBlock(CARDINAL, UBlocks.BIRCH_FENCE_GATE);
        setBlock(CARDINAL, UBlocks.SPRUCE_FENCE_GATE);
        setBlock(CARDINAL, UBlocks.DARK_OAK_FENCE_GATE);
        setBlock(CARDINAL, UBlocks.JUNGLE_FENCE_GATE);
        setBlock(CARDINAL, UBlocks.OAK_FENCE_GATE);
        setBlock(CARDINAL, UBlocks.DARK_OAK_FENCE_GATE);

        /* Set light emitters */
        setBlock(LIGHT, UBlocks.BEACON);
        setBlock(LIGHT, UBlocks.TORCH);
        setBlock(LIGHT, UBlocks.GLOWSTONE);
        setBlock(LIGHT, UBlocks.LAVA);
        setBlock(LIGHT, UBlocks.FIRE);
        setBlock(LIGHT, UBlocks.LIT_PUMPKIN);
        setBlock(LIGHT, UBlocks.END_PORTAL);
        setBlock(LIGHT, UBlocks.REDSTONE_LAMP);
        setBlock(LIGHT, UBlocks.LIT_FURNACE);
        setBlock(LIGHT, UBlocks.PORTAL);
        setBlock(LIGHT, UBlocks.END_PORTAL);
        setBlock(LIGHT, UBlocks.REDSTONE_ORE);
        setBlock(LIGHT, UBlocks.REDSTONE_TORCH);
        setBlock(LIGHT, UBlocks.BREWING_STAND);
        setBlock(LIGHT, UBlocks.DRAGON_EGG);
        setBlock(LIGHT, UBlocks.SEA_LANTERN);
        setBlock(LIGHT, UBlocks.END_ROD);
        setBlock(LIGHT, UBlocks.ENCHANTING_TABLE);
        setBlock(LIGHT, UBlocks.ENDER_CHEST);
        setBlock(LIGHT, UBlocks.MAGMA);
        setBlock(LIGHT, UBlocks.BROWN_MUSHROOM_BLOCK);
        setBlock(LIGHT, UBlocks.END_PORTAL_FRAME);

        /* Set gas */
        setBlock(GAS, UBlocks.AIR);

        for (int blockID = 0; blockID < 256; ++blockID) {
            UBlock block = UBlocks.getBlockVanilla(blockID);
            if (block == null) {
                continue;
            }
             /* HEAT_RAY - combination of liquid and overlook */
            if (isBlock(OVERLOOK, blockID) || isBlock(LIQUID, blockID)) {
                setBlock(HEAT_RAY, blockID);
            }
            /* SOP - combination of liquid and overlook */
            if (isBlock(GAS, blockID) || isBlock(LIQUID, blockID)) {
                setBlock(SOP, blockID);
            }
            /* TRANSPARENT - non solid blocks */
            if (block.getOpacity() < 16) {
                setBlock(TRANSPARENT, blockID);
            }
        }

        /* Set protected blocks */
        for (String exclusion : Configurator.PROTECT_BLOCKS) {
            try {
                Pattern ePattern = Pattern.compile(exclusion, Pattern.CASE_INSENSITIVE);
                for (UBlock block : UBlocks.selector.select(ePattern)) {
                    if (block != null && block.isValid()) {
                        setBlock(PROTECTED, block);
                    }
                }
            } catch (PatternSyntaxException pse) {
                new Report().post("BAD PATTERN", pse.getMessage()).print();
            }
        }

    }

}