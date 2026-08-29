package org.powernukkitx.entity.ai.sensor;


import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.item.EntityItem;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.NukkitMath;
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
                    if(e instanceof EntityItem entityItem) {
                        if(itemClass.isAssignableFrom(entityItem.getItem().getClass())) {
                            double distanceSquared = entity.distanceSquared(e);
                            if (distanceSquared <= rangeSquared && distanceSquared >= minRangeSquared) {
                                if (item == null || distanceSquared < nearestSquared) {
                                    item = entityItem;
                                    nearestSquared = distanceSquared;
                                }
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
