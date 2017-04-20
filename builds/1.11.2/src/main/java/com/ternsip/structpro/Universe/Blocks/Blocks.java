package com.ternsip.structpro.Universe.Blocks;

import com.ternsip.structpro.Logic.Configurator;
import com.ternsip.structpro.Utils.Selector;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/* Blocks control class */
@SuppressWarnings({"WeakerAccess", "deprecation"})
public class Blocks extends net.minecraft.init.Blocks {

    /* Default vanilla blocks by classical indices */
    private static final Block[] blocks = new ArrayList<Block>() {{

        /* Construct default vanilla blocks */
        final Block[] vanilla = new Block[256];
        vanilla[0] = Blocks.AIR;
        vanilla[1] = Blocks.STONE;
        vanilla[2] = Blocks.GRASS;
        vanilla[3] = Blocks.DIRT;
        vanilla[4] = Blocks.COBBLESTONE;
        vanilla[5] = Blocks.PLANKS;
        vanilla[6] = Blocks.SAPLING;
        vanilla[7] = Blocks.BEDROCK;
        vanilla[8] = Blocks.FLOWING_WATER;
        vanilla[9] = Blocks.WATER;
        vanilla[10] = Blocks.FLOWING_LAVA;
        vanilla[11] = Blocks.LAVA;
        vanilla[12] = Blocks.SAND;
        vanilla[13] = Blocks.GRAVEL;
        vanilla[14] = Blocks.GOLD_ORE;
        vanilla[15] = Blocks.IRON_ORE;
        vanilla[16] = Blocks.COAL_ORE;
        vanilla[17] = Blocks.LOG;
        vanilla[18] = Blocks.LEAVES;
        vanilla[19] = Blocks.SPONGE;
        vanilla[20] = Blocks.GLASS;
        vanilla[21] = Blocks.LAPIS_ORE;
        vanilla[22] = Blocks.LAPIS_BLOCK;
        vanilla[23] = Blocks.DISPENSER;
        vanilla[24] = Blocks.SANDSTONE;
        vanilla[25] = Blocks.NOTEBLOCK;
        vanilla[26] = Blocks.BED;
        vanilla[27] = Blocks.GOLDEN_RAIL;
        vanilla[28] = Blocks.DETECTOR_RAIL;
        vanilla[29] = Blocks.STICKY_PISTON;
        vanilla[30] = Blocks.WEB;
        vanilla[31] = Blocks.TALLGRASS;
        vanilla[32] = Blocks.DEADBUSH;
        vanilla[33] = Blocks.PISTON;
        vanilla[34] = Blocks.PISTON_HEAD;
        vanilla[35] = Blocks.WOOL;
        vanilla[36] = Blocks.PISTON_EXTENSION;
        vanilla[37] = Blocks.YELLOW_FLOWER;
        vanilla[38] = Blocks.RED_FLOWER;
        vanilla[39] = Blocks.BROWN_MUSHROOM;
        vanilla[40] = Blocks.RED_MUSHROOM;
        vanilla[41] = Blocks.GOLD_BLOCK;
        vanilla[42] = Blocks.IRON_BLOCK;
        vanilla[43] = Blocks.DOUBLE_STONE_SLAB;
        vanilla[44] = Blocks.STONE_SLAB;
        vanilla[45] = Blocks.BRICK_BLOCK;
        vanilla[46] = Blocks.TNT;
        vanilla[47] = Blocks.BOOKSHELF;
        vanilla[48] = Blocks.MOSSY_COBBLESTONE;
        vanilla[49] = Blocks.OBSIDIAN;
        vanilla[50] = Blocks.TORCH;
        vanilla[51] = Blocks.FIRE;
        vanilla[52] = Blocks.MOB_SPAWNER;
        vanilla[53] = Blocks.OAK_STAIRS;
        vanilla[54] = Blocks.CHEST;
        vanilla[55] = Blocks.REDSTONE_WIRE;
        vanilla[56] = Blocks.DIAMOND_ORE;
        vanilla[57] = Blocks.DIAMOND_BLOCK;
        vanilla[58] = Blocks.CRAFTING_TABLE;
        vanilla[59] = Blocks.WHEAT;
        vanilla[60] = Blocks.FARMLAND;
        vanilla[61] = Blocks.FURNACE;
        vanilla[62] = Blocks.LIT_FURNACE;
        vanilla[63] = Blocks.STANDING_SIGN;
        vanilla[64] = Blocks.OAK_DOOR;
        vanilla[65] = Blocks.LADDER;
        vanilla[66] = Blocks.RAIL;
        vanilla[67] = Blocks.STONE_STAIRS;
        vanilla[68] = Blocks.WALL_SIGN;
        vanilla[69] = Blocks.LEVER;
        vanilla[70] = Blocks.STONE_PRESSURE_PLATE;
        vanilla[71] = Blocks.IRON_DOOR;
        vanilla[72] = Blocks.WOODEN_PRESSURE_PLATE;
        vanilla[73] = Blocks.REDSTONE_ORE;
        vanilla[74] = Blocks.LIT_REDSTONE_ORE;
        vanilla[75] = Blocks.UNLIT_REDSTONE_TORCH;
        vanilla[76] = Blocks.REDSTONE_TORCH;
        vanilla[77] = Blocks.STONE_BUTTON;
        vanilla[78] = Blocks.SNOW_LAYER;
        vanilla[79] = Blocks.ICE;
        vanilla[80] = Blocks.SNOW;
        vanilla[81] = Blocks.CACTUS;
        vanilla[82] = Blocks.CLAY;
        vanilla[83] = Blocks.REEDS;
        vanilla[84] = Blocks.JUKEBOX;
        vanilla[85] = Blocks.OAK_FENCE;
        vanilla[86] = Blocks.PUMPKIN;
        vanilla[87] = Blocks.NETHERRACK;
        vanilla[88] = Blocks.SOUL_SAND;
        vanilla[89] = Blocks.GLOWSTONE;
        vanilla[90] = Blocks.PORTAL;
        vanilla[91] = Blocks.LIT_PUMPKIN;
        vanilla[92] = Blocks.CAKE;
        vanilla[93] = Blocks.UNPOWERED_REPEATER;
        vanilla[94] = Blocks.POWERED_REPEATER;
        vanilla[95] = Blocks.STAINED_GLASS;
        vanilla[96] = Blocks.TRAPDOOR;
        vanilla[97] = Blocks.MONSTER_EGG;
        vanilla[98] = Blocks.STONEBRICK;
        vanilla[99] = Blocks.BROWN_MUSHROOM_BLOCK;
        vanilla[100] = Blocks.RED_MUSHROOM_BLOCK;
        vanilla[101] = Blocks.IRON_BARS;
        vanilla[102] = Blocks.GLASS_PANE;
        vanilla[103] = Blocks.MELON_BLOCK;
        vanilla[104] = Blocks.PUMPKIN_STEM;
        vanilla[105] = Blocks.MELON_STEM;
        vanilla[106] = Blocks.VINE;
        vanilla[107] = Blocks.OAK_FENCE_GATE;
        vanilla[108] = Blocks.BRICK_STAIRS;
        vanilla[109] = Blocks.STONE_BRICK_STAIRS;
        vanilla[110] = Blocks.MYCELIUM;
        vanilla[111] = Blocks.WATERLILY;
        vanilla[112] = Blocks.NETHER_BRICK;
        vanilla[113] = Blocks.NETHER_BRICK_FENCE;
        vanilla[114] = Blocks.NETHER_BRICK_STAIRS;
        vanilla[115] = Blocks.NETHER_WART;
        vanilla[116] = Blocks.ENCHANTING_TABLE;
        vanilla[117] = Blocks.BREWING_STAND;
        vanilla[118] = Blocks.CAULDRON;
        vanilla[119] = Blocks.END_PORTAL;
        vanilla[120] = Blocks.END_PORTAL_FRAME;
        vanilla[121] = Blocks.END_STONE;
        vanilla[122] = Blocks.DRAGON_EGG;
        vanilla[123] = Blocks.REDSTONE_LAMP;
        vanilla[124] = Blocks.LIT_REDSTONE_LAMP;
        vanilla[125] = Blocks.DOUBLE_WOODEN_SLAB;
        vanilla[126] = Blocks.WOODEN_SLAB;
        vanilla[127] = Blocks.COCOA;
        vanilla[128] = Blocks.SANDSTONE_STAIRS;
        vanilla[129] = Blocks.EMERALD_ORE;
        vanilla[130] = Blocks.ENDER_CHEST;
        vanilla[131] = Blocks.TRIPWIRE_HOOK;
        vanilla[132] = Blocks.TRIPWIRE;
        vanilla[133] = Blocks.EMERALD_BLOCK;
        vanilla[134] = Blocks.SPRUCE_STAIRS;
        vanilla[135] = Blocks.BIRCH_STAIRS;
        vanilla[136] = Blocks.JUNGLE_STAIRS;
        vanilla[137] = Blocks.COMMAND_BLOCK;
        vanilla[138] = Blocks.BEACON;
        vanilla[139] = Blocks.COBBLESTONE_WALL;
        vanilla[140] = Blocks.FLOWER_POT;
        vanilla[141] = Blocks.CARROTS;
        vanilla[142] = Blocks.POTATOES;
        vanilla[143] = Blocks.WOODEN_BUTTON;
        vanilla[144] = Blocks.SKULL;
        vanilla[145] = Blocks.ANVIL;
        vanilla[146] = Blocks.TRAPPED_CHEST;
        vanilla[147] = Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE;
        vanilla[148] = Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE;
        vanilla[149] = Blocks.UNPOWERED_COMPARATOR;
        vanilla[150] = Blocks.POWERED_COMPARATOR;
        vanilla[151] = Blocks.DAYLIGHT_DETECTOR;
        vanilla[152] = Blocks.REDSTONE_BLOCK;
        vanilla[153] = Blocks.QUARTZ_ORE;
        vanilla[154] = Blocks.HOPPER;
        vanilla[155] = Blocks.QUARTZ_BLOCK;
        vanilla[156] = Blocks.QUARTZ_STAIRS;
        vanilla[157] = Blocks.ACTIVATOR_RAIL;
        vanilla[158] = Blocks.DROPPER;
        vanilla[159] = Blocks.STAINED_HARDENED_CLAY;
        vanilla[160] = Blocks.STAINED_GLASS_PANE;
        vanilla[161] = Blocks.LEAVES2;
        vanilla[162] = Blocks.LOG2;
        vanilla[163] = Blocks.ACACIA_STAIRS;
        vanilla[164] = Blocks.DARK_OAK_STAIRS;
        vanilla[165] = Blocks.SLIME_BLOCK;
        vanilla[166] = Blocks.BARRIER;
        vanilla[167] = Blocks.IRON_TRAPDOOR;
        vanilla[168] = Blocks.PRISMARINE;
        vanilla[169] = Blocks.SEA_LANTERN;
        vanilla[170] = Blocks.HAY_BLOCK;
        vanilla[171] = Blocks.CARPET;
        vanilla[172] = Blocks.HARDENED_CLAY;
        vanilla[173] = Blocks.COAL_BLOCK;
        vanilla[174] = Blocks.PACKED_ICE;
        vanilla[175] = Blocks.DOUBLE_PLANT;
        vanilla[176] = Blocks.STANDING_BANNER;
        vanilla[177] = Blocks.WALL_BANNER;
        vanilla[178] = Blocks.DAYLIGHT_DETECTOR_INVERTED;
        vanilla[179] = Blocks.RED_SANDSTONE;
        vanilla[180] = Blocks.RED_SANDSTONE_STAIRS;
        vanilla[181] = Blocks.DOUBLE_STONE_SLAB2;
        vanilla[182] = Blocks.STONE_SLAB2;
        vanilla[183] = Blocks.SPRUCE_FENCE_GATE;
        vanilla[184] = Blocks.BIRCH_FENCE_GATE;
        vanilla[185] = Blocks.JUNGLE_FENCE_GATE;
        vanilla[186] = Blocks.DARK_OAK_FENCE_GATE;
        vanilla[187] = Blocks.ACACIA_FENCE_GATE;
        vanilla[188] = Blocks.SPRUCE_FENCE;
        vanilla[189] = Blocks.BIRCH_FENCE;
        vanilla[190] = Blocks.JUNGLE_FENCE;
        vanilla[191] = Blocks.DARK_OAK_FENCE;
        vanilla[192] = Blocks.ACACIA_FENCE;
        vanilla[193] = Blocks.SPRUCE_DOOR;
        vanilla[194] = Blocks.BIRCH_DOOR;
        vanilla[195] = Blocks.JUNGLE_DOOR;
        vanilla[196] = Blocks.ACACIA_DOOR;
        vanilla[197] = Blocks.DARK_OAK_DOOR;
        vanilla[198] = Blocks.END_ROD;
        vanilla[199] = Blocks.CHORUS_PLANT;
        vanilla[200] = Blocks.CHORUS_FLOWER;
        vanilla[201] = Blocks.PURPUR_BLOCK;
        vanilla[202] = Blocks.PURPUR_PILLAR;
        vanilla[203] = Blocks.PURPUR_STAIRS;
        vanilla[204] = Blocks.PURPUR_DOUBLE_SLAB;
        vanilla[205] = Blocks.PURPUR_SLAB;
        vanilla[206] = Blocks.END_BRICKS;
        vanilla[207] = Blocks.BEETROOTS;
        vanilla[208] = Blocks.GRASS_PATH;
        vanilla[209] = Blocks.END_GATEWAY;
        vanilla[210] = Blocks.REPEATING_COMMAND_BLOCK;
        vanilla[211] = Blocks.CHAIN_COMMAND_BLOCK;
        vanilla[212] = Blocks.FROSTED_ICE;
        vanilla[213] = Blocks.MAGMA;
        vanilla[214] = Blocks.NETHER_WART_BLOCK;
        vanilla[215] = Blocks.RED_NETHER_BRICK;
        vanilla[216] = Blocks.BONE_BLOCK;
        vanilla[217] = Blocks.STRUCTURE_VOID;
        vanilla[218] = Blocks.OBSERVER;
        vanilla[219] = Blocks.WHITE_SHULKER_BOX;
        vanilla[220] = Blocks.ORANGE_SHULKER_BOX;
        vanilla[221] = Blocks.MAGENTA_SHULKER_BOX;
        vanilla[222] = Blocks.LIGHT_BLUE_SHULKER_BOX;
        vanilla[223] = Blocks.YELLOW_SHULKER_BOX;
        vanilla[224] = Blocks.LIME_SHULKER_BOX;
        vanilla[225] = Blocks.PINK_SHULKER_BOX;
        vanilla[226] = Blocks.GRAY_SHULKER_BOX;
        vanilla[227] = Blocks.SILVER_SHULKER_BOX;
        vanilla[228] = Blocks.CYAN_SHULKER_BOX;
        vanilla[229] = Blocks.PURPLE_SHULKER_BOX;
        vanilla[230] = Blocks.BLUE_SHULKER_BOX;
        vanilla[231] = Blocks.BROWN_SHULKER_BOX;
        vanilla[232] = Blocks.GREEN_SHULKER_BOX;
        vanilla[233] = Blocks.RED_SHULKER_BOX;
        vanilla[234] = Blocks.BLACK_SHULKER_BOX;
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
        vanilla[255] = Blocks.STRUCTURE_BLOCK;

        /* Construct block replaces */
        final HashMap<Block, Block> replace = new HashMap<Block, Block>() {{
            Selector<Block> names = new Selector<Block>() {{
                for (Block block : vanilla) {
                    if (block != null && block.getRegistryName() != null) {
                        add(block.getRegistryName().getResourcePath(), block);
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

    /* Check if the block have vanilla index */
    public static boolean isVanilla(int blockID) {
        return blockID >= 0 && blockID < 256;
    }

    /* Block id from block*/
    public static int getID(Block block) {
        return Block.getIdFromBlock(block);
    }

    /* Block from block id */
    public static Block getBlockVanilla(int blockID) {
        return isVanilla(blockID) ? blocks[blockID] : null;
    }

    /* Block from block id */
    public static Block getBlock(int blockID) {
        return Block.getBlockById(blockID);
    }

    /* Block from block state */
    public static Block getBlock(IBlockState state) {
        return state.getBlock();
    }

    /* Block id from block state */
    public static int getID(IBlockState block) {
        return getID(getBlock(block));
    }

    /* Block state from block */
    public static IBlockState state(Block block) {
        return block.getDefaultState();
    }

    /* Block metadata from block state */
    public static int getMeta(IBlockState state) {
        return state.getBlock().getMetaFromState(state);
    }

    /* Block state from block and metadata */
    public static IBlockState state(Block block, int meta) {
        IBlockState result = state(block);
        try {
            result = block.getStateFromMeta(meta);
        } catch (Throwable ignored) {}
        return result;
    }

    /* Get light level that emits block state */
    public static int getLight(IBlockState state) {
        return state.getLightValue();
    }

    /* Get opacity of block state */
    public static int getOpacity(IBlockState state) {
        return state.getLightOpacity();
    }

    /* Get light level that emits block */
    public static int getLight(Block block) {
        return state(block).getLightValue();
    }

    /* Get opacity of block */
    public static int getOpacity(Block block) {
        return state(block).getLightOpacity();
    }

}
