package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Projector;
import com.ternsip.structpro.Utils.Report;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/* Structures control class */
class Structures {

    /* Selector for all types of projectors */
    static Selector<Projector> structures = new Selector<Projector>();
    static Selector<ArrayList<Projector>> villages = new Selector<ArrayList<Projector>>();
    static Selector<Projector> saves = new Selector<Projector>();

    /* Load structure from file */
    static void loadStructure(File file) {
        try {
            final Projector projector = new Projector(file);
            if (file.getPath().contains(Configurator.getSchematicsSavesFolder().getPath())) {
                saves.add(projector.getMethod(), projector);
                saves.add(projector.getBiome(), projector);
                saves.add(projector.getOriginFile().getPath(), projector);
                return;
            }
            structures.add(projector.getMethod(), projector);
            structures.add(projector.getBiome(), projector);
            structures.add(projector.getOriginFile().getPath(), projector);
            String parent = file.getParent().toLowerCase().replace("\\", "/").replace("//", "/");
            if (parent.contains("/village/") || parent.contains("/town/")) {
                ArrayList<ArrayList<Projector>> village = villages.select(parent, true);
                if (village.isEmpty()) {
                    villages.add(parent, new ArrayList<Projector>(){{add(projector);}});
                } else {
                    village.get(0).add(projector);
                }
            }
            int width = projector.getWidth();
            int height = projector.getHeight();
            int length = projector.getLength();
            if (Configurator.additionalOutput) {
                new Report()
                        .add("LOAD", file.getPath())
                        .add("SIZE", "[W=" + width + ";H=" + height + ";L=" + length + "]")
                        .add("LIFT", String.valueOf(projector.getLift()))
                        .add("METHOD", projector.getMethod().name)
                        .add("BIOME", projector.getBiome().name)
                        .print();
            }
        } catch (IOException ioe) {
            if (Configurator.additionalOutput) {
                new Report()
                        .add("CAN'T LOAD SCHEMATIC", file.getPath())
                        .add("ERROR", ioe.getMessage())
                        .print();
            }
        }
    }

    /* Sort village structures by size */
    static void sortVillages() {
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

}
