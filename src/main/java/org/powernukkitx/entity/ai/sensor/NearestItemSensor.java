package org.powernukkitx.entity.ai.sensor;


import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.item.EntityItem;
import org.powernukkitx.item.Item;
import lombok.Getter;

//Memory that stores the nearest player


@Getter
public class NearestItemSensor implements ISensor {

    protected double range;

    protected double minRange;

    protected int period;

    public NearestItemSensor(double range, double minRange) {
        this(range, minRange, 1);
    }

    public NearestItemSensor(double range, double minRange, int period) {
        this.range = range;
        this.minRange = minRange;
        this.period = period;
    }

    @Override
    public void sense(EntityIntelligent entity) {

        Class<? extends Item> itemClass = entity.getMemoryStorage().get(CoreMemoryTypes.LOOKING_ITEM);
        if(itemClass == null) return;

        EntityItem item = null;
        double rangeSquared = this.range * this.range;
        double minRangeSquared = this.minRange * this.minRange;
        //Find the nearest player within range
        for (Entity e : entity.getLevel().getEntities()) {
            if(e instanceof EntityItem entityItem) {
                if(itemClass.isAssignableFrom(entityItem.getItem().getClass())) {
                    if (entity.distanceSquared(e) <= rangeSquared && entity.distanceSquared(e) >= minRangeSquared) {
                        if (item == null) {
                            item = entityItem;
                        } else {
                            if (entity.distanceSquared(entityItem) < entity.distanceSquared(item)) {
                                 item = entityItem;
                            }
                        }
                    }
                }
            }
        }
        entity.getMemoryStorage().put(CoreMemoryTypes.NEAREST_ITEM, item);
    }

    @Override
    public int getPeriod() {
        return period;
    }
}
