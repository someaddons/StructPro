package com.ternsip.structpro.Utils;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Utilities providing small abstract and independent universal methods
 * @author Ternsip
 * @since JDK 1.6
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Utils {

    /**
     * Join tokens into one string, separated with delimiter
     * @param args String tokens to join
     * @param delimiter Separator
     * @return Resulting string
     */
    public static String join(String[] args, String delimiter) {
        if (args.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length - 1; ++i) {
            sb.append(args[i]).append(delimiter);
        }
        sb.append(args[args.length - 1]);
        return sb.toString();
    }

    /**
     * Split string into tokens using delimiter
     * @param array Monotone string
     * @param delimiter String separator
     * @return Array of tokens
     */
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

    /**
     * Convert short array to byte array
     * @param arr Array of shorts
     * @return Array of bytes
     */
    public static byte[] toByteArray(short[] arr) {
        byte[] byteBlocks = new byte[arr.length * 2];
        ByteBuffer.wrap(byteBlocks).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(arr);
        return byteBlocks;
    }

    /**
     * Convert byte array to short array
     * @param arr Array of bytes
     * @return Array of shorts
     */
    public static short[] toShortArray(byte[] arr) {
        short[] shortBlocks = new short[arr.length / 2];
        ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortBlocks);
        return shortBlocks;
    }

    /**
     * Convert BitSet to byte array
     * @param bits Array of bits
     * @return Array of bytes
     */
    public static byte[] toByteArray(BitSet bits) {
        byte[] result = new byte[(bits.length() + 7) / 8];
        for (int i = 0; i < bits.length(); ++i) {
            if (bits.get(i)) {
                result[i / 8] |= 1 << (i % 8);
            }
        }
        return result;
    }

    /**
     * Convert byte array to BitSet
     * @param bytes Array of bytes
     * @return Array of bits
     */
    public static BitSet toBitSet(byte[] bytes) {
        BitSet result = new BitSet(bytes.length * 8);
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[i / 8] & (1 << (i % 8))) > 0) {
                result.set(i);
            }
        }
        return result;
    }

    /**
     * Convert HashSet to array
     * @param set Set of strings
     * @return Array of unique strings
     */
    public static String[] toArray(HashSet<String> set) {
        return set.toArray(new String[set.size()]);
    }

    /**
     * Convert collection to HashSet
     * @param array Array of objects
     * @return Set of objects
     */
    public static <T> HashSet<T> toHashSet(final List<T> array) {
       return new HashSet<T>(array);
    }

    /**
     * Select random element
     * @param array Array of objects
     * @param seed Selector seed
     * @return Selected object or null
     */
    public static <TYPE> TYPE select(ArrayList<TYPE> array, long seed) {
        return array.size() > 0 ? array.get(new Random(seed).nextInt(array.size())) : null;
    }

    /**
     * Select random element
     * @param array Array of objects
     * @return Selected object or null
     */
    public static <TYPE> TYPE select(ArrayList<TYPE> array) {
        return select(array, System.currentTimeMillis());
    }

    /**
     * Get all unique strings from iterable, that matches pattern name
     * @param iterable Collection to iterate
     * @param pattern Pattern
     * @return Set of strings
     */
    public static HashSet<String> match(Iterable<String> iterable, Pattern pattern) {
        HashSet<String> result = new HashSet<String>();
        for (String name : iterable) {
            if (pattern.matcher(name).matches()) {
                result.add(name);
            }
        }
        return result;
    }

    /**
     * Get all correct configuration fields
     * @param clazz Class to scan
     * @return Set of strings
     */
    public static HashSet<String> getFields(final Class clazz) {
        return new HashSet<String>() {{
            for (Field f : clazz.getDeclaredFields()) {
                add(f.getName());
            }
        }};
    }

    /**
     * Set class field value, returns success status
     * @param clazz Class to scan
     * @param target Instance
     * @param fieldName Name of field
     * @return Field value
     */
    public static Object getFieldValue(Class clazz, Object target, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        HashMap<String, Object> result = new HashMap<String, Object>();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        return field.get(target);
    }

    /**
     * Set class field value, returns success status
     * @param clazz Class to scan
     * @param target Instance
     * @param fieldName Name of field
     */
    public static void setFieldValue(Class clazz, Object target, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        HashMap<String, Object> result = new HashMap<String, Object>();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(target, value);
    }

    /**
     * Write tags to file
     * @param file File to write
     * @param tag NBT tag to write
     * @throws IOException If writing failed
     */
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

    /**
     * Load map tag from file
     * @param file File to read
     * @return NBT tag from file
     * @throws IOException If reading failed
     */
    public static NBTTagCompound readTags(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        try {
            return CompressedStreamTools.readCompressed(fis);
        } finally {
            fis.close();
        }
    }

    /**
     * Get complete list of files in folder
     * @param file File or Folder to scan
     * @return Array of files
     */
    public static File[] getFileList(File file) {
        if (file.isFile() && !file.isDirectory()) {
            return new File[]{file};
        }
        return file.listFiles() != null ? file.listFiles() : new File[0];
    }

}
