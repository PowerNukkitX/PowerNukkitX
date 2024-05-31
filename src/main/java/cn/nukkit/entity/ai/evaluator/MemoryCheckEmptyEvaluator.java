package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;


public class MemoryCheckEmptyEvaluator implements IBehaviorEvaluator {

    protected MemoryType<?> type;
    /**
     * @deprecated 
     */
    

    public MemoryCheckEmptyEvaluator(MemoryType<?> type) {
        this.type = type;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean evaluate(EntityIntelligent entity) {
        return entity.getBehaviorGroup().getMemoryStorage().isEmpty(type);
    }
}
