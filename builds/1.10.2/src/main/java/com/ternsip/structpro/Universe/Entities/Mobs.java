package com.ternsip.structpro.Universe.Entities;

import com.ternsip.structpro.Utils.Selector;
import com.ternsip.structpro.Structure.Biome;
import com.ternsip.structpro.Structure.Method;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameData;

import java.util.Map;

/* Mobs control class*/
@SuppressWarnings({"WeakerAccess", "deprecation"})
public class Mobs {

    /* Selector for all eggs mobs */
    public static final Selector<Class<? extends Entity>> eggs = new Selector<Class<? extends Entity>>(){{
        for (Map.Entry<String, EntityList.EntityEggInfo> e : EntityList.ENTITY_EGGS.entrySet()) {
            Class<? extends Entity> mob = EntityList.getClassFromID(EntityList.getIDFromString(e.getValue().spawnedID));
            add(classToName(mob), mob);
        }
    }};

    /* Selector for all eggs mobs */
    public static final Selector<Class<? extends Entity>> mobs = new Selector<Class<? extends Entity>>(){{
        for (Map.Entry<String, Class<? extends Entity>> e : EntityList.NAME_TO_CLASS.entrySet()) {
            Class<? extends Entity> mob = e.getValue();
            add(classToName(mob), mob);
        }
    }};

    /* Selector for hostile mobs */
    public static final Selector<Class<? extends Entity>> hostile = new Selector<Class<? extends Entity>>(){{
        for (Biome biome : Biome.values()) {
            add(biome, EntitySkeleton.class);
            add(biome, EntityZombie.class);
            add(biome, EntitySpider.class);
            add(biome, EntityCreeper.class);
        }
        add(Biome.NETHER, EntityGhast.class);
        add(Biome.NETHER, EntityPigZombie.class);
        add(Biome.NETHER, EntityWitherSkull.class);
        add(Biome.NETHER, EntityBlaze.class);
        add(Biome.SNOW, EntityEnderman.class);
        add(Biome.SNOW, EntitySnowman.class);
        add(Biome.END, EntityEnderman.class);
        add(Method.UNDERGROUND, EntityCaveSpider.class);
        add(Method.UNDERGROUND, EntityBat.class);
        add(Method.AFLOAT, EntityWitch.class);
    }};

    /* Selector for village mobs */
    public static final Selector<Class<? extends Entity>> village = new Selector<Class<? extends Entity>>(){{
        add(EntityVillager.class);
    }};

    /* Transform entity class to name */
    public static String classToName(Class<? extends Entity> mob) {
        return EntityList.getEntityStringFromClass(mob);
    }

    /* Construct entity by class in the world */
    public static Entity construct(World world, Class<? extends Entity> mob) {
        return EntityList.createEntityByName(classToName(mob), world);
    }

}
