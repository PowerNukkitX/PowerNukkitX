package cn.nukkit.entity.ai.sensor;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.item.Item;
import lombok.Getter;

//存储最近的玩家的Memory


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
        //寻找范围内最近的玩家
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
