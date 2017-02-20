package com.ternsip.structpro.WorldCache;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Random;

/* Memoize world block communication */
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

    public static Entity spawnEntity(World world, Class<? extends Entity> mob, BlockPos pos) {
        Entity entity = Mobs.construct(world, mob);
        entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, new Random(System.currentTimeMillis()).nextFloat() * 360.0F, 0.0F);
        world.spawnEntityInWorld(entity);
        entity.onUpdate();
        return entity;
    }

    public static void setBlockState(World world, int x, int y, int z, IBlockState blockState) {
        getChunkster(world, getStartChunkX(x), getStartChunkZ(z)).setBlockState(x, y, z, blockState);
    }

    public static IBlockState getBlockState(World world, int x, int y, int z) {
        return getChunkster(world, getStartChunkX(x), getStartChunkZ(z)).getBlockState(x, y, z);
    }

    public static TileEntity getTileEntity(World world, int x, int y, int z) {
        return getChunkster(world, getStartChunkX(x), getStartChunkZ(z)).getTileEntity(x, y, z);
    }

    public static void setTileEntity(World world, int x, int y, int z, TileEntity tileEntity) {
        getChunkster(world, getStartChunkX(x), getStartChunkZ(z)).setTileEntity(x, y, z, tileEntity);
    }

    public static void setBlockState(World world, BlockPos blockPos, IBlockState blockState) {
        setBlockState(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockState);
    }

    public static IBlockState getBlockState(World world, BlockPos blockPos) {
        return getBlockState(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static void setTileEntity(World world, BlockPos blockPos, TileEntity tileEntity) {
        setTileEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), tileEntity);
    }

    public static TileEntity getTileEntity(World world, BlockPos blockPos) {
        return getTileEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static int getHeight(World world, int x, int z) {
        return getChunkster(world, getStartChunkX(x), getStartChunkZ(z)).getHeight(x, z);
    }

    public static int getBottomHeight(World world, int x, int z) {
        return getChunkster(world, getStartChunkX(x), getStartChunkZ(z)).getBottomHeight(x, z);
    }

    /* Unload obsoleted data */
    private static void unload() {
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

    public static String getDimensionName(World world) {
        return world.provider.getDimensionType().getName();
    }

    public static int getDimensionID(World world) {
        return world.provider.getDimension();
    }

    public static int blockID(Block block) {
        return Block.getIdFromBlock(block);
    }

    public static Item ItemByName(String name) {
        return Item.REGISTRY.getObject(new ResourceLocation(name));
    }

    public static int itemMaxMeta(Item item) {
        return item.getMaxDamage();
    }

    public static int itemMaxStack(Item item) {
        return item.getItemStackLimit();
    }

    public static IBlockState blockState(Block block, int meta) {
        IBlockState result = block.getDefaultState();
        try {
            result = block.getStateFromMeta(meta);
        } catch (Throwable ignored) {}
        return result;
    }

}
