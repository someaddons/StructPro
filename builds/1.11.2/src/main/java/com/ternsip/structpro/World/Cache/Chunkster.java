package com.ternsip.structpro.World.Cache;

import com.ternsip.structpro.Logic.Configurator;
import com.ternsip.structpro.World.Blocks.Blocks;
import com.ternsip.structpro.World.Blocks.Classifier;
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

import static com.ternsip.structpro.World.Blocks.Classifier.*;

/* Chunk control class */
class Chunkster {

    /* Classical chunk size */
    private static final int CHUNK_SIZE_X = 16;
    private static final int CHUNK_SIZE_Y = 256;
    private static final int CHUNK_SIZE_Z = 16;
    private static final int CHUNK_PART_Y = 16;
    private static final int CHUNK_PARTS = 16;
    private static final int PLAYER_NOTIFY_RADIUS = 32;

    private ExtendedBlockStorage[] storage;
    private int[][] heightMapOverlook = new int[CHUNK_SIZE_X][CHUNK_SIZE_Z];
    private int[][] heightMapBottom = new int[CHUNK_SIZE_X][CHUNK_SIZE_Z];
    private World world;
    private Chunk chunk;
    private int chunkX;
    private int chunkZ;
    private int chunkStartX;
    private int chunkStartZ;
    private boolean modified = false;
    private boolean[] modifiedParts = new boolean[CHUNK_PARTS];
    private Timer timer;

    Chunkster(World world, int chunkX, int chunkZ) {
        this.world = world;
        this.chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        chunkStartX = chunkX * CHUNK_SIZE_X;
        chunkStartZ = chunkZ * CHUNK_SIZE_Z;
        timer = new Timer();
        storage = chunk.getBlockStorageArray();
        for (int i = 0 ; i < CHUNK_PARTS; ++i) {
            if (storage[i] == Chunk.NULL_BLOCK_STORAGE) {
                storage[i] = new ExtendedBlockStorage(i << 4, this.world.provider.hasSkyLight());
            }
        }
        trackHeightMaps();
    }

    /* Calculate height maps */
    private void trackHeightMaps() {
        String dimName = WorldCache.getDimensionName(world);
        int startHeight = CHUNK_SIZE_Y - 1;
        startHeight = dimName.equalsIgnoreCase("Nether") ? 63 : startHeight;
        startHeight = dimName.equalsIgnoreCase("End") ? 127 : startHeight;
        for (int cx = 0; cx < CHUNK_SIZE_X; ++cx) {
            for (int cz = 0; cz < CHUNK_SIZE_Z; ++cz) {
                int hg = startHeight;
                while (hg >= 0 && Classifier.isBlock(OVERLOOK, getBlockState(cx, hg, cz))) {
                    --hg;
                }
                heightMapOverlook[cx][cz] = hg + 1;
                while (hg >= 0 && (Classifier.isBlock(OVERLOOK, getBlockState(cx, hg, cz)) || Classifier.isBlock(LIQUID, getBlockState(cx, hg, cz)))) {
                    --hg;
                }
                heightMapBottom[cx][cz] = hg + 1;
            }
        }
    }

    /* Apply cosmetic to blocks */
    private void cosmetic() {
        for (int cx = 0; cx < CHUNK_SIZE_X; ++cx) {
            for (int cz = 0; cz < CHUNK_SIZE_Z; ++cz) {
                Block prevBlock = Blocks.AIR;
                boolean grassed = false;
                for (int y = Math.min(heightMapOverlook[cx][cz], CHUNK_SIZE_Y - 1); y >= 0; --y) {
                    Block block = Blocks.getBlock(getBlockState(cx, y, cz));
                    if (block == Blocks.GRASS && Classifier.isBlock(SOIL, prevBlock)) {
                        setBlockState(cx, y, cz, Blocks.state(Blocks.DIRT));
                    }
                    if (!grassed && block == Blocks.DIRT && Classifier.isBlock(OVERLOOK, prevBlock)) {
                        setBlockState(cx, y, cz, Blocks.state(Blocks.GRASS));
                        grassed = true;
                    }
                    prevBlock = block;
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

    /* Process block lights and state changes */
    private void reprocess(int part) {
        int ys = part * CHUNK_PART_Y, ye = (part + 1) * CHUNK_PART_Y;
        for (int cx = 0, wx = chunkStartX; cx < CHUNK_SIZE_X; ++cx, ++wx) {
            for (int cz = 0, wz = chunkStartZ; cz < CHUNK_SIZE_Z; ++cz, ++wz) {
                for (int cy = ys, wy = ys; cy < ye; ++cy, ++wy) {
                    BlockPos pos = new BlockPos(wx, wy, wz);
                    Block block = Blocks.getBlock(getBlockState(cx, cy, cz));
                    if (!Configurator.ignoreLight && Classifier.isBlock(LIGHT, block)) {
                        world.checkLight(pos);
                    }
                    try {
                        world.notifyNeighborsOfStateChange(pos, block, true);
                    } catch (Throwable ignored) {}
                }
            }
        }
    }

    /* Set block state internal */
    private void setBlockState(int x, int y, int z, IBlockState blockState) {
        if (isInsideY(y)) {
            WorldCache.removeTileEntity(world, new BlockPos(x + chunkStartX, y, z + chunkStartZ));
            storage[y / CHUNK_PART_Y].set(x, y % CHUNK_PART_Y, z, blockState);
            modifiedParts[y / CHUNK_PART_Y] = true;
            modified = true;
        }
    }

    /* Get block state internal */
    private IBlockState getBlockState(int x, int y, int z) {
        return isInsideY(y) ? storage[y / CHUNK_PART_Y].get(x, y % CHUNK_PART_Y, z) : Blocks.state(Blocks.AIR);
    }

    /* Get overlook height by block position */
    int getHeightOverlook(BlockPos pos) {
        return heightMapOverlook[pos.getX() - chunkStartX][pos.getZ() - chunkStartZ];
    }

    /* Get bottom height by block position */
    int getHeightBottom(BlockPos pos) {
        return heightMapBottom[pos.getX() - chunkStartX][pos.getZ() - chunkStartZ];
    }

    /* Set new block state in specific world position */
    void setBlockState(BlockPos pos, IBlockState blockState) {
        setBlockState(pos.getX() - chunkStartX, pos.getY(), pos.getZ() - chunkStartZ, blockState);
    }

    /* Get block state from specific world position */
    IBlockState getBlockState(BlockPos pos) {
        return getBlockState(pos.getX() - chunkStartX, pos.getY(), pos.getZ() - chunkStartZ);
    }

    /* Check if y-coordinate inside chunk */
    private boolean isInsideY(int wy) {
        return wy >= 0 && wy < CHUNK_SIZE_Y && storage[wy / CHUNK_PART_Y] != Chunk.NULL_BLOCK_STORAGE;
    }

    /* Apply chunk changes */
    void update() {
        if (!modified) {
            return;
        }
        modified = false;
        trackHeightMaps();
        cosmetic();
        if (!Configurator.ignoreLight) {
            chunk.generateSkylightMap();
        }
        for (int part = 0; part < CHUNK_PARTS; ++part) {
            if (modifiedParts[part]) {
                modifiedParts[part] = false;
                reprocess(part);
            }
        }
        notifyPlayers();
    }

    Timer getTimer() {
        return timer;
    }

}
