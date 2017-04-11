package com.ternsip.structpro.Structure;

import com.ternsip.structpro.Utils.Report;
import net.minecraft.world.World;

import java.io.IOException;
import java.text.DecimalFormat;

/* Construct projection that determines blueprint state in the world */
public class Projection {

    private World world;
    private Blueprint blueprint;
    private Posture posture;
    private long seed;

    public Projection(World world, Blueprint blueprint, Posture posture, long seed) {
        this.world = world;
        this.blueprint = blueprint;
        this.posture = posture;
        this.seed = seed;
    }

    /* Generate projection report */
    public Report report() {
        return blueprint.report()
                .post(posture.report())
                .post("WORLD", world.getWorldInfo().getWorldName())
                .post("DIMENSION", String.valueOf(world.provider.getDimension()));
    }

    /* Project blueprint into the world according it posture and flags */
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
