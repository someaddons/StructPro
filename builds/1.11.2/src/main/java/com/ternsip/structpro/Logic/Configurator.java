package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Structure.Biome;
import com.ternsip.structpro.Structure.Structure.Method;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Properties;
import java.util.Stack;

/* Loads configuration and holds mod data */
@SuppressWarnings({"WeakerAccess", "deprecation"})
public class Configurator {

    /* Spawn probability per chunk, to not generate set any negative value */
    public static double density = 0.0035;

    /* Village probability per chunk, to not generate set any negative value */
    public static double densityVillage = 0.00035;

    /* Calibration accuracy */
    public static double accuracy = 1.0;

    /* Pull out structure from the ground and lift up (recommended 0) */
    public static int forceLift = 0;

    /* Set chest spawn priority to native loot if it exists */
    public static boolean nativeLoot = false;

    /* Ban modded items from spawning */
    public static boolean onlyVanillaLoot = true;

    /* Chest loot chance, to not loot set any negative value */
    public static double lootChance = 0.5;

    /* Min number of stack per chest inclusive */
    public static int minChestItems = 2;

    /* Max number of stacks per chest exclusive */
    public static int maxChestItems = 7;

    /* Max item stack size for chest loot */
    public static int maxChestStackSize = 12;

    /* Spawn mobs in generated structures */
    public static boolean spawnMobs = true;

    /* Spawn mobs in generated structures */
    public static boolean mobSpawnersEggsOnly = true;

    /* Print additional mod output to console */
    public static boolean additionalOutput = true;

    /* Max length in chunks for world to be generated, 4096 chunks = area 65536 x 65536 blocks */
    public static int worldChunkBorder = 4096;

    /* Schematics loads from this folder */
    public static File schematicsFolder = new File("schematics");

    /* Allow spawning structures only in dimensions with given ids, case sensitive */
    public static HashSet<String> spawnDimensions = new HashSet<String>() {{
        add("-1");
        add("0");
        add("1");
    }};

    /* Allow spawning villages only in dimensions with given ids, case sensitive */
    public static HashSet<String> villageDimensions = new HashSet<String>() {{
        add("0");
    }};

    /* Replaces this blocks with stone */
    public static HashSet<String> banBlocks = new HashSet<String>() {{
        add("BARRIER");
        add("BEDROCK");
        add("IRON_BLOCK");
        add("GOLD_BLOCK");
        add("DIAMOND_BLOCK");
        add("LAPIS_BLOCK");
        add("EMERALD_BLOCK");
        add("WOOL");
        add("BEACON");
    }};

    /* Exclude items from possible loot */
    public static HashSet<String> banItems = new HashSet<String>() {{
        add("BARRIER");
        add("COMMAND");
    }};

    /* Load structures from folder */
    private static void loadStructures(File folder) {
        new Report().add("LOADING SCHEMATICS FROM", folder.getPath()).print();
        Stack<File> folders = new Stack<File>();
        folders.add(folder);
        while (!folders.empty()) {
            File[] listOfFiles = folders.pop().listFiles();
            for (File file : listOfFiles != null ? listOfFiles : new File[0]) {
                if (file.isFile()) {
                    Structures.loadStructure(file);
                } else if (file.isDirectory()) {
                    folders.add(file);
                }
            }
        }
        Structures.sortVillages();
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
                forceLift = (int) Double.parseDouble(config.getProperty("FORCE_LIFT", Double.toString(forceLift)));
                nativeLoot = Boolean.parseBoolean(config.getProperty("NATIVE_LOOT", Boolean.toString(nativeLoot)));
                onlyVanillaLoot = Boolean.parseBoolean(config.getProperty("ONLY_VANILLA_LOOT", Boolean.toString(onlyVanillaLoot)));
                lootChance = Double.parseDouble(config.getProperty("CHEST_LOOT_CHANCE", Double.toString(lootChance)));
                minChestItems = (int) Double.parseDouble(config.getProperty("MIN_CHEST_ITEMS", Double.toString(minChestItems)));
                maxChestItems = (int) Double.parseDouble(config.getProperty("MAX_CHEST_ITEMS", Double.toString(maxChestItems)));
                maxChestStackSize = (int) Double.parseDouble(config.getProperty("MAX_CHEST_STACK_SIZE", Double.toString(maxChestStackSize)));
                spawnMobs = Boolean.parseBoolean(config.getProperty("SPAWN_MOBS", Boolean.toString(spawnMobs)));
                mobSpawnersEggsOnly = Boolean.parseBoolean(config.getProperty("MOB_SPAWNERS_EGGS_ONLY", Boolean.toString(mobSpawnersEggsOnly)));
                additionalOutput = Boolean.parseBoolean(config.getProperty("ADDITIONAL_OUTPUT", Boolean.toString(additionalOutput)));
                worldChunkBorder = (int) Double.parseDouble(config.getProperty("WORLD_CHUNK_BORDER", Double.toString(worldChunkBorder)));
                schematicsFolder = new File(config.getProperty("SCHEMATICS_FOLDER", schematicsFolder.getPath()));
                spawnDimensions = tokenize(config.getProperty("SPAWN_DIMENSIONS", combine(spawnDimensions)));
                villageDimensions = tokenize(config.getProperty("VILLAGE_DIMENSIONS", combine(villageDimensions)));
                banBlocks = tokenize(config.getProperty("BAN_BLOCKS", combine(banBlocks)));
                banItems = tokenize(config.getProperty("BAN_ITEMS", combine(banItems)));
            } finally {
                fis.close();
            }
        }
        FileOutputStream fos = new FileOutputStream(file);
        try {
            config.setProperty("DENSITY", Double.toString(density));
            config.setProperty("DENSITY_VILLAGE", Double.toString(densityVillage));
            config.setProperty("ACCURACY", Double.toString(accuracy));
            config.setProperty("FORCE_LIFT", Integer.toString(forceLift));
            config.setProperty("NATIVE_LOOT", Boolean.toString(nativeLoot));
            config.setProperty("ONLY_VANILLA_LOOT", Boolean.toString(onlyVanillaLoot));
            config.setProperty("CHEST_LOOT_CHANCE", Double.toString(lootChance));
            config.setProperty("MIN_CHEST_ITEMS", Integer.toString(minChestItems));
            config.setProperty("MAX_CHEST_ITEMS", Integer.toString(maxChestItems));
            config.setProperty("MAX_CHEST_STACK_SIZE", Integer.toString(maxChestStackSize));
            config.setProperty("SPAWN_MOBS", Boolean.toString(spawnMobs));
            config.setProperty("MOB_SPAWNERS_EGGS_ONLY", Boolean.toString(mobSpawnersEggsOnly));
            config.setProperty("ADDITIONAL_OUTPUT", Boolean.toString(additionalOutput));
            config.setProperty("WORLD_CHUNK_BORDER", Integer.toString(worldChunkBorder));
            config.setProperty("SCHEMATICS_FOLDER", schematicsFolder.getPath());
            config.setProperty("SPAWN_DIMENSIONS", combine(spawnDimensions));
            config.setProperty("VILLAGE_DIMENSIONS", combine(villageDimensions));
            config.setProperty("BAN_BLOCKS", combine(banBlocks));
            config.setProperty("BAN_ITEMS", combine(banItems));
            config.store(fos, null);
        } finally {
            fos.close();
        }
    }

    public static File getSchematicsSavesFolder() {
        return new File(schematicsFolder,"Saves");
    }

    private static String combine(HashSet<String> set) {
        return Utils.join(Utils.toArray(set), ", ");
    }

    private static HashSet<String> tokenize(String string) {
        return Utils.tokenize(string, ",");
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
                    .add("POSSIBLE_DIMENSIONS", combine(spawnDimensions))
                    .add("POSSIBLE_VILLAGE_DIMENSIONS", combine(villageDimensions))
                    .print();
        }

        Blocks.remove(banBlocks);
        Items.remove(banItems);

        if (onlyVanillaLoot) {
            Items.removeNotVanilla();
        }

        long startTime = System.currentTimeMillis();
        loadStructures(schematicsFolder);
        long loadTime = (System.currentTimeMillis() - startTime);
        Report report = new Report();
        for (Method method : Method.values()) {
            report.add("METHOD " + method.name.toUpperCase(), String.valueOf(Structures.structures.select(method).size()));
        }
        for (Biome biome : Biome.values()) {
            report.add("BIOME " + biome.name.toUpperCase(), String.valueOf(Structures.structures.select(biome).size()));
        }
        report.add("TOTAL STRUCTURES LOADED", String.valueOf(Structures.structures.select().size()));
        report.add("TOTAL VILLAGES LOADED", String.valueOf(Structures.villages.select().size()));
        report.add("LOAD TIME", new DecimalFormat("###0.00").format(loadTime / 1000.0) + "s");
        report.print();

    }

}
