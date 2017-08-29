package com.ternsip.structpro.structure;

import com.ternsip.structpro.universe.utils.Report;

/*
 * 3D volume of cells
 * Provide indexation inside
 * @author Ternsip
 */
public class Volume implements Cuboid, Reportable {

    /** Amount of cells over x-axis */
    private int width;

    /** Amount of cells over y-axis */
    private int height;

    /** Amount of cells over z-axis */
    private int length;

    Volume() {}

    Volume(Cuboid cuboid) {
        setWidth(cuboid.getWidth());
        setHeight(cuboid.getHeight());
        setLength(cuboid.getLength());
    }

    public Volume(int width, int height, int length) {
        setWidth(width);
        setHeight(height);
        setLength(length);
    }

    @Override
    public int getIndex(int x, int y, int z) {
        return x + y * width * length + z * width;
    }

    @Override
    public int getX(int index) {
        return index % width;
    }

    @Override
    public int getY(int index) {
        return index / (width * length);
    }

    @Override
    public int getZ(int index) {
        return (index / width) % length;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getSize() {
        return width * height * length;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public boolean isInside(int x, int y, int z) {
        return  x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < length;
    }

    @Override
    public boolean isInside(int index) {
        return isInside(getX(index), getY(index), getZ(index));
    }

    @Override
    public Report report() {
        return new Report().post("SIZE", "[W=" + width + ";H=" + height + ";L=" + length + "]");
    }

    /** Returns posture over the volume */
    public Posture getPosture(int x, int y, int z, int rotX, int rotY, int rotZ, boolean flipX, boolean flipY, boolean flipZ) {
        return new Posture(x, y, z, rotX, rotY, rotZ, flipX, flipY, flipZ, width, height, length);
    }

    /**
     * Extend volume on X, Y, Z axis in both directions
     * @param x Extension on X axis
     * @param y Extension on Y axis
     * @param z Extension on Z axis
     * @return Extended volume
     */
    public Volume extend(int x, int y, int z) {
        return new Volume(width + 2 * x, height + 2 * y, length + 2 * z);
    }

}
