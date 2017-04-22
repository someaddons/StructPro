package com.ternsip.structpro.Structure;

import com.ternsip.structpro.Utils.Report;
import net.minecraft.world.World;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Construct projection that determines blueprint state in the world
 * @author Ternsip
 * @since JDK 1.6
 */
public class Projection {

    /** Target world */
    private final World world;

    /** Target blueprint */
    private final Blueprint blueprint;

    /** Posture transformation */
    private final Posture posture;

    /** Generation seed */
    private final long seed;

    /** Default constructor */
    public Projection(World world, Blueprint blueprint, Posture posture, long seed) {
        this.world = world;
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
                .post("WORLD", world.getWorldInfo().getWorldName())
                .post("DIMENSION", String.valueOf(world.provider.getDimension()));
    }

    /**
     * Project blueprint into the world according it posture and flags
     * @return Result report
     */
    public Report project() {
        Report result = report();
        long startTime = System.currentTimeMillis();
        try {
            blueprint.project(world, posture, seed);
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

    public World getWorld() {
        return world;
    }
}
