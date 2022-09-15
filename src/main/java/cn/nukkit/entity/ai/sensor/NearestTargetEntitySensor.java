package cn.nukkit.entity.ai.sensor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.EntityMemory;
import cn.nukkit.utils.SortedList;

import java.util.*;
import java.util.function.Function;

/**
 * 用来搜索最近的目标实体，构造函数中接受一个目标函数{@code Function<T, Boolean> target}的Set，用于实体检测，最终结果保存到{@code Class<? extends EntityMemory<Entity>>[] memories}中.
 * <p>
 * The constructor accepts a Set of Integer to target function {@code Function<T, Boolean> target} to search for the nearest target entity, and the final result is saved to {@code Class<? extends EntityMemory<Entity>>[] memories}.
 */
@PowerNukkitXOnly
@Since("1.19.21-r5")
public class NearestTargetEntitySensor<T extends Entity> implements ISensor {

    protected double minRange;

    protected double maxRange;

    protected int period;

    protected Function<T, Boolean>[] allTargetFunction;

    protected List<Class<? extends EntityMemory<Entity>>> memories;

    /**
     * 不指定目标函数，默认将全部结果存入第一个记忆
     * <p>
     * Without specifying the target function, all results will be stored in the first memory by default
     *
     * @see #NearestTargetEntitySensor(double, double, int, List, Function[])
     */
    public NearestTargetEntitySensor(double minRange, double maxRange, List<Class<? extends EntityMemory<Entity>>> memories) {
        this(minRange, maxRange, 1, memories, (Function<T, Boolean>) null);
    }

    /**
     * @param minRange          最小搜索范围<br>Minimum Search Range
     * @param maxRange          最大搜索范围<br>Maximum Search Range
     * @param period            传感器执行周期，单位tick<br>Senor execute period
     * @param allTargetFunction 接收一个Set，将指定目标函数筛选的结果映射到指定索引的记忆上，目标函数接受一个参数T，返回一个Boolean<br>Receives a Set that set the results filtered by the specified target function to the memory of the specified index, the target function accepts a parameter T and returns a Boolean
     * @param memories          保存结果的记忆类型<br>Memory class type for saving results
     */
    @SafeVarargs
    public NearestTargetEntitySensor(double minRange, double maxRange, int period, List<Class<? extends EntityMemory<Entity>>> memories, Function<T, Boolean>... allTargetFunction) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.period = period;
        if (allTargetFunction == null) this.allTargetFunction = null;
        else {
            if (memories.size() >= 1 && allTargetFunction.length == memories.size()) {
                this.allTargetFunction = allTargetFunction;
            } else
                throw new IllegalArgumentException("allTargetFunction必须与memories一一对应");
        }
        this.memories = memories;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        double minRangeSquared = this.minRange * this.minRange;
        double maxRangeSquared = this.maxRange * this.maxRange;

        if (allTargetFunction == null && memories.size() == 1) {
            var currentMemory = Objects.requireNonNull(entity.getMemoryStorage()).get(memories.get(0));
            if (currentMemory.getData() != null && currentMemory.getData().isAlive()) return;

            //寻找范围内最近的实体
            var entities = Collections.synchronizedList(new SortedList<>(Comparator.comparingDouble((Entity e) -> e.distanceSquared(entity))));
            for (Entity p : entity.getLevel().getEntities()) {
                if (entity.distanceSquared(p) <= maxRangeSquared && entity.distanceSquared(p) >= minRangeSquared && !p.equals(entity)) {
                    entities.add(p);
                }
            }

            if (entities.isEmpty()) {
                currentMemory.setData(null);
            } else currentMemory.setData(entities.get(0));
            return;
        }
        if (allTargetFunction != null) {
            List<List<Entity>> sortEntities = new ArrayList<>(memories.size());

            for (int i = 0, len = memories.size(); i < len; ++i) {
                sortEntities.add(new SortedList<>(Comparator.comparingDouble((Entity e) -> e.distanceSquared(entity))));
            }

            for (Entity p : entity.getLevel().getEntities()) {
                if (entity.distanceSquared(p) <= maxRangeSquared && entity.distanceSquared(p) >= minRangeSquared && !p.equals(entity)) {
                    int i = 0;
                    for (var targetFunction : allTargetFunction) {
                        if (targetFunction.apply((T) p)) {
                            sortEntities.get(i).add(p);
                        }
                        ++i;
                    }
                }
            }

            for (int i = 0, len = sortEntities.size(); i < len; ++i) {
                var currentMemory = Objects.requireNonNull(entity.getMemoryStorage()).get(memories.get(i));
                if (currentMemory.getData() != null && currentMemory.getData().isAlive()) continue;

                if (sortEntities.get(i).isEmpty()) {
                    currentMemory.setData(null);
                } else currentMemory.setData(sortEntities.get(i).get(0));
            }
        }
    }


    @Override
    public int getPeriod() {
        return period;
    }
}