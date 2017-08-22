package com.ternsip.structpro.universe.blocks;

import com.ternsip.structpro.logic.Configurator;
import com.ternsip.structpro.universe.utils.Report;
import com.ternsip.structpro.universe.utils.Selector;
import com.ternsip.structpro.universe.utils.Utils;
import net.minecraft.init.Blocks;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Blocks wrapper
 * @author Ternsip
 */
@SuppressWarnings({"WeakerAccess"})
public class UBlocks {

    public static final UBlock AIR = new UBlock(Blocks.air);
    public static final UBlock STONE = new UBlock(Blocks.stone);
    public static final UBlock GRASS = new UBlock(Blocks.grass);
    public static final UBlock DIRT = new UBlock(Blocks.dirt);
    public static final UBlock COBBLESTONE = new UBlock(Blocks.cobblestone);
    public static final UBlock PLANKS = new UBlock(Blocks.planks);
    public static final UBlock SAPLING = new UBlock(Blocks.sapling);
    public static final UBlock BEDROCK = new UBlock(Blocks.bedrock);
    public static final UBlock FLOWING_WATER = new UBlock(Blocks.flowing_water);
    public static final UBlock WATER = new UBlock(Blocks.water);
    public static final UBlock FLOWING_LAVA = new UBlock(Blocks.flowing_lava);
    public static final UBlock LAVA = new UBlock(Blocks.lava);
    public static final UBlock SAND = new UBlock(Blocks.sand);
    public static final UBlock GRAVEL = new UBlock(Blocks.gravel);
    public static final UBlock GOLD_ORE = new UBlock(Blocks.gold_ore);
    public static final UBlock IRON_ORE = new UBlock(Blocks.iron_ore);
    public static final UBlock COAL_ORE = new UBlock(Blocks.coal_ore);
    public static final UBlock LOG = new UBlock(Blocks.log);
    public static final UBlock LOG2 = new UBlock(Blocks.log2);
    public static final UBlock LEAVES = new UBlock(Blocks.leaves);
    public static final UBlock LEAVES2 = new UBlock(Blocks.leaves2);
    public static final UBlock SPONGE = new UBlock(Blocks.sponge);
    public static final UBlock GLASS = new UBlock(Blocks.glass);
    public static final UBlock LAPIS_ORE = new UBlock(Blocks.lapis_ore);
    public static final UBlock LAPIS_BLOCK = new UBlock(Blocks.lapis_block);
    public static final UBlock DISPENSER = new UBlock(Blocks.dispenser);
    public static final UBlock SANDSTONE = new UBlock(Blocks.sandstone);
    public static final UBlock NOTEBLOCK = new UBlock(Blocks.noteblock);
    public static final UBlock BED = new UBlock(Blocks.bed);
    public static final UBlock GOLDEN_RAIL = new UBlock(Blocks.golden_rail);
    public static final UBlock DETECTOR_RAIL = new UBlock(Blocks.detector_rail);
    public static final UBlock STICKY_PISTON = new UBlock(Blocks.sticky_piston);
    public static final UBlock WEB = new UBlock(Blocks.web);
    public static final UBlock TALLGRASS = new UBlock(Blocks.tallgrass);
    public static final UBlock DEADBUSH = new UBlock(Blocks.deadbush);
    public static final UBlock PISTON = new UBlock(Blocks.piston);
    public static final UBlock PISTON_HEAD = new UBlock(Blocks.piston_head);
    public static final UBlock WOOL = new UBlock(Blocks.wool);
    public static final UBlock PISTON_EXTENSION = new UBlock(Blocks.piston_extension);
    public static final UBlock YELLOW_FLOWER = new UBlock(Blocks.yellow_flower);
    public static final UBlock RED_FLOWER = new UBlock(Blocks.red_flower);
    public static final UBlock BROWN_MUSHROOM = new UBlock(Blocks.brown_mushroom);
    public static final UBlock RED_MUSHROOM = new UBlock(Blocks.red_mushroom);
    public static final UBlock GOLD_BLOCK = new UBlock(Blocks.gold_block);
    public static final UBlock IRON_BLOCK = new UBlock(Blocks.iron_block);
    public static final UBlock DOUBLE_STONE_SLAB = new UBlock(Blocks.double_stone_slab);
    public static final UBlock STONE_SLAB = new UBlock(Blocks.stone_slab);
    public static final UBlock BRICK_BLOCK = new UBlock(Blocks.brick_block);
    public static final UBlock TNT = new UBlock(Blocks.tnt);
    public static final UBlock BOOKSHELF = new UBlock(Blocks.bookshelf);
    public static final UBlock MOSSY_COBBLESTONE = new UBlock(Blocks.mossy_cobblestone);
    public static final UBlock OBSIDIAN = new UBlock(Blocks.obsidian);
    public static final UBlock TORCH = new UBlock(Blocks.torch);
    public static final UBlock FIRE = new UBlock(Blocks.fire);
    public static final UBlock MOB_SPAWNER = new UBlock(Blocks.mob_spawner);
    public static final UBlock OAK_STAIRS = new UBlock(Blocks.oak_stairs);
    public static final UBlock CHEST = new UBlock(Blocks.chest);
    public static final UBlock REDSTONE_WIRE = new UBlock(Blocks.redstone_wire);
    public static final UBlock DIAMOND_ORE = new UBlock(Blocks.diamond_ore);
    public static final UBlock DIAMOND_BLOCK = new UBlock(Blocks.diamond_block);
    public static final UBlock CRAFTING_TABLE = new UBlock(Blocks.crafting_table);
    public static final UBlock WHEAT = new UBlock(Blocks.wheat);
    public static final UBlock FARMLAND = new UBlock(Blocks.farmland);
    public static final UBlock FURNACE = new UBlock(Blocks.furnace);
    public static final UBlock LIT_FURNACE = new UBlock(Blocks.lit_furnace);
    public static final UBlock STANDING_SIGN = new UBlock(Blocks.standing_sign);
    public static final UBlock LADDER = new UBlock(Blocks.ladder);
    public static final UBlock RAIL = new UBlock(Blocks.rail);
    public static final UBlock STONE_STAIRS = new UBlock(Blocks.stone_stairs);
    public static final UBlock WALL_SIGN = new UBlock(Blocks.wall_sign);
    public static final UBlock LEVER = new UBlock(Blocks.lever);
    public static final UBlock STONE_PRESSURE_PLATE = new UBlock(Blocks.stone_pressure_plate);
    public static final UBlock IRON_DOOR = new UBlock(Blocks.iron_door);
    public static final UBlock WOODEN_PRESSURE_PLATE = new UBlock(Blocks.wooden_pressure_plate);
    public static final UBlock REDSTONE_ORE = new UBlock(Blocks.redstone_ore);
    public static final UBlock LIT_REDSTONE_ORE = new UBlock(Blocks.lit_redstone_ore);
    public static final UBlock UNLIT_REDSTONE_TORCH = new UBlock(Blocks.unlit_redstone_torch);
    public static final UBlock REDSTONE_TORCH = new UBlock(Blocks.redstone_torch);
    public static final UBlock STONE_BUTTON = new UBlock(Blocks.stone_button);
    public static final UBlock SNOW_LAYER = new UBlock(Blocks.snow_layer);
    public static final UBlock ICE = new UBlock(Blocks.ice);
    public static final UBlock SNOW = new UBlock(Blocks.snow);
    public static final UBlock CACTUS = new UBlock(Blocks.cactus);
    public static final UBlock CLAY = new UBlock(Blocks.clay);
    public static final UBlock REEDS = new UBlock(Blocks.reeds);
    public static final UBlock JUKEBOX = new UBlock(Blocks.jukebox);
    public static final UBlock PUMPKIN = new UBlock(Blocks.pumpkin);
    public static final UBlock NETHERRACK = new UBlock(Blocks.netherrack);
    public static final UBlock SOUL_SAND = new UBlock(Blocks.soul_sand);
    public static final UBlock GLOWSTONE = new UBlock(Blocks.glowstone);
    public static final UBlock PORTAL = new UBlock(Blocks.portal);
    public static final UBlock LIT_PUMPKIN = new UBlock(Blocks.lit_pumpkin);
    public static final UBlock CAKE = new UBlock(Blocks.cake);
    public static final UBlock UNPOWERED_REPEATER = new UBlock(Blocks.unpowered_repeater);
    public static final UBlock POWERED_REPEATER = new UBlock(Blocks.powered_repeater);
    public static final UBlock TRAPDOOR = new UBlock(Blocks.trapdoor);
    public static final UBlock MONSTER_EGG = new UBlock(Blocks.monster_egg);
    public static final UBlock STONEBRICK = new UBlock(Blocks.stonebrick);
    public static final UBlock BROWN_MUSHROOM_BLOCK = new UBlock(Blocks.brown_mushroom_block);
    public static final UBlock RED_MUSHROOM_BLOCK = new UBlock(Blocks.red_mushroom_block);
    public static final UBlock IRON_BARS = new UBlock(Blocks.iron_bars);
    public static final UBlock GLASS_PANE = new UBlock(Blocks.glass_pane);
    public static final UBlock MELON_BLOCK = new UBlock(Blocks.melon_block);
    public static final UBlock PUMPKIN_STEM = new UBlock(Blocks.pumpkin_stem);
    public static final UBlock MELON_STEM = new UBlock(Blocks.melon_stem);
    public static final UBlock VINE = new UBlock(Blocks.vine);
    public static final UBlock BRICK_STAIRS = new UBlock(Blocks.brick_stairs);
    public static final UBlock STONE_BRICK_STAIRS = new UBlock(Blocks.stone_brick_stairs);
    public static final UBlock MYCELIUM = new UBlock(Blocks.mycelium);
    public static final UBlock WATERLILY = new UBlock(Blocks.waterlily);
    public static final UBlock NETHER_BRICK = new UBlock(Blocks.nether_brick);
    public static final UBlock NETHER_BRICK_FENCE = new UBlock(Blocks.nether_brick_fence);
    public static final UBlock NETHER_BRICK_STAIRS = new UBlock(Blocks.nether_brick_stairs);
    public static final UBlock NETHER_WART = new UBlock(Blocks.nether_wart);
    public static final UBlock ENCHANTING_TABLE = new UBlock(Blocks.enchanting_table);
    public static final UBlock BREWING_STAND = new UBlock(Blocks.brewing_stand);
    public static final UBlock CAULDRON = new UBlock(Blocks.cauldron);
    public static final UBlock END_PORTAL = new UBlock(Blocks.end_portal);
    public static final UBlock END_PORTAL_FRAME = new UBlock(Blocks.end_portal_frame);
    public static final UBlock END_STONE = new UBlock(Blocks.end_stone);
    public static final UBlock DRAGON_EGG = new UBlock(Blocks.dragon_egg);
    public static final UBlock REDSTONE_LAMP = new UBlock(Blocks.redstone_lamp);
    public static final UBlock LIT_REDSTONE_LAMP = new UBlock(Blocks.lit_redstone_lamp);
    public static final UBlock DOUBLE_WOODEN_SLAB = new UBlock(Blocks.double_wooden_slab);
    public static final UBlock WOODEN_SLAB = new UBlock(Blocks.wooden_slab);
    public static final UBlock COCOA = new UBlock(Blocks.cocoa);
    public static final UBlock SANDSTONE_STAIRS = new UBlock(Blocks.sandstone_stairs);
    public static final UBlock EMERALD_ORE = new UBlock(Blocks.emerald_ore);
    public static final UBlock ENDER_CHEST = new UBlock(Blocks.ender_chest);
    public static final UBlock TRIPWIRE_HOOK = new UBlock(Blocks.tripwire_hook);
    public static final UBlock TRIPWIRE = new UBlock(Blocks.tripwire);
    public static final UBlock EMERALD_BLOCK = new UBlock(Blocks.emerald_block);
    public static final UBlock SPRUCE_STAIRS = new UBlock(Blocks.spruce_stairs);
    public static final UBlock BIRCH_STAIRS = new UBlock(Blocks.birch_stairs);
    public static final UBlock JUNGLE_STAIRS = new UBlock(Blocks.jungle_stairs);
    public static final UBlock COMMAND_BLOCK = new UBlock(Blocks.command_block);
    public static final UBlock BEACON = new UBlock(Blocks.beacon);
    public static final UBlock COBBLESTONE_WALL = new UBlock(Blocks.cobblestone_wall);
    public static final UBlock FLOWER_POT = new UBlock(Blocks.flower_pot);
    public static final UBlock CARROTS = new UBlock(Blocks.carrots);
    public static final UBlock POTATOES = new UBlock(Blocks.potatoes);
    public static final UBlock WOODEN_BUTTON = new UBlock(Blocks.wooden_button);
    public static final UBlock SKULL = new UBlock(Blocks.skull);
    public static final UBlock ANVIL = new UBlock(Blocks.anvil);
    public static final UBlock TRAPPED_CHEST = new UBlock(Blocks.trapped_chest);
    public static final UBlock LIGHT_WEIGHTED_PRESSURE_PLATE = new UBlock(Blocks.light_weighted_pressure_plate);
    public static final UBlock HEAVY_WEIGHTED_PRESSURE_PLATE = new UBlock(Blocks.heavy_weighted_pressure_plate);
    public static final UBlock UNPOWERED_COMPARATOR = new UBlock(Blocks.unpowered_comparator);
    public static final UBlock POWERED_COMPARATOR = new UBlock(Blocks.powered_comparator);
    public static final UBlock DAYLIGHT_DETECTOR = new UBlock(Blocks.daylight_detector);
    public static final UBlock REDSTONE_BLOCK = new UBlock(Blocks.redstone_block);
    public static final UBlock QUARTZ_ORE = new UBlock(Blocks.quartz_ore);
    public static final UBlock HOPPER = new UBlock(Blocks.hopper);
    public static final UBlock QUARTZ_BLOCK = new UBlock(Blocks.quartz_block);
    public static final UBlock QUARTZ_STAIRS = new UBlock(Blocks.quartz_stairs);
    public static final UBlock ACTIVATOR_RAIL = new UBlock(Blocks.activator_rail);
    public static final UBlock DROPPER = new UBlock(Blocks.dropper);
    public static final UBlock STAINED_HARDENED_CLAY = new UBlock(Blocks.stained_hardened_clay);
    public static final UBlock HAY_BLOCK = new UBlock(Blocks.hay_block);
    public static final UBlock CARPET = new UBlock(Blocks.carpet);
    public static final UBlock HARDENED_CLAY = new UBlock(Blocks.hardened_clay);
    public static final UBlock COAL_BLOCK = new UBlock(Blocks.coal_block);
    public static final UBlock PACKED_ICE = new UBlock(Blocks.packed_ice);
    public static final UBlock ACACIA_STAIRS = new UBlock(Blocks.acacia_stairs);
    public static final UBlock DARK_OAK_STAIRS = new UBlock(Blocks.dark_oak_stairs);
    public static final UBlock DOUBLE_PLANT = new UBlock(Blocks.double_plant);
    public static final UBlock STAINED_GLASS = new UBlock(Blocks.stained_glass);
    public static final UBlock STAINED_GLASS_PANE = new UBlock(Blocks.stained_glass_pane);

    /** Default vanilla blocks by classical indices */
    private static final UBlock[] blocks = new UBlock[256];

    /** Block selector */
    final static Selector<UBlock> selector = new Selector<>();

    /**
     * Check if the block have vanilla index
     * @param blockID Block index
     * @return True if block has vanilla ID
     */
    public static boolean isVanilla(int blockID) {
        return blockID >= 0 && blockID < 256;
    }

    /**
     * Vanilla block from block id
     * @param blockID Block index
     * @return Minecraft native block or null
     */
    public static UBlock getBlockVanilla(int blockID) {
        return isVanilla(blockID) ? blocks[blockID] : null;
    }

    static {

        blocks[0] = AIR;
        blocks[1] = STONE;
        blocks[2] = GRASS;
        blocks[3] = DIRT;
        blocks[4] = COBBLESTONE;
        blocks[5] = PLANKS;
        blocks[6] = SAPLING;
        blocks[7] = BEDROCK;
        blocks[8] = FLOWING_WATER;
        blocks[9] = WATER;
        blocks[10] = FLOWING_LAVA;
        blocks[11] = LAVA;
        blocks[12] = SAND;
        blocks[13] = GRAVEL;
        blocks[14] = GOLD_ORE;
        blocks[15] = IRON_ORE;
        blocks[16] = COAL_ORE;
        blocks[17] = LOG;
        blocks[18] = LEAVES;
        blocks[19] = SPONGE;
        blocks[20] = GLASS;
        blocks[21] = LAPIS_ORE;
        blocks[22] = LAPIS_BLOCK;
        blocks[23] = DISPENSER;
        blocks[24] = SANDSTONE;
        blocks[25] = NOTEBLOCK;
        blocks[26] = BED;
        blocks[27] = GOLDEN_RAIL;
        blocks[28] = DETECTOR_RAIL;
        blocks[29] = STICKY_PISTON;
        blocks[30] = WEB;
        blocks[31] = TALLGRASS;
        blocks[32] = DEADBUSH;
        blocks[33] = PISTON;
        blocks[34] = PISTON_HEAD;
        blocks[35] = WOOL;
        blocks[36] = PISTON_EXTENSION;
        blocks[37] = YELLOW_FLOWER;
        blocks[38] = RED_FLOWER;
        blocks[39] = BROWN_MUSHROOM;
        blocks[40] = RED_MUSHROOM;
        blocks[41] = GOLD_BLOCK;
        blocks[42] = IRON_BLOCK;
        blocks[43] = DOUBLE_STONE_SLAB;
        blocks[44] = STONE_SLAB;
        blocks[45] = BRICK_BLOCK;
        blocks[46] = TNT;
        blocks[47] = BOOKSHELF;
        blocks[48] = MOSSY_COBBLESTONE;
        blocks[49] = OBSIDIAN;
        blocks[50] = TORCH;
        blocks[51] = FIRE;
        blocks[52] = MOB_SPAWNER;
        blocks[53] = OAK_STAIRS;
        blocks[54] = CHEST;
        blocks[55] = REDSTONE_WIRE;
        blocks[56] = DIAMOND_ORE;
        blocks[57] = DIAMOND_BLOCK;
        blocks[58] = CRAFTING_TABLE;
        blocks[59] = WHEAT;
        blocks[60] = FARMLAND;
        blocks[61] = FURNACE;
        blocks[62] = LIT_FURNACE;
        blocks[63] = STANDING_SIGN;
        blocks[64] = null;
        blocks[65] = LADDER;
        blocks[66] = RAIL;
        blocks[67] = STONE_STAIRS;
        blocks[68] = WALL_SIGN;
        blocks[69] = LEVER;
        blocks[70] = STONE_PRESSURE_PLATE;
        blocks[71] = IRON_DOOR;
        blocks[72] = WOODEN_PRESSURE_PLATE;
        blocks[73] = REDSTONE_ORE;
        blocks[74] = LIT_REDSTONE_ORE;
        blocks[75] = UNLIT_REDSTONE_TORCH;
        blocks[76] = REDSTONE_TORCH;
        blocks[77] = STONE_BUTTON;
        blocks[78] = SNOW_LAYER;
        blocks[79] = ICE;
        blocks[80] = SNOW;
        blocks[81] = CACTUS;
        blocks[82] = CLAY;
        blocks[83] = REEDS;
        blocks[84] = JUKEBOX;
        blocks[85] = null;
        blocks[86] = PUMPKIN;
        blocks[87] = NETHERRACK;
        blocks[88] = SOUL_SAND;
        blocks[89] = GLOWSTONE;
        blocks[90] = PORTAL;
        blocks[91] = LIT_PUMPKIN;
        blocks[92] = CAKE;
        blocks[93] = UNPOWERED_REPEATER;
        blocks[94] = POWERED_REPEATER;
        blocks[95] = STAINED_GLASS;
        blocks[96] = TRAPDOOR;
        blocks[97] = MONSTER_EGG;
        blocks[98] = STONEBRICK;
        blocks[99] = BROWN_MUSHROOM_BLOCK;
        blocks[100] = RED_MUSHROOM_BLOCK;
        blocks[101] = IRON_BARS;
        blocks[102] = GLASS_PANE;
        blocks[103] = MELON_BLOCK;
        blocks[104] = PUMPKIN_STEM;
        blocks[105] = MELON_STEM;
        blocks[106] = VINE;
        blocks[107] = null;
        blocks[108] = BRICK_STAIRS;
        blocks[109] = STONE_BRICK_STAIRS;
        blocks[110] = MYCELIUM;
        blocks[111] = WATERLILY;
        blocks[112] = NETHER_BRICK;
        blocks[113] = NETHER_BRICK_FENCE;
        blocks[114] = NETHER_BRICK_STAIRS;
        blocks[115] = NETHER_WART;
        blocks[116] = ENCHANTING_TABLE;
        blocks[117] = BREWING_STAND;
        blocks[118] = CAULDRON;
        blocks[119] = END_PORTAL;
        blocks[120] = END_PORTAL_FRAME;
        blocks[121] = END_STONE;
        blocks[122] = DRAGON_EGG;
        blocks[123] = REDSTONE_LAMP;
        blocks[124] = LIT_REDSTONE_LAMP;
        blocks[125] = DOUBLE_WOODEN_SLAB;
        blocks[126] = WOODEN_SLAB;
        blocks[127] = COCOA;
        blocks[128] = SANDSTONE_STAIRS;
        blocks[129] = EMERALD_ORE;
        blocks[130] = ENDER_CHEST;
        blocks[131] = TRIPWIRE_HOOK;
        blocks[132] = TRIPWIRE;
        blocks[133] = EMERALD_BLOCK;
        blocks[134] = SPRUCE_STAIRS;
        blocks[135] = BIRCH_STAIRS;
        blocks[136] = JUNGLE_STAIRS;
        blocks[137] = COMMAND_BLOCK;
        blocks[138] = BEACON;
        blocks[139] = COBBLESTONE_WALL;
        blocks[140] = FLOWER_POT;
        blocks[141] = CARROTS;
        blocks[142] = POTATOES;
        blocks[143] = WOODEN_BUTTON;
        blocks[144] = SKULL;
        blocks[145] = ANVIL;
        blocks[146] = TRAPPED_CHEST;
        blocks[147] = LIGHT_WEIGHTED_PRESSURE_PLATE;
        blocks[148] = HEAVY_WEIGHTED_PRESSURE_PLATE;
        blocks[149] = UNPOWERED_COMPARATOR;
        blocks[150] = POWERED_COMPARATOR;
        blocks[151] = DAYLIGHT_DETECTOR;
        blocks[152] = REDSTONE_BLOCK;
        blocks[153] = QUARTZ_ORE;
        blocks[154] = HOPPER;
        blocks[155] = QUARTZ_BLOCK;
        blocks[156] = QUARTZ_STAIRS;
        blocks[157] = ACTIVATOR_RAIL;
        blocks[158] = DROPPER;
        blocks[159] = STAINED_HARDENED_CLAY;
        blocks[160] = STAINED_GLASS_PANE;
        blocks[161] = LEAVES2;
        blocks[162] = LOG2;
        blocks[163] = ACACIA_STAIRS;
        blocks[164] = DARK_OAK_STAIRS;
        blocks[165] = null;
        blocks[166] = null;
        blocks[167] = null;
        blocks[168] = null;
        blocks[169] = null;
        blocks[170] = HAY_BLOCK;
        blocks[171] = CARPET;
        blocks[172] = HARDENED_CLAY;
        blocks[173] = COAL_BLOCK;
        blocks[174] = PACKED_ICE;
        blocks[175] = DOUBLE_PLANT;
        blocks[176] = null;
        blocks[177] = null;
        blocks[178] = null;
        blocks[179] = null;
        blocks[180] = null;
        blocks[181] = null;
        blocks[182] = null;
        blocks[183] = null;
        blocks[184] = null;
        blocks[185] = null;
        blocks[186] = null;
        blocks[187] = null;
        blocks[188] = null;
        blocks[189] = null;
        blocks[190] = null;
        blocks[191] = null;
        blocks[192] = null;
        blocks[193] = null;
        blocks[194] = null;
        blocks[195] = null;
        blocks[196] = null;
        blocks[197] = null;
        blocks[198] = null;
        blocks[199] = null;
        blocks[200] = null;
        blocks[201] = null;
        blocks[202] = null;
        blocks[203] = null;
        blocks[204] = null;
        blocks[205] = null;
        blocks[206] = null;
        blocks[207] = null;
        blocks[208] = null;
        blocks[209] = null;
        blocks[210] = null;
        blocks[211] = null;
        blocks[212] = null;
        blocks[213] = null;
        blocks[214] = null;
        blocks[215] = null;
        blocks[216] = null;
        blocks[217] = null;
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
        blocks[255] = null;

        /* Construct block replaces */
        for (UBlock uBlock : blocks) {
            if (uBlock != null && uBlock.isValid()) {
                selector.add(uBlock.getPath(), uBlock);
            }
        }
        selector.add("NULL", null);
        final HashMap<UBlock, UBlock> replace = new HashMap<>();
        for (String name : Configurator.REPLACE_BLOCKS) {
            List<String> tokens = Utils.tokenize(name, "->");
            if (tokens.size() == 2) {
                try {
                    Pattern srcPattern = Pattern.compile(tokens.get(0), Pattern.CASE_INSENSITIVE);
                    Pattern dstPattern = Pattern.compile(tokens.get(1), Pattern.CASE_INSENSITIVE);
                    for (UBlock blockSrc : selector.select(srcPattern)) {
                        for (UBlock blockDst : selector.select(dstPattern)) {
                            replace.put(blockSrc, blockDst);
                        }
                    }
                } catch (PatternSyntaxException pse) {
                    new Report().post("BAD PATTERN", pse.getMessage()).print();
                }
            }
        }

        /* Replace configured blocks */
        for (int index = 0; index < blocks.length; ++index) {
            UBlock ublock = blocks[index];
            blocks[index] = ublock == null || !replace.containsKey(ublock) ? ublock : replace.get(ublock);
        }

    }

}
