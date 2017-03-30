package com.ternsip.structpro.World.Cache;

import net.minecraft.world.World;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/* Chunks memoization */
class ChunkCache {

    private static final long CACHE_TIME_LIMIT = 32 * 1000;

    /* Actual world */
    private World world;

    /* Chunkster cache */
    private ConcurrentHashMap<Long, Chunkster> chunksters = new ConcurrentHashMap<Long, Chunkster>();

    ChunkCache(World world) {
        this.world = world;
    }

    /* Get appropriate chunkster by coordinates */
    Chunkster getChunkster(int chunkX, int chunkZ) {
        long key = getKey(chunkX, chunkZ);
        if (!chunksters.containsKey(key)) {
            chunksters.put(key, new Chunkster(world, chunkX, chunkZ));
        }
        chunksters.get(key).getTimer().drop();
        return chunksters.get(key);
    }

    /* Unload obsoleted chunks */
    void unload() {
        for (Iterator<ConcurrentHashMap.Entry<Long, Chunkster>> iterator = chunksters.entrySet().iterator(); iterator.hasNext();) {
            ConcurrentHashMap.Entry<Long, Chunkster> entry = iterator.next();
            if(entry.getValue().getTimer().spent() > CACHE_TIME_LIMIT){
                iterator.remove();
            }
        }
    }

    /* Update all chunsters */
    void update() {
        for (ConcurrentHashMap.Entry <Long, Chunkster> entry : chunksters.entrySet()) {
            entry.getValue().update();
        }
    }

    /* Get chunk key */
    private long getKey(int chunkX, int chunkZ) {
        return (long)chunkX << 32 | chunkZ & 0xFFFFFFFFL;
    }

}