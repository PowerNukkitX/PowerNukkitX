package cn.nukkit.entity.ai.sensor;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.NearestEntityMemory;
import cn.nukkit.utils.SortedList;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;


public class NearestTargetEntitySensor<T extends Entity> implements ISensor {

    protected double minRange;

    protected double maxRange;

    protected int period;

    protected Function<T, Boolean> target;

    public NearestTargetEntitySensor(double minRange, double maxRange) {
        this(minRange, maxRange, 1, null);
    }

    public NearestTargetEntitySensor(double minRange, double maxRange, int period, Function<T, Boolean> target) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.period = period;
        this.target = target;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        var currentMemory = Objects.requireNonNull(entity.getMemoryStorage()).get(NearestEntityMemory.class);
        if (currentMemory.getData() != null) return;
        double minRangeSquared = this.minRange * this.minRange;
        double maxRangeSquared = this.maxRange * this.maxRange;
        //寻找范围内最近的实体
        var sortEntities = Collections.synchronizedList(new SortedList<>(Comparator.comparingDouble((Entity e) -> e.distanceSquared(entity))));

        for (Entity p : entity.getLevel().getEntities()) {
            if (entity.distanceSquared(p) <= maxRangeSquared && entity.distanceSquared(p) >= minRangeSquared) {
                if (target == null) return;
                if (target.apply((T) p)) {
                    sortEntities.add(p);
                }
            }
        }
        if (sortEntities.isEmpty()) {
            currentMemory.setData(null);
        } else {
            if (sortEntities.get(0).equals(entity)) sortEntities.remove(0);
            currentMemory.setData(sortEntities.get(0));
        }
    }

    @Override
    public int getPeriod() {
        return period;
    }
}