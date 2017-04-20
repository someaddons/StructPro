package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.*;
import com.ternsip.structpro.Universe.Universe;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import static com.ternsip.structpro.Universe.Blocks.Classifier.HEAT_RAY;
import static com.ternsip.structpro.Universe.Blocks.Classifier.OVERLOOK;

/* Distributes single structures */
class Construction {

    /* Structures spawn sets in attempting order */
    private static final ArrayList<ArrayList<Structure>> spawnOrder = new ArrayList<ArrayList<Structure>>(){{
        add(Structures.structures.select());
        add(Structures.structures.select(new Method[]{Method.BASIC}));
        add(Structures.structures.select(new Method[]{Method.BASIC}));
        add(Structures.structures.select(new Method[]{Method.BASIC}));
        add(Structures.structures.select(new Method[]{Method.UNDERWATER}));
        add(Structures.structures.select(new Method[]{Method.AFLOAT}));
        add(Structures.structures.select(new Method[]{Method.SKY, Method.HILL, Method.UNDERGROUND}));
        add(Structures.structures.select(new Method[]{Method.SKY, Method.HILL, Method.UNDERGROUND}));
        add(Structures.structures.select(new Method[]{Method.SKY, Method.HILL, Method.UNDERGROUND}));
        add(Structures.structures.select(new Biome[]{Biome.NETHER}));
        add(Structures.structures.select(new Biome[]{Biome.SNOW}));
        add(Structures.structures.select(new Biome[]{Biome.END}));
    }};

    /* Process chunk generations */
    static ArrayList<Projection> generate(final World world, final int chunkX, final int chunkZ) {
        return new ArrayList<Projection>() {{
            for (int drop = drops(world, chunkX, chunkZ); drop > 0; --drop) {
                Random random = getRandom(world, chunkX, chunkZ);
                boolean spawned = false;
                for (ArrayList<Structure> structures : spawnOrder) {
                    int cx = chunkX * 16 + Math.abs(random.nextInt()) % 16;
                    int cz = chunkZ * 16 + Math.abs(random.nextInt()) % 16;
                    Structure structure = Utils.select(structures, random.nextLong());
                    if (structure != null) {
                        Projection projection = construct(world, structure, cx, cz, random.nextLong());
                        if (projection != null) {
                            add(projection);
                            spawned = true;
                            break;
                        }
                    }
                }
                if (!spawned) {
                    new Report().post("GIVE UP SPAWNING IN CHUNK", "[X=" + chunkX + ";Z=" + chunkZ + "]").print();
                }
            }
        }};
    }

    /* Construct projection in the world in specific position */
    static Projection construct(World world, Structure structure, int worldX, int worldZ, long seed) {
        try {
            return spawn(world, structure, worldX, worldZ, seed);
        } catch (IOException ioe) {
            if (Configurator.ADDITIONAL_OUTPUT) {
                structure.report().pref(new Report().post("NOT SPAWNED", ioe.getMessage()).post("AT", "[X=" + worldX + ";Z=" + worldZ + "]")).print();
            }
            return null;
        }
    }

    /* Get drops in certain chunk in the world */
    private static int drops(World world, int chunkX, int chunkZ) {
        if (outsideBorder(chunkX, chunkZ)) {
            return 0;
        }
        String dimID = String.valueOf(Universe.getDimensionID(world));
        String dimName = String.valueOf(Universe.getDimensionName(world));
        Random random = getRandom(world, chunkX, chunkZ);
        HashSet<String> dims = Configurator.SPAWN_DIMENSIONS;
        double density = Configurator.DENSITY;
        if (!dims.contains(dimID) && !dims.contains(dimName)) {
            return 0;
        }
        return (int) density + (random.nextDouble() <= (density - (int) density) ? 1 : 0);
    }

    /* Check if a chunk is outside of the border */
    static boolean outsideBorder(int chunkX, int chunkZ) {
        return  chunkX > Configurator.WORLD_CHUNK_BORDER || chunkX < -Configurator.WORLD_CHUNK_BORDER ||
                chunkZ > Configurator.WORLD_CHUNK_BORDER || chunkZ < -Configurator.WORLD_CHUNK_BORDER;
    }

    /* Spawn candidate in certain position in the world */
    static Projection spawn(World world, Structure candidate, int worldX, int worldZ, long seed) throws IOException {
        Random random = new Random(seed);
        int rotX = 0, rotY = random.nextInt() % 4, rotZ = 0;
        boolean flipX = random.nextBoolean(), flipY = false, flipZ = random.nextBoolean();
        Posture posture = candidate.getPosture(worldX, 64, worldZ, rotX, rotY, rotZ, flipX, flipY, flipZ);
        candidate.matchBiome(Biome.valueOf(Universe.getBiome(world, new BlockPos(posture.getPosX(), posture.getPosY(), posture.getPosZ()))));
        Region surface = new Region(world, posture.getPosX(), posture.getPosZ(), posture.getSizeX(), posture.getSizeZ(), OVERLOOK);
        Region bottom = new Region(world, posture.getPosX(), posture.getPosZ(), posture.getSizeX(), posture.getSizeZ(), HEAT_RAY);
        candidate.matchAccuracy(surface, bottom);
        int worldY = candidate.getBestY(surface, bottom, random.nextLong());
        posture = candidate.getPosture(worldX, worldY, worldZ, rotX, rotY, rotZ, flipX, flipY, flipZ);
        return new Projection(world, candidate, posture, random.nextLong());
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
