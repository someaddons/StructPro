package com.ternsip.structpro.Universe.Blocks;

import com.ternsip.structpro.Logic.Configurator;
import com.ternsip.structpro.Utils.IBlockState;
import com.ternsip.structpro.Utils.Selector;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Blocks control class
 * Provide block transformation
 * @author Ternsip
 * @since JDK 1.6
 */
@SuppressWarnings({"WeakerAccess", "deprecation", "unused"})
public class Blocks extends net.minecraft.init.Blocks {

    /** Default vanilla blocks by classical indices */
    private static final Block[] blocks = new ArrayList<Block>() {{

        /* Construct default vanilla blocks */
        final Block[] vanilla = new Block[256];
        vanilla[0] = Blocks.air;
        vanilla[1] = Blocks.stone;
        vanilla[2] = Blocks.grass;
        vanilla[3] = Blocks.dirt;
        vanilla[4] = Blocks.cobblestone;
        vanilla[5] = Blocks.planks;
        vanilla[6] = Blocks.sapling;
        vanilla[7] = Blocks.bedrock;
        vanilla[8] = Blocks.flowing_water;
        vanilla[9] = Blocks.water;
        vanilla[10] = Blocks.flowing_lava;
        vanilla[11] = Blocks.lava;
        vanilla[12] = Blocks.sand;
        vanilla[13] = Blocks.gravel;
        vanilla[14] = Blocks.gold_ore;
        vanilla[15] = Blocks.iron_ore;
        vanilla[16] = Blocks.coal_ore;
        vanilla[17] = Blocks.log;
        vanilla[18] = Blocks.leaves;
        vanilla[19] = Blocks.sponge;
        vanilla[20] = Blocks.glass;
        vanilla[21] = Blocks.lapis_ore;
        vanilla[22] = Blocks.lapis_block;
        vanilla[23] = Blocks.dispenser;
        vanilla[24] = Blocks.sandstone;
        vanilla[25] = Blocks.noteblock;
        vanilla[26] = Blocks.bed;
        vanilla[27] = Blocks.golden_rail;
        vanilla[28] = Blocks.detector_rail;
        vanilla[29] = Blocks.sticky_piston;
        vanilla[30] = Blocks.web;
        vanilla[31] = Blocks.tallgrass;
        vanilla[32] = Blocks.deadbush;
        vanilla[33] = Blocks.piston;
        vanilla[34] = Blocks.piston_head;
        vanilla[35] = Blocks.wool;
        vanilla[36] = Blocks.piston_extension;
        vanilla[37] = Blocks.yellow_flower;
        vanilla[38] = Blocks.red_flower;
        vanilla[39] = Blocks.brown_mushroom;
        vanilla[40] = Blocks.red_mushroom;
        vanilla[41] = Blocks.gold_block;
        vanilla[42] = Blocks.iron_block;
        vanilla[43] = Blocks.double_stone_slab;
        vanilla[44] = Blocks.stone_slab;
        vanilla[45] = Blocks.brick_block;
        vanilla[46] = Blocks.tnt;
        vanilla[47] = Blocks.bookshelf;
        vanilla[48] = Blocks.mossy_cobblestone;
        vanilla[49] = Blocks.obsidian;
        vanilla[50] = Blocks.torch;
        vanilla[51] = Blocks.fire;
        vanilla[52] = Blocks.mob_spawner;
        vanilla[53] = Blocks.oak_stairs;
        vanilla[54] = Blocks.chest;
        vanilla[55] = Blocks.redstone_wire;
        vanilla[56] = Blocks.diamond_ore;
        vanilla[57] = Blocks.diamond_block;
        vanilla[58] = Blocks.crafting_table;
        vanilla[59] = Blocks.wheat;
        vanilla[60] = Blocks.farmland;
        vanilla[61] = Blocks.furnace;
        vanilla[62] = Blocks.lit_furnace;
        vanilla[63] = Blocks.standing_sign;
        vanilla[64] = Blocks.wooden_door;
        vanilla[65] = Blocks.ladder;
        vanilla[66] = Blocks.rail;
        vanilla[67] = Blocks.stone_stairs;
        vanilla[68] = Blocks.wall_sign;
        vanilla[69] = Blocks.lever;
        vanilla[70] = Blocks.stone_pressure_plate;
        vanilla[71] = Blocks.iron_door;
        vanilla[72] = Blocks.wooden_pressure_plate;
        vanilla[73] = Blocks.redstone_ore;
        vanilla[74] = Blocks.lit_redstone_ore;
        vanilla[75] = Blocks.unlit_redstone_torch;
        vanilla[76] = Blocks.redstone_torch;
        vanilla[77] = Blocks.stone_button;
        vanilla[78] = Blocks.snow_layer;
        vanilla[79] = Blocks.ice;
        vanilla[80] = Blocks.snow;
        vanilla[81] = Blocks.cactus;
        vanilla[82] = Blocks.clay;
        vanilla[83] = Blocks.reeds;
        vanilla[84] = Blocks.jukebox;
        vanilla[85] = Blocks.fence;
        vanilla[86] = Blocks.pumpkin;
        vanilla[87] = Blocks.netherrack;
        vanilla[88] = Blocks.soul_sand;
        vanilla[89] = Blocks.glowstone;
        vanilla[90] = Blocks.portal;
        vanilla[91] = Blocks.lit_pumpkin;
        vanilla[92] = Blocks.cake;
        vanilla[93] = Blocks.unpowered_repeater;
        vanilla[94] = Blocks.powered_repeater;
        vanilla[95] = Blocks.stained_glass;
        vanilla[96] = Blocks.trapdoor;
        vanilla[97] = Blocks.monster_egg;
        vanilla[98] = Blocks.stonebrick;
        vanilla[99] = Blocks.brown_mushroom_block;
        vanilla[100] = Blocks.red_mushroom_block;
        vanilla[101] = Blocks.iron_bars;
        vanilla[102] = Blocks.glass_pane;
        vanilla[103] = Blocks.melon_block;
        vanilla[104] = Blocks.pumpkin_stem;
        vanilla[105] = Blocks.melon_stem;
        vanilla[106] = Blocks.vine;
        vanilla[107] = Blocks.fence_gate;
        vanilla[108] = Blocks.brick_stairs;
        vanilla[109] = Blocks.stone_brick_stairs;
        vanilla[110] = Blocks.mycelium;
        vanilla[111] = Blocks.waterlily;
        vanilla[112] = Blocks.nether_brick;
        vanilla[113] = Blocks.nether_brick_fence;
        vanilla[114] = Blocks.nether_brick_stairs;
        vanilla[115] = Blocks.nether_wart;
        vanilla[116] = Blocks.enchanting_table;
        vanilla[117] = Blocks.brewing_stand;
        vanilla[118] = Blocks.cauldron;
        vanilla[119] = Blocks.end_portal;
        vanilla[120] = Blocks.end_portal_frame;
        vanilla[121] = Blocks.end_stone;
        vanilla[122] = Blocks.dragon_egg;
        vanilla[123] = Blocks.redstone_lamp;
        vanilla[124] = Blocks.lit_redstone_lamp;
        vanilla[125] = Blocks.double_wooden_slab;
        vanilla[126] = Blocks.wooden_slab;
        vanilla[127] = Blocks.cocoa;
        vanilla[128] = Blocks.sandstone_stairs;
        vanilla[129] = Blocks.emerald_ore;
        vanilla[130] = Blocks.ender_chest;
        vanilla[131] = Blocks.tripwire_hook;
        vanilla[132] = Blocks.tripwire;
        vanilla[133] = Blocks.emerald_block;
        vanilla[134] = Blocks.spruce_stairs;
        vanilla[135] = Blocks.birch_stairs;
        vanilla[136] = Blocks.jungle_stairs;
        vanilla[137] = Blocks.command_block;
        vanilla[138] = Blocks.beacon;
        vanilla[139] = Blocks.cobblestone_wall;
        vanilla[140] = Blocks.flower_pot;
        vanilla[141] = Blocks.carrots;
        vanilla[142] = Blocks.potatoes;
        vanilla[143] = Blocks.wooden_button;
        vanilla[144] = Blocks.skull;
        vanilla[145] = Blocks.anvil;
        vanilla[146] = Blocks.trapped_chest;
        vanilla[147] = Blocks.light_weighted_pressure_plate;
        vanilla[148] = Blocks.heavy_weighted_pressure_plate;
        vanilla[149] = Blocks.unpowered_comparator;
        vanilla[150] = Blocks.powered_comparator;
        vanilla[151] = Blocks.daylight_detector;
        vanilla[152] = Blocks.redstone_block;
        vanilla[153] = Blocks.quartz_ore;
        vanilla[154] = Blocks.hopper;
        vanilla[155] = Blocks.quartz_block;
        vanilla[156] = Blocks.quartz_stairs;
        vanilla[157] = Blocks.activator_rail;
        vanilla[158] = Blocks.dropper;
        vanilla[159] = Blocks.stained_hardened_clay;
        vanilla[160] = Blocks.stained_glass_pane;
        vanilla[161] = Blocks.leaves2;
        vanilla[162] = Blocks.log2;
        vanilla[163] = Blocks.acacia_stairs;
        vanilla[164] = Blocks.dark_oak_stairs;
        vanilla[165] = null;
        vanilla[166] = null;
        vanilla[167] = null;
        vanilla[168] = null;
        vanilla[169] = null;
        vanilla[170] = Blocks.hay_block;
        vanilla[171] = Blocks.carpet;
        vanilla[172] = Blocks.hardened_clay;
        vanilla[173] = Blocks.coal_block;
        vanilla[174] = Blocks.packed_ice;
        vanilla[175] = Blocks.double_plant;
        vanilla[176] = null;
        vanilla[177] = null;
        vanilla[178] = null;
        vanilla[179] = null;
        vanilla[180] = null;
        vanilla[181] = null;
        vanilla[182] = null;
        vanilla[183] = null;
        vanilla[184] = null;
        vanilla[185] = null;
        vanilla[186] = null;
        vanilla[187] = null;
        vanilla[188] = null;
        vanilla[189] = null;
        vanilla[190] = null;
        vanilla[191] = null;
        vanilla[192] = null;
        vanilla[193] = null;
        vanilla[194] = null;
        vanilla[195] = null;
        vanilla[196] = null;
        vanilla[197] = null;
        vanilla[198] = null;
        vanilla[199] = null;
        vanilla[200] = null;
        vanilla[201] = null;
        vanilla[202] = null;
        vanilla[203] = null;
        vanilla[204] = null;
        vanilla[205] = null;
        vanilla[206] = null;
        vanilla[207] = null;
        vanilla[208] = null;
        vanilla[209] = null;
        vanilla[210] = null;
        vanilla[211] = null;
        vanilla[212] = null;
        vanilla[213] = null;
        vanilla[214] = null;
        vanilla[215] = null;
        vanilla[216] = null;
        vanilla[217] = null;
        vanilla[218] = null;
        vanilla[219] = null;
        vanilla[220] = null;
        vanilla[221] = null;
        vanilla[222] = null;
        vanilla[223] = null;
        vanilla[224] = null;
        vanilla[225] = null;
        vanilla[226] = null;
        vanilla[227] = null;
        vanilla[228] = null;
        vanilla[229] = null;
        vanilla[230] = null;
        vanilla[231] = null;
        vanilla[232] = null;
        vanilla[233] = null;
        vanilla[234] = null;
        vanilla[235] = null;
        vanilla[236] = null;
        vanilla[237] = null;
        vanilla[238] = null;
        vanilla[239] = null;
        vanilla[240] = null;
        vanilla[241] = null;
        vanilla[242] = null;
        vanilla[243] = null;
        vanilla[244] = null;
        vanilla[245] = null;
        vanilla[246] = null;
        vanilla[247] = null;
        vanilla[248] = null;
        vanilla[249] = null;
        vanilla[250] = null;
        vanilla[251] = null;
        vanilla[252] = null;
        vanilla[253] = null;
        vanilla[254] = null;
        vanilla[255] = null;

        /* Construct block replaces */
        final HashMap<Block, Block> replace = new HashMap<Block, Block>() {{
            Selector<Block> names = new Selector<Block>() {{
                for (Block block : vanilla) {
                    if (block != null && block.getUnlocalizedName() != null) {
                        add(block.getUnlocalizedName().replace("tile.", ""), block);
                    }
                }
                add("NULL", null);
            }};
            for (String name : Configurator.REPLACE_BLOCKS) {
                List<String> tokens = Utils.tokenize(name, "->");
                if (tokens.size() == 2) {
                    try {
                        Pattern srcPattern = Pattern.compile(tokens.get(0), Pattern.CASE_INSENSITIVE);
                        Pattern dstPattern = Pattern.compile(tokens.get(1), Pattern.CASE_INSENSITIVE);
                        for (Block blockSrc : names.select(srcPattern)) {
                            for (Block blockDst : names.select(dstPattern)) {
                                put(blockSrc, blockDst);
                            }
                        }
                    } catch (PatternSyntaxException pse) {
                        new Report().post("BAD PATTERN", pse.getMessage()).print();
                    }
                }
            }
        }};
        /* Add all blocks */
        for (Block block : vanilla) {
            add(block == null || !replace.containsKey(block) ? block : replace.get(block));
        }
    }}.toArray(new Block[256]);

    /**
     * Check if the block have vanilla index
     * @param blockID Block index
     * @return True if block has vanilla ID
     */
    public static boolean isVanilla(int blockID) {
        return blockID >= 0 && blockID < 256;
    }

    /**
     * Block id from block
     * @param block Target block
     * @return Block index
     */
    public static int getID(Block block) {
        return Block.getIdFromBlock(block);
    }

    /**
     * Vanilla block from block id
     * @param blockID Block index
     * @return Minecraft native block or null
     */
    public static Block getBlockVanilla(int blockID) {
        return isVanilla(blockID) ? blocks[blockID] : null;
    }

    /**
     * Get block from block id
     * @param blockID Block index
     * @return Converted block
     */
    public static Block getBlock(int blockID) {
        return Block.getBlockById(blockID);
    }

    /**
     * Get block from block state
     * @param state Block state
     * @return Extracted block
     */
    public static Block getBlock(IBlockState state) {
        return state.getBlock();
    }

    /**
     * Get Block id from block state
     * @param state Block state
     * @return Block index
     */
    public static int getID(IBlockState state) {
        return getID(getBlock(state));
    }

    /**
     * Get Block state from block
     * @param block Target block
     * @return Block state
     */
    public static IBlockState getState(Block block) {
        return new IBlockState(block, 0);
    }

    /**
     * Get Block metadata from block state
     * @param state Target block state
     * @return index
     */
    public static int getMeta(IBlockState state) {
        return state.getMeta();
    }

    /**
     * Get Block state from block and metadata
     * @param block Target block
     * @param meta Block metadata
     * @return Converted block state
     */
    public static IBlockState getState(Block block, int meta) {
        return new IBlockState(block, meta);
    }

    /**
     * Get light level that emits block state
     * @param state Target block state
     * @return block light value
     */
    public static int getLight(IBlockState state) {
        return getLight(getBlock(state));
    }

    /**
     * Get opacity of block state
     * @param state Target block state
     * @return Block opacity level
     */
    public static int getOpacity(IBlockState state) {
        return getOpacity(getBlock(state));
    }

    /**
     * Get light level that emits block
     * @param block Target block
     * @return block light value
     */
    public static int getLight(Block block) {
        return block.getLightValue();
    }

    /**
     * Get light level that emits block
     * @param block Target block
     * @return Block opacity level
     */
    public static int getOpacity(Block block) {
        return block.getLightOpacity();
    }

}
