package com.ternsip.structpro.Utils;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Utils {

    /* Join tokens into one string, separated with delimiter */
    public static String join(String[] args, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length - 1; ++i) {
            sb.append(args[i]).append(delimiter);
        }
        sb.append(args[args.length - 1]);
        return sb.toString();
    }

    /* Split string into tokens using delimiter */
    public static List<String> tokenize(String array, String delimiter) {
        final List<String> tokens = Arrays.asList(array.split("\\s*" + delimiter + "\\s*"));
        return new ArrayList<String>(){{
            for (String token : tokens) {
                if (token != null && !token.trim().isEmpty()) {
                    add(token.trim());
                }
            }
        }};
    }

    /* Convert short array to byte array */
    public static byte[] toByteArray(short[] arr) {
        byte[] byteBlocks = new byte[arr.length * 2];
        ByteBuffer.wrap(byteBlocks).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(arr);
        return byteBlocks;
    }

    /* Convert byte array to short array */
    public static short[] toShortArray(byte[] arr) {
        short[] shortBlocks = new short[arr.length / 2];
        ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortBlocks);
        return shortBlocks;
    }

    /* Convert BitSet to byte array */
    public static byte[] toByteArray(BitSet bits) {
        byte[] result = new byte[(bits.length() + 7) / 8];
        for (int i = 0; i < bits.length(); ++i) {
            if (bits.get(i)) {
                result[i / 8] |= 1 << (i % 8);
            }
        }
        return result;
    }

    /* Convert byte array to BitSet */
    public static BitSet toBitSet(byte[] bytes) {
        BitSet result = new BitSet(bytes.length * 8);
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[i / 8] & (1 << (i % 8))) > 0) {
                result.set(i);
            }
        }
        return result;
    }

    /* Convert HashSet to array */
    public static String[] toArray(HashSet<String> set) {
        return set.toArray(new String[set.size()]);
    }

    /* Convert collection to HashSet */
    public static <T> HashSet<T> toHashSet(final List<T> array) {
       return new HashSet<T>(){{addAll(array);}};
    }

    /* Select random element */
    public static <TYPE> TYPE select(ArrayList<TYPE> array, long seed) {
        return array.size() > 0 ? array.get(new Random(seed).nextInt(array.size())) : null;
    }

    /* Select random element */
    public static <TYPE> TYPE select(ArrayList<TYPE> array) {
        return select(array, System.currentTimeMillis());
    }

    /* Get all unique strings from iterable, that matches pattern name */
    public static HashSet<String> match(Iterable<String> iterable, Pattern pattern) {
        HashSet<String> result = new HashSet<String>();
        for (String name : iterable) {
            if (pattern.matcher(name).matches()) {
                result.add(name);
            }
        }
        return result;
    }

    /* Get all correct configuration fields */
    public static HashSet<String> getFields(final Class clazz) {
        return new HashSet<String>() {{
            for (Field f : clazz.getDeclaredFields()) {
                add(f.getName());
            }
        }};
    }

    /* Set class field value, returns success status */
    public static Object getFieldValue(Class clazz, String field) throws NoSuchFieldException, IllegalAccessException {
        HashMap<String, Object> result = new HashMap<String, Object>();
        return clazz.getDeclaredField(field).get(clazz);
    }

    /* Set class field value, returns success status */
    public static void setFieldValue(Class clazz, String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        HashMap<String, Object> result = new HashMap<String, Object>();
        clazz.getDeclaredField(field).set(clazz, value);
    }

    /* Write tags to file */
    public static void writeTags(File file, NBTTagCompound tag) throws IOException {
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new IOException("Can't create path: " + file.getParent());
            }
        }
        FileOutputStream fos = new FileOutputStream(file);
        try {
            CompressedStreamTools.writeCompressed(tag, fos);
        } finally {
            fos.close();
        }
    }

    /* Load map tag from file */
    public static NBTTagCompound readTags(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        try {
            return CompressedStreamTools.readCompressed(fis);
        } finally {
            fis.close();
        }
    }

}
