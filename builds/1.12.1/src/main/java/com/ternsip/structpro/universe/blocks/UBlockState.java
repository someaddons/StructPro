package com.ternsip.structpro.universe.blocks;

import net.minecraft.block.state.IBlockState;

/**
 * Block state wrapper
 *
 * @author Ternsip
 */
@SuppressWarnings({"deprecation"})
public class UBlockState {

    /**
     * Native minecraft block state
     */
    private IBlockState state;

    public UBlockState(IBlockState state) {
        this.state = state;
    }

    /**
     * Get block
     *
     * @return Extracted block
     */
    public UBlock getBlock() {
        return new UBlock(state.getBlock());
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMeta() {
        return state.getBlock().getMetaFromState(state);
    }

    /**
     * Get block id
     *
     * @return Block index
     */
    public int getID() {
        return getBlock().getID();
    }

    /**
     * Get light level that emits block state
     *
     * @return block light value
     */
    public int getLight() {
        return state.getLightValue();
    }

    /**
     * Get opacity of block state
     *
     * @return Block opacity level
     */
    public int getOpacity() {
        return state.getLightOpacity();
    }

    /**
     * Get minecraft native block state
     */
    public IBlockState getState() {
        return state;
    }
}
