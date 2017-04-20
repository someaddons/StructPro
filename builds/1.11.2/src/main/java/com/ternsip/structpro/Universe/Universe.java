package com.ternsip.structpro.Universe;

import com.ternsip.structpro.Logic.Configurator;
import com.ternsip.structpro.Structure.Posture;
import com.ternsip.structpro.Structure.Volume;
import com.ternsip.structpro.Universe.Blocks.Blocks;
import com.ternsip.structpro.Universe.Blocks.Classifier;
import com.ternsip.structpro.Universe.Entities.Mobs;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

import static com.ternsip.structpro.Universe.Blocks.Classifier.GAS;
import static com.ternsip.structpro.Universe.Blocks.Classifier.SOIL;

/* Memoize world block communication */
@SuppressWarnings({"WeakerAccess"})
public class Universe {

    /* Get block storage array for y-coordinate */
    private static ExtendedBlockStorage getStorage(Chunk chunk, int y) {
        ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
        if (y < 0 || y >= 256) {
            return null;
        }
        int i = y >> 4;
        if (storage[i] == Chunk.NULL_BLOCK_STORAGE) {
            storage[i] = new ExtendedBlockStorage(i << 4, chunk.getWorld().provider.hasSkyLight());
        }
        return storage[i];
    }

    /* Get block biome in the world */
    public static Biome getBiome(World world, BlockPos blockPos) {
        return world.getBiome(blockPos);
    }

    /* Spawn entity in the world */
    public static Entity spawnEntity(World world, Class<? extends Entity> mob, BlockPos pos) {
        Entity entity = Mobs.construct(world, mob);
        entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, new Random(System.currentTimeMillis()).nextFloat() * 360.0F, 0.0F);
        world.spawnEntity(entity);
        entity.onUpdate();
        return entity;
    }

    /* Remove tile entity in the world */
    public static void removeTileEntity(World world, BlockPos blockPos) {
        world.removeTileEntity(blockPos);
    }

    /* Get tile entity from the world */
    public static TileEntity getTileEntity(World world, BlockPos pos) {
        return world.getTileEntity(pos);
    }

    /* Set tile entity from the world */
    public static void setTileEntity(World world, BlockPos pos, TileEntity tileEntity) {
        world.setTileEntity(pos, tileEntity);
    }

    /* Set tile entity as NBT tag */
    public static void setTileTag(World world, BlockPos pos, NBTTagCompound tile) {
        if (tile == null) {
            return;
        }
        Universe.removeTileEntity(world, pos);
        NBTTagCompound tag = tile.copy();
        tag.setInteger("x", pos.getX());
        tag.setInteger("y", pos.getY());
        tag.setInteger("z", pos.getZ());
        TileEntity entity = TileEntity.create(world, tag);
        Universe.setTileEntity(world, pos, entity);
    }

    /* Get tile entity as NBT tag */
    public static NBTTagCompound getTileTag(World world, BlockPos pos) {
        TileEntity tile = getTileEntity(world, pos);
        return tile == null ? null : tile.serializeNBT();
    }

    /* Set block state in the world */
    public static void setBlockState(World world, BlockPos pos, IBlockState state) {
        ExtendedBlockStorage storage = getStorage(world.getChunkFromBlockCoords(pos), pos.getY());
        if (storage != null) {
            removeTileEntity(world, pos);
            storage.set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
        }
    }

    /* Get block state from the world */
    public static IBlockState getBlockState(World world, BlockPos pos) {
        ExtendedBlockStorage storage = getStorage(world.getChunkFromBlockCoords(pos), pos.getY());
        if (storage != null) {
            return storage.get(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
        }
        return Blocks.state(Blocks.AIR);
    }

    /* Set light for block */
    public static void setLight(World world, BlockPos pos, int light, boolean sky) {
        ExtendedBlockStorage storage = getStorage(world.getChunkFromBlockCoords(pos), pos.getY());
        if (storage != null) {
            if (sky) {
                if (storage.getSkylightArray() == null) {
                    return;
                }
                storage.setExtSkylightValue(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, light);
            } else {
                storage.setExtBlocklightValue(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, light);
            }
        }
    }

    /* Get height passed by classifier */
    public static int getHeight(World world, Classifier classifier, int x, int z) {
        int y = 255;
        String dimName = Universe.getDimensionName(world);
        y = dimName.equalsIgnoreCase("Nether") ? 63 : y;
        y = dimName.equalsIgnoreCase("End") ? 127 : y;
        while (y >= 0 && Classifier.isBlock(classifier, getBlockState(world, new BlockPos(x, y, z)))) {
            --y;
        }
        return y + 1;
    }

    /* Play sound at the world */
    public static void sound(World world, BlockPos pos, SoundEvent event, SoundCategory category, float volume) {
        Random random = new Random(System.currentTimeMillis());
        world.playSound(null, pos, event, category, volume, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
    }

    /* Updates queued data */
    public static void updateBlock(World world, BlockPos pos) {
        ExtendedBlockStorage storage = getStorage(world.getChunkFromBlockCoords(pos), pos.getY());
        if (storage != null) {
            IBlockState state = getBlockState(world, pos);
            Block block = Blocks.getBlock(state);
            try {
                world.notifyNeighborsOfStateChange(pos, block, true);
                world.immediateBlockTick(pos, state, new Random());
            } catch (Throwable ignored) {}
        }
    }

    /* Notify all nearby players about chunk changes */
    public static void notifyChunk(World world, Chunk chunk) {
        for (EntityPlayer player : world.playerEntities) {
            if (player instanceof EntityPlayerMP) {
                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                if (    Math.abs(playerMP.chunkCoordX - chunk.xPosition) <= 32 &&
                        Math.abs(playerMP.chunkCoordZ - chunk.zPosition) <= 32) {
                    playerMP.connection.sendPacket(new SPacketChunkData(chunk, 65535));
                }
            }
        }
    }

    /* Updates queued data */
    public static void notifyPosture(World world, Posture posture) {
        if (Configurator.TICKER) {
            world.tickUpdates(true);
        }
        for (int x = posture.getPosX(); x <= posture.getEndX(); ++x) {
            for (int y = posture.getPosY(); y <= posture.getEndY(); ++y) {
                for (int z = posture.getPosZ(); z <= posture.getEndZ(); ++z) {
                    updateBlock(world, new BlockPos(x, y, z));
                }
            }
        }
        if (Configurator.TICKER) {
            world.tickUpdates(true);
        }
        int sx = posture.getPosX() >> 4, ex = posture.getEndX() >> 4;
        int sz = posture.getPosZ() >> 4, ez = posture.getEndZ() >> 4;
        for (int x = sx; x <= ex; ++x) {
            for (int z = sz; z <= ez; ++z) {
                notifyChunk(world, world.getChunkFromChunkCoords(x, z));
            }
        }
        if (Configurator.TICKER) {
            world.tickUpdates(true);
        }
    }

    /* Get dimension name of the world */
    public static String getDimensionName(World world) {
        return world.provider.getDimensionType().getName();
    }

    /* Get dimension id from the world */
    public static int getDimensionID(World world) {
        return world.provider.getDimension();
    }

    /* Fix grass */
    public static void grassFix(World world, Posture posture) {
        for (int x = posture.getPosX(); x <= posture.getEndX(); ++x) {
            for (int z = posture.getPosZ(); z <= posture.getEndZ(); ++z) {
                Block block = Blocks.AIR;
                boolean grassed = false;
                for (int y = 255; y >= 0; --y) {
                    Block curBlock = Blocks.getBlock(getBlockState(world, new BlockPos(x, y, z)));
                    if (curBlock == Blocks.GRASS && Classifier.isBlock(SOIL, block)) {
                        setBlockState(world, new BlockPos(x, y, z), Blocks.state(Blocks.DIRT));
                        block = Blocks.DIRT;
                    } else if (!grassed && curBlock == Blocks.DIRT && !Classifier.isBlock(SOIL, block)) {
                        grassed = true;
                        setBlockState(world, new BlockPos(x, y, z), Blocks.state(Blocks.GRASS));
                        block = Blocks.GRASS;
                    } else {
                        block = curBlock;
                    }
                }
            }
        }
    }

    /* Recheck world light in region */
    public static void checkLight(World world, Posture posture) {
        int wx = posture.getPosX(), wy = posture.getPosY(), wz = posture.getPosZ();
        int width = posture.getSizeX(), height = posture.getSizeY(), length = posture.getSizeZ();
        Volume volume = new Volume(width, height, length);
        int[] light = new int[volume.getVolume()];
        int[] opacity = new int[volume.getVolume()];
        int[][] heights = new int[width][length];
        for (int x = 0; x < heights.length; ++x) {
            for (int z = 0; z < heights[x].length; ++z) {
                heights[x][z] = getHeight(world, GAS, wx + x, wz + z);
            }
        }
        Queue<Integer> queue = new ArrayDeque<Integer>();
        for (int x = 0; x < width; ++x) {
            for (int z = 0; z < length; ++z) {
                for (int y = 0; y < height; ++y) {
                    int index = volume.getIndex(x, y, z);
                    IBlockState state = getBlockState(world, new BlockPos(wx + x, wy + y, wz + z));
                    opacity[index] = Math.max(Blocks.getOpacity(state), 1);
                    light[index] = Blocks.getLight(state);
                    if (light[index] > 0) {
                        queue.add(index);
                    }
                }
            }
        }
        for (int k = 0; k < 2; ++k) {
            if (k == 1) {
                for (int x = 0; x < width; ++x) {
                    for (int z = 0; z < length; ++z) {
                        for (int y = 0; y < height; ++y) {
                            int index = volume.getIndex(x, y, z);
                            if (wy + y < heights[x][z]) {
                                light[index] = 0;
                            } else {
                                light[index] = 15;
                                queue.add(index);
                            }
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
                        int level = Math.max(0, light[top] - opacity[next]);
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
                        setLight(world, new BlockPos(wx + x, wy + y, wz + z), light[volume.getIndex(x, y, z)], k == 1);
                    }
                }
            }
        }
    }



}
