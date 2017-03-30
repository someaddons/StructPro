package com.ternsip.structpro.Structure;

import java.io.File;

/*
* Spawn methods
* Basic - spawn on ground and pass water
* Underground - spawn under soil stratum
* Underwater - spawn under water on bottom
* Sky - spawn floating in the sky
* Hill - spawn on ground without roughness bias
*/
public enum Method {
    BASIC (0x00, "BASIC"),
    UNDERGROUND (0x01, "UNDERGROUND"),
    UNDERWATER(0x02, "UNDERWATER"),
    AFLOAT (0x03, "AFLOAT"),
    SKY (0x04, "SKY"),
    HILL (0x05, "HILL");

    public final int value;
    public final String name;

    Method(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /* Get method by it internal value */
    public static Method valueOf(int value) {
        for (Method sample : Method.values()) {
            if (sample.value == value) {
                return sample;
            }
        }
        return BASIC;
    }

    /* Get method by file name */
    public static Method valueOf(File file) {
        String path = file.getPath().toLowerCase().replace("\\", "/").replace("//", "/");
        if (path.contains("/underground/"))     return Method.UNDERGROUND;
        if (path.contains("/sky/"))             return Method.SKY;
        if (path.contains("/floating/"))        return Method.SKY;
        if (path.contains("/afloat/"))          return Method.AFLOAT;
        if (path.contains("/water/"))           return Method.AFLOAT;
        if (path.contains("/underwater/"))      return Method.UNDERWATER;
        if (path.contains("/hill/"))            return Method.HILL;
        if (path.contains("/mountain/"))        return Method.HILL;
        return Method.BASIC;
    }

}