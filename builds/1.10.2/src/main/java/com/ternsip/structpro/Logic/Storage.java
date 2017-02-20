package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Projector;
import com.ternsip.structpro.Structure.Structure.Biome;
import com.ternsip.structpro.Structure.Structure.Method;
import com.ternsip.structpro.Utils.Report;

import java.io.File;
import java.io.IOException;
import java.util.*;

class Storage {

    private ArrayList<Projector> projectors = new ArrayList<Projector>();

    /* All possible projectors for each type of method */
    private HashMap<Method, ArrayList<Projector>> methodProjectors = new HashMap<Method, ArrayList<Projector>>(){{
        for (Method method : Method.values()) {
            put(method, new ArrayList<Projector>());
        }
    }};

    /* All possible projectors for each type of biome */
    private HashMap<Biome, ArrayList<Projector>> biomeProjectors = new HashMap<Biome, ArrayList<Projector>>(){{
        for (Biome biome : Biome.values()) {
            put(biome, new ArrayList<Projector>());
        }
    }};

    /* Villages structures */
    private HashMap<String, Integer> villageIndices = new HashMap<String, Integer>();
    private ArrayList<ArrayList<Projector>> villages = new ArrayList<ArrayList<Projector>>();

    /* Load structure from file */
    void loadStructure(File file) {
        try {
            final Projector projector = new Projector(file);
            projectors.add(projector);
            methodProjectors.get(projector.getMethod()).add(projector);
            biomeProjectors.get(projector.getBiome()).add(projector);
            String parent = file.getParent().toLowerCase().replace("\\", "/").replace("//", "/");
            if (parent.contains("/village/") || parent.contains("/town/")) {
                if (!villageIndices.containsKey(parent)) {
                    villageIndices.put(parent, villages.size());
                    villages.add(new ArrayList<Projector>());
                }
                villages.get(villageIndices.get(parent)).add(projector);
            }
            int width = projector.getWidth();
            int height = projector.getHeight();
            int length = projector.getLength();
            if (Loader.additionalOutput) {
                new Report()
                        .add("LOAD", file.getPath())
                        .add("SIZE", "[W=" + width + ";H=" + height + ";L=" + length + "]")
                        .add("LIFT", String.valueOf(projector.getLift()))
                        .add("METHOD", projector.getMethod().name)
                        .add("BIOME", projector.getBiome().name)
                        .print();
            }
        } catch (IOException ioe) {
            if (Loader.additionalOutput) {
                new Report()
                        .add("CAN'T LOAD SCHEMATIC", file.getPath())
                        .add("ERROR", ioe.getMessage())
                        .print();
            }
        }
    }

    /* Sort village structures by size */
    void sortVillages() {
        for (ArrayList<Projector> village : villages) {
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

    /* Select all structures */
    ArrayList<Projector> select() {
        return projectors;
    }

    /* Select structures by biome */
    ArrayList<Projector> select(Biome biome) {
        return biomeProjectors.get(biome);
    }

    /* Select structures by method */
    ArrayList<Projector> select(Method method) {
        return methodProjectors.get(method);
    }

    /* Select structures that matches any method */
    ArrayList<Projector> select(Method[] methods) {
        ArrayList<Projector> result = new ArrayList<Projector>();
        for (Method method : methods) {
            result.addAll(select(method));
        }
        return result;
    }

    /* Select structures that matches any method */
    ArrayList<Projector> select(Biome[] biomes) {
        ArrayList<Projector> result = new ArrayList<Projector>();
        for (Biome biome : biomes) {
            result.addAll(select(biome));
        }
        return result;
    }

    /* Returns suitable projectors for file name */
    ArrayList<Projector> select(String name) {
        ArrayList<Projector> result = new ArrayList<Projector>();
        for (Projector projector : select()) {
            if (projector.getOriginFile().getPath().toLowerCase().contains(name.toLowerCase())) {
                result.add(projector);
            }
        }
        return result;
    }

    /* Select village by name */
    ArrayList<Projector> selectVillage(String name) {
        for (HashMap.Entry<String, Integer> entry : villageIndices.entrySet()) {
            if (entry.getKey().toLowerCase().contains(name)) {
                return villages.get(entry.getValue());
            }
        }
        return null;
    }

    ArrayList<ArrayList<Projector>> getVillages() {
        return villages;
    }
}
