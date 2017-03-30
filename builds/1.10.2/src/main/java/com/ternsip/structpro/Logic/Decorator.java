package com.ternsip.structpro.Logic;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/* Decorate chunk generation process */
public class Decorator extends Distributor implements IWorldGenerator {

    @Override
    public void generate(Random randomDefault, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        gen(world, chunkX, chunkZ);
    }

}
