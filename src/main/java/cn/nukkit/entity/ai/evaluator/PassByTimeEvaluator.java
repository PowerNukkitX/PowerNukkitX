package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class PassByTimeEvaluator implements IBehaviorEvaluator {

    protected MemoryType<Integer> timedMemory;
    protected int minPassByTimeRange;
    protected int maxPassByTimeRange;

    public PassByTimeEvaluator(MemoryType<Integer> timedMemory, int minPassByTimeRange, int maxPassByTimeRange) {
        this.timedMemory = timedMemory;
        this.minPassByTimeRange = minPassByTimeRange;
        this.maxPassByTimeRange = maxPassByTimeRange;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        var time = entity.getMemoryStorage().get(timedMemory);
        int passByTime = Server.getInstance().getTick() - time;
        return passByTime >= minPassByTimeRange && passByTime <= maxPassByTimeRange;
    }
}
