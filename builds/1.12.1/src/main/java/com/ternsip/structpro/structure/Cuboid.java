package com.ternsip.structpro.structure;

@SuppressWarnings({"unused"})
public interface Cuboid {

    /**
     * Get index using relative position
     * @param x Internal X coordinate
     * @param y Internal Y coordinate
     * @param z Internal Z coordinate
     * @return Volume cell index
     */
    int getIndex(int x, int y, int z);

    /**
     * Get X position using index
     * @param index Volume cell index
     * @return Internal X coordinate
     */
    int getX(int index);

    /**
     * Get Y position using index
     * @param index Volume cell index
     * @return Internal Y coordinate
     */
    int getY(int index);

    /**
     * Get Z position using index
     * @param index Volume cell index
     * @return Internal Z coordinate
     */
    int getZ(int index);

    /** Get cuboid width */
    int getWidth();

    /** Get cuboid length */
    int getLength();

    /** Get cuboid height */
    int getHeight();

    /** Get cuboid inner volume - amount of cells inside */
    int getSize();

    /** Set cuboid width */
    void setWidth(int width);

    /** Set cuboid height */
    void setHeight(int height);

    /** Set cuboid length */
    void setLength(int length);

    /**
     * Check cell with coordinates x, y, z is inside
     * @param x Internal X coordinate
     * @param y Internal Y coordinate
     * @param z Internal Z coordinate
     * @return Is cell inside
     */
    boolean isInside(int x, int y, int z);

    /**
     * Check cell with index is inside
     * @return Is cell inside
     */
    boolean isInside(int index);

}
