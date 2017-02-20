package com.ternsip.structpro.WorldCache;

import com.ternsip.structpro.Logic.Loader;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.tileentity.TileEntity;
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
    private int chunkEndX;
    private int chunkEndY;
    private int chunkEndZ;
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
        chunkEndX = chunkStartX + CHUNK_SIZE_X - 1;
        chunkEndY = CHUNK_SIZE_Y - 1;
        chunkEndZ = chunkStartZ + CHUNK_SIZE_Z - 1;
        needToUpdate = false;
        timer = new Timer();
        track();
        heights = new int[CHUNK_SIZE_X][CHUNK_SIZE_Z];
        bottomHeights = new int[CHUNK_SIZE_X][CHUNK_SIZE_Z];
        trackHeight();
    }

    private void track() {
        IBlockState[] traceData = new IBlockState[CHUNK_SIZE_Y];
        storage = chunk.getBlockStorageArray();
        if (storage[CHUNK_PARTS - 1] == null) {
            for (int sy = chunkStartY; sy < chunkEndY; sy += 16) {
                traceData[sy] = chunk.getBlockState(new BlockPos(0, sy, 0));
                chunk.setBlockState(new BlockPos(0, sy, 0), Blocks.OBSIDIAN.getDefaultState());
            }
            storage = chunk.getBlockStorageArray();
            for (int sy = chunkStartY; sy < chunkEndY; sy += 16) {
                chunk.setBlockState(new BlockPos(0, sy, 0), traceData[sy]);
            }
        }
    }

    private void trackHeight() {
        boolean[] overlook = Loader.overlook;
        boolean[] liquid = Loader.liquid;
        String dimName = WorldCache.getDimensionName(world);
        int startHeight = 255;
        startHeight = dimName.equalsIgnoreCase("Nether") ? 63 : startHeight;
        startHeight = dimName.equalsIgnoreCase("End") ? 127 : startHeight;
        for (int cx = 0, wx = chunkStartX; cx < CHUNK_SIZE_X; ++wx, ++cx) {
            for (int cz = 0, wz = chunkStartZ; cz < CHUNK_SIZE_Z; ++wz, ++cz) {
                int hg = startHeight;
                while (hg > 0) {
                    int blockID = WorldCache.blockID(getBlockState(wx, hg, wz).getBlock());
                    if (blockID >= 0 && blockID < 256 && overlook[blockID]) {
                        --hg;
                    } else {
                        break;
                    }
                }
                heights[cx][cz] = hg + 1;
                while (hg > 0) {
                    int blockID = WorldCache.blockID(getBlockState(wx, hg, wz).getBlock());
                    if (blockID >= 0 && blockID < 256 && (overlook[blockID] || liquid[blockID])) {
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
        for (int cx = 0, wx = chunkStartX; cx < CHUNK_SIZE_X; ++wx, ++cx) {
            for (int cz = 0, wz = chunkStartZ; cz < CHUNK_SIZE_Z; ++wz, ++cz) {
                Block prevBlock = Blocks.AIR;
                boolean grassed = false;
                for (int wy = Math.min(heights[cx][cz], 255); wy >= 0; --wy) {
                    Block block = getBlockState(wx, wy, wz).getBlock();
                    if (block == Blocks.GRASS && Loader.soil[WorldCache.blockID(prevBlock)]) {
                        setBlockState(wx, wy, wz, Blocks.DIRT.getDefaultState());
                    }
                    if (!grassed && block == Blocks.DIRT && Loader.overlook[WorldCache.blockID(prevBlock)]) {
                        setBlockState(wx, wy, wz, Blocks.GRASS.getDefaultState());
                        grassed = true;
                    }
                    prevBlock = block;
                }
            }
        }
    }

    int getHeight(int wx, int wz) {
        return heights[wx - chunkStartX][wz - chunkStartZ];
    }

    int getBottomHeight(int wx, int wz) {
        return bottomHeights[wx - chunkStartX][wz - chunkStartZ];
    }

    void setBlockState(int wx, int wy, int wz, IBlockState blockState) {
        removeTileEntity(wx, wy, wz);
        storage[wy / CHUNK_PART_Y].set(wx - chunkStartX, wy % CHUNK_PART_Y, wz - chunkStartZ, blockState);
        needToUpdate = true;
    }

    IBlockState getBlockState(int wx, int wy, int wz) {
        return storage[wy / CHUNK_PART_Y].get(wx - chunkStartX, wy % CHUNK_PART_Y, wz - chunkStartZ);
    }

    TileEntity getTileEntity(int wx, int wy, int wz) {
        return world.getTileEntity(new BlockPos(wx, wy, wz));
    }

    void setTileEntity(int wx, int wy, int wz, TileEntity tileEntity) {
        world.setTileEntity(new BlockPos(wx, wy, wz), tileEntity);
    }

    void removeTileEntity(int wx, int wy, int wz) {
        world.removeTileEntity(new BlockPos(wx, wy, wz));
    }

    void update() {
        if (!needToUpdate) {
            return;
        }
        trackHeight();
        updateGrass();
        chunk.checkLight();
        for (EntityPlayer player : world.playerEntities) {
            if (player instanceof EntityPlayerMP) {
                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                if (Math.abs(playerMP.chunkCoordX - chunkX) <= 32 && Math.abs(playerMP.chunkCoordZ - chunkZ) <= 32) {
                    Packet<?> packet = new SPacketChunkData(chunk, 65535);
                    playerMP.connection.sendPacket(packet);
                }
            }
        }
        needToUpdate = false;
    }

    public Timer getTimer() {
        return timer;
    }
}
