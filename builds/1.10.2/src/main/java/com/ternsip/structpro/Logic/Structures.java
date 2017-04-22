package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Biome;
import com.ternsip.structpro.Structure.Method;
import com.ternsip.structpro.Structure.Structure;
import com.ternsip.structpro.Utils.Pool;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Selector;
import com.ternsip.structpro.Utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

/**
 * Structure static storage
 * Holds all loaded structures in selectors
 * Are able to load structures on fly
 * @author  Ternsip
 * @since JDK 1.6
 */
public class Structures {

    /** Selector for structures */
    public static final Selector<Structure> structures = new Selector<Structure>();

    /** Selector for villages */
    public static final Selector<ArrayList<Structure>> villages = new Selector<ArrayList<Structure>>();

    /** Selector for savings */
    public static final Selector<Structure> saves = new Selector<Structure>();

    /**
     * Register structure to storage
     * @param structure Structure instance
     */
    private static void load(final Structure structure) {
        if (structure.getFile().getPath().contains(Configurator.getSchematicsSavesFolder().getPath())) {
            saves.add(structure.getMethod(), structure);
            saves.add(structure.getBiome(), structure);
            saves.add(structure.getFile().getPath(), structure);
            return;
        }
        structures.add(structure.getMethod(), structure);
        structures.add(structure.getBiome(), structure);
        structures.add(structure.getFile().getPath(), structure);
        String parent = structure.getFile().getParent().toLowerCase().replace("\\", "/").replace("//", "/");
        if (parent.contains("/village/") || parent.contains("/town/")) {
            Pattern pPattern = Pattern.compile(Pattern.quote(parent), Pattern.CASE_INSENSITIVE);
            ArrayList<ArrayList<Structure>> village = villages.select(pPattern);
            if (village.isEmpty()) {
                villages.add(parent, new ArrayList<Structure>(){{add(structure);}});
            } else {
                village.get(0).add(structure);
            }
        }
    }

    /**
     * Load all structures from folder or from file
     * Works in parallel threads
     * @param folder Folder with structures or Structure File to load
     */
    public static void load(File folder) {
        new Report().post("LOADING SCHEMATICS FROM", folder.getPath()).print();
        Stack<File> folders = new Stack<File>();
        folders.add(folder);
        final ConcurrentLinkedQueue<Structure> loads = new ConcurrentLinkedQueue<Structure>();
        Pool pool = new Pool();
        while (!folders.empty()) {
            for (final File file : Utils.getFileList(folders.pop())) {
                if (file.isFile()) {
                    Thread action = new Thread() {
                        public void run() {
                            try {
                                Structure structure = new Structure(file);
                                int width = structure.getWidth();
                                int height = structure.getHeight();
                                int length = structure.getLength();
                                if (Configurator.ADDITIONAL_OUTPUT) {
                                    new Report()
                                            .post("LOAD", file.getPath())
                                            .post("SIZE", "[W=" + width + ";H=" + height + ";L=" + length + "]")
                                            .post("LIFT", String.valueOf(structure.getLift()))
                                            .post("METHOD", structure.getMethod().name)
                                            .post("BIOME", structure.getBiome().name)
                                            .print();
                                }
                                loads.add(structure);
                            } catch (IOException ioe) {
                                if (Configurator.ADDITIONAL_OUTPUT) {
                                    new Report()
                                            .post("CAN'T LOAD SCHEMATIC", file.getPath())
                                            .post("ERROR", ioe.getMessage())
                                            .print();
                                }
                            }
                        }
                    };
                    pool.add(action);
                } else if (file.isDirectory()) {
                    folders.add(file);
                }
            }
        }
        pool.wait(3600);
        for (Structure structure : loads) {
            load(structure);
        }
        sortVillages();
    }

    /**
     * Sort village structures by size
     * Firstly goes structures with smallest clearance
     */
    private static void sortVillages() {
        for (ArrayList<Structure> village : villages.select()) {
            Collections.sort(village, new Comparator<Structure>(){
                @Override
                public int compare(final Structure lhs,Structure rhs) {
                    int lSize = Math.max(lhs.getWidth(), lhs.getLength());
                    int rSize = Math.max(rhs.getWidth(), rhs.getLength());
                    if (lSize == rSize) return 0;
                    return rSize > lSize ? 1 : -1;
                }
            });
        }
    }

    static {
        long startTime = System.currentTimeMillis();
        load(Configurator.SCHEMATIC_FOLDER);
        long loadTime = (System.currentTimeMillis() - startTime);
        Report report = new Report();
        for (Method method : Method.values()) {
            report.post("METHOD " + method.name.toUpperCase(), String.valueOf(Structures.structures.select(method).size()));
        }
        for (Biome biome : Biome.values()) {
            report.post("BIOME " + biome.name.toUpperCase(), String.valueOf(Structures.structures.select(biome).size()));
        }
        report.post("TOTAL STRUCTURES LOADED", String.valueOf(Structures.structures.select().size()));
        report.post("TOTAL VILLAGES LOADED", String.valueOf(Structures.villages.select().size()));
        report.post("LOAD TIME", new DecimalFormat("###0.00").format(loadTime / 1000.0) + "s");
        report.print();
    }

}
