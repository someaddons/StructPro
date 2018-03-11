package com.ternsip.structpro.logic.generation;

import com.ternsip.structpro.universe.utils.Report;
import com.ternsip.structpro.universe.world.UWorld;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * World pre-generation helper
 *
 * @author Ternsip
 */
public class Pregen {

    /**
     * Number of chunk to generate per one step
     */
    private static int step = 16;

    /**
     * Target world to generate
     */
    private static UWorld world;

    /**
     * How much chunks already processed
     */
    private static int progress = 0;

    /**
     * Generation radius in chunks
     */
    private static int size = 0;

    /**
     * Central chunk
     */
    private static int startX, startZ;

    /**
     * Activated or not
     */
    private static boolean active = false;

    /**
     * Skip chunks without structures
     */
    private static boolean skip = false;

    /**
     * Tick generation step progress
     */
    public static void tick() {
        if (!active) {
            return;
        }
        int rSize = 2 * size + 1;
        for (int cnt = 0; cnt < step && progress < rSize * rSize; ++progress, ++cnt) {
            int chunkX = startX - size + progress / rSize;
            int chunkZ = startZ - size + progress % rSize;
            if (world.isDecorated(chunkX, chunkZ)) {
                continue;
            }
            if (skip && Construction.drops(world, chunkX, chunkZ) <= 0 && Village.drops(world, chunkX, chunkZ) <= 0) {
                continue;
            }
            world.decorate(chunkX, chunkZ);
        }
        new Report().post("WORLD GEN", progress + "/" + rSize * rSize).print();
        if (progress >= rSize * rSize) {
            deactivate();
        }
        world.saveWorlds();
    }

    /**
     * Activate generation process
     *
     * @param world    Target world
     * @param startX   Starting chunk X coordinate
     * @param startZ   Starting chunk Z coordinate
     * @param step     Number of chunks to process per step
     * @param skip     Chunks with no structures
     * @param progress Start generation progress from given number
     * @param size     Number of chunks for x and z axis in each direction
     */
    public static void activate(UWorld world, int startX, int startZ, int step, int size, boolean skip, int progress) {
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

    /**
     * Deactivate generation process
     */
    public static void deactivate() {
        new Report().post("WORLD GEN", "FINISH").print();
        active = false;
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        tick();
    }

}
