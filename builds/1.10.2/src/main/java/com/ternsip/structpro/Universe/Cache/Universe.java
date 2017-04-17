package com.ternsip.structpro.Universe.Cache;

import com.ternsip.structpro.Structure.Volume;
import com.ternsip.structpro.Universe.Blocks.Classifier;
import com.ternsip.structpro.Universe.Entities.Mobs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;
import java.util.Random;

/* Memoize world block communication */
@SuppressWarnings({"WeakerAccess", "unused", "deprecation"})
public class Universe {

    /* Holds World -> Worldster */
    private static HashMap<World, Worldster> holders = new HashMap<World, Worldster>();

    private static Chunkster getChunkster(World world, int chunkX, int chunkZ) {
        if (!holders.containsKey(world)) {
            holders.put(world, new Worldster(world));
        }
        return holders.get(world).getChunkster(chunkX, chunkZ);
    }

    public static int getStartChunkX(int wx) {
        return  wx < 0 ? (wx + 1) / 16 - 1 : wx / 16;
    }

    public static int getStartChunkZ(int wz) {
        return wz < 0 ? (wz + 1) / 16 - 1 : wz / 16;
    }

    /* Get block biome in the world */
    public static Biome getBiome(World world, BlockPos blockPos) {
        return world.getBiome(blockPos);
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

    /* Set tile entity from the world */
    public static void setTileEntity(World world, BlockPos blockPos, TileEntity tileEntity) {
        world.setTileEntity(blockPos, tileEntity);
    }

    /* Get height in the world */
    public static int getHeight(World world, BlockPos blockPos, Classifier classifier) {
        return getChunkster(world, blockPos).getHeight(classifier, blockPos);
    }

    /* Unload obsoleted data */
    public static void unload() {
        for (HashMap.Entry<World, Worldster> entry : holders.entrySet()) {
            entry.getValue().unload();
        }
    }

    /* Updates queued data */
    public static void update() {
        for (HashMap.Entry<World, Worldster> entry : holders.entrySet()) {
            entry.getValue().update();
        }
        unload();
    }

    public static void sound(World world, BlockPos pos, SoundEvent event, SoundCategory category, float volume) {
        Random random = new Random(System.currentTimeMillis());
        world.playSound(null, pos, event, category, 1.0F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
    }

    /* Get dimension name of the world */
    public static String getDimensionName(World world) {
        return world.provider.getDimensionType().getName();
    }

    /* Get dimension id from the world */
    public static int getDimensionID(World world) {
        return world.provider.getDimension();
    }

    /* Recheck world light in region */
    public static void checkLight(World world, int wx, int wy, int wz, int width, int height, int length) {
        int LIGHT_MAX_LENGTH = 16;
        wx -= LIGHT_MAX_LENGTH;
        wy -= LIGHT_MAX_LENGTH;
        wz -= LIGHT_MAX_LENGTH;
        width += 2 * LIGHT_MAX_LENGTH;
        height += 2 * LIGHT_MAX_LENGTH;
        length += 2 * LIGHT_MAX_LENGTH;
        Volume volume = new Volume(width, height, length);
        int[] light = new int[width * height * length];
        int[] opacity = new int[width * height * length];
        Queue<Integer> queue = new ArrayDeque<Integer>();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int index = volume.getIndex(x, y, z);
                    IBlockState state = getBlockState(world, new BlockPos(wx + x, wy + y, wz + z));
                    light[index] = state.getLightValue();
                    opacity[index] = state.getLightOpacity();
                    if (light[index] > 0) {
                        queue.add(volume.getIndex(x, y, z));
                    }
                }
            }
        }
        while (!queue.isEmpty()) {
            int top = queue.poll();
            BlockPos pos = new BlockPos(volume.getX(top), volume.getY(top), volume.getZ(top));
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos nPos = pos.offset(facing);
                if (volume.isInside(nPos.getX(), nPos.getY(), nPos.getZ())) {
                    int next = volume.getIndex(nPos.getX(), nPos.getY(), nPos.getZ());
                    int level = Math.max(0, light[top] - opacity[next] - 1);
                    if (level > light[next]) {
                        light[next] = level;
                        queue.add(next);
                    }
                }
            }
        }
        for (int x = 0; x < width; ++x) {
            for (int z = 0; z < length; ++z) {
                for (int y = 0; y < height; ++y) {
                    BlockPos pos = new BlockPos(wx + x, wy + y, wz + z);
                    world.setLightFor(EnumSkyBlock.BLOCK, pos, light[volume.getIndex(x, y, z)]);
                }
            }
        }
    }

}
