package com.ternsip.structpro.universe.blocks;

/**
 * Block state wrapper
 * @author  Ternsip
 */
@SuppressWarnings({"deprecation"})
public class UBlockState {

    /** Native minecraft block state */
    private UBlock block;
    private int meta;

    public UBlockState(UBlock block, int meta) {
        this.block = block;
        this.meta = meta;
    }

    /**
     * Get block
     * @return Extracted block
     */
    public UBlock getBlock() {
        return block;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMeta() {
        return meta;
    }

    /**
     * Get block id
     * @return Block index
     */
    public int getID() {
        return getBlock().getID();
    }

    /**
     * Get light level that emits block state
     * @return block light value
     */
    public int getLight() {
        return block.getLight();
    }

    /**
     * Get opacity of block state
     * @return Block opacity level
     */
    public int getOpacity() {
        return block.getOpacity();
    }

}
