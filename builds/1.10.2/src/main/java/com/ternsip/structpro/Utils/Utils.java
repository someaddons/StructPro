package com.ternsip.structpro.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String join(String[] args, String delimeter) {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(delimeter);
        }
        return sb.toString();
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

    public static byte[] shortArrToByteArr(short[] arr) {
        byte[] byteBlocks = new byte[arr.length * 2];
        ByteBuffer.wrap(byteBlocks).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(arr);
        return byteBlocks;
    }

    public static short[] byteArrToShortArr(byte[] arr) {
        short[] shortBlocks = new short[arr.length / 2];
        ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortBlocks);
        return shortBlocks;
    }

    public static ArrayList<Integer> stringToArray(String array) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(String stringValue : Arrays.asList(array.split("\\s*,\\s*"))) {
            try {result.add(Integer.parseInt(stringValue));} catch(NumberFormatException ignored) {}
        }
        return result;
    }

    public static String arrayToString(ArrayList<Integer> array) {
        String result = "";
        for(Integer integerValue : array) {
            result += (result.isEmpty() ? "": ", ") + integerValue.toString();
        }
        return result;
    }

    /* Select random element */
    public static <TYPE> TYPE select(ArrayList<TYPE> array, long seed) {
        return array.size() > 0 ? array.get(new Random(seed).nextInt(array.size())) : null;
    }

}
