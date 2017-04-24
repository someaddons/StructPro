package com.ternsip.structpro.Universe.Blocks;

import com.ternsip.structpro.Utils.IBlockState;
import net.minecraft.block.Block;

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
 * @author Ternsip
 * @since JDK 1.6
 */
@SuppressWarnings({"unused"})
public enum Classifier {

    SOIL (0x00),
    OVERLOOK (0x01),
    CARDINAL(0x02),
    LIQUID (0x03),
    LIGHT (0x04),
    HEAT_RAY(0x05),
    GAS(0x06),
    SOP(0x07),
    TRANSPARENT(0x8);

    private final int value;

    Classifier(int value) {
        this.value = value;
    }

    /** Ground soil blocks */
    private static final boolean[][] blocks = new boolean[Classifier.values().length][256];

    /**
     * Check if block of specific class by ID
     * @param type Classifier type
     * @param blockID Target block ID
     * @return If class block of type
     */
    public static boolean isBlock(Classifier type, int blockID) {
        return Blocks.isVanilla(blockID) && blocks[type.value][blockID];
    }

    /**
     * Check if block of specific class by ID
     * @param type Classifier type
     * @param block Target block
     * @return If class block of type
     */
    public static boolean isBlock(Classifier type, Block block) {
        return isBlock(type, Blocks.getID(block));
    }

    /**
     * Check if block of specific class by ID
     * @param type Classifier type
     * @param state Target block state
     * @return If class block of type
     */
    public static boolean isBlock(Classifier type, IBlockState state) {
        return isBlock(type, Blocks.getBlock(state));
    }

    /**
     * Set block class by block state
     * @param type Classifier type
     * @param state Target block state
     */
    private static void setBlock(Classifier type, IBlockState state) {
        setBlock(type, Blocks.getID(state));
    }

    /**
     * Set block class by block
     * @param type Classifier type
     * @param block Target block
     */
    private static void setBlock(Classifier type, Block block) {
        setBlock(type, Blocks.getID(block));
    }

    /**
     * Set block class by block ID
     * @param type Classifier type
     * @param blockID Target block ID
     */
    private static void setBlock(Classifier type, int blockID) {
        if (Blocks.isVanilla(blockID)) {
            blocks[type.value][blockID] = true;
        }
    }

    static {

         /* set SOIL Blocks */
        setBlock(SOIL, Blocks.grass);
        setBlock(SOIL, Blocks.dirt);
        setBlock(SOIL, Blocks.stone);
        setBlock(SOIL, Blocks.cobblestone);
        setBlock(SOIL, Blocks.sandstone);
        setBlock(SOIL, Blocks.netherrack);
        setBlock(SOIL, Blocks.gravel);
        setBlock(SOIL, Blocks.sand);
        setBlock(SOIL, Blocks.bedrock);
        setBlock(SOIL, Blocks.coal_ore);
        setBlock(SOIL, Blocks.iron_ore);
        setBlock(SOIL, Blocks.gold_ore);
        setBlock(SOIL, Blocks.diamond_ore);
        setBlock(SOIL, Blocks.redstone_ore);
        setBlock(SOIL, Blocks.lapis_ore);
        setBlock(SOIL, Blocks.emerald_ore);
        setBlock(SOIL, Blocks.lit_redstone_ore);
        setBlock(SOIL, Blocks.quartz_ore);
        setBlock(SOIL, Blocks.clay);
        setBlock(SOIL, Blocks.obsidian);

        /* set OVERLOOK Blocks */
        setBlock(OVERLOOK, Blocks.air);
        setBlock(OVERLOOK, Blocks.log);
        setBlock(OVERLOOK, Blocks.log2);
        setBlock(OVERLOOK, Blocks.leaves);
        setBlock(OVERLOOK, Blocks.leaves2);
        setBlock(OVERLOOK, Blocks.sapling);
        setBlock(OVERLOOK, Blocks.web);
        setBlock(OVERLOOK, Blocks.tallgrass);
        setBlock(OVERLOOK, Blocks.deadbush);
        setBlock(OVERLOOK, Blocks.yellow_flower);
        setBlock(OVERLOOK, Blocks.red_flower);
        setBlock(OVERLOOK, Blocks.red_mushroom_block);
        setBlock(OVERLOOK, Blocks.brown_mushroom_block);
        setBlock(OVERLOOK, Blocks.brown_mushroom);
        setBlock(OVERLOOK, Blocks.fire);
        setBlock(OVERLOOK, Blocks.wheat);
        setBlock(OVERLOOK, Blocks.snow_layer);
        setBlock(OVERLOOK, Blocks.snow);
        setBlock(OVERLOOK, Blocks.cactus);
        setBlock(OVERLOOK, Blocks.pumpkin);
        setBlock(OVERLOOK, Blocks.vine);
        setBlock(OVERLOOK, Blocks.cocoa);
        setBlock(OVERLOOK, Blocks.waterlily);
        setBlock(OVERLOOK, Blocks.double_plant);

        /* set LIQUIDs */
        setBlock(LIQUID, Blocks.water);
        setBlock(LIQUID, Blocks.flowing_water);
        setBlock(LIQUID, Blocks.ice);
        setBlock(LIQUID, Blocks.lava);
        setBlock(LIQUID, Blocks.flowing_lava);

        /* CARDINAL Blocks - doors */
        setBlock(CARDINAL, Blocks.trapdoor);
        setBlock(CARDINAL, Blocks.iron_door);

        /* CARDINAL Blocks - glass */
        setBlock(CARDINAL, Blocks.glass);
        setBlock(CARDINAL, Blocks.glass_pane);
        setBlock(CARDINAL, Blocks.stained_glass_pane);
        setBlock(CARDINAL, Blocks.stained_glass);

        /* CARDINAL Blocks - interior */
        setBlock(CARDINAL, Blocks.chest);
        setBlock(CARDINAL, Blocks.ender_chest);
        setBlock(CARDINAL, Blocks.trapped_chest);
        setBlock(CARDINAL, Blocks.flower_pot);
        setBlock(CARDINAL, Blocks.red_flower);
        setBlock(CARDINAL, Blocks.yellow_flower);
        setBlock(CARDINAL, Blocks.rail);
        setBlock(CARDINAL, Blocks.activator_rail);
        setBlock(CARDINAL, Blocks.detector_rail);
        setBlock(CARDINAL, Blocks.golden_rail);
        setBlock(CARDINAL, Blocks.redstone_wire);
        setBlock(CARDINAL, Blocks.tripwire);
        setBlock(CARDINAL, Blocks.tripwire_hook);
        setBlock(CARDINAL, Blocks.portal);
        setBlock(CARDINAL, Blocks.command_block);
        setBlock(CARDINAL, Blocks.powered_comparator);
        setBlock(CARDINAL, Blocks.unpowered_comparator);
        setBlock(CARDINAL, Blocks.dragon_egg);
        setBlock(CARDINAL, Blocks.hay_block);
        setBlock(CARDINAL, Blocks.nether_wart);
        setBlock(CARDINAL, Blocks.anvil);
        setBlock(CARDINAL, Blocks.crafting_table);
        setBlock(CARDINAL, Blocks.cactus);
        setBlock(CARDINAL, Blocks.jukebox);
        setBlock(CARDINAL, Blocks.cocoa);
        setBlock(CARDINAL, Blocks.deadbush);
        setBlock(CARDINAL, Blocks.beacon);
        setBlock(CARDINAL, Blocks.piston);
        setBlock(CARDINAL, Blocks.piston_extension);
        setBlock(CARDINAL, Blocks.piston_head);
        setBlock(CARDINAL, Blocks.dispenser);
        setBlock(CARDINAL, Blocks.standing_sign);
        setBlock(CARDINAL, Blocks.wall_sign);
        setBlock(CARDINAL, Blocks.bed);
        setBlock(CARDINAL, Blocks.double_plant);
        setBlock(CARDINAL, Blocks.wheat);
        setBlock(CARDINAL, Blocks.melon_block);
        setBlock(CARDINAL, Blocks.torch);
        setBlock(CARDINAL, Blocks.redstone_torch);
        setBlock(CARDINAL, Blocks.unlit_redstone_torch);
        setBlock(CARDINAL, Blocks.tnt);
        setBlock(CARDINAL, Blocks.sapling);
        setBlock(CARDINAL, Blocks.redstone_lamp);
        setBlock(CARDINAL, Blocks.daylight_detector);
        setBlock(CARDINAL, Blocks.pumpkin);
        setBlock(CARDINAL, Blocks.potatoes);
        setBlock(CARDINAL, Blocks.flower_pot);
        setBlock(CARDINAL, Blocks.lit_pumpkin);
        setBlock(CARDINAL, Blocks.furnace);
        setBlock(CARDINAL, Blocks.lit_furnace);
        setBlock(CARDINAL, Blocks.iron_bars);
        setBlock(CARDINAL, Blocks.bookshelf);
        setBlock(CARDINAL, Blocks.enchanting_table);
        setBlock(CARDINAL, Blocks.dropper);

        /* CARDINAL Blocks - stairs */
        setBlock(CARDINAL, Blocks.acacia_stairs);
        setBlock(CARDINAL, Blocks.sandstone_stairs);
        setBlock(CARDINAL, Blocks.birch_stairs);
        setBlock(CARDINAL, Blocks.spruce_stairs);
        setBlock(CARDINAL, Blocks.stone_brick_stairs);
        setBlock(CARDINAL, Blocks.stone_stairs);
        setBlock(CARDINAL, Blocks.brick_stairs);
        setBlock(CARDINAL, Blocks.dark_oak_stairs);
        setBlock(CARDINAL, Blocks.jungle_stairs);
        setBlock(CARDINAL, Blocks.nether_brick_stairs);
        setBlock(CARDINAL, Blocks.oak_stairs);
        setBlock(CARDINAL, Blocks.dark_oak_stairs);
        setBlock(CARDINAL, Blocks.quartz_stairs);

        /* CARDINAL Blocks - fence */
        setBlock(CARDINAL, Blocks.fence);
        setBlock(CARDINAL, Blocks.fence_gate);
        setBlock(CARDINAL, Blocks.nether_brick_fence);

        /* set LIGHT emitters */
        setBlock(LIGHT, Blocks.beacon);
        setBlock(LIGHT, Blocks.torch);
        setBlock(LIGHT, Blocks.glowstone);
        setBlock(LIGHT, Blocks.lava);
        setBlock(LIGHT, Blocks.fire);
        setBlock(LIGHT, Blocks.lit_pumpkin);
        setBlock(LIGHT, Blocks.end_portal);
        setBlock(LIGHT, Blocks.redstone_lamp);
        setBlock(LIGHT, Blocks.lit_furnace);
        setBlock(LIGHT, Blocks.portal);
        setBlock(LIGHT, Blocks.end_portal);
        setBlock(LIGHT, Blocks.redstone_ore);
        setBlock(LIGHT, Blocks.redstone_torch);
        setBlock(LIGHT, Blocks.brewing_stand);
        setBlock(LIGHT, Blocks.dragon_egg);
        setBlock(LIGHT, Blocks.enchanting_table);
        setBlock(LIGHT, Blocks.ender_chest);
        setBlock(LIGHT, Blocks.brown_mushroom_block);
        setBlock(LIGHT, Blocks.end_portal_frame);

        /* set GAS */
        setBlock(GAS, Blocks.air);

        for (int blockID = 0; blockID < 256; ++blockID) {
            Block block = Blocks.getBlockVanilla(blockID);
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
            if (Blocks.getOpacity(block) < 16) {
                setBlock(TRANSPARENT, blockID);
            }
        }

    }

}