package com.ternsip.structpro.structure;

import com.ternsip.structpro.universe.world.UWorld;
import com.ternsip.structpro.universe.utils.Report;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Construct projection that determines blueprint state in the world
 * @author Ternsip
 */
public class Projection {

    /** Target world */
    private final UWorld uWorld;

    /** Target blueprint */
    private final Blueprint blueprint;

    /** Posture transformation */
    private final Posture posture;

    /** Generation seed */
    private final long seed;

    /** Default constructor */
    public Projection(UWorld uWorld, Blueprint blueprint, Posture posture, long seed) {
        this.uWorld = uWorld;
        this.blueprint = blueprint;
        this.posture = posture;
        this.seed = seed;
    }

    /**
     * Combine projection report
     * @return Generated report
     */
    public Report report() {
        return blueprint.report()
                .post(posture.report())
                .post("WORLD", uWorld.getWorldName())
                .post("DIMENSION", String.valueOf(uWorld.getDimensionID()));
    }

    /**
     * Project blueprint into the world according it posture and flags
     * @param isInsecure Projection will be insecure
     * @return Result report
     */
    public Report project(boolean isInsecure) {
        Report result = report();
        long startTime = System.currentTimeMillis();
        try {
            blueprint.project(uWorld, posture, seed, isInsecure);
            result.pref("PASTED", "SUCCESS");
        } catch (IOException ioe) {
            result.pref("NOT PASTED", ioe.getMessage());
        }
        long spentTime = (System.currentTimeMillis() - startTime);
        return result.post("SPENT TIME", new DecimalFormat("###0.00").format(spentTime / 1000.0) + "s");
    }

    public Blueprint getBlueprint() {
        return blueprint;
    }

    public Posture getPosture() {
        return posture;
    }

    public UWorld getWorld() {
        return uWorld;
    }
}
