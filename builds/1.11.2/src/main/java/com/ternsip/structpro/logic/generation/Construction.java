package com.ternsip.structpro.logic.generation;

import com.ternsip.structpro.logic.Configurator;
import com.ternsip.structpro.logic.Structures;
import com.ternsip.structpro.structure.Method;
import com.ternsip.structpro.structure.Projection;
import com.ternsip.structpro.structure.Structure;
import com.ternsip.structpro.universe.biomes.Biomus;
import com.ternsip.structpro.universe.blocks.UBlockPos;
import com.ternsip.structpro.universe.utils.Report;
import com.ternsip.structpro.universe.utils.Utils;
import com.ternsip.structpro.universe.world.UWorld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Single Structure distributor
 * Unequivocally determine Structure positions
 * @author  Ternsip
 */
@SuppressWarnings({"WeakerAccess"})
public class Construction extends Constructor {

    /** Structures calibrate sets in attempting order */
    private static final ArrayList<ArrayList<Structure>> spawnOrder = new ArrayList<ArrayList<Structure>>() {{
        add(Structures.structures.select());
        add(Structures.structures.select(new Method[]{Method.BASIC}));
        add(Structures.structures.select(new Method[]{Method.BASIC}));
        add(Structures.structures.select(new Method[]{Method.BASIC}));
        add(Structures.structures.select(new Method[]{Method.UNDERWATER}));
        add(Structures.structures.select(new Method[]{Method.AFLOAT}));
        add(Structures.structures.select(new Method[]{Method.SKY, Method.HILL, Method.UNDERGROUND}));
        add(Structures.structures.select(new Method[]{Method.SKY, Method.HILL, Method.UNDERGROUND}));
        add(Structures.structures.select(new Method[]{Method.SKY, Method.HILL, Method.UNDERGROUND}));
    }};

    /** Structures calibrate sets in attempting order for each biomus */
    private static final HashMap<Biomus, ArrayList<ArrayList<Structure>>> bioOrder = new HashMap<Biomus, ArrayList<ArrayList<Structure>>>() {{
        for (Biomus biomus : Biomus.values()) {
            ArrayList<ArrayList<Structure>> order = new ArrayList<>();
            for (ArrayList<Structure> entire : spawnOrder) {
                ArrayList<Structure> acceptable = new ArrayList<>();
                for (Structure structure : entire) {
                    try {
                        structure.matchBiome(biomus);
                        acceptable.add(structure);
                    } catch (IOException ignored) {
                    }
                }
                order.add(acceptable);
            }
            put(biomus, order);
        }
    }};

    /**
     * Obtain array of construction projections calibrated inside chunk
     * @param world Target world object
     * @param chunkX Chunk X coordinate
     * @param chunkZ Chunk Z coordinate
     * @return Array of spawned projections
     */
    public static ArrayList<Projection> obtain(final UWorld world, final int chunkX, final int chunkZ) {
        ArrayList<Projection> projections = new ArrayList<>();
        for (int drop = drops(world, chunkX, chunkZ); drop > 0; --drop) {
            Random random = getRandom(world, chunkX, chunkZ);
            boolean spawned = false;
            Biomus biomus = Biomus.valueOf(world.getBiome(new UBlockPos(chunkX * 16, 64, chunkZ * 16)));
            for (ArrayList<Structure> structures : bioOrder.get(biomus)) {
                int cx = chunkX * 16 + Math.abs(random.nextInt()) % 16;
                int cz = chunkZ * 16 + Math.abs(random.nextInt()) % 16;
                Structure structure = Utils.select(structures, random.nextLong());
                if (structure != null && !Limiter.isStructureLimitExceeded(world, structure)) {
                    Projection projection = construct(world, cx, cz, random.nextLong(), structure);
                    if (projection != null) {
                        projections.add(projection);
                        Limiter.useStructure(world, structure);
                        spawned = true;
                        break;
                    }
                }
            }
            if (!spawned) {
                new Report().post("GIVE UP CALIBRATE IN CHUNK", "[X=" + chunkX + ";Z=" + chunkZ + "]").print();
            }
        }
        return projections;
    }

    public static int drops(UWorld world, int chunkX, int chunkZ) {
        if (Limiter.isChunkOutsideBorder(chunkX, chunkZ) || !Limiter.isPossibleDimension(world)) {
            return 0;
        }
        Random random = getRandom(world, chunkX, chunkZ);
        double density = Configurator.DENSITY;
        return (int) density + (random.nextDouble() <= (density - (int) density) ? 1 : 0);
    }

    /**
     * Get random generator for specific world chunk
     * @param world Target world object
     * @param chunkX Chunk X coordinate
     * @param chunkZ Chunk Z coordinate
     * @return Random generator
     */
    private static Random getRandom(UWorld world, int chunkX, int chunkZ) {
        long seed = world.getSeed();
        long chunkIndex = (long)chunkX << 32 | chunkZ & 0xFFFFFFFFL;
        Random random = new Random(chunkIndex);
        random.setSeed(random.nextLong() ^ seed);
        random.setSeed(random.nextLong() ^ chunkIndex);
        random.setSeed(random.nextLong() ^ seed);
        random.setSeed(random.nextLong());
        return random;
    }

}
