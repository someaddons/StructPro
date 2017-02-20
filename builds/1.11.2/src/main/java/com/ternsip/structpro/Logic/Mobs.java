package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Structure.Biome;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "deprecation"})
public class Mobs {

    /* All Mobs that have eggs for each biome */
    @SuppressWarnings({"unchecked"})
    private static final HashMap<Biome, ArrayList<Class<? extends Entity>>> mobsBiome = new HashMap<Biome, ArrayList<Class<? extends Entity>>>(){{
        for (Biome biome : Biome.values()) {
            put(biome, new ArrayList<Class<? extends Entity>>());
            Collections.addAll(get(biome), EntitySkeleton.class, EntityZombie.class, EntitySpider.class, EntityCreeper.class);
        }
        Collections.addAll(get(Biome.NETHER), EntityGhast.class, EntityPigZombie.class, EntityWitherSkull.class, EntityBlaze.class);
        Collections.addAll(get(Biome.SNOW),  EntityPolarBear.class, EntitySnowman.class);
        Collections.addAll(get(Biome.END), EntityEnderman.class);
    }};

    /* Village mobs */
    private static final ArrayList<Class<? extends Entity>> mobsVillage = new ArrayList<Class<? extends Entity>>() {{
        add(EntityVillager.class);
    }};

    /* All mobs that have eggs */
    private static final ArrayList<Class<? extends Entity>> mobs = new ArrayList<Class<? extends Entity>>() {{
        for (Map.Entry<ResourceLocation, EntityList.EntityEggInfo> e : EntityList.ENTITY_EGGS.entrySet()) {
            add(EntityList.getClass(e.getKey()));
        }
    }};

    public static ArrayList<Class<? extends Entity>> select(Biome biome) {
        return mobsBiome.get(biome);
    }

    public static ArrayList<Class<? extends Entity>> selectVillage() {
        return mobsVillage;
    }

    public static Class<? extends Entity> selectByName(String name) {
        for (Class<? extends Entity> mob : mobs) {
            ResourceLocation mobName = classToName(mob);
            if (mobName != null && (mobName.toString().equalsIgnoreCase(name) || mobName.getResourcePath().equalsIgnoreCase(name))) {
                return mob;
            }
        }
        for (Class<? extends Entity> mob : mobs) {
            ResourceLocation mobName = classToName(mob);
            if (mobName != null && (mobName.toString().toLowerCase().contains(name.toLowerCase()) || name.toLowerCase().contains(mobName.toString().toLowerCase()))) {
                return mob;
            }
        }
        return null;
    }

    public static ResourceLocation classToName(Class<? extends Entity> mob) {
        return EntityList.getKey(mob);
    }

    public static Entity construct(World world, Class<? extends Entity> mob) {
        return EntityList.newEntity(mob, world);
    }

}
