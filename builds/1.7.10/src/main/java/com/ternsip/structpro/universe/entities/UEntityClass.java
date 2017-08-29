package com.ternsip.structpro.universe.entities;

import com.ternsip.structpro.universe.world.UWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

/**
 * Tile entity class wrapper
 * @author  Ternsip
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class UEntityClass {

    /** Native minecraft entity class */
    private Class<? extends Entity> entityClass;

    /** Construct entity class from native minecraft entity class */
    public UEntityClass(Class<? extends Entity> entityClass) {
        this.entityClass = entityClass;
    }

    /** Construct entity class from index */
    public UEntityClass(Integer index) {
        this.entityClass = EntityList.getClassFromID(index);
    }

    /**
     * Transform entity class to path
     * @return Name
     */
    public String getPath() {
        return getName();
    }

    /**
     * Transform entity class to name
     * @return Name
     */
    public String getName() {
        return (String) EntityList.classToStringMapping.get(entityClass);
    }

    /**
     * Construct entity by class in the world
     * @param world Target world
     * @return Spawned entity
     * */
    public Entity construct(UWorld world) {
        return EntityList.createEntityByName(getName(), world.getWorld());
    }

    /** Return minecraft native entity class */
    public Class<? extends Entity> getEntityClass() {
        return entityClass;
    }

}
