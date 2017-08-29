package com.ternsip.structpro.logic.generation;


import com.ternsip.structpro.logic.Configurator;
import com.ternsip.structpro.structure.Structure;
import com.ternsip.structpro.universe.world.UWorld;
import com.ternsip.structpro.universe.world.WorldData;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Set;

/**
 * Helps to determine object limitations
 * @author Ternsip
 */
@SuppressWarnings({"WeakerAccess"})
class Limiter {

    /**
     * Check if a chunk is outside of the border
     * @param chunkX Chunk X coordinate
     * @param chunkZ Chunk Z coordinate
     * @return chunk is outside of a border
     */
    static boolean isChunkOutsideBorder(int chunkX, int chunkZ) {
        return  chunkX > Configurator.WORLD_CHUNK_BORDER || chunkX < -Configurator.WORLD_CHUNK_BORDER ||
                chunkZ > Configurator.WORLD_CHUNK_BORDER || chunkZ < -Configurator.WORLD_CHUNK_BORDER;
    }

    /** Check if the world gives possibility to operate */
    static boolean isPossibleDimension(UWorld world) {
        String dimID = String.valueOf(world.getDimensionID());
        String dimName = world.getDimensionName();
        Set<String> dims = Configurator.SPAWN_DIMENSIONS;
        return dims.contains(dimID) || dims.contains(dimName);
    }

    /** Check if the world gives possibility to operate with villages */
    static boolean isPossibleDimensionVillage(UWorld world) {
        String dimID = String.valueOf(world.getDimensionID());
        String dimName = world.getDimensionName();
        Set<String> dims = Configurator.VILLAGE_DIMENSIONS;
        return dims.contains(dimID) || dims.contains(dimName);
    }

    public static void useStructure(UWorld world, Structure structure) {
        WorldData worldData = world.getWorldData();
        NBTTagCompound data = worldData.getData();
        NBTTagCompound spawned = data.getCompoundTag("spawned");
        String key = structure.getFile().getPath();
        int used = spawned.getInteger(key);
        spawned.setInteger(key, used + 1);
        data.setTag("spawned", spawned);
        worldData.markDirty();
    }

    public static boolean isStructureLimitExceeded(UWorld world, Structure structure) {
        return world.getWorldData().getData().getCompoundTag("spawned").getInteger(structure.getFile().getPath()) >= Configurator.STRUCTURE_SPAWN_QUOTA;
    }


}
