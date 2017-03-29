package com.ternsip.structpro.WorldCache;

import com.ternsip.structpro.Logic.Blocks;
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
    private int heights[][];
    private int bottomHeights[][];
    private World world;
    private Chunk chunk;
    private int chunkX;
    private int chunkZ;
    private int chunkStartX;
    private int chunkStartY;
    private int chunkStartZ;
    private boolean needToUpdate;
    private Timer timer;

    Chunkster(World world, int chunkX, int chunkZ) {
        this.world = world;
        this.chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        chunkStartX = chunkX * CHUNK_SIZE_X;
        chunkStartY = 0;
        chunkStartZ = chunkZ * CHUNK_SIZE_Z;
        needToUpdate = false;
        timer = new Timer();
        storage = chunk.getBlockStorageArray();
        for (int i = 0 ; i < CHUNK_PARTS; ++i) {
            if (storage[i] == Chunk.NULL_BLOCK_STORAGE) {
                storage[i] = new ExtendedBlockStorage(i << 4, !this.world.provider.getHasNoSky());
            }
        }
        heights = new int[CHUNK_SIZE_X][CHUNK_SIZE_Z];
        bottomHeights = new int[CHUNK_SIZE_X][CHUNK_SIZE_Z];
        trackHeight();
    }

    private void trackHeight() {
        String dimName = WorldCache.getDimensionName(world);
        int startHeight = CHUNK_SIZE_Y - 1;
        startHeight = dimName.equalsIgnoreCase("Nether") ? 63 : startHeight;
        startHeight = dimName.equalsIgnoreCase("End") ? 127 : startHeight;
        for (int cx = 0; cx < CHUNK_SIZE_X; ++cx) {
            for (int cz = 0; cz < CHUNK_SIZE_Z; ++cz) {
                int hg = startHeight;
                while (hg > 0) {
                    int blockID = Blocks.blockID(getBlockState(cx, hg, cz));
                    if (Blocks.isOverlook(blockID)) {
                        --hg;
                    } else {
                        break;
                    }
                }
                heights[cx][cz] = hg + 1;
                while (hg > 0) {
                    int blockID = Blocks.blockID(getBlockState(cx, hg, cz));
                    if (Blocks.isOverlook(blockID) || Blocks.isLiquid(blockID)) {
                        --hg;
                    } else {
                        break;
                    }
                }
                bottomHeights[cx][cz] = hg + 1;
            }
        }
    }

    private void updateGrass() {
        for (int cx = 0; cx < CHUNK_SIZE_X; ++cx) {
            for (int cz = 0; cz < CHUNK_SIZE_Z; ++cz) {
                Block prevBlock = Blocks.AIR;
                boolean grassed = false;
                for (int y = Math.min(heights[cx][cz], CHUNK_SIZE_Y - 1); y >= 0; --y) {
                    Block block = Blocks.getBlock(getBlockState(cx, y, cz));
                    if (block == Blocks.GRASS && Blocks.isSoil(Blocks.blockID(prevBlock))) {
                        setBlockState(cx, y, cz, Blocks.state(Blocks.DIRT));
                    }
                    if (!grassed && block == Blocks.DIRT && Blocks.isOverlook(Blocks.blockID(prevBlock))) {
                        setBlockState(cx, y, cz, Blocks.state(Blocks.GRASS));
                        grassed = true;
                    }
                    prevBlock = block;
                }
            }
        }
    }

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

    private void notifyNeighbours() {
        for (int cx = 0, wx = chunkStartX; cx < CHUNK_SIZE_X; ++cx, ++wx) {
            for (int cz = 0, wz = chunkStartZ; cz < CHUNK_SIZE_Z; ++cz, ++wz) {
                for (int cy = 0, wy = chunkStartY; cy < CHUNK_SIZE_Y; ++cy, ++wy) {
                    try {
                        BlockPos pos = new BlockPos(wx, wy, wz);
                        world.notifyNeighborsOfStateChange(pos, Blocks.getBlock(getBlockState(cx, cy, cz)));
                    } catch (Throwable ignored) {}
                }
            }
        }
    }

    private void setBlockState(int x, int y, int z, IBlockState blockState) {
        if (isInsideY(y)) {
            WorldCache.removeTileEntity(world, new BlockPos(x + chunkStartX, y, z + chunkStartZ));
            storage[y / CHUNK_PART_Y].set(x, y % CHUNK_PART_Y, z, blockState);
            needToUpdate = true;
        }
    }

    private IBlockState getBlockState(int x, int y, int z) {
        return isInsideY(y) ? storage[y / CHUNK_PART_Y].get(x, y % CHUNK_PART_Y, z) : Blocks.state(Blocks.AIR);
    }

    int getHeight(BlockPos pos) {
        return heights[pos.getX() - chunkStartX][pos.getZ() - chunkStartZ];
    }

    int getBottomHeight(BlockPos pos) {
        return bottomHeights[pos.getX() - chunkStartX][pos.getZ() - chunkStartZ];
    }

    void setBlockState(BlockPos pos, IBlockState blockState) {
        setBlockState(pos.getX() - chunkStartX, pos.getY(), pos.getZ() - chunkStartZ, blockState);
    }

    IBlockState getBlockState(BlockPos pos) {
        return getBlockState(pos.getX() - chunkStartX, pos.getY(), pos.getZ() - chunkStartZ);
    }

    private boolean isInsideY(int wy) {
        return wy >= 0 && wy < CHUNK_SIZE_Y && storage[wy / CHUNK_PART_Y] != Chunk.NULL_BLOCK_STORAGE;
    }

    void update() {
        if (!needToUpdate) {
            return;
        }
        needToUpdate = false;
        trackHeight();
        updateGrass();
        notifyNeighbours();
        chunk.checkLight();
        notifyPlayers();
    }

    Timer getTimer() {
        return timer;
    }

}
