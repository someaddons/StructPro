package com.ternsip.structpro.logic;

import com.ternsip.structpro.structure.Method;
import com.ternsip.structpro.structure.Structure;
import com.ternsip.structpro.universe.biomes.Biomus;
import com.ternsip.structpro.universe.utils.Pool;
import com.ternsip.structpro.universe.utils.Report;
import com.ternsip.structpro.universe.utils.Selector;
import com.ternsip.structpro.universe.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

/**
 * Structure static storage
 * Holds all loaded structures in selectors
 * Are able to load structures on fly
 * @author  Ternsip
 */
public class Structures {

    /** Selector for structures */
    public static final Selector<Structure> structures = new Selector<>();

    /** Selector for villages */
    public static final Selector<ArrayList<Structure>> villages = new Selector<>();

    /** Selector for savings */
    public static final Selector<Structure> saves = new Selector<>();

    /**
     * Register Structure to storage
     * @param structure Structure instance
     */
    private static void load(final Structure structure) {
        if (structure.getFile().getPath().contains(Configurator.getSchematicsSavesFolder().getPath())) {
            saves.add(structure.getMethod(), structure);
            saves.add(structure.getBiomus(), structure);
            saves.add(structure.getFile().getPath(), structure);
            return;
        }
        structures.add(structure.getMethod(), structure);
        structures.add(structure.getBiomus(), structure);
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
        Stack<File> folders = new Stack<>();
        folders.add(folder);
        final ConcurrentLinkedQueue<Structure> loads = new ConcurrentLinkedQueue<>();
        Pool pool = new Pool();
        while (!folders.empty()) {
            for (final File file : Utils.getFileList(folders.pop())) {
                if (file.isDirectory()) {
                    folders.add(file);
                    continue;
                }
                if (!file.isFile()) {
                    continue;
                }
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
                                        .post("BIOME", structure.getBiomus().name)
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
            village.sort((lhs, rhs) -> {
                int lSize = Math.max(lhs.getWidth(), lhs.getLength());
                int rSize = Math.max(rhs.getWidth(), rhs.getLength());
                if (lSize == rSize) return 0;
                return rSize > lSize ? 1 : -1;
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
        for (Biomus biomus : Biomus.values()) {
            report.post("BIOME " + biomus.name.toUpperCase(), String.valueOf(Structures.structures.select(biomus).size()));
        }
        report.post("TOTAL STRUCTURES LOADED", String.valueOf(Structures.structures.select().size()));
        report.post("TOTAL VILLAGES LOADED", String.valueOf(Structures.villages.select().size()));
        report.post("LOAD TIME", new DecimalFormat("###0.00").format(loadTime / 1000.0) + "s");
        report.print();
    }

}
