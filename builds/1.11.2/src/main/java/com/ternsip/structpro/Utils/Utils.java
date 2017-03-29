package com.ternsip.structpro.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Utils {

    public static String join(String[] args, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length - 1; ++i) {
            sb.append(args[i]).append(delimiter);
        }
        sb.append(args[args.length - 1]);
        return sb.toString();
    }

    public static <K> int parseOrDefault(Map<K, String> map, K key, int defaultValue) {
        try {
            return map.containsKey(key) ? Integer.valueOf(map.get(key)) : defaultValue;
        } catch (Throwable throwable) {
            return defaultValue;
        }
    }

    public static <K> boolean parseOrDefault(Map<K, String> map, K key, boolean defaultValue) {
        return map.containsKey(key) ? Boolean.valueOf(map.get(key)) : defaultValue;
    }

    public static <K> String parseOrDefault(Map<K, String> map, K key, String defaultValue) {
        return map.containsKey(key) ? map.get(key) : defaultValue;
    }

    public static String[] toArray(HashSet<String> set) {
        return set.toArray(new String[set.size()]);
    }

    public static HashMap<String, String> extractVariables(String string) {
        HashMap<String, String> result = new HashMap<String, String>();
        Pattern varPattern = Pattern.compile("[\\S]+[\\s]*=[\\s]*[\\S]+");
        Matcher m = varPattern.matcher(string);
        while (m.find()) {
            String[] tokens = m.group().split("[ =]+");
            result.put(tokens[0].toLowerCase(), tokens[1]);
        }
        return result;
    }

    public static byte[] toByteArray(short[] arr) {
        byte[] byteBlocks = new byte[arr.length * 2];
        ByteBuffer.wrap(byteBlocks).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(arr);
        return byteBlocks;
    }

    public static short[] toShortArray(byte[] arr) {
        short[] shortBlocks = new short[arr.length / 2];
        ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortBlocks);
        return shortBlocks;
    }

    public static byte[] toByteArray(BitSet bits) {
        byte[] result = new byte[(bits.length() + 7) / 8];
        for (int i = 0; i < bits.length(); ++i) {
            if (bits.get(i)) {
                result[i / 8] |= 1 << (i % 8);
            }
        }
        return result;
    }

    public static BitSet toBitSet(byte[] bytes) {
        BitSet result = new BitSet(bytes.length * 8);
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[i / 8] & (1 << (i % 8))) > 0) {
                result.set(i);
            }
        }
        return result;
    }

    public static HashSet<String> tokenize(String array, String delimiter) {
        HashSet<String> result = new HashSet<String>();
        result.addAll(Arrays.asList(array.split("\\s*" + delimiter + "\\s*")));
        return result;
    }

    /* Select random element */
    public static <TYPE> TYPE select(ArrayList<TYPE> array, long seed) {
        return array.size() > 0 ? array.get(new Random(seed).nextInt(array.size())) : null;
    }

    /* Select random element */
    public static <TYPE> TYPE select(ArrayList<TYPE> array) {
        return select(array, System.currentTimeMillis());
    }

}
