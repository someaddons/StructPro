package com.ternsip.structpro.structure;

import java.io.File;

/**
 * Spawning methods
 * Have to cover all known structure spawn methods
 * Basic - calibrate on ground and pass water
 * Underground - calibrate under soil stratum
 * Underwater - calibrate under water on bottom
 * Sky - calibrate floating in the sky
 * Hill - calibrate on ground without roughness bias
 *
 * @author Ternsip
 */
public enum Method {

    BASIC(0x00, "BASIC"),
    UNDERGROUND(0x01, "UNDERGROUND"),
    UNDERWATER(0x02, "UNDERWATER"),
    AFLOAT(0x03, "AFLOAT"),
    SKY(0x04, "SKY"),
    HILL(0x05, "HILL");

    private final int value;
    private final String name;

    /**
     * Default method constructor
     *
     * @param value Method index
     * @param name  Method name
     */
    Method(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * Get method by it internal value
     *
     * @param value Method index
     * @return The method
     */
    public static Method valueOf(int value) {
        for (Method sample : Method.values()) {
            if (sample.value == value) {
                return sample;
            }
        }
        return BASIC;
    }

    /**
     * Get method by file name, works only with path
     *
     * @param file Target file name
     * @return The method
     */
    public static Method valueOf(File file) {
        String path = file.getPath().toLowerCase().replace("\\", "/").replace("//", "/");
        if (path.contains("/underground/")) return Method.UNDERGROUND;
        if (path.contains("/sky/")) return Method.SKY;
        if (path.contains("/floating/")) return Method.SKY;
        if (path.contains("/afloat/")) return Method.AFLOAT;
        if (path.contains("/water/")) return Method.AFLOAT;
        if (path.contains("/underwater/")) return Method.UNDERWATER;
        if (path.contains("/hill/")) return Method.HILL;
        if (path.contains("/mountain/")) return Method.HILL;
        return Method.BASIC;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

}