package com.ternsip.structpro.Structure;

import com.ternsip.structpro.Utils.Report;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

/* Reflects information about cuboid stereo-metric state in the world. */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Posture extends Volume {

    private int posX, posY, posZ;
    private int rotateX, rotateY, rotateZ;
    private boolean flipX, flipY, flipZ;
    private int sizeX, sizeY, sizeZ;
    private int endX, endY, endZ;

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

    /* Get world position of structure index block */
    BlockPos getWorldPos(int index) {
        return getWorldPos(getX(index), getY(index), getZ(index));
    }

    /* Get world position of structure index(x,y,z) block */
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

    /* Get posture position of world (wx,wy,wz) block*/
    BlockPos getPosturePos(int wx, int wy, int wz) {
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

    /* Get world metadata of block with metadata */
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

    public Posture extend(int x, int y, int z) {
        return super.extend(x, y, z).getPosture(posX - x, posY - y, posZ - z, rotateX, rotateY, rotateZ, flipX, flipY, flipZ);
    }

    /* Generate posture report */
    public Report report() {
        return new Report()
                .post("POS", "[X=" + posX + ";Y=" + posY + ";Z=" + posZ + "]")
                .post("ROTATE", "[X=" + rotateX + ";Y=" + rotateY + ";Z=" + rotateZ + "]")
                .post("FLIP", "[X=" + flipX + ";Y=" + flipY + ";Z=" + flipZ + "]");
    }

    public int getRotateX() {
        return rotateX;
    }

    public int getRotateY() {
        return rotateY;
    }

    public int getRotateZ() {
        return rotateZ;
    }

    public boolean isFlipX() {
        return flipX;
    }

    public boolean isFlipY() {
        return flipY;
    }

    public boolean isFlipZ() {
        return flipZ;
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
