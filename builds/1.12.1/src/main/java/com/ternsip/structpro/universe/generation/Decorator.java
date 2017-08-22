package com.ternsip.structpro.universe.generation;

import com.ternsip.structpro.logic.generation.Construction;
import com.ternsip.structpro.logic.generation.Village;
import com.ternsip.structpro.structure.Projection;
import com.ternsip.structpro.universe.world.UWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/**
 * Overrides default world generator
 * Basically fires when chunk populates
 * Decorate chunk generation process
 * @author  Ternsip
 */
public class Decorator implements IWorldGenerator {

    /**
     * Chunk population band method
     * @param random the chunk specific {@link Random}
     * @param chunkX the chunk X coordinate of this chunk
     * @param chunkZ the chunk Z coordinate of this chunk
     * @param world The minecraft world we're generating for
     * @param generator Generating rig
     * @param provider Requesting the world generation rig
     */
    @Override
    public void generate(Random random,
                         final int chunkX,
                         final int chunkZ,
                         final World world,
                         IChunkGenerator generator,
                         IChunkProvider provider) {

        for (Projection projection : Construction.obtain(new UWorld(world), chunkX, chunkZ)) {
            projection.project(false).print();
        }

        for (Projection projection : Village.obtain(new UWorld(world), chunkX, chunkZ)) {
            projection.project(false).print();
        }

    }

}
