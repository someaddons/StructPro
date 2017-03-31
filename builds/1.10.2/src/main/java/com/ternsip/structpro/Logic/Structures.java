package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Biome;
import com.ternsip.structpro.Structure.Method;
import com.ternsip.structpro.Structure.Projector;
import com.ternsip.structpro.Utils.Report;

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

    /* Selector for all types of projectors */
    static final Selector<Projector> structures = new Selector<Projector>();
    static final Selector<ArrayList<Projector>> villages = new Selector<ArrayList<Projector>>();
    static final Selector<Projector> saves = new Selector<Projector>();

    /* Load structure from file */
    static void loadStructure(File file) {
        try {
            final Projector projector = new Projector(file);
            if (file.getPath().contains(Configurator.getSchematicsSavesFolder().getPath())) {
                saves.add(projector.getMethod(), projector);
                saves.add(projector.getBiome(), projector);
                saves.add(projector.getFile().getPath(), projector);
                return;
            }
            structures.add(projector.getMethod(), projector);
            structures.add(projector.getBiome(), projector);
            structures.add(projector.getFile().getPath(), projector);
            String parent = file.getParent().toLowerCase().replace("\\", "/").replace("//", "/");
            if (parent.contains("/village/") || parent.contains("/town/")) {
                Pattern pPattern = Pattern.compile(Pattern.quote(parent), Pattern.CASE_INSENSITIVE);
                ArrayList<ArrayList<Projector>> village = villages.select(pPattern);
                if (village.isEmpty()) {
                    villages.add(parent, new ArrayList<Projector>(){{add(projector);}});
                } else {
                    village.get(0).add(projector);
                }
            }
            int width = projector.getWidth();
            int height = projector.getHeight();
            int length = projector.getLength();
            if (Configurator.ADDITIONAL_OUTPUT) {
                new Report()
                        .add("LOAD", file.getPath())
                        .add("SIZE", "[W=" + width + ";H=" + height + ";L=" + length + "]")
                        .add("LIFT", String.valueOf(projector.getLift()))
                        .add("METHOD", projector.getMethod().name)
                        .add("BIOME", projector.getBiome().name)
                        .print();
            }
        } catch (IOException ioe) {
            if (Configurator.ADDITIONAL_OUTPUT) {
                new Report()
                        .add("CAN'T LOAD SCHEMATIC", file.getPath())
                        .add("ERROR", ioe.getMessage())
                        .print();
            }
        }
    }

    /* Load structures from folder */
    private static void loadStructures(File folder) {
        new Report().add("LOADING SCHEMATICS FROM", folder.getPath()).print();
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
        for (ArrayList<Projector> village : villages.select()) {
            Collections.sort(village, new Comparator<Projector>(){
                @Override
                public int compare(final Projector lhs,Projector rhs) {
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
