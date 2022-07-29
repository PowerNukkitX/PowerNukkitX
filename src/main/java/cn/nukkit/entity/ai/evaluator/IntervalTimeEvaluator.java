package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.IMemory;
import cn.nukkit.entity.ai.memory.TimedMemory;

/**
 * @author LT_Name
 */
public class IntervalTimeEvaluator<T extends TimedMemory & IMemory<?>> implements IBehaviorEvaluator {

    protected Class<T> timedMemory;
    protected int minIntervalTime;
    protected int maxIntervalTime;

    public IntervalTimeEvaluator(Class<T> timedMemory, int minIntervalTime, int maxIntervalTime) {

    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        return false;
    }
}
