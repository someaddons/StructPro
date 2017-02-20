package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Structure.Biome;
import com.ternsip.structpro.Structure.Structure.Method;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import com.ternsip.structpro.WorldCache.WorldCache;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;

/* This class holds mod data */
public class Loader {

    /* Spawn probability per chunk */
    public static double density = 0.005;

    /* Village probability per chunk */
    public static double densityVillage = 0.0005;

    /* Calibration accuracy */
    public static double accuracy = 1.0;

    /* Replace rich blocks to poor */
    public static boolean balanceMode = true;

    /* Prevent command block for spawning */
    public static boolean noCommandBlock = false;

    /* Set chest spawn priority to native loot if it exists */
    public static boolean nativeLoot = false;

    /* Chest loot chance [0..1] */
    public static double lootChance = 0.25;

    /* Pull out structure from the ground and lift up (recommended 0) */
    public static int forceLift = 0;

    /* Prevent mobspawners for spawning */
    public static boolean noMobSpawners = false;

    /* Spawn mobs in generated structures */
    public static boolean spawnMobs = true;

    /* Allow only vanilla blocks to spawn */
    public static boolean allowOnlyVanillaBlocks = true;

    /* Print additional mod output to console */
    public static boolean additionalOutput = true;

    /* Min number of stack per chest inclusive */
    public static int minChestItems = 2;

    /* Max number of stacks per chest exclusive */
    public static int maxChestItems = 7;

    /* Max item stack size for chest loot */
    public static int maxChestStackSize = 12;

    /* Allow spawning structures only in dimensions with given ids */
    public static ArrayList<Integer> spawnDimensions = new ArrayList<Integer>() {{add(-1); add(0); add(1);}};

    /* Allow spawning villages only in dimensions with given ids */
    public static ArrayList<Integer> villageDimensions = new ArrayList<Integer>() {{add(0);}};

    /* Cardinal blocks skinning radius */
    public static double cardinalBlocksRadius = 3;

    /* Ground soil blocks */
    public static boolean[] soil = new boolean[256];

    /* Plants, stuff, web, fire, decorations, etc. */
    public static boolean[] overlook = new boolean[256];

    /* Cardinal blocks */
    public static boolean[] cardinalBlocks = new boolean[256];

    /* Liquid blocks  */
    public static boolean[] liquid = new boolean[256];

    /* Default vanilla blocks by classical indices */
    public static Block[] vanillaBlocks = new Block[256];

    /* Block force replaces */
    public static int [] blockReplaces = new int[256];

    /* All possible items */
    public static ArrayList<Item> items = new ArrayList<Item>();

    public static Storage storage = new Storage();

    /* Is debug enabled flag */
    public static boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
            getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

    /* Load structures from folder */
    private static void loadStructures(File folder) {
        new Report().add("LOADING SCHEMATICS FROM", folder.getPath()).print();
        Stack<File> folders = new Stack<File>();
        folders.add(folder);
        while (!folders.empty()) {
            File[] listOfFiles = folders.pop().listFiles();
            for (File file : listOfFiles != null ? listOfFiles : new File[0]) {
                if (file.isFile()) {
                    storage.loadStructure(file);
                } else if (file.isDirectory()) {
                    folders.add(file);
                }
            }
        }
        storage.sortVillages();
    }

    /* Load and generate mod settings */
    private static void configure(File file) throws IOException {
        if (new File(file.getParent()).mkdirs()) {
            new Report().add("CREATE CONFIG", file.getParent());
        }
        Properties config = new Properties();
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            try {
                config.load(fis);
                density = Double.parseDouble(config.getProperty("DENSITY", Double.toString(density)));
                densityVillage = Double.parseDouble(config.getProperty("DENSITY_VILLAGE", Double.toString(densityVillage)));
                accuracy = Double.parseDouble(config.getProperty("ACCURACY", Double.toString(accuracy)));
                balanceMode = Boolean.parseBoolean(config.getProperty("BALANCE_MODE", Boolean.toString(balanceMode)));
                nativeLoot = Boolean.parseBoolean(config.getProperty("NATIVE_LOOT", Boolean.toString(nativeLoot)));
                noCommandBlock = Boolean.parseBoolean(config.getProperty("NO_COMMAND_BLOCK", Boolean.toString(noCommandBlock)));
                lootChance = Double.parseDouble(config.getProperty("CHEST_LOOT_CHANCE", Double.toString(lootChance)));
                forceLift = (int) Double.parseDouble(config.getProperty("FORCE_LIFT", Double.toString(forceLift)));
                noMobSpawners = Boolean.parseBoolean(config.getProperty("NO_MOB_SPAWNERS", Boolean.toString(noMobSpawners)));
                spawnMobs = Boolean.parseBoolean(config.getProperty("SPAWN_MOBS", Boolean.toString(spawnMobs)));
                allowOnlyVanillaBlocks = Boolean.parseBoolean(config.getProperty("ALLOW_ONLY_VANILLA_BLOCKS", Boolean.toString(allowOnlyVanillaBlocks)));
                additionalOutput = Boolean.parseBoolean(config.getProperty("ADDITIONAL_OUTPUT", Boolean.toString(additionalOutput)));
                minChestItems = (int) Double.parseDouble(config.getProperty("MIN_CHEST_ITEMS", Double.toString(minChestItems)));
                maxChestItems = (int) Double.parseDouble(config.getProperty("MAX_CHEST_ITEMS", Double.toString(maxChestItems)));
                maxChestStackSize = (int) Double.parseDouble(config.getProperty("MAX_CHEST_STACK_SIZE", Double.toString(maxChestStackSize)));
                spawnDimensions = Utils.stringToArray(config.getProperty("SPAWN_DIMENSIONS", "-1, 0, 1"));
                villageDimensions = Utils.stringToArray(config.getProperty("VILLAGE_DIMENSIONS", "0"));
            } finally {
                fis.close();
            }
        }
        FileOutputStream fos = new FileOutputStream(file);
        try {
            config.setProperty("DENSITY", Double.toString(density));
            config.setProperty("DENSITY_VILLAGE", Double.toString(densityVillage));
            config.setProperty("ACCURACY", Double.toString(accuracy));
            config.setProperty("BALANCE_MODE", Boolean.toString(balanceMode));
            config.setProperty("NATIVE_LOOT", Boolean.toString(nativeLoot));
            config.setProperty("NO_COMMAND_BLOCK", Boolean.toString(noCommandBlock));
            config.setProperty("CHEST_LOOT_CHANCE", Double.toString(lootChance));
            config.setProperty("FORCE_LIFT", Integer.toString(forceLift));
            config.setProperty("NO_MOB_SPAWNERS", Boolean.toString(noMobSpawners));
            config.setProperty("SPAWN_MOBS", Boolean.toString(spawnMobs));
            config.setProperty("ALLOW_ONLY_VANILLA_BLOCKS", Boolean.toString(allowOnlyVanillaBlocks));
            config.setProperty("ADDITIONAL_OUTPUT", Boolean.toString(additionalOutput));
            config.setProperty("MIN_CHEST_ITEMS", Integer.toString(minChestItems));
            config.setProperty("MAX_CHEST_ITEMS", Integer.toString(maxChestItems));
            config.setProperty("MAX_CHEST_STACK_SIZE", Integer.toString(maxChestStackSize));
            config.setProperty("SPAWN_DIMENSIONS", Utils.arrayToString(spawnDimensions));
            config.setProperty("VILLAGE_DIMENSIONS", Utils.arrayToString(villageDimensions));
            config.store(fos, null);
        } finally {
            fos.close();
        }
    }

    static {

        try {
            configure(new File("config/structpro.cfg"));
        } catch (IOException ioe) {
            new Report()
                    .add("CAN'T CONFIGURE: ", ioe.getMessage())
                    .print();
        } finally {
            new Report()
                    .add("POSSIBLE_DIMENSIONS", Utils.arrayToString(spawnDimensions))
                    .print();
        }

        soil[WorldCache.blockID(Blocks.GRASS)] = true;
        soil[WorldCache.blockID(Blocks.DIRT)] = true;
        soil[WorldCache.blockID(Blocks.STONE)] = true;
        soil[WorldCache.blockID(Blocks.COBBLESTONE)] = true;
        soil[WorldCache.blockID(Blocks.SANDSTONE)] = true;
        soil[WorldCache.blockID(Blocks.NETHERRACK)] = true;
        soil[WorldCache.blockID(Blocks.GRAVEL)] = true;
        soil[WorldCache.blockID(Blocks.SAND)] = true;
        soil[WorldCache.blockID(Blocks.BEDROCK)] = true;
        soil[WorldCache.blockID(Blocks.COAL_ORE)] = true;
        soil[WorldCache.blockID(Blocks.IRON_ORE)] = true;
        soil[WorldCache.blockID(Blocks.GOLD_ORE)] = true;
        soil[WorldCache.blockID(Blocks.DIAMOND_ORE)] = true;
        soil[WorldCache.blockID(Blocks.REDSTONE_ORE)] = true;
        soil[WorldCache.blockID(Blocks.LAPIS_ORE)] = true;
        soil[WorldCache.blockID(Blocks.EMERALD_ORE)] = true;
        soil[WorldCache.blockID(Blocks.LIT_REDSTONE_ORE)] = true;
        soil[WorldCache.blockID(Blocks.QUARTZ_ORE)] = true;
        soil[WorldCache.blockID(Blocks.CLAY)] = true;
        soil[WorldCache.blockID(Blocks.OBSIDIAN)] = true;

        overlook[WorldCache.blockID(Blocks.AIR)] = true;
        overlook[WorldCache.blockID(Blocks.LOG)] = true;
        overlook[WorldCache.blockID(Blocks.LOG2)] = true;
        overlook[WorldCache.blockID(Blocks.LEAVES)] = true;
        overlook[WorldCache.blockID(Blocks.LEAVES2)] = true;
        overlook[WorldCache.blockID(Blocks.SAPLING)] = true;
        overlook[WorldCache.blockID(Blocks.WEB)] = true;
        overlook[WorldCache.blockID(Blocks.TALLGRASS)] = true;
        overlook[WorldCache.blockID(Blocks.DEADBUSH)] = true;
        overlook[WorldCache.blockID(Blocks.YELLOW_FLOWER)] = true;
        overlook[WorldCache.blockID(Blocks.RED_FLOWER)] = true;
        overlook[WorldCache.blockID(Blocks.RED_MUSHROOM_BLOCK)] = true;
        overlook[WorldCache.blockID(Blocks.BROWN_MUSHROOM_BLOCK)] = true;
        overlook[WorldCache.blockID(Blocks.BROWN_MUSHROOM)] = true;
        overlook[WorldCache.blockID(Blocks.FIRE)] = true;
        overlook[WorldCache.blockID(Blocks.WHEAT)] = true;
        overlook[WorldCache.blockID(Blocks.SNOW_LAYER)] = true;
        overlook[WorldCache.blockID(Blocks.SNOW)] = true;
        overlook[WorldCache.blockID(Blocks.CACTUS)] = true;
        overlook[WorldCache.blockID(Blocks.PUMPKIN)] = true;
        overlook[WorldCache.blockID(Blocks.VINE)] = true;
        overlook[WorldCache.blockID(Blocks.COCOA)] = true;
        overlook[WorldCache.blockID(Blocks.WATERLILY)] = true;
        overlook[WorldCache.blockID(Blocks.DOUBLE_PLANT)] = true;

        liquid[WorldCache.blockID(Blocks.WATER)] = true;
        liquid[WorldCache.blockID(Blocks.FLOWING_WATER)] = true;
        liquid[WorldCache.blockID(Blocks.ICE)] = true;
        liquid[WorldCache.blockID(Blocks.LAVA)] = true;
        liquid[WorldCache.blockID(Blocks.FLOWING_LAVA)] = true;


        /* Cardinal blocks - doors */
        cardinalBlocks[WorldCache.blockID(Blocks.TRAPDOOR)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.IRON_TRAPDOOR)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.SPRUCE_DOOR)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.OAK_DOOR)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.DARK_OAK_DOOR)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.ACACIA_DOOR)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.BIRCH_DOOR)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.IRON_DOOR)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.JUNGLE_DOOR)] = true;

        /* Cardinal blocks - glass */
        cardinalBlocks[WorldCache.blockID(Blocks.GLASS)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.GLASS_PANE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.STAINED_GLASS_PANE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.STAINED_GLASS)] = true;

        /* Cardinal blocks - interior */
        cardinalBlocks[WorldCache.blockID(Blocks.CHEST)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.ENDER_CHEST)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.TRAPPED_CHEST)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.FLOWER_POT)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.CHORUS_FLOWER)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.RED_FLOWER)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.YELLOW_FLOWER)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.RAIL)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.ACTIVATOR_RAIL)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.DETECTOR_RAIL)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.GOLDEN_RAIL)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.REDSTONE_WIRE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.TRIPWIRE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.TRIPWIRE_HOOK)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.PORTAL)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.COMMAND_BLOCK)] = true;

        /* Cardinal blocks - stairs */
        cardinalBlocks[WorldCache.blockID(Blocks.ACACIA_STAIRS)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.SANDSTONE_STAIRS)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.BIRCH_STAIRS)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.SPRUCE_STAIRS)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.STONE_BRICK_STAIRS)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.STONE_STAIRS)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.BRICK_STAIRS)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.DARK_OAK_STAIRS)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.JUNGLE_STAIRS)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.NETHER_BRICK_STAIRS)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.OAK_STAIRS)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.DARK_OAK_STAIRS)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.PURPUR_STAIRS)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.RED_SANDSTONE_STAIRS)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.QUARTZ_STAIRS)] = true;

        /* Cardinal blocks - fence */
        cardinalBlocks[WorldCache.blockID(Blocks.ACACIA_FENCE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.BIRCH_FENCE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.SPRUCE_FENCE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.DARK_OAK_FENCE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.JUNGLE_FENCE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.NETHER_BRICK_FENCE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.OAK_FENCE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.DARK_OAK_FENCE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.ACACIA_FENCE_GATE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.BIRCH_FENCE_GATE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.SPRUCE_FENCE_GATE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.DARK_OAK_FENCE_GATE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.JUNGLE_FENCE_GATE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.OAK_FENCE_GATE)] = true;
        cardinalBlocks[WorldCache.blockID(Blocks.DARK_OAK_FENCE_GATE)] = true;


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
        vanillaBlocks[213] = null;
        vanillaBlocks[214] = null;
        vanillaBlocks[215] = null;
        vanillaBlocks[216] = null;
        vanillaBlocks[217] = null;
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

        for (int blockID = 0; blockID < 256; ++blockID) {
            blockReplaces[blockID] = blockID;
        }
        if (balanceMode) {
            blockReplaces[WorldCache.blockID(Blocks.BARRIER)] = WorldCache.blockID(Blocks.COBBLESTONE);
            blockReplaces[WorldCache.blockID(Blocks.BEDROCK)] = WorldCache.blockID(Blocks.COBBLESTONE);
            blockReplaces[WorldCache.blockID(Blocks.IRON_BLOCK)] = WorldCache.blockID(Blocks.MOSSY_COBBLESTONE);
            blockReplaces[WorldCache.blockID(Blocks.GOLD_BLOCK)] = WorldCache.blockID(Blocks.STONEBRICK);
            blockReplaces[WorldCache.blockID(Blocks.DIAMOND_BLOCK)] = WorldCache.blockID(Blocks.MOSSY_COBBLESTONE);
            blockReplaces[WorldCache.blockID(Blocks.LAPIS_BLOCK)] = WorldCache.blockID(Blocks.STONEBRICK);
            blockReplaces[WorldCache.blockID(Blocks.EMERALD_BLOCK)] = WorldCache.blockID(Blocks.MOSSY_COBBLESTONE);
            blockReplaces[WorldCache.blockID(Blocks.WOOL)] = WorldCache.blockID(Blocks.LOG);
            blockReplaces[WorldCache.blockID(Blocks.BEACON)] = WorldCache.blockID(Blocks.QUARTZ_BLOCK);
        }
        if (noCommandBlock) {
            blockReplaces[WorldCache.blockID(Blocks.COMMAND_BLOCK)] = WorldCache.blockID(Blocks.MOSSY_COBBLESTONE);
        }
        if (noMobSpawners) {
            blockReplaces[WorldCache.blockID(Blocks.MOB_SPAWNER)] = WorldCache.blockID(Blocks.MOSSY_COBBLESTONE);
        }

        for (ResourceLocation resourceLocation : GameData.getItemRegistry().getKeys()) {
            String itemName = resourceLocation.getResourcePath().toLowerCase();
            if (itemName.contains("barrier") || itemName.contains("command")) {
                continue;
            }
            Item item = Item.REGISTRY.getObject(resourceLocation);
            if (item != null) {
                items.add(item);
            }
        }

        long startTime = System.currentTimeMillis();
        loadStructures(new File("schematics"));
        long loadTime = (System.currentTimeMillis() - startTime);
        Report report = new Report();
        for (Method method : Method.values()) {
            report.add("METHOD " + method.name.toUpperCase(), String.valueOf(storage.select(method).size()));
        }
        for (Biome biome : Biome.values()) {
            report.add("BIOME " + biome.name.toUpperCase(), String.valueOf(storage.select(biome).size()));
        }
        report.add("TOTAL LOADED", String.valueOf(storage.select().size()));
        report.add("LOAD TIME", new DecimalFormat("###0.00").format(loadTime / 1000.0) + "s");
        report.print();

    }

}
