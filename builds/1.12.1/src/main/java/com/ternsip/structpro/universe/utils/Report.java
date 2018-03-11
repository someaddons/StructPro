package com.ternsip.structpro.universe.utils;

import com.ternsip.structpro.Structpro;

/**
 * Console message reporter [key = value]
 *
 * @author Ternsip
 */
public class Report {

    /**
     * Report resulting storage
     */
    private String result = "";

    /**
     * Add new word mapping
     *
     * @param key   Posting key
     * @param value Posting value
     * @return Self instance
     */
    public Report post(String key, String value) {
        result = result + combine(key, value);
        return this;
    }

    /**
     * Add new word mapping
     *
     * @param key   Prefixing key
     * @param value Prefixing value
     * @return Self instance
     */
    public Report pref(String key, String value) {
        result = combine(key, value) + result;
        return this;
    }

    /**
     * Post another report to the end
     *
     * @param report Report to post
     * @return Self instance
     */
    public Report post(Report report) {
        result = result + report.result;
        return this;
    }

    /**
     * Prefix another report to start
     *
     * @param report Report to prefix
     * @return Self instance
     */
    public Report pref(Report report) {
        result = report.result + result;
        return this;
    }

    /**
     * Combine into one
     *
     * @param key   Combining key
     * @param value Combining value
     * @return Combined key-value
     */
    private String combine(String key, String value) {
        String k = key == null ? "NULL" : key.replace("\n", "");
        String v = value == null ? "NULL" : value.replace("\n", "");
        return "[" + k + " = " + v + "]";
    }

    /**
     * Print report to console
     */
    public void print() {
        System.out.println(toString());
    }

    /**
     * Convert report to string with mod sign
     *
     * @return Converted result
     */
    @Override
    public String toString() {
        return "[" + Structpro.MODNAME + " v" + Structpro.VERSION + "]" + result;
    }
}
