package com.ternsip.structpro.universe.biomes;


import net.minecraft.world.biome.BiomeGenBase;

/**
 * Biome wrapper class
 * @author Ternsip
 */
@SuppressWarnings({"WeakerAccess"})
public class UBiome {

    /** Minecraft native biome */
    private BiomeGenBase biome;

    /** Construct from minecraft native biome */
    public UBiome(BiomeGenBase biome) {
        this.biome = biome;
    }

    /** Check if biome is in valid state */
    public boolean isValid() {
        return biome != null && biome.biomeName != null;
    }

    /** Returns biome path */
    public String getPath() {
        return biome.biomeName;
    }

}
