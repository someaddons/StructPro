package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.*;
import com.ternsip.structpro.Universe.Universe;
import com.ternsip.structpro.Utils.BlockPos;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import static com.ternsip.structpro.Universe.Blocks.Classifier.HEAT_RAY;
import static com.ternsip.structpro.Universe.Blocks.Classifier.OVERLOOK;

/**
 * Single structure distributor
 * Unequivocally determine structure positions
 * @author  Ternsip
 * @since JDK 1.6
 */
public class Construction {

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

    /** Structures calibrate sets in attempting order for each biome */
    private static final HashMap<Biome, ArrayList<ArrayList<Structure>>> bioOrder = new HashMap<Biome, ArrayList<ArrayList<Structure>>>() {{
        for (Biome biome : Biome.values()) {
            ArrayList<ArrayList<Structure>> order = new ArrayList<ArrayList<Structure>>();
            for (ArrayList<Structure> entire : spawnOrder) {
                ArrayList<Structure> acceptable = new ArrayList<Structure>();
                for (Structure structure : entire) {
                    try {
                        structure.matchBiome(biome);
                        acceptable.add(structure);
                    } catch (IOException ignored) {
                    }
                }
                order.add(acceptable);
            }
            put(biome, order);
        }
    }};

    /**
     * Obtain array of construction projections calibrated inside chunk
     * @param world Target world object
     * @param chunkX Chunk X coordinate
     * @param chunkZ Chunk Z coordinate
     * @return Array of spawned projections
     */
    public static ArrayList<Projection> obtain(final World world, final int chunkX, final int chunkZ) {
        return new ArrayList<Projection>() {{
            for (int drop = drops(world, chunkX, chunkZ); drop > 0; --drop) {
                Random random = getRandom(world, chunkX, chunkZ);
                boolean spawned = false;
                Biome biome = Biome.valueOf(Universe.getBiome(world, new BlockPos(chunkX * 16, 64, chunkZ * 16)));
                for (ArrayList<Structure> structures : bioOrder.get(biome)) {
                    int cx = chunkX * 16 + Math.abs(random.nextInt()) % 16;
                    int cz = chunkZ * 16 + Math.abs(random.nextInt()) % 16;
                    Structure structure = Utils.select(structures, random.nextLong());
                    if (structure != null) {
                        Projection projection = construct(world, cx, cz, random.nextLong(), structure);
                        if (projection != null) {
                            add(projection);
                            spawned = true;
                            break;
                        }
                    }
                }
                if (!spawned) {
                    new Report().post("GIVE UP CALIBRATE IN CHUNK", "[X=" + chunkX + ";Z=" + chunkZ + "]").print();
                }
            }
        }};
    }

    /**
     * Construct projection in the world in specific position
     * In case structure can not be constructed null will be returned
     * @param world Target world object
     * @param worldX World block X starting coordinate
     * @param worldZ World block Z starting coordinate
     * @param seed Constructing seed
     * @param structure Structure to construct
     * @return Constructed projection or null
     */
    static Projection construct(World world, int worldX, int worldZ, long seed, Structure structure) {
        try {
            return calibrate(world, worldX, worldZ, seed, structure);
        } catch (IOException ioe) {
            if (Configurator.ADDITIONAL_OUTPUT) {
                structure.report().pref(new Report().post("NOT CALIBRATED", ioe.getMessage()).post("AT", "[X=" + worldX + ";Z=" + worldZ + "]")).print();
            }
            return null;
        }
    }

    /**
     * Get number of structures which spawns in chunk
     * @param world Target world object
     * @param chunkX Chunk X coordinate
     * @param chunkZ Chunk Z coordinate
     * @return Number of structures
     */
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

    /**
     * Check if a chunk is outside of the border
     * @param chunkX Chunk X coordinate
     * @param chunkZ Chunk Z coordinate
     * @return chunk is outside of a border
     */
    static boolean outsideBorder(int chunkX, int chunkZ) {
        return  chunkX > Configurator.WORLD_CHUNK_BORDER || chunkX < -Configurator.WORLD_CHUNK_BORDER ||
                chunkZ > Configurator.WORLD_CHUNK_BORDER || chunkZ < -Configurator.WORLD_CHUNK_BORDER;
    }

    /**
     * Calibrate candidate in certain position in the world
     * @param world Target world object
     * @param worldX World block X starting coordinate
     * @param worldZ World block Z starting coordinate
     * @param seed calibrate seed
     * @param candidate Desired structure to construct
     * @throws IOException Structure can't calibrate due conditions
     * @return Calibrated projection
     */
    @SuppressWarnings({"ConstantConditions"})
    public static Projection calibrate(World world, int worldX, int worldZ, long seed, Structure candidate) throws IOException {
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

    /**
     * Get random generator for specific world chunk
     * @param world Target world object
     * @param chunkX Chunk X coordinate
     * @param chunkZ Chunk Z coordinate
     * @return Random generator
     */
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
