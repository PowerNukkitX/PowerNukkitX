package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;


/**
 * An evaluator used to determine whether a time type of memory is within a specified range
 */
public class PassByTimeEvaluator implements IBehaviorEvaluator {

    protected MemoryType<Integer> timedMemory;
    protected int minPassByTimeRange;
    protected int maxPassByTimeRange;


    public PassByTimeEvaluator(MemoryType<Integer> timedMemory, int minPassByTimeRange) {
        this(timedMemory, minPassByTimeRange, Integer.MAX_VALUE);
    }

    /**
     * An evaluator used to determine whether a time type of memory is within a specified range
     *
     * @param timedMemory        the timed memory
     * @param minPassByTimeRange the min pass by time range
     * @param maxPassByTimeRange the max pass by time range
     */
    public PassByTimeEvaluator(MemoryType<Integer> timedMemory, int minPassByTimeRange, int maxPassByTimeRange) {
        this.timedMemory = timedMemory;
        this.minPassByTimeRange = minPassByTimeRange;
        this.maxPassByTimeRange = maxPassByTimeRange;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        var time = entity.getMemoryStorage().get(timedMemory);
        int passByTime = entity.getLevel().getTick() - time;
        return passByTime >= minPassByTimeRange && passByTime <= maxPassByTimeRange;
    }
}
