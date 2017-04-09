package com.ternsip.structpro.Structure;

import com.ternsip.structpro.Universe.Blocks.Blocks;
import com.ternsip.structpro.Universe.Cache.Universe;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.io.File;
import java.io.IOException;

/* Schematic - Classical Minecraft schematic storage. Provide data access and world control */
public class Blueprint extends Volume {

    /* Tag file size limit in bytes */
    private static final long TAG_FILE_SIZE_LIMIT = 1024 * 1024 * 16;

    /* Blueprint abstract limitations */
    private static final int WIDTH_LIMIT = 1024;
    private static final int HEIGHT_LIMIT = 256;
    private static final int LENGTH_LIMIT = 1024;
    private static final long VOLUME_LIMIT = 256 * 256 * 256;

    short[] blocks;
    byte[] meta;
    NBTTagCompound[] tiles;

    Blueprint() {}

    public Blueprint(World world, BlockPos start, Volume volume) {
        loadSchematic(world, start, volume);
    }

    /* Load schematic from file */
    void loadSchematic(File file) throws IOException {
        if (file.length() > TAG_FILE_SIZE_LIMIT) {
            throw new IOException("File is too large: " + file.length());
        }
        readSchematic(Utils.readTags(file));
    }

    /* Load schematic from world fragment */
    private void loadSchematic(World world, BlockPos start, Volume volume) {
        this.width = volume.getWidth();
        this.height = volume.getHeight();
        this.length = volume.getLength();
        int size = getVolume();
        blocks = new short[size];
        meta = new byte[size];
        tiles = new NBTTagCompound[size];
        Posture posture = getPosture(start.getX(), start.getY(), start.getZ(), 0, 0, 0, false, false, false);
        for (int ix = 0; ix < width; ++ix) {
            for (int iy = 0; iy < height; ++iy) {
                for (int iz = 0; iz < length; ++iz) {
                    BlockPos pos = posture.getWorldPos(ix, iy, iz);
                    IBlockState state = Universe.getBlockState(world, pos);
                    int blockID = Blocks.getID(state);
                    int index = getIndex(ix, iy, iz);
                    blocks[index] = (short) blockID;
                    meta[index] = (byte) Blocks.getMeta(state);
                    TileEntity tile = Universe.getTileEntity(world, pos);
                    if (tile != null) {
                        tiles[index] = new NBTTagCompound();
                        try {
                            tile.writeToNBT(tiles[index]);
                            tiles[index].setInteger("x", ix);
                            tiles[index].setInteger("y", iy);
                            tiles[index].setInteger("z", iz);
                        } catch (Throwable ignored) {}
                    }
                }
            }
        }
    }

    /* Save blueprint as schematic */
    public void saveSchematic(File file) throws IOException {
        Utils.writeTags(file, getSchematic());
    }

    /* Read Schematic blueprint from tags */
    private void readSchematic(NBTTagCompound tag) throws IOException {
        String materials = tag.getString("Materials");
        if (!materials.equals("Alpha")) {
            throw new IOException("Materials of schematic is not an alpha: [" + materials + "]");
        }
        width = tag.getShort("Width");
        height = tag.getShort("Height");
        length = tag.getShort("Length");
        String dimensions = "[W=" + width + ";H=" + height + ";L=" + length + "]";
        String dimLimit = "[W=" + WIDTH_LIMIT + ";H=" + HEIGHT_LIMIT + ";L=" + LENGTH_LIMIT + "]";
        if (width <= 0 || height <= 0 || length <= 0) {
            throw new IOException("Schematic has non-positive dimensions: " + dimensions);
        }
        if (width > WIDTH_LIMIT || height > HEIGHT_LIMIT || length > LENGTH_LIMIT) {
            throw new IOException("Schematic dimensions are too large: " + dimensions + "/" + dimLimit);
        }
        int volume = getVolume();
        if (volume > VOLUME_LIMIT) {
            throw new IOException("Schematic is too big: " + volume + "/" + VOLUME_LIMIT);
        }
        byte[] addBlocks = tag.getByteArray("AddBlocks");
        byte[] blocksID = tag.getByteArray("Blocks");
        if (volume != blocksID.length) {
            throw new IOException("Wrong schematic blocks length: " + blocksID.length + "/" + volume);
        }
        blocks = compose(blocksID, addBlocks);
        meta = tag.getByteArray("Data");
        if (volume != meta.length) {
            throw new IOException("Wrong schematic metadata length: " + blocksID.length + "/" + volume);
        }
        tiles = new NBTTagCompound[width * height * length];
        NBTTagList tileEntities = tag.getTagList("TileEntities", Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < tileEntities.tagCount(); i++) {
            NBTTagCompound tile = tileEntities.getCompoundTagAt(i);
            int x = tile.getInteger("x");
            int y = tile.getInteger("y");
            int z = tile.getInteger("z");
            int idx = getIndex(x, y, z);
            if (idx >= 0 && idx < width * height * length) {
                tiles[idx] = tile;
            }
        }
    }

    /* Read Schematic blueprint from tags */
    private NBTTagCompound getSchematic() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("Materials", "Alpha");
        tag.setShort("Width", (short) width);
        tag.setShort("Height", (short) height);
        tag.setShort("Length", (short) length);
        tag.setByteArray("AddBlocks", new byte[0]);
        byte[] blocksID = new byte[blocks.length];
        for (int i = 0; i < blocks.length; ++i) {
            blocksID[i] = Blocks.isVanilla(blocksID[i]) ? (byte) blocks[i] : 0;
        }
        tag.setByteArray("Blocks", blocksID);
        tag.setByteArray("Data", meta);
        NBTTagList tileEntities = new NBTTagList();
        for (NBTTagCompound tile : tiles) {
            if (tile != null) {
                tileEntities.appendTag(tile);
            }
        }
        tag.setTag("TileEntities", tileEntities);
        return tag;
    }

    /* Combine all 8b-blocksID and 8b-addBlocks to 16b-block */
    private static short[] compose(byte[] blocksID, byte[] addBlocks) {
        short[] blocks = new short[blocksID.length];
        for (int index = 0; index < blocksID.length; index++) {
            if ((index >> 1) >= addBlocks.length) {
                blocks[index] = (short) (blocksID[index] & 0xFF);
            } else {
                if ((index & 1) == 0) {
                    blocks[index] = (short) (((addBlocks[index >> 1] & 0x0F) << 8) + (blocksID[index] & 0xFF));
                } else {
                    blocks[index] = (short) (((addBlocks[index >> 1] & 0xF0) << 4) + (blocksID[index] & 0xFF));
                }
            }
        }
        return blocks;
    }

    /*
    * Inject blueprint into specific position in the world
    * Unsafe for cross-versioned data
    */
    void project(World world, Posture posture, long seed) throws IOException {
        for (int ix = 0; ix < width; ++ix) {
            for (int iy = 0; iy < height; ++iy) {
                for (int iz = 0; iz < length; ++iz) {
                    int index = getIndex(ix, iy, iz);
                    BlockPos worldPos = posture.getWorldPos(ix, iy, iz);
                    Universe.setBlockState(world, worldPos, Blocks.state(Blocks.getBlock(blocks[index]), meta[index]));
                    Universe.removeTileEntity(world, worldPos);
                    if (tiles[index] != null) {
                        NBTTagCompound tag = tiles[index].copy();
                        tag.setInteger("x", worldPos.getX());
                        tag.setInteger("y", worldPos.getY());
                        tag.setInteger("z", worldPos.getZ());
                        TileEntity entity = TileEntity.create(world, tag);
                        Universe.setTileEntity(world, worldPos, entity);
                    }
                }
            }
        }
        Universe.update();
    }

    /* Generate blueprint report */
    public Report report() {
        return new Report().post("SIZE", "[W=" + width + ";H=" + height + ";L=" + length + "]");
    }

}
