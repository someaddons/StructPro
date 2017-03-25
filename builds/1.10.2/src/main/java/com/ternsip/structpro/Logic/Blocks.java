package com.ternsip.structpro.Logic;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import java.util.ArrayList;
import java.util.HashSet;

@SuppressWarnings({"WeakerAccess", "deprecation"})
public class Blocks extends net.minecraft.init.Blocks {

    /* Cardinal blocks skinning radius */
    public static double cardinalRadius = 3;

    /* Ground soil blocks */
    private static boolean[] soil = new boolean[256];

    /* Plants, stuff, web, fire, decorations, etc. */
    private static boolean[] overlook = new boolean[256];

    /* Cardinal blocks */
    private static boolean[] cardinal = new boolean[256];

    /* Liquid blocks  */
    private static boolean[] liquid = new boolean[256];

    /* Default vanilla blocks by classical indices */
    private static Block[] vanillaBlocks = new Block[256];

    /* Block force replaces */
    private static int [] blockReplaces = new int[256];

    public static int blockID(Block block) {
        return Block.getIdFromBlock(block);
    }

    public static Block idToBlock(int id) {
        return isVanillaID(id) ? vanillaBlocks[blockReplaces[id]] : null;
    }

    public static IBlockState state(Block block) {
        return block.getDefaultState();
    }

    public static IBlockState state(Block block, int meta) {
        IBlockState result = state(block);
        try {
            result = block.getStateFromMeta(meta);
        } catch (Throwable ignored) {}
        return result;
    }

    /* Returns suitable blocks by name */
    public static ArrayList<Block> select(String name) {
        ArrayList<Block> blocks = new ArrayList<Block>();
        for (Block block : vanillaBlocks) {
            if (    block != null &&
                    block.getRegistryName() != null &&
                    block.getRegistryName().getResourcePath().toLowerCase().contains(name.toLowerCase())) {
                blocks.add(block);
            }
        }
        return blocks;
    }

    public static void remove(final Iterable<String> bannedBlockNames) {
        final HashSet<Block> blocks = new HashSet<Block>() {{
            for (String blockName : bannedBlockNames) {
                addAll(select(blockName));
            }
        }};
        for (Block block : blocks) {
            setReplace(block);
        }
    }

    public static boolean isVanillaID(int blockID) {
        return blockID >= 0 && blockID < 256;
    }

    public static void setReplace(Block srcBlock) {
        int srcID = blockID(srcBlock);
        int dstID = blockID(Blocks.STONE);
        if (srcBlock == WOOL) {
            dstID = blockID(Blocks.LOG);
        }
        if (isVanillaID(srcID) && isVanillaID(dstID)) {
            blockReplaces[srcID] = dstID;
        }
    }

    public static void setSoil(Block block) {
        int id = blockID(block);
        if (isVanillaID(id)) {
            soil[id] = true;
        }
    }

    public static void setOverlook(Block block) {
        int id = blockID(block);
        if (isVanillaID(id)) {
            overlook[id] = true;
        }
    }

    public static void setLiquid(Block block) {
        int id = blockID(block);
        if (isVanillaID(id)) {
            liquid[id] = true;
        }
    }

    public static void setCardinal(Block block) {
        int id = blockID(block);
        if (isVanillaID(id)) {
            cardinal[id] = true;
        }
    }

    public static boolean isSoil(int blockID) {
        return isVanillaID(blockID) && soil[blockID];
    }

    public static boolean isLiquid(int blockID) {
        return isVanillaID(blockID) && liquid[blockID];
    }

    public static boolean isCardinal(int blockID) {
        return isVanillaID(blockID) && cardinal[blockID];
    }

    public static boolean isOverlook(int blockID) {
        return isVanillaID(blockID) && overlook[blockID];
    }

    static {

        setSoil(Blocks.GRASS);
        setSoil(Blocks.DIRT);
        setSoil(Blocks.STONE);
        setSoil(Blocks.COBBLESTONE);
        setSoil(Blocks.SANDSTONE);
        setSoil(Blocks.NETHERRACK);
        setSoil(Blocks.GRAVEL);
        setSoil(Blocks.SAND);
        setSoil(Blocks.BEDROCK);
        setSoil(Blocks.COAL_ORE);
        setSoil(Blocks.IRON_ORE);
        setSoil(Blocks.GOLD_ORE);
        setSoil(Blocks.DIAMOND_ORE);
        setSoil(Blocks.REDSTONE_ORE);
        setSoil(Blocks.LAPIS_ORE);
        setSoil(Blocks.EMERALD_ORE);
        setSoil(Blocks.LIT_REDSTONE_ORE);
        setSoil(Blocks.QUARTZ_ORE);
        setSoil(Blocks.CLAY);
        setSoil(Blocks.OBSIDIAN);
        
        setOverlook(Blocks.AIR);
        setOverlook(Blocks.LOG);
        setOverlook(Blocks.LOG2);
        setOverlook(Blocks.LEAVES);
        setOverlook(Blocks.LEAVES2);
        setOverlook(Blocks.SAPLING);
        setOverlook(Blocks.WEB);
        setOverlook(Blocks.TALLGRASS);
        setOverlook(Blocks.DEADBUSH);
        setOverlook(Blocks.YELLOW_FLOWER);
        setOverlook(Blocks.RED_FLOWER);
        setOverlook(Blocks.RED_MUSHROOM_BLOCK);
        setOverlook(Blocks.BROWN_MUSHROOM_BLOCK);
        setOverlook(Blocks.BROWN_MUSHROOM);
        setOverlook(Blocks.FIRE);
        setOverlook(Blocks.WHEAT);
        setOverlook(Blocks.SNOW_LAYER);
        setOverlook(Blocks.SNOW);
        setOverlook(Blocks.CACTUS);
        setOverlook(Blocks.PUMPKIN);
        setOverlook(Blocks.VINE);
        setOverlook(Blocks.COCOA);
        setOverlook(Blocks.WATERLILY);
        setOverlook(Blocks.DOUBLE_PLANT);

        setLiquid(Blocks.WATER);
        setLiquid(Blocks.FLOWING_WATER);
        setLiquid(Blocks.ICE);
        setLiquid(Blocks.LAVA);
        setLiquid(Blocks.FLOWING_LAVA);


        /* Cardinal blocks - doors */
        setCardinal(Blocks.TRAPDOOR);
        setCardinal(Blocks.IRON_TRAPDOOR);
        setCardinal(Blocks.SPRUCE_DOOR);
        setCardinal(Blocks.OAK_DOOR);
        setCardinal(Blocks.DARK_OAK_DOOR);
        setCardinal(Blocks.ACACIA_DOOR);
        setCardinal(Blocks.BIRCH_DOOR);
        setCardinal(Blocks.IRON_DOOR);
        setCardinal(Blocks.JUNGLE_DOOR);

        /* Cardinal blocks - glass */
        setCardinal(Blocks.GLASS);
        setCardinal(Blocks.GLASS_PANE);
        setCardinal(Blocks.STAINED_GLASS_PANE);
        setCardinal(Blocks.STAINED_GLASS);

        /* Cardinal blocks - interior */
        setCardinal(Blocks.CHEST);
        setCardinal(Blocks.ENDER_CHEST);
        setCardinal(Blocks.TRAPPED_CHEST);
        setCardinal(Blocks.FLOWER_POT);
        setCardinal(Blocks.CHORUS_FLOWER);
        setCardinal(Blocks.RED_FLOWER);
        setCardinal(Blocks.YELLOW_FLOWER);
        setCardinal(Blocks.RAIL);
        setCardinal(Blocks.ACTIVATOR_RAIL);
        setCardinal(Blocks.DETECTOR_RAIL);
        setCardinal(Blocks.GOLDEN_RAIL);
        setCardinal(Blocks.REDSTONE_WIRE);
        setCardinal(Blocks.TRIPWIRE);
        setCardinal(Blocks.TRIPWIRE_HOOK);
        setCardinal(Blocks.PORTAL);
        setCardinal(Blocks.COMMAND_BLOCK);

        /* Cardinal blocks - stairs */
        setCardinal(Blocks.ACACIA_STAIRS);
        setCardinal(Blocks.SANDSTONE_STAIRS);
        setCardinal(Blocks.BIRCH_STAIRS);
        setCardinal(Blocks.SPRUCE_STAIRS);
        setCardinal(Blocks.STONE_BRICK_STAIRS);
        setCardinal(Blocks.STONE_STAIRS);
        setCardinal(Blocks.BRICK_STAIRS);
        setCardinal(Blocks.DARK_OAK_STAIRS);
        setCardinal(Blocks.JUNGLE_STAIRS);
        setCardinal(Blocks.NETHER_BRICK_STAIRS);
        setCardinal(Blocks.OAK_STAIRS);
        setCardinal(Blocks.DARK_OAK_STAIRS);
        setCardinal(Blocks.PURPUR_STAIRS);
        setCardinal(Blocks.RED_SANDSTONE_STAIRS);
        setCardinal(Blocks.QUARTZ_STAIRS);

        /* Cardinal blocks - fence */
        setCardinal(Blocks.ACACIA_FENCE);
        setCardinal(Blocks.BIRCH_FENCE);
        setCardinal(Blocks.SPRUCE_FENCE);
        setCardinal(Blocks.DARK_OAK_FENCE);
        setCardinal(Blocks.JUNGLE_FENCE);
        setCardinal(Blocks.NETHER_BRICK_FENCE);
        setCardinal(Blocks.OAK_FENCE);
        setCardinal(Blocks.DARK_OAK_FENCE);
        setCardinal(Blocks.ACACIA_FENCE_GATE);
        setCardinal(Blocks.BIRCH_FENCE_GATE);
        setCardinal(Blocks.SPRUCE_FENCE_GATE);
        setCardinal(Blocks.DARK_OAK_FENCE_GATE);
        setCardinal(Blocks.JUNGLE_FENCE_GATE);
        setCardinal(Blocks.OAK_FENCE_GATE);
        setCardinal(Blocks.DARK_OAK_FENCE_GATE);

        for (int blockID = 0; blockID < 256; ++blockID) {
            vanillaBlocks[blockID] = Block.getBlockById(blockID);
            blockReplaces[blockID] = blockID;
        }

        vanillaBlocks[0] = Blocks.AIR;
        vanillaBlocks[1] = Blocks.STONE;
        vanillaBlocks[2] = Blocks.GRASS;
        vanillaBlocks[3] = Blocks.DIRT;
        vanillaBlocks[4] = Blocks.COBBLESTONE;
        vanillaBlocks[5] = Blocks.PLANKS;
        vanillaBlocks[6] = Blocks.SAPLING;
        vanillaBlocks[7] = Blocks.BEDROCK;
        vanillaBlocks[8] = Blocks.FLOWING_WATER;
        vanillaBlocks[9] = Blocks.WATER;
        vanillaBlocks[10] = Blocks.FLOWING_LAVA;
        vanillaBlocks[11] = Blocks.LAVA;
        vanillaBlocks[12] = Blocks.SAND;
        vanillaBlocks[13] = Blocks.GRAVEL;
        vanillaBlocks[14] = Blocks.GOLD_ORE;
        vanillaBlocks[15] = Blocks.IRON_ORE;
        vanillaBlocks[16] = Blocks.COAL_ORE;
        vanillaBlocks[17] = Blocks.LOG;
        vanillaBlocks[18] = Blocks.LEAVES;
        vanillaBlocks[19] = Blocks.SPONGE;
        vanillaBlocks[20] = Blocks.GLASS;
        vanillaBlocks[21] = Blocks.LAPIS_ORE;
        vanillaBlocks[22] = Blocks.LAPIS_BLOCK;
        vanillaBlocks[23] = Blocks.DISPENSER;
        vanillaBlocks[24] = Blocks.SANDSTONE;
        vanillaBlocks[25] = Blocks.NOTEBLOCK;
        vanillaBlocks[26] = Blocks.BED;
        vanillaBlocks[27] = Blocks.GOLDEN_RAIL;
        vanillaBlocks[28] = Blocks.DETECTOR_RAIL;
        vanillaBlocks[29] = Blocks.STICKY_PISTON;
        vanillaBlocks[30] = Blocks.WEB;
        vanillaBlocks[31] = Blocks.TALLGRASS;
        vanillaBlocks[32] = Blocks.DEADBUSH;
        vanillaBlocks[33] = Blocks.PISTON;
        vanillaBlocks[34] = Blocks.PISTON_HEAD;
        vanillaBlocks[35] = Blocks.WOOL;
        vanillaBlocks[36] = Blocks.PISTON_EXTENSION;
        vanillaBlocks[37] = Blocks.YELLOW_FLOWER;
        vanillaBlocks[38] = Blocks.RED_FLOWER;
        vanillaBlocks[39] = Blocks.BROWN_MUSHROOM;
        vanillaBlocks[40] = Blocks.RED_MUSHROOM;
        vanillaBlocks[41] = Blocks.GOLD_BLOCK;
        vanillaBlocks[42] = Blocks.IRON_BLOCK;
        vanillaBlocks[43] = Blocks.DOUBLE_STONE_SLAB;
        vanillaBlocks[44] = Blocks.STONE_SLAB;
        vanillaBlocks[45] = Blocks.BRICK_BLOCK;
        vanillaBlocks[46] = Blocks.TNT;
        vanillaBlocks[47] = Blocks.BOOKSHELF;
        vanillaBlocks[48] = Blocks.MOSSY_COBBLESTONE;
        vanillaBlocks[49] = Blocks.OBSIDIAN;
        vanillaBlocks[50] = Blocks.TORCH;
        vanillaBlocks[51] = Blocks.FIRE;
        vanillaBlocks[52] = Blocks.MOB_SPAWNER;
        vanillaBlocks[53] = Blocks.OAK_STAIRS;
        vanillaBlocks[54] = Blocks.CHEST;
        vanillaBlocks[55] = Blocks.REDSTONE_WIRE;
        vanillaBlocks[56] = Blocks.DIAMOND_ORE;
        vanillaBlocks[57] = Blocks.DIAMOND_BLOCK;
        vanillaBlocks[58] = Blocks.CRAFTING_TABLE;
        vanillaBlocks[59] = Blocks.WHEAT;
        vanillaBlocks[60] = Blocks.FARMLAND;
        vanillaBlocks[61] = Blocks.FURNACE;
        vanillaBlocks[62] = Blocks.LIT_FURNACE;
        vanillaBlocks[63] = Blocks.STANDING_SIGN;
        vanillaBlocks[64] = Blocks.OAK_DOOR;
        vanillaBlocks[65] = Blocks.LADDER;
        vanillaBlocks[66] = Blocks.RAIL;
        vanillaBlocks[67] = Blocks.STONE_STAIRS;
        vanillaBlocks[68] = Blocks.WALL_SIGN;
        vanillaBlocks[69] = Blocks.LEVER;
        vanillaBlocks[70] = Blocks.STONE_PRESSURE_PLATE;
        vanillaBlocks[71] = Blocks.IRON_DOOR;
        vanillaBlocks[72] = Blocks.WOODEN_PRESSURE_PLATE;
        vanillaBlocks[73] = Blocks.REDSTONE_ORE;
        vanillaBlocks[74] = Blocks.LIT_REDSTONE_ORE;
        vanillaBlocks[75] = Blocks.UNLIT_REDSTONE_TORCH;
        vanillaBlocks[76] = Blocks.REDSTONE_TORCH;
        vanillaBlocks[77] = Blocks.STONE_BUTTON;
        vanillaBlocks[78] = Blocks.SNOW_LAYER;
        vanillaBlocks[79] = Blocks.ICE;
        vanillaBlocks[80] = Blocks.SNOW;
        vanillaBlocks[81] = Blocks.CACTUS;
        vanillaBlocks[82] = Blocks.CLAY;
        vanillaBlocks[83] = Blocks.REEDS;
        vanillaBlocks[84] = Blocks.JUKEBOX;
        vanillaBlocks[85] = Blocks.OAK_FENCE;
        vanillaBlocks[86] = Blocks.PUMPKIN;
        vanillaBlocks[87] = Blocks.NETHERRACK;
        vanillaBlocks[88] = Blocks.SOUL_SAND;
        vanillaBlocks[89] = Blocks.GLOWSTONE;
        vanillaBlocks[90] = Blocks.PORTAL;
        vanillaBlocks[91] = Blocks.LIT_PUMPKIN;
        vanillaBlocks[92] = Blocks.CAKE;
        vanillaBlocks[93] = Blocks.UNPOWERED_REPEATER;
        vanillaBlocks[94] = Blocks.POWERED_REPEATER;
        vanillaBlocks[95] = Blocks.STAINED_GLASS;
        vanillaBlocks[96] = Blocks.TRAPDOOR;
        vanillaBlocks[97] = Blocks.MONSTER_EGG;
        vanillaBlocks[98] = Blocks.STONEBRICK;
        vanillaBlocks[99] = Blocks.BROWN_MUSHROOM_BLOCK;
        vanillaBlocks[100] = Blocks.RED_MUSHROOM_BLOCK;
        vanillaBlocks[101] = Blocks.IRON_BARS;
        vanillaBlocks[102] = Blocks.GLASS_PANE;
        vanillaBlocks[103] = Blocks.MELON_BLOCK;
        vanillaBlocks[104] = Blocks.PUMPKIN_STEM;
        vanillaBlocks[105] = Blocks.MELON_STEM;
        vanillaBlocks[106] = Blocks.VINE;
        vanillaBlocks[107] = Blocks.OAK_FENCE_GATE;
        vanillaBlocks[108] = Blocks.BRICK_STAIRS;
        vanillaBlocks[109] = Blocks.STONE_BRICK_STAIRS;
        vanillaBlocks[110] = Blocks.MYCELIUM;
        vanillaBlocks[111] = Blocks.WATERLILY;
        vanillaBlocks[112] = Blocks.NETHER_BRICK;
        vanillaBlocks[113] = Blocks.NETHER_BRICK_FENCE;
        vanillaBlocks[114] = Blocks.NETHER_BRICK_STAIRS;
        vanillaBlocks[115] = Blocks.NETHER_WART;
        vanillaBlocks[116] = Blocks.ENCHANTING_TABLE;
        vanillaBlocks[117] = Blocks.BREWING_STAND;
        vanillaBlocks[118] = Blocks.CAULDRON;
        vanillaBlocks[119] = Blocks.END_PORTAL;
        vanillaBlocks[120] = Blocks.END_PORTAL_FRAME;
        vanillaBlocks[121] = Blocks.END_STONE;
        vanillaBlocks[122] = Blocks.DRAGON_EGG;
        vanillaBlocks[123] = Blocks.REDSTONE_LAMP;
        vanillaBlocks[124] = Blocks.LIT_REDSTONE_LAMP;
        vanillaBlocks[125] = Blocks.DOUBLE_WOODEN_SLAB;
        vanillaBlocks[126] = Blocks.WOODEN_SLAB;
        vanillaBlocks[127] = Blocks.COCOA;
        vanillaBlocks[128] = Blocks.SANDSTONE_STAIRS;
        vanillaBlocks[129] = Blocks.EMERALD_ORE;
        vanillaBlocks[130] = Blocks.ENDER_CHEST;
        vanillaBlocks[131] = Blocks.TRIPWIRE_HOOK;
        vanillaBlocks[132] = Blocks.TRIPWIRE;
        vanillaBlocks[133] = Blocks.EMERALD_BLOCK;
        vanillaBlocks[134] = Blocks.SPRUCE_STAIRS;
        vanillaBlocks[135] = Blocks.BIRCH_STAIRS;
        vanillaBlocks[136] = Blocks.JUNGLE_STAIRS;
        vanillaBlocks[137] = Blocks.COMMAND_BLOCK;
        vanillaBlocks[138] = Blocks.BEACON;
        vanillaBlocks[139] = Blocks.COBBLESTONE_WALL;
        vanillaBlocks[140] = Blocks.FLOWER_POT;
        vanillaBlocks[141] = Blocks.CARROTS;
        vanillaBlocks[142] = Blocks.POTATOES;
        vanillaBlocks[143] = Blocks.WOODEN_BUTTON;
        vanillaBlocks[144] = Blocks.SKULL;
        vanillaBlocks[145] = Blocks.ANVIL;
        vanillaBlocks[146] = Blocks.TRAPPED_CHEST;
        vanillaBlocks[147] = Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE;
        vanillaBlocks[148] = Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE;
        vanillaBlocks[149] = Blocks.UNPOWERED_COMPARATOR;
        vanillaBlocks[150] = Blocks.POWERED_COMPARATOR;
        vanillaBlocks[151] = Blocks.DAYLIGHT_DETECTOR;
        vanillaBlocks[152] = Blocks.REDSTONE_BLOCK;
        vanillaBlocks[153] = Blocks.QUARTZ_ORE;
        vanillaBlocks[154] = Blocks.HOPPER;
        vanillaBlocks[155] = Blocks.QUARTZ_BLOCK;
        vanillaBlocks[156] = Blocks.QUARTZ_STAIRS;
        vanillaBlocks[157] = Blocks.ACTIVATOR_RAIL;
        vanillaBlocks[158] = Blocks.DROPPER;
        vanillaBlocks[159] = Blocks.STAINED_HARDENED_CLAY;
        vanillaBlocks[160] = Blocks.STAINED_GLASS_PANE;
        vanillaBlocks[161] = Blocks.LEAVES2;
        vanillaBlocks[162] = Blocks.LOG2;
        vanillaBlocks[163] = Blocks.ACACIA_STAIRS;
        vanillaBlocks[164] = Blocks.DARK_OAK_STAIRS;
        vanillaBlocks[165] = Blocks.SLIME_BLOCK;
        vanillaBlocks[166] = Blocks.BARRIER;
        vanillaBlocks[167] = Blocks.IRON_TRAPDOOR;
        vanillaBlocks[168] = Blocks.PRISMARINE;
        vanillaBlocks[169] = Blocks.SEA_LANTERN;
        vanillaBlocks[170] = Blocks.HAY_BLOCK;
        vanillaBlocks[171] = Blocks.CARPET;
        vanillaBlocks[172] = Blocks.HARDENED_CLAY;
        vanillaBlocks[173] = Blocks.COAL_BLOCK;
        vanillaBlocks[174] = Blocks.PACKED_ICE;
        vanillaBlocks[175] = Blocks.DOUBLE_PLANT;
        vanillaBlocks[176] = Blocks.STANDING_BANNER;
        vanillaBlocks[177] = Blocks.WALL_BANNER;
        vanillaBlocks[178] = Blocks.DAYLIGHT_DETECTOR_INVERTED;
        vanillaBlocks[179] = Blocks.RED_SANDSTONE;
        vanillaBlocks[180] = Blocks.RED_SANDSTONE_STAIRS;
        vanillaBlocks[181] = Blocks.DOUBLE_STONE_SLAB2;
        vanillaBlocks[182] = Blocks.STONE_SLAB2;
        vanillaBlocks[183] = Blocks.SPRUCE_FENCE_GATE;
        vanillaBlocks[184] = Blocks.BIRCH_FENCE_GATE;
        vanillaBlocks[185] = Blocks.JUNGLE_FENCE_GATE;
        vanillaBlocks[186] = Blocks.DARK_OAK_FENCE_GATE;
        vanillaBlocks[187] = Blocks.ACACIA_FENCE_GATE;
        vanillaBlocks[188] = Blocks.SPRUCE_FENCE;
        vanillaBlocks[189] = Blocks.BIRCH_FENCE;
        vanillaBlocks[190] = Blocks.JUNGLE_FENCE;
        vanillaBlocks[191] = Blocks.DARK_OAK_FENCE;
        vanillaBlocks[192] = Blocks.ACACIA_FENCE;
        vanillaBlocks[193] = Blocks.SPRUCE_DOOR;
        vanillaBlocks[194] = Blocks.BIRCH_DOOR;
        vanillaBlocks[195] = Blocks.JUNGLE_DOOR;
        vanillaBlocks[196] = Blocks.ACACIA_DOOR;
        vanillaBlocks[197] = Blocks.DARK_OAK_DOOR;
        vanillaBlocks[198] = Blocks.END_ROD;
        vanillaBlocks[199] = Blocks.CHORUS_PLANT;
        vanillaBlocks[200] = Blocks.CHORUS_FLOWER;
        vanillaBlocks[201] = Blocks.PURPUR_BLOCK;
        vanillaBlocks[202] = Blocks.PURPUR_PILLAR;
        vanillaBlocks[203] = Blocks.PURPUR_STAIRS;
        vanillaBlocks[204] = Blocks.PURPUR_DOUBLE_SLAB;
        vanillaBlocks[205] = Blocks.PURPUR_SLAB;
        vanillaBlocks[206] = Blocks.END_BRICKS;
        vanillaBlocks[207] = Blocks.BEETROOTS;
        vanillaBlocks[208] = Blocks.GRASS_PATH;
        vanillaBlocks[209] = Blocks.END_GATEWAY;
        vanillaBlocks[210] = Blocks.REPEATING_COMMAND_BLOCK;
        vanillaBlocks[211] = Blocks.CHAIN_COMMAND_BLOCK;
        vanillaBlocks[212] = Blocks.FROSTED_ICE;
        vanillaBlocks[213] = Blocks.MAGMA;
        vanillaBlocks[214] = Blocks.NETHER_WART_BLOCK;
        vanillaBlocks[215] = Blocks.RED_NETHER_BRICK;
        vanillaBlocks[216] = Blocks.BONE_BLOCK;
        vanillaBlocks[217] = Blocks.STRUCTURE_VOID;
        vanillaBlocks[218] = null;
        vanillaBlocks[219] = null;
        vanillaBlocks[220] = null;
        vanillaBlocks[221] = null;
        vanillaBlocks[222] = null;
        vanillaBlocks[223] = null;
        vanillaBlocks[224] = null;
        vanillaBlocks[225] = null;
        vanillaBlocks[226] = null;
        vanillaBlocks[227] = null;
        vanillaBlocks[228] = null;
        vanillaBlocks[229] = null;
        vanillaBlocks[230] = null;
        vanillaBlocks[231] = null;
        vanillaBlocks[232] = null;
        vanillaBlocks[233] = null;
        vanillaBlocks[234] = null;
        vanillaBlocks[235] = null;
        vanillaBlocks[236] = null;
        vanillaBlocks[237] = null;
        vanillaBlocks[238] = null;
        vanillaBlocks[239] = null;
        vanillaBlocks[240] = null;
        vanillaBlocks[241] = null;
        vanillaBlocks[242] = null;
        vanillaBlocks[243] = null;
        vanillaBlocks[244] = null;
        vanillaBlocks[245] = null;
        vanillaBlocks[246] = null;
        vanillaBlocks[247] = null;
        vanillaBlocks[248] = null;
        vanillaBlocks[249] = null;
        vanillaBlocks[250] = null;
        vanillaBlocks[251] = null;
        vanillaBlocks[252] = null;
        vanillaBlocks[253] = null;
        vanillaBlocks[254] = null;
        vanillaBlocks[255] = Blocks.STRUCTURE_BLOCK;

    }

}
