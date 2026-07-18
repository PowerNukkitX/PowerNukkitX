package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.MemoryType;
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
        double rangeSquared = this.range * this.range;
        double minRangeSquared = this.minRange * this.minRange;
        //Find the nearest player within range
        for (Entity e : entity.getLevel().getEntities()) {
            if(entityClass.isAssignableFrom(e.getClass())) {
                if (entity.distanceSquared(e) <= rangeSquared && entity.distanceSquared(e) >= minRangeSquared) {
                    if (ent == null) {
                        ent = e;
                    } else {
                        if (entity.distanceSquared(e) < entity.distanceSquared(ent)) {
                            ent = e;
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
