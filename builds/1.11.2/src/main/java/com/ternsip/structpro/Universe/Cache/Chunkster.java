package com.ternsip.structpro.Universe.Cache;

import com.ternsip.structpro.Logic.Configurator;
import com.ternsip.structpro.Utils.Timer;
import com.ternsip.structpro.Universe.Blocks.Blocks;
import com.ternsip.structpro.Universe.Blocks.Classifier;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.BitSet;
import java.util.HashMap;

import static com.ternsip.structpro.Universe.Blocks.Classifier.*;

/* Chunk control class */
class Chunkster {

    /* Classical chunk sizes */
    private static final int CHUNK_SIZE_X = 16;
    private static final int CHUNK_SIZE_Y = 256;
    private static final int CHUNK_SIZE_Z = 16;
    private static final int CHUNK_PART_Y = 16;
    private static final int PLAYER_NOTIFY_RADIUS = 32;

    private final World world;
    private final Chunk chunk;

    /* Chunk world coordinates */
    private final int chunkX, chunkZ;

    /* Chunk world start coordinates */
    private final int chunkStartX, chunkStartZ;

    /* Timer since last chunk action */
    private final Timer timer = new Timer(32 * 1000);

    /* Signals that chunk was modified */
    private boolean modified = false;

    /* Height-maps of chunk for each type of classifier */
    private HashMap<Classifier, HashMap<Integer, Integer>> heights = new HashMap<Classifier, HashMap<Integer, Integer>>();

    /* Block state changes */
    private final BitSet changes = new BitSet(CHUNK_SIZE_X * CHUNK_PART_Y * CHUNK_SIZE_Z);

    /* Get internal chunk index */
    private int getIndex(int x, int y, int z) {
        return x + y * CHUNK_SIZE_X * CHUNK_SIZE_Z + z * CHUNK_SIZE_X;
    }

    /* Construct new chunk cache in certain position in the world */
    Chunkster(World world, int chunkX, int chunkZ) {
        this.world = world;
        this.chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        chunkStartX = chunkX * CHUNK_SIZE_X;
        chunkStartZ = chunkZ * CHUNK_SIZE_Z;
    }

    /* Get block storage array for y-coordinate */
    private ExtendedBlockStorage getStorage(int y) {
        ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
        if (y < 0 || y >= CHUNK_SIZE_Y) {
            return null;
        }
        int i = y / CHUNK_PART_Y;
        if (storage[i] == Chunk.NULL_BLOCK_STORAGE) {
            storage[i] = new ExtendedBlockStorage(i << 4, this.world.provider.hasSkyLight());
        }
        return storage[i];
    }

    /* Set block state internal */
    private void setBlockState(int x, int y, int z, IBlockState blockState) {
        ExtendedBlockStorage storage = getStorage(y);
        if (storage != null) {
            Universe.removeTileEntity(world, new BlockPos(x + chunkStartX, y, z + chunkStartZ));
            storage.set(x, y % CHUNK_PART_Y, z, blockState);
            changes.set(getIndex(x, y, z), true);
            modified = true;
        }
    }

    /* Get height by classifier internal */
    private int getHeight(Classifier classifier, int x, int z) {
        if (!heights.containsKey(classifier)) {
            heights.put(classifier, new HashMap<Integer, Integer>());
        }
        HashMap<Integer, Integer> map = heights.get(classifier);
        int index = x * 16 + z;
        if (!map.containsKey(index)) {
            int y = CHUNK_SIZE_Y - 1;
            String dimName = Universe.getDimensionName(world);
            y = dimName.equalsIgnoreCase("Nether") ? 63 : y;
            y = dimName.equalsIgnoreCase("End") ? 127 : y;
            while (y >= 0 && Classifier.isBlock(classifier, getBlockState(x, y, z))) {
                --y;
            }
            map.put(index, y + 1);
        }
        return map.get(index);
    }

    /* Get block state internal */
    private IBlockState getBlockState(int x, int y, int z) {
        ExtendedBlockStorage storage = getStorage(y);
        return storage != null ? storage.get(x, y % CHUNK_PART_Y, z) : Blocks.state(Blocks.AIR);
    }

    /* Set new block state in specific world position */
    void setBlockState(BlockPos pos, IBlockState blockState) {
        setBlockState(pos.getX() - chunkStartX, pos.getY(), pos.getZ() - chunkStartZ, blockState);
    }

    /* Get block state from specific world position */
    IBlockState getBlockState(BlockPos pos) {
        return getBlockState(pos.getX() - chunkStartX, pos.getY(), pos.getZ() - chunkStartZ);
    }

    /* Get height by classifier for world block position */
    int getHeight(Classifier classifier, BlockPos blockPos) {
        return getHeight(classifier, blockPos.getX() - chunkStartX, blockPos.getZ() - chunkStartZ);
    }

    /* Do blocks cosmetics */
    private void cosmetics() {
        for (int x = 0, wx = chunkStartX; x < CHUNK_SIZE_X; ++x, ++wx) {
            for (int z = 0, wz = chunkStartZ; z < CHUNK_SIZE_Z; ++z, ++wz) {
                boolean grassed = false;
                for (int y = CHUNK_SIZE_Y - 1; y >= 0; --y) {
                    BlockPos pos = new BlockPos(wx, y, wz);
                    Block prev = Blocks.getBlock(getBlockState(x, y + 1, z));
                    Block block = Blocks.getBlock(getBlockState(x, y, z));
                    if (block == Blocks.GRASS && Classifier.isBlock(SOIL, prev)) {
                        setBlockState(x, y, z, Blocks.state(Blocks.DIRT));
                    }
                    if (!grassed && block == Blocks.DIRT && Classifier.isBlock(OVERLOOK, prev)) {
                        setBlockState(x, y, z, Blocks.state(Blocks.GRASS));
                        grassed = true;
                    }
                    int index = getIndex(x, y, z);
                    if (changes.get(index)) {
                        changes.set(index, false);
                        if (!Configurator.IGNORE_LIGHT && Classifier.isBlock(LIGHT, block)) {
                            world.checkLight(pos);
                        }
                        try {
                            world.notifyNeighborsOfStateChange(pos, block, true);
                        } catch (Throwable ignored) {}
                    }
                }
            }
        }
    }

    /* Notify all nearby players about chunk changes */
    private void notifyPlayers() {
        for (EntityPlayer player : world.playerEntities) {
            if (player instanceof EntityPlayerMP) {
                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                if (    Math.abs(playerMP.chunkCoordX - chunkX) <= PLAYER_NOTIFY_RADIUS &&
                        Math.abs(playerMP.chunkCoordZ - chunkZ) <= PLAYER_NOTIFY_RADIUS) {
                    Packet<?> packet = new SPacketChunkData(chunk, 65535);
                    playerMP.connection.sendPacket(packet);
                }
            }
        }
    }

    /* Apply chunk changes */
    boolean update() {
        if (!modified) {
            return false;
        }
        modified = false;
        if (!Configurator.IGNORE_LIGHT) {
            chunk.generateSkylightMap();
        }
        cosmetics();
        notifyPlayers();
        return true;
    }

    Timer getTimer() {
        return timer;
    }

}
