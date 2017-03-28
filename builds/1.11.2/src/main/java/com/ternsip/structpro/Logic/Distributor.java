package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Structure.Method;
import com.ternsip.structpro.Structure.Structure.Biome;
import com.ternsip.structpro.Structure.Projector;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import com.ternsip.structpro.WorldCache.WorldCache;
import net.minecraft.world.World;

import java.io.File;
import java.io.IOException;
import java.util.*;

/* Distributes world things generation */
class Distributor extends Configurator {

    /* Structures spawn sets in attempting order */
    private static ArrayList<ArrayList<Projector>> spawnOrder = new ArrayList<ArrayList<Projector>>(){{
            add(Structures.select(new Method[]{Method.BASIC, Method.UNDERWATER, Method.AFLOAT, Method.SKY, Method.HILL, Method.UNDERGROUND}));
            add(Structures.select(new Method[]{Method.BASIC}));
            add(Structures.select(new Method[]{Method.BASIC}));
            add(Structures.select(new Method[]{Method.BASIC}));
            add(Structures.select(new Method[]{Method.UNDERWATER}));
            add(Structures.select(new Method[]{Method.AFLOAT}));
            add(Structures.select(new Method[]{Method.SKY, Method.HILL, Method.UNDERGROUND}));
            add(Structures.select(new Method[]{Method.SKY, Method.HILL, Method.UNDERGROUND}));
            add(Structures.select(new Method[]{Method.SKY, Method.HILL, Method.UNDERGROUND}));
            add(Structures.select(new Biome[]{Biome.NETHER}));
            add(Structures.select(new Biome[]{Biome.SNOW}));
            add(Structures.select(new Biome[]{Biome.END}));
    }};

    /* Process chunk generations */
    static void structGen(World world, int chunkX, int chunkZ) {
        Random random = getRandom(world, chunkX, chunkZ);
        for (int drop = getDrops(world, chunkX, chunkZ, false); drop > 0; --drop) {
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
        for (int drop = getDrops(world, chunkX, chunkZ, true); drop > 0; --drop) {
            if (Structures.selectVillages().size() > 0) {
                spawnVillage(world, Utils.select(Structures.selectVillages(), random.nextLong()), chunkX, chunkZ, random).print();
            }
        }
    }

    static int getDrops(World world, int chunkX, int chunkZ, boolean village) {
        if (outsideBorder(chunkX, chunkZ)) {
            return 0;
        }
        String dimID = String.valueOf(WorldCache.getDimensionID(world));
        String dimName = String.valueOf(WorldCache.getDimensionName(world));
        Random random = getRandom(world, chunkX, chunkZ);
        if (village) {
            if (!villageDimensions.contains(dimID) && !villageDimensions.contains(dimName)) {
                return 0;
            }
            return (int) densityVillage + (random.nextDouble() <= (densityVillage - (int) densityVillage) ? 1 : 0);
        } else {
            if (!spawnDimensions.contains(dimID) && !spawnDimensions.contains(dimName)) {
                return 0;
            }
            return (int) density + (random.nextDouble() <= (density - (int) density) ? 1 : 0);
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

    /* Check if a chunk is outside of the border */
    private static boolean outsideBorder(int chunkX, int chunkZ) {
        return chunkX > worldChunkBorder || chunkX < -worldChunkBorder || chunkZ > worldChunkBorder || chunkZ < -worldChunkBorder;
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

    static Report saveStructure(World world, String name, int posX, int posY, int posZ, int width, int height, int length) {
        Report report = new Report()
                .add("WORLD FRAGMENT", name)
                .add("POS", "[X=" + posX + ";Y=" + posY + ";Z=" + posZ + "]")
                .add("SIZE", "[W=" + width + ";H=" + height + ";L=" + length + "]");
        try {
            File file = new File(getSchematicsSavesFolder(), name + ".schematic");
            Projector projector = new Projector(file, world, posX, posY, posZ, width, height, length);
            projector.saveSchematic(projector.getOriginFile());
            Structures.loadStructure(projector.getOriginFile());
            report.add("SAVED", projector.getOriginFile().getPath());
        } catch (IOException ioe) {
            report.add("NOT SAVED", ioe.getMessage());
        }
        return report;
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
