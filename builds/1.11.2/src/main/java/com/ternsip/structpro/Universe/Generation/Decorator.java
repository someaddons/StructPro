package com.ternsip.structpro.Universe.Generation;

import com.ternsip.structpro.Logic.Construction;
import com.ternsip.structpro.Logic.Village;
import com.ternsip.structpro.Structure.Projection;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/**
 * Overrides default world generator
 * Basically fires when chunk populates
 * Decorate chunk generation process
 * @author  Ternsip
 * @since JDK 1.6
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
    public void generate(Random random, final int chunkX, final int chunkZ, final World world, IChunkGenerator generator, IChunkProvider provider) {
        for (Projection projection : Construction.obtain(world, chunkX, chunkZ)) {
            projection.project(false).print();
        }
        for (Projection projection : Village.obtain(world, chunkX, chunkZ)) {
            projection.project(false).print();
        }
    }

}
