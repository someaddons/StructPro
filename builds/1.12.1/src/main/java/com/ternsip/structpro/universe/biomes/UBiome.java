package com.ternsip.structpro.universe.biomes;

import net.minecraft.world.biome.Biome;

/**
 * Biome wrapper class
 * @author Ternsip
 */
@SuppressWarnings({"WeakerAccess"})
public class UBiome {

    /** Minecraft native biome */
    private Biome biome;

    /** Construct from minecraft native biome */
    public UBiome(Biome biome) {
        this.biome = biome;
    }

    /** Check if biome is in valid state */
    public boolean isValid() {
        return biome != null && biome.getRegistryName() != null;
    }

    /** Returns biome path */
    public String getPath() {
        return biome.getRegistryName().getResourcePath();
    }

}
