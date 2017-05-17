package com.ternsip.structpro.Utils;

import com.ternsip.structpro.Structure.Biome;
import com.ternsip.structpro.Structure.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Object selection relative parameters
 * @author Ternsip
 * @since JDK 1.6
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Selector<T> {

    /** Used objects */
    private HashSet<T> used = new HashSet<T>();

    /** All possible objects */
    private ArrayList<T> all = new ArrayList<T>();

    /** All possible objects for each type of method */
    private HashMap<Method, ArrayList<T>> methodFilter = new HashMap<Method, ArrayList<T>>(){{
        for (Method method : Method.values()) {
            put(method, new ArrayList<T>());
        }
    }};

    /** All possible objects for each type of biome */
    private HashMap<Biome, ArrayList<T>> biomeFilter = new HashMap<Biome, ArrayList<T>>(){{
        for (Biome biome : Biome.values()) {
            put(biome, new ArrayList<T>());
        }
    }};

    /** Name (lowercase) -> object mapping */
    private HashMap<String, T> nameFilter = new HashMap<String, T>();

    /**
     * Select all objects, O(1)
     * @return All objects
     */
    public ArrayList<T> select() {
        return all;
    }

    /**
     * Select all objects that matches biome, O(1)
     * @param biome Target biome
     * @return Objects by biome
     */
    public ArrayList<T> select(Biome biome) {
        return biomeFilter.get(biome);
    }

    /**
     * Select all objects that matches method, O(1)
     * @param method Target method
     * @return Objects by method
     */
    public ArrayList<T> select(Method method) {
        return methodFilter.get(method);
    }

    /**
     * Select all objects that matches pattern, O(n)
     * @param pattern Target pattern
     * @return Array of objects which name matches pattern
     */
    public ArrayList<T> select(final Pattern pattern) {
        return new ArrayList<T>() {{
            for (String match : Utils.match(nameFilter.keySet(), pattern)) {
                add(nameFilter.get(match));
            }
        }};
    }

    /**
     * Select structures that matches any method, O(n)
     * @param methods Array of methods to select
     * @return Objects that matches any method
     */
    public ArrayList<T> select(final Method[] methods) {
        return new ArrayList<T>(){{
            for (Method method : methods) {
                addAll(select(method));
            }
        }};
    }

    /**
     * Select structures that matches any biome, O(n)
     * @param biomes Array of biomes to select
     * @return Objects that matches any biome
     */
    public ArrayList<T> select(final Biome[] biomes) {
        return new ArrayList<T>(){{
            for (Biome biome : biomes) {
                addAll(select(biome));
            }
        }};
    }

    /**
     * Select structures that matches any biome or any method, O(n * log(n))
     * @param methods Array of methods to select
     * @param biomes Array of biomes to select
     * @return Objects that matches any biome or any method
     */
    public ArrayList<T> select(final Biome[] biomes, final Method[] methods) {
        return new ArrayList<T>(new HashSet<T>(){{
            for (Biome biome : biomes) {
                addAll(select(biome));
            }
            for (Method method : methods) {
                addAll(select(method));
            }
        }});
    }

    /**
     * Add object without bias just by reference
     * @param target Object instance
     */
    public void add(T target) {
        if (!used.contains(target)) {
            used.add(target);
            all.add(target);
        }
    }

    /**
     * Add targets for specific method
     * @param method Method signature
     * @param target Object instance
     */
    public void add(Method method, T target) {
        methodFilter.get(method).add(target);
        add(target);
    }

    /**
     * Add targets for specific biome
     * @param biome Biome signature
     * @param target Object instance
     */
    public void add(Biome biome, T target) {
        biomeFilter.get(biome).add(target);
        add(target);
    }

    /**
     * Add targets for specific biome
     * @param name Name signature
     * @param target Object instance
     */
    public void add(String name, T target) {
        nameFilter.put(name, target);
        add(target);
    }

}
