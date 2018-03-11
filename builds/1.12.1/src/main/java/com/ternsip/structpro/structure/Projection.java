package com.ternsip.structpro.structure;

import com.ternsip.structpro.universe.utils.Report;
import com.ternsip.structpro.universe.world.UWorld;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Construct projection that determines blueprint state in the world
 *
 * @author Ternsip
 */
public class Projection implements Reportable {

    /**
     * Target world
     */
    private final UWorld world;

    /**
     * Target blueprint
     */
    private final Schema schema;

    /**
     * Posture transformation
     */
    private final Posture posture;

    /**
     * Generation seed
     */
    private final long seed;

    /**
     * Default constructor
     */
    public Projection(UWorld world, Schema schema, Posture posture, long seed) {
        this.world = world;
        this.schema = schema;
        this.posture = posture;
        this.seed = seed;
    }

    /**
     * Combine projection report
     *
     * @return Generated report
     */
    @Override
    public Report report() {
        return getSchema().report()
                .post(getPosture().report())
                .post("WORLD", getWorld().getWorldName())
                .post("DIMENSION", String.valueOf(getWorld().getDimensionID()));
    }

    /**
     * Project blueprint into the world according it posture and flags
     *
     * @param isInsecure Projection will be insecure
     * @return Result report
     */
    public Report project(boolean isInsecure) {
        Report result = report();
        long startTime = System.currentTimeMillis();
        try {
            getSchema().project(getWorld(), getPosture(), getSeed(), isInsecure);
            result.pref("PASTED", "SUCCESS");
        } catch (IOException ioe) {
            result.pref("NOT PASTED", ioe.getMessage());
        }
        long spentTime = (System.currentTimeMillis() - startTime);
        return result.post("SPENT TIME", new DecimalFormat("###0.00").format(spentTime / 1000.0) + "s");
    }

    public Schema getSchema() {
        return schema;
    }

    public Posture getPosture() {
        return posture;
    }

    public UWorld getWorld() {
        return world;
    }

    private long getSeed() {
        return seed;
    }

}
