package com.ternsip.structpro.universe.blocks;

import net.minecraft.block.Block;

/**
 * Block wrapper
 * @author  Ternsip
 */
@SuppressWarnings({"WeakerAccess", "unused", "deprecation"})
public class UBlock {

    /** Native minecraft block */
    private Block block;

    /** Construct block from native minecraft block */
    public UBlock(Block block) {
        this.block = block;
    }

    /**
     * Get block from block id
     * @param blockID Block index
     * @return Converted block
     */
    public static UBlock getById(int blockID) {
        return new UBlock(Block.getBlockById(blockID));
    }

    /**
     * Get Block state from block
     * @return Block state
     */
    public final UBlockState getState() {
        return new UBlockState(this, 0);
    }

    /**
     * Get Block state from block and metadata
     * @param meta Block metadata
     * @return Converted block state
     */
    public UBlockState getState(int meta) {
        UBlockState result = getState();
        try {
            result = getStateFromMeta(meta);
        } catch (Throwable ignored) {}
        return result;
    }

    /**
     * Get block state from metadata
     * @param meta Block metadata
     * @return Block state
     */
    public UBlockState getStateFromMeta(int meta) {
        return new UBlockState(this, meta);
    }

    /**
     * Block id from block
     * @return Block index
     */
    public int getID() {
        return Block.getIdFromBlock(block);
    }

    /**
     * Check if current block valid
     * @return is block valid
     */
    public boolean isValid() {
        return block != null && block.getUnlocalizedName() != null;
    }

    /**
     * Get resource path
     * @return Resource path
     */
    public String getPath() {
        return block.getUnlocalizedName().replace("tile.", "");
    }

    /**
     * Get light level that emits block
     * @return block light value
     */
    public int getLight() {
        return block.getLightValue();
    }

    /**
     * Get light level that emits block
     * @return Block opacity level
     */
    public int getOpacity() {
        return block.getLightOpacity();
    }

    /**
     * Get native minecraft block
     * @return Native block
     */
    public Block getBlock() {
        return block;
    }


}
