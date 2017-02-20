package com.ternsip.structpro.Structure;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Schematic - Classical Minecraft schematic storage
 * Provide data access and world control
 */
class Blueprint {

    /* Tag file size limit in bytes */
    private static final long TAG_FILE_SIZE_LIMIT = 1024 * 1024 * 16;

    /* Blueprint abstract limitations */
    private static final int WIDTH_LIMIT = 1024;
    private static final int HEIGHT_LIMIT = 255;
    private static final int LENGTH_LIMIT = 1024;
    private static final long VOLUME_LIMIT = 256 * 256 * 256;

    int width;
    int height;
    int length;
    short[] blocks;
    byte[] meta;
    NBTTagCompound[] tiles;

    Blueprint() {}

    int getIndex(int x, int y, int z) {
        return x + y * width * length + z * width;
    }

    int getX(int index) {
        return index % width;
    }

    int getY(int index) {
        return index / (width * length);
    }

    int getZ(int index) {
        return (index / width) % length;
    }

    void loadSchematic(File file) throws IOException {
        readSchematic(readTags(file));
    }

    /* Load map tag from file */
    static NBTTagCompound readTags(File file) throws IOException {
        if (file.length() > TAG_FILE_SIZE_LIMIT) {
            throw new IOException("File is too large: " + file.length());
        }
        FileInputStream fis = new FileInputStream(file);
        try {
            return CompressedStreamTools.readCompressed(fis);
        } finally {
            fis.close();
        }
    }

    static void writeTags(File file, NBTTagCompound tag) throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(file);
        try {
            CompressedStreamTools.writeCompressed(tag, fos);
        } finally {
            fos.close();
        }
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
