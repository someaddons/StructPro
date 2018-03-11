package com.ternsip.structpro.logic;

import com.ternsip.structpro.universe.utils.Report;
import com.ternsip.structpro.universe.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;

/**
 * Loads configuration and holds mod data
 * Defined variables must be public static and not final due of reflect changes
 * New variable types requires parsing and combination handlers
 *
 * @author Ternsip
 */
@SuppressWarnings({"WeakerAccess"})
public class Configurator {

    /**
     * [0..+INF, Overflowing probability, Recommended 0.0035] Spawn probability per chunk
     */
    public static double DENSITY = 0.0035;

    /**
     * [0..+INF, Overflowing probability, Recommended 0.00035] Village probability per chunk
     */
    public static double DENSITY_VILLAGE = 0.00035;

    /**
     * [0..+INF, Recommended 1.0] Calibration accuracy criteria, 0.5 means two times more accurate
     */
    public static double ACCURACY = 1.0;

    /**
     * [-INF; +INF, Recommended 0] Force lift up every structure (recommended 0)
     */
    public static int FORCE_LIFT = 0;

    /**
     * [Recommended true] Set chest calibrate priority to native loot if it exists
     */
    public static boolean NATIVE_LOOT = true;

    /**
     * [Recommended true] Ban modded items from spawning
     */
    public static boolean ONLY_VANILLA_LOOT = false;

    /**
     * [0..1, Normalized probability, Recommended 0.5] Chest loot chance
     */
    public static double LOOT_CHANCE = 0.5;

    /**
     * [0..27, Recommended 2] Min number of stacks per chest inclusive
     */
    public static int MIN_CHEST_ITEMS = 2;

    /**
     * [0..27, Recommended 7] Max number of stacks per chest exclusive
     */
    public static int MAX_CHEST_ITEMS = 7;

    /**
     * [0..64, Recommended 12] Max item stack size for chest loot
     */
    public static int MAX_CHEST_STACK_SIZE = 12;

    /**
     * [Recommended true] Spawn mobs in generated structures
     */
    public static boolean SPAWN_MOBS = true;

    /**
     * [Recommended true] Pasting mob spawners only with mobs that have eggs
     */
    public static boolean MOB_SPAWNERS_EGGS_ONLY = true;

    /**
     * [Recommended true] Print details about schematics loading and failed paste attempts
     */
    public static boolean ADDITIONAL_OUTPUT = true;

    /**
     * [Recommended true] Additional World ticks for spawning process
     */
    public static boolean TICKER = true;

    /**
     * [0..+INF, Measured in chunks, Recommended 4096] Generation border
     */
    public static int WORLD_CHUNK_BORDER = 4096;

    /**
     * [Relative path, Use "/", Recommended schematics] Schematics goes this folder
     */
    public static File SCHEMATIC_FOLDER = new File("schematics");

    /**
     * [Recommended 1024] Maximal spawn quantity for each structure
     */
    public static int STRUCTURE_SPAWN_QUOTA = 1024;

    /**
     * [Case sensitive, Recommended -1, 0] Allow spawning structures only in this dimensions
     */
    public static HashSet<String> SPAWN_DIMENSIONS = new HashSet<String>() {{
        add("-1");
        add("0");
    }};

    /**
     * [Case sensitive, Recommended 0] Allow spawning villages only in specified dimensions
     */
    public static HashSet<String> VILLAGE_DIMENSIONS = new HashSet<String>() {{
        add("0");
    }};

    /**
     * [BlockSrc -> BlockDst, Comma separated, Case non-sensitive] Replaces blocks to another
     */
    public static HashSet<String> REPLACE_BLOCKS = new HashSet<String>() {{
        add("BARRIER -> NULL");
        add("BEDROCK -> STONE");
        add("IRON_BLOCK -> STONE");
        add("GOLD_BLOCK -> STONE");
        add("DIAMOND_BLOCK -> STONE");
        add("LAPIS_BLOCK -> STONE");
        add("EMERALD_BLOCK -> STONE");
        add(".*COMMAND.* -> REDSTONE_BLOCK");
        add(".*STRUCTURE.* -> GOLD_BLOCK");
        add("WOOL -> LOG");
        add("BEACON -> QUARTZ_BLOCK");
        add("FIRE -> AIR");
    }};

    /**
     * [Block names, Comma separated, Case non-sensitive] Protect blocks from pasting structures over it
     */
    public static HashSet<String> PROTECT_BLOCKS = new HashSet<String>() {{
        add("BEDROCK");
        add(".*COMMAND.*");
    }};

    /**
     * [Item names, Comma separated, Case non-sensitive] Exclude items from possible loot
     */
    public static HashSet<String> EXCLUDE_ITEMS = new HashSet<String>() {{
        add(".*BARRIER.*");
        add(".*COMMAND.*");
        add(".*BEACON.*");
        add(".*CRYSTAL.*");
        add(".*VOID.*");
        add(".*SHULKER.*");
        add(".*STRUCTURE.*");
        add(".*SPAWNER.*");
        add(".*PORTAL.*");
    }};

    /**
     * Enable auth-module to register and login in game
     */
    public static boolean AUTH_ENABLE = false;

    /**
     * Get schematics savings folder
     *
     * @return Schematic save folder
     */
    public static File getSchematicsSavesFolder() {
        return new File(SCHEMATIC_FOLDER, "Saves");
    }

    /**
     * Combine config object to parsable string
     *
     * @param object Arbitrary source object
     * @return Object combined to string
     */
    @SuppressWarnings({"unchecked"})
    private static String combine(Object object) {
        if (HashSet.class.isAssignableFrom(object.getClass())) {
            return Utils.join(Utils.toArray((HashSet<String>) object), ", ");
        }
        return object.toString();
    }

    /**
     * Parse string according given class
     *
     * @param string Source string
     * @param clazz  Target class
     * @return Parsed class object
     */
    private static Object parse(String string, Class clazz) throws IOException {
        if (Integer.class.isAssignableFrom(clazz)) {
            return (int) Double.parseDouble(string);
        }
        if (Boolean.class.isAssignableFrom(clazz)) {
            return Boolean.parseBoolean(string);
        }
        if (Double.class.isAssignableFrom(clazz)) {
            return Double.parseDouble(string);
        }
        if (HashSet.class.isAssignableFrom(clazz)) {
            return Utils.toHashSet(Utils.tokenize(string, ","));
        }
        if (File.class.isAssignableFrom(clazz)) {
            return new File(string);
        }
        throw new IOException("Unknown instance: " + clazz.getSimpleName());
    }

    /**
     * Load configuration from file
     *
     * @param file Target file to load
     */
    private static void load(File file) {
        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            config.load(fis);
            for (String field : Utils.getFields(Configurator.class)) {
                try {
                    String property = config.getProperty(field);
                    Object origin = Utils.getFieldValue(Configurator.class, Configurator.class, field);
                    if (property == null) {
                        throw new IOException("Field doesn't exists");
                    }
                    if (origin == null) {
                        throw new IOException("Field has a null value");
                    }
                    Utils.setFieldValue(Configurator.class, Configurator.class, field, parse(property, origin.getClass()));
                } catch (Throwable throwable) {
                    new Report().post("FIELD", field).post("ERROR", throwable.getMessage()).print();
                }
            }
        } catch (Throwable throwable) {
            new Report().post("CONFIG", file.getPath()).post("ERROR", throwable.getMessage()).print();
        }
    }

    /**
     * Save configuration to file
     *
     * @param file Target file to save
     */
    private static void save(File file) {
        Properties config = new Properties();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            for (String field : Utils.getFields(Configurator.class)) {
                try {
                    config.setProperty(field, combine(Utils.getFieldValue(Configurator.class, Configurator.class, field)));
                } catch (Throwable throwable) {
                    new Report().post("FIELD", field).post("ERROR", throwable.getMessage()).print();
                }
            }
            config.store(fos, null);
        } catch (Throwable throwable) {
            new Report().post("CONFIG", file.getPath()).post("ERROR", throwable.getMessage()).print();
        }
    }

    /**
     * Configure mod settings
     * Loads configuration from file parse and save back
     *
     * @param file Target file
     */
    public static void configure(File file) {
        if (new File(file.getParent()).mkdirs()) {
            new Report().post("CREATE CONFIG", file.getParent()).print();
        }
        if (file.exists()) {
            load(file);
        }
        save(file);
        Report report = new Report();
        for (String field : Utils.getFields(Configurator.class)) {
            try {
                report.post(field, combine(Utils.getFieldValue(Configurator.class, Configurator.class, field)));
            } catch (Throwable ignored) {
            }
        }
        report.print();
    }

}