package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.MemoryType;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.NukkitMath;
import lombok.Getter;

//Memory that stores the nearest player


@Getter
public class NearestEntitySensor implements ISensor {

    protected double range;

    protected double minRange;

    protected int period;

    protected Class<? extends Entity> entityClass;

    protected MemoryType<Entity> memoryType;

    public NearestEntitySensor(Class<? extends Entity> entityClass, MemoryType<Entity> memoryType, double range, double minRange) {
        this(entityClass, memoryType, range, minRange, 1);
    }

    public NearestEntitySensor(Class<? extends Entity> entityClass, MemoryType<Entity> memoryType, double range, double minRange, int period) {
        this.entityClass = entityClass;
        this.memoryType = memoryType;
        this.range = range;
        this.minRange = minRange;
        this.period = period;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        Entity ent = null;
        double nearestSquared = 0;
        double rangeSquared = this.range * this.range;
        double minRangeSquared = this.minRange * this.minRange;
        Level level = entity.getLevel();
        int minChunkX = NukkitMath.floorDouble((entity.x - this.range - 2) * 0.0625);
        int maxChunkX = NukkitMath.ceilDouble((entity.x + this.range + 2) * 0.0625);
        int minChunkZ = NukkitMath.floorDouble((entity.z - this.range - 2) * 0.0625);
        int maxChunkZ = NukkitMath.ceilDouble((entity.z + this.range + 2) * 0.0625);
        //Find the nearest player within range
        for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ) {
                for (Entity e : level.getChunkEntities(chunkX, chunkZ, false).values()) {
                    if(entityClass.isAssignableFrom(e.getClass())) {
                        double distanceSquared = entity.distanceSquared(e);
                        if (distanceSquared <= rangeSquared && distanceSquared >= minRangeSquared) {
                            if (ent == null || distanceSquared < nearestSquared) {
                                ent = e;
                                nearestSquared = distanceSquared;
                            }
                        }
                    }
                }
            }
        }
        if(ent == null) {
            if(entity.getMemoryStorage().notEmpty(memoryType) && entity.getMemoryStorage().get(memoryType).getClass().isAssignableFrom(entityClass)) {
                entity.getMemoryStorage().clear(memoryType);
            } // We don't want to clear data from different sensors
        } else entity.getMemoryStorage().put(memoryType, ent);
    }

    @Override
    public int getPeriod() {
        return period;
    }
}
