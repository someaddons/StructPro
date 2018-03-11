package com.ternsip.structpro.logic.generation;

import com.ternsip.structpro.logic.Configurator;
import com.ternsip.structpro.structure.Posture;
import com.ternsip.structpro.structure.Projection;
import com.ternsip.structpro.structure.Region;
import com.ternsip.structpro.structure.Structure;
import com.ternsip.structpro.universe.biomes.Biomus;
import com.ternsip.structpro.universe.blocks.UBlockPos;
import com.ternsip.structpro.universe.utils.Report;
import com.ternsip.structpro.universe.world.UWorld;

import java.io.IOException;
import java.util.Random;

import static com.ternsip.structpro.universe.blocks.Classifier.HEAT_RAY;
import static com.ternsip.structpro.universe.blocks.Classifier.OVERLOOK;

/**
 * Provides ways to control object constructing in the world
 *
 * @author Ternsip
 */
public abstract class Constructor {

    /**
     * Calibrate candidate in certain position in the world
     *
     * @param world     Target world object
     * @param worldX    World block X starting coordinate
     * @param worldZ    World block Z starting coordinate
     * @param seed      calibrate seed
     * @param candidate Desired structure to construct
     * @return Calibrated projection
     * @throws IOException Structure can't calibrate due conditions
     */
    @SuppressWarnings({"ConstantConditions"})
    public static Projection calibrate(UWorld world, int worldX, int worldZ, long seed, Structure candidate) throws IOException {
        Random random = new Random(seed);
        int rotX = 0, rotY = random.nextInt() % 4, rotZ = 0;
        boolean flipX = random.nextBoolean(), flipY = false, flipZ = random.nextBoolean();
        Posture posture = candidate.getPosture(worldX, 64, worldZ, rotX, rotY, rotZ, flipX, flipY, flipZ);
        candidate.matchBiome(Biomus.valueOf(world.getBiome(new UBlockPos(posture.getPosX(), posture.getPosY(), posture.getPosZ()))));
        Region surface = new Region(world, posture.getPosX(), posture.getPosZ(), posture.getSizeX(), posture.getSizeZ(), OVERLOOK);
        Region bottom = new Region(world, posture.getPosX(), posture.getPosZ(), posture.getSizeX(), posture.getSizeZ(), HEAT_RAY);
        candidate.matchAccuracy(surface, bottom);
        int worldY = candidate.getBestY(surface, bottom, random.nextLong());
        posture = candidate.getPosture(worldX, worldY, worldZ, rotX, rotY, rotZ, flipX, flipY, flipZ);
        return new Projection(world, candidate, posture, random.nextLong());
    }

    /**
     * Construct projection in the world in specific position
     * In case Structure can not be constructed null will be returned
     *
     * @param world     Target world object
     * @param worldX    World block X starting coordinate
     * @param worldZ    World block Z starting coordinate
     * @param seed      Constructing seed
     * @param structure Structure to construct
     * @return Constructed projection or null
     */
    public static Projection construct(UWorld world, int worldX, int worldZ, long seed, Structure structure) {
        try {
            return calibrate(world, worldX, worldZ, seed, structure);
        } catch (IOException ioe) {
            if (Configurator.ADDITIONAL_OUTPUT) {
                structure.report().pref(new Report().post("NOT CALIBRATED", ioe.getMessage()).post("AT", "[X=" + worldX + ";Z=" + worldZ + "]")).print();
            }
            return null;
        }
    }

}
