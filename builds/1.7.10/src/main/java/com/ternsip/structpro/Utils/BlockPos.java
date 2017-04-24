package com.ternsip.structpro.Utils;

import net.minecraft.util.EnumFacing;

/**
 * BlocPos class is vanilla in 1.8+ version
 * @author Ternsip
 * @since JDK 1.6
 */
public class BlockPos {

    private int x, y, z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public BlockPos offset(EnumFacing facing) {
        return offset(facing, 1);
    }

    public BlockPos offset(EnumFacing facing, int n) {
        return n == 0 ? this : new BlockPos(this.getX() + facing.getFrontOffsetX() * n, this.getY() + facing.getFrontOffsetY() * n, this.getZ() + facing.getFrontOffsetZ() * n);
    }
}