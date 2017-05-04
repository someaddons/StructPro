package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Universe.Universe;
import com.ternsip.structpro.Utils.Report;
import net.minecraft.world.World;

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

    /** Tick generation step progress */
    public static void tick() {
        if (!active) {
            return;
        }
        int rSize = 2 * size + 1;
        for (int cnt = 0; cnt < step && progress < rSize * rSize; ++progress, ++cnt) {
            Universe.generate(world, startX - size + progress / rSize, startZ - size + progress % rSize);
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
     * @param size Number of chunks for x and z axis in each direction
     */
    public static void activate(World world, int startX, int startZ, int step, int size) {
        if (active) {
            new Report().post("WORLD GEN", "INTERRUPT").print();
        }
        Pregen.world = world;
        Pregen.step = step;
        Pregen.size = size;
        Pregen.startX = startX;
        Pregen.startZ = startZ;
        Pregen.progress = 0;
        Pregen.active = true;
        new Report().post("WORLD GEN", "START").print();
    }

    /** Deactivate generation process */
    public static void deactivate() {
        new Report().post("WORLD GEN", "FINISH").print();
        active = false;
    }

}
