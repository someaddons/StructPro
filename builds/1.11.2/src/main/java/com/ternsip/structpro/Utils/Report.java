package com.ternsip.structpro.Utils;

import com.ternsip.structpro.Structpro;

/* Console message reporter [key = value] */
public class Report {

    private boolean success = true;
    private String result = "[" + Structpro.MODNAME + " v" + Structpro.VERSION + "]";

    /* Add new word mapping */
    public Report add(String key, String value) {
        String k = key == null ? "NULL" : key.replace("\n", "");
        String v = value == null ? "NULL" : value.replace("\n", "");
        result += "[" + k + " = " + v + "]";
        return this;
    }

    /* Change success status */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void print() {
        System.out.println(result);
    }

    @Override
    public String toString() {
        return result;
    }
}
