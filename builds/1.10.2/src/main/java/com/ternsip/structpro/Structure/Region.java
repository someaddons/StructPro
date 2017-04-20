package com.ternsip.structpro.Structure;

import com.ternsip.structpro.Universe.Blocks.Classifier;
import com.ternsip.structpro.Universe.Universe;
import net.minecraft.world.World;

/* Face of region */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Region {

    /* Characterizes roughness */
    private double variance;

    /* Average blocks height */
    private double average;

    /* Max block height in region */
    private int major;

    /* Min block height in region */
    private int minor;

    public Region(World world, int startX, int startZ, int sizeX, int sizeZ, Classifier classifier) {
        variance = 0;
        average = 0;
        major = 0;
        minor = 256;
        double total = 0;
        double squareSum = 0;
        for (int x = 0, wx = startX; x < sizeX; ++x, ++wx) {
            for (int z = 0, wz = startZ; z < sizeZ; ++z, ++wz) {
                int level = Universe.getHeight(world, classifier, wx, wz);
                major = Math.max(major, level);
                minor = Math.min(minor, level);
                total += level;
                squareSum += level * level;
            }
        }
        double area = sizeX * sizeZ;
        variance = Math.abs((squareSum - (total * total) / area) / (area - 1));
        average = total / area;
    }

    public double getVariance() {
        return variance;
    }

    public double getAverage() {
        return average;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public double getRoughness() {
        return Math.sqrt(variance);
    }
}
