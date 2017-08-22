package com.ternsip.structpro.universe.world;

import com.ternsip.structpro.logic.Configurator;
import com.ternsip.structpro.structure.Posture;
import com.ternsip.structpro.structure.Volume;
import com.ternsip.structpro.universe.biomes.UBiome;
import com.ternsip.structpro.universe.blocks.*;
import com.ternsip.structpro.universe.entities.Tiles;
import com.ternsip.structpro.universe.entities.UEntityClass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

import static com.ternsip.structpro.universe.blocks.Classifier.GAS;
import static com.ternsip.structpro.universe.blocks.Classifier.SOIL;

/**
 * World wrapper
 * Fast universal accessor for world-based interaction
 * @author Ternsip
 */
@SuppressWarnings({"WeakerAccess"})
public class UWorld {

    private World world;

    public UWorld(World world) {
        this.world = world;
    }

    /**
     * Call decoration manually at chunk position
     * @param chunkX Chunk X position
     * @param chunkZ Chunk Z position
     */
    public void decorate(int chunkX, int chunkZ) {
        if (world.getChunkProvider() instanceof ChunkProviderServer) {
            ChunkProviderServer cps = (ChunkProviderServer) world.getChunkProvider();
            world.getChunkFromChunkCoords(chunkX + 1, chunkZ);
            world.getChunkFromChunkCoords(chunkX + 1, chunkZ + 1);
            world.getChunkFromChunkCoords(chunkX, chunkZ + 1);
            world.getChunkFromChunkCoords(chunkX, chunkZ).populateChunk(cps, cps.currentChunkProvider, chunkX, chunkZ);
        }
    }

    /**
     * Check if the chunk was already decorated
     * @param chunkX Chunk X position
     * @param chunkZ Chunk Z position
     * @return Is chunk decorated
     */
    public boolean isDecorated(int chunkX, int chunkZ) {
        return world.getChunkFromChunkCoords(chunkX, chunkZ).isTerrainPopulated;
    }

    /**
     * Get block biome in the world
     * @param pos Block position
     * @return Minecraft native biome
     */
    public UBiome getBiome(UBlockPos pos) {
        return new UBiome(world.getBiomeGenForCoords(pos.getX(), pos.getZ()));
    }

    /**
     * Spawn entity in the world
     * @param mob Mob class to spawn
     * @param pos Block position where entity going to spawn
     * @return Spawned entity
     */
    public Entity spawnEntity(UEntityClass mob, UBlockPos pos) {
        Entity entity = mob.construct(this);
        entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, new Random(System.currentTimeMillis()).nextFloat() * 360.0F, 0.0F);
        world.spawnEntityInWorld(entity);
        return entity;
    }

    /**
     * Remove tile entity in the world
     * @param pos Block position over tile entity
     */
    public void removeTileEntity(UBlockPos pos) {
        world.removeTileEntity(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Get tile entity from the world
     * @param pos Block position over tile entity
     * @return Tile entity on the block
     */
    public TileEntity getTileEntity(UBlockPos pos) {
        return world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Set tile entity from the world
     * @param pos Block position over tile entity
     * @param tile Tile entity to set
     */
    public void setTileEntity(UBlockPos pos, TileEntity tile) {
        world.setTileEntity(pos.getX(), pos.getY(), pos.getZ(), tile);
    }

    /**
     * Set tile entity as NBT tag
     * @param pos Block position over tile entity
     * @param tile Tile entity NBT tag
     */
    @SuppressWarnings({"deprecation"})
    public void setTileTag(UBlockPos pos, NBTTagCompound tile) {
        if (tile == null) {
            return;
        }
        removeTileEntity(pos);
        NBTTagCompound tag = (NBTTagCompound) tile.copy();
        tag.setInteger("x", pos.getX());
        tag.setInteger("y", pos.getY());
        tag.setInteger("z", pos.getZ());
        try {
            TileEntity entity = TileEntity.createAndLoadEntity(tag);
            setTileEntity(pos, entity);
        } catch (Throwable throwable) {
            Tiles.load(getTileEntity(pos), tile, 0);
        }
    }

    /**
     * Get tile entity as NBT tag
     * @param pos Block position over tile entity
     * @return Serialized to NBT tag tile entity
     */
    public NBTTagCompound getTileTag(UBlockPos pos) {
        TileEntity tile = getTileEntity(pos);
        if (tile == null) {
            return null;
        }
        NBTTagCompound tag = new NBTTagCompound();
        tile.writeToNBT(tag);
        return tag;
    }

    /**
     * Set block state in the world
     * @param pos Block position
     * @param state Block state to set
     */
    public void setBlockState(UBlockPos pos, UBlockState state) {
        ExtendedBlockStorage storage = getStorage(world.getChunkFromBlockCoords(pos.getX(), pos.getZ()), pos.getY());
        if (storage != null) {
            removeTileEntity(pos);
            storage.func_150818_a(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state.getBlock().getBlock());
            storage.setExtBlockMetadata(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state.getMeta());
        }
    }

    /**
     * Get block state from the world
     * @param pos Block position
     * @return Block state
     */
    public UBlockState getBlockState(UBlockPos pos) {
        ExtendedBlockStorage storage = getStorage(world.getChunkFromBlockCoords(pos.getX(), pos.getZ()), pos.getY());
        if (storage != null) {
            UBlock uBlock = new UBlock(storage.getBlockByExtId(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15));
            int meta = storage.getExtBlockMetadata(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
            return new UBlockState(uBlock, meta);
        }
        return UBlocks.AIR.getState();
    }

    /**
     * Set light for block
     * @param pos Block position
     * @param light Block light value
     * @param sky Set for sky
     */
    @SuppressWarnings({"ConstantConditions"})
    public void setLight(UBlockPos pos, int light, boolean sky) {
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
     * @param classifier Skip block of this class
     * @param x X-block position
     * @param z Z-block position
     * @return Height level
     */
    public int getHeight(Classifier classifier, int x, int z) {
        int y = 255;
        String dimName = getDimensionName();
        y = dimName.equalsIgnoreCase("Nether") ? 63 : y;
        y = dimName.equalsIgnoreCase("End") ? 127 : y;
        while (y >= 0 && Classifier.isBlock(classifier, getBlockState(new UBlockPos(x, y, z)))) {
            --y;
        }
        return y + 1;
    }

    /**
     * Play sound at the world
     * @param pos Block position to play
     * @param sound Sound type
     * @param volume volume level
     */
    public void sound(UBlockPos pos, String sound, float volume) {
        Random random = new Random(System.currentTimeMillis());
        world.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), sound, volume, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
    }

    /**
     * Updates queued data
     * @param pos Block position to update
     */
    public void updateBlock(UBlockPos pos) {
        ExtendedBlockStorage storage = getStorage(world.getChunkFromBlockCoords(pos.getX(), pos.getZ()), pos.getY());
        if (storage != null) {
            UBlockState state = getBlockState(pos);
            UBlock block = state.getBlock();
            try {
                world.notifyBlocksOfNeighborChange(pos.getX(), pos.getY(), pos.getZ(), block.getBlock());
                block.getBlock().updateTick(world, pos.getX(), pos.getY(), pos.getZ(), new Random());
            } catch (Throwable ignored) {}
        }
    }

    /**
     * Notify all nearby players about chunk changes
     * @param chunk Chunk to notify
     */
    @SuppressWarnings({"unchecked"})
    public void notifyChunk(Chunk chunk) {
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
    public void generateHeightMap(Chunk chunk) {
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
     * Updates queued data. Includes light updates
     * @param posture Posture area going to update
     */
    public void notifyPosture(Posture posture) {
        int sx = posture.getPosX() >> 4, ex = posture.getEndX() >> 4;
        int sz = posture.getPosZ() >> 4, ez = posture.getEndZ() >> 4;
        for (int x = sx; x <= ex; ++x) {
            for (int z = sz; z <= ez; ++z) {
                generateHeightMap(world.getChunkFromChunkCoords(x, z));
            }
        }
        updateLight(posture);
        for (int x = posture.getPosX(); x <= posture.getEndX(); ++x) {
            for (int y = posture.getPosY(); y <= posture.getEndY(); ++y) {
                for (int z = posture.getPosZ(); z <= posture.getEndZ(); ++z) {
                    updateBlock(new UBlockPos(x, y, z));
                }
            }
        }
        for (int x = sx; x <= ex; ++x) {
            for (int z = sz; z <= ez; ++z) {
                notifyChunk(world.getChunkFromChunkCoords(x, z));
            }
        }
    }

    /**
     * Save all data for world
     */
    public void saveWorlds() {
       world.getChunkProvider().unloadQueuedChunks();
    }

    /**
     * Get dimension name of the world
     * @return Dimension name
     */
    public String getDimensionName() {
        return world.provider.getDimensionName();
    }

    /**
     * Get dimension id from the world
     * @return Dimension index
     */
    public int getDimensionID() {
        return world.provider.dimensionId;
    }

    /**
     * Fix grass and dirt
     * @param posture Posture area going to fix
     */
    public void grassFix(Posture posture) {
        for (int x = posture.getPosX(); x <= posture.getEndX(); ++x) {
            for (int z = posture.getPosZ(); z <= posture.getEndZ(); ++z) {
                UBlock block = UBlocks.AIR;
                boolean grassed = false;
                for (int y = 255; y >= 0; --y) {
                    UBlock curBlock = getBlockState(new UBlockPos(x, y, z)).getBlock();
                    if (curBlock.getID() == UBlocks.GRASS.getID() && Classifier.isBlock(SOIL, block)) {
                        setBlockState(new UBlockPos(x, y, z), UBlocks.DIRT.getState());
                        block = UBlocks.DIRT;
                    } else if (!grassed && curBlock.getID() == UBlocks.DIRT.getID() && !Classifier.isBlock(SOIL, block)) {
                        grassed = true;
                        setBlockState(new UBlockPos(x, y, z), UBlocks.GRASS.getState());
                        block = UBlocks.GRASS;
                    } else {
                        block = curBlock;
                    }
                }
            }
        }
    }

    /**
     * Recheck world light in region
     * @param posture Posture area going to update light
     */
    public void updateLight(Posture posture) {
        int wx = posture.getPosX(), wy = posture.getPosY(), wz = posture.getPosZ();
        int width = posture.getSizeX(), height = posture.getSizeY(), length = posture.getSizeZ();
        Volume volume = new Volume(width, height, length);
        int[] light = new int[volume.getSize()];
        int[] opacity = new int[volume.getSize()];
        int[][] heights = new int[width][length];
        for (int x = 0; x < heights.length; ++x) {
            for (int z = 0; z < heights[x].length; ++z) {
                heights[x][z] = getHeight(GAS, wx + x, wz + z);
            }
        }
        /* Iterate 2 times first - for block light, second - for sky light */
        for (int k = 0; k < 2; ++k) {
            Queue<Integer> queue = new ArrayDeque<>();
            for (int x = 0; x < width; ++x) {
                for (int z = 0; z < length; ++z) {
                    for (int y = 0; y < height; ++y) {
                        int index = volume.getIndex(x, y, z);
                        if (k == 0) {
                            UBlockState state = getBlockState(new UBlockPos(wx + x, wy + y, wz + z));
                            opacity[index] = Math.max(state.getOpacity(), 1);
                            light[index] = state.getLight();
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
                UBlockPos pos = new UBlockPos(volume.getX(top), volume.getY(top), volume.getZ(top));
                for (EnumFacing facing : EnumFacing.values()) {
                    UBlockPos nPos = pos.offset(facing);
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
                        setLight(new UBlockPos(wx + x, wy + y, wz + z), light[volume.getIndex(x, y, z)], k == 1);
                    }
                }
            }
        }
    }

    /**
     * Get world instance by dimension name or id, case insensitive
     * @param dimension Dimension name or id
     * @return World instance
     */
    public static UWorld getWorld(String dimension) {
        for (int dim : DimensionManager.getStaticDimensionIDs()) {
            if (DimensionManager.getWorld(dim) == null) {
                DimensionManager.initDimension(dim);
            }
        }
        for (World world : DimensionManager.getWorlds()) {
            UWorld uWorld = new UWorld(world);
            if (uWorld.getDimensionName().equalsIgnoreCase(dimension) || String.valueOf(uWorld.getDimensionID()).equalsIgnoreCase(dimension)) {
                return uWorld;
            }
        }
        return null;
    }

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
     * Get world seed
     * @return Actual seed
     */
    public long getSeed() {
        return world.getSeed();
    }

    /**
     * Get world name
     * @return Actual name
     */
    public String getWorldName() {
        return world.getWorldInfo().getWorldName();
    }

    /**
     * Check if block in given position is air
     * @return Is block air
     */
    public boolean isAirBlock(UBlockPos pos) {
        return world.isAirBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    /** Get minecraft native world */
    public World getWorld() {
        return world;
    }

    /**
     * Get world data
     * @return World saved data
     */
    public WorldData getWorldData() {
        MapStorage storage = world.perWorldStorage;
        WorldData result = (WorldData) storage.loadData(WorldData.class, WorldData.DATA_NAME);
        if (result == null) {
            result = new WorldData(WorldData.DATA_NAME);
            storage.setData(WorldData.DATA_NAME, result);
        }
        return result;
    }

}
