package com.ternsip.structpro.universe.blocks;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Block position wrapper
 *
 * @author Ternsip
 */
public class UBlockPos {

    /**
     * Native minecraft block position
     */
    private BlockPos blockPos;

    /**
     * Construct block position from native minecraft block position
     */
    public UBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    /**
     * Construct block position from coordinates
     */
    public UBlockPos(int x, int y, int z) {
        blockPos = new BlockPos(x, y, z);
    }

    /**
     * Offset this BlockPos 1 block in the given direction
     */
    public UBlockPos offset(EnumFacing facing) {
        return new UBlockPos(blockPos.offset(facing));
    }

    /**
     * Gets the X coordinate
     */
    public int getX() {
        return blockPos.getX();
    }

    /**
     * Gets the Y coordinate
     */
    public int getY() {
        return blockPos.getY();
    }

    /**
     * Gets the Z coordinate
     */
    public int getZ() {
        return blockPos.getZ();
    }

    /**
     * Return native minecraft block position
     */
    public BlockPos getBlockPos() {
        return blockPos;
    }

}
