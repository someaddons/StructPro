package com.ternsip.structpro.Universe.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

/*
* Block type classifier enumeration
* SOIL - ground soil blocks
* OVERLOOK - plants, stuff, web, fire, decorations, etc.
* CARDINAL - blocks with cardinal influence
* LIQUID - liquid blocks
* LIGHT - blocks that emits light
* HEAT_RAY - OVERLOOKS + LIQUID
* GAS - Air, gases, etc.
* SOP - GAS + LIQUID
*/
public enum Classifier {
    SOIL (0x00),
    OVERLOOK (0x01),
    CARDINAL(0x02),
    LIQUID (0x03),
    LIGHT (0x04),
    HEAT_RAY(0x05),
    GAS(0x06),
    SOP(0x07);

    private final int value;

    Classifier(int value) {
        this.value = value;
    }

    /* Ground soil blocks */
    private static final boolean[][] blocks = new boolean[Classifier.values().length][256];

    /* Check if block of specific class by id */
    public static boolean isBlock(Classifier type, int blockID) {
        return Blocks.isVanilla(blockID) && blocks[type.value][blockID];
    }

    /* Check if block of specific class by block */
    public static boolean isBlock(Classifier type, Block block) {
        return isBlock(type, Blocks.getID(block));
    }

    /* Check if block of specific class by state */
    public static boolean isBlock(Classifier type, IBlockState state) {
        return isBlock(type, Blocks.getBlock(state));
    }

    /* Set block class */
    private static void setBlock(Classifier type, Block block) {
        setBlock(type, Blocks.getID(block));
    }

    /* Set block class */
    private static void setBlock(Classifier type, int blockID) {
        if (Blocks.isVanilla(blockID)) {
            blocks[type.value][blockID] = true;
        }
    }

    static {

        /* Set soil blocks */
        setBlock(SOIL, Blocks.GRASS);
        setBlock(SOIL, Blocks.DIRT);
        setBlock(SOIL, Blocks.STONE);
        setBlock(SOIL, Blocks.COBBLESTONE);
        setBlock(SOIL, Blocks.SANDSTONE);
        setBlock(SOIL, Blocks.NETHERRACK);
        setBlock(SOIL, Blocks.GRAVEL);
        setBlock(SOIL, Blocks.SAND);
        setBlock(SOIL, Blocks.BEDROCK);
        setBlock(SOIL, Blocks.COAL_ORE);
        setBlock(SOIL, Blocks.IRON_ORE);
        setBlock(SOIL, Blocks.GOLD_ORE);
        setBlock(SOIL, Blocks.DIAMOND_ORE);
        setBlock(SOIL, Blocks.REDSTONE_ORE);
        setBlock(SOIL, Blocks.LAPIS_ORE);
        setBlock(SOIL, Blocks.EMERALD_ORE);
        setBlock(SOIL, Blocks.LIT_REDSTONE_ORE);
        setBlock(SOIL, Blocks.QUARTZ_ORE);
        setBlock(SOIL, Blocks.CLAY);
        setBlock(SOIL, Blocks.OBSIDIAN);

        /* Set overlook blocks */
        setBlock(OVERLOOK, Blocks.AIR);
        setBlock(OVERLOOK, Blocks.LOG);
        setBlock(OVERLOOK, Blocks.LOG2);
        setBlock(OVERLOOK, Blocks.LEAVES);
        setBlock(OVERLOOK, Blocks.LEAVES2);
        setBlock(OVERLOOK, Blocks.SAPLING);
        setBlock(OVERLOOK, Blocks.WEB);
        setBlock(OVERLOOK, Blocks.TALLGRASS);
        setBlock(OVERLOOK, Blocks.DEADBUSH);
        setBlock(OVERLOOK, Blocks.YELLOW_FLOWER);
        setBlock(OVERLOOK, Blocks.RED_FLOWER);
        setBlock(OVERLOOK, Blocks.RED_MUSHROOM_BLOCK);
        setBlock(OVERLOOK, Blocks.BROWN_MUSHROOM_BLOCK);
        setBlock(OVERLOOK, Blocks.BROWN_MUSHROOM);
        setBlock(OVERLOOK, Blocks.FIRE);
        setBlock(OVERLOOK, Blocks.WHEAT);
        setBlock(OVERLOOK, Blocks.SNOW_LAYER);
        setBlock(OVERLOOK, Blocks.SNOW);
        setBlock(OVERLOOK, Blocks.CACTUS);
        setBlock(OVERLOOK, Blocks.PUMPKIN);
        setBlock(OVERLOOK, Blocks.VINE);
        setBlock(OVERLOOK, Blocks.COCOA);
        setBlock(OVERLOOK, Blocks.WATERLILY);
        setBlock(OVERLOOK, Blocks.DOUBLE_PLANT);

        /* Set liquids */
        setBlock(LIQUID, Blocks.WATER);
        setBlock(LIQUID, Blocks.FLOWING_WATER);
        setBlock(LIQUID, Blocks.ICE);
        setBlock(LIQUID, Blocks.LAVA);
        setBlock(LIQUID, Blocks.FLOWING_LAVA);

        /* Cardinal blocks - doors */
        setBlock(CARDINAL, Blocks.TRAPDOOR);
        setBlock(CARDINAL, Blocks.IRON_TRAPDOOR);
        setBlock(CARDINAL, Blocks.SPRUCE_DOOR);
        setBlock(CARDINAL, Blocks.OAK_DOOR);
        setBlock(CARDINAL, Blocks.DARK_OAK_DOOR);
        setBlock(CARDINAL, Blocks.ACACIA_DOOR);
        setBlock(CARDINAL, Blocks.BIRCH_DOOR);
        setBlock(CARDINAL, Blocks.IRON_DOOR);
        setBlock(CARDINAL, Blocks.JUNGLE_DOOR);

        /* Cardinal blocks - glass */
        setBlock(CARDINAL, Blocks.GLASS);
        setBlock(CARDINAL, Blocks.GLASS_PANE);
        setBlock(CARDINAL, Blocks.STAINED_GLASS_PANE);
        setBlock(CARDINAL, Blocks.STAINED_GLASS);

        /* Cardinal blocks - interior */
        setBlock(CARDINAL, Blocks.CHEST);
        setBlock(CARDINAL, Blocks.ENDER_CHEST);
        setBlock(CARDINAL, Blocks.TRAPPED_CHEST);
        setBlock(CARDINAL, Blocks.FLOWER_POT);
        setBlock(CARDINAL, Blocks.CHORUS_FLOWER);
        setBlock(CARDINAL, Blocks.RED_FLOWER);
        setBlock(CARDINAL, Blocks.YELLOW_FLOWER);
        setBlock(CARDINAL, Blocks.RAIL);
        setBlock(CARDINAL, Blocks.ACTIVATOR_RAIL);
        setBlock(CARDINAL, Blocks.DETECTOR_RAIL);
        setBlock(CARDINAL, Blocks.GOLDEN_RAIL);
        setBlock(CARDINAL, Blocks.REDSTONE_WIRE);
        setBlock(CARDINAL, Blocks.TRIPWIRE);
        setBlock(CARDINAL, Blocks.TRIPWIRE_HOOK);
        setBlock(CARDINAL, Blocks.PORTAL);
        setBlock(CARDINAL, Blocks.COMMAND_BLOCK);
        setBlock(CARDINAL, Blocks.POWERED_COMPARATOR);
        setBlock(CARDINAL, Blocks.UNPOWERED_COMPARATOR);
        setBlock(CARDINAL, Blocks.DRAGON_EGG);
        setBlock(CARDINAL, Blocks.HAY_BLOCK);
        setBlock(CARDINAL, Blocks.NETHER_WART);
        setBlock(CARDINAL, Blocks.ANVIL);
        setBlock(CARDINAL, Blocks.CRAFTING_TABLE);
        setBlock(CARDINAL, Blocks.CACTUS);
        setBlock(CARDINAL, Blocks.JUKEBOX);
        setBlock(CARDINAL, Blocks.COCOA);
        setBlock(CARDINAL, Blocks.DEADBUSH);
        setBlock(CARDINAL, Blocks.BEACON);
        setBlock(CARDINAL, Blocks.PISTON);
        setBlock(CARDINAL, Blocks.PISTON_EXTENSION);
        setBlock(CARDINAL, Blocks.PISTON_HEAD);
        setBlock(CARDINAL, Blocks.DISPENSER);
        setBlock(CARDINAL, Blocks.STANDING_BANNER);
        setBlock(CARDINAL, Blocks.WALL_BANNER);
        setBlock(CARDINAL, Blocks.STANDING_SIGN);
        setBlock(CARDINAL, Blocks.WALL_SIGN);
        setBlock(CARDINAL, Blocks.BED);
        setBlock(CARDINAL, Blocks.DOUBLE_PLANT);
        setBlock(CARDINAL, Blocks.WHEAT);
        setBlock(CARDINAL, Blocks.MELON_BLOCK);
        setBlock(CARDINAL, Blocks.TORCH);
        setBlock(CARDINAL, Blocks.REDSTONE_TORCH);
        setBlock(CARDINAL, Blocks.UNLIT_REDSTONE_TORCH);
        setBlock(CARDINAL, Blocks.TNT);
        setBlock(CARDINAL, Blocks.SAPLING);
        setBlock(CARDINAL, Blocks.REDSTONE_LAMP);
        setBlock(CARDINAL, Blocks.DAYLIGHT_DETECTOR);
        setBlock(CARDINAL, Blocks.DAYLIGHT_DETECTOR_INVERTED);
        setBlock(CARDINAL, Blocks.PUMPKIN);
        setBlock(CARDINAL, Blocks.POTATOES);
        setBlock(CARDINAL, Blocks.FLOWER_POT);
        setBlock(CARDINAL, Blocks.LIT_PUMPKIN);
        setBlock(CARDINAL, Blocks.FURNACE);
        setBlock(CARDINAL, Blocks.LIT_FURNACE);
        setBlock(CARDINAL, Blocks.IRON_TRAPDOOR);
        setBlock(CARDINAL, Blocks.IRON_BARS);
        setBlock(CARDINAL, Blocks.BOOKSHELF);
        setBlock(CARDINAL, Blocks.ENCHANTING_TABLE);
        setBlock(CARDINAL, Blocks.DROPPER);

        /* Cardinal blocks - stairs */
        setBlock(CARDINAL, Blocks.ACACIA_STAIRS);
        setBlock(CARDINAL, Blocks.SANDSTONE_STAIRS);
        setBlock(CARDINAL, Blocks.BIRCH_STAIRS);
        setBlock(CARDINAL, Blocks.SPRUCE_STAIRS);
        setBlock(CARDINAL, Blocks.STONE_BRICK_STAIRS);
        setBlock(CARDINAL, Blocks.STONE_STAIRS);
        setBlock(CARDINAL, Blocks.BRICK_STAIRS);
        setBlock(CARDINAL, Blocks.DARK_OAK_STAIRS);
        setBlock(CARDINAL, Blocks.JUNGLE_STAIRS);
        setBlock(CARDINAL, Blocks.NETHER_BRICK_STAIRS);
        setBlock(CARDINAL, Blocks.OAK_STAIRS);
        setBlock(CARDINAL, Blocks.DARK_OAK_STAIRS);
        setBlock(CARDINAL, Blocks.PURPUR_STAIRS);
        setBlock(CARDINAL, Blocks.RED_SANDSTONE_STAIRS);
        setBlock(CARDINAL, Blocks.QUARTZ_STAIRS);

        /* Cardinal blocks - fence */
        setBlock(CARDINAL, Blocks.ACACIA_FENCE);
        setBlock(CARDINAL, Blocks.BIRCH_FENCE);
        setBlock(CARDINAL, Blocks.SPRUCE_FENCE);
        setBlock(CARDINAL, Blocks.DARK_OAK_FENCE);
        setBlock(CARDINAL, Blocks.JUNGLE_FENCE);
        setBlock(CARDINAL, Blocks.NETHER_BRICK_FENCE);
        setBlock(CARDINAL, Blocks.OAK_FENCE);
        setBlock(CARDINAL, Blocks.DARK_OAK_FENCE);
        setBlock(CARDINAL, Blocks.ACACIA_FENCE_GATE);
        setBlock(CARDINAL, Blocks.BIRCH_FENCE_GATE);
        setBlock(CARDINAL, Blocks.SPRUCE_FENCE_GATE);
        setBlock(CARDINAL, Blocks.DARK_OAK_FENCE_GATE);
        setBlock(CARDINAL, Blocks.JUNGLE_FENCE_GATE);
        setBlock(CARDINAL, Blocks.OAK_FENCE_GATE);
        setBlock(CARDINAL, Blocks.DARK_OAK_FENCE_GATE);

        /* Set light emitters */
        setBlock(LIGHT, Blocks.TORCH);
        setBlock(LIGHT, Blocks.GLOWSTONE);
        setBlock(LIGHT, Blocks.LAVA);
        setBlock(LIGHT, Blocks.FIRE);
        setBlock(LIGHT, Blocks.LIT_PUMPKIN);
        setBlock(LIGHT, Blocks.END_PORTAL);
        setBlock(LIGHT, Blocks.REDSTONE_LAMP);
        setBlock(LIGHT, Blocks.LIT_FURNACE);
        setBlock(LIGHT, Blocks.PORTAL);
        setBlock(LIGHT, Blocks.END_PORTAL);
        setBlock(LIGHT, Blocks.REDSTONE_ORE);
        setBlock(LIGHT, Blocks.REDSTONE_TORCH);
        setBlock(LIGHT, Blocks.BREWING_STAND);
        setBlock(LIGHT, Blocks.DRAGON_EGG);

        /* HEAT_RAY - combination of liquid and overlook */
        for (int blockID = 0; blockID < 256; ++blockID) {
            if (isBlock(OVERLOOK, blockID) || isBlock(LIQUID, blockID)) {
                setBlock(HEAT_RAY, blockID);
            }
        }

        /* Set gas */
        setBlock(GAS, Blocks.AIR);

        /* SOP - combination of liquid and overlook */
        for (int blockID = 0; blockID < 256; ++blockID) {
            if (isBlock(GAS, blockID) || isBlock(LIQUID, blockID)) {
                setBlock(SOP, blockID);
            }
        }

    }

}