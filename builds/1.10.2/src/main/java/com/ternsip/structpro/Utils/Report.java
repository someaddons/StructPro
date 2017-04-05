package com.ternsip.structpro.Utils;

import com.ternsip.structpro.Structpro;

/* Console message reporter [key = value] */
public class Report {

    private String result = "";

    /* Add new word mapping */
    public Report post(String key, String value) {
        result = result + combine(key, value);
        return this;
    }

    /* Add new word mapping */
    public Report pref(String key, String value) {
        result = combine(key, value) + result;
        return this;
    }

    /* Add new word mapping */
    public Report post(Report report) {
        result = result + report.getResult();
        return this;
    }

    /* Add new word mapping */
    public Report pref(Report report) {
        result = report.getResult() + result;
        return this;
    }

    private String getResult() {
        return result;
    }

    /* Combine into one */
    private String combine(String key, String value) {
        String k = key == null ? "NULL" : key.replace("\n", "");
        String v = value == null ? "NULL" : value.replace("\n", "");
        return  "[" + k + " = " + v + "]";
    }

    public void print() {
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "[" + Structpro.MODNAME + " v" + Structpro.VERSION + "]" + result;
    }
}
