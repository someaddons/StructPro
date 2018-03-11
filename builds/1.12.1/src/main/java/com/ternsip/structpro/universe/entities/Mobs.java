package com.ternsip.structpro.universe.entities;

import com.ternsip.structpro.structure.Method;
import com.ternsip.structpro.universe.biomes.Biomus;
import com.ternsip.structpro.universe.utils.Selector;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.GameData;

import java.util.Map;

/**
 * Mobs control class
 *
 * @author Ternsip
 */
@SuppressWarnings({"WeakerAccess"})
public class Mobs {

    /**
     * Selector for all eggs mobs
     */
    public static final Selector<UEntityClass> eggs = new Selector<>();

    /**
     * Selector for all eggs mobs
     */
    public static final Selector<UEntityClass> mobs = new Selector<>();

    /**
     * Selector for hostile mobs
     */
    public static final Selector<UEntityClass> hostile = new Selector<>();

    /**
     * Selector for village mobs
     */
    public static final Selector<UEntityClass> village = new Selector<>();

    static {

        /* Fill mob eggs */
        for (Map.Entry<ResourceLocation, EntityList.EntityEggInfo> e : EntityList.ENTITY_EGGS.entrySet()) {
            UEntityClass mob = new UEntityClass(EntityList.getClass(e.getKey()));
            eggs.add(mob.getPath(), mob);
        }

        /* Fill mobs */
        for (EntityEntry e : GameData.getEntityRegistry()) {
            UEntityClass mob = new UEntityClass(e.getEntityClass());
            mobs.add(mob.getPath(), mob);
        }

        /* Fill hostile mobs */
        for (Biomus biomus : Biomus.values()) {
            hostile.add(biomus, new UEntityClass(EntitySkeleton.class));
            hostile.add(biomus, new UEntityClass(EntityZombie.class));
            hostile.add(biomus, new UEntityClass(EntitySpider.class));
            hostile.add(biomus, new UEntityClass(EntityCreeper.class));
        }
        hostile.add(Biomus.NETHER, new UEntityClass(EntityGhast.class));
        hostile.add(Biomus.NETHER, new UEntityClass(EntityPigZombie.class));
        hostile.add(Biomus.NETHER, new UEntityClass(EntityWitherSkull.class));
        hostile.add(Biomus.NETHER, new UEntityClass(EntityBlaze.class));
        hostile.add(Biomus.SNOW, new UEntityClass(EntityEnderman.class));
        hostile.add(Biomus.SNOW, new UEntityClass(EntitySnowman.class));
        hostile.add(Biomus.END, new UEntityClass(EntityEnderman.class));
        hostile.add(Method.UNDERGROUND, new UEntityClass(EntityCaveSpider.class));
        hostile.add(Method.UNDERGROUND, new UEntityClass(EntityBat.class));
        hostile.add(Method.AFLOAT, new UEntityClass(EntityWitch.class));

        /* Fill village mobs */
        village.add(new UEntityClass(EntityVillager.class));

    }

}
