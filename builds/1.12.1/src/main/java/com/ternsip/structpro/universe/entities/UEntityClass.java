package com.ternsip.structpro.universe.entities;

import com.ternsip.structpro.universe.world.UWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;

/**
 * Tile entity class wrapper
 *
 * @author Ternsip
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class UEntityClass {

    /**
     * Native minecraft entity class
     */
    private Class<? extends Entity> entityClass;

    /**
     * Construct entity class from native minecraft entity class
     */
    public UEntityClass(Class<? extends Entity> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Transform entity class to path
     *
     * @return Name
     */
    public String getPath() {
        ResourceLocation res = EntityList.getKey(entityClass);
        return res == null ? "" : res.getResourcePath();
    }

    /**
     * Transform entity class to name
     *
     * @return Name
     */
    public ResourceLocation getName() {
        return EntityList.getKey(entityClass);
    }

    /**
     * Construct entity by class in the world
     *
     * @param world Target world
     * @return Spawned entity
     */
    public Entity construct(UWorld world) {
        return EntityList.newEntity(entityClass, world.getWorld());
    }

    /**
     * Return minecraft native entity class
     */
    public Class<? extends Entity> getEntityClass() {
        return entityClass;
    }

}
