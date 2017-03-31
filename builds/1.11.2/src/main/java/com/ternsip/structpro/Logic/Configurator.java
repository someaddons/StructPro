package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;

/* Loads configuration and holds mod data */
@SuppressWarnings({"WeakerAccess", "deprecation"})
public class Configurator {

    /* Spawn probability per chunk, to not generate set any negative value */
    public static double DENSITY = 0.0035;

    /* Village probability per chunk, to not generate set any negative value */
    public static double DENSITY_VILLAGE = 0.00035;

    /* Calibration accuracy */
    public static double ACCURACY = 1.0;

    /* Pull out structure from the ground and lift up (recommended 0) */
    public static int FORCE_LIFT = 0;

    /* Set chest spawn priority to native loot if it exists */
    public static boolean NATIVE_LOOT = false;

    /* Ban modded items from spawning */
    public static boolean ONLY_VANILLA_LOOT = true;

    /* Chest loot chance, to not loot set any negative value */
    public static double LOOT_CHANCE = 0.5;

    /* Min number of stack per chest inclusive */
    public static int MIN_CHEST_ITEMS = 2;

    /* Max number of stacks per chest exclusive */
    public static int MAX_CHEST_ITEMS = 7;

    /* Max item stack size for chest loot */
    public static int MAX_CHEST_STACK_SIZE = 12;

    /* Spawn mobs in generated structures */
    public static boolean SPAWN_MOBS = true;

    /* Spawn mobs in generated structures */
    public static boolean MOB_SPAWNERS_EGGS_ONLY = true;

    /* Print additional mod output to console */
    public static boolean ADDITIONAL_OUTPUT = true;

    /* Ignore block light update, disabling may speed up pasting process */
    public static boolean IGNORE_LIGHT = false;

    /* Max length in chunks for world to be generated, 4096 chunks = area 65536 x 65536 blocks */
    public static int WORLD_CHUNK_BORDER = 4096;

    /* Schematics loads from this folder */
    public static File SCHEMATIC_FOLDER = new File("schematics");

    /* Allow spawning structures only in dimensions with given ids, case sensitive */
    public static HashSet<String> SPAWN_DIMENSIONS = new HashSet<String>() {{
        add("-1");
        add("0");
        add("1");
    }};

    /* Allow spawning villages only in dimensions with given ids, case sensitive */
    public static HashSet<String> VILLAGE_DIMENSIONS = new HashSet<String>() {{
        add("0");
    }};

    /* Replaces this blocks with stone */
    public static HashSet<String> REPLACE_BLOCKS = new HashSet<String>() {{
        add("BARRIER -> NULL");
        add("BEDROCK -> STONE");
        add("IRON_BLOCK -> STONE");
        add("GOLD_BLOCK -> STONE");
        add("DIAMOND_BLOCK -> STONE");
        add("LAPIS_BLOCK -> STONE");
        add("EMERALD_BLOCK -> STONE");
        add(".*COMMAND.* -> REDSTONE_BLOCK");
        add(".*SPAWNER.* -> IRON_BLOCK");
        add("WOOL -> LOG");
        add("BEACON -> QUARTZ_BLOCK");
    }};

    /* Exclude items from possible loot */
    public static HashSet<String> EXCLUDE_ITEMS = new HashSet<String>() {{
        add(".*BARRIER.*");
        add(".*COMMAND.*");
    }};

    /* Get schematics savings file */
    public static File getSchematicsSavesFolder() {
        return new File(SCHEMATIC_FOLDER,"Saves");
    }

    /* Combine object to config string */
    @SuppressWarnings({"unchecked"})
    private static String combine(Object object) {
        if (HashSet.class.isAssignableFrom(object.getClass())) {
            return Utils.join(Utils.toArray((HashSet<String>) object), ", ");
        }
        return object.toString();
    }
    /* Parse string */
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

    /* Load configuration from file */
    private static void load(File file) {
        Properties config = new Properties();
        try {
            FileInputStream fis = new FileInputStream(file);
            try {
                config.load(fis);
                for (String field : Utils.getFields(Configurator.class)) {
                    try {
                        String property = config.getProperty(field);
                        Object origin = Utils.getFieldValue(Configurator.class, field);
                        if (property == null) {
                            throw new IOException("Field doesn't exists");
                        }
                        if (origin == null) {
                            throw new IOException("Field has a null value");
                        }
                        Utils.setFieldValue(Configurator.class, field, parse(property, origin.getClass()));
                    } catch (Throwable throwable) {
                        new Report().add("FIELD", field).add("ERROR", throwable.getMessage()).print();
                    }
                }
            } catch (Throwable throwable) {
                new Report().add("CONFIG", file.getPath()).add("ERROR", throwable.getMessage()).print();
            } finally {
                fis.close();
            }
        } catch (IOException ioe) {
            new Report().add("CONFIG FILE", file.getPath()).add("ERROR", ioe.getMessage()).print();
        }
    }

    /* Save configuration to file */
    private static void save(File file) {
        Properties config = new Properties();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                for (String field : Utils.getFields(Configurator.class)) {
                    config.setProperty(field, combine(Utils.getFieldValue(Configurator.class, field)));
                }
                config.store(fos, null);
            } catch (Throwable throwable) {
                new Report().add("CONFIG", file.getPath()).add("ERROR", throwable.getMessage()).print();
            } finally {
                fos.close();
            }
        } catch (IOException ioe) {
            new Report().add("CONFIG FILE", file.getPath()).add("ERROR", ioe.getMessage()).print();
        }
    }

    /* Configure mod settings */
    public static void configure(File file) {
        if (new File(file.getParent()).mkdirs()) {
            new Report().add("CREATE CONFIG", file.getParent()).print();
        }
        if (file.exists()) {
            load(file);
        }
        save(file);
        Report report = new Report();
        for (String field : Utils.getFields(Configurator.class)) {
            try {
                report.add(field, combine(Utils.getFieldValue(Configurator.class, field)));
            } catch (Throwable ignored) {}
        }
        report.print();
    }

}
