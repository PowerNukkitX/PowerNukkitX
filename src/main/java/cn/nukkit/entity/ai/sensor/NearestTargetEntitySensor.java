package cn.nukkit.entity.ai.sensor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.EntityMemory;
import cn.nukkit.utils.SortedList;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

/**
 * 用来搜索最近的目标实体，构造函数中接受一个目标函数{@code Function<T, Boolean> target}，用于实体检测，最终结果保存到{@code Class<? extends EntityMemory<Entity>> targetMemory}中.
 * <p>
 * The constructor accepts a target function {@code Function<T, Boolean> target} to search for the nearest target entity, and the final result is saved to {@code Class<? extends EntityMemory<Entity>> targetMemory}.
 */
@PowerNukkitXOnly
@Since("1.19.21-r5")
public class NearestTargetEntitySensor<T extends Entity> implements ISensor {

    protected double minRange;

    protected double maxRange;

    protected int period;

    protected Function<T, Boolean> target;

    protected Class<? extends EntityMemory<Entity>> memoryClazz;

    /**
     * @see #NearestTargetEntitySensor(double, double, int, Class, Function)
     */
    public NearestTargetEntitySensor(double minRange, double maxRange, Class<? extends EntityMemory<Entity>> targetMemory) {
        this(minRange, maxRange, 1, targetMemory, null);
    }

    /**
     * @param minRange     最小搜索范围<br>Minimum Search Range
     * @param maxRange     最大搜索范围<br>Maximum Search Range
     * @param targetMemory 保存结果的记忆类型<br>Memory class type for saving results
     * @param target       目标函数，接受一个参数T，返回一个Boolean<br>The target function, which takes one argument T, returns a Boolean
     */
    public NearestTargetEntitySensor(double minRange, double maxRange, int period, Class<? extends EntityMemory<Entity>> targetMemory, Function<T, Boolean> target) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.period = period;
        this.memoryClazz = targetMemory;
        this.target = target;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        var currentMemory = Objects.requireNonNull(entity.getMemoryStorage()).get(memoryClazz);
        if (currentMemory.getData() != null && currentMemory.getData().isAlive()) return;
        double minRangeSquared = this.minRange * this.minRange;
        double maxRangeSquared = this.maxRange * this.maxRange;
        //寻找范围内最近的实体
        var sortEntities = Collections.synchronizedList(new SortedList<>(Comparator.comparingDouble((Entity e) -> e.distanceSquared(entity))));

        for (Entity p : entity.getLevel().getEntities()) {
            if (entity.distanceSquared(p) <= maxRangeSquared && entity.distanceSquared(p) >= minRangeSquared) {
                if (target == null || target.apply((T) p)) {
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