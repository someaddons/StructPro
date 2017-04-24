package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Projection;
import com.ternsip.structpro.Structure.Structure;
import com.ternsip.structpro.Universe.Universe;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Village constructor
 * @author  Ternsip
 * @since JDK 1.6
 */
public class Village extends Construction {

    /**
     * Obtain array of village projections calibrated inside chunk
     * @param world Target world object
     * @param chunkX Chunk X coordinate
     * @param chunkZ Chunk Z coordinate
     * @return Array of spawned projections
     */
    public static ArrayList<Projection> obtain(final World world, final int chunkX, final int chunkZ) {
        return new ArrayList<Projection>() {{
            for (int drop = drops(world, chunkX, chunkZ); drop > 0; --drop) {
                Random random = getRandom(world, chunkX, chunkZ);
                ArrayList<Structure> village = Utils.select(Structures.villages.select(), random.nextLong());
                if (village != null) {
                    addAll(combine(world, village, chunkX, chunkZ, random.nextLong()));
                }
            }
        }};
    }

    /**
     * Generate projection set combined from village that spawned in specific position
     * @param world Target world object
     * @param village Village structures
     * @param chunkX Chunk X coordinate
     * @param chunkZ Chunk Z coordinate
     * @param seed Combination seed
     * @return Array of spawned projections
     */
    public static ArrayList<Projection> combine(final World world, final ArrayList<Structure> village, final int chunkX, final int chunkZ, final long seed) {
        ArrayList<Projection> result = new ArrayList<Projection>();
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
            Structure candidate = village.get(i);
            int realX = chunkX * 16 + offsetX;
            int realZ = chunkZ * 16 + offsetZ;
            int curSize = Math.max(candidate.getWidth(), candidate.getLength());
            maxSize = Math.max(maxSize, curSize);
            offsetX += maxSize;
            Projection projection = construct(world, realX, realZ, random.nextLong(), candidate);
            if (projection != null) {
                result.add(projection);
            }
        }
        new Report().post("VILLAGE", villageName).post("CHUNK", "[X=" + chunkX + ";Z=" + chunkZ + "]").post("TOTAL SPAWNED", String.valueOf(result.size())).print();
        return result;
    }

    /**
     * Get drops in certain chunk in the world
     * @param world Target world object
     * @param chunkX Chunk X coordinate
     * @param chunkZ Chunk Z coordinate
     * @return Amount of villages spawned in chunk
     */
    private static int drops(World world, int chunkX, int chunkZ) {
        if (outsideBorder(chunkX, chunkZ)) {
            return 0;
        }
        String dimID = String.valueOf(Universe.getDimensionID(world));
        String dimName = String.valueOf(Universe.getDimensionName(world));
        Random random = getRandom(world, chunkX, chunkZ);
        HashSet<String> dims = Configurator.VILLAGE_DIMENSIONS;
        double density = Configurator.DENSITY_VILLAGE;
        if (!dims.contains(dimID) && !dims.contains(dimName)) {
            return 0;
        }
        return (int) density + (random.nextDouble() <= (density - (int) density) ? 1 : 0);
    }

    /**
     * Get random for world chunk
     * @param world Target world object
     * @param chunkX Chunk X coordinate
     * @param chunkZ Chunk Z coordinate
     * @return Random generator
     */
    private static Random getRandom(World world, int chunkX, int chunkZ) {
        long seed = world.getSeed();
        long chunkIndex = (long)chunkX << 32 | chunkZ & 0xFFFFFFFFL;
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
