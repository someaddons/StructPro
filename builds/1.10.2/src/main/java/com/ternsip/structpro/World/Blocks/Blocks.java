package com.ternsip.structpro.World.Blocks;

import com.ternsip.structpro.Logic.Selector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

/* Blocks control class */
@SuppressWarnings({"WeakerAccess", "deprecation"})
public class Blocks extends net.minecraft.init.Blocks {

    /* Cardinal blocks skinning radius */
    public static double cardinalRadius = 3;

    /* Default vanilla blocks by classical indices */
    private static Block[] blocks = new Block[256];

    /* Blocks that have to be replaced one time to another block */
    private static int [] replaces = new int[256];

    /* Blocks names selector */
    private static Selector<Block> names = new Selector<Block>();

    /* Check if the block have vanilla index */
    public static boolean isVanillaID(int blockID) {
        return blockID >= 0 && blockID < 256;
    }

    /* Block id from block*/
    public static int blockID(Block block) {
        return Block.getIdFromBlock(block);
    }

    /* Block from block id */
    public static Block idToBlock(int blockID) {
        return isVanillaID(blockID) ? blocks[replaces[blockID]] : null;
    }

    /* Block from block state */
    public static Block getBlock(IBlockState state) {
        return state.getBlock();
    }

    /* Block id from block state */
    public static int blockID(IBlockState block) {
        return blockID(getBlock(block));
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

    /* Replace all bad blocks */
    public static void replace(Iterable<String> badBlocks) {
        for (String blockName : badBlocks) {
            for (Block block : names.select(blockName, false)) {
                int srcID = blockID(block);
                int dstID = blockID(Blocks.STONE);
                if (block == WOOL) {
                    dstID = blockID(Blocks.LOG);
                }
                if (isVanillaID(srcID) && isVanillaID(dstID)) {
                    replaces[srcID] = dstID;
                }
            }
        }
    }

    static {

        for (int blockID = 0; blockID < 256; ++blockID) {
            blocks[blockID] = Blocks.idToBlock(blockID);
            replaces[blockID] = blockID;
        }

        blocks[0] = Blocks.AIR;
        blocks[1] = Blocks.STONE;
        blocks[2] = Blocks.GRASS;
        blocks[3] = Blocks.DIRT;
        blocks[4] = Blocks.COBBLESTONE;
        blocks[5] = Blocks.PLANKS;
        blocks[6] = Blocks.SAPLING;
        blocks[7] = Blocks.BEDROCK;
        blocks[8] = Blocks.FLOWING_WATER;
        blocks[9] = Blocks.WATER;
        blocks[10] = Blocks.FLOWING_LAVA;
        blocks[11] = Blocks.LAVA;
        blocks[12] = Blocks.SAND;
        blocks[13] = Blocks.GRAVEL;
        blocks[14] = Blocks.GOLD_ORE;
        blocks[15] = Blocks.IRON_ORE;
        blocks[16] = Blocks.COAL_ORE;
        blocks[17] = Blocks.LOG;
        blocks[18] = Blocks.LEAVES;
        blocks[19] = Blocks.SPONGE;
        blocks[20] = Blocks.GLASS;
        blocks[21] = Blocks.LAPIS_ORE;
        blocks[22] = Blocks.LAPIS_BLOCK;
        blocks[23] = Blocks.DISPENSER;
        blocks[24] = Blocks.SANDSTONE;
        blocks[25] = Blocks.NOTEBLOCK;
        blocks[26] = Blocks.BED;
        blocks[27] = Blocks.GOLDEN_RAIL;
        blocks[28] = Blocks.DETECTOR_RAIL;
        blocks[29] = Blocks.STICKY_PISTON;
        blocks[30] = Blocks.WEB;
        blocks[31] = Blocks.TALLGRASS;
        blocks[32] = Blocks.DEADBUSH;
        blocks[33] = Blocks.PISTON;
        blocks[34] = Blocks.PISTON_HEAD;
        blocks[35] = Blocks.WOOL;
        blocks[36] = Blocks.PISTON_EXTENSION;
        blocks[37] = Blocks.YELLOW_FLOWER;
        blocks[38] = Blocks.RED_FLOWER;
        blocks[39] = Blocks.BROWN_MUSHROOM;
        blocks[40] = Blocks.RED_MUSHROOM;
        blocks[41] = Blocks.GOLD_BLOCK;
        blocks[42] = Blocks.IRON_BLOCK;
        blocks[43] = Blocks.DOUBLE_STONE_SLAB;
        blocks[44] = Blocks.STONE_SLAB;
        blocks[45] = Blocks.BRICK_BLOCK;
        blocks[46] = Blocks.TNT;
        blocks[47] = Blocks.BOOKSHELF;
        blocks[48] = Blocks.MOSSY_COBBLESTONE;
        blocks[49] = Blocks.OBSIDIAN;
        blocks[50] = Blocks.TORCH;
        blocks[51] = Blocks.FIRE;
        blocks[52] = Blocks.MOB_SPAWNER;
        blocks[53] = Blocks.OAK_STAIRS;
        blocks[54] = Blocks.CHEST;
        blocks[55] = Blocks.REDSTONE_WIRE;
        blocks[56] = Blocks.DIAMOND_ORE;
        blocks[57] = Blocks.DIAMOND_BLOCK;
        blocks[58] = Blocks.CRAFTING_TABLE;
        blocks[59] = Blocks.WHEAT;
        blocks[60] = Blocks.FARMLAND;
        blocks[61] = Blocks.FURNACE;
        blocks[62] = Blocks.LIT_FURNACE;
        blocks[63] = Blocks.STANDING_SIGN;
        blocks[64] = Blocks.OAK_DOOR;
        blocks[65] = Blocks.LADDER;
        blocks[66] = Blocks.RAIL;
        blocks[67] = Blocks.STONE_STAIRS;
        blocks[68] = Blocks.WALL_SIGN;
        blocks[69] = Blocks.LEVER;
        blocks[70] = Blocks.STONE_PRESSURE_PLATE;
        blocks[71] = Blocks.IRON_DOOR;
        blocks[72] = Blocks.WOODEN_PRESSURE_PLATE;
        blocks[73] = Blocks.REDSTONE_ORE;
        blocks[74] = Blocks.LIT_REDSTONE_ORE;
        blocks[75] = Blocks.UNLIT_REDSTONE_TORCH;
        blocks[76] = Blocks.REDSTONE_TORCH;
        blocks[77] = Blocks.STONE_BUTTON;
        blocks[78] = Blocks.SNOW_LAYER;
        blocks[79] = Blocks.ICE;
        blocks[80] = Blocks.SNOW;
        blocks[81] = Blocks.CACTUS;
        blocks[82] = Blocks.CLAY;
        blocks[83] = Blocks.REEDS;
        blocks[84] = Blocks.JUKEBOX;
        blocks[85] = Blocks.OAK_FENCE;
        blocks[86] = Blocks.PUMPKIN;
        blocks[87] = Blocks.NETHERRACK;
        blocks[88] = Blocks.SOUL_SAND;
        blocks[89] = Blocks.GLOWSTONE;
        blocks[90] = Blocks.PORTAL;
        blocks[91] = Blocks.LIT_PUMPKIN;
        blocks[92] = Blocks.CAKE;
        blocks[93] = Blocks.UNPOWERED_REPEATER;
        blocks[94] = Blocks.POWERED_REPEATER;
        blocks[95] = Blocks.STAINED_GLASS;
        blocks[96] = Blocks.TRAPDOOR;
        blocks[97] = Blocks.MONSTER_EGG;
        blocks[98] = Blocks.STONEBRICK;
        blocks[99] = Blocks.BROWN_MUSHROOM_BLOCK;
        blocks[100] = Blocks.RED_MUSHROOM_BLOCK;
        blocks[101] = Blocks.IRON_BARS;
        blocks[102] = Blocks.GLASS_PANE;
        blocks[103] = Blocks.MELON_BLOCK;
        blocks[104] = Blocks.PUMPKIN_STEM;
        blocks[105] = Blocks.MELON_STEM;
        blocks[106] = Blocks.VINE;
        blocks[107] = Blocks.OAK_FENCE_GATE;
        blocks[108] = Blocks.BRICK_STAIRS;
        blocks[109] = Blocks.STONE_BRICK_STAIRS;
        blocks[110] = Blocks.MYCELIUM;
        blocks[111] = Blocks.WATERLILY;
        blocks[112] = Blocks.NETHER_BRICK;
        blocks[113] = Blocks.NETHER_BRICK_FENCE;
        blocks[114] = Blocks.NETHER_BRICK_STAIRS;
        blocks[115] = Blocks.NETHER_WART;
        blocks[116] = Blocks.ENCHANTING_TABLE;
        blocks[117] = Blocks.BREWING_STAND;
        blocks[118] = Blocks.CAULDRON;
        blocks[119] = Blocks.END_PORTAL;
        blocks[120] = Blocks.END_PORTAL_FRAME;
        blocks[121] = Blocks.END_STONE;
        blocks[122] = Blocks.DRAGON_EGG;
        blocks[123] = Blocks.REDSTONE_LAMP;
        blocks[124] = Blocks.LIT_REDSTONE_LAMP;
        blocks[125] = Blocks.DOUBLE_WOODEN_SLAB;
        blocks[126] = Blocks.WOODEN_SLAB;
        blocks[127] = Blocks.COCOA;
        blocks[128] = Blocks.SANDSTONE_STAIRS;
        blocks[129] = Blocks.EMERALD_ORE;
        blocks[130] = Blocks.ENDER_CHEST;
        blocks[131] = Blocks.TRIPWIRE_HOOK;
        blocks[132] = Blocks.TRIPWIRE;
        blocks[133] = Blocks.EMERALD_BLOCK;
        blocks[134] = Blocks.SPRUCE_STAIRS;
        blocks[135] = Blocks.BIRCH_STAIRS;
        blocks[136] = Blocks.JUNGLE_STAIRS;
        blocks[137] = Blocks.COMMAND_BLOCK;
        blocks[138] = Blocks.BEACON;
        blocks[139] = Blocks.COBBLESTONE_WALL;
        blocks[140] = Blocks.FLOWER_POT;
        blocks[141] = Blocks.CARROTS;
        blocks[142] = Blocks.POTATOES;
        blocks[143] = Blocks.WOODEN_BUTTON;
        blocks[144] = Blocks.SKULL;
        blocks[145] = Blocks.ANVIL;
        blocks[146] = Blocks.TRAPPED_CHEST;
        blocks[147] = Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE;
        blocks[148] = Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE;
        blocks[149] = Blocks.UNPOWERED_COMPARATOR;
        blocks[150] = Blocks.POWERED_COMPARATOR;
        blocks[151] = Blocks.DAYLIGHT_DETECTOR;
        blocks[152] = Blocks.REDSTONE_BLOCK;
        blocks[153] = Blocks.QUARTZ_ORE;
        blocks[154] = Blocks.HOPPER;
        blocks[155] = Blocks.QUARTZ_BLOCK;
        blocks[156] = Blocks.QUARTZ_STAIRS;
        blocks[157] = Blocks.ACTIVATOR_RAIL;
        blocks[158] = Blocks.DROPPER;
        blocks[159] = Blocks.STAINED_HARDENED_CLAY;
        blocks[160] = Blocks.STAINED_GLASS_PANE;
        blocks[161] = Blocks.LEAVES2;
        blocks[162] = Blocks.LOG2;
        blocks[163] = Blocks.ACACIA_STAIRS;
        blocks[164] = Blocks.DARK_OAK_STAIRS;
        blocks[165] = Blocks.SLIME_BLOCK;
        blocks[166] = Blocks.BARRIER;
        blocks[167] = Blocks.IRON_TRAPDOOR;
        blocks[168] = Blocks.PRISMARINE;
        blocks[169] = Blocks.SEA_LANTERN;
        blocks[170] = Blocks.HAY_BLOCK;
        blocks[171] = Blocks.CARPET;
        blocks[172] = Blocks.HARDENED_CLAY;
        blocks[173] = Blocks.COAL_BLOCK;
        blocks[174] = Blocks.PACKED_ICE;
        blocks[175] = Blocks.DOUBLE_PLANT;
        blocks[176] = Blocks.STANDING_BANNER;
        blocks[177] = Blocks.WALL_BANNER;
        blocks[178] = Blocks.DAYLIGHT_DETECTOR_INVERTED;
        blocks[179] = Blocks.RED_SANDSTONE;
        blocks[180] = Blocks.RED_SANDSTONE_STAIRS;
        blocks[181] = Blocks.DOUBLE_STONE_SLAB2;
        blocks[182] = Blocks.STONE_SLAB2;
        blocks[183] = Blocks.SPRUCE_FENCE_GATE;
        blocks[184] = Blocks.BIRCH_FENCE_GATE;
        blocks[185] = Blocks.JUNGLE_FENCE_GATE;
        blocks[186] = Blocks.DARK_OAK_FENCE_GATE;
        blocks[187] = Blocks.ACACIA_FENCE_GATE;
        blocks[188] = Blocks.SPRUCE_FENCE;
        blocks[189] = Blocks.BIRCH_FENCE;
        blocks[190] = Blocks.JUNGLE_FENCE;
        blocks[191] = Blocks.DARK_OAK_FENCE;
        blocks[192] = Blocks.ACACIA_FENCE;
        blocks[193] = Blocks.SPRUCE_DOOR;
        blocks[194] = Blocks.BIRCH_DOOR;
        blocks[195] = Blocks.JUNGLE_DOOR;
        blocks[196] = Blocks.ACACIA_DOOR;
        blocks[197] = Blocks.DARK_OAK_DOOR;
        blocks[198] = Blocks.END_ROD;
        blocks[199] = Blocks.CHORUS_PLANT;
        blocks[200] = Blocks.CHORUS_FLOWER;
        blocks[201] = Blocks.PURPUR_BLOCK;
        blocks[202] = Blocks.PURPUR_PILLAR;
        blocks[203] = Blocks.PURPUR_STAIRS;
        blocks[204] = Blocks.PURPUR_DOUBLE_SLAB;
        blocks[205] = Blocks.PURPUR_SLAB;
        blocks[206] = Blocks.END_BRICKS;
        blocks[207] = Blocks.BEETROOTS;
        blocks[208] = Blocks.GRASS_PATH;
        blocks[209] = Blocks.END_GATEWAY;
        blocks[210] = Blocks.REPEATING_COMMAND_BLOCK;
        blocks[211] = Blocks.CHAIN_COMMAND_BLOCK;
        blocks[212] = Blocks.FROSTED_ICE;
        blocks[213] = Blocks.MAGMA;
        blocks[214] = Blocks.NETHER_WART_BLOCK;
        blocks[215] = Blocks.RED_NETHER_BRICK;
        blocks[216] = Blocks.BONE_BLOCK;
        blocks[217] = Blocks.STRUCTURE_VOID;
        blocks[218] = null;
        blocks[219] = null;
        blocks[220] = null;
        blocks[221] = null;
        blocks[222] = null;
        blocks[223] = null;
        blocks[224] = null;
        blocks[225] = null;
        blocks[226] = null;
        blocks[227] = null;
        blocks[228] = null;
        blocks[229] = null;
        blocks[230] = null;
        blocks[231] = null;
        blocks[232] = null;
        blocks[233] = null;
        blocks[234] = null;
        blocks[235] = null;
        blocks[236] = null;
        blocks[237] = null;
        blocks[238] = null;
        blocks[239] = null;
        blocks[240] = null;
        blocks[241] = null;
        blocks[242] = null;
        blocks[243] = null;
        blocks[244] = null;
        blocks[245] = null;
        blocks[246] = null;
        blocks[247] = null;
        blocks[248] = null;
        blocks[249] = null;
        blocks[250] = null;
        blocks[251] = null;
        blocks[252] = null;
        blocks[253] = null;
        blocks[254] = null;
        blocks[255] = Blocks.STRUCTURE_BLOCK;

        for (int blockID = 0; blockID < 256; ++blockID) {
            Block block = blocks[blockID];
            if (block != null && block.getRegistryName() != null) {
                String blockName = block.getRegistryName().getResourcePath().toLowerCase();
                names.add(blockName, block);
            }
        }

    }

}
