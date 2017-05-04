package com.ternsip.structpro.Universe;

import com.ternsip.structpro.Logic.Configurator;
import com.ternsip.structpro.Structure.Posture;
import com.ternsip.structpro.Structure.Volume;
import com.ternsip.structpro.Universe.Blocks.Blocks;
import com.ternsip.structpro.Universe.Blocks.Classifier;
import com.ternsip.structpro.Universe.Entities.Mobs;
import com.ternsip.structpro.Utils.BlockPos;
import com.ternsip.structpro.Utils.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

import static com.ternsip.structpro.Universe.Blocks.Classifier.GAS;
import static com.ternsip.structpro.Universe.Blocks.Classifier.SOIL;

/**
 * Fast universal accessor for world-based interaction
 * @author Ternsip
 * @since JDK 1.6
 */
@SuppressWarnings({"WeakerAccess"})
public class Universe {

    /**
     * Get block storage array for y-coordinate
     * Returns null in case block is outside
     * @param chunk Target chunk
     * @param y Block height inside chunk
     * @return Block storage or null
     */
    private static ExtendedBlockStorage getStorage(Chunk chunk, int y) {
        ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
        if (y < 0 || y >= 256) {
            return null;
        }
        int i = y >> 4;
        if (storage[i] == null) {
            storage[i] = new ExtendedBlockStorage(i << 4, !chunk.worldObj.provider.hasNoSky);
            chunk.generateSkylightMap();
        }
        return storage[i];
    }

    /**
     * Call generation manually at chunk position
     * @param world Target world
     * @param chunkX Chunk X position
     * @param chunkZ Chunk Z position
     */
    public static void generate(World world, int chunkX, int chunkZ) {
        if (world.getChunkProvider() instanceof ChunkProviderServer) {
            ChunkProviderServer cps = (ChunkProviderServer) world.getChunkProvider();
            world.getChunkFromChunkCoords(chunkX + 1, chunkZ);
            world.getChunkFromChunkCoords(chunkX + 1, chunkZ + 1);
            world.getChunkFromChunkCoords(chunkX, chunkZ + 1);
            world.getChunkFromChunkCoords(chunkX, chunkZ).populateChunk(cps, cps.currentChunkProvider, chunkX, chunkZ);
        }
    }

    /**
     * Get block biome in the world
     * @param world Target world
     * @param pos Block position
     * @return Minecraft native biome
     */
    public static BiomeGenBase getBiome(World world, BlockPos pos) {
        return world.getBiomeGenForCoords(pos.getX(), pos.getZ());
    }

    /**
     * Spawn entity in the world
     * @param world Target world
     * @param mob Mob class to spawn
     * @param pos Block position where entity going to spawn
     * @return Spawned entity
     */
    public static Entity spawnEntity(World world, Class<? extends Entity> mob, BlockPos pos) {
        Entity entity = Mobs.construct(world, mob);
        entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, new Random(System.currentTimeMillis()).nextFloat() * 360.0F, 0.0F);
        world.spawnEntityInWorld(entity);
        entity.onUpdate();
        return entity;
    }

    /**
     * Remove tile entity in the world
     * @param world Target world
     * @param pos Block position over tile entity
     */
    public static void removeTileEntity(World world, BlockPos pos) {
        world.removeTileEntity(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Get tile entity from the world
     * @param world Target world
     * @param pos Block position over tile entity
     * @return Tile entity on the block
     */
    public static TileEntity getTileEntity(World world, BlockPos pos) {
        return world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Set tile entity from the world
     * @param world Target world
     * @param pos Block position over tile entity
     * @param tile Tile entity to set
     */
    public static void setTileEntity(World world, BlockPos pos, TileEntity tile) {
        world.setTileEntity(pos.getX(), pos.getY(), pos.getZ(), tile);
    }

    /**
     * Set tile entity as NBT tag
     * @param world Target world
     * @param pos Block position over tile entity
     * @param tile Tile entity NBT tag
     */
    public static void setTileTag(World world, BlockPos pos, NBTTagCompound tile) {
        if (tile == null) {
            return;
        }
        Universe.removeTileEntity(world, pos);
        NBTTagCompound tag = (NBTTagCompound) tile.copy();
        tag.setInteger("x", pos.getX());
        tag.setInteger("y", pos.getY());
        tag.setInteger("z", pos.getZ());
        TileEntity entity = TileEntity.createAndLoadEntity(tag);
        Universe.setTileEntity(world, pos, entity);
    }

    /**
     * Get tile entity as NBT tag
     * @param world Target world
     * @param pos Block position over tile entity
     * @return Serialized to NBT tag tile entity
     */
    public static NBTTagCompound getTileTag(World world, BlockPos pos) {
        TileEntity tile = getTileEntity(world, pos);
        if (tile == null) {
            return null;
        }
        NBTTagCompound tag = new NBTTagCompound();
        tile.writeToNBT(tag);
        return tag;
    }

    /**
     * Set block state in the world
     * @param world Target world
     * @param pos Block position
     * @param state Block state to set
     */
    public static void setBlockState(World world, BlockPos pos, IBlockState state) {
        ExtendedBlockStorage storage = getStorage(world.getChunkFromBlockCoords(pos.getX(), pos.getZ()), pos.getY());
        if (storage != null) {
            removeTileEntity(world, pos);
            storage.func_150818_a(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state.getBlock());
            storage.setExtBlockMetadata(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state.getMeta());
        }
    }

    /**
     * Get block state from the world
     * @param world Target world
     * @param pos Block position
     * @return Block state
     */
    public static IBlockState getBlockState(World world, BlockPos pos) {
        ExtendedBlockStorage storage = getStorage(world.getChunkFromBlockCoords(pos.getX(), pos.getZ()), pos.getY());
        if (storage != null) {
            Block block = storage.getBlockByExtId(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
            int meta = storage.getExtBlockMetadata(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
            return new IBlockState(block, meta);
        }
        return Blocks.getState(Blocks.air);
    }

    /**
     * Set light for block
     * @param world Target world
     * @param pos Block position
     * @param light Block light value
     * @param sky Set for sky
     */
    @SuppressWarnings({"ConstantConditions"})
    public static void setLight(World world, BlockPos pos, int light, boolean sky) {
        ExtendedBlockStorage storage = getStorage(world.getChunkFromBlockCoords(pos.getX(), pos.getZ()), pos.getY());
        if (storage != null) {
            if (sky) {
                if (storage.getSkylightArray() != null) {
                    storage.setExtSkylightValue(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, light);
                }
            } else {
                storage.setExtBlocklightValue(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, light);
            }
        }
    }

    /**
     * Get height passed by classifier
     * @param world Target world
     * @param classifier Skip block of this class
     * @param x X-block position
     * @param z Z-block position
     * @return Height level
     */
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

    /**
     * Play sound at the world
     * @param world Target world
     * @param pos Block position to play
     * @param sound Sound type
     * @param volume volume level
     */
    public static void sound(World world, BlockPos pos, String sound, float volume) {
        Random random = new Random(System.currentTimeMillis());
        world.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), sound, volume, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
    }

    /**
     * Updates queued data
     * @param world Target world
     * @param pos Block position to update
     */
    public static void updateBlock(World world, BlockPos pos) {
        ExtendedBlockStorage storage = getStorage(world.getChunkFromBlockCoords(pos.getX(), pos.getZ()), pos.getY());
        if (storage != null) {
            IBlockState state = getBlockState(world, pos);
            Block block = Blocks.getBlock(state);
            try {
                world.notifyBlocksOfNeighborChange(pos.getX(), pos.getY(), pos.getZ(), block);
                block.updateTick(world, pos.getX(), pos.getY(), pos.getZ(), new Random());
            } catch (Throwable ignored) {}
        }
    }

    /**
     * Notify all nearby players about chunk changes
     * @param world Target world
     * @param chunk Chunk to notify
     */
    @SuppressWarnings({"unchecked"})
    public static void notifyChunk(World world, Chunk chunk) {
        for (EntityPlayer player : (ArrayList<EntityPlayer>)world.playerEntities) {
            if (player instanceof EntityPlayerMP) {
                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                if (    Math.abs(playerMP.chunkCoordX - chunk.xPosition) <= 32 &&
                        Math.abs(playerMP.chunkCoordZ - chunk.zPosition) <= 32) {
                    playerMP.playerNetServerHandler.sendPacket(new S21PacketChunkData(chunk, true, 65535));
                }
            }
        }
    }

    /**
     * Recalculate height map and precipitations map for chunk
     * @param chunk Chunk to process
     */
    public static void generateHeightMap(Chunk chunk) {
        int i = chunk.getTopFilledSegment();
        chunk.heightMapMinimum = Integer.MAX_VALUE;
        for (int j = 0; j < 16; ++j) {
            int k = 0;
            while (k < 16) {
                int l = i + 16 - 1;
                while (true) {
                    if (l > 0) {
                        if (chunk.func_150808_b(j, l - 1, k) == 0) {
                            --l;
                            continue;
                        }
                        chunk.heightMap[k << 4 | j] = l;
                        if (l < chunk.heightMapMinimum) {
                            chunk.heightMapMinimum = l;
                        }
                    }
                    ++k;
                    break;
                }
            }
        }
    }

    /**
     * Updates queued data
     * @param world Target world
     * @param posture Posture area going to update
     */
    public static void notifyPosture(World world, Posture posture) {
        int sx = posture.getPosX() >> 4, ex = posture.getEndX() >> 4;
        int sz = posture.getPosZ() >> 4, ez = posture.getEndZ() >> 4;
        for (int x = sx; x <= ex; ++x) {
            for (int z = sz; z <= ez; ++z) {
                generateHeightMap(world.getChunkFromChunkCoords(x, z));
            }
        }
        updateLight(world, posture);
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
        for (int x = sx; x <= ex; ++x) {
            for (int z = sz; z <= ez; ++z) {
                notifyChunk(world, world.getChunkFromChunkCoords(x, z));
            }
        }
        if (Configurator.TICKER) {
            world.tickUpdates(true);
        }
    }

    /**
     * Save all data for world
     * @param world The world instance
     */
    public static void saveWorlds(World world) {
        world.getChunkProvider().unloadQueuedChunks();
    }

    /**
     * Get world instance by dimension name or id, case insensitive
     * @param dimension Dimension name or id
     * @return World instance
     */
    public static World getWorld(String dimension) {
        for (int dim : DimensionManager.getStaticDimensionIDs()) {
            if (DimensionManager.getWorld(dim) == null) {
                DimensionManager.initDimension(dim);
            }
        }
        for (World world : DimensionManager.getWorlds()) {
            if (getDimensionName(world).equalsIgnoreCase(dimension) || String.valueOf(getDimensionID(world)).equalsIgnoreCase(dimension)) {
                return world;
            }
        }
        return null;
    }

    /**
     * Get dimension name of the world
     * @param world Target world
     * @return Dimension name
     */
    public static String getDimensionName(World world) {
        return world.provider.getDimensionName();
    }

    /**
     * Get dimension id from the world
     * @param world Target world
     * @return Dimension index
     */
    public static int getDimensionID(World world) {
        return world.provider.dimensionId;
    }

    /**
     * Fix grass and dirt
     * @param world Target world
     * @param posture Posture area going to fix
     */
    public static void grassFix(World world, Posture posture) {
        for (int x = posture.getPosX(); x <= posture.getEndX(); ++x) {
            for (int z = posture.getPosZ(); z <= posture.getEndZ(); ++z) {
                Block block = Blocks.air;
                boolean grassed = false;
                for (int y = 255; y >= 0; --y) {
                    Block curBlock = Blocks.getBlock(getBlockState(world, new BlockPos(x, y, z)));
                    if (curBlock == Blocks.grass && Classifier.isBlock(SOIL, block)) {
                        setBlockState(world, new BlockPos(x, y, z), Blocks.getState(Blocks.dirt));
                        block = Blocks.dirt;
                    } else if (!grassed && curBlock == Blocks.dirt && !Classifier.isBlock(SOIL, block)) {
                        grassed = true;
                        setBlockState(world, new BlockPos(x, y, z), Blocks.getState(Blocks.grass));
                        block = Blocks.grass;
                    } else {
                        block = curBlock;
                    }
                }
            }
        }
    }

    /**
     * Recheck world light in region
     * @param world Target world
     * @param posture Posture area going to update light
     */
    public static void updateLight(World world, Posture posture) {
        int wx = posture.getPosX(), wy = posture.getPosY(), wz = posture.getPosZ();
        int width = posture.getSizeX(), height = posture.getSizeY(), length = posture.getSizeZ();
        Volume volume = new Volume(width, height, length);
        int[] light = new int[volume.getSize()];
        int[] opacity = new int[volume.getSize()];
        int[][] heights = new int[width][length];
        for (int x = 0; x < heights.length; ++x) {
            for (int z = 0; z < heights[x].length; ++z) {
                heights[x][z] = getHeight(world, GAS, wx + x, wz + z);
            }
        }
        /* Iterate 2 times first - for block light, second - for sky light */
        for (int k = 0; k < 2; ++k) {
            Queue<Integer> queue = new ArrayDeque<Integer>();
            for (int x = 0; x < width; ++x) {
                for (int z = 0; z < length; ++z) {
                    for (int y = 0; y < height; ++y) {
                        int index = volume.getIndex(x, y, z);
                        if (k == 0) {
                            IBlockState state = getBlockState(world, new BlockPos(wx + x, wy + y, wz + z));
                            opacity[index] = Math.max(Blocks.getOpacity(state), 1);
                            light[index] = Blocks.getLight(state);
                            if (light[index] > 0) {
                                queue.add(index);
                            }
                        } else {
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
