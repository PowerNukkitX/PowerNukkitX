package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.IMemory;
import cn.nukkit.entity.ai.memory.TimedMemory;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class TimedMemoryCheckEvaluator<T extends TimedMemory & IMemory<?>> implements IBehaviorEvaluator{

    protected Class<T> timedMemory;
    protected int timeRange;

    public TimedMemoryCheckEvaluator(Class<T> timedMemory,int timeRange){
        this.timedMemory = timedMemory;
        this.timeRange = timeRange;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        T instance = entity.getMemoryStorage().get(timedMemory);
        return instance.hasData() && (Server.getInstance().getTick() - instance.getTime()) <= timeRange;
    }
}
