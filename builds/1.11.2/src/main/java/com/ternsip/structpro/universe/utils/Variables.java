package com.ternsip.structpro.universe.utils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Variables holds mapping variable name -> value
 * Providing easy access to variables
 * @author Ternsip
 */
public class Variables {

    /** Variables mapping */
    private final HashMap<String, String> values = new HashMap<>();

    /**
     * Construct variables extracted from string
     * @param string Origin string for parsing
     */
    public Variables(String string) {
        Pattern varPattern = Pattern.compile("[\\S]+[\\s]*=[\\s]*[\\S]+");
        Matcher m = varPattern.matcher(string);
        while (m.find()) {
            String[] tokens = m.group().split("[ =]+");
            values.put(tokens[0].toLowerCase(), tokens[1]);
        }
        for (String token : string.replaceAll("[\\S]+[\\s]*=[\\s]*[\\S]+", "").split(" ")) {
            if (token.length() > 0 && !values.containsKey(token)) {
                values.put(token, "true");
            }
        }
    }

    /**
     * Get Integer variable if it exists, default value otherwise
     * @param keys Array of keys for matching
     * @param defaultValue Value as default
     * @return Variable value or default if not exists
     */
    public int get(String[] keys, int defaultValue) {
        try {
            return get(keys) != null ? Integer.valueOf(get(keys)) : defaultValue;
        } catch (Throwable throwable) {
            return defaultValue;
        }
    }

    /**
     * Get Boolean variable if it exists, default value otherwise
     * @param keys Array of keys for matching
     * @param defaultValue Value as default
     * @return Variable value or default if not exists
     */
    public boolean get(String[] keys, boolean defaultValue) {
        return get(keys) != null ? Boolean.valueOf(get(keys)) : defaultValue;
    }

    /**
     * Get String variable if it exists, default value otherwise
     * @param keys Array of keys for matching
     * @param defaultValue Value as default
     * @return Variable value or default if not exists
     */
    public String get(String[] keys, String defaultValue) {
        return get(keys) != null ? get(keys) : defaultValue;
    }

    /**
     * Get String variable if it exists, null otherwise
     * @param keys Array of keys for matching
     * @return Variable value or null if not exists
     */
    public String get(String[] keys) {
        for (String key : keys) {
            if (values.containsKey(key)) {
                return values.get(key);
            }
        }
        return null;
    }

    /** Manually add key variable with value
     * @param key Variable name
     * @param value Variable value
     */
    public void put(String key, String value) {
        values.put(key, value);
    }

}
