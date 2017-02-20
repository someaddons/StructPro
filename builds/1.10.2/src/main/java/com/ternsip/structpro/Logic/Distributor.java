package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Structure.Method;
import com.ternsip.structpro.Structure.Structure.Biome;
import com.ternsip.structpro.Structure.Projector;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import com.ternsip.structpro.WorldCache.WorldCache;
import net.minecraft.world.World;

import java.util.*;

/* Assign array of clusters to biome map and distribute relative spawn chance */
class Distributor extends Loader {

    /* Structures spawn sets in attempting order */
    private static ArrayList<ArrayList<Projector>> spawnOrder = new ArrayList<ArrayList<Projector>>(){{
            add(storage.select(new Method[]{Method.BASIC, Method.UNDERWATER, Method.AFLOAT, Method.SKY, Method.HILL, Method.UNDERGROUND}));
            add(storage.select(new Method[]{Method.BASIC}));
            add(storage.select(new Method[]{Method.BASIC}));
            add(storage.select(new Method[]{Method.BASIC}));
            add(storage.select(new Method[]{Method.UNDERWATER}));
            add(storage.select(new Method[]{Method.AFLOAT}));
            add(storage.select(new Method[]{Method.SKY, Method.HILL, Method.UNDERGROUND}));
            add(storage.select(new Method[]{Method.SKY, Method.HILL, Method.UNDERGROUND}));
            add(storage.select(new Method[]{Method.SKY, Method.HILL, Method.UNDERGROUND}));
            add(storage.select(new Biome[]{Biome.NETHER}));
            add(storage.select(new Biome[]{Biome.SNOW}));
            add(storage.select(new Biome[]{Biome.END}));
    }};

    /* Process chunk generations */
    void structGen(World world, int chunkX, int chunkZ) {
        Random random = getRandom(world, chunkX, chunkZ);
        if (spawnDimensions.contains(WorldCache.getDimensionID(world))) {
            int drops = (int) density + (random.nextDouble() <= (density - (int) density) ? 1 : 0);
            for (int drop = 0; drop < drops; ++drop) {
                boolean spawned = false;
                for (ArrayList<Projector> projectors : spawnOrder) {
                    if (projectors.size() > 0 && spawn(world, Utils.select(projectors, random.nextLong()), chunkX, chunkZ, random)) {
                        spawned = true;
                        break;
                    }
                }
                if (!spawned) {
                    new Report().add("GIVE UP SPAWNING IN CHUNK", "[X=" + chunkX + ";Z=" + chunkZ + "]").print();
                }
            }
        }
        if (storage.getVillages().size() > 0 && villageDimensions.contains(WorldCache.getDimensionID(world))) {
            int drops = (int) densityVillage + (random.nextDouble() <= (densityVillage - (int) densityVillage) ? 1 : 0);
            for (int drop = 0; drop < drops; ++drop) {
                spawnVillage(world, Utils.select(storage.getVillages(), random.nextLong()), chunkX, chunkZ, random).print();
            }
        }
    }

    /* Spawn village that starts in chunk */
    static Report spawnVillage(World world, ArrayList<Projector> village, int chunkX, int chunkZ, Random random) {
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
            spawned += spawn(world, candidate, realX, 0, realZ, random) ? 1 : 0;
        }
        return new Report()
                .add("VILLAGE", villageName)
                .add("CHUNK", "[X=" + chunkX + ";Z=" + chunkZ + "]")
                .add("TOTAL SPAWNED", String.valueOf(spawned));
    }

    /* Spawn candidate in certain position in the world */
    private static boolean spawn(World world, Projector candidate, int worldX, int worldY, int worldZ, Random random) {
        int rotX = 0, rotY = random.nextInt() % 4, rotZ = 0;
        boolean flipX = random.nextBoolean(), flipY = false, flipZ = random.nextBoolean();
        Report report = candidate.paste(world, worldX, worldY, worldZ, rotX, rotY, rotZ, flipX, flipY, flipZ, random.nextLong());
        if (report.isSuccess() || additionalOutput) {
            report.print();
        }
        return report.isSuccess();
    }

    /* Spawn candidate in given world, chunk, seed */
    private static boolean spawn(World world, Projector candidate, int chunkX, int chunkZ, Random random) {
        int cx = chunkX * 16 + Math.abs(random.nextInt()) % 16;
        int cz = chunkZ * 16 + Math.abs(random.nextInt()) % 16;
        return spawn(world, candidate, cx, 0, cz, random);
    }

    /* Get random for world chunk */
    private static Random getRandom(World world, int chunkX, int chunkZ) {
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
