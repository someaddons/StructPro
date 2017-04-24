package com.ternsip.structpro.Structure;

import com.ternsip.structpro.Utils.BlockPos;
import com.ternsip.structpro.Utils.Report;
import net.minecraft.block.Block;

/**
 * Reflects information about cuboid stereo-metric state in the world
 * @author Ternsip
 * @since JDK 1.6
 */
@SuppressWarnings({"unused"})
public class Posture extends Volume {

    /** Starting block position */
    private int posX, posY, posZ;

    /** Rotation around X, Y, Z axis */
    private int rotateX, rotateY, rotateZ;

    /** Mirror flip towards X, Y, Z axis */
    private boolean flipX, flipY, flipZ;

    /** Size towards X, Y, Z axis according to all transformations */
    private int sizeX, sizeY, sizeZ;

    /** Ending block position according to all transformations */
    private int endX, endY, endZ;

    /**
     * Construct from Starting point, transformations and size
     * @param posX Starting X position
     * @param posY Starting Y position
     * @param posZ Starting Z position
     * @param flipX Flipping toward X axis
     * @param flipY Flipping toward Y axis
     * @param flipZ Flipping toward Z axis
     * @param width Volume width
     * @param height Volume height
     * @param length Volume length
     */
    Posture(int posX, int posY, int posZ,
            int rotateX, int rotateY, int rotateZ,
            boolean flipX, boolean flipY, boolean flipZ,
            int width, int height, int length) {
        super(width, height, length);
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.rotateX = (rotateX % 4 + 4) % 4;
        this.rotateY = (rotateY % 4 + 4) % 4;
        this.rotateZ = (rotateZ % 4 + 4) % 4;
        this.flipX = flipX;
        this.flipY = flipY;
        this.flipZ = flipZ;
        if (width <= 0 || height <= 0 || length <= 0) {
            throw new IllegalArgumentException("Bad dimensions: [W=" + width + ";H=" + height + ";L=" + length + "]");
        }
        this.sizeX = width;
        this.sizeY = height;
        this.sizeZ = length;
        if (rotateY % 2 > 0) {
            int tmp = sizeX; sizeX = sizeZ; sizeZ = tmp;
        }
        /*
        if (rotateX % 2 > 0) { int tmp = sizeY; sizeY = sizeZ; sizeZ = tmp;}
        if (rotateZ % 2 > 0) {int tmp = sizeX; sizeX = sizeY; sizeY = tmp;}
        */
        this.endX = posX + sizeX - 1;
        this.endY = posY + sizeY - 1;
        this.endZ = posZ + sizeZ - 1;
    }

    /**
     * Get world position of volume index block
     * @param index Volume cell index
     * @return World block position
     */
    BlockPos getWorldPos(int index) {
        return getWorldPos(getX(index), getY(index), getZ(index));
    }

    /**
     * Get world position of volume x, y, z block
     * @param x Volume x position
     * @param y Volume y position
     * @param z Volume z position
     * @return World block position
     */
    BlockPos getWorldPos(int x, int y, int z) {
        int wx = flipX ? width - x - 1 : x;
        int wy = flipY ? height - y - 1 : y;
        int wz = flipZ ? length - z - 1 : z;
        int w = width, h = height, l = length;
        /* ADD X rotations */
        /* ADD Z rotations */
        for (int i = 0; i < rotateY; ++i) {
            int tmp = wz; wz = w - wx - 1; wx = tmp; tmp = w; w = l; l = tmp;
        }
        return new BlockPos(wx + posX, wy + posY, wz + posZ);
    }

    /**
     * Get volume position of world (wx, wy, wz) block
     * @param wx World X position
     * @param wy World Y position
     * @param wz World Z position
     * @return Volume block position
     */
    BlockPos getVolumePos(int wx, int wy, int wz) {
        int x = wx - posX;
        int y = wy - posY;
        int z = wz - posZ;
        int w = width, h = height, l = length;
        if (rotateY % 2 > 0) {
            int tmp = w; w = l; l = tmp;
        }
        for (int i = 0; i < rotateY; ++i) {
            int tmp = x; x = l - z - 1; z = tmp; tmp = w; w = l; l = tmp;
        }
        /* ADD Z rotations */
        /* ADD X rotations */
        x = flipX ? w - x - 1 : x;
        y = flipY ? h - y - 1 : y;
        z = flipZ ? l - z - 1 : z;
        return new BlockPos(x, y, z);
    }

    /**
     * Get world metadata of block state
     * @param block Target block
     * @param meta Block metadata
     * @return transformed metadata
     */
    int getWorldMeta(Block block, byte meta) {
        Directions.BlockType blockType = Directions.getBlockType(block, meta);
        int mask = Directions.getMask(blockType);
        int overlap = (meta & mask) ^ meta;
        int direction = Directions.getDirection(meta & mask, blockType);
        /* Rotation counter-clockwise */
        int[] rotationsY = {Directions.EAST, Directions.NORTH, Directions.WEST, Directions.SOUTH};
        /* ADD X rotations */
        /* ADD Z rotations */
        /* ADD Y flip */
        int rotY = rotateY;
        boolean zAxis = (direction == Directions.SOUTH || direction == Directions.NORTH);
        boolean xAxis = (direction == Directions.WEST || direction == Directions.EAST);
        if (Directions.isDoubleDirected(blockType)) {
            /* Always should be directed clockwise */
            if (flipX && flipZ) {
                rotY += 2;
            } else {
                rotY += flipX ? zAxis ? 1 : -1 : 0;
                rotY += flipZ ? xAxis ? 1 : -1 : 0;
            }
        } else {
            rotY += (flipX && xAxis) ? 2 : 0;
            rotY += (flipZ && zAxis) ? 2 : 0;
        }
        rotY = (4 + rotY % 4) % 4;
        if (direction == Directions.EAST) {
            return Directions.getMeta(meta, rotationsY[rotY % 4], blockType) | overlap;
        }
        if (direction == Directions.NORTH) {
            return Directions.getMeta(meta, rotationsY[(1 + rotY) % 4], blockType) | overlap;
        }
        if (direction == Directions.WEST) {
            return Directions.getMeta(meta, rotationsY[(2 + rotY) % 4], blockType) | overlap;
        }
        if (direction == Directions.SOUTH) {
            return Directions.getMeta(meta, rotationsY[(3 + rotY) % 4], blockType) | overlap;
        }
        return meta | overlap;
    }

    /**
     * Extend posture towards and backwards X, Y, Z axis
     * @param x X-axis extension delta
     * @param y Y-axis extension delta
     * @param z Z-axis extension delta
     * @return New extended posture
     */
    public Posture extend(int x, int y, int z) {
        return super.extend(x, y, z).getPosture(posX - x, posY - y, posZ - z, rotateX, rotateY, rotateZ, flipX, flipY, flipZ);
    }

    /**
     * Combine posture report
     * @return Generated report
     */
    public Report report() {
        return new Report()
                .post("POS", "[X=" + posX + ";Y=" + posY + ";Z=" + posZ + "]")
                .post("ROTATE", "[X=" + rotateX + ";Y=" + rotateY + ";Z=" + rotateZ + "]")
                .post("FLIP", "[X=" + flipX + ";Y=" + flipY + ";Z=" + flipZ + "]");
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getPosZ() {
        return posZ;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public int getEndZ() {
        return endZ;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getSizeZ() {
        return sizeZ;
    }
}
