package com.ternsip.structpro.universe.utils;

import com.ternsip.structpro.universe.biomes.Biomus;
import com.ternsip.structpro.structure.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Object selection relative parameters
 * @author Ternsip
 */
@SuppressWarnings({"unused"})
public class Selector<T> {

    /** Used objects */
    private HashSet<T> used = new HashSet<>();

    /** All possible objects */
    private ArrayList<T> all = new ArrayList<>();

    /** All possible objects for each type of method */
    private HashMap<Method, ArrayList<T>> methodFilter = new HashMap<Method, ArrayList<T>>(){{
        for (Method method : Method.values()) {
            put(method, new ArrayList<>());
        }
    }};

    /** All possible objects for each type of biome */
    private HashMap<Biomus, ArrayList<T>> biomeFilter = new HashMap<Biomus, ArrayList<T>>(){{
        for (Biomus biomus : Biomus.values()) {
            put(biomus, new ArrayList<>());
        }
    }};

    /** Name (lowercase) -> object mapping */
    private HashMap<String, T> nameFilter = new HashMap<>();

    /**
     * Select all objects, O(1)
     * @return All objects
     */
    public ArrayList<T> select() {
        return all;
    }

    /**
     * Select all objects that matches biome, O(1)
     * @param biomus Target biome
     * @return Objects by biome
     */
    public ArrayList<T> select(Biomus biomus) {
        return biomeFilter.get(biomus);
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
        ArrayList<T> result = new ArrayList<>();
        for (String match : Utils.match(nameFilter.keySet(), pattern)) {
            result.add(nameFilter.get(match));
        }
        return result;
    }

    /**
     * Select structures that matches any method, O(n)
     * @param methods Array of methods to select
     * @return Objects that matches any method
     */
    public ArrayList<T> select(final Method[] methods) {
        ArrayList<T> result = new ArrayList<>();
        for (Method method : methods) {
            result.addAll(select(method));
        }
        return result;
    }

    /**
     * Select structures that matches any biome, O(n)
     * @param biomes Array of biomes to select
     * @return Objects that matches any biome
     */
    public ArrayList<T> select(final Biomus[] biomes) {
        ArrayList<T> result = new ArrayList<>();
        for (Biomus biomus : biomes) {
            result.addAll(select(biomus));
        }
        return result;
    }

    /**
     * Select structures that matches any biome or any method, O(n * log(n))
     * @param methods Array of methods to select
     * @param biomes Array of biomes to select
     * @return Objects that matches any biomus or any method
     */
    public ArrayList<T> select(final Biomus[] biomes, final Method[] methods) {
        HashSet<T> result = new HashSet<>();
        for (Biomus biomus : biomes) {
            result.addAll(select(biomus));
        }
        for (Method method : methods) {
            result.addAll(select(method));
        }
        return new ArrayList<>(result);
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
     * @param biomus Biomus signature
     * @param target Object instance
     */
    public void add(Biomus biomus, T target) {
        biomeFilter.get(biomus).add(target);
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
