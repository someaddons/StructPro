package com.ternsip.structpro.Structure;

import com.ternsip.structpro.Universe.Blocks.Blocks;
import com.ternsip.structpro.Universe.Cache.Universe;
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
public class Blueprint {

    /* Tag file size limit in bytes */
    private static final long TAG_FILE_SIZE_LIMIT = 1024 * 1024 * 16;

    /* Blueprint abstract limitations */
    private static final int WIDTH_LIMIT = 1024;
    private static final int HEIGHT_LIMIT = 256;
    private static final int LENGTH_LIMIT = 1024;
    private static final long VOLUME_LIMIT = 256 * 256 * 256;

    int width;
    int height;
    int length;
    short[] blocks;
    byte[] meta;
    NBTTagCompound[] tiles;

    Blueprint() {}

    public Blueprint(World world, int posX, int posY, int posZ, int width, int height, int length) throws IOException {
        loadSchematic(world, posX, posY, posZ, width, height, length);
    }

    /* Get index using relative position */
    int getIndex(int x, int y, int z) {
        return x + y * width * length + z * width;
    }

    /* Get X-relative position using index */
    int getX(int index) {
        return index % width;
    }

    /* Get Y-relative position using index */
    int getY(int index) {
        return index / (width * length);
    }

    /* Get Z-relative position using index */
    int getZ(int index) {
        return (index / width) % length;
    }

    /* Load schematic from file */
    void loadSchematic(File file) throws IOException {
        if (file.length() > TAG_FILE_SIZE_LIMIT) {
            throw new IOException("File is too large: " + file.length());
        }
        readSchematic(Utils.readTags(file));
    }

    /* Load schematic from world fragment */
    private void loadSchematic(World world, int posX, int posY, int posZ, int width, int height, int length) throws IOException {
        String dimensions = "[W=" + width + ";H=" + height + ";L=" + length + "]";
        String dimLimit = "[W=" + WIDTH_LIMIT + ";H=" + HEIGHT_LIMIT + ";L=" + LENGTH_LIMIT + "]";
        if (width <= 0 || height <= 0 || length <= 0) {
            throw new IOException("Schematic has non-positive dimensions: " + dimensions);
        }
        if (width > WIDTH_LIMIT || height > HEIGHT_LIMIT || length > LENGTH_LIMIT) {
            throw new IOException("Schematic dimensions are too large: " + dimensions + "/" + dimLimit);
        }
        int volume = width * height * length;
        if (volume > VOLUME_LIMIT) {
            throw new IOException("Schematic is too big: " + volume + "/" + VOLUME_LIMIT);
        }
        this.width = width;
        this.height = height;
        this.length = length;
        blocks = new short[volume];
        meta = new byte[volume];
        tiles = new NBTTagCompound[volume];
        int airID = Blocks.blockID(Blocks.AIR);
        for (int ix = 0, x = posX; ix < width; ++ix, ++x) {
            for (int iy = 0, y = posY; iy < height; ++iy, ++y) {
                for (int iz = 0, z = posZ; iz < length; ++iz, ++z) {
                    IBlockState state = Universe.getBlockState(world, new BlockPos(x, y, z));
                    int blockID = Blocks.blockID(state);
                    int index = getIndex(ix, iy, iz);
                    if (Blocks.isVanillaID(blockID)) {
                        blocks[index] = (short) blockID;
                        meta[index] = (byte) Blocks.getMeta(state);
                        TileEntity tile = Universe.getTileEntity(world, new BlockPos(x, y, z));
                        if (tile != null) {
                            tiles[index] = new NBTTagCompound();
                            try {
                                tile.writeToNBT(tiles[index]);
                                tiles[index].setInteger("x", ix);
                                tiles[index].setInteger("y", iy);
                                tiles[index].setInteger("z", iz);
                            } catch (Throwable ignored) {}
                        }
                    } else {
                        blocks[index] = (short) airID;
                        meta[index] = 0;
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
        int volume = width * height * length;
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
            blocksID[i] = (byte) blocks[i];
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

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }
}
