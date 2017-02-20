package com.ternsip.structpro.WorldCache;

import com.ternsip.structpro.Structure.Structure;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Mobs {

    /* All Mobs that have eggs for each biome */
    private static final HashMap<Structure.Biome, ArrayList<Class<? extends Entity>>> mobsBiome = new HashMap<Structure.Biome, ArrayList<Class<? extends Entity>>>(){{
        for (Structure.Biome biome : Structure.Biome.values()) {
            put(biome, new ArrayList<Class<? extends Entity>>());
            Collections.addAll(get(biome), EntitySkeleton.class, EntityZombie.class, EntitySpider.class, EntityCreeper.class);
        }
        Collections.addAll(get(Structure.Biome.NETHER), EntityGhast.class, EntityPigZombie.class, EntityWitherSkull.class, EntityBlaze.class);
        Collections.addAll(get(Structure.Biome.SNOW),  EntityPolarBear.class, EntitySnowman.class);
        Collections.addAll(get(Structure.Biome.END), EntityEnderman.class);
    }};

    /* Village mobs */
    private static final ArrayList<Class<? extends Entity>> mobsVillage = new ArrayList<Class<? extends Entity>>() {{
        add(EntityVillager.class);
    }};

    /* All mobs that have eggs */
    private static final ArrayList<Class<? extends Entity>> mobs = new ArrayList<Class<? extends Entity>>() {{
        for (Map.Entry<String, EntityList.EntityEggInfo> e : EntityList.ENTITY_EGGS.entrySet()) {
            add(EntityList.getClassFromID(EntityList.getIDFromString(e.getKey())));
        }
    }};

    public static ArrayList<Class<? extends Entity>> select(Structure.Biome biome) {
        return mobsBiome.get(biome);
    }

    public static ArrayList<Class<? extends Entity>> selectVillage() {
        return mobsVillage;
    }

    public static Class<? extends Entity> selectByName(String name) {
        for (Class<? extends Entity> mob : mobs) {
            String mobName = classToName(mob);
            if (mobName.equalsIgnoreCase(name)) {
                return mob;
            }
        }
        for (Class<? extends Entity> mob : mobs) {
            String mobName = classToName(mob);
            if (mobName.toLowerCase().contains(name.toLowerCase()) || name.toLowerCase().contains(mobName.toLowerCase())) {
                return mob;
            }
        }
        return null;
    }

    public static String classToName(Class<? extends Entity> mob) {
        return EntityList.getEntityStringFromClass(mob);
    }

    public static Entity construct(World world, Class<? extends Entity> mob) {
        String mobName = EntityList.getEntityStringFromClass(mob);
        return EntityList.createEntityByName(mobName, world);
    }

}
