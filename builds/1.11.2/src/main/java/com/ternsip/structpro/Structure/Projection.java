package com.ternsip.structpro.Structure;

import com.ternsip.structpro.Utils.Report;
import net.minecraft.world.World;

import java.io.IOException;
import java.text.DecimalFormat;

/* Construct projection that determines structure state in the world */
public class Projection {

    private World world;
    private Structure structure;
    private Posture posture;
    private boolean liana;
    private long seed;

    public Projection(World world, Structure structure, Posture posture, boolean liana, long seed) {
        this.world = world;
        this.structure = structure;
        this.posture = posture;
        this.liana = liana;
        this.seed = seed;
    }

    /* Generate projection report */
    public Report report() {
        return structure.report()
                .post(posture.report())
                .post("WORLD", world.getWorldInfo().getWorldName())
                .post("DIMENSION", String.valueOf(world.provider.getDimension()));
    }

    /* Project structure into the world according it posture and flags */
    public Report project() {
        Report result = report();
        long startTime = System.currentTimeMillis();
        try {
            structure.project(world, posture, liana, seed);
            result.pref("PASTED", "SUCCESS");
        } catch (IOException ioe) {
            result.pref("NOT PASTED", ioe.getMessage());
        }
        long spentTime = (System.currentTimeMillis() - startTime);
        return result.post("SPENT TIME", new DecimalFormat("###0.00").format(spentTime / 1000.0) + "s");
    }
    
}
