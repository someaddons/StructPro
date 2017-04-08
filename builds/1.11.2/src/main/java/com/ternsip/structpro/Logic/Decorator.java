package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Projection;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/* Decorate chunk generation process */
public class Decorator implements IWorldGenerator {

    @Override
    public void generate(Random randomDefault, final int chunkX, final int chunkZ, final World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        spawn(world, chunkX, chunkZ);
    }

    /* Project all generated structures in chunk */
    private static void spawn(World world, int chunkX, int chunkZ) {
        /* Paste generated constructions */
        for (Projection projection : Construction.generate(world, chunkX, chunkZ)) {
            projection.project().print();
        }
        /* Paste generated villages */
        for (Projection projection : Village.generate(world, chunkX, chunkZ)) {
            projection.project().print();
        }
    }

}
