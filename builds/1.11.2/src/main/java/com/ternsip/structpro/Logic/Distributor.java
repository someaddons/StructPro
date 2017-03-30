package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Projector;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import com.ternsip.structpro.World.Cache.WorldCache;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/* Distributes world things generation */
class Distributor {

    /* Structures spawn sets in attempting order */
    static ArrayList<ArrayList<Projector>> spawnOrder = new ArrayList<ArrayList<Projector>>();

    /* Process chunk generations */
    static void gen(World world, int chunkX, int chunkZ) {
        for (int drop = drops(world, chunkX, chunkZ, false); drop > 0; --drop) {
            Random random = getRandom(world, chunkX, chunkZ, false);
            boolean spawned = false;
            for (ArrayList<Projector> projectors : spawnOrder) {
                if (projectors.size() > 0 && spawn(world, Utils.select(projectors, random.nextLong()), chunkX, chunkZ, random.nextLong())) {
                    spawned = true;
                    break;
                }
            }
            if (!spawned) {
                new Report().add("GIVE UP SPAWNING IN CHUNK", "[X=" + chunkX + ";Z=" + chunkZ + "]").print();
            }
        }
        for (int drop = drops(world, chunkX, chunkZ, true); drop > 0; --drop) {
            Random random = getRandom(world, chunkX, chunkZ, true);
            if (Structures.villages.select().size() > 0) {
                spawnVillage(world, Utils.select(Structures.villages.select(), random.nextLong()), chunkX, chunkZ, random.nextLong()).print();
            }
        }
    }

    /* Get drops in certain chunk in the world */
    private static int drops(World world, int chunkX, int chunkZ, boolean village) {
        if (outsideBorder(chunkX, chunkZ)) {
            return 0;
        }
        String dimID = String.valueOf(WorldCache.getDimensionID(world));
        String dimName = String.valueOf(WorldCache.getDimensionName(world));
        Random random = getRandom(world, chunkX, chunkZ, village);
        HashSet<String> dims = village ? Configurator.villageDimensions : Configurator.spawnDimensions;
        double density = village ? Configurator.densityVillage : Configurator.density;
        if (!dims.contains(dimID) && !dims.contains(dimName)) {
            return 0;
        }
        return (int) density + (random.nextDouble() <= (density - (int) density) ? 1 : 0);
    }

    /* Spawn village that starts in chunk */
    static Report spawnVillage(World world, ArrayList<Projector> village, int chunkX, int chunkZ, long seed) {
        Random random = new Random(seed);
        String villageName = village.get(0).getOriginFile().getParent();
        int side = (int) (1 + Math.sqrt(village.size()));
        int spawned = 0;
        for (int i = 0, maxSize = 0, offsetX = 0, offsetZ = 0; i < village.size(); ++i) {
            int posX = i % side;
            if (posX == 0) {
                offsetX = 0;
                offsetZ += maxSize;
                maxSize = 0;
            }
            Projector candidate = village.get(i);
            int realX = chunkX * 16 + offsetX;
            int realZ = chunkZ * 16 + offsetZ;
            int curSize = Math.max(candidate.getWidth(), candidate.getLength());
            maxSize = Math.max(maxSize, curSize);
            offsetX += maxSize;
            spawned += spawn(world, candidate, realX, 0, realZ, random.nextLong()) ? 1 : 0;
        }
        return new Report()
                .add("VILLAGE", villageName)
                .add("CHUNK", "[X=" + chunkX + ";Z=" + chunkZ + "]")
                .add("TOTAL SPAWNED", String.valueOf(spawned));
    }

    /* Check if a chunk is outside of the border */
    private static boolean outsideBorder(int chunkX, int chunkZ) {
        return  chunkX > Configurator.worldChunkBorder || chunkX < -Configurator.worldChunkBorder ||
                chunkZ > Configurator.worldChunkBorder || chunkZ < -Configurator.worldChunkBorder;
    }

    /* Spawn candidate in certain position in the world */
    private static boolean spawn(World world, Projector candidate, int worldX, int worldY, int worldZ, long seed) {
        Random random = new Random(seed);
        int rotX = 0, rotY = random.nextInt() % 4, rotZ = 0;
        boolean flipX = random.nextBoolean(), flipY = false, flipZ = random.nextBoolean();
        Report report = candidate.paste(world, worldX, worldY, worldZ, rotX, rotY, rotZ, flipX, flipY, flipZ, random.nextLong());
        if (report.isSuccess() || Configurator.additionalOutput) {
            report.print();
        }
        return report.isSuccess();
    }

    /* Spawn candidate in given world, chunk, seed */
    private static boolean spawn(World world, Projector candidate, int chunkX, int chunkZ, long seed) {
        Random random = new Random(seed);
        int cx = chunkX * 16 + Math.abs(random.nextInt()) % 16;
        int cz = chunkZ * 16 + Math.abs(random.nextInt()) % 16;
        return spawn(world, candidate, cx, 0, cz, random.nextLong());
    }

    /* Get random for world chunk */
    private static Random getRandom(World world, int chunkX, int chunkZ, boolean village) {
        long seed = world.getSeed();
        long chunkIndex = (long)chunkX << 32 | chunkZ & 0xFFFFFFFFL;
        Random random = new Random(chunkIndex);
        random.setSeed(random.nextLong() ^ seed);
        random.setSeed(random.nextLong() ^ chunkIndex);
        random.setSeed(random.nextLong() ^ seed);
        random.setSeed(random.nextLong());
        if (village) {
            random.setSeed(random.nextLong() ^ chunkIndex);
            random.setSeed(random.nextLong() ^ seed);
            random.setSeed(random.nextLong());
        }
        return random;
    }

}
