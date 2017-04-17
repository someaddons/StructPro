package com.ternsip.structpro.Structure;

/* 3D volume structure */
public class Volume {

    int width;
    int height;
    int length;

    Volume() {}

    public Volume(int width, int height, int length) {
        this.width = width;
        this.height = height;
        this.length = length;
    }

    /* Returns posture over the volume */
    public Posture getPosture(int x, int y, int z, int rotX, int rotY, int rotZ, boolean flipX, boolean flipY, boolean flipZ) {
        return new Posture(x, y, z, rotX, rotY, rotZ, flipX, flipY, flipZ, width, height, length);
    }

    /* Get index using relative position */
    public int getIndex(int x, int y, int z) {
        return x + y * width * length + z * width;
    }

    /* Get X-relative position using index */
    public int getX(int index) {
        return index % width;
    }

    /* Get Y-relative position using index */
    public int getY(int index) {
        return index / (width * length);
    }

    /* Get Z-relative position using index */
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

    int getVolume() {
        return width * height * length;
    }

    public boolean isInside(int x, int y, int z) {
        return  x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < length;
    }

}
