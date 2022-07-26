package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.IMemory;
import cn.nukkit.entity.ai.memory.TimedMemory;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class PassByTimeEvaluator<T extends TimedMemory & IMemory<?>> implements IBehaviorEvaluator{

    protected Class<T> timedMemory;
    protected int minPassByTimeRange;
    protected int maxPassByTimeRange;
    protected boolean allowEmpty;

    public PassByTimeEvaluator(Class<T> timedMemory, int minPassByTimeRange, int maxPassByTimeRange){
        this(timedMemory, minPassByTimeRange, maxPassByTimeRange, false);
    }

    public PassByTimeEvaluator(Class<T> timedMemory, int minPassByTimeRange, int maxPassByTimeRange, boolean allowEmpty){
        this.timedMemory = timedMemory;
        this.minPassByTimeRange = minPassByTimeRange;
        this.maxPassByTimeRange = maxPassByTimeRange;
        this.allowEmpty = allowEmpty;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        T instance = entity.getMemoryStorage().get(timedMemory);
        if (!instance.hasData()){
            if (allowEmpty) return true;
            return false;
        }
        int passByTime = Server.getInstance().getTick() - instance.getTime();
        return passByTime >= minPassByTimeRange && passByTime <= maxPassByTimeRange;
    }
}
