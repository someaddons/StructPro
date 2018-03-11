package com.ternsip.structpro.logic.generation;

import com.ternsip.structpro.logic.Configurator;
import com.ternsip.structpro.logic.Structures;
import com.ternsip.structpro.structure.Projection;
import com.ternsip.structpro.structure.Structure;
import com.ternsip.structpro.universe.utils.Report;
import com.ternsip.structpro.universe.utils.Utils;
import com.ternsip.structpro.universe.world.UWorld;

import java.util.ArrayList;
import java.util.Random;

/**
 * Village constructor
 *
 * @author Ternsip
 */
@SuppressWarnings({"WeakerAccess"})
public class Village extends Constructor {

    /**
     * Obtain array of village projections calibrated inside chunk
     *
     * @param world  Target world object
     * @param chunkX Chunk X coordinate
     * @param chunkZ Chunk Z coordinate
     * @return Array of spawned projections
     */
    public static ArrayList<Projection> obtain(final UWorld world, final int chunkX, final int chunkZ) {
        ArrayList<Projection> projections = new ArrayList<>();
        for (int drop = drops(world, chunkX, chunkZ); drop > 0; --drop) {
            Random random = getRandom(world, chunkX, chunkZ);
            ArrayList<Structure> village = Utils.select(Structures.villages.select(), random.nextLong());
            if (village != null) {
                projections.addAll(combine(world, village, chunkX, chunkZ, random.nextLong()));
            }
        }
        return projections;
    }

    /**
     * Generate projection set combined from village that spawned in specific position
     *
     * @param world   Target world object
     * @param village Village structures
     * @param chunkX  Chunk X coordinate
     * @param chunkZ  Chunk Z coordinate
     * @param seed    Combination seed
     * @return Array of spawned projections
     */
    public static ArrayList<Projection> combine(final UWorld world, final ArrayList<Structure> village, final int chunkX, final int chunkZ, final long seed) {
        ArrayList<Projection> projections = new ArrayList<>();
        Random random = new Random(seed);
        String villageName = village.get(0).getFile().getParent();
        int side = (int) (1 + Math.sqrt(village.size()));
        for (int i = 0, maxSize = 0, offsetX = 0, offsetZ = 0; i < village.size(); ++i) {
            int posX = i % side;
            if (posX == 0) {
                offsetX = 0;
                offsetZ += maxSize;
                maxSize = 0;
            }
            Structure structure = village.get(i);
            int realX = chunkX * 16 + offsetX;
            int realZ = chunkZ * 16 + offsetZ;
            int curSize = Math.max(structure.getWidth(), structure.getLength());
            maxSize = Math.max(maxSize, curSize);
            offsetX += maxSize;
            if (!Limiter.isStructureLimitExceeded(world, structure)) {
                Projection projection = construct(world, realX, realZ, random.nextLong(), structure);
                if (projection != null) {
                    projections.add(projection);
                    Limiter.useStructure(world, structure);
                }
            }
        }
        new Report().post("VILLAGE", villageName).post("CHUNK", "[X=" + chunkX + ";Z=" + chunkZ + "]").post("TOTAL SPAWNED", String.valueOf(projections.size())).print();
        return projections;
    }

    /**
     * Get drops in certain chunk in the world
     *
     * @param world  Target world object
     * @param chunkX Chunk X coordinate
     * @param chunkZ Chunk Z coordinate
     * @return Amount of villages spawned in chunk
     */
    public static int drops(UWorld world, int chunkX, int chunkZ) {
        if (Limiter.isChunkOutsideBorder(chunkX, chunkZ) || !Limiter.isPossibleDimensionVillage(world)) {
            return 0;
        }
        Random random = getRandom(world, chunkX, chunkZ);
        double density = Configurator.DENSITY_VILLAGE;
        return (int) density + (random.nextDouble() <= (density - (int) density) ? 1 : 0);
    }

    /**
     * Get random for world chunk
     *
     * @param world  Target world object
     * @param chunkX Chunk X coordinate
     * @param chunkZ Chunk Z coordinate
     * @return Random generator
     */
    private static Random getRandom(UWorld world, int chunkX, int chunkZ) {
        long seed = world.getSeed();
        long chunkIndex = (long) chunkX << 32 | chunkZ & 0xFFFFFFFFL;
        Random random = new Random(chunkIndex);
        random.setSeed(random.nextLong());
        random.setSeed(random.nextLong() ^ chunkIndex);
        random.setSeed(random.nextLong() ^ seed);
        random.setSeed(random.nextLong());
        random.setSeed(random.nextLong() ^ chunkIndex);
        random.setSeed(random.nextLong() ^ seed);
        random.setSeed(random.nextLong());
        return random;
    }


}
