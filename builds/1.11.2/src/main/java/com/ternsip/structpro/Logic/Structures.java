package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Biome;
import com.ternsip.structpro.Structure.Method;
import com.ternsip.structpro.Structure.Structure;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Selector;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;
import java.util.regex.Pattern;

/* Structures control class */
class Structures {

    /* Selector for all types of structures */
    static final Selector<Structure> structures = new Selector<Structure>();
    static final Selector<ArrayList<Structure>> villages = new Selector<ArrayList<Structure>>();
    static final Selector<Structure> saves = new Selector<Structure>();

    /* Load structure from file */
    static void loadStructure(File file) {
        try {
            final Structure structure = new Structure(file);
            if (file.getPath().contains(Configurator.getSchematicsSavesFolder().getPath())) {
                saves.add(structure.getMethod(), structure);
                saves.add(structure.getBiome(), structure);
                saves.add(structure.getFile().getPath(), structure);
                return;
            }
            structures.add(structure.getMethod(), structure);
            structures.add(structure.getBiome(), structure);
            structures.add(structure.getFile().getPath(), structure);
            String parent = file.getParent().toLowerCase().replace("\\", "/").replace("//", "/");
            if (parent.contains("/village/") || parent.contains("/town/")) {
                Pattern pPattern = Pattern.compile(Pattern.quote(parent), Pattern.CASE_INSENSITIVE);
                ArrayList<ArrayList<Structure>> village = villages.select(pPattern);
                if (village.isEmpty()) {
                    villages.add(parent, new ArrayList<Structure>(){{add(structure);}});
                } else {
                    village.get(0).add(structure);
                }
            }
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
        } catch (IOException ioe) {
            if (Configurator.ADDITIONAL_OUTPUT) {
                new Report()
                        .post("CAN'T LOAD SCHEMATIC", file.getPath())
                        .post("ERROR", ioe.getMessage())
                        .print();
            }
        }
    }

    /* Load structures from folder */
    private static void loadStructures(File folder) {
        new Report().post("LOADING SCHEMATICS FROM", folder.getPath()).print();
        Stack<File> folders = new Stack<File>();
        folders.add(folder);
        while (!folders.empty()) {
            File[] listOfFiles = folders.pop().listFiles();
            for (File file : listOfFiles != null ? listOfFiles : new File[0]) {
                if (file.isFile()) {
                    loadStructure(file);
                } else if (file.isDirectory()) {
                    folders.add(file);
                }
            }
        }
        sortVillages();
    }

    /* Sort village structures by size */
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
        loadStructures(Configurator.SCHEMATIC_FOLDER);
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
