package com.ternsip.structpro.Structure;

import com.ternsip.structpro.Utils.Report;

/*
 * 3D volume of cells
 * Provide indexation inside
 * @author Ternsip
 * @since JDK 1.6
 */
@SuppressWarnings({"unused"})
public class Volume {

    /** Amount of cells over x-axis */
    int width;

    /** Amount of cells over y-axis */
    int height;

    /** Amount of cells over z-axis */
    int length;

    Volume() {}

    public Volume(int width, int height, int length) {
        this.width = width;
        this.height = height;
        this.length = length;
    }

    /** Returns posture over the volume */
    public Posture getPosture(int x, int y, int z, int rotX, int rotY, int rotZ, boolean flipX, boolean flipY, boolean flipZ) {
        return new Posture(x, y, z, rotX, rotY, rotZ, flipX, flipY, flipZ, width, height, length);
    }

    /**
     * Get index using relative position
     * @param x Internal X coordinate
     * @param y Internal Y coordinate
     * @param z Internal Z coordinate
     * @return Volume cell index
     */
    public int getIndex(int x, int y, int z) {
        return x + y * width * length + z * width;
    }

    /**
     * Get X position using index
     * @param index Volume cell index
     * @return Internal X coordinate
     */
    public int getX(int index) {
        return index % width;
    }

    /**
     * Get Y position using index
     * @param index Volume cell index
     * @return Internal Y coordinate
     */
    public int getY(int index) {
        return index / (width * length);
    }

    /**
     * Get Z position using index
     * @param index Volume cell index
     * @return Internal Z coordinate
     */
    public int getZ(int index) {
        return (index / width) % length;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public int getSize() {
        return width * height * length;
    }

    /**
     * Check cell with coordinates x, y, z is inside
     * @param x Internal X coordinate
     * @param y Internal Y coordinate
     * @param z Internal Z coordinate
     * @return Is cell inside
     */
    public boolean isInside(int x, int y, int z) {
        return  x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < length;
    }

    /**
     * Check cell with index is inside
     * @return Is cell inside
     */
    public boolean isInside(int index) {
        return isInside(getX(index), getY(index), getZ(index));
    }

    /**
     * Extend volume on X, Y, Z axis
     * @param x Extension on X axis
     * @param y Extension on Y axis
     * @param z Extension on Z axis
     * @return Extended volume
     */
    public Volume extend(int x, int y, int z) {
        return new Volume(width + 2 * x, height + 2 * y, length + 2 * z);
    }

    /**
     * Get report
     * @return Generated report
     */
    public Report report() {
        return new Report().post("SIZE", "[W=" + width + ";H=" + height + ";L=" + length + "]");
    }

}
