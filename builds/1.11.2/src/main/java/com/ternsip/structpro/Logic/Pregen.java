package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Universe.Universe;
import com.ternsip.structpro.Utils.Report;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;

/**
 * World pre-generation helper
 * @author  Ternsip
 * @since JDK 1.6
 */
public class Pregen {

    /** Number of chunk to generate per one step */
    private static int step = 16;

    /** Target world to generate */
    private static World world;

    /** How much chunks already processed */
    private static int progress = 0;

    /** Generation radius in chunks */
    private static int size = 0;

    /** Central chunk */
    private static int startX, startZ;

    /** Activated or not */
    private static boolean active = false;

    /** Skip chunks without structures */
    private static boolean skip = false;

    /** Tick generation step progress */
    public static void tick() {
        if (!active) {
            return;
        }
        int rSize = 2 * size + 1;
        for (int cnt = 0; cnt < step && progress < rSize * rSize; ++progress, ++cnt) {
            int chunkX = startX - size + progress / rSize;
            int chunkZ = startZ - size + progress % rSize;
            if (Universe.isDecorated(world, chunkX, chunkZ)) {
                continue;
            }
            if (skip && Construction.drops(world, chunkX, chunkZ) <= 0 && Village.drops(world, chunkX, chunkZ) <= 0) {
                continue;
            }
            Universe.decorate(world, chunkX, chunkZ);
        }
        new Report().post("WORLD GEN", progress + "/" + rSize * rSize).print();
        if (progress >= rSize * rSize) {
            deactivate();
        }
        Universe.saveWorlds(world);
    }

    /**
     * Activate generation process
     * @param world Target world
     * @param startX Starting chunk X coordinate
     * @param startZ Starting chunk Z coordinate
     * @param step Number of chunks to process per step
     * @param skip Chunks with no structures
     * @param progress Start generation progress from given number
     * @param size Number of chunks for x and z axis in each direction
     */
    public static void activate(World world, int startX, int startZ, int step, int size, boolean skip, int progress) {
        if (active) {
            new Report().post("WORLD GEN", "INTERRUPT").print();
        }
        Pregen.world = world;
        Pregen.skip = skip;
        Pregen.step = Math.max(1, step);
        Pregen.size = Math.max(0, size);
        Pregen.startX = startX;
        Pregen.startZ = startZ;
        Pregen.progress = Math.max(0, progress);
        Pregen.active = true;
        new Report().post("WORLD GEN", "START").print();
    }

    /** Deactivate generation process */
    public static void deactivate() {
        new Report().post("WORLD GEN", "FINISH").print();
        active = false;
    }

}
