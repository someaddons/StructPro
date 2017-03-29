package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Structure;
import com.ternsip.structpro.Structure.Structure.Biome;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameData;

import java.util.Map;

/* Mobs control class*/
@SuppressWarnings({"WeakerAccess", "deprecation"})
public class Mobs {

    /* Selector for all eggs mobs */
    public static final Selector<Class<? extends Entity>> mobsEggs = new Selector<Class<? extends Entity>>(){{
        for (Map.Entry<ResourceLocation, EntityList.EntityEggInfo> e : EntityList.ENTITY_EGGS.entrySet()) {
            Class<? extends Entity> mob = EntityList.getClass(e.getKey());
            add(classToName(mob).getResourcePath(), mob);
        }
    }};

    /* Selector for all eggs mobs */
    public static final Selector<Class<? extends Entity>> mobs = new Selector<Class<? extends Entity>>(){{
        for (EntityEntry e : GameData.getEntityRegistry()) {
            Class<? extends Entity> mob = e.getEntityClass();
            add(classToName(mob).getResourcePath(), mob);
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
        add(Structure.Method.UNDERGROUND, EntityCaveSpider.class);
        add(Structure.Method.UNDERGROUND, EntityBat.class);
        add(Structure.Method.AFLOAT, EntityWitch.class);
    }};

    /* Selector for village mobs */
    public static final Selector<Class<? extends Entity>> village = new Selector<Class<? extends Entity>>(){{
        add(EntityVillager.class);
    }};

    /* Transform entity class to name */
    public static ResourceLocation classToName(Class<? extends Entity> mob) {
        return EntityList.getKey(mob);
    }

    /* Construct entity by class in the world */
    public static Entity construct(World world, Class<? extends Entity> mob) {
        return EntityList.newEntity(mob, world);
    }

}
