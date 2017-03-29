package com.ternsip.structpro.WorldCache;

import com.ternsip.structpro.Logic.Mobs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashMap;
import java.util.Random;

/* Memoize world block communication */
@SuppressWarnings({"WeakerAccess", "unused", "deprecation"})
public class WorldCache {

    /* Holds World -> ChunkCache */
    private static HashMap<World, ChunkCache> holders = new HashMap<World, ChunkCache>();

    private static Chunkster getChunkster(World world, int chunkX, int chunkZ) {
        if (!holders.containsKey(world)) {
            holders.put(world, new ChunkCache(world));
        }
        return holders.get(world).getChunkster(chunkX, chunkZ);
    }

    private static int getStartChunkX(int wx) {
        return  wx < 0 ? (wx + 1) / 16 - 1 : wx / 16;
    }

    private static int getStartChunkZ(int wz) {
        return wz < 0 ? (wz + 1) / 16 - 1 : wz / 16;
    }

    /* Spawn entity in the world */
    public static Entity spawnEntity(World world, Class<? extends Entity> mob, BlockPos pos) {
        Entity entity = Mobs.construct(world, mob);
        entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, new Random(System.currentTimeMillis()).nextFloat() * 360.0F, 0.0F);
        world.spawnEntityInWorld(entity);
        entity.onUpdate();
        return entity;
    }

    /* Call generation manually at chunk position */
    public static void generate(World world, int chunkX, int chunkZ) {
        if (world.getChunkProvider() instanceof ChunkProviderServer) {
            ChunkProviderServer cps = (ChunkProviderServer) world.getChunkProvider();
            Chunk chunk = cps.provideChunk(chunkX, chunkZ);
             if(!chunk.isTerrainPopulated()) {
                chunk.checkLight();
                cps.chunkGenerator.populate(chunkX, chunkZ);
                GameRegistry.generateWorld(chunkX, chunkZ, world, cps.chunkGenerator, world.getChunkProvider());
                chunk.setChunkModified();
            }
        }
    }

    /* Remove tile entity in the world */
    public static void removeTileEntity(World world, BlockPos blockPos) {
        world.removeTileEntity(blockPos);
    }

    /* Get chunkster from the world */
    public static Chunkster getChunkster(World world, BlockPos blockPos) {
        return getChunkster(world, getStartChunkX(blockPos.getX()), getStartChunkZ(blockPos.getZ()));
    }

    /* Set block state in the world */
    public static void setBlockState(World world, BlockPos blockPos, IBlockState blockState) {
        getChunkster(world, blockPos).setBlockState(blockPos, blockState);
    }

    /* Get block state from the world */
    public static IBlockState getBlockState(World world, BlockPos blockPos) {
        return getChunkster(world, blockPos).getBlockState(blockPos);
    }

    /* Get tile entity from the world */
    public static TileEntity getTileEntity(World world, BlockPos blockPos) {
        return world.getTileEntity(blockPos);
    }

    /* Get soil height in the world */
    public static int getHeight(World world, BlockPos blockPos) {
        return getChunkster(world, blockPos).getHeight(blockPos);
    }

    /* Get bottom height in the world */
    public static int getBottomHeight(World world, BlockPos blockPos) {
        return getChunkster(world, blockPos).getBottomHeight(blockPos);
    }

    /* Unload obsoleted data */
    public static void unload() {
        for (HashMap.Entry<World, ChunkCache> entry : holders.entrySet()) {
            entry.getValue().unload();
        }
    }

    /* Updates queued data */
    public static void update() {
        for (HashMap.Entry<World, ChunkCache> entry : holders.entrySet()) {
            entry.getValue().update();
        }
        unload();
    }

    /* Get dimenstion name of the world */
    public static String getDimensionName(World world) {
        return world.provider.getDimensionType().getName();
    }

    /* Get dimension id from the world */
    public static int getDimensionID(World world) {
        return world.provider.getDimension();
    }

}
