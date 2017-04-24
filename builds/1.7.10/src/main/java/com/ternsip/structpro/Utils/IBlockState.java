package com.ternsip.structpro.Utils;

import net.minecraft.block.Block;

/**
 * BlocState class is vanilla in 1.8+ version
 * @author Ternsip
 * @since JDK 1.6
 */
public class IBlockState {

    private Block block;
    private int meta;

    public IBlockState(Block block, int meta) {
        this.block = block;
        this.meta = meta;
    }

    public Block getBlock() {
        return block;
    }

    public int getMeta() {
        return meta;
    }
}
