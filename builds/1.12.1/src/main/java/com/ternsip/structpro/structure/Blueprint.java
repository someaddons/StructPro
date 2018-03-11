package com.ternsip.structpro.structure;

import com.ternsip.structpro.universe.blocks.UBlock;
import com.ternsip.structpro.universe.blocks.UBlockPos;
import com.ternsip.structpro.universe.blocks.UBlockState;
import com.ternsip.structpro.universe.blocks.UBlocks;
import com.ternsip.structpro.universe.entities.Tiles;
import com.ternsip.structpro.universe.utils.Utils;
import com.ternsip.structpro.universe.world.UWorld;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Schematic - Classical Minecraft schematic storage
 * Provide controls for schematic
 *
 * @author Ternsip
 */
@SuppressWarnings({"WeakerAccess"})
public class Blueprint extends Volume implements Schema {

    /**
     * Tag file size limit in bytes
     */
    private static final long TAG_FILE_SIZE_LIMIT = 1024 * 1024 * 16;

    /**
     * Block ID array
     */
    private short[] blocks;

    /**
     * Block metadata array
     */
    private byte[] metas;

    /**
     * Tag array
     */
    private NBTTagCompound[] tiles;

    /**
     * Empty constructor
     */
    Blueprint() {
    }

    /**
     * Construct from extracted world part
     *
     * @param world  World instance
     * @param start  Starting position
     * @param volume Volume dimensions
     */
    public Blueprint(UWorld world, UBlockPos start, Volume volume) {
        super(volume);
        int size = getSize();
        setBlocks(new short[size]);
        setMetas(new byte[size]);
        setTiles(new NBTTagCompound[size]);
        Posture posture = getPosture(start.getX(), start.getY(), start.getZ(), 0, 0, 0, false, false, false);
        for (int ix = 0; ix < getWidth(); ++ix) {
            for (int iy = 0; iy < getHeight(); ++iy) {
                for (int iz = 0; iz < getLength(); ++iz) {
                    UBlockPos pos = posture.getWorldPos(ix, iy, iz);
                    UBlockState state = world.getBlockState(pos);
                    int blockID = state.getID();
                    int index = getIndex(ix, iy, iz);
                    setBlock((short) blockID, index);
                    setMeta((byte) state.getMeta(), index);
                    setTile(world.getTileTag(pos), index);
                    if (getTile(index) != null) {
                        getTile(index).setInteger("x", ix);
                        getTile(index).setInteger("y", iy);
                        getTile(index).setInteger("z", iz);
                    }
                }
            }
        }
    }

    /**
     * Load schematic from file
     *
     * @param file File to load
     * @throws IOException If schematic can not be loaded
     */
    public void loadSchematic(File file) throws IOException {
        if (file.length() > TAG_FILE_SIZE_LIMIT) {
            throw new IOException("File is too large: " + file.length());
        }
        readSchematic(Utils.readTags(file));
    }

    /**
     * Save as schematic to file
     *
     * @param file Destination file
     * @throws IOException If schematic can not be saved
     */
    public void saveSchematic(File file) throws IOException {
        Utils.writeTags(file, getSchematic());
    }

    /**
     * Read from schematic tag
     *
     * @param tag Control tag
     * @throws IOException If schematic tag can not be read
     */
    private void readSchematic(NBTTagCompound tag) throws IOException {
        String materials = tag.getString("Materials");
        if (!materials.equals("Alpha")) {
            throw new IOException("Materials of schematic is not an alpha: [" + materials + "]");
        }
        setWidth(tag.getShort("Width"));
        setHeight(tag.getShort("Height"));
        setLength(tag.getShort("Length"));
        String dimensions = "[W=" + getWidth() + ";H=" + getHeight() + ";L=" + getLength() + "]";
        String dimLimit = "[W=" + WIDTH_LIMIT + ";H=" + HEIGHT_LIMIT + ";L=" + LENGTH_LIMIT + "]";
        if (getWidth() <= 0 || getHeight() <= 0 || getLength() <= 0) {
            throw new IOException("Schematic has non-positive dimensions: " + dimensions);
        }
        if (getWidth() > WIDTH_LIMIT || getHeight() > HEIGHT_LIMIT || getLength() > LENGTH_LIMIT) {
            throw new IOException("Schematic dimensions are too large: " + dimensions + "/" + dimLimit);
        }
        int size = getSize();
        if (size > VOLUME_LIMIT) {
            throw new IOException("Schematic is too big: " + size + "/" + VOLUME_LIMIT);
        }
        byte[] addBlocks = tag.getByteArray("AddBlocks");
        byte[] blocksID = tag.getByteArray("Blocks");
        if (size != blocksID.length) {
            throw new IOException("Wrong schematic blocks length: " + blocksID.length + "/" + size);
        }
        setBlocks(compose(blocksID, addBlocks));
        setMetas(tag.getByteArray("Data"));
        if (size != getMetas().length) {
            throw new IOException("Wrong schematic metadata length: " + blocksID.length + "/" + size);
        }
        setTiles(new NBTTagCompound[size]);
        NBTTagList tileEntities = tag.getTagList("TileEntities", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tileEntities.tagCount(); i++) {
            NBTTagCompound tile = tileEntities.getCompoundTagAt(i);
            int x = tile.getInteger("x");
            int y = tile.getInteger("y");
            int z = tile.getInteger("z");
            int idx = getIndex(x, y, z);
            if (idx >= 0 && idx < size) {
                setTile(tile, idx);
            }
        }
    }

    /**
     * Write to schematic tags
     *
     * @return Control tag
     */
    private NBTTagCompound getSchematic() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("Materials", "Alpha");
        tag.setShort("Width", (short) getWidth());
        tag.setShort("Height", (short) getHeight());
        tag.setShort("Length", (short) getLength());
        byte[] blocksID = new byte[getBlocks().length];
        for (int i = 0; i < getBlocks().length; ++i) {
            blocksID[i] = (byte) getBlock(i);
        }
        tag.setByteArray("Blocks", blocksID);
        tag.setByteArray("AddBlocks", getAddBlocks(getBlocks()));
        tag.setByteArray("Data", getMetas());
        NBTTagList tileEntities = new NBTTagList();
        for (NBTTagCompound tile : getTiles()) {
            if (tile != null) {
                tileEntities.appendTag(tile);
            }
        }
        tag.setTag("TileEntities", tileEntities);
        return tag;
    }

    /**
     * Combine all 8b-blocksID and 8b-addBlocks to 16b-block
     *
     * @param blocksID  Vanilla block array
     * @param addBlocks Additional postfix array
     * @return Combined array of vanilla and additional blocks
     */
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

    /**
     * Decompose to 8b-addBlocks from 16b-block id
     *
     * @param blocksID Blocks id array
     * @return Decomposed array to AddBlocks
     */
    private static byte[] getAddBlocks(short[] blocksID) {
        byte[] addBlocks = new byte[(blocksID.length >> 1) + 1];
        for (int index = 0; index < blocksID.length; ++index) {
            short block = blocksID[index];
            if (block > 255) {
                addBlocks[index >> 1] = (byte) (((index & 1) == 0) ? addBlocks[index >> 1] & 0xF0 | (block >> 8) & 0xF : addBlocks[index >> 1] & 0xF | ((block >> 8) & 0xF) << 4);
            }
        }
        return addBlocks;
    }

    @Override
    public void project(UWorld world, Posture posture, long seed, boolean isInsecure) throws IOException {
        Random random = new Random(0);
        for (int ix = 0; ix < getWidth(); ++ix) {
            for (int iy = 0; iy < getHeight(); ++iy) {
                for (int iz = 0; iz < getLength(); ++iz) {
                    project(world, posture, getIndex(ix, iy, iz), isInsecure, random);
                }
            }
        }
        world.notifyPosture(posture);
    }

    @Override
    public void project(UWorld world, Posture posture, int index, boolean isInsecure, Random random) {
        UBlock block = isInsecure ? UBlock.getById(getBlock(index)) : UBlocks.getBlockVanilla(getBlock(index));
        if (block == null) {
            return;
        }
        UBlockPos pos = posture.getWorldPos(index);
        world.setBlockState(pos, block.getState(posture.getWorldMeta(block, getMeta(index))));
        if (isInsecure) {
            world.setTileTag(pos, getTile(index));
        } else {
            Tiles.load(world.getTileEntity(pos), getTile(index), random.nextLong());
        }
    }

    protected short[] getBlocks() {
        return blocks;
    }

    protected byte[] getMetas() {
        return metas;
    }

    protected NBTTagCompound[] getTiles() {
        return tiles;
    }

    protected short getBlock(int index) {
        return this.blocks[index];
    }

    protected byte getMeta(int index) {
        return this.metas[index];
    }

    protected NBTTagCompound getTile(int index) {
        return this.tiles[index];
    }

    protected void setBlocks(short[] blocks) {
        this.blocks = blocks;
    }

    protected void setMetas(byte[] meta) {
        this.metas = meta;
    }

    protected void setTiles(NBTTagCompound[] tiles) {
        this.tiles = tiles;
    }

    protected void setBlock(short block, int index) {
        this.blocks[index] = block;
    }

    protected void setMeta(byte meta, int index) {
        this.metas[index] = meta;
    }

    protected void setTile(NBTTagCompound tile, int index) {
        this.tiles[index] = tile;
    }

}
