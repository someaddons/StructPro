package com.ternsip.structpro.WorldCache;

import com.ternsip.structpro.Logic.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
        track();
        heights = new int[CHUNK_SIZE_X][CHUNK_SIZE_Z];
        bottomHeights = new int[CHUNK_SIZE_X][CHUNK_SIZE_Z];
        trackHeight();
    }

    private void track() {
        storage = chunk.getBlockStorageArray();
        for (int i = 0 ; i < CHUNK_PARTS; ++i) {
            if (storage[i] == Chunk.NULL_BLOCK_STORAGE) {
                storage[i] = new ExtendedBlockStorage(i << 4, !this.world.provider.getHasNoSky());
            }
        }
    }

    private void trackHeight() {
        String dimName = WorldCache.getDimensionName(world);
        int startHeight = CHUNK_SIZE_Y - 1;
        startHeight = dimName.equalsIgnoreCase("Nether") ? 63 : startHeight;
        startHeight = dimName.equalsIgnoreCase("End") ? 127 : startHeight;
        for (int cx = 0, wx = chunkStartX; cx < CHUNK_SIZE_X; ++wx, ++cx) {
            for (int cz = 0, wz = chunkStartZ; cz < CHUNK_SIZE_Z; ++wz, ++cz) {
                int hg = startHeight;
                while (hg > 0) {
                    int blockID = Blocks.blockID(getBlockState(wx, hg, wz));
                    if (Blocks.isOverlook(blockID)) {
                        --hg;
                    } else {
                        break;
                    }
                }
                heights[cx][cz] = hg + 1;
                while (hg > 0) {
                    int blockID = Blocks.blockID(getBlockState(wx, hg, wz));
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
        for (int cx = 0, wx = chunkStartX; cx < CHUNK_SIZE_X; ++wx, ++cx) {
            for (int cz = 0, wz = chunkStartZ; cz < CHUNK_SIZE_Z; ++wz, ++cz) {
                Block prevBlock = Blocks.AIR;
                boolean grassed = false;
                for (int wy = Math.min(heights[cx][cz], CHUNK_SIZE_Y - 1); wy >= 0; --wy) {
                    Block block = Blocks.getBlock(getBlockState(wx, wy, wz));
                    if (block == Blocks.GRASS && Blocks.isSoil(Blocks.blockID(prevBlock))) {
                        setBlockState(wx, wy, wz, Blocks.state(Blocks.DIRT));
                    }
                    if (!grassed && block == Blocks.DIRT && Blocks.isOverlook(Blocks.blockID(prevBlock))) {
                        setBlockState(wx, wy, wz, Blocks.state(Blocks.GRASS));
                        grassed = true;
                    }
                    prevBlock = block;
                }
            }
        }
    }

    void setBlockState(int wx, int wy, int wz, IBlockState blockState) {
        if (isInsideY(wy)) {
            removeTileEntity(wx, wy, wz);
            storage[wy / CHUNK_PART_Y].set(wx - chunkStartX, wy % CHUNK_PART_Y, wz - chunkStartZ, blockState);
            needToUpdate = true;
        }
    }

    void setTileEntity(int wx, int wy, int wz, TileEntity tileEntity) {
        if (isInsideY(wy)) {
            world.setTileEntity(new BlockPos(wx, wy, wz), tileEntity);
            needToUpdate = true;
        }
    }

    int getHeight(int wx, int wz) {
        return heights[wx - chunkStartX][wz - chunkStartZ];
    }

    int getBottomHeight(int wx, int wz) {
        return bottomHeights[wx - chunkStartX][wz - chunkStartZ];
    }

    IBlockState getBlockState(int wx, int wy, int wz) {
        return isInsideY(wy) ? storage[wy / CHUNK_PART_Y].get(wx - chunkStartX, wy % CHUNK_PART_Y, wz - chunkStartZ) : Blocks.state(Blocks.AIR);
    }

    TileEntity getTileEntity(int wx, int wy, int wz) {
        return isInsideY(wy) ? world.getTileEntity(new BlockPos(wx, wy, wz)) : null;
    }

    private boolean isInsideY(int wy) {
        return wy >= 0 && wy < CHUNK_SIZE_Y && storage[wy / CHUNK_PART_Y] != Chunk.NULL_BLOCK_STORAGE;
    }

    private void removeTileEntity(int wx, int wy, int wz) {
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

    Timer getTimer() {
        return timer;
    }
}
