package com.ternsip.structpro.universe.blocks;

import net.minecraft.util.EnumFacing;

/**
 * Block position wrapper
 * @author  Ternsip
 */
public class UBlockPos {

    private int x, y, z;

    /** Construct block position from coordinates */
    public UBlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** Offset this BlockPos 1 block in the given direction */
    public UBlockPos offset(EnumFacing facing) {
        return offset(facing, 1);
    }

    public UBlockPos offset(EnumFacing facing, int n) {
        return n == 0 ? this : new UBlockPos(
                this.getX() + facing.getFrontOffsetX() * n,
                this.getY() + facing.getFrontOffsetY() * n,
                this.getZ() + facing.getFrontOffsetZ() * n);
    }

    /** Gets the X coordinate */
    public int getX() {
        return x;
    }

    /** Gets the Y coordinate */
    public int getY() {
        return y;
    }

    /** Gets the Z coordinate */
    public int getZ() {
        return z;
    }

}
