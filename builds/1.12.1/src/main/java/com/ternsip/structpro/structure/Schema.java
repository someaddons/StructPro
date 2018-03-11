package com.ternsip.structpro.structure;

import com.ternsip.structpro.universe.world.UWorld;

import java.io.IOException;
import java.util.Random;

public interface Schema extends Cuboid, Reportable {

    /**
     * Volume width limitation
     */
    int WIDTH_LIMIT = 1024;

    /**
     * Volume height limitation
     */
    int HEIGHT_LIMIT = 256;

    /**
     * Volume length limitation
     */
    int LENGTH_LIMIT = 1024;

    /**
     * Volume size limitation
     */
    long VOLUME_LIMIT = 256 * 256 * 256;

    /**
     * Inject schema into specific position in the world
     * Unsafe for cross-versioned data for insecure mod
     * It's not a transaction
     * Notifying and marking dirty all changes after it's done
     *
     * @param world      World instance
     * @param posture    Transformation state
     * @param seed       Projection seed
     * @param isInsecure Projection will be insecure
     * @throws IOException If blueprint failed to project
     */
    void project(UWorld world, Posture posture, long seed, boolean isInsecure) throws IOException;

    /**
     * Projects block with index to the world according posture
     * Not notifying or marking anything for update
     *
     * @param world      The world to project
     * @param posture    Projection posture
     * @param index      Blueprint index to paste
     * @param isInsecure Projection will be insecure
     * @param random     Random object
     */
    void project(UWorld world, Posture posture, int index, boolean isInsecure, Random random);

}
